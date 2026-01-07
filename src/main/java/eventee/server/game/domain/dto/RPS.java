package eventee.server.game.domain.dto;

import eventee.server.common.exception.BaseException;
import eventee.server.common.exception.codes.ErrorCode;
import eventee.server.game.domain.model.GameType;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.List;

public class RPS{

    public record PlayerDto(
            long memberId,
            String nickname,
            RPSType type
    ){}

    public enum RPSType {
        ROCK("ROCK","SCISSOR"),
        PAPER("PAPER","ROCK"),
        SCISSOR("SCISSOR","PAPER");

        public String type;
        public String winTargets;

        RPSType(String type,String winTargets){
            this.type = type;
            this.winTargets = winTargets;
        }

        public String getType(){
            return this.type;
        }

        public boolean isWin(RPSType type){
            return this.winTargets.equals(type.type);
        }

        public static RPSType from(String type) {
            return Arrays.stream(RPSType.values())
                    .filter(t -> t.getType().equalsIgnoreCase(type))
                    .findFirst()
                    .orElseThrow(() -> new BaseException(ErrorCode.POST_TYPE_NOT_VALID));
        }
    }

    public record StartGameDto(
            long eventId,
            int winnerCnt,
            PlayerDto leader,
            List<PlayerDto> players
    ){}

    public record GamePlayDto(
            PlayerDto leader,
            List<PlayerDto> players,
            long gameId,
            long eventId
    ){}

}
