package eventee.server.game.domain.repository;

import eventee.server.game.domain.dto.GameRuntimeState;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class GameRuntimeRepository {

    private static final Duration TTL = Duration.ofMinutes(30);

    private final RedisTemplate<String, GameRuntimeState> redisTemplate;

    private String key(Long gameId) {
        return "game:" + gameId + ":state";
    }

    public void save(GameRuntimeState state) {
        redisTemplate.opsForValue()
                .set(key(state.getGameId()), state, TTL);
    }

    public GameRuntimeState find(Long gameId) {
        return redisTemplate.opsForValue().get(key(gameId));
    }

    public void delete(Long gameId) {
        redisTemplate.delete(key(gameId));
    }
}