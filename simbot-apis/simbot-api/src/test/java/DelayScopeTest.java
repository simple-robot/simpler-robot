import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import love.forte.simbot.ability.DelayCompletionFutureStage;
import love.forte.simbot.ability.DelayableCoroutineScope;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.RepeatedTest;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author ForteScarlet
 */
public class DelayScopeTest {

    private final FooDelayableCoroutineScope scope = new FooDelayableCoroutineScope();


    @RepeatedTest(10)
    public void completableApiTest() throws ExecutionException, InterruptedException {
        final long t1 = ThreadLocalRandom.current().nextLong(500);
        final long t2 = ThreadLocalRandom.current().nextLong(500);
        final DelayCompletionFutureStage<Long> f1 = scope.delayAndCompute(t1, () -> t1);
        final DelayCompletionFutureStage<Long> f2 = scope.delayAndCompute(t2, () -> t2);

        final DelayCompletionFutureStage<Void> either = f1.acceptEitherAsync(f2, (v) -> {
            assert v == Math.min(t1, t2);
        });

        either.get();
    }

    private static class FooDelayableCoroutineScope implements DelayableCoroutineScope {
        @NotNull
        @Override
        public CoroutineContext getCoroutineContext() {
            return EmptyCoroutineContext.INSTANCE;
        }
    }
}



