package eventee.server.game.domain.service;

import eventee.server.game.domain.dto.RPS;

import java.util.List;

public interface GameService {
    void shooting(List<String> nicknames,long eventId);
    void startRPS(RPS.StartGameDto request);
    void playRPS(RPS.GamePlayDto request);
}
