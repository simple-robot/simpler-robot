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

@file:JvmName("IntentsUtil")

package love.forte.simbot.qguild.event

import kotlin.jvm.JvmName

@PublishedApi
internal class SimpleIntentsAppender : IntentsAppender {
    var intents: Intents = Intents(0)

    override fun appendIntents(intents: Intents) {
        this.intents += intents
    }
}

/**
 * 使用 [IntentsAppender] DSL 构建一个 [Intents].
 *
 * ```kotlin
 * val intents = Intents {
 *   // ...
 * }
 * ```
 *
 * @since 4.1.4
 */
public inline fun Intents(block: IntentsAppenderOp.() -> Unit): Intents {
    val appender = SimpleIntentsAppender()
    IntentsAppenderOp(appender).block()
    return appender.intents
}
