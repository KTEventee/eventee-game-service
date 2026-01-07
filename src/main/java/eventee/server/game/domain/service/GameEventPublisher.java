package eventee.server.game.domain.service;

import eventee.server.game.domain.dto.GameRuntimeState;
import eventee.server.game.domain.dto.RPS;
import eventee.server.game.domain.dto.RoundEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GameEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void publishRSPResult(
            Long eventId,
            String nickname
    ){
        messagingTemplate.convertAndSend(
                "/sub/game/" + eventId + "/result",
                nickname
        );
    }

    public void publishRoundStart(
            Long eventId,
            GameRuntimeState state
    ) {
        messagingTemplate.convertAndSend(
                "/sub/game/" + eventId + "/start",
                RoundEvent.roundStart(eventId, state)
        );
    }

    public void publishGameFinished(
            Long eventId,
            GameRuntimeState state
    ) {
        messagingTemplate.convertAndSend(
                "/sub/game/" + eventId + "/round",
                RoundEvent.gameFinished(eventId, state)
        );
    }
}
