package odyseja.odysejapka.chat

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class ChatService(
    private val chatMessageRepository: ChatMessageRepository,
) {

    fun getMessages(performanceId: Int): List<ChatMessageDto> {
        return chatMessageRepository
            .findAllByPerformanceIdOrderByCreatedAtAsc(performanceId)
            .map { it.toDto() }
    }

    @Transactional
    fun sendMessage(
        performanceId: Int,
        userId: String,
        userName: String,
        message: String,
    ): ChatMessageDto {
        val entity = ChatMessageEntity().apply {
            this.performanceId = performanceId
            this.userId = userId
            this.userName = userName
            this.message = message.trim()
            this.createdAt = Instant.now()
        }
        return chatMessageRepository.save(entity).toDto()
    }

    private fun ChatMessageEntity.toDto() = ChatMessageDto(
        id = id,
        performanceId = performanceId,
        userId = userId,
        userName = userName,
        message = message,
        createdAt = createdAt,
    )
}
