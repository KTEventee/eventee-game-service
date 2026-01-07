package eventee.server.game.domain.controller;


import eventee.server.common.exception.BaseResponse;
import eventee.server.common.jwt.exception.JwtErrorCode;
import eventee.server.common.jwt.exception.JwtHandler;
import eventee.server.game.domain.dto.RPS;
import eventee.server.game.domain.service.GameService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Comment", description = "댓글 API")
@Slf4j
public class GameController {

    //note 게임 뭐뭐하지
    private final GameService gameService;

    @GetMapping("/{eventId}/cannon")
    public BaseResponse<?> shooting(
            HttpServletRequest request,
            @RequestBody List<String> users,
            Authentication authentication,
            @PathVariable long eventId){
        Long memberId = (Long) authentication.getPrincipal();
        if (memberId == null) {
            throw new JwtHandler(JwtErrorCode.JWT_MISSING_TOKEN);
        }
        gameService.shooting(users,eventId);
        return BaseResponse.onSuccess("success");
    }

    //가위바위보 전용
    @PostMapping("/rsp/start")
    public BaseResponse<?> startRPS(
            HttpServletRequest request,
            Authentication authentication,
            @RequestBody RPS.StartGameDto requestDto){
        Long memberId = (Long) authentication.getPrincipal();
        if (memberId == null) {
            throw new JwtHandler(JwtErrorCode.JWT_MISSING_TOKEN);
        }
        gameService.startRPS(requestDto);
        return BaseResponse.onSuccess("success");
    }

    @PostMapping("/rsp/play")
    public BaseResponse<?> submitRPS(
            HttpServletRequest request,
            Authentication authentication,
            @RequestBody RPS.GamePlayDto requestDto){
        Long memberId = (Long) authentication.getPrincipal();
        if (memberId == null) {
            throw new JwtHandler(JwtErrorCode.JWT_MISSING_TOKEN);
        }
        gameService.playRPS(requestDto);
        return BaseResponse.onSuccess("success");
    }

//    @GetMapping("/{gameId}/status")
//    public BaseResponse<?> gameStatus(@PathVariable Long gameId){
//
//    }

}
