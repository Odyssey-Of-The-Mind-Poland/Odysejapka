package odyseja.odysejapka.chat

import odyseja.odysejapka.dashboard.PerformanceAccessService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/form/{performanceId}/chat")
class ChatController(
    private val chatService: ChatService,
    private val wsTicketStore: WsTicketStore,
    private val performanceAccessService: PerformanceAccessService
) {

    @GetMapping
    fun getMessages(
        @PathVariable performanceId: Int,
        @AuthenticationPrincipal principal: Any?,
    ): List<ChatMessage> {
        performanceAccessService.checkAccessByPrincipal(performanceId, principal)
        return chatService.getMessages(performanceId)
    }

    @PostMapping
    fun sendMessage(
        @PathVariable performanceId: Int,
        @RequestBody request: SendChatMessageRequest,
        @AuthenticationPrincipal principal: Any?,
    ): ChatMessage {
        val userId = performanceAccessService.checkAccessByPrincipal(performanceId, principal)
        return chatService.sendMessage(request.message, performanceId, userId)
    }

    @PostMapping("/ws-ticket")
    fun createWsTicket(
        @PathVariable performanceId: Int,
        @AuthenticationPrincipal principal: Any?,
    ): WsTicketResponse {
        val userId = performanceAccessService.checkAccessByPrincipal(performanceId, principal)
        return wsTicketStore.createTicket(userId)
    }
}
