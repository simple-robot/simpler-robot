package love.forte.simbot.component.kaiheila.event.system.channel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * 新增频道
 *
 * added_channel
 *
 * @author ForteScarlet
 */
@Serializable
public data class AddedChannelExtraBody(
    /**
     * 服务器频道ID
     */
    val id: String,
    /**
     * 服务器id
     */
    @SerialName("guild_id")
    val guildId: String,
    /**
     * 频道创建者id
     */
    @SerialName("master_id")
    val masterId: String,
    /**
     * 父分组频道id
     */
    @SerialName("parent_id")
    val parentId: String,
    /**
     * 频道名称
     */
    val name: String,
    /**
     * 频道简介
     */
    val topic: String,
    /**
     * 频道类型，1为文字频道，2为语音频道
     *
     */
    val type: Int,

    /**
     * 频道排序
     */
    val level: Int,

    /**
     * 慢速限制，单位秒。用户发送消息之后再次发送消息的等待时间。
     */
    @SerialName("slow_mode")
    val slowMode: Int,

    /**
     * 人数限制（如果为语音频道）
     */
    @SerialName("limit_amount")
    val limitAmount: Int,

    /**
     * 是否为分组类型
     */
    @SerialName("is_category")
    val isCategory: Boolean,

    /**
     * 语音服务器地址，HOST:PORT的格式
     */
    @SerialName("server_url")
    val serverUrl: String
) : ChannelEventExtraBody, Comparable<AddedChannelExtraBody> {
    init {
        check(type in 1..2) { "Parameter type must be 1 or 2, but $type" }
    }

    override fun compareTo(other: AddedChannelExtraBody): Int = level.compareTo(other.level)
}
