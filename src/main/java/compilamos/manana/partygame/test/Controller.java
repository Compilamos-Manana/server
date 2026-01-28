package compilamos.manana.partygame.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@Slf4j
public class Controller {
    private final List<SseEmitter> emitters = new ArrayList<>();

    @GetMapping("/hello")
    public String hello() {
        log.info("Hello World");
        emitters.forEach(e -> {
            try {
                e.send(SseEmitter.event()
                        .name("HOLAAAAAA")
                        .data("HOLAAAAA  " +  " @ " + Instant.now()));
            } catch (IOException ex) {
                log.info("Error sending data", ex);
                // Si el cliente cortó la conexión, esto suele fallar acá.
                e.completeWithError(ex);
            }
        });
        return "Hello, World!";
    }

    @GetMapping(path = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream() {
        // 0L = sin timeout (ojo: depende del server/proxy; igual puede cortar por idle)
        SseEmitter emitter = new SseEmitter(0L);
        emitters.add(emitter);

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        AtomicLong seq = new AtomicLong(0);

        Runnable task = () -> {
            try {
                long n = seq.incrementAndGet();
                emitter.send(
                        SseEmitter.event()
                                .id(Long.toString(n))
                                .name("tick")
                                .data("tick " + n + " @ " + Instant.now())
                );
            } catch (IOException ex) {
                log.info("Error sending data", ex);
                // Si el cliente cortó la conexión, esto suele fallar acá.
                emitter.completeWithError(ex);
            } catch (Exception ex) {
                emitter.completeWithError(ex);
            }
        };

        scheduler.scheduleAtFixedRate(task, 0, 5, TimeUnit.SECONDS);

        // Limpieza cuando el cliente se desconecta o el server completa la respuesta
        emitter.onCompletion(scheduler::shutdown);
        emitter.onTimeout(() -> {
            log.info("Timeout");
            scheduler.shutdown();
            emitter.complete();
        });
        emitter.onError(e -> scheduler.shutdown());
        return emitter;
    }
}
