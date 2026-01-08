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

@file:JvmName("BotRequests")
@file:JvmMultifileClass

package love.forte.simbot.qguild.stdlib

import io.ktor.client.statement.*
import love.forte.simbot.qguild.api.QQGuildApi
import love.forte.simbot.qguild.api.request
import love.forte.simbot.qguild.api.requestData
import love.forte.simbot.qguild.api.requestText
import love.forte.simbot.qguild.stdlib.internal.BotImpl
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmSynthetic


/**
 * 直接通过bot进行请求。
 */
@JvmSynthetic
public suspend fun QQGuildApi<*>.requestBy(bot: Bot): HttpResponse {
    return request(
        client = bot.apiClient,
        token = bot.qqBotToken(),
        server = bot.apiServer,
        appId = bot.ticket.appId
    )
}


/**
 * 直接通过bot进行请求。
 */
@JvmSynthetic
public suspend inline fun QQGuildApi<*>.requestTextBy(
    bot: Bot,
    useResp: (HttpResponse) -> Unit = {}
): String {
    return requestText(
        client = bot.apiClient,
        token = bot.qqBotToken(),
        server = bot.apiServer,
        useResp = useResp,
        appId = bot.ticket.appId
    )
}

/**
 * 直接通过bot进行请求。
 *
 * @throws love.forte.simbot.qguild.QQGuildApiException 如果返回状态码不在 200..300之间。
 */
@JvmSynthetic
public suspend inline fun <R : Any> QQGuildApi<R>.requestDataBy(bot: Bot): R {
    return requestData(
        client = bot.apiClient,
        token = bot.qqBotToken(),
        server = bot.apiServer,
        decoder = bot.apiDecoder,
        appId = bot.ticket.appId
    )
}

/**
 * 直接通过bot进行请求。
 */
@JvmSynthetic
public suspend fun Bot.request(api: QQGuildApi<*>): HttpResponse = api.requestBy(this)

/**
 * 直接通过bot进行请求。
 */
@JvmSynthetic
public suspend fun Bot.requestText(api: QQGuildApi<*>): String = api.requestTextBy(this)

/**
 * 直接通过bot进行请求。
 *
 * @throws love.forte.simbot.qguild.QQGuildApiException 如果返回状态码不在 200..300之间。
 */
@JvmSynthetic
public suspend fun <R : Any> Bot.requestData(api: QQGuildApi<R>): R = api.requestDataBy(this)


@Suppress("DEPRECATION")
@PublishedApi
@Deprecated("Use qqBotToken", ReplaceWith("qqBotToken()"))
internal fun Bot.botToken(): String =
    if (this is BotImpl) botToken else "Bot ${ticket.appId}.${ticket.token}"

/**
 * see [鉴权方式](https://bot.q.qq.com/wiki/develop/api-v2/dev-prepare/interface-framework/api-use.html#%E8%8E%B7%E5%8F%96%E8%B0%83%E7%94%A8%E5%87%AD%E8%AF%81)
 */
@PublishedApi
internal fun Bot.qqBotToken(): String =
    if (this is BotImpl) qqBotToken else "QQBot $accessToken"
