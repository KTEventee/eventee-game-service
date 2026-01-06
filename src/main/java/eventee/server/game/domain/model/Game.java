package eventee.server.game.domain.model;

import eventee.server.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name = "game")
@SQLDelete(sql = "UPDATE game SET is_deleted = true, deleted_at = now() where comment_id = ?")
@SQLRestriction("is_deleted is FALSE")
public class Game extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Long gameId;

    @Enumerated(EnumType.STRING)
    private GameType gameType;

    private Long eventId;

    //note group일경우 g_조이름, 사람이면 m_멤버이름
    private String winner;

    private int winnerCnt;

    private int round;

    public boolean isMember(){
        return winner.split("_")[0].equals("m");
    }

    @Builder
    public Game(GameType gameType, Long eventId, String winner, int round,int winnerCnt) {
        this.gameType = gameType;
        this.eventId = eventId;
        this.winner = winner;
        this.round = round;
        this.winnerCnt = winnerCnt;
    }
    public void setWinner(String winner){
        this.winner = winner;
    }

}
