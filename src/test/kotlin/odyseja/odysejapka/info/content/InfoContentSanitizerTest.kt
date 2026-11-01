package odyseja.odysejapka.info.content

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class InfoContentSanitizerTest {

    @Test
    fun `should turn bold word processor spans into strong and drop styles`() {
        val html = """<span style="font-weight: normal" id="docs-internal-guid-1"><p dir="ltr" style="text-align: center">""" +
            """<span style="font-size: 18pt; font-family: Arial; color: rgb(0, 0, 0); font-weight: 700">PATRONI</span></p></span>"""

        assertThat(InfoContentSanitizer.sanitize(html)).isEqualTo("<p><strong>PATRONI</strong></p>")
    }

    @Test
    fun `should unwrap the normal weight bold wrapper of google docs and map b and i`() {
        val html = """<b style="font-weight:normal" id="docs-internal-guid-2"><p>Tekst <b>ważny</b> i <i>kursywa</i></p></b>"""

        assertThat(InfoContentSanitizer.sanitize(html)).isEqualTo("<p>Tekst <strong>ważny</strong> i <em>kursywa</em></p>")
    }

    @Test
    fun `should keep the accent class on paragraphs and table cells only`() {
        val html = """<p class="accent">Gdynia</p><p class="other">x</p><table><tbody><tr><td class="accent"><p>06:30</p></td><td class="x"><p>y</p></td></tr></tbody></table>"""

        assertThat(InfoContentSanitizer.sanitize(html)).isEqualTo(
            """<p class="accent">Gdynia</p><p>x</p><table><tbody><tr><td class="accent"><p>06:30</p></td><td><p>y</p></td></tr></tbody></table>""",
        )
    }

    @Test
    fun `should keep tables and drop colgroup widths and styles`() {
        val html = """<table style="min-width: 50px"><colgroup><col style="min-width: 25px"></colgroup><tbody><tr><th colspan="1"><p>Godzina</p></th></tr></tbody></table>"""

        assertThat(InfoContentSanitizer.sanitize(html)).isEqualTo("<table><tbody><tr><th><p>Godzina</p></th></tr></tbody></table>")
    }

    @Test
    fun `should map headings onto the two allowed levels`() {
        val html = "<h1>A</h1><h2>B</h2><h3>C</h3><h4>D</h4><h6>E</h6>"

        assertThat(InfoContentSanitizer.sanitize(html)).isEqualTo("<h2>A</h2><h2>B</h2><h3>C</h3><h3>D</h3><h3>E</h3>")
    }

    @Test
    fun `should turn a plain div into a paragraph and unwrap a div with blocks`() {
        assertThat(InfoContentSanitizer.sanitize("<div>Tekst</div>")).isEqualTo("<p>Tekst</p>")
        assertThat(InfoContentSanitizer.sanitize("<div><p>A</p><ul><li>B</li></ul></div>")).isEqualTo("<p>A</p><ul><li>B</li></ul>")
    }

    @Test
    fun `should keep links with a safe protocol only`() {
        val html = """<p><a href="https://odyseja.pl" target="_blank" rel="noopener">ok</a> <a href="javascript:alert(1)">zły</a></p>"""

        assertThat(InfoContentSanitizer.sanitize(html)).isEqualTo("""<p><a href="https://odyseja.pl">ok</a> <a>zły</a></p>""")
    }

    @Test
    fun `should drop scripts, unsupported tags and blockquotes`() {
        val html = "<p>A</p><script>alert(1)</script><blockquote>cytat</blockquote><u>pod</u><img src=\"x.png\">"

        assertThat(InfoContentSanitizer.sanitize(html)).isEqualTo("<p>A</p>cytatpod")
    }

    @Test
    fun `should keep polish letters and lists`() {
        val html = "<ul><li><p>Żółć</p></li></ul><ol><li>Gęślą</li></ol>"

        assertThat(InfoContentSanitizer.sanitize(html)).isEqualTo(html)
    }

    @Test
    fun `should return an empty string for blank input and be idempotent`() {
        assertThat(InfoContentSanitizer.sanitize("  ")).isEmpty()

        val once = InfoContentSanitizer.sanitize("""<p style="color:red" class="accent"><span style="font-weight:bold">x</span> &nbsp; y</p>""")
        assertThat(InfoContentSanitizer.sanitize(once)).isEqualTo(once)
    }
}
