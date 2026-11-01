package odyseja.odysejapka.info.content

import liquibase.change.custom.CustomTaskChange
import liquibase.database.Database
import liquibase.database.jvm.JdbcConnection
import liquibase.exception.ValidationErrors
import liquibase.resource.ResourceAccessor
import java.sql.Connection

/**
 * One-off Liquibase step (changeset 022): runs the text of every existing info through
 * [InfoContentSanitizer], dropping the colours, fonts and sizes pasted from word processors.
 * The data version is not bumped: it changes with every edit anyway before the apps need the new texts.
 */
class SanitizeInfoTextChange : CustomTaskChange {
    private var confirmation = "No info text needed sanitizing"

    override fun execute(database: Database) {
        val connection = (database.connection as JdbcConnection).underlyingConnection
        val updated = sanitizeAll(connection)
        if (updated > 0) confirmation = "Sanitized the text of $updated infos"
    }

    /** @return the number of infos whose text changed. */
    internal fun sanitizeAll(connection: Connection): Int {
        data class Row(val id: Int, val text: String)

        val rows = connection.createStatement().use { statement ->
            statement.executeQuery("SELECT id, info_text FROM info WHERE info_text IS NOT NULL").use { rs ->
                generateSequence { if (rs.next()) Row(rs.getInt(1), rs.getString(2)) else null }.toList()
            }
        }
        var updated = 0
        connection.prepareStatement("UPDATE info SET info_text = ? WHERE id = ?").use { update ->
            for (row in rows) {
                val sanitized = InfoContentSanitizer.sanitize(row.text)
                if (sanitized == row.text) continue
                update.setString(1, sanitized)
                update.setInt(2, row.id)
                update.executeUpdate()
                updated++
            }
        }
        return updated
    }

    override fun getConfirmationMessage() = confirmation

    override fun setUp() = Unit

    override fun setFileOpener(resourceAccessor: ResourceAccessor) = Unit

    override fun validate(database: Database) = ValidationErrors()
}
