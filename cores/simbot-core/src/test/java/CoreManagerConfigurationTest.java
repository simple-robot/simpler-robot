import love.forte.simbot.Identifies;
import love.forte.simbot.PriorityConstant;
import love.forte.simbot.core.event.EventInterceptorsGenerator;

/**
 * @author ForteScarlet
 */
@SuppressWarnings("Convert2MethodRef")
public class CoreManagerConfigurationTest {

    public void run(EventInterceptorsGenerator generator) {
        generator
                .listenerIntercept(Identifies.randomID(), PriorityConstant.FIRST, context -> {
                    // do...

                    return context.proceedBlocking();
                })
                .listenerIntercept(Identifies.randomID(), PriorityConstant.FIRST, context -> {
                    // do...

                    return context.proceedBlocking();
                })
                .end() // back to config
                // ...
                ;

    }

}
