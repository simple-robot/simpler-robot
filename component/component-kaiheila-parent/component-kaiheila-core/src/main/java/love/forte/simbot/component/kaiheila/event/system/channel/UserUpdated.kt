package love.forte.simbot.component.kaiheila.event.system.channel

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 *
 * 用户信息更新
 *
 * `user_updated`
 *
 * @author ForteScarlet
 */
@Serializable
public data class UserUpdatedExtraBody(
    @SerialName("user_id")
    val userId: String,
    val username: String,
    val avatar: String,
) : ChannelEventExtraBody

/*
user_id
string
用户id
username
string
用户名
avatar
string
头像地址
 */