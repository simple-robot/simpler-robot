import love.forte.simbot.core.event.CoreListenerUtil;
import love.forte.simbot.event.EventListener;
import love.forte.simbot.event.MessageEvent;

/**
 * @author ForteScarlet
 */
public class JEventMain {
    public static void main(String[] args) {
        final EventListener eventListener = CoreListenerUtil.newCoreListener(MessageEvent.class, (c, e) -> {
            return 1;
        });


    }
}
