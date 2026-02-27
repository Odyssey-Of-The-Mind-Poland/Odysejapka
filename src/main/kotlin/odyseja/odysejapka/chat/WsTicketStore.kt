package odyseja.odysejapka.chat

import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

data class WsTicketInfo(
    val userId: String,
    val userName: String,
    val createdAt: Instant = Instant.now(),
)

@Component
class WsTicketStore {

    private val tickets = ConcurrentHashMap<String, WsTicketInfo>()

    fun createTicket(userId: String, userName: String): String {
        cleanup()
        val ticket = UUID.randomUUID().toString()
        tickets[ticket] = WsTicketInfo(userId, userName)
        return ticket
    }

    fun consumeTicket(ticket: String): WsTicketInfo? {
        cleanup()
        return tickets.remove(ticket)
    }

    private fun cleanup() {
        val cutoff = Instant.now().minusSeconds(TICKET_TTL_SECONDS)
        tickets.entries.removeIf { it.value.createdAt.isBefore(cutoff) }
    }

    companion object {
        private const val TICKET_TTL_SECONDS = 60L
    }
}
