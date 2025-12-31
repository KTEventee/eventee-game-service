package eventee.server.game.domain.service;


import eventee.server.common.exception.BaseException;
import eventee.server.common.exception.codes.ErrorCode;
import eventee.server.game.domain.dto.GameRuntimeState;
import eventee.server.game.domain.dto.RPS;
import eventee.server.game.domain.model.Game;
import eventee.server.game.domain.model.GameType;
import eventee.server.game.domain.repository.GameRepository;
import eventee.server.game.domain.repository.GameRuntimeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
@Service
@Slf4j
public class GameServiceImpl implements GameService{

    private final GameRepository gameRepository;
    private final GameRuntimeRepository runtimeRepository;
    private final GameEventPublisher gameEventPublisher;

    public void shooting(List<String> nicknames,long eventId){
        if(nicknames.isEmpty() || nicknames == null){
            //note 에러 발생
        }
        int rand = ThreadLocalRandom.current().nextInt(nicknames.size());
        String nickname = nicknames.get(rand);
        Game game = Game.builder()
                .round(0)
                .winner("m_"+nickname)
                .eventId(eventId)
                .gameType(GameType.CANNON).build();
        gameRepository.save(game);
    }

    public void startRPS(RPS.StartGameDto request){
        Game game = Game.builder()
                .round(0)
                .eventId(request.eventId())
                .winnerCnt(request.winnerCnt())
                .gameType(GameType.RPS).build();

        game = gameRepository.save(game);

        GameRuntimeState state = initState(game,request.players(),request.leader());

        runtimeRepository.save(state);
        gameEventPublisher.publishRoundStart(game.getGameId(), state);
    }

    public void playRPS(RPS.GamePlayDto request) {
        Game game = loadGameById(request.gameId());

        GameRuntimeState state =
                runtimeRepository.find(game.getGameId());

        if (state == null) {
            state = initState(game, request.players(),request.leader());
        }

        List<RPS.PlayerDto> survivors =
                calculateSurvivors(request);

        handleRoundResult(game, state, survivors);
    }

    private List<RPS.PlayerDto> calculateSurvivors(RPS.GamePlayDto request) {
        return request.players().stream()
                .filter(player ->
                        player.type().isWin(request.leader().type()))
                .toList();
    }

    private void handleRoundResult(
            Game game,
            GameRuntimeState state,
            List<RPS.PlayerDto> survivors
    ) {
        int alreadyConfirmed = state.getConfirmedWinners().size();
        int needed = game.getWinnerCnt() - alreadyConfirmed;

        if (survivors.isEmpty()) {
            handleNoSurvivor(game, state);
        }
        else if (survivors.size() < needed) {
            handleInsufficientWinners(game, state, survivors);
        }

        else if (survivors.size() == needed) {
            finishRPS(survivors,state,game);
        }
    }

    private void handleNoSurvivor(Game game, GameRuntimeState state) {
        GameRuntimeState prev =
                runtimeRepository.find(game.getGameId());

        int nextRound = prev == null ? 1 : prev.getRound() + 1;

        state.setRound(nextRound);
        state.setCurrentPlayers(state.getCurrentPlayers());
        state.setStatus(GameRuntimeState.Status.REMATCH);
        runtimeRepository.save(state);
        gameEventPublisher.publishRoundStart(game.getGameId(), state);
    }

    private void handleInsufficientWinners(
            Game game,
            GameRuntimeState state,
            List<RPS.PlayerDto> survivors
    ) {
        state.getConfirmedWinners().addAll(survivors);

        state.setRound(state.getRound() + 1);
        state.setCurrentPlayers(
                state.getCurrentPlayers().stream()
                        .filter(p -> !survivors.contains(p))
                        .toList()
        );

        state.setStatus(GameRuntimeState.Status.IN_PROGRESS);

        runtimeRepository.save(state);
        gameEventPublisher.publishRoundStart(game.getGameId(), state);
    }

    private void finishRPS(List<RPS.PlayerDto> survivors, GameRuntimeState state,Game game){
        String winner = "";
        for(RPS.PlayerDto p : survivors){
            winner += ("p_"+p.nickname());
        }
        game.setWinner(winner);
        gameRepository.save(game);

        runtimeRepository.delete(game.getGameId());
        gameEventPublisher.publishGameFinished(
                game.getGameId(),
                state.getConfirmedWinners()
        );
    }

    private Game loadGameById(Long gameId){
        return gameRepository.findGameByGameId(gameId).orElseThrow(
                () -> new BaseException(ErrorCode.BAD_REQUEST)
        );
    }

    private GameRuntimeState initState(
            Game game,
            List<RPS.PlayerDto> players,
            RPS.PlayerDto leader
    ) {
        GameRuntimeState state = GameRuntimeState.builder()
                .gameId(game.getGameId())
                .round(1)
                .confirmedWinners(new ArrayList<>())
                .currentPlayers(players)
                .leader(leader)
                .status(GameRuntimeState.Status.IN_PROGRESS)
                .build();

        runtimeRepository.save(state);
        return state;
    }

}
