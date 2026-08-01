package com.lovelycatv.vertex.media

import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream

/**
 * Manual tests that require real audio hardware. They verify whether
 * [VertexAudioService] can actually capture microphone input and play audio
 * back on this machine, so they are not suitable for CI.
 */
class VertexAudioServiceTest {

    /** Capture duration in milliseconds. */
    private val CAPTURE_MS = 3000L

    private val audioService = VertexAudioService()

    /**
     * List the system microphone and speaker devices.
     */
    @Test
    fun listDevices() {
        println("Microphone devices:")
        audioService.getSystemMicrophoneDevices().forEachIndexed { i, d ->
            println("  [$i] '${d.name}' vendor='${d.vendor}'")
        }
        println("Speaker devices:")
        audioService.getSystemSoundPlayerDevices().forEachIndexed { i, d ->
            println("  [$i] '${d.name}' vendor='${d.vendor}'")
        }
    }

    /**
     * Open the default microphone, capture [CAPTURE_MS] of audio and report the
     * byte count, peak and RMS level to tell real audio apart from silence.
     * Speak into the microphone while it records.
     */
    @Test
    fun recordDefaultMicrophone() {
        val line = audioService.openDefaultMicrophone()
            ?: error("Failed to open default microphone")
        println("Microphone opened, format: ${line.format}")

        check(audioService.startRecording()) { "Failed to start recording" }

        val captured = captureFor(CAPTURE_MS)
        audioService.releaseMicrophone()

        val stats = analyze(captured)
        println("Capture done: ${captured.size} bytes, peak=${stats.peak}, RMS=${"%.1f".format(stats.rms)}")

        check(captured.isNotEmpty()) { "No bytes were captured" }
        check(stats.peak > 0) { "Captured audio is all silence (peak == 0); likely a device or permission issue" }
        println("Microphone captured real audio signal")
    }

    /**
     * End-to-end: record [CAPTURE_MS] of audio, then play it back through the
     * speaker. Hearing what you just said confirms both capture and playback work.
     */
    @Test
    fun recordThenPlayback() {
        // 1. Record
        audioService.openDefaultMicrophone() ?: error("Failed to open default microphone")
        check(audioService.startRecording()) { "Failed to start recording" }
        println("Recording for ${CAPTURE_MS}ms, please speak...")
        val captured = captureFor(CAPTURE_MS)
        audioService.releaseMicrophone()

        val stats = analyze(captured)
        println("Captured ${captured.size} bytes, peak=${stats.peak}")
        check(stats.peak > 0) { "Captured audio is all silence, nothing to play back" }

        // 2. Play back
        audioService.openDefaultSpeaker() ?: error("Failed to open default speaker")
        check(audioService.startPlaying()) { "Failed to start playback" }
        println("Playing back...")
        audioService.writeSpeaker(captured)
        audioService.drainSpeaker()
        audioService.releaseSpeaker()
        println("Playback finished; if you heard your words the capture+playback path works")
    }

    /**
     * Speaker-only test: generate a 1s 440Hz sine tone and play it, to verify the
     * output path on its own.
     */
    @Test
    fun playSineTone() {
        val format = VertexAudioService.DEFAULT_AUDIO_FORMAT
        val sampleRate = format.sampleRate.toInt()
        val durationSec = 1
        val tone = ByteArrayOutputStream()
        for (i in 0 until sampleRate * durationSec) {
            val angle = 2.0 * Math.PI * 440.0 * i / sampleRate
            val sample = (Math.sin(angle) * 0.3 * Short.MAX_VALUE).toInt().toShort()
            // 16-bit little-endian
            tone.write(sample.toInt() and 0xFF)
            tone.write((sample.toInt() shr 8) and 0xFF)
        }

        audioService.openDefaultSpeaker(format) ?: error("Failed to open default speaker")
        check(audioService.startPlaying()) { "Failed to start playback" }
        println("Playing 440Hz test tone for ${durationSec}s...")
        audioService.writeSpeaker(tone.toByteArray())
        audioService.drainSpeaker()
        audioService.releaseSpeaker()
        println("If you heard a tone the speaker works")
    }

    /**
     * Continuously read from the current microphone for [millis] ms and return
     * the aggregated bytes.
     */
    private fun captureFor(millis: Long): ByteArray {
        val out = ByteArrayOutputStream()
        val buffer = ByteArray(6400) // 200ms @ 16k/16bit/mono
        val deadline = System.currentTimeMillis() + millis
        while (System.currentTimeMillis() < deadline) {
            val read = audioService.readMicrophone(buffer)
            if (read > 0) out.write(buffer, 0, read)
        }
        return out.toByteArray()
    }

    private data class Stats(val peak: Int, val rms: Double)

    /**
     * Parse 16-bit little-endian PCM into amplitude stats to distinguish real
     * audio from silence.
     */
    private fun analyze(pcm: ByteArray): Stats {
        var peak = 0
        var sumSquares = 0.0
        var count = 0
        var i = 0
        while (i + 1 < pcm.size) {
            val sample = (pcm[i].toInt() and 0xFF) or (pcm[i + 1].toInt() shl 8)
            val abs = kotlin.math.abs(sample)
            if (abs > peak) peak = abs
            sumSquares += sample.toDouble() * sample
            count++
            i += 2
        }
        val rms = if (count > 0) Math.sqrt(sumSquares / count) else 0.0
        return Stats(peak, rms)
    }
}
