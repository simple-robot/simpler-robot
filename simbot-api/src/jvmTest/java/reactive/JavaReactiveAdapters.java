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

package reactive;

import love.forte.simbot.application.*;
import love.forte.simbot.event.*;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

/**
 * Builds adapters from Java lambdas so JVM tests exercise the Java-facing signatures.
 */
final class JavaReactiveAdapters {
    private JavaReactiveAdapters() {
    }

    static EventListener listener(Publisher<? extends EventResult> publisher) {
        return EventListeners.reactive(context -> publisher);
    }

    static <E extends Event> EventListener typedListener(Class<E> type, EventResult result) {
        return EventListeners.reactive(type, (context, event) -> Mono.just(result));
    }

    static EventInterceptor interceptor() {
        return EventInterceptors.reactive(context -> Mono.from(context.invoke()));
    }

    static EventDispatchInterceptor dispatchInterceptor(EventContext replacement) {
        return EventDispatchInterceptors.reactive(context ->
            Flux.from(context.invoke(replacement)).concatWith(Mono.just(EventResult.empty())));
    }

    static ApplicationLauncher<Application> launcher(Supplier<Publisher<Application>> publisher) {
        return JReactiveApplicationLauncher.toLauncher(publisher::get);
    }

    static Publisher<Application> launch(ApplicationLauncher<Application> launcher) {
        return Applications.launchReactive(launcher);
    }

    static SuspendApplicationEventHandler<Application> launchHandler(Publisher<Void> completion) {
        return JReactiveSuspendApplicationEventHandler.toHandler(application -> completion);
    }
}
