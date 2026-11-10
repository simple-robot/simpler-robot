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

@file:JvmName("Interceptors")
@file:JvmMultifileClass

package love.forte.simbot.interception

import kotlinx.coroutines.Dispatchers
import love.forte.simbot.annotations.Api4J
import love.forte.simbot.suspendrunner.runInNoScopeBlocking
import kotlin.coroutines.CoroutineContext

// TODO doc...
public sealed class JvmPlatformInterceptor<C : Interceptor.Context, R, P : InterceptionPoint<C, R>> :
    Interceptor<C, R, P>

/**
 * TODO doc...
 *
 * ```java
 * var interceptor = Interceptors.blocking((point, context, proceed) -> proceed.invoke(context));
 * ```
 *
 * @since 5.0
 */
@Api4J
@JvmOverloads
public fun <C : Interceptor.Context, R, P : InterceptionPoint<C, R>> blocking(
    context: CoroutineContext? = Dispatchers.IO,
    interceptor: (
        point: P,
        context: C,
        proceed: (C) -> R
    ) -> R
): Interceptor<C, R, P> {
    return object : BlockingInterceptor<C, R, P>(context) {
        override fun interceptBlocking(point: P, context: C, proceed: (C) -> R): R {
            return interceptor(point, context, proceed)
        }
    }
}

/**
 * TODO doc...
 *
 * @since 5.0
 */
@Api4J
public abstract class BlockingInterceptor<C : Interceptor.Context, R, P : InterceptionPoint<C, R>>(
    private val blockingContext: CoroutineContext? = Dispatchers.IO
) : JvmPlatformInterceptor<C, R, P>() {
    final override suspend fun intercept(point: P, context: C, proceed: suspend (C) -> R): R {
        return if (blockingContext == null) {
            interceptBlocking(
                point,
                context,
                proceed = { c -> runInNoScopeBlocking { proceed(c) } }
            )
        } else {
            interceptBlocking(
                point,
                context,
                proceed = { c -> runInNoScopeBlocking(blockingContext) { proceed(c) } }
            )
        }
    }

    public abstract fun interceptBlocking(point: P, context: C, proceed: (C) -> R): R
}

// TODO async, reactive ...
