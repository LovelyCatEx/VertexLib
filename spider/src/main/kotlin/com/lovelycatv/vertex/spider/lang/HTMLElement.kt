package com.lovelycatv.vertex.spider.lang

/**
 * An HTML element node, e.g. `<div id="main" class="a b">...</div>`.
 *
 * This is an abstract adapter type: the framework-agnostic shape lives here, while each
 * backend (jsoup, selenium, ...) provides a concrete subclass that holds its native handle
 * and implements the query methods such as [findElementsByXPath] against its own engine.
 */
abstract class HTMLElement(
    /**
     * The lower-cased tag name, e.g. `div`, `a`, `p`.
     */
    val tagName: String,
    /**
     * The element's attributes, preserving their raw values.
     */
    val attributes: Map<String, String>,
    /**
     * Combined text of this element and its descendants (see [HTMLNode.text]).
     */
    override val text: String,
    /**
     * Text owned directly by this element, excluding text of child elements.
     */
    val ownText: String,
) : AbstractHTMLNode() {
    override val nodeType: HTMLNodeType
        get() = HTMLNodeType.ELEMENT

    /**
     * The `value` attribute (e.g. of a form control), empty when absent.
     */
    override val value: String
        get() = attributes["value"] ?: ""

    /**
     * The `id` attribute, empty when absent.
     */
    val id: String
        get() = attributes["id"] ?: ""

    /**
     * The raw `class` attribute, empty when absent.
     */
    val className: String
        get() = attributes["class"] ?: ""

    /**
     * The individual css classes of this element.
     */
    val classNames: List<String>
        get() = className.split(WHITESPACE).filter { it.isNotBlank() }

    /**
     * Returns the value of attribute [name], or `null` when the element has no such attribute.
     */
    fun attr(name: String): String? = attributes[name]

    /**
     * Whether the element declares an attribute named [name].
     */
    fun hasAttr(name: String): Boolean = attributes.containsKey(name)

    /**
     * Finds descendant elements matching the given [xpath] expression.
     *
     * This is an adapter hook: concrete subclasses delegate to their backend's native
     * XPath engine (e.g. jsoup's `selectXpath`). We do not implement XPath ourselves.
     */
    abstract fun findElementsByXPath(xpath: String): List<HTMLElement>

    abstract fun findElementByXPath(xpath: String): HTMLElement?

    abstract fun findElementsByCssSelector(selector: String): List<HTMLElement>

    abstract fun findElementByCssSelector(selector: String): HTMLElement?

    override fun toString(): String =
        "<$tagName${attributes.entries.joinToString("") { " ${it.key}=\"${it.value}\"" }}>"

    private companion object {
        val WHITESPACE = Regex("\\s+")
    }
}
