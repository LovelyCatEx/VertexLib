package com.lovelycatv.vertex.spider.adatper.selenium.chromium

import com.lovelycatv.vertex.spider.HTTPStatus
import com.lovelycatv.vertex.spider.adatper.selenium.RemoteResponse
import com.lovelycatv.vertex.spider.adatper.selenium.interceptor.ResponseInterceptor
import org.openqa.selenium.devtools.Command
import org.openqa.selenium.devtools.DevTools
import org.openqa.selenium.devtools.v150.network.Network
import java.util.*

interface DevToolsResponseInterceptorApplication {
    // The CDP child session that createSession() attaches is bound to a page target. After a
    // navigation (or if Grid tears it down between fetches), the sessionId held inside the
    // DevTools client goes stale, and the next command — findTarget's getTargets during
    // createSession() — is sent with that stale id and fails with "No session with given id".
    // disconnectSession() clears cdpSession to null (and swallows any errors from the dead
    // session), so the subsequent createSession() attaches fresh. Network.enable is per-session
    // and must be re-sent every time.
    fun refreshCdpNetworkSession(devTools: DevTools) {
        devTools.disconnectSession()
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
    }

    // The listener lives on the underlying Connection, not on the CDP session — it survives
    // disconnect/reconnect, so attach it exactly once per DevTools instance. Interceptors are
    // resolved via the provider at event time so runtime add/remove is visible.
    fun attachResponseListener(devTools: DevTools, interceptorsProvider: () -> List<ResponseInterceptor>) {
        devTools.addListener(Network.responseReceived()) { resp ->
            interceptorsProvider().forEach {
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