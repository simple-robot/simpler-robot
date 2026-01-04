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

package love.forte.simbot.component.onebot.v11.message.segment

import love.forte.simbot.component.onebot.v11.message.Base64Encoder
import love.forte.simbot.component.onebot.v11.message.base64
import love.forte.simbot.resource.ByteArrayResource
import love.forte.simbot.resource.Resource
import love.forte.simbot.resource.toResource
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi


@OptIn(ExperimentalEncodingApi::class)
internal fun resolveUrlOrFileToResource(url: String?, file: String, encoder: Base64Encoder): Resource {
    return if (url != null) {
        uriResource(url)
    } else {
        when {
            file.startsWith("file://") -> {
                pathResource(file.substring(7))
            }

            file.startsWith("base64://") -> {
                base64Resource(file.substring(9), encoder.base64)
            }

            else -> {
                uriResource(file)
            }
        }
    }
}

internal expect fun uriResource(uri: String): Resource
internal expect fun pathResource(path: String): Resource

@OptIn(ExperimentalEncodingApi::class)
internal fun base64Resource(data: String, base64: Base64?): Resource {
    // TODO 是否需要考虑为 decoder 也提供额外参数或注解？
    return (base64 ?: Base64.Default).decode(data).toResource()
}

internal expect fun resolveResourceToFileValuePlatform(
    resource: Resource,
    localFileToBase64: Boolean,
    encoder: Base64Encoder
): String?

internal fun resolveResourceToFileValue(
    resource: Resource,
    localFileToBase64: Boolean,
    encoder: Base64Encoder
): String {
    return when (resource) {
        is ByteArrayResource -> computeBase64FileValue(resource.data(), encoder)
        else -> {
            resolveResourceToFileValuePlatform(resource, localFileToBase64, encoder)
                ?: computeBase64FileValue(resource.data(), encoder)
        }
    }
}

@OptIn(ExperimentalEncodingApi::class)
internal fun computeBase64FileValue(data: ByteArray, encoder: Base64Encoder): String {
    return buildString {
        append("base64://")
        encoder.encodeToAppendable(data, this)
    }
}
