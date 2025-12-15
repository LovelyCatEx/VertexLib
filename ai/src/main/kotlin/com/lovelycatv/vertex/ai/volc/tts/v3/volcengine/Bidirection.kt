package com.lovelycatv.vertex.ai.volc.tts.v3.volcengine

import com.google.gson.Gson
import com.lovelycatv.vertex.ai.network.VertexOkHttp
import com.lovelycatv.vertex.ai.volc.VolcanoEngineConstants
import com.lovelycatv.vertex.ai.volc.tts.v3.protocol.EventType
import com.lovelycatv.vertex.ai.volc.tts.v3.protocol.Message
import com.lovelycatv.vertex.ai.volc.tts.v3.protocol.MsgType
import com.lovelycatv.vertex.ai.volc.tts.v3.protocol.SimpleVolcanoTTSWebSocketClientStreamCallbackV3
import com.lovelycatv.vertex.ai.volc.tts.v3.protocol.VolcanoTTSWebSocketClientV3
import com.lovelycatv.vertex.log.logger
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.slf4j.Logger
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Files
import java.util.*
import java.util.Map

object Bidirection {
    private val log = logger()
    private val gson = Gson()

    /**
     * Get resource ID based on voice type
     *
     * @param voice Voice type string
     * @return Corresponding resource ID
     */
    fun voiceToResourceId(voice: String): String {
        // Map different voice types to resource IDs based on actual needs
        if (voice.startsWith("S_")) {
            return "volc.megatts.default"
        }
        return "volc.service_type.10029"
    }

    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        runBlocking {
            // Configure parameters
            val appId = System.getProperty("appId", "8687248201")
            val accessToken = System.getProperty("accessToken", "siP9uaJQoSadboCNogd91fpZ5dcRWaWn")
            val resourceId = System.getProperty("resourceId", "seed-tts-2.0")
            val voice = System.getProperty("voice", "zh_female_xiaohe_uranus_bigtts")
            val text = System.getProperty("text", "测试语音合成。你好，世界。啊？还有这种操作。")
            val encoding = System.getProperty("encoding", "mp3")

            require(!(appId.isEmpty() || accessToken.isEmpty())) { "Please set appId and accessToken system properties" }

            // Create WebSocket client
            val client = VolcanoTTSWebSocketClientV3(
                VolcanoEngineConstants.TTS_V3_WS_API_URL,

                VertexOkHttp(
                    timeoutSeconds = 60,
                    enableLogging = true,
                    preInterceptor = { chain ->
                        val originalRequest = chain.request()
                        val requestWithAuth = originalRequest.newBuilder()
                            .addHeader("Authorization", "Bearer; $accessToken")
                            .addHeader("X-Api-App-Id", appId)
                            .addHeader("X-Api-Access-Key", accessToken)
                            .addHeader("X-Api-Request-Id", UUID.randomUUID().toString())
                            .addHeader("X-Api-Resource-Id", resourceId.ifEmpty { voiceToResourceId(voice) })
                            .addHeader("X-Control-Require-Usage-Tokens-Return", "*")
                            .build()
                        chain.proceed(requestWithAuth)
                    }
                ).okHttpClient,
                callback = object : SimpleVolcanoTTSWebSocketClientStreamCallbackV3() {
                    override fun onConnected() {
                    }

                    override fun onMessage(message: Message) {
                    }

                    override fun onSendSuccessfully(message: Message) {
                    }

                    override fun onClosing(code: Int, reason: String) {
                    }

                    override fun onClosed(code: Int, reason: String) {
                    }

                    override fun onError(t: Throwable) {
                    }
                },
                enableLogging = true
            )

            try {
                val request = Map.of<String?, Any?>(
                    "user", Map.of<String?, String?>("uid", "2115242973"),
                    "namespace", "BidirectionalTTS",
                    "req_params", Map.of<String?, Any?>(
                        "speaker", voice,
                        "audio_params", Map.of(
                            "format", encoding,
                            "sample_rate", 24000,
                            "enable_timestamp", true
                        ),  // additions requires a JSON string
                        "additions", gson.toJson(
                            Map.of<String?, Boolean?>(
                                "disable_markdown_filter", false
                            )
                        )
                    )
                )

                // Start connection
                client.sendStartConnection()
                // Wait for connection started
                client.waitForMessage(MsgType.FULL_SERVER_RESPONSE, EventType.CONNECTION_STARTED)

                // Process each sentence
                val sentences: Array<String?> = text.split("。".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                var audioReceived = false
                for (i in sentences.indices) {
                    println("开始处理: ${sentences[i]}")
                    if (sentences[i]!!.trim { it <= ' ' }.isEmpty()) {
                        continue
                    }

                    val sessionId = UUID.randomUUID().toString()
                    val audioStream = ByteArrayOutputStream()

                    // Start session
                    val startReq = Map.of<String?, Any?>(
                        "user", request.get("user"),
                        "namespace", request.get("namespace"),
                        "req_params", request.get("req_params"),
                        "event", EventType.START_SESSION.value
                    )
                    client.sendStartSession(gson.toJson(startReq), sessionId)
                    // Wait for session started
                    client.waitForMessage(MsgType.FULL_SERVER_RESPONSE, EventType.SESSION_STARTED)

                    // Send text
                    for (c in sentences[i]!!.toCharArray()) {
                        // Create new req_params with text
                        val currentReqParams: MutableMap<String?, Any?> = HashMap<String?, Any?>(
                            request.get("req_params") as MutableMap<String?, Any?>?
                        )
                        currentReqParams.put("text", c.toString())

                        // Create current request
                        val currentRequest = Map.of<String?, Any?>(
                            "user", request.get("user"),
                            "namespace", request.get("namespace"),
                            "req_params", currentReqParams,
                            "event", EventType.TASK_REQUEST.value
                        )

                        client.sendTaskRequest(gson.toJson(currentRequest), sessionId)
                    }

                    // End session
                    client.sendFinishSession(sessionId)

                    // Receive response
                    while (true) {
                        val msg = client.receiveMessage()
                        when (msg.type) {
                            MsgType.FULL_SERVER_RESPONSE -> {}
                            MsgType.AUDIO_ONLY_SERVER -> {
                                if (!audioReceived && audioStream.size() > 0) {
                                    audioReceived = true
                                }

                                audioStream.write(msg.payload)
                            }

                            else -> throw RuntimeException("Unexpected message: " + msg)
                        }
                        if (msg.event == EventType.SESSION_FINISHED) {
                            break
                        }
                    }

                    if (audioStream.size() > 0) {
                        val fileName = String.format("%s_session_%d.%s", voice, i, encoding)
                        Files.write(File(fileName).toPath(), audioStream.toByteArray())
                        log.info("Audio saved to file: {}", fileName)
                    }
                }

                if (!audioReceived) {
                    throw RuntimeException("No audio data received")
                }

                // End connection
                client.sendFinishConnection()
            } finally {
            }
            delay(100000)
        }
    }
}