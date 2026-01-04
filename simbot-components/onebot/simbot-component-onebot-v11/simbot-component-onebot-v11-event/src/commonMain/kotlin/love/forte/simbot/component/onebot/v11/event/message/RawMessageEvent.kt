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

package love.forte.simbot.component.onebot.v11.event.message

import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.LongID
import love.forte.simbot.component.onebot.v11.event.ExpectEventSubTypeProperty
import love.forte.simbot.component.onebot.v11.event.RawEvent
import love.forte.simbot.component.onebot.v11.message.segment.OneBotMessageSegment


/**
 * [消息事件](https://github.com/botuniverse/onebot-11/blob/master/event/message.md#消息事件)
 *
 * [RawMessageEvent] 中定义的属性为针对私聊消息和群消息事件属性中人为抽取的 _可能公共_ 属性，
 * 并非协议中明确定义一定相同的公共属性。
 * 此处只做最低限度的公共抽取。
 *
 * @author ForteScarlet
 */
@ExpectEventSubTypeProperty(value = "messageType", postType = RawMessageEvent.POST_TYPE, name = "message_type")
public interface RawMessageEvent : RawEvent {
    /**
     * 消息 ID
     */
    public val messageId: ID

    /**
     * 消息类型。比如 `private`、`group`。
     */
    public val messageType: String

    /**
     * 消息子类型。
     */
    public val subType: String

    /**
     * 消息内容。
     */
    public val message: List<OneBotMessageSegment>

    /**
     * 原始消息内容。
     */
    public val rawMessage: String

    /**
     * 发送者 QQ 号
     */
    public val userId: LongID

    /**
     * 字体。
     * 如果不支持、无法获取则会得到 `null`。
     */
    public val font: Int?

    /**
     * 发送人信息
     */
    public val sender: Sender

    /**
     * 从私聊消息事件的 `sender` 和群消息事件的 `sender`
     * 中抽取出的公共属性所定义的 sender 接口。
     */
    public interface Sender {
        /**
         * 发送者 QQ 号
         */
        public val userId: LongID

        /**
         * 昵称
         */
        public val nickname: String

        /**
         * 性别，`male` 或 `female` 或 `unknown`
         */
        public val sex: String

        /**
         * 年龄。在不支持的情况下可能会得到负数，
         * 比如一个默认值 `-1`。
         *
         * 这种不支持的默认值可能来自 OneBot 协议服务端实现，
         * 也可能来自组件内对设置的于此字段缺失的缺省默认值。
         */
        public val age: Int
    }

    public companion object {
        public const val POST_TYPE: String = "message"
    }
}


internal const val DEFAULT_SEX: String = "unknown"
internal const val DEFAULT_AGE: Int = -1
