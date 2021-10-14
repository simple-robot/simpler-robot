package love.forte.simbot.kaiheila.event.system.guild

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * 服务器信息更新
 *
 * `updated_guild`
 *
 * @author ForteScarlet
 */
@Serializable
public data class UpdatedGuildExtraBody(
    /**
     * 服务器id
     */
    val id: String,
    /**
     * 服务器主id
     */
    val name: String,
    /**
     * 服务器icon的地址
     */
    val icon: String,
    /**
     * 通知类型, 0代表默认使用服务器通知设置，1代表接收所有通知, 2代表仅@被提及，3代表不接收通知
     */
    @SerialName("notify_type")
    val notifyType: Int,
    /**
     * 服务器默认使用语音区域
     */
    val region: String,
    /**
     * 是否为公开服务器
     */
    @SerialName("enable_open")
    val enableOpen: Boolean,
    /**
     * 公开服务器id
     */
    @SerialName("open_id")
    val openId: String,
    /**
     * 默认频道id
     */
    @SerialName("default_channel_id")
    val defaultChannelId: String,
    /**
     * 欢迎频道id
     */
    @SerialName("welcome_channel_id")
    val welcomeChannelId: String,
) : GuildEventExtraBody {
    init {
        check(notifyType in 0..3) { "Parameter notify_type must be 0|1|2|3, but $notifyType" }
    }
}

