package com.lovelycatv.vertex.spider.adatper.selenium.chromium

import com.lovelycatv.vertex.spider.HTTPStatus
import com.lovelycatv.vertex.spider.adatper.selenium.RemoteResponse
import com.lovelycatv.vertex.spider.adatper.selenium.interceptor.ResponseInterceptor
import org.openqa.selenium.devtools.Command
import org.openqa.selenium.devtools.DevTools
import org.openqa.selenium.devtools.v150.network.Network
import java.util.*

interface DevToolsResponseInterceptorApplication {
    fun applyResponseInterceptor(devTools: DevTools, interceptors: List<ResponseInterceptor>) {
        devTools.createSession()
        // Network.enable is a Command<Void>; keeping that static type makes Kotlin emit a
        // `checkcast Void` on send()'s return value, which throws when CDP replies with a
        // (non-empty) result body. Widen to Command<*> so no Void cast is generated.
        val enableNetwork: Command<*> = Network.enable(
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
        )
        devTools.send(enableNetwork)

        devTools.addListener(Network.responseReceived()) { resp ->
            interceptors.forEach {
                it.intercept(
                    RemoteResponse(
                        timestamp = (resp.timestamp.toString().toDouble() * 1_000_000L).toLong(),
                        url = resp.response.url,
                        status = HTTPStatus.fromCode(resp.response.status),
                    )
                )
            }
        }
    }
}