package compilamos.manana.partygame.sse.lifecycle;

import compilamos.manana.partygame.sse.registry.SseRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class KeepAliveJob {

    private final SseRegistry sseRegistry;

    public KeepAliveJob(SseRegistry sseRegistry) {
        this.sseRegistry = sseRegistry;
    }

    /**
     * Frecuencia del job. Recomendación MVP: 10–15s.
     * El registry ya filtra por "idle >= 25s" para no spamear.
     */
    @Scheduled(fixedDelayString = "${sse.keepalive.jobDelayMs:1}")
    public void run() {
        sseRegistry.sendKeepAliveAll();
    }
}
