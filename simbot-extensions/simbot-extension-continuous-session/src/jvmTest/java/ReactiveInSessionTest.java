/*
 *     Copyright (c) 2026. ForteScarlet.
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

import kotlinx.coroutines.Dispatchers;
import love.forte.simbot.extension.continuous.session.ContinuousSessionContexts;
import love.forte.simbot.extension.continuous.session.InSessions;
import love.forte.simbot.extension.continuous.session.UnitContinuousSessionKey;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 验证 Java 响应式处理器与持续会话之间的完成和取消传播。
 */
class ReactiveInSessionTest {
    /**
     * 发布者尚未完成时会话保持运行。发布者正常完成后，会话回调收到空的结束原因。
     */
    @Test
    void publisherCompletionCompletesSession() throws Exception {
        var context = ContinuousSessionContexts.<Integer, String>createContinuousSessionContext(Dispatchers.getDefault());
        var publisherCompletion = new CompletableFuture<Void>();
        var session = context.session(new UnitContinuousSessionKey(),
                InSessions.reactive(receiver -> Mono.fromFuture(publisherCompletion)));

        try {
            var sessionCompletion = new CompletableFuture<Throwable>();
            session.onCompletion(sessionCompletion::complete);

            assertFalse(sessionCompletion.isDone());
            publisherCompletion.complete(null);
            assertNull(sessionCompletion.get(5, TimeUnit.SECONDS));
        } finally {
            session.cancel();
        }
    }

    /**
     * 取消使用无限期发布者的会话时，已建立的订阅也会被取消。
     */
    @Test
    void sessionCancellationCancelsSubscription() throws InterruptedException {
        var context = ContinuousSessionContexts.<Integer, String>createContinuousSessionContext(Dispatchers.getDefault());
        var subscribed = new CountDownLatch(1);
        var cancelled = new CountDownLatch(1);
        var session = context.session(new UnitContinuousSessionKey(), InSessions.reactive(
                receiver -> Mono.<Void>never()
                        .doOnSubscribe(subscription -> subscribed.countDown())
                        .doOnCancel(cancelled::countDown)
        ));

        try {
            assertTrue(subscribed.await(5, TimeUnit.SECONDS));
        } finally {
            session.cancel();
        }
        assertTrue(cancelled.await(5, TimeUnit.SECONDS));
    }
}
