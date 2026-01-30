package compilamos.manana.partygame.sse.routing;

import compilamos.manana.partygame.game.event.DomainEvent;
import org.springframework.stereotype.Component;

@Component
public class EventRouter {
    public enum Audience {
        HOST,
        PLAYERS,
        SPECIFIC_PLAYER,
        ALL
    }

    public Audience route(DomainEvent event) {
        return switch (event.type()) {
            case PLAYER_JOINED, PLAYER_DISCONNECTED, PLAYER_LEFT, PLAYER_CONNECTED, HOST_SNAPSHOT ->  Audience.HOST;
            case HOST_CONNECTED, HOST_DISCONNECTED, FRONTEND_CUSTOM_EVENT ->  Audience.PLAYERS;
            case ROLES_ASIGNADOS -> Audience.SPECIFIC_PLAYER;
            default -> Audience.ALL;
        };
    }
}
