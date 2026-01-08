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

package love.forte.simbot.qguild.api.message

import io.ktor.client.request.forms.*
import love.forte.simbot.qguild.QGInternalApi

/**
 *
 * JS平台所能支持的针对 [MessageSendApi.Body.fileImage] 的解析。
 *
 * _暂无额外支持。_
 *
 *
 */
@QGInternalApi
public actual fun FormBuilder.resolveOther(fileImage: Any?) {
}

