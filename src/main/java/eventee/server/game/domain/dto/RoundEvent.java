package eventee.server.game.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RoundEvent {

    private String type; // ROUND_START, ROUND_RESULT, GAME_FINISHED
    private Long gameId;
    private Long eventId;
    private int round;
    private List<String> currentPlayers;
    private List<String> confirmedWinners;

    public static RoundEvent roundStart(
            Long eventId,
            GameRuntimeState state
    ) {
        return new RoundEvent(
                state.getStatus().toString(),
                state.getGameId(),
                eventId,
                state.getRound(),
                state.getCurrentPlayers()
                        .stream()
                        .map(RPS.PlayerDto::nickname)
                        .toList(),
                state.getConfirmedWinners()
                        .stream()
                        .map(RPS.PlayerDto::nickname)
                        .toList()
        );
    }

    public static RoundEvent gameFinished(
            Long eventId,
            GameRuntimeState state
    ) {
        return new RoundEvent(
                state.getStatus().toString(),
                state.getGameId(),
                eventId,
                state.getRound(),
                List.of(),
                state.getConfirmedWinners().stream()
                        .map(RPS.PlayerDto::nickname)
                        .toList()
        );
    }
}