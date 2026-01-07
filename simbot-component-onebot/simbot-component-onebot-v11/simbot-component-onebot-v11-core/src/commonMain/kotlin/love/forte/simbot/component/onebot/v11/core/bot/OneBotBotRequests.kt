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

@file:JvmName("OneBotBotRequests")
@file:JvmMultifileClass

package love.forte.simbot.component.onebot.v11.core.bot

import io.ktor.client.statement.*
import io.ktor.http.*
import love.forte.simbot.component.onebot.v11.core.api.*
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmSynthetic

/**
 * 使用 [bot] 对 [this] 发起一次请求，
 * 并得到相应的 [HttpResponse] 响应。
 *
 * 更多描述参考 [BasicOneBotApi.request]
 *
 * @see BasicOneBotApi.request
 */
@Deprecated(
    "Use OneBotBot.execute",
    ReplaceWith("bot.execute(this)")
)
@JvmSynthetic
public suspend fun BasicOneBotApi<*>.requestBy(
    bot: OneBotBot,
): HttpResponse = bot.execute(this)

/**
 * 使用 [bot] 对 [this] 发起一次请求，
 * 并得到相应的 [String] 响应。
 *
 * 更多描述参考 [BasicOneBotApi.requestRaw]
 *
 * @throws OneBotApiResponseNotSuccessException 如果响应状态码不是 2xx (参考 [HttpStatusCode.isSuccess])
 * @see BasicOneBotApi.requestRaw
 */
@Deprecated(
    "Use OneBotBot.executeRaw",
    ReplaceWith("bot.executeRaw(this)")
)
@JvmSynthetic
public suspend fun BasicOneBotApi<*>.requestRawBy(
    bot: OneBotBot,
): String = bot.executeRaw(this)

/**
 * 使用 [bot] 对 [this] 发起一次请求，
 * 并得到相应的 [OneBotApiResult] 响应。
 *
 * 更多描述参考 [BasicOneBotApi.requestResult]
 *
 * @throws OneBotApiResponseNotSuccessException 如果响应状态码不是 2xx (参考 [HttpStatusCode.isSuccess])
 * @see BasicOneBotApi.requestResult
 */
@Deprecated(
    "Use OneBotBot.executeResult",
    ReplaceWith("bot.executeResult(this)")
)
@JvmSynthetic
public suspend fun <T : Any> BasicOneBotApi<T>.requestResultBy(
    bot: OneBotBot,
): OneBotApiResult<T> = bot.executeResult(this)

/**
 * 使用 [bot] 对 [this] 发起一次请求，
 * 并得到相应的 [T] 响应。
 *
 * 更多描述参考 [BasicOneBotApi.requestResult]
 *
 * @throws OneBotApiResponseNotSuccessException 如果响应状态码不是 2xx (参考 [HttpStatusCode.isSuccess])
 * @throws IllegalStateException 如果响应结果体的状态 [OneBotApiResult.retcode]
 * 不是成功或 [OneBotApiResult.data] 为 `null`
 *
 * @see OneBotBot.executeData
 */
@Deprecated(
    "Use OneBotBot.executeData",
    ReplaceWith("bot.executeData(this)")
)
@JvmSynthetic
public suspend fun <T : Any> BasicOneBotApi<T>.requestDataBy(
    bot: OneBotBot,
): T = bot.executeData(this)
