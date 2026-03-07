package odyseja.odysejapka.chat

import org.springframework.context.annotation.Configuration
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer
import java.security.Principal

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(
    private val wsTicketStore: WsTicketStore,
) : WebSocketMessageBrokerConfigurer {

    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/topic")
        config.setApplicationDestinationPrefixes("/app")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws")
            .setAllowedOriginPatterns(
                "http://localhost:5172",
                "http://localhost:5173",
                "http://grzybek.snet.ovh:3000",
                "https://grzybek.snet.ovh",
                "https://panel.odyseja.org",
                "https://app.odyseja.org"
            )
            .withSockJS()
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(object : ChannelInterceptor {
            override fun preSend(message: Message<*>, channel: MessageChannel): Message<*> {
                val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)
                if (accessor != null && StompCommand.CONNECT == accessor.command) {
                    val ticket = accessor.getFirstNativeHeader("ticket")
                    if (ticket != null) {
                        val ticketInfo = wsTicketStore.consumeTicket(ticket)
                        if (ticketInfo != null) {
                            accessor.user = ChatPrincipal(ticketInfo.userId, ticketInfo.userName)
                        }
                    }
                }
                return message
            }
        })
    }
}

data class ChatPrincipal(
    val userId: String,
    val userName: String,
) : Principal {
    override fun getName(): String = userId
}
