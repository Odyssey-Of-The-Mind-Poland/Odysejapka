package odyseja.odysejapka.chat

import odyseja.odysejapka.users.UserService
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class ChatService(
    private val chatMessageRepository: ChatMessageRepository,
    private val userService: UserService,
    private val messagingTemplate: SimpMessagingTemplate
) {

    fun getMessages(performanceId: Int): List<ChatMessage> {
        return chatMessageRepository
            .findAllByPerformanceIdOrderByCreatedAtAsc(performanceId)
            .map { it.toChatMessage() }
    }

    @Transactional
    fun saveMessage(
        performanceId: Int,
        userId: String,
        userName: String,
        message: String,
    ): ChatMessage {
        val entity = ChatMessageEntity().apply {
            this.performanceId = performanceId
            this.userId = userId
            this.userName = userName
            this.message = message.trim()
            this.createdAt = Instant.now()
        }
        return chatMessageRepository.save(entity).toChatMessage()
    }

    @Transactional
    fun sendMessage(message: String, performanceId: Int, userId: String): ChatMessage {
        if (message.isBlank()) {
            throw IllegalArgumentException("Wiadomość nie może być pusta")
        }

        val user = userService.getUserEntityByUserId(userId)
        val message = saveMessage(performanceId, userId, user.name ?: "Nieznany", message)
        messagingTemplate.convertAndSend("/topic/chat.$performanceId", message)

        return message
    }
}
