package compilamos.manana.partygame.sse.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.concurrent.atomic.AtomicLong;

@Getter
@Setter
@Data
public class EmitterRef {
    final SseEmitter emitter;;
    final AtomicLong lastSentAtMs = new AtomicLong(System.currentTimeMillis());

    public EmitterRef(SseEmitter emitter) {
        this.emitter = emitter;
    }
}
