/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

package interception;

import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.BuildersKt;
import love.forte.simbot.common.attribute.MutableAttributeMap;
import love.forte.simbot.interception.*;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.CompletionStage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies factories, checked exceptions and subclassing from Java source.
 */
public class JInterceptorTests {
    private static final class Point implements InterceptionPoint<String, String> {
    }

    private static final Point POINT = new Point();

    private static String invoke(Interceptor<String, String, Point> interceptor) throws Exception {
        InterceptorInvoker<String, String> invoker = InterceptorInvokerKt.InterceptorInvoker(
            POINT, Collections.singletonList(interceptor), MutableAttributeMap.create(new HashMap<>()), (context, continuation) -> context
        );
        return BuildersKt.runBlocking(EmptyCoroutineContext.INSTANCE,
            (scope, continuation) -> invoker.invoke("value", continuation));
    }

    /**
     * Java lambdas must compose with the downstream chain through every factory.
     */
    @Test
    public void factoriesSupportJavaLambdas() throws Exception {
        assertEquals("value-blocking", invoke(Interceptors.blocking((point, context, chain) -> {
            return chain.proceed(context) + "-blocking";
        })));
        assertEquals("value-async", invoke(Interceptors.async((point, context, chain) ->
            chain.proceed(context).thenApply(result -> result + "-async"))));
        assertEquals("value-reactive", invoke(Interceptors.reactive((point, context, chain) ->
            Mono.from(chain.proceed(context)).map(result -> result + "-reactive"))));
    }

    /**
     * Checked exceptions must compile in every Java handler and reach the caller.
     */
    @Test
    public void checkedExceptionsPropagateFromEveryHandler() {
        assertEquals("blocking", assertThrows(IOException.class, () ->
            invoke(Interceptors.<String, String, Point>blocking((point, context, chain) -> {
                throw new IOException("blocking");
            }))).getMessage());
        assertEquals("async", assertThrows(IOException.class, () ->
            invoke(Interceptors.<String, String, Point>async((point, context, chain) -> {
                throw new IOException("async");
            }))).getMessage());
        assertEquals("reactive", assertThrows(IOException.class, () ->
            invoke(Interceptors.<String, String, Point>reactive((point, context, chain) -> {
                throw new IOException("reactive");
            }))).getMessage());
        assertEquals("nullable", assertThrows(IOException.class, () ->
            invoke(Interceptors.<String, String, Point>reactiveNullable((point, context, chain) -> {
                throw new IOException("nullable");
            }))).getMessage());
    }

    @Test
    public void adaptersSupportJavaSubclassing() throws Exception {
        assertEquals("value", invoke(new BlockingInterceptor<String, String, Point>() {
            @Override
            public String interceptBlocking(Point point, String context, Chain<String, String> chain)
                throws Exception {
                return chain.proceed(context);
            }
        }));
        assertEquals("value", invoke(new AsyncInterceptor<String, String, Point>() {
            @Override
            public CompletionStage<String> interceptAsync(Point point, String context, Chain<String, String> chain) {
                return chain.proceed(context);
            }
        }));
        assertEquals("value", invoke(new ReactiveInterceptor<String, String, Point>() {
            @Override
            public Publisher<String> interceptReactive(Point point, String context, Chain<String, String> chain) {
                return chain.proceed(context);
            }
        }));
    }

    /**
     * Nullable adapters retain Java lambda inference and subclass support.
     */
    @Test
    public void nullableReactiveAdaptersSupportJava() throws Exception {
        assertNull(invoke(Interceptors.reactiveNullable((point, context, chain) -> Mono.empty())));
        assertEquals("value-nullable", invoke(Interceptors.reactiveNullable((point, context, chain) ->
            Mono.from(chain.proceed(context)).map(result -> result + "-nullable"))));
        assertEquals("value", invoke(new NullableReactiveInterceptor<String, String, Point>() {
            /**
             * Preserve the downstream result through the nullable adapter.
             */
            @Override
            public Publisher<String> interceptReactive(Point point, String context, ReactiveInterceptor.Chain<String, String> chain) {
                return chain.proceed(context);
            }
        }));
    }
}
