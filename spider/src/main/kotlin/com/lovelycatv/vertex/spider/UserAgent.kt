package com.lovelycatv.vertex.spider

/**
 * Constructs a `User-Agent` header string.
 *
 * Format: `<prefix> (<platform>) <engine> <product1> <product2> ...`
 *
 * Example:
 * ```
 * Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36
 * ```
 */
class UserAgent(
    val platform: Platform,
    val engine: Engine? = null,
    val products: List<Product> = emptyList(),
    val prefix: String = DEFAULT_PREFIX
) {
    override fun toString(): String = buildString {
        append(prefix)
        append(" (").append(platform).append(")")
        engine?.let { append(' ').append(it) }
        products.forEach { append(' ').append(it) }
    }

    /**
     * Tokens inside the parentheses right after `Mozilla/5.0`, joined by `"; "`.
     * e.g. `Windows NT 10.0; Win64; x64`
     */
    class Platform(private val tokens: List<String>) {
        constructor(vararg tokens: String) : this(tokens.toList())

        override fun toString(): String = tokens.joinToString("; ")

        /**
         * Returns a new [Platform] with [extra] tokens appended. Useful for adding
         * `rv:<version>` when constructing a Firefox UA.
         */
        fun with(vararg extra: String): Platform = Platform(tokens + extra)

        companion object {
            val WINDOWS_10 = windows()
            val WINDOWS_11 = windows()
            val MAC_INTEL = macOS()
            val LINUX_X64 = Platform("X11", "Linux x86_64")
            val ANDROID = android()

            fun windows(ntVersion: String = "10.0", arch: String = "Win64; x64"): Platform =
                Platform(listOf("Windows NT $ntVersion") + arch.split("; "))

            fun macOS(version: String = "10_15_7"): Platform =
                Platform("Macintosh", "Intel Mac OS X $version")

            fun android(version: String = "10", device: String = "K"): Platform =
                Platform("Linux", "Android $version", device)
        }
    }

    /**
     * Rendering engine token, e.g. `AppleWebKit/537.36 (KHTML, like Gecko)`.
     */
    class Engine(
        val name: String,
        val version: String,
        val details: String? = null
    ) {
        override fun toString(): String = buildString {
            append(name).append('/').append(version)
            details?.let { append(" (").append(it).append(')') }
        }

        companion object {
            fun appleWebKit(version: String = "537.36"): Engine =
                Engine("AppleWebKit", version, "KHTML, like Gecko")

            fun gecko(buildDate: String = "20100101"): Engine =
                Engine("Gecko", buildDate)
        }
    }

    /**
     * A `Name/Version` token trailing the engine, e.g. `Chrome/120.0.0.0`.
     * [version] may be null for bare markers like `Mobile`.
     */
    class Product(val name: String, val version: String? = null) {
        override fun toString(): String =
            if (version == null) name else "$name/$version"

        companion object {
            fun chrome(version: String = "120.0.0.0"): Product = Product("Chrome", version)
            fun safari(version: String = "537.36"): Product = Product("Safari", version)
            fun edge(version: String = "120.0.0.0"): Product = Product("Edg", version)
            fun edgeAndroid(version: String = "120.0.0.0"): Product = Product("EdgA", version)
            fun firefox(version: String = "121.0"): Product = Product("Firefox", version)
            fun version(version: String): Product = Product("Version", version)
            fun mobile(): Product = Product("Mobile")
        }
    }

    class Builder {
        private var prefix: String = DEFAULT_PREFIX
        private var platform: Platform = Platform.WINDOWS_10
        private var engine: Engine? = null
        private val products: MutableList<Product> = mutableListOf()

        fun prefix(prefix: String): Builder = apply { this.prefix = prefix }
        fun platform(platform: Platform): Builder = apply { this.platform = platform }
        fun engine(engine: Engine): Builder = apply { this.engine = engine }
        fun product(product: Product): Builder = apply { this.products.add(product) }
        fun products(vararg products: Product): Builder = apply { this.products.addAll(products) }

        fun build(): UserAgent = UserAgent(platform, engine, products.toList(), prefix)
    }

    companion object {
        const val DEFAULT_PREFIX = "Mozilla/5.0"

        fun builder(): Builder = Builder()

        // ---------- Chrome ----------

        fun chromeOnWindows(chromeVersion: String = "120.0.0.0"): UserAgent = builder()
            .platform(Platform.WINDOWS_10)
            .engine(Engine.appleWebKit())
            .product(Product.chrome(chromeVersion))
            .product(Product.safari())
            .build()

        fun chromeOnMac(chromeVersion: String = "120.0.0.0"): UserAgent = builder()
            .platform(Platform.MAC_INTEL)
            .engine(Engine.appleWebKit())
            .product(Product.chrome(chromeVersion))
            .product(Product.safari())
            .build()

        fun chromeOnAndroid(
            chromeVersion: String = "120.0.0.0",
            androidVersion: String = "10",
            device: String = "K"
        ): UserAgent = builder()
            .platform(Platform.android(androidVersion, device))
            .engine(Engine.appleWebKit())
            .product(Product.chrome(chromeVersion))
            .product(Product.mobile())
            .product(Product.safari())
            .build()

        // ---------- Edge ----------

        fun edgeOnWindows(edgeVersion: String = "120.0.0.0"): UserAgent = builder()
            .platform(Platform.WINDOWS_10)
            .engine(Engine.appleWebKit())
            .product(Product.chrome(edgeVersion))
            .product(Product.safari())
            .product(Product.edge(edgeVersion))
            .build()

        fun edgeOnMac(edgeVersion: String = "120.0.0.0"): UserAgent = builder()
            .platform(Platform.MAC_INTEL)
            .engine(Engine.appleWebKit())
            .product(Product.chrome(edgeVersion))
            .product(Product.safari())
            .product(Product.edge(edgeVersion))
            .build()

        fun edgeOnAndroid(
            edgeVersion: String = "120.0.0.0",
            androidVersion: String = "10",
            device: String = "K"
        ): UserAgent = builder()
            .platform(Platform.android(androidVersion, device))
            .engine(Engine.appleWebKit())
            .product(Product.chrome(edgeVersion))
            .product(Product.mobile())
            .product(Product.safari())
            .product(Product.edgeAndroid(edgeVersion))
            .build()

        // ---------- Firefox ----------

        fun firefoxOnWindows(firefoxVersion: String = "121.0"): UserAgent = builder()
            .platform(Platform.WINDOWS_10.with("rv:$firefoxVersion"))
            .engine(Engine.gecko())
            .product(Product.firefox(firefoxVersion))
            .build()

        fun firefoxOnMac(firefoxVersion: String = "121.0"): UserAgent = builder()
            .platform(Platform.MAC_INTEL.with("rv:$firefoxVersion"))
            .engine(Engine.gecko())
            .product(Product.firefox(firefoxVersion))
            .build()

        fun firefoxOnAndroid(
            firefoxVersion: String = "121.0",
            androidVersion: String = "10",
            device: String = "K"
        ): UserAgent = builder()
            .platform(Platform.android(androidVersion, device).with("rv:$firefoxVersion"))
            .engine(Engine.gecko(firefoxVersion))
            .product(Product.firefox(firefoxVersion))
            .build()

        // ---------- Safari ----------

        fun safariOnMac(safariVersion: String = "17.0"): UserAgent = builder()
            .platform(Platform.MAC_INTEL)
            .engine(Engine.appleWebKit("605.1.15"))
            .product(Product.version(safariVersion))
            .product(Product.safari("605.15"))
            .build()
    }
}
