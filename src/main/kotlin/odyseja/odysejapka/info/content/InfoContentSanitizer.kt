package odyseja.odysejapka.info.content

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.safety.Safelist

/**
 * Reduces the HTML of an info to the elements the app knows how to draw (same set as the panel editor
 * schema): headings 2-3, paragraphs, bold, italic, links, lists and tables, plus the `accent` class on
 * paragraphs and table cells. Colours, sizes, fonts and every other attribute are dropped, so the app's
 * theme (light or dark) decides how the content looks.
 */
object InfoContentSanitizer {
    const val ACCENT_CLASS = "accent"

    private val safelist = Safelist()
        .addTags("h2", "h3", "p", "br", "strong", "em", "a", "ul", "ol", "li", "table", "thead", "tbody", "tr", "th", "td")
        .addAttributes("a", "href")
        .addAttributes("p", "class")
        .addAttributes("td", "class")
        .addProtocols("a", "href", "http", "https", "mailto", "tel")

    private val blockTags = setOf("p", "ul", "ol", "table", "h1", "h2", "h3", "h4", "h5", "h6")
    private val boldWeight = Regex("""font-weight\s*:\s*(bold|[6-9]00)""", RegexOption.IGNORE_CASE)
    private val normalWeight = Regex("""font-weight\s*:\s*(normal|[1-5]00)""", RegexOption.IGNORE_CASE)
    private val italicStyle = Regex("""font-style\s*:\s*italic""", RegexOption.IGNORE_CASE)

    fun sanitize(html: String): String {
        if (html.isBlank()) return ""
        val parsed = Jsoup.parseBodyFragment(html)
        parsed.outputSettings().prettyPrint(false)
        val body = parsed.body()
        body.select("*").toList().forEach { normalise(it) }

        val cleaned = Jsoup.parseBodyFragment(
            Jsoup.clean(body.html(), "", safelist, org.jsoup.nodes.Document.OutputSettings().prettyPrint(false)),
        )
        cleaned.outputSettings().prettyPrint(false)
        cleaned.body().select("[class]").forEach {
            if (it.attr("class") != ACCENT_CLASS) it.removeAttr("class")
        }
        return cleaned.body().html()
    }

    /** Maps what pasted word-processor markup means (bold spans, `<b>`, `<div>`) onto the allowed elements. */
    private fun normalise(el: Element) {
        val style = el.attr("style")
        when (el.tagName()) {
            "h1" -> el.tagName("h2")
            "h4", "h5", "h6" -> el.tagName("h3")
            "b" -> if (normalWeight.containsMatchIn(style)) el.unwrap() else el.tagName("strong")
            "i" -> el.tagName("em")
            "span" -> {
                if (italicStyle.containsMatchIn(style)) wrapChildren(el, "em")
                if (boldWeight.containsMatchIn(style)) wrapChildren(el, "strong")
            }
            "div" -> if (el.children().any { it.tagName() in blockTags }) el.unwrap() else el.tagName("p")
        }
    }

    private fun wrapChildren(el: Element, tag: String) {
        val wrapper = Element(tag)
        el.childNodes().toList().forEach { wrapper.appendChild(it) }
        el.appendChild(wrapper)
    }
}
