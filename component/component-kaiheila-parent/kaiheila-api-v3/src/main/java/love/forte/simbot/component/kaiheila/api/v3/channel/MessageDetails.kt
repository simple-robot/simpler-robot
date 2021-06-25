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

package love.forte.simbot.component.kaiheila.api.v3.channel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.`object`.Role
import love.forte.simbot.component.kaiheila.`object`.User


/**
 *
 * [消息详情](https://developer.kaiheila.cn/doc/http/message#%E6%B6%88%E6%81%AF%E8%AF%A6%E6%83%85%E5%8F%82%E6%95%B0%E8%AF%B4%E6%98%8E)
 *
 */
@Serializable
public data class MessageDetails(
    /**
     * 	消息 id
     */
    val id: String,

    /**
     * 消息类型
     */
    val type: Int,

    /**
     * 	作者的用户信息
     */
    val author: MessageAuthor,

    /**
     * 消息内容
     */
    val content: String,

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
    val embeds: List<Map<String, String>>,

    /**
     * 附加的多媒体数据
     */
    val attachments: List<Map<String, String>>,


    /*
     *  TODO
     *   "reactions": [
                    {
                        "emoji": {
                            "id": "[#129315;]",
                            "name": "[#129315;]"
                        },
                        "count": 1,
                        "me": true
                    }
                ],
     */
    val reactions: List<Map<String, String>>,


    /**
     * 引用消息
     */
    val quote: Map<String, String>? = null,


    @SerialName("mention_info")
    val mentionInfo: MentionInfo,



)


/**
 * [MessageDetails] 中的 [作者信息][MessageDetails.author]
 */
@Serializable
public data class MessageAuthor(
    override val id: String,
    override val username: String,
    override val online: Boolean,
    override val avatar: String,

    // maybe miss

    override val identifyNum: String = "",
    override val status: Int = 0,
    override val bot: Boolean = false,
    override val mobileVerified: Boolean = false,
    override val system: Boolean = false,
    override val mobilePrefix: String? = null,
    override val mobile: String? = null,
    override val invitedCount: Int = 0,
    override val nickname: String = "",
    override val roles: List<Int> = emptyList(),
) : User {
    override val originalData: String
        get() = toString()
}




// /**
//  *
//  * [MessageDetails] 中的 [超链接解析数据][MessageDetails.embeds]
//  *
//  */
// public data class Embed(
//     val type: String,
//     val url: String,
//     val originUrl: String = url,
//     val avNo: String? = null,
//     val iframePath: String? = null,
//     val duration: Long = -1,
//     val title: String? = null,
//     val pic: String? = null
// )

/*
{
    "type": "bili-video",
    "url": "https://www.bilibili.com/video/XXXXX",
    "origin_url": "https://www.bilibili.com/video/XXXXX",
    "av_no": "11J411E",
    "iframe_path": "https://player.bilibili.com/player.html?xxx=xxx",
    "duration": 97,
    "title": "Title",
    "pic": "https://**/lc01gi.jpg"
}
 */


@Serializable
public data class MentionInfo(
    @SerialName("mention_part")
    val mentionPart: List<MessageAuthor>,

    @SerialName("mention_role_part")
    val mentionRolePart: List<Role>
)
