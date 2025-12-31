package eventee.server.game.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RoundEvent {

    private String type; // ROUND_START, ROUND_RESULT, GAME_FINISHED
    private Long gameId;
    private int round;
    private List<String> currentPlayers;
    private List<String> confirmedWinners;

    public static RoundEvent roundStart(
            Long gameId,
            GameRuntimeState state
    ) {
        return new RoundEvent(
                "ROUND_START",
                gameId,
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
            Long gameId,
            List<RPS.PlayerDto> winners
    ) {
        return new RoundEvent(
                "GAME_FINISHED",
                gameId,
                -1,
                List.of(),
                winners.stream()
                        .map(RPS.PlayerDto::nickname)
                        .toList()
        );
    }
}