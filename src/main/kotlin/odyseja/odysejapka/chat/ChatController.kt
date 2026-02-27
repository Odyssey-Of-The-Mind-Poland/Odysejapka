package odyseja.odysejapka.chat

import odyseja.odysejapka.dashboard.PerformanceAccessService
import odyseja.odysejapka.dashboard.extractUserId
import odyseja.odysejapka.users.UserRepository
import org.springframework.http.HttpStatus
import org.springframework.messaging.simp.SimpMessagingTemplate
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/v1/form/{performanceId}/chat")
class ChatController(
    private val chatService: ChatService,
    private val performanceAccessService: PerformanceAccessService,
    private val userRepository: UserRepository,
    private val wsTicketStore: WsTicketStore,
    private val messagingTemplate: SimpMessagingTemplate,
) {

    @GetMapping
    fun getMessages(
        @PathVariable performanceId: Int,
        @AuthenticationPrincipal principal: Any?,
    ): List<ChatMessageDto> {
        val userId = extractUserId(principal) ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        performanceAccessService.checkAccess(userId, performanceId)
        return chatService.getMessages(performanceId)
    }

    @PostMapping
    fun sendMessage(
        @PathVariable performanceId: Int,
        @RequestBody request: SendChatMessageRequest,
        @AuthenticationPrincipal principal: Any?,
    ): ChatMessageDto {
        val userId = extractUserId(principal) ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        performanceAccessService.checkAccess(userId, performanceId)

        if (request.message.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Message cannot be empty")
        }

        val user = userRepository.findByUserId(userId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        val message = chatService.sendMessage(
            performanceId = performanceId,
            userId = userId,
            userName = user.name ?: "Nieznany",
            message = request.message,
        )

        messagingTemplate.convertAndSend("/topic/chat.$performanceId", message)

        return message
    }

    @PostMapping("/ws-ticket")
    fun createWsTicket(
        @PathVariable performanceId: Int,
        @AuthenticationPrincipal principal: Any?,
    ): WsTicketResponse {
        val userId = extractUserId(principal) ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        performanceAccessService.checkAccess(userId, performanceId)

        val user = userRepository.findByUserId(userId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")

        val ticket = wsTicketStore.createTicket(userId, user.name ?: "Nieznany")
        return WsTicketResponse(ticket)
    }
}
