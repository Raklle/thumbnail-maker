package templates.messaging

import org.springframework.stereotype.Component
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import java.io.IOException

@Component
class TextHandler : TextWebSocketHandler() {

    public override fun handleTextMessage(session: WebSocketSession, message:
    TextMessage
    ) {
        session.sendMessage(TextMessage("Your message: "))
    }
}