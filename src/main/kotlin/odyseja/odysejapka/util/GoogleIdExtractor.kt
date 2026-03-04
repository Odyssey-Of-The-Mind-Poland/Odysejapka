package odyseja.odysejapka.util

object GoogleIdExtractor {

    private val FOLDERS_PATTERN = Regex("""/folders/([a-zA-Z0-9_-]+)""")
    private val SPREADSHEET_D_PATTERN = Regex("""/spreadsheets/d/([a-zA-Z0-9_-]+)""")
    private val DOCS_D_PATTERN = Regex("""/d/([a-zA-Z0-9_-]+)(?:/|$|\?)""")
    private val FILE_D_PATTERN = Regex("""/file/d/([a-zA-Z0-9_-]+)""")

    /**
     * Extracts Google Drive/Sheets ID from URL or returns the input as-is if it's already an ID.
     * Supports:
     * - https://drive.google.com/drive/u/1/folders/ID
     * - https://drive.google.com/drive/folders/ID
     * - https://docs.google.com/spreadsheets/d/ID/edit?gid=...
     * - https://docs.google.com/document/d/ID/...
     * - Plain ID (alphanumeric with - and _)
     */
    fun extractGoogleId(input: String?): String {
        if (input.isNullOrBlank()) return ""
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return ""

        FOLDERS_PATTERN.find(trimmed)?.groupValues?.get(1)?.let { return it }
        SPREADSHEET_D_PATTERN.find(trimmed)?.groupValues?.get(1)?.let { return it }
        DOCS_D_PATTERN.find(trimmed)?.groupValues?.get(1)?.let { return it }
        FILE_D_PATTERN.find(trimmed)?.groupValues?.get(1)?.let { return it }

        return trimmed
    }
}
