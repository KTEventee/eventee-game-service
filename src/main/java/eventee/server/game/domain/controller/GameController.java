package eventee.server.game.domain.controller;


import eventee.server.common.exception.BaseResponse;
import eventee.server.game.domain.dto.RPS;
import eventee.server.game.domain.service.GameService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/game")
@Tag(name = "Comment", description = "댓글 API")
@Slf4j
public class GameController {

    //note 게임 뭐뭐하지
    private final GameService gameService;

    @GetMapping("/cannon")
    public BaseResponse<?> shooting(@RequestBody List<String> users,long eventId){
        gameService.shooting(users,eventId);
        return BaseResponse.onSuccess("success");
    }

    //가위바위보 전용
    @PostMapping("/{gameId}/start")
    public BaseResponse<?> startRPS(@PathVariable Long gameId,@RequestBody RPS.StartGameDto request){
        gameService.startRPS(request);
        return BaseResponse.onSuccess("success");
    }

    @PostMapping("/{gameId}/play")
    public BaseResponse<?> submitRPS(@PathVariable Long gameId,@RequestBody RPS.GamePlayDto request){
        gameService.playRPS(request);
        return BaseResponse.onSuccess("success");
    }

//    @GetMapping("/{gameId}/status")
//    public BaseResponse<?> gameStatus(@PathVariable Long gameId){
//
//    }

}
