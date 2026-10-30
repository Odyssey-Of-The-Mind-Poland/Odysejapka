package odyseja.odysejapka.info.figma

import java.text.Normalizer

/**
 * Icon and accent colour of every info tile in the "Mocks" page of the Figma redesign.
 *
 * Figma was drawn with the names the tiles had at the time ("Kiedy, gdzie i co?", "Problemy 2022-2023"),
 * while the live data has been renamed since, so tiles are matched by [InfoNameMatcher] and not by equality.
 * Glyphs the shared icon set lacks (apple, pill, hourglass, hand, cart, warning triangle) use the
 * closest icon that exists.
 */
data class FigmaTileStyle(val names: List<String>, val icon: String, val color: String)

object InfoFigmaStyles {
    val TILES = listOf(
        FigmaTileStyle(listOf("Kiedy, gdzie i co?"), "map-01", "blue"),
        FigmaTileStyle(listOf("Dojazd i parkowanie"), "car-01", "orange"),
        FigmaTileStyle(listOf("Szatnia i jedzenie"), "home-02", "red"),
        FigmaTileStyle(listOf("Wstęp na występy"), "ticket-01", "purple"),
        FigmaTileStyle(listOf("Zasady zachowania"), "alert-circle", "green"),
        FigmaTileStyle(listOf("Szoping w Szop Shopie"), "shopping-bag-01", "blue"),
        FigmaTileStyle(listOf("Odyseja w pigułce"), "lightbulb-02", "orange"),
        FigmaTileStyle(listOf("Problemy 2022-2023"), "file-05", "red"),
        FigmaTileStyle(listOf("Dołącz do nas!"), "users-01", "purple"),
        FigmaTileStyle(listOf("Przygotowania i występ"), "clock", "green"),
        FigmaTileStyle(listOf("Punktacja po występie"), "award-01", "blue"),
        FigmaTileStyle(listOf("Problem Spontaniczny", "Spontan"), "dice-1", "orange"),
        FigmaTileStyle(listOf("Atrakcje na Finale"), "rocket-01", "red"),
        FigmaTileStyle(listOf("Drużyny Kumpelskie"), "puzzle-piece-01", "purple")
    )
}

/**
 * Fuzzy name matching that survives typos, diacritics, punctuation, swapped words and changed years.
 *
 * Similarity is Jaro-Winkler over the normalised text and over its alphabetically sorted words,
 * whichever is higher. A match must be clearly the best one, so two similar names never pick a
 * style by accident.
 */
class InfoNameMatcher(private val tiles: List<FigmaTileStyle> = InfoFigmaStyles.TILES) {

    fun match(name: String): FigmaTileStyle? {
        val scored = tiles
            .map { tile -> tile to tile.names.maxOf { similarity(name, it) } }
            .sortedByDescending { it.second }
        val (best, bestScore) = scored.firstOrNull() ?: return null
        val runnerUp = scored.getOrNull(1)?.second ?: 0.0
        return best.takeIf { bestScore >= MIN_SIMILARITY && bestScore - runnerUp >= MIN_MARGIN }
    }

    companion object {
        const val MIN_SIMILARITY = 0.9
        const val MIN_MARGIN = 0.05

        fun similarity(a: String, b: String): Double {
            val wordsA = words(a)
            val wordsB = words(b)
            return maxOf(
                jaroWinkler(wordsA.joinToString(" "), wordsB.joinToString(" ")),
                jaroWinkler(wordsA.sorted().joinToString(" "), wordsB.sorted().joinToString(" "))
            )
        }

        /** Lower-case ASCII words without years, so "Problemy 2025-2026" equals "Problemy 2022-2023". */
        internal fun words(text: String): List<String> =
            Normalizer.normalize(text.lowercase().replace('ł', 'l'), Normalizer.Form.NFD)
                .filter { Character.getType(it) != Character.NON_SPACING_MARK.toInt() }
                .replace(Regex("[^a-z0-9 ]"), " ")
                .split(' ')
                .filter { it.isNotEmpty() && !YEAR.matches(it) }

        private val YEAR = Regex("\\d{4}")

        internal fun jaroWinkler(a: String, b: String): Double {
            if (a == b) return 1.0
            if (a.isEmpty() || b.isEmpty()) return 0.0
            val window = maxOf(a.length, b.length) / 2 - 1
            val matchedA = BooleanArray(a.length)
            val matchedB = BooleanArray(b.length)
            var matches = 0
            for (i in a.indices) {
                for (j in maxOf(0, i - window)..minOf(b.length - 1, i + window)) {
                    if (!matchedB[j] && a[i] == b[j]) {
                        matchedA[i] = true
                        matchedB[j] = true
                        matches++
                        break
                    }
                }
            }
            if (matches == 0) return 0.0
            var transpositions = 0
            var k = 0
            for (i in a.indices) {
                if (!matchedA[i]) continue
                while (!matchedB[k]) k++
                if (a[i] != b[k]) transpositions++
                k++
            }
            val m = matches.toDouble()
            val jaro = (m / a.length + m / b.length + (m - transpositions / 2.0) / m) / 3
            val prefix = a.zip(b).takeWhile { (x, y) -> x == y }.size.coerceAtMost(4)
            return jaro + prefix * 0.1 * (1 - jaro)
        }
    }
}
