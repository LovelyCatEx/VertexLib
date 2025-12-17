package com.lovelycatv.vertex.ai.volc.tts.v1

import com.lovelycatv.vertex.ai.volc.tts.v1.protocol.SimpleVolcanoTTSWebSocketClientStreamCallbackV1
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class VolcanoTTSClientV1Test {
    private val client = VolcanoTTSClientV1(
        System.getProperty("VolcanoTTSClientTestAppToken"),
        enableLogging = true
    )

    private val exampleRequest = VolcanoTTSRequestV1(
        app = VolcanoTTSRequestV1.AppConfig(
            appId = System.getProperty("VolcanoTTSClientTestAppId"),
            token = System.getProperty("VolcanoTTSClientTestAppToken"),
            cluster = "volcano_tts"
        ),
        user = VolcanoTTSRequestV1.UserConfig(
            uid = System.getProperty("VolcanoTTSClientTestUserId")
        ),
        audio = VolcanoTTSRequestV1.AudioConfig(
            voiceType = "BV001_streaming",
            encoding = VolcanoTTSRequestV1.Encoding.PCM
        ),
        request = VolcanoTTSRequestV1.RequestConfig(
            text = "This is a test voice",
            operation = VolcanoTTSRequestV1.Operation.QUERY
        )
    )

    private val exampleStreamRequest = exampleRequest.copy(
        request = exampleRequest.request.copy(
            operation = VolcanoTTSRequestV1.Operation.SUBMIT
        )
    )

    @Test
    fun sendHttpRequest() {
        runBlocking {
            println(client.sendHttpRequest(exampleRequest))
        }
    }

    @Test
    fun sendWebSocketRequest() {
        runBlocking {
            suspendCoroutine {
                client.sendWebSocketRequest(exampleStreamRequest, object : SimpleVolcanoTTSWebSocketClientStreamCallbackV1() {
                    override fun onSendSuccessful() {}

                    override fun onSendFailed() {
                        it.resume(false)
                    }

                    override fun onReceived(data: ByteArray) {
                        println("Received data, length: ${data.size}")
                    }

                    override fun onCompleted() {
                        it.resume(true)
                    }

                    override fun onError(throwable: Throwable) {
                        it.resume(false)
                    }
                })
            }
        }
    }

}