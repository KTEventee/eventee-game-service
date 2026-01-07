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
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

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
        gameEventPublisher.publishRSPResult(eventId,nickname);
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
        gameEventPublisher.publishRoundStart(game.getEventId(), state);
    }

    public void playRPS(RPS.GamePlayDto request) {
        Game game = loadGameById(request.gameId());

        GameRuntimeState beforeGame =
                runtimeRepository.find(game.getGameId());

        if (beforeGame == null) {
            log.info(" state 발견 모샇ㅁ");
            beforeGame = initState(game, request.players(),request.leader());
        }else{
            log.info("state 발견함.");
        }

        List<RPS.PlayerDto> survivors =
                calculateSurvivors(request,beforeGame.getCurrentPlayers());

        log.info("승리자 복록");
        for(RPS.PlayerDto player : survivors){
            log.info(player.nickname());
        }

        handleRoundResult(game, beforeGame, survivors);
    }

    private List<RPS.PlayerDto> calculateSurvivors(RPS.GamePlayDto request,List<RPS.PlayerDto> players) {

        Set<Long> allowedIds = players.stream()
                .map(RPS.PlayerDto::memberId)
                .collect(Collectors.toSet());

        return request.players().stream()
                .filter(player ->
                        (allowedIds.contains(player.memberId()))&&
                        (player.type().isWin(request.leader().type())))
                .toList();
    }

    private void handleRoundResult(
            Game game,
            GameRuntimeState beforeGame,
            List<RPS.PlayerDto> survivors
    ) {
        int alreadyConfirmed = beforeGame.getConfirmedWinners().size();
        int needed = game.getWinnerCnt() - alreadyConfirmed;
        log.info("alreadyConfirmed:"+alreadyConfirmed+" needed:"+needed+" 이긴 사람 수 :"+survivors.size());

        if (survivors.size() == needed) {
            finishRPS(survivors, beforeGame, game);
        }else if(survivors.size() < needed){
            handleInsufficientWinners(game, beforeGame, survivors);
        }else{
            handleNoSurvivor(game,beforeGame);
        }


    }

    private void handleNoSurvivor(Game game, GameRuntimeState beforeGame) {

        beforeGame.setRound(beforeGame.getRound() + 1);
        beforeGame.setStatus(GameRuntimeState.Status.REMATCH);

        runtimeRepository.save(beforeGame);
        gameEventPublisher.publishRoundStart(game.getEventId(), beforeGame);
    }

    private void handleInsufficientWinners(
            Game game,
            GameRuntimeState beforeGame,
            List<RPS.PlayerDto> survivors
    ) {

        Set<Long> survivorIds = survivors.stream()
                .map(RPS.PlayerDto::memberId)
                .collect(Collectors.toSet());

        List<RPS.PlayerDto> updatedWinners =
                new ArrayList<>(beforeGame.getConfirmedWinners());
        updatedWinners.addAll(survivors);
        beforeGame.setConfirmedWinners(updatedWinners);

        beforeGame.setRound(beforeGame.getRound() + 1);
        beforeGame.setCurrentPlayers(
                beforeGame.getCurrentPlayers().stream()
                        .filter(p -> !survivorIds.contains(p.memberId()))
                        .toList()
        );

        beforeGame.setStatus(GameRuntimeState.Status.IN_PROGRESS);
        log.info("beforeGame:"+beforeGame.toString());
        runtimeRepository.save(beforeGame);
        gameEventPublisher.publishRoundStart(game.getEventId(), beforeGame);
    }

    private void finishRPS(List<RPS.PlayerDto> survivors, GameRuntimeState beforeGame,Game game){
        String winner = "";
        for(RPS.PlayerDto p : survivors){
            winner += ("p_"+p.nickname()+",");
        }
        game.setWinner(winner);
        log.info("winner:"+winner);
        gameRepository.save(game);

        beforeGame.setStatus(GameRuntimeState.Status.FINISHED);
        beforeGame.setRound(beforeGame.getRound() + 1);

        List<RPS.PlayerDto> updatedWinners =
                new ArrayList<>(beforeGame.getConfirmedWinners());
        updatedWinners.addAll(survivors);
        beforeGame.setConfirmedWinners(updatedWinners);

        runtimeRepository.delete(game.getGameId());
        gameEventPublisher.publishGameFinished(
                game.getEventId(),
                beforeGame
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
                .round(0)
                .confirmedWinners(new ArrayList<>())
                .currentPlayers(players)
                .leader(leader)
                .status(GameRuntimeState.Status.IN_PROGRESS)
                .build();

        runtimeRepository.save(state);
        return state;
    }

}
