package compilamos.manana.partygame.game.event;

public record DomainEvent(
        DomainEventType type,
        EventMetadata metadata,
        Object payload
) {
}
