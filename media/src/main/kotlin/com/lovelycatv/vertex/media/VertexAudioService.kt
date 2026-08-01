package com.lovelycatv.vertex.media

import javax.sound.sampled.*

/**
 * Service class for managing audio input (microphone) and output (speaker) devices.
 * Provides safe resource management with automatic cleanup.
 */
class VertexAudioService {

    // Track currently open lines to ensure proper cleanup
    private var currentMicrophone: TargetDataLine? = null
    private var currentSpeaker: SourceDataLine? = null

    // Thread safety lock for concurrent access
    private val lock = Any()

    /**
     * Get all available microphone (input) devices.
     * @return List of [AudioDeviceInfo] representing available microphones
     */
    fun getSystemMicrophoneDevices(): List<AudioDeviceInfo> {
        return getAudioDevices(TargetDataLine::class.java)
    }

    /**
     * Get all available speaker (output) devices.
     * @return List of [AudioDeviceInfo] representing available speakers
     */
    fun getSystemSoundPlayerDevices(): List<AudioDeviceInfo> {
        return getAudioDevices(SourceDataLine::class.java)
    }

    /**
     * Internal helper to query audio devices by line type.
     * @param lineClass TargetDataLine::class.java for input, SourceDataLine::class.java for output
     * @return List of matching audio devices
     */
    private fun getAudioDevices(lineClass: Class<out Line>): List<AudioDeviceInfo> {
        val devices = mutableListOf<AudioDeviceInfo>()
        val mixerInfos = AudioSystem.getMixerInfo()

        for (info in mixerInfos) {
            val mixer = AudioSystem.getMixer(info)
            val lineInfo = DataLine.Info(lineClass, null as AudioFormat?)
            if (mixer.isLineSupported(lineInfo)) {
                devices.add(
                    AudioDeviceInfo(
                        name = info.name,
                        description = info.description ?: "",
                        vendor = info.vendor ?: "",
                        version = info.version ?: "",
                        mixerInfo = info
                    )
                )
            }
        }

        return devices
    }

    /**
     * Find and open a microphone by name keyword.
     * @param keyword Substring to match against device names (case-insensitive)
     * @return Opened [TargetDataLine] or null if not found or failed to open
     */
    fun getMicrophoneByName(keyword: String): TargetDataLine? {
        val devices = getSystemMicrophoneDevices()
        val matched = devices.find { it.name.contains(keyword, ignoreCase = true) }
        return matched?.let {
            openTargetDataLine(it.mixerInfo)
        }
    }

    /**
     * Find and open a speaker by name keyword.
     * @param keyword Substring to match against device names (case-insensitive)
     * @return Opened [SourceDataLine] or null if not found or failed to open
     */
    fun getSpeakerByName(keyword: String): SourceDataLine? {
        val devices = getSystemSoundPlayerDevices()
        val matched = devices.find { it.name.contains(keyword, ignoreCase = true) }
        return matched?.let {
            openSourceDataLine(it.mixerInfo)
        }
    }

    /**
     * Open a specific microphone device with the given format.
     * Note: Call [startRecording] to begin audio capture.
     * @param mixerInfo The mixer info identifying the target device
     * @param format Audio format (default: 16kHz, 16-bit, mono)
     * @return Opened [TargetDataLine] or null on failure
     */
    fun openTargetDataLine(
        mixerInfo: Mixer.Info,
        format: AudioFormat = DEFAULT_AUDIO_FORMAT
    ): TargetDataLine? {
        synchronized(lock) {
            // Ensure any previously opened microphone is properly closed
            releaseMicrophone()

            return try {
                val lineInfo = DataLine.Info(TargetDataLine::class.java, format)
                // On macOS, requesting a line from a specific mixer with a concrete
                // format often fails with "Line unsupported"; fall back to letting
                // AudioSystem pick a line that supports the format (system default input).
                val line = try {
                    AudioSystem.getMixer(mixerInfo).getLine(lineInfo) as TargetDataLine
                } catch (e: Exception) {
                    AudioSystem.getLine(lineInfo) as TargetDataLine
                }
                line.open(format)
                currentMicrophone = line
                line
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * Open a specific speaker device with the given format.
     * Note: Call [startPlaying] to begin audio playback.
     * @param mixerInfo The mixer info identifying the target device
     * @param format Audio format (default: 16kHz, 16-bit, mono)
     * @return Opened [SourceDataLine] or null on failure
     */
    fun openSourceDataLine(
        mixerInfo: Mixer.Info,
        format: AudioFormat = DEFAULT_AUDIO_FORMAT
    ): SourceDataLine? {
        synchronized(lock) {
            // Ensure any previously opened speaker is properly closed
            releaseSpeaker()

            return try {
                val lineInfo = DataLine.Info(SourceDataLine::class.java, format)
                // Same macOS workaround as openTargetDataLine: fall back to the
                // system default output line if the specific mixer rejects the format.
                val line = try {
                    AudioSystem.getMixer(mixerInfo).getLine(lineInfo) as SourceDataLine
                } catch (e: Exception) {
                    AudioSystem.getLine(lineInfo) as SourceDataLine
                }
                line.open(format)
                currentSpeaker = line
                line
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * Open the system default microphone (input) line.
     *
     * On macOS, selecting a line through a specific [Mixer] is unreliable: some
     * mixers hand back a line that opens and starts but never delivers audio.
     * Letting [AudioSystem] pick the default capture line for the format avoids
     * that problem, so this is the recommended way to start recording.
     *
     * Note: Call [startRecording] to begin audio capture.
     * @param format Audio format (default: 16kHz, 16-bit, mono)
     * @return Opened [TargetDataLine] or null on failure
     */
    fun openDefaultMicrophone(format: AudioFormat = DEFAULT_AUDIO_FORMAT): TargetDataLine? {
        synchronized(lock) {
            releaseMicrophone()

            return try {
                val line = AudioSystem.getTargetDataLine(format)
                line.open(format)
                currentMicrophone = line
                line
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * Open the system default speaker (output) line. See [openDefaultMicrophone]
     * for why the default line is preferred over selecting a specific mixer.
     *
     * Note: Call [startPlaying] to begin audio playback.
     * @param format Audio format (default: 16kHz, 16-bit, mono)
     * @return Opened [SourceDataLine] or null on failure
     */
    fun openDefaultSpeaker(format: AudioFormat = DEFAULT_AUDIO_FORMAT): SourceDataLine? {
        synchronized(lock) {
            releaseSpeaker()

            return try {
                val line = AudioSystem.getSourceDataLine(format)
                line.open(format)
                currentSpeaker = line
                line
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * Start recording from the currently opened microphone.
     * @return true if started successfully, false otherwise
     */
    fun startRecording(): Boolean {
        synchronized(lock) {
            val line = currentMicrophone
            return if (line != null && !line.isRunning) {
                try {
                    line.start()
                    true
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            } else {
                false
            }
        }
    }

    /**
     * Stop recording from the currently opened microphone.
     * Note: The line remains open and can be restarted with [startRecording].
     * @return true if stopped successfully, false otherwise
     */
    fun stopRecording(): Boolean {
        synchronized(lock) {
            val line = currentMicrophone
            return if (line != null && line.isRunning) {
                try {
                    line.stop()
                    true
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            } else {
                false
            }
        }
    }

    /**
     * Start playback on the currently opened speaker.
     * @return true if started successfully, false otherwise
     */
    fun startPlaying(): Boolean {
        synchronized(lock) {
            val line = currentSpeaker
            return if (line != null && !line.isRunning) {
                try {
                    line.start()
                    true
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            } else {
                false
            }
        }
    }

    /**
     * Stop playback on the currently opened speaker.
     * Note: The line remains open and can be restarted with [startPlaying].
     * @return true if stopped successfully, false otherwise
     */
    fun stopPlaying(): Boolean {
        synchronized(lock) {
            val line = currentSpeaker
            return if (line != null && line.isRunning) {
                try {
                    line.stop()
                    true
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            } else {
                false
            }
        }
    }

    /**
     * Read audio data from the currently opened microphone.
     * @param buffer Byte array to store the read data
     * @param offset Starting offset in the buffer
     * @param length Maximum number of bytes to read
     * @return Actual number of bytes read, or -1 if error
     */
    fun readMicrophone(buffer: ByteArray, offset: Int = 0, length: Int = buffer.size): Int {
        synchronized(lock) {
            val line = currentMicrophone
            // Gate on isOpen, not isRunning: on macOS a TargetDataLine reports
            // isRunning == false between start() and the first read() (I/O hasn't
            // engaged yet), so using isRunning here would drop every read.
            return if (line != null && line.isOpen) {
                try {
                    line.read(buffer, offset, length)
                } catch (e: Exception) {
                    e.printStackTrace()
                    -1
                }
            } else {
                -1
            }
        }
    }

    /**
     * Write audio data to the currently opened speaker for playback.
     * @param data Audio data to play
     * @param offset Starting offset in the data array
     * @param length Number of bytes to write
     * @return Number of bytes actually written, or -1 if error
     */
    fun writeSpeaker(data: ByteArray, offset: Int = 0, length: Int = data.size): Int {
        synchronized(lock) {
            val line = currentSpeaker
            // Gate on isOpen, not isRunning (same reason as readMicrophone): a
            // SourceDataLine reports isRunning == false until data actually flows,
            // so gating on isRunning would drop the first writes.
            return if (line != null && line.isOpen) {
                try {
                    line.write(data, offset, length)
                } catch (e: Exception) {
                    e.printStackTrace()
                    -1
                }
            } else {
                -1
            }
        }
    }

    /**
     * Wait for all pending audio data to finish playing.
     * Should be called before [releaseSpeaker] to avoid cutting off audio.
     * @return true if drain completed successfully, false otherwise
     */
    fun drainSpeaker(): Boolean {
        synchronized(lock) {
            val line = currentSpeaker
            return if (line != null) {
                try {
                    line.drain()
                    true
                } catch (e: Exception) {
                    e.printStackTrace()
                    false
                }
            } else {
                false
            }
        }
    }

    /**
     * Safely release the currently opened microphone.
     * Stops and closes the line, freeing all system resources.
     */
    fun releaseMicrophone() {
        synchronized(lock) {
            currentMicrophone?.let { line ->
                try {
                    if (line.isRunning) {
                        line.stop()
                    }
                    if (line.isOpen) {
                        line.close()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            currentMicrophone = null
        }
    }

    /**
     * Safely release the currently opened speaker.
     * Stops and closes the line, freeing all system resources.
     */
    fun releaseSpeaker() {
        synchronized(lock) {
            currentSpeaker?.let { line ->
                try {
                    if (line.isRunning) {
                        line.stop()
                    }
                    if (line.isOpen) {
                        line.close()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            currentSpeaker = null
        }
    }

    /**
     * Release all audio resources (both microphone and speaker).
     * Should be called when the service is no longer needed.
     */
    fun releaseAll() {
        synchronized(lock) {
            releaseMicrophone()
            releaseSpeaker()
        }
    }

    /**
     * Check if the microphone is currently open and running.
     */
    fun isMicrophoneRunning(): Boolean {
        synchronized(lock) {
            return currentMicrophone?.isRunning == true
        }
    }

    /**
     * Check if the speaker is currently open and running.
     */
    fun isSpeakerRunning(): Boolean {
        synchronized(lock) {
            return currentSpeaker?.isRunning == true
        }
    }

    /**
     * Get the currently opened microphone line, or null if none.
     */
    fun getCurrentMicrophone(): TargetDataLine? {
        synchronized(lock) {
            return currentMicrophone
        }
    }

    /**
     * Get the currently opened speaker line, or null if none.
     */
    fun getCurrentSpeaker(): SourceDataLine? {
        synchronized(lock) {
            return currentSpeaker
        }
    }

    companion object {
        val DEFAULT_AUDIO_FORMAT = AudioFormat(16000.0f, 16, 1, true, false)
    }
}