package com.lovelycatv.vertex.spider.adatper.selenium.chromium

import com.lovelycatv.vertex.spider.HTTPMethod
import com.lovelycatv.vertex.spider.HTTPStatus
import com.lovelycatv.vertex.spider.adatper.selenium.RemoteRequest
import com.lovelycatv.vertex.spider.adatper.selenium.RemoteResponse
import com.lovelycatv.vertex.spider.adatper.selenium.RemoteResponseBody
import com.lovelycatv.vertex.spider.adatper.selenium.interceptor.RequestInterceptor
import com.lovelycatv.vertex.spider.adatper.selenium.interceptor.ResponseBodyInterceptor
import com.lovelycatv.vertex.spider.adatper.selenium.interceptor.ResponseInterceptor
import com.lovelycatv.vertex.spider.adatper.selenium.interceptor.SeleniumInterceptor
import org.openqa.selenium.devtools.Command
import org.openqa.selenium.devtools.DevTools
import org.openqa.selenium.devtools.v150.network.Network
import org.slf4j.Logger
import java.util.*

interface DevToolsRequestResponseInterceptorApplication {
    val requestRecords: MutableMap<String, RemoteRequest>
    val responseRecords: MutableMap<String, RemoteResponse>

    val logger: Logger?

    // The CDP child session that createSession() attaches is bound to a page target. After a
    // navigation (or if Grid tears it down between fetches), the sessionId held inside the
    // DevTools client goes stale, and the next command — findTarget's getTargets during
    // createSession() — is sent with that stale id and fails with "No session with given id".
    // disconnectSession() clears cdpSession to null (and swallows any errors from the dead
    // session), so the subsequent createSession() attaches fresh. Network.enable is per-session
    // and must be re-sent every time.
    fun refreshCdpNetworkSession(devTools: DevTools) {
        logger?.info("Refreshing cdp network session...")
        devTools.disconnectSession()
        requestRecords.clear()
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
        logger?.info("Cdp network session refreshed")
    }

    // The listener lives on the underlying Connection, not on the CDP session — it survives
    // disconnect/reconnect, so attach it exactly once per DevTools instance. Interceptors are
    // resolved via the provider at event time so runtime add/remove is visible.
    fun attachListener(devTools: DevTools, interceptorsProvider: () -> List<SeleniumInterceptor>) {
        attachRequestListener(devTools, interceptorsProvider)
        attachResponseListener(devTools, interceptorsProvider)
    }

    fun attachRequestListener(devTools: DevTools, interceptorsProvider: () -> List<SeleniumInterceptor>) {
        devTools.addListener(Network.requestWillBeSent()) { resp ->
            val request = RemoteRequest(
                requestId = resp.requestId.toString(),
                url = resp.request.url,
                method = HTTPMethod.fromName(resp.request.method),
                timestamp = System.currentTimeMillis(),
                headers = resp.request.headers
            )

            requestRecords[request.requestId] = request

            interceptorsProvider().filterIsInstance<RequestInterceptor>().forEach {
                it.intercept(request)
            }
        }
    }

    fun attachResponseListener(devTools: DevTools, interceptorsProvider: () -> List<SeleniumInterceptor>) {
        devTools.addListener(Network.responseReceived()) { resp ->
            val requestId = resp.requestId.toString()

            val request = requestRecords[requestId]

            if (request != null) {
                val response = RemoteResponse(
                    requestId = requestId,
                    request = request,
                    timestamp = System.currentTimeMillis(),
                    url = resp.response.url,
                    status = HTTPStatus.fromCode(resp.response.status),
                    headers = resp.response.headers
                )

                responseRecords[response.requestId] = response

                interceptorsProvider().filterIsInstance<ResponseInterceptor>().forEach {
                    it.intercept(response)
                }
            } else {
                logger?.warn("Could not get request of request $requestId from records")
            }
        }

        devTools.addListener(Network.loadingFinished()) { resp ->
            val requestId = resp.requestId.toString()
            val response = responseRecords[requestId]

            val responseBody = response?.let {
                val body = try {
                    devTools.send(Network.getResponseBody(resp.requestId)).body
                } catch (e: Exception) {
                    logger?.warn("Could not get response body of request $requestId", e)
                    null
                }
                if (body != null) {
                    RemoteResponseBody(
                        requestId = requestId,
                        response = it,
                        responseBody = body
                    )
                } else {
                    logger?.warn("Could not get valid response body of request $requestId, received null")
                    null
                }
            }

            if (responseBody != null) {
                interceptorsProvider().filterIsInstance<ResponseBodyInterceptor>().forEach {
                    it.intercept(responseBody)
                }
            } else {
                logger?.warn("Could not get remote response of request $requestId from records")
            }
        }
    }
}