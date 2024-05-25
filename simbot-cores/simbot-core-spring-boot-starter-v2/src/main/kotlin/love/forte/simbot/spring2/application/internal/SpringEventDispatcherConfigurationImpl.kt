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

package love.forte.simbot.spring2.application.internal

import love.forte.simbot.common.function.ConfigurerFunction
import love.forte.simbot.core.event.impl.SimpleEventDispatcherConfigurationImpl
import love.forte.simbot.event.*
import love.forte.simbot.spring.common.application.SpringEventDispatcherConfiguration
import kotlin.coroutines.CoroutineContext

internal typealias EventInterceptorConfigPair =
    Pair<EventInterceptor, ConfigurerFunction<EventInterceptorRegistrationProperties>?>

internal typealias EventDispatchInterceptorConfigPair =
    Pair<EventDispatchInterceptor, ConfigurerFunction<EventDispatchInterceptorRegistrationProperties>?>

/**
 *
 * @author ForteScarlet
 */
internal class SpringEventDispatcherConfigurationImpl(
    internal val simple: SimpleEventDispatcherConfigurationImpl
) : AbstractEventDispatcherConfiguration(),
    SpringEventDispatcherConfiguration {
    override var coroutineContext: CoroutineContext by simple::coroutineContext
    public override val interceptors:
        MutableList<EventInterceptorConfigPair> by simple::interceptors
    public override val dispatchInterceptors:
        MutableList<EventDispatchInterceptorConfigPair> by simple::dispatchInterceptors
}
