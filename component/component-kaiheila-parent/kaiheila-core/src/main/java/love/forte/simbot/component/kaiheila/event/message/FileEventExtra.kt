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

package love.forte.simbot.component.kaiheila.event.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.objects.Attachments
import love.forte.simbot.component.kaiheila.objects.Role
import love.forte.simbot.component.kaiheila.objects.User


/**
 * [文件消息](https://developer.kaiheila.cn/doc/event/message#%E6%96%87%E4%BB%B6%E6%B6%88%E6%81%AF)
 * @author ForteScarlet
 */
@Serializable
public data class FileEventExtra(
    override val type: Int,
    @SerialName("guild_id")
    override val guildId: String,
    @SerialName("channel_name")
    override val channelName: String = "",

    override val mention: List<String>,
    override val mentionAll: Boolean,
    override val mentionRoles: List<Role>,
    override val mentionHere: Boolean,
    /**
     * 附件
     */
    override val attachments: FileAttachments,
    override val author: User,
    ) : AttachmentsMessageEventExtra {

}


/**
 *
 * 视频消息的资源信息。
 *
 */
@Serializable
public data class FileAttachments(
    override val type: String,
    override val name: String,
    override val url: String,
    /** 文件格式 */
    @SerialName("file_type")
    val fileType: String,
    override val size: Long,
    /** 视频时长（s） */
    val duration: Int,
    /** 视频宽度 */
    val width: Int,
    /** 视频高度 */
    val height: Int,
) : Attachments
