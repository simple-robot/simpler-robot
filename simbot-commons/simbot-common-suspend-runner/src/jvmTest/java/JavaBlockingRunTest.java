/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

import love.forte.simbot.suspendrunner.SuspendFoo;
import love.forte.simbot.suspendrunner.reserve.SuspendReserves;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author ForteScarlet
 */
public class JavaBlockingRunTest {

    private static Scheduler parallelScheduler;

    @BeforeAll
    public static void initScheduler() {
        parallelScheduler = Schedulers.newParallel("parallel", 4);
    }

    @AfterAll
    public static void disposeScheduler() {
        parallelScheduler.dispose();
    }

    private static final String EXPECT_NAME = "forte";

    private void checkDuration(long startNano) {
        final var now = System.nanoTime();
        Assertions.assertTrue((now - startNano) > TimeUnit.MILLISECONDS.toNanos(100));
    }

    private void checkDuration(long startNano, long expectDurationNano) {
        final var now = System.nanoTime();
        Assertions.assertTrue((now - startNano) > expectDurationNano);
    }

    @Test
    public void blockingRun() {
        final var start = System.nanoTime();
        final var foo = new SuspendFoo();
        final var name = foo.runReserve(EXPECT_NAME).transform(SuspendReserves.block());
        Assertions.assertEquals(name, EXPECT_NAME);
        checkDuration(start);
    }

    @Test
    public void asyncRun() {
        final var start = System.nanoTime();
        final var foo = new SuspendFoo();
        final var nameFuture = foo.runReserve(EXPECT_NAME).transform(SuspendReserves.async());
        final var name = nameFuture.join();

        Assertions.assertEquals(name, EXPECT_NAME);
        checkDuration(start);
    }

    @Test
    public void monoRun() {
        final var start = System.nanoTime();
        final var foo = new SuspendFoo();
        final var nameMono = foo.runReserve(EXPECT_NAME).transform(SuspendReserves.mono());
        final var duration = Duration.ofSeconds(5);
        final var name = nameMono.block(duration);
        // .subscribeOn(parallelScheduler)

        Assertions.assertEquals(name, EXPECT_NAME);
        checkDuration(start, duration.toNanos());
    }

    @Test
    public void rx2Run() {
        final var start = System.nanoTime();
        final var foo = new SuspendFoo();
        final var nameMaybe = foo.runReserve(EXPECT_NAME).transform(SuspendReserves.rx2Maybe());
        final var name = nameMaybe.blockingGet();

        Assertions.assertEquals(name, EXPECT_NAME);
        checkDuration(start);
    }

    @Test
    public void rx3Run() {
        final var start = System.nanoTime();
        final var foo = new SuspendFoo();
        final var nameMaybe = foo.runReserve(EXPECT_NAME).transform(SuspendReserves.rx3Maybe());
        final var name = nameMaybe.blockingGet();

        Assertions.assertEquals(name, EXPECT_NAME);
        checkDuration(start);
    }

}
