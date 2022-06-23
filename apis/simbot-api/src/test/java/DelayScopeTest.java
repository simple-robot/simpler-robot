import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import love.forte.simbot.Bot;
import love.forte.simbot.ability.DelayableCompletableFuture;
import love.forte.simbot.ability.DelayableCoroutineScope;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.Test;

import java.time.LocalTime;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author ForteScarlet
 */
public class DelayScopeTest {

    @Test
    public void test() throws ExecutionException, InterruptedException {
        final FooDelayableCoroutineScope scope = new FooDelayableCoroutineScope();
        final DelayableCompletableFuture<LocalTime> whole = scope.delay(5, TimeUnit.SECONDS, () -> {
            System.out.println(LocalTime.now());
        }).delay(5, TimeUnit.SECONDS, () -> {
            throw new IllegalStateException();
            // System.out.println(LocalTime.now());
        }).delay(5, TimeUnit.SECONDS, () -> {
            System.out.println(LocalTime.now());
        }).delay(5, TimeUnit.SECONDS, () -> {
            System.out.println(LocalTime.now());
        }).delay(5, TimeUnit.SECONDS, () -> {
            System.out.println(LocalTime.now());
        }).delay(5, TimeUnit.SECONDS, () -> {
            System.out.println(LocalTime.now());
        }).delay(5, TimeUnit.SECONDS, () -> {
            System.out.println(LocalTime.now());
        }).delay(5, TimeUnit.SECONDS, () -> {
            System.out.println(LocalTime.now());
        }).delay(5, TimeUnit.SECONDS, () -> {
            System.out.println(LocalTime.now());
        }).delay(5, TimeUnit.SECONDS, () -> {
            System.out.println(LocalTime.now());
        }).delay(5, TimeUnit.SECONDS, () -> {
            System.out.println(LocalTime.now());
        }).delay(5, TimeUnit.SECONDS, () -> {
            System.out.println(LocalTime.now());
        }).delay(5, TimeUnit.SECONDS, () -> {
            System.out.println(LocalTime.now());
        }).delayAndCompute(5, TimeUnit.SECONDS, (v) -> LocalTime.now()); //.map(t -> t.plusSeconds(5));

        Thread.sleep(TimeUnit.SECONDS.toMillis(10));
        System.out.println("wake up!");
        System.out.println("whole.get() = " + whole.get());
    }

    // @Test
    public void foo(Bot bot) {
        final DelayableCompletableFuture<LocalTime> whole = bot
                // (1). 延时5秒，打印当前时间
                .delay(5, TimeUnit.SECONDS, () -> {
                    System.out.println(LocalTime.now());
                })
                // (2). 流程(1)结束后，再延时5秒，打印当前时间
                .delay(5, TimeUnit.SECONDS, () -> {
                    throw new IllegalStateException();
                    // System.out.println(LocalTime.now());
                })
                // (3). 流程(2)结束后，再延时5秒，返回当前时间
                .delayAndCompute(5, TimeUnit.SECONDS, (v) -> LocalTime.now());

    }

}


class FooDelayableCoroutineScope implements DelayableCoroutineScope {
    @NotNull
    @Override
    public CoroutineContext getCoroutineContext() {
        return EmptyCoroutineContext.INSTANCE;
    }
}
