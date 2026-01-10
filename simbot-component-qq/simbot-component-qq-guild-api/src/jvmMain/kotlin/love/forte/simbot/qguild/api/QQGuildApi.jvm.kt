/*
 * Copyright (c) 2023-2024. ForteScarlet.
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

package love.forte.simbot.qguild.api

import io.ktor.http.*

// 急切初始化
private val logNameProcessor: HttpMethod.() -> String = run {
    // default true
    val enable = System.getProperty("simbot.qguild.api.logger.color.enable")?.toBoolean() ?: true

    // 以 DELETE 为最长标准
    // API基本不存在 HEAD OPTIONS

    if (enable) {
        {
            when (this) {
                /*
                    GET        绿色
                    POST       蓝色
                    PUT, PATCH 紫色
                    DELETE     红色
                 */
                HttpMethod.Get -> "\u001B[32m   GET\u001B[0m"
                HttpMethod.Post -> "\u001B[34m  POST\u001B[0m"
                HttpMethod.Put -> "\u001B[35m   PUT\u001B[0m"
                HttpMethod.Patch -> "\u001B[35m PATCH\u001B[0m"
                HttpMethod.Delete -> "\u001B[31mDELETE\u001B[0m"
                else -> value
            }
        }
    } else {
        {
            defaultForLogName()
        }
    }
}

/**
 * 日志对齐
 */
@PublishedApi
internal actual val HttpMethod.logName: String
    get() = logNameProcessor()
