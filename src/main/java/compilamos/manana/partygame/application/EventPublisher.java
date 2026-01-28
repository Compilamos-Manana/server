package compilamos.manana.partygame.application;

import compilamos.manana.partygame.game.event.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventPublisher {

    private final ApplicationEventPublisher delegate;

    public EventPublisher(ApplicationEventPublisher delegate) {
        this.delegate = delegate;
    }

    public void publish(DomainEvent event) {
        if (event == null) return;
        delegate.publishEvent(event);
    }

    public void publishAll(List<DomainEvent> events) {
        if (events == null || events.isEmpty()) return;
        for (DomainEvent event : events) {
            publish(event);
        }
    }
}