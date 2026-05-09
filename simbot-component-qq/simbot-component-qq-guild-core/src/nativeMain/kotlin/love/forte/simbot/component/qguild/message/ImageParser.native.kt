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

package love.forte.simbot.component.qguild.message

import love.forte.simbot.message.Messages
import love.forte.simbot.message.OfflineImage
import love.forte.simbot.message.OfflineResourceImage

internal actual fun processOfflineImage0(
    index: Int,
    element: OfflineImage,
    messages: Messages?,
    builderContext: SendingMessageParser.BuilderContext
): Boolean = false

internal actual suspend fun processOfflineImage0(
    index: Int,
    element: OfflineImage,
    messages: Messages?,
    builderContext: SendingMessageParser.GroupAndC2CBuilderContext
): Boolean {
    return when (element) {
        is OfflineResourceImage -> processBase64OfflineImage(
            index,
            element,
            resource = element.resource,
            data = element.data(),
            builderContext
        )
        else -> processBase64OfflineImage(
            index,
            element,
            resource = null,
            data = element.data(),
            builderContext
        )
    }
}

internal actual const val BASE64_UPLOAD_WARN_INITIAL_VALUE: Boolean = true
