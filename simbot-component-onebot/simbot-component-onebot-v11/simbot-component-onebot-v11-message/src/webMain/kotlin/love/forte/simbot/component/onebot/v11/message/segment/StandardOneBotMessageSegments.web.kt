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
import love.forte.simbot.resource.Resource

internal actual fun resolveResourceToFileValuePlatform(
    resource: Resource,
    localFileToBase64: Boolean,
    encoder: Base64Encoder
): String? = null

internal actual fun uriResource(uri: String): Resource {
    throw UnsupportedOperationException("URI resource is not supported in Web platform")
}

internal actual fun pathResource(path: String): Resource {
    throw UnsupportedOperationException("Path resource is not supported in Web platform")
}
