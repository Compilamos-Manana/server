package compilamos.manana.partygame.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "game")
@Data
public class GameConfig {
    // Avatar configuration
    private AvatarConfig avatars;

    // Room configuration
    private RoomConfig room;

    // Player configuration
    private PlayerConfig player;

    public boolean isValidAvatarId(int avatarId) {
        return avatars != null && avatars.getValidIds() != null && avatars.getValidIds().contains(avatarId);
    }

    @Data
    public static class AvatarConfig {
        private List<Integer> validIds;
    }

    @Data
    public static class RoomConfig {
        private int maxPlayers = 8;
        private int minPlayers = 2;
        private int maxRounds = 10;
        private int minRounds = 1;
        private int timePerRoundSeconds = 300;
        private int timeToAnswerSeconds = 60;
        private int timeToVoteSeconds = 60;
    }

    @Data
    public static class PlayerConfig {
        private int maxAlcoholLevel = 100;
        private int minNameLength = 1;
        private int maxNameLength = 50;
    }
}
