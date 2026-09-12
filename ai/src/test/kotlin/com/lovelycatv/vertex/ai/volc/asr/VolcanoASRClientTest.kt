package com.lovelycatv.vertex.ai.volc.asr

import com.lovelycatv.vertex.ai.llm.ChatRequest
import com.lovelycatv.vertex.ai.llm.config.LLMClientConfig
import com.lovelycatv.vertex.ai.llm.impl.OpenAiLLMClient
import com.lovelycatv.vertex.ai.llm.message.UserChatMessage
import com.lovelycatv.vertex.ai.volc.tts.v3.VolcanoTTSClientV3
import com.lovelycatv.vertex.ai.volc.tts.v3.VolcanoTTSClientV3Config
import com.lovelycatv.vertex.ai.volc.tts.v3.VolcanoTTSRequestV3
import com.lovelycatv.vertex.ai.volc.tts.v3.VolcanoTTSResponseV3
import com.lovelycatv.vertex.ai.volc.tts.v3.protocol.SimpleVolcanoTTSWebSocketClientStreamCallbackV3
import com.lovelycatv.vertex.media.VertexAudioService
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.TargetDataLine
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * End-to-end voice chat: microphone -> ASR (Volcano SAUC) -> AI -> TTS -> speaker.
 *
 * Fill in the constants below (keys / tokens / model / device names) before running.
 */
class VolcanoASRClientTest {

    // region ---- Fill these in ----

    /** Volcano SAUC ASR credentials. */
    private val ASR_APP_ID = ""
    private val ASR_ACCESS_TOKEN = ""
    private val ASR_UID = "vertex_demo_uid"

    /** Volcano TTS v3 credentials. */
    private val TTS_APP_ID = ""
    private val TTS_ACCESS_TOKEN = ""
    private val TTS_RESOURCE_ID = "seed-tts-2.0"
    private val TTS_SPEAKER = "zh_female_xiaohe_uranus_bigtts"

    /** Volcengine Ark, which speaks the OpenAI-compatible protocol. */
    private val AI_BASE_URL = "https://ark.cn-beijing.volces.com/api/v3"

    /** LLM credentials + model. */
    private val AI_API_KEY = ""
    private val AI_MODEL = ""

    /** Substring of the microphone / speaker device names (case-insensitive), or null for default. */
    private val MICROPHONE_KEYWORD: String? = null
    private val SPEAKER_KEYWORD: String? = null

    /** How long to capture from the microphone, in milliseconds. */
    private val RECORD_DURATION_MS = 5000L

    // endregion

    private val asrClient = VolcanoASRClient(
        VolcanoASRClientConfig(
            appId = ASR_APP_ID,
            accessToken = ASR_ACCESS_TOKEN,
            enableLogging = true
        )
    )

    private val aiClient = OpenAiLLMClient(
        LLMClientConfig(
            baseUrl = AI_BASE_URL,
            apiKey = AI_API_KEY
        )
    )

    private val ttsClient = VolcanoTTSClientV3(
        TTS_RESOURCE_ID,
        VolcanoTTSClientV3Config(
            appId = TTS_APP_ID,
            accessToken = TTS_ACCESS_TOKEN,
            enableLogging = true
        )
    )

    private val audioService = VertexAudioService()

    @Test
    fun voiceChat() {
        runBlocking {
            // 1. Capture speech from the microphone and recognize it.
            val recognizedText = recognizeFromMicrophone()
            println("ASR result: $recognizedText")
            require(recognizedText.isNotBlank()) { "ASR returned empty text" }

            // 2. Ask the AI.
            val response = aiClient.chatCompletion(
                ChatRequest(
                    model = AI_MODEL,
                    messages = listOf(UserChatMessage(recognizedText)),
                    stream = false
                )
            )
            require(response.success) { "AI request failed: ${response.originalResponse}" }

            val answer = response.choices.first().message.content ?: ""
            require(answer.isNotBlank()) { "AI returned no text" }
            println("AI answer: $answer")

            // 3. Speak the answer.
            playText(answer)
        }
    }

    /**
     * Records [RECORD_DURATION_MS] of audio from the microphone, streaming PCM
     * segments to the ASR service, and returns the final recognized text.
     */
    private suspend fun recognizeFromMicrophone(): String = suspendCoroutine { continuation ->
        val microphone = if (MICROPHONE_KEYWORD != null) {
            audioService.getMicrophoneByName(MICROPHONE_KEYWORD)
        } else {
            // Default line via AudioSystem — reliable on macOS (see openDefaultMicrophone).
            audioService.openDefaultMicrophone()
        } ?: throw IllegalStateException("No microphone available")

        val resumed = java.util.concurrent.atomic.AtomicBoolean(false)
        fun resumeOnce(block: () -> Unit) {
            if (resumed.compareAndSet(false, true)) {
                block()
            }
        }

        var lastText = ""
        val stream = asrClient.startStream(
            request = VolcanoASRRequest(user = VolcanoASRRequest.UserConfig(uid = ASR_UID)),
            callback = object : SimpleVolcanoASRStreamCallback() {
                override fun onResult(response: VolcanoASRResponse, isLast: Boolean) {
                    response.result?.text?.let { if (it.isNotBlank()) lastText = it }
                    println("partial: ${response.result?.text}")
                }

                override fun onCompleted(response: VolcanoASRResponse) {
                    audioService.releaseMicrophone()
                    resumeOnce { continuation.resume(lastText) }
                }

                override fun onError(t: Throwable) {
                    audioService.releaseMicrophone()
                    resumeOnce { continuation.resumeWith(Result.failure(t)) }
                }
            }
        )

        // Stream microphone data on a separate thread until the duration elapses.
        Thread {
            try {
                check(audioService.startRecording()) {
                    "Failed to start recording (microphone line not running). " +
                            "On macOS, grant microphone permission to the JVM/IDE running this test."
                }

                // 200ms segments at 16kHz/16bit/mono = 16000 * 2 * 0.2 = 6400 bytes
                val segmentSize = 6400
                val buffer = ByteArray(segmentSize)
                val deadline = System.currentTimeMillis() + RECORD_DURATION_MS

                // Buffer one segment ahead so the final real chunk can be flagged as
                // terminating (a trailing empty packet is legal now, but sending the
                // last audio with the negative-sequence flag matches the demo).
                var pending: ByteArray? = null
                var totalBytes = 0L

                while (System.currentTimeMillis() < deadline) {
                    val read = audioService.readMicrophone(buffer)
                    if (read <= 0) continue
                    totalBytes += read
                    pending?.let { stream.sendAudio(it, isLast = false) }
                    pending = buffer.copyOf(read)
                }

                if (pending != null) {
                    stream.sendAudio(pending!!, isLast = true)
                } else {
                    // No audio captured at all — surface it instead of sending silence.
                    stream.close()
                    error(
                        "No audio captured from microphone in ${RECORD_DURATION_MS}ms " +
                                "(readMicrophone kept returning <= 0). Check the selected device " +
                                "and OS microphone permission."
                    )
                }
                println("Captured $totalBytes bytes from microphone")
            } catch (e: Exception) {
                audioService.releaseMicrophone()
                resumeOnce { continuation.resumeWith(Result.failure(e)) }
            }
        }.apply { isDaemon = true }.start()
    }

    /**
     * Synthesizes [text] via TTS (PCM 16k) and plays it through the speaker.
     */
    private fun playText(text: String) {
        val speaker = if (SPEAKER_KEYWORD != null) {
            audioService.getSpeakerByName(SPEAKER_KEYWORD)
        } else {
            // Default line via AudioSystem — reliable on macOS (see openDefaultSpeaker).
            audioService.openDefaultSpeaker()
        } ?: throw IllegalStateException("No speaker available")

        audioService.startPlaying()

        val request = VolcanoTTSRequestV3(
            user = VolcanoTTSRequestV3.UserConfig(uid = ASR_UID),
            requestConfig = VolcanoTTSRequestV3.RequestConfig(
                text = text,
                speaker = TTS_SPEAKER,
                // PCM 16kHz so the bytes can be written straight to the SourceDataLine.
                audioParams = VolcanoTTSRequestV3.RequestConfig.AudioConfig(
                    format = VolcanoTTSRequestV3.AudioFormat.PCM,
                    sampleRate = 16000
                )
            )
        )

        runBlocking {
            suspendCoroutine { continuation ->
                ttsClient.sendWebSocketRequest(request, object : SimpleVolcanoTTSWebSocketClientStreamCallbackV3() {
                    override fun onReceived(payload: ByteArray) {
                        audioService.writeSpeaker(payload)
                    }

                    override fun onCompleted(
                        sentence: VolcanoTTSResponseV3.Sentence,
                        usage: VolcanoTTSResponseV3.Usage
                    ) {
                        audioService.drainSpeaker()
                        audioService.releaseSpeaker()
                        continuation.resume(Unit)
                    }

                    override fun onError(t: Throwable) {
                        audioService.releaseSpeaker()
                        continuation.resumeWith(Result.failure(t))
                    }
                })
            }
        }
    }

    /**
     * Diagnostic: lists every microphone device and probes which ones actually
     * deliver PCM bytes. Run this to pick a working [MICROPHONE_KEYWORD] and to
     * tell a device-selection problem apart from an OS permission problem.
     */
    @Test
    fun listAndProbeMicrophones() {
        val devices = audioService.getSystemMicrophoneDevices()
        println("Found ${devices.size} microphone device(s):")
        devices.forEachIndexed { index, device ->
            println("  [$index] name='${device.name}' vendor='${device.vendor}' desc='${device.description}'")
        }

        println("\nProbing each device for ~1s of audio...")
        devices.forEach { device ->
            val line = audioService.openTargetDataLine(device.mixerInfo)
            if (line == null) {
                println("  '${device.name}' -> could not open (format unsupported?)")
                return@forEach
            }

            val started = audioService.startRecording()
            if (!started) {
                println("  '${device.name}' -> opened but failed to start")
                audioService.releaseMicrophone()
                return@forEach
            }

            val buffer = ByteArray(6400)
            var total = 0L
            var nonSilent = false
            val deadline = System.currentTimeMillis() + 1000
            while (System.currentTimeMillis() < deadline) {
                val read = audioService.readMicrophone(buffer)
                if (read <= 0) continue
                total += read
                // Detect whether we actually got signal, not just zeroed frames.
                if (!nonSilent) {
                    nonSilent = (0 until read).any { buffer[it].toInt() != 0 }
                }
            }
            audioService.releaseMicrophone()

            val verdict = when {
                total <= 0 -> "NO DATA (permission denied or dummy mixer)"
                !nonSilent -> "data but all-silence (mic muted or no permission)"
                else -> "OK — captured $total bytes with signal"
            }
            println("  '${device.name}' -> $verdict")
        }

        println("\nPick a name above that says OK and set MICROPHONE_KEYWORD to a unique substring of it.")
    }

    /**
     * 对照测试：分别用「AudioSystem.getLine」和「mixer.getLine」拿麦克风，各读 1 秒，
     * 直接比出到底是哪种方式在 macOS 上读不到数据（用来定位是不是封装问题）。
     */
    @Test
    fun compareMicrophoneAccess() {
        val format = AudioFormat(16000f, 16, 1, true, false)

        fun readOneSecond(line: TargetDataLine): Long {
            line.open(format)
            line.start()
            val buffer = ByteArray(6400)
            var total = 0L
            val deadline = System.currentTimeMillis() + 1000
            while (System.currentTimeMillis() < deadline) {
                val read = line.read(buffer, 0, buffer.size)
                if (read > 0) total += read
            }
            line.stop()
            line.close()
            return total
        }

        // 方式一：AudioSystem.getLine（系统默认输入，原生常用写法）
        try {
            val info = DataLine.Info(TargetDataLine::class.java, format)
            val line = AudioSystem.getLine(info) as TargetDataLine
            val total = readOneSecond(line)
            println("方式一 AudioSystem.getLine 读到 $total 字节")
        } catch (e: Exception) {
            println("方式一 AudioSystem.getLine 失败: ${e.message}")
        }

        // 方式二：mixer.getLine（VertexAudioService 现在的做法）
        val macMixer = AudioSystem.getMixerInfo().firstOrNull { it.name.contains("MacBook", true) }
        if (macMixer != null) {
            try {
                val info = DataLine.Info(TargetDataLine::class.java, format)
                val line = AudioSystem.getMixer(macMixer).getLine(info) as TargetDataLine
                val total = readOneSecond(line)
                println("方式二 mixer.getLine('${macMixer.name}') 读到 $total 字节")
            } catch (e: Exception) {
                println("方式二 mixer.getLine 失败: ${e.message}")
            }
        } else {
            println("方式二 找不到包含 'MacBook' 的 mixer")
        }
    }
}
