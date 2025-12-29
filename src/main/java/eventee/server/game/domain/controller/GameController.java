package eventee.server.game.domain.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/game")
@Tag(name = "Comment", description = "댓글 API")
@Slf4j
public class GameController {

    //note 게임 뭐뭐하지

}
