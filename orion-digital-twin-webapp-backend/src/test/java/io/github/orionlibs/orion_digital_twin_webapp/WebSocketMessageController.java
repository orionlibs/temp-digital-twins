package io.github.orionlibs.orion_digital_twin_webapp;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketMessageController
{
    @MessageMapping("/topic/mqtt") //incoming messages sent to "/app/mqtt"
    @SendTo("/topic/mqtt") //responses sent to "/topic/mqtt" which will be received by the subscribers
    public String handleTestTopicMessage(String message)
    {
        return "Received: " + message;
    }
}
