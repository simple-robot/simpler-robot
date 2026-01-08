/*
 * Copyright (c) 2024. ForteScarlet.
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

package love.forte.simbot.component.qguild.message

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.util.cio.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import love.forte.simbot.component.qguild.bot.QGBot
import java.io.IOException
import java.net.URI

internal actual suspend fun readBinaryData(
    bot: QGBot?,
    url: String
): ByteArray {
    val client = bot?.source?.apiClient
    if (client != null) {
        return client.get(url).readBytes()
    }

    try {
        val jUrl = URI.create(url).toURL()
        return withContext(Dispatchers.IO) {
            jUrl.openStream()
                .toByteReadChannel(context = Dispatchers.IO)
                .toByteArray()
        }
    } catch (io: IOException) {
        throw IllegalStateException(io.localizedMessage, io)
    }
}
