package odyseja.odysejapka.chat

import org.springframework.data.repository.CrudRepository

interface ChatMessageRepository : CrudRepository<ChatMessageEntity, Long> {

    fun findAllByPerformanceIdOrderByCreatedAtAsc(performanceId: Int): List<ChatMessageEntity>
}
