import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import love.forte.simbot.ability.DelayableCoroutineScope;
import love.forte.simbot.ability.DelayableFuture;
import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author ForteScarlet
 */
public class DelayScopeTest {

    // @Test
    public void test() throws ExecutionException, InterruptedException {
        final FooDelayableCoroutineScope scope = new FooDelayableCoroutineScope();
        final DelayableFuture<LocalTime> whole = scope.delay(5, TimeUnit.SECONDS, () -> {
            System.out.println(LocalTime.now());
        }).delay(5, TimeUnit.SECONDS, () -> {
            System.out.println(LocalTime.now());
        }).delayAndCompute(5, TimeUnit.SECONDS, (v) -> LocalTime.now()).map(t -> t.plusSeconds(5));

        System.out.println("whole.get() = " + whole.get());
    }

}


class FooDelayableCoroutineScope implements DelayableCoroutineScope {
    @NotNull
    @Override
    public CoroutineContext getCoroutineContext() {
        return EmptyCoroutineContext.INSTANCE;
    }
}
