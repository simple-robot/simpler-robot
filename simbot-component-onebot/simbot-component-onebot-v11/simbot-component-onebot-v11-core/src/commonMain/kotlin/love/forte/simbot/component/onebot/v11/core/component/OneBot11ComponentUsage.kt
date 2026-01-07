/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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


@file:JvmName("OneBot11ComponentUsage")

package love.forte.simbot.component.onebot.v11.core.component

import love.forte.simbot.application.ApplicationBuilder
import love.forte.simbot.application.ApplicationFactoryConfigurer
import love.forte.simbot.common.function.ConfigurerFunction
import kotlin.jvm.JvmName

/**
 * 在 [ApplicationBuilder] 中安装使用 [OneBot11Component]。
 *
 * usage:
 * ```kotlin
 * simbotApplication(Foo) {
 *    useOneBot11Component()
 *    // 或
 *    useOneBot11Component { ... }
 * }
 * ```
 *
 * 相当于：
 * ```kotlin
 * simbotApplication(Foo) {
 *    install(OneBot11Component) { ... }
 * }
 * ```
 *
 * @see OneBot11Component
 *
 */
public fun ApplicationFactoryConfigurer<*, *, *>.useOneBot11Component(
    configurator: ConfigurerFunction<OneBot11ComponentConfiguration>? = null
) {
    if (configurator != null) {
        install(OneBot11Component, configurator)
    } else {
        install(OneBot11Component)
    }
}
