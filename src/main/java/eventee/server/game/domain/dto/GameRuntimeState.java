package eventee.server.game.domain.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameRuntimeState {

    private Long gameId;
    private int round;
    private List<RPS.PlayerDto> confirmedWinners;
    private List<RPS.PlayerDto> currentPlayers;
    private RPS.PlayerDto leader;
    private Status status;

    public enum Status {
        IN_PROGRESS,
        REMATCH,
        FINISHED
    }
}