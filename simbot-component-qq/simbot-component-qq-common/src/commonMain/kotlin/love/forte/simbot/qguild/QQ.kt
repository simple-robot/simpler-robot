/*
 *     Copyright (c) 2022-2026. ForteScarlet.
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

package love.forte.simbot.qguild

import kotlinx.serialization.json.Json
import kotlin.jvm.JvmField

/**
 * QQ 组件针对 [QQ Bot API](https://bot.q.qq.com/wiki/) 定义的一些可能会用到的常量信息和
 * QQ 组件范围内可能需要共享的内容。
 *
 * @since 5.0
 */
public object QQ {
    /**
     * 正式环境接口域名 `https://api.bot.qq.com`
     *
     * 参考[官方文档 `20260810` 更新日志](https://bot.q.qq.com/wiki/develop/api-v2/changelog.html#_20260810)：
     * > 接口调用域名统一：所有接口调用域名统一为 `api.bot.qq.com`。
     */
    public const val URL: String = "https://api.bot.qq.com"

    /**
     * 部分API中默认使用的Json序列化器。
     *
     * ```kotlin
     * Json {
     *     isLenient = true
     *     ignoreUnknownKeys = true
     *     allowSpecialFloatingPointValues = true
     *     allowStructuredMapKeys = true
     *     prettyPrint = false
     *     useArrayPolymorphism = false
     * }
     * ```
     *
     */
    @JvmField
    public val DefaultJson: Json = Json {
        isLenient = true
        ignoreUnknownKeys = true
        allowSpecialFloatingPointValues = true
        allowStructuredMapKeys = true
        prettyPrint = false
        useArrayPolymorphism = false
    }

    /**
     * 用在一些不支持获取时间的地方使用的默认值 `"0000-01-01T08:00:00.000+08:00"`。
     *
     */
    public const val ZERO_ISO_INSTANT: String = "0000-01-01T08:00:00.000+08:00"
}

