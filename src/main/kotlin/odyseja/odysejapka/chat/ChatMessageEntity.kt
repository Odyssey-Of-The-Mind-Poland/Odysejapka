package odyseja.odysejapka.chat

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "chat_messages")
class ChatMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0

    @Column(name = "performance_id", nullable = false)
    var performanceId: Int = 0

    @Column(name = "user_id", nullable = false)
    var userId: String = ""

    @Column(name = "user_name", nullable = false)
    var userName: String = ""

    @Column(nullable = false, columnDefinition = "TEXT")
    var message: String = ""

    @Column(name = "created_at", nullable = false)
    var createdAt: Instant = Instant.now()
}
