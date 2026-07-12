package com.lovelycatv.vertex.spider.adatper.selenium

import com.lovelycatv.vertex.log.logger
import org.openqa.selenium.Capabilities
import org.openqa.selenium.MutableCapabilities
import org.openqa.selenium.remote.Augmenter
import org.openqa.selenium.remote.RemoteWebDriver
import java.net.URI
import java.net.URL

@Suppress("FINITE_BOUNDS_VIOLATION_IN_JAVA")
abstract class RemoteSeleniumSpider<D: RemoteWebDriver, O: Capabilities>(
    private val url: URL,
    seleniumOptions: SeleniumSpiderOptions,
    webDriverOptions: (SeleniumSpiderOptions) -> O
) : SeleniumSpider<D, O>(seleniumOptions, webDriverOptions) {
    private val logger = logger()

    protected fun createAugmentedDriver(options: O): RemoteWebDriver {
        val raw = RemoteWebDriver(url, options)
        rewriteCdpEndpoint(raw)
        return Augmenter().augment(raw) as RemoteWebDriver
    }

    private fun rewriteCdpEndpoint(driver: RemoteWebDriver) {
        val caps = driver.capabilities
        val cdp = caps.getCapability("se:cdp") as? String ?: return

        val original = try {
            URI(cdp)
        } catch (e: Exception) {
            logger.error("Could not rewrite cdp endpoint", e)
            return
        }

        val fixed = URI(
            original.scheme, original.userInfo, url.host, url.port,
            original.path, original.query, original.fragment
        ).toString()

        if (fixed == cdp) {
            return
        }

        val patched = MutableCapabilities(caps).apply {
            setCapability("se:cdp", fixed)
        }

        try {
            val field = RemoteWebDriver::class.java.getDeclaredField("capabilities")
            field.isAccessible = true
            field.set(driver, patched)
        } catch (e: Exception) {
            logger.error("Could not rewrite cdp endpoint", e)
            throw e
        }
    }
}
