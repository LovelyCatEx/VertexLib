package com.lovelycatv.vertex.ai.volc.tts.v3

import com.lovelycatv.vertex.ai.volc.tts.v3.protocol.SimpleVolcanoTTSWebSocketClientStreamCallbackV3
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class VolcanoTTSClientV3Test {
    private val client = VolcanoTTSClientV3(
        "seed-tts-2.0",
        VolcanoTTSClientV3Config(
            appId = System.getProperty("VolcanoTTSClientTestAppId"),
            accessToken = System.getProperty("VolcanoTTSClientTestAppToken"),
            enableLogging = true
        )
    )

    private val exampleRequest = VolcanoTTSRequestV3(
        user = VolcanoTTSRequestV3.UserConfig(
            uid = System.getProperty("VolcanoTTSClientTestUserId")
        ),
        requestConfig = VolcanoTTSRequestV3.RequestConfig(
            text = "This is a test voice",
            speaker = "zh_female_xiaohe_uranus_bigtts",
            additions = VolcanoTTSRequestV3.RequestConfig.AdditionConfig().toJSONString()
        ),
    )

    @Test
    fun sendHttpRequest() {
        runBlocking {
            println(client.sendHttpRequest("seed-tts-2.0", exampleRequest))
        }
    }

    @Test
    fun sendWebSocketRequest() {
        runBlocking {
            suspendCoroutine {
                client.sendWebSocketRequest(exampleRequest, object : SimpleVolcanoTTSWebSocketClientStreamCallbackV3() {
                    override fun onReceived(payload: ByteArray) {
                        println("received audio segment, size: ${payload.size}")
                    }

                    override fun onCompleted(
                        sentence: VolcanoTTSResponseV3.Sentence,
                        usage: VolcanoTTSResponseV3.Usage
                    ) {
                        println(sentence)
                        println(usage)
                        it.resume(true)
                    }
                })
            }
        }
    }
}