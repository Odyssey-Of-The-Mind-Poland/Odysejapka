package odyseja.odysejapka.info.figma

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class InfoNameMatcherTest {

    private val matcher = InfoNameMatcher()

    private fun iconOf(name: String) = matcher.match(name)?.icon

    @Test
    fun `should match an exact name`() {
        assertThat(iconOf("Wstęp na występy")).isEqualTo("ticket-01")
    }

    @Test
    fun `should ignore case, diacritics and punctuation`() {
        assertThat(iconOf("WSTEP NA WYSTEPY")).isEqualTo("ticket-01")
        assertThat(iconOf("dolacz do nas")).isEqualTo("users-01")
    }

    @Test
    fun `should match swapped words`() {
        assertThat(iconOf("Gdzie, kiedy i co?")).isEqualTo("map-01")
    }

    @Test
    fun `should ignore the season in a name`() {
        assertThat(iconOf("Problemy 2025-2026")).isEqualTo("file-05")
    }

    @Test
    fun `should tolerate typos and small renames`() {
        assertThat(iconOf("Dojazd i parkingi")).isEqualTo("car-01")
        assertThat(iconOf("Zasdy zachowania")).isEqualTo("alert-circle")
        assertThat(iconOf("Atrakcje na Finale!")).isEqualTo("rocket-01")
    }

    @Test
    fun `should use the aliases of a tile`() {
        assertThat(iconOf("Spontan")).isEqualTo("dice-1")
    }

    @Test
    fun `should not match names that only look a bit alike`() {
        assertThat(iconOf("Uściślenia Problemów")).isNull()
        assertThat(iconOf("Pytania po Finale")).isNull()
        assertThat(iconOf("Dzięki, Patroni!")).isNull()
        assertThat(iconOf("Immergas Party")).isNull()
    }

    @Test
    fun `should not pick a tile when two are equally close`() {
        val twins = InfoNameMatcher(
            listOf(
                FigmaTileStyle(listOf("Scena główna"), "map-01", "blue"),
                FigmaTileStyle(listOf("Scena glówna"), "car-01", "red")
            )
        )

        assertThat(twins.match("Scena główna")).isNull()
    }
}
