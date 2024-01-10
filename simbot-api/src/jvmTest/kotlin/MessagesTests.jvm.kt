/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

import love.forte.simbot.message.MessagesBuilder
import love.forte.simbot.message.OfflineFileImage.Companion.toOfflineImage
import love.forte.simbot.message.OfflineImage.Companion.toOfflineImage
import love.forte.simbot.message.OfflinePathImage.Companion.toOfflineImage
import love.forte.simbot.message.OfflineURIImage.Companion.toOfflineImage
import love.forte.simbot.resource.toResource
import java.io.File
import java.net.URI
import kotlin.io.path.Path

internal actual fun MessagesBuilder.addIntoMessages() {
    add(File("").toResource().toOfflineImage())
    add(Path("").toResource().toOfflineImage())
    add(File("").toOfflineImage())
    add(Path("").toOfflineImage())
    add(URI.create("https://q1.qlogo.cn/g?b=qq&nk=1149159218&s=100").toURL().toResource().toOfflineImage())
    add(URI.create("https://q1.qlogo.cn/g?b=qq&nk=1149159218&s=100").toResource().toOfflineImage())
    add(URI.create("https://q1.qlogo.cn/g?b=qq&nk=1149159218&s=100").toOfflineImage())
}
