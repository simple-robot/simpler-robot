/*
 * Copyright (c) 2025. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.event

/**
 * 一个 [Intents] 追加器。
 * 用于方便配置类等类型对外提供Kotlin中更便捷的DSL API使用的接口类型。
 *
 * 例如:
 *
 * ```kotlin
 * intents {
 *     guilds()
 *     groupAndC2C()
 * }
 * ```
 *
 * 相关操作由KSP生成 inline API，性能无损且随着 [EventIntents] 的变化自动更新，可靠又便捷。
 *
 * 借助工厂API [IntentsAppender] 也可以用来通过 DSL API 构建一个 [Intents] 值。
 *
 * @since 4.1.4
 */
public interface IntentsAppender {
    public fun appendIntents(intents: Intents)
}

public inline fun IntentsAppender.intents(block: IntentsAppenderOp.() -> Unit) {
    IntentsAppenderOp(this).block()
}


