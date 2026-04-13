package odyseja.odysejapka.chat

import odyseja.odysejapka.users.UserService
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

data class WsTicketInfo(
    val userId: String,
    val userName: String,
    val createdAt: Instant = Instant.now()
)

@Component
class WsTicketStore (
    private val userService: UserService
) {

    private val tickets = ConcurrentHashMap<String, WsTicketInfo>()

    fun createTicket(userId: String): WsTicketResponse {
        cleanup()
        val userName = userService.getUserEntityByUserId(userId).name ?: "Nieznany"
        val ticket = UUID.randomUUID().toString()
        tickets[ticket] = WsTicketInfo(userId, userName)
        return WsTicketResponse(ticket)
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
