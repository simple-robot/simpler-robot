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

package love.forte.simbot.application

import kotlinx.coroutines.reactive.awaitSingle
import love.forte.simbot.annotations.Api4J
import love.forte.simbot.annotations.ExperimentalReactiveBridgingApi
import org.jetbrains.annotations.NonBlocking
import org.reactivestreams.Publisher

/**
 * 通过 Reactive Streams [Publisher] 实现应用启动器。
 * 运行时需要提供 `kotlinx-coroutines-reactive` 及 Reactive Streams。
 *
 * @since 5.0
 */
@Api4J
@ExperimentalReactiveBridgingApi
public fun interface JReactiveApplicationLauncher<out A : Application> {
    /**
     * 启动应用并返回发送一个应用实例后完成的 publisher。
     */
    @NonBlocking
    @Throws(Exception::class)
    public fun launch(): Publisher<out A>

    public companion object {
        /**
         * 将 [launcher] 转为等待 publisher 唯一结果的应用启动器。
         */
        @JvmStatic
        public fun <A : Application> toLauncher(launcher: JReactiveApplicationLauncher<A>): ApplicationLauncher<A> =
            ReactiveApplicationLauncherAdapter(launcher)
    }
}

@OptIn(Api4J::class, ExperimentalReactiveBridgingApi::class)
private data class ReactiveApplicationLauncherAdapter<A : Application>(
    val launcher: JReactiveApplicationLauncher<A>,
) : ApplicationLauncher<A> {
    override suspend fun launch(): A = launcher.launch().awaitSingle()
}
