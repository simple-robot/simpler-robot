/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

package love.forte.simbot.kaiheila.api.v3.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kaiheila.api.ApiData
import love.forte.simbot.kaiheila.api.BaseRespData
import love.forte.simbot.kaiheila.objects.MentionPart
import love.forte.simbot.kaiheila.objects.Role
import love.forte.simbot.kaiheila.objects.User


/**
 * 对消息的统一描述。
 */
public interface MessageDetails : ApiData.Resp.Data {
    val id: String

    /**
     * 消息类型
     */
    val type: Int

    /**
     * 作者ID
     */
    val authorId: String

    /**
     * 消息内容
     */
    val content: String

    /**
     * 超链接解析数据
     */
    val embeds: List<Map<String, String>>

    /**
     * 附加的多媒体数据
     */
    val attachments: List<Map<String, String>>

    /**
     * 消息回应数据
     */
    val reactions: List<Reaction>

    /**
     * 引用消息
     */
    val quote: Map<String, String>?

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
     */
    override val type: Int,

    /**
     * 	作者的用户信息
     */
    val author: Author,

    /**
     * 消息内容
     */
    override val content: String,

    /**
     * at特定用户 的用户ID数组，与 mention_info 中的数据对应
     */
    val mention: List<String> = emptyList(),

    /**
     * 是否含有 @全体人员
     */
    @SerialName("mention_all")
    val mentionAll: Boolean,

    /**
     * at特定角色 的角色ID数组，与 mention_info 中的数据对应
     */
    @SerialName("mention_roles")
    val mentionRoles: List<String> = emptyList(),

    /**
     * 是否含有 @在线人员
     */
    @SerialName("mention_here")
    val mentionHere: Boolean,

    /**
     * 超链接解析数据
     */
    override val embeds: List<Map<String, String>>,

    /**
     * 附加的多媒体数据
     */
    override val attachments: List<Map<String, String>>,


    /**
     * 回应信息
     */
    override val reactions: List<Reaction>,


    /**
     * 引用消息
     */
    override val quote: Map<String, String>? = null,


    @SerialName("mention_info")
    val mentionInfo: MentionInfo,


    ) : MessageDetails, BaseRespData() {

    override val authorId: String
        get() = author.id
}


/**
 * [ChannelMessageDetails] 中的 [作者信息][ChannelMessageDetails.author]
 */
@Serializable
public data class Author(
    override val id: String,
    override val username: String,
    @SerialName("online")
    override val isOnline: Boolean,
    override val avatar: String,

    @SerialName("vip_avatar")
    override val vipAvatar: String? = null,
    // maybe miss
    @SerialName("identify_num")
    override val identifyNum: String = "",
    override val status: Int = 0,
    @SerialName("bot")
    override val isBot: Boolean = false,
    @SerialName("mobile_verified")
    override val mobileVerified: Boolean = false,
    override val nickname: String = "",
    override val roles: List<Int> = emptyList(),
) : User, BaseRespData() {
    override val originalData: String
        get() = toString()
}


/**
 * 私聊消息的内容详情
 */
@Serializable
public data class DirectMessageDetails(
    override val id: String,
    override val type: Int,
    @SerialName("author_id")
    override val authorId: String,
    override val content: String,
    override val embeds: List<Map<String, String>>,
    override val attachments: List<Map<String, String>>,
    override val reactions: List<Reaction>,
    override val quote: Map<String, String>?,
    @SerialName("read_status")
    val readStatus: Boolean,
) : MessageDetails, BaseRespData()


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
    val emoji: Emoji,
    val count: Int,
    val me: Boolean,
)


/**
 * 一个 `emoji`.
 */
@Serializable
public data class Emoji(val id: String, val name: String)


@Serializable
public data class MentionInfo(
    @SerialName("mention_part")
    val mentionPart: List<MentionPart>,

    @SerialName("mention_role_part")
    val mentionRolePart: List<Role>,
)
