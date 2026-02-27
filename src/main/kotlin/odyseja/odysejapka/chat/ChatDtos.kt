package odyseja.odysejapka.chat

import java.time.Instant

data class ChatMessageDto(
    val id: Long,
    val performanceId: Int,
    val userId: String,
    val userName: String,
    val message: String,
    val createdAt: Instant,
)

data class SendChatMessageRequest(
    val message: String,
)

data class WsTicketResponse(
    val ticket: String,
)
