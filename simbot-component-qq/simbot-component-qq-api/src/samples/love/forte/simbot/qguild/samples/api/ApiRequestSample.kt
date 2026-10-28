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

package love.forte.simbot.qguild.samples.api

import io.ktor.client.*
import kotlinx.serialization.json.Json
import love.forte.simbot.qguild.QQGuild
import love.forte.simbot.qguild.api.request
import love.forte.simbot.qguild.api.user.GetBotGuildListApi
import love.forte.simbot.qguild.api.user.GetBotInfoApi


/**
 * 以 [GetBotGuildListApi] 为例请求API的示例。
 *
 */
suspend fun apiRequestSample1(
    client: HttpClient,
) {
    val api = GetBotGuildListApi.create(limit = 50)
    val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }
    val guildList = api.request(
        client = client,
        server = QQGuild.URL, // QQ 开放平台统一接口地址
        token = "Bot xxx",
        decoder = json
    )

    println(guildList)
}

/**
 * 以 [GetBotInfoApi] 为例请求API的示例。
 *
 */
suspend fun apiRequestSample2(
    client: HttpClient,
) {
    val json = Json {
        isLenient = true
        ignoreUnknownKeys = true
    }
    val info = GetBotInfoApi.request(
        client = client,
        server = QQGuild.URL, // QQ 开放平台统一接口地址
        token = "Bot xxx",
        decoder = json
    )

    println(info)
}
