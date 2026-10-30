package odyseja.odysejapka.info.figma

import liquibase.change.custom.CustomTaskChange
import liquibase.database.Database
import liquibase.database.jvm.JdbcConnection
import liquibase.exception.ValidationErrors
import liquibase.resource.ResourceAccessor
import java.sql.Connection
import java.sql.Timestamp

/**
 * One-off Liquibase step (changeset 020) that gives the existing info tiles the icon and colour of
 * the Figma redesign. Tiles that already have their own icon or colour keep it, so manual edits
 * are never overwritten. When anything changed the data version is bumped, so apps download it.
 */
class BackfillInfoStyleChange : CustomTaskChange {
    private var confirmation = "No info tiles needed a style"

    override fun execute(database: Database) {
        val connection = (database.connection as JdbcConnection).underlyingConnection
        val updated = backfill(connection, InfoNameMatcher())
        if (updated > 0) {
            bumpVersion(connection)
            confirmation = "Styled $updated info tiles after the Figma design"
        }
    }

    /** @return the number of info rows that got an icon or a colour. */
    internal fun backfill(connection: Connection, matcher: InfoNameMatcher): Int {
        data class Row(val id: Int, val name: String, val icon: String?, val color: String?)

        val rows = connection.createStatement().use { statement ->
            statement.executeQuery("SELECT id, info_name, icon, color FROM info").use { rs ->
                generateSequence { if (rs.next()) Row(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)) else null }.toList()
            }
        }
        var updated = 0
        connection.prepareStatement("UPDATE info SET icon = ?, color = ? WHERE id = ?").use { update ->
            for (row in rows) {
                if (row.icon != null && row.color != null) continue
                val style = matcher.match(row.name) ?: continue
                update.setString(1, row.icon ?: style.icon)
                update.setString(2, row.color ?: style.color)
                update.setInt(3, row.id)
                update.executeUpdate()
                updated++
            }
        }
        return updated
    }

    /**
     * Same effect as ChangeService.updateVersion(). Hibernate hands out `change` ids in blocks from a
     * sequence, so the id is taken from that sequence too; a hand-made MAX(id) + 1 would collide with
     * a block the running app has already reserved.
     */
    private fun bumpVersion(connection: Connection) {
        val id = nextFromSequence(connection)
        connection.prepareStatement("INSERT INTO change (id, changed_at) VALUES (COALESCE(?, (SELECT COALESCE(MAX(id), 0) + 1 FROM change)), ?)").use {
            if (id == null) it.setNull(1, java.sql.Types.INTEGER) else it.setInt(1, id)
            it.setTimestamp(2, Timestamp(System.currentTimeMillis()))
            it.executeUpdate()
        }
    }

    private fun nextFromSequence(connection: Connection): Int? {
        if (!connection.metaData.databaseProductName.equals("PostgreSQL", ignoreCase = true)) return null
        val sequence = connection.createStatement().use { statement ->
            statement.executeQuery(
                "SELECT relname FROM pg_class WHERE relkind = 'S' AND relname IN ('change_seq', 'change_id_seq', 'changeentity_seq', 'change_entity_seq') ORDER BY relname LIMIT 1"
            ).use { rs -> if (rs.next()) rs.getString(1) else null }
        } ?: return null
        return connection.createStatement().use { statement ->
            statement.executeQuery("SELECT nextval('$sequence')").use { rs -> if (rs.next()) rs.getInt(1) else null }
        }
    }

    override fun getConfirmationMessage() = confirmation

    override fun setUp() = Unit

    override fun setFileOpener(resourceAccessor: ResourceAccessor) = Unit

    override fun validate(database: Database) = ValidationErrors()
}
