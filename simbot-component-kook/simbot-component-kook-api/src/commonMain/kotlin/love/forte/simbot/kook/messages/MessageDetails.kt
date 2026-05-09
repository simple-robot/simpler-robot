/*
 *     Copyright (c) 2021-2026. ForteScarlet.
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

package love.forte.simbot.kook.messages

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import love.forte.simbot.kook.Kook
import love.forte.simbot.kook.api.ApiResultType
import love.forte.simbot.kook.objects.*


/**
 * 对消息的统一描述。
 */
public interface MessageDetails {
    /**
     * ID
     */
    public val id: String

    /**
     * 消息类型
     *
     * @see MessageType
     */
    public val type: Int

    /**
     * 作者ID
     */
    public val authorId: String

    /**
     * 消息内容
     */
    public val content: String

    /**
     * 超链接解析数据
     */
    public val embeds: List<Map<String, String>>

    /**
     * 附加的多媒体数据
     */
    public val attachments: Attachments?

    /**
     * 消息回应数据
     */
    public val reactions: List<Reaction>

    /**
     * 引用消息
     */
    public val quote: Quote?

}


/**
 *
 * 频道的[消息详情](https://developer.kaiheila.cn/doc/http/message#%E6%B6%88%E6%81%AF%E8%AF%A6%E6%83%85%E5%8F%82%E6%95%B0%E8%AF%B4%E6%98%8E)
 *
 */
@Serializable
public data class ChannelMessageDetails(

    /**
     * 	消息 id
     */
    override val id: String,

    /**
     * 消息类型
     * @see MessageType
     */
    override val type: Int,

    /**
     * 	作者的用户信息
     */
    public val author: SimpleUser,

    /**
     * 消息内容
     *
     * _（为了保障消息正常发出，请不要超过 8000 字符）_
     */
    override val content: String,

    /**
     * at特定用户 的用户ID数组，与 mention_info 中的数据对应
     */
    public val mention: List<String> = emptyList(),

    /**
     * 是否含有 @全体人员
     */
    @SerialName("mention_all") public val isMentionAll: Boolean = false,

    /**
     * at特定角色 的角色ID数组，与 [mentionInfo] 中的数据对应
     */
    @SerialName("mention_roles") public val mentionRoles: List<Int> = emptyList(),

    /**
     * 是否含有 @在线人员
     */
    @SerialName("mention_here") public val isMentionHere: Boolean = false,

    /**
     * 超链接解析数据
     */
    override val embeds: List<Map<String, String>> = emptyList(),

    /**
     * 附加的多媒体数据
     */
    override val attachments: SimpleAttachments? = null,

    /**
     * 回应信息
     */
    override val reactions: List<Reaction> = emptyList(),

    /**
     * 引用消息
     */
    override val quote: Quote? = null,

    /**
     * 引用特定用户或特定角色的信息
     */
    @SerialName("mention_info") public val mentionInfo: MentionInfo? = null,

    /**
     * 消息所属的频道id
     */
    @SerialName("channel_id")
    public val channelId: String? = null, // TODO null? or exists?

) : MessageDetails {
    override val authorId: String
        get() = author.id
}


/**
 * 私聊消息的内容详情
 *
 * 更多参考 [文档](https://developer.kookapp.cn/doc/http/direct-message#%E6%B6%88%E6%81%AF%E8%AF%A6%E6%83%85%E5%8F%82%E6%95%B0%E8%AF%B4%E6%98%8E)
 *
 */
@Serializable
public data class DirectMessageDetails(
    public val id: String,
    public val type: Int,
    @SerialName("author_id") public val authorId: String,
    public val content: String,
    public val embeds: List<Map<String, String>> = emptyList(),
    // 2024/8/6
    // 事件中，客户端发送的附件都被转成了card消息，attachments 永远为 null，看不出结构；
    // 私聊事件查询详情时会得到空数组 `"attachments":[]`, 进而导致报错
    // 暂时不知道到底是两边都是数组，还是只有私聊变成了数组，因此暂且仅处理私聊。
    @SerialName("attachments")
    public val attachmentsList: List<SimpleAttachments>? = null,
    public val reactions: List<Reaction> = emptyList(),
    // 如果没有引用，会冒出来一个空字符串。
    // 我真服了。
    @SerialName("quote")
    private val sourceQuote: JsonElement? = null,
    @SerialName("read_status") public val readStatus: Boolean = false,
) {
    val quote: Quote?
        get() {
            val obj = sourceQuote as? JsonObject? ?: return null
            return Kook.DEFAULT_JSON.decodeFromJsonElement(Quote.serializer(), obj)
        }

    public val attachments: Attachments?
        get() = attachmentsList?.firstOrNull()
}


/**
 * 回应信息
 * ```json
 *  "reactions": [
 *      {
 *          "emoji": {
 *              "id": "[#129315;]",
 *              "name": "[#129315;]"
 *           },
 *          "count": 1,
 *          "me": true
 *      }
 *  ],
 * ```
 */
@Serializable
public data class Reaction(
    public val emoji: Emoji, public val count: Int, public val me: Boolean
)


/**
 * 一个 `emoji`.
 */
@Serializable
public data class Emoji @ApiResultType constructor(
    val id: String, val name: String
)


/**
 * 引用特定用户或特定角色的信息
 */
@Serializable
public data class MentionInfo @ApiResultType constructor(
    /**
     * `@特定用户` 详情
     */
    @SerialName("mention_part") val mentionPart: List<MentionPart>,

    /**
     * `@特定角色` 详情
     */
    @SerialName("mention_role_part") val mentionRolePart: List<Role>,
)


/**
 * Mention part info.
 */
@Serializable
public data class MentionPart @ApiResultType constructor(
    val id: String,
    val username: String,
    @SerialName("full_name") val fullName: String,
    val avatar: String,
)
