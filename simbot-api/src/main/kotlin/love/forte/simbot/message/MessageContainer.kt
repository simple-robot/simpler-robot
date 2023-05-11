/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.message


/**
 * 一个消息容器，代表了一个承载着消息的内容。
 *
 * 常见有消息事件，和可以查询的历史消息。
 *
 * @author ForteScarlet
 */
public interface MessageContainer {

    /**
     * 消息内容。
     */
    public val messageContent: MessageContent

}

//
public interface RemoteMessageContainer : MessageContainer {
    override val messageContent: RemoteMessageContent
}
