/*
 * Copyright (c) 2022-2024. ForteScarlet.
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

package love.forte.simbot.qguild

//import kotlin.js.ExperimentalJsExport
//import kotlin.js.JsExport
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlin.jvm.JvmField

/**
 *
 * [QQ頻道API](https://bot.q.qq.com/wiki/develop/api/) 的一些可能会用到的常量信息，
 * 例如正式和沙箱环境的服务器地址。
 *
 *
 */
//@OptIn(ExperimentalJsExport::class)
//@JsExport
public object QQGuild {
    /**
     * 正式环境接口域名 `https://api.sgroup.qq.com`
     *
     * @see URL
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public const val URL_STRING: String = "https://api.sgroup.qq.com"

    /**
     * 正式环境接口域名 `https://api.sgroup.qq.com`
     *
     * @see URL_STRING
     */
    @JvmField
    public val URL: Url = Url(URL_STRING)

    /**
     * 沙箱环境接口域名 `https://sandbox.api.sgroup.qq.com`
     *
     * 沙箱环境只会收到测试频道的事件，且调用openapi仅能操作测试频道
     *
     * @see SANDBOX_URL
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public const val SANDBOX_URL_STRING: String = "https://sandbox.api.sgroup.qq.com"

    /**
     * 沙箱环境接口域名 `https://sandbox.api.sgroup.qq.com`
     *
     * @see SANDBOX_URL_STRING
     */
    @JvmField
    public val SANDBOX_URL: Url = Url(SANDBOX_URL_STRING)

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
}


