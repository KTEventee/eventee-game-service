package eventee.server.game.domain.repository;

import eventee.server.game.domain.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findGameByGameId(long id);
}
