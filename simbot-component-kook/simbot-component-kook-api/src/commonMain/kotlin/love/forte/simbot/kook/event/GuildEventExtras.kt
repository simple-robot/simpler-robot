/*
 *     Copyright (c) 2023-2026. ForteScarlet.
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

package love.forte.simbot.kook.event

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.messages.Emoji
import love.forte.simbot.kook.objects.Guild
import love.forte.simbot.kook.objects.SimpleGuild

/**
 * // TODO
 *
 */
@Serializable
public data class EventGuild(
    override val id: String,
    override val name: String,
    override val topic: String,
    @SerialName("user_id") override val userId: String,
    override val icon: String,
    @SerialName("notify_type") override val notifyType: Int,
    override val region: String,
    /**
     * 是否为公开服务器, 1 or 0
     */
    @SerialName("enable_open") val enableOpenValue: Int,
    @SerialName("open_id") override val openId: String,
    @SerialName("default_channel_id") override val defaultChannelId: String,
    @SerialName("welcome_channel_id") override val welcomeChannelId: String
) : Guild {

    /**
     * 是否为公开服务器
     *
     * @see enableOpenValue
     */
    override val enableOpen: Boolean
        get() = enableOpenValue == 1

}

/**
 * [服务器信息更新](https://developer.kookapp.cn/doc/event/guild#%E6%9C%8D%E5%8A%A1%E5%99%A8%E4%BF%A1%E6%81%AF%E6%9B%B4%E6%96%B0)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(UpdateGuildEventExtra.TYPE)
public data class UpdateGuildEventExtra(override val body: SimpleGuild) : SystemExtra() {
    public companion object {
        public const val TYPE: String = "updated_guild"
    }
}


/**
 * [服务器删除](https://developer.kookapp.cn/doc/event/guild#%E6%9C%8D%E5%8A%A1%E5%99%A8%E5%88%A0%E9%99%A4)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(DeleteGuildEventExtra.TYPE)
public data class DeleteGuildEventExtra(override val body: SimpleGuild) : SystemExtra() {
    public companion object {
        public const val TYPE: String = "deleted_guild"
    }
}

/**
 * [AddBlockListEventExtra] 事件体。
 */
@Serializable
public data class AddBlockListEventBody(
    /**
     * 操作人 id
     */
    @SerialName("operator_id")
    val operatorId: String,
    /**
     * 封禁理由
     */
    val remark: String,
    /**
     * 被封禁成员 id 列表
     */
    @SerialName("user_id")
    val userId: List<String> = emptyList(),
)

/**
 * [服务器封禁用户](https://developer.kookapp.cn/doc/event/guild#%E6%9C%8D%E5%8A%A1%E5%99%A8%E5%B0%81%E7%A6%81%E7%94%A8%E6%88%B7)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(AddBlockListEventExtra.TYPE)
public data class AddBlockListEventExtra(override val body: AddBlockListEventBody) : SystemExtra() {
    public companion object {
        public const val TYPE: String = "added_block_list"
    }
}

/**
 * [DeleteBlockListEventExtra] 事件体。
 */
@Serializable
public data class DeleteBlockListEventBody(
    /**
     * 操作人 id
     */
    @SerialName("operator_id")
    val operatorId: String,
    /**
     * 封禁理由
     */
    val remark: String,
)

/**
 * [服务器取消封禁用户](https://developer.kookapp.cn/doc/event/guild#%E6%9C%8D%E5%8A%A1%E5%99%A8%E5%8F%96%E6%B6%88%E5%B0%81%E7%A6%81%E7%94%A8%E6%88%B7)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(DeleteBlockListEventExtra.TYPE)
public data class DeleteBlockListEventExtra(override val body: DeleteBlockListEventBody) : SystemExtra() {
    public companion object {
        public const val TYPE: String = "deleted_block_list"
    }
}

/**
 * [服务器添加新表情](https://developer.kookapp.cn/doc/event/guild#%E6%9C%8D%E5%8A%A1%E5%99%A8%E6%B7%BB%E5%8A%A0%E6%96%B0%E8%A1%A8%E6%83%85)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(AddedEmojiEventExtra.TYPE)
public data class AddedEmojiEventExtra(override val body: Emoji) : SystemExtra() {
    public companion object {
        public const val TYPE: String = "added_emoji"
    }
}

/**
 * [服务器删除表情](https://developer.kookapp.cn/doc/event/guild#%E6%9C%8D%E5%8A%A1%E5%99%A8%E5%88%A0%E9%99%A4%E8%A1%A8%E6%83%85)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(RemovedEmojiEventExtra.TYPE)
public data class RemovedEmojiEventExtra(override val body: Emoji) : SystemExtra() {
    public companion object {
        public const val TYPE: String = "removed_emoji"
    }
}


/**
 * [服务器更新表情](https://developer.kookapp.cn/doc/event/guild#%E6%9C%8D%E5%8A%A1%E5%99%A8%E6%9B%B4%E6%96%B0%E8%A1%A8%E6%83%85)
 *
 * @author ForteScarlet
 */
@Serializable
@SerialName(UpdatedEmojiEventExtra.TYPE)
public data class UpdatedEmojiEventExtra(override val body: Emoji) : SystemExtra() {
    public companion object {
        public const val TYPE: String = "updated_emoji"
    }
}

