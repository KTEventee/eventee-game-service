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

    public void publishRoundStart(
            Long gameId,
            GameRuntimeState state
    ) {
        messagingTemplate.convertAndSend(
                "/sub/game/" + gameId + "/round",
                RoundEvent.roundStart(gameId, state)
        );
    }

    public void publishGameFinished(
            Long gameId,
            List<RPS.PlayerDto> winners
    ) {
        messagingTemplate.convertAndSend(
                "/sub/game/" + gameId + "/round",
                RoundEvent.gameFinished(gameId, winners)
        );
    }
}
