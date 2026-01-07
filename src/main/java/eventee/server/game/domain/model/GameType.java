package eventee.server.game.domain.model;

import eventee.server.common.exception.BaseException;
import eventee.server.common.exception.codes.ErrorCode;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum GameType {
    CANNON("CANNON"),
    RPS("RPS");

    public String type;

    GameType(String type){
        this.type = type;
    }

    public static GameType from(String type) {
        return Arrays.stream(GameType.values())
                .filter(t -> t.getType().equalsIgnoreCase(type))
                .findFirst()
                .orElseThrow(() -> new BaseException(ErrorCode.POST_TYPE_NOT_VALID));
    }
}