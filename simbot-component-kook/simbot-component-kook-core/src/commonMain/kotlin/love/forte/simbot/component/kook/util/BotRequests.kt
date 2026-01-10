/*
 *     Copyright (c) 2023-2026. ForteScarlet.
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
@file:JvmName("KookBotRequests")
@file:JvmMultifileClass

package love.forte.simbot.component.kook.util

import io.ktor.client.statement.*
import love.forte.simbot.component.kook.bot.KookBot
import love.forte.simbot.kook.api.ApiResult
import love.forte.simbot.kook.api.KookApi
import love.forte.simbot.kook.api.request
import love.forte.simbot.kook.stdlib.requestBy
import love.forte.simbot.kook.stdlib.requestDataBy
import love.forte.simbot.kook.stdlib.requestResultBy
import love.forte.simbot.kook.stdlib.requestTextBy
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmSynthetic

//region request
/**
 * 使用 [KookBot] 对 [api] 发起请求。
 *
 * @see KookApi.request
 */
@JvmSynthetic
public suspend fun KookBot.request(api: KookApi<*>): HttpResponse = api.requestBy(sourceBot)

/**
 * 使用 [KookBot] 对 [api] 发起请求。
 *
 * @see KookApi.requestDataBy
 */
@JvmSynthetic
public suspend fun <T : Any> KookBot.requestData(api: KookApi<T>): T = api.requestDataBy(sourceBot)

/**
 * 使用 [KookBot] 对 [api] 发起请求。
 *
 * @see KookApi.requestTextBy
 */
@JvmSynthetic
public suspend fun KookBot.requestText(api: KookApi<*>): String {
    return api.requestTextBy(sourceBot)
}

/**
 * 使用 [KookBot] 对 [api] 发起请求。
 *
 * @see KookApi.requestResultBy
 */
@JvmSynthetic
public suspend fun KookBot.requestResult(api: KookApi<*>): ApiResult {
    return api.requestResultBy(sourceBot)
}
//endregion

//region requestBy
/**
 * 使用 [KookApi] 通过 [bot] 发起请求。
 *
 * @see KookApi.request
 */
@JvmSynthetic
public suspend fun KookApi<*>.requestBy(bot: KookBot): HttpResponse = bot.request(this)

/**
 * 使用 [KookApi] 通过 [bot] 发起请求。
 *
 * @see KookApi.requestDataBy
 */
@JvmSynthetic
public suspend fun <T : Any> KookApi<T>.requestDataBy(bot: KookBot): T = bot.requestData(this)


/**
 * 使用 [KookApi] 通过 [bot] 发起请求。
 *
 * @see KookApi.requestTextBy
 */
@JvmSynthetic
public suspend fun KookApi<*>.requestTextBy(bot: KookBot): String = bot.requestText(this)


/**
 * 使用 [KookApi] 通过 [bot] 发起请求。
 *
 * @see KookApi.requestResultBy
 */
@JvmSynthetic
public suspend fun KookApi<*>.requestResultBy(bot: KookBot): ApiResult = bot.requestResult(this)
//endregion
