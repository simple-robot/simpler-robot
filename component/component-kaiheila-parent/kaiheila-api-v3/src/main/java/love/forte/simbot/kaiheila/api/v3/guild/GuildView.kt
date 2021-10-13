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

package love.forte.simbot.kaiheila.api.v3.guild

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.api.message.results.GroupAdmin
import love.forte.simbot.api.message.results.GroupFullInfo
import love.forte.simbot.api.message.results.GroupOwner
import love.forte.simbot.component.kaiheila.api.*
import love.forte.simbot.component.kaiheila.objects.Channel
import love.forte.simbot.component.kaiheila.objects.Guild
import love.forte.simbot.component.kaiheila.objects.Role


/**
 * [获取服务器详情](https://developer.kaiheila.cn/doc/http/guild#%E8%8E%B7%E5%8F%96%E6%9C%8D%E5%8A%A1%E5%99%A8%E8%AF%A6%E6%83%85)
 *
 * api: /guild/view
 *
 * request method: GET
 *
 */
public class GuildViewReq(private val guildId: String) : GetGuildApiReq<ObjectResp<GuildView>> {
    companion object Key : ApiData.Req.Key by key("/guild/view") {
        private val ROUTE = listOf("guild", "view")
    }

    override val key: ApiData.Req.Key
        get() = Key

    override val body: Any?
        get() = null

    override val dataSerializer: DeserializationStrategy<ObjectResp<GuildView>>
        get() = objectResp(GuildView.serializer())

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = ROUTE
        builder.parameters {
            append("guild_id", guildId)
        }
    }
}


/*
{
    "code": 0,
    "message": "操作成功",
    "data": {
        "id": "91686000000",
        "name": "Hello",
        "topic": "",
        "master_id": "17000000",
        "is_master": false,
        "icon": "",
        "invite_enabled": true,
        "notify_type": 2,
        "region": "beijing",
        "enable_open": true,
        "open_id": "1600000",
        "default_channel_id": "2710000000",
        "welcome_channel_id": "0",
        "features": [],
        "roles": [
            {
                "role_id": 0,
                "name": "@全体成员",
                "color": 0,
                "position": 999,
                "hoist": 0,
                "mentionable": 0,
                "permissions": 148691464
            }
        ],
        "channels": [
            {
                "id": "37090000000",
                "user_id": "1780000000",
                "parent_id": "0",
                "name": "Hello World",
                "type": 1,
                "level": 100,
                "limit_amount": 0,
                "is_category": false,
                "is_readonly": false,
                "is_private": false
            }
        ],
        "emojis": [
            {
                "name": "ceeb65XXXXXXX0j60jpwfu",
                "id": "9168XXXXX53/4c43fcb7XXXXX0c80ck"
            }
        ],
        "user_config": {
            "notify_type": null,
            "nickname": "XX",
            "role_ids": [
                702
            ],
            "chat_setting": "1"
        }
    }
}

 */

@Serializable
public data class GuildView(
    override val id: String,
    override val name: String,
    override val topic: String,
    @SerialName("master_id")
    override val masterId: String,
    override val icon: String,
    @SerialName("notify_type")
    override val notifyType: Int,
    override val region: String,
    @SerialName("enable_open")
    override val enableOpen: Boolean,
    @SerialName("open_id")
    override val openId: String,
    @SerialName("default_channel_id")
    override val defaultChannelId: String,
    @SerialName("welcome_channel_id")
    override val welcomeChannelId: String,
    /**
     * 服务器助力数量
     */
    @SerialName("boost_num")
    val boostNum: Int,
    /**
     * 服务器等级
     */
    val level: Int,
    override val roles: List<Role>,
    override val channels: List<Channel>
) : GuildApiRespData(), Guild, GroupFullInfo {
    override suspend fun channels(): List<Channel> = channels
    override val originalData: String get() = toString()

    override val maximum: Int get() = -1
    override var total  = -1 // late init

    override val createTime: Long
        get() = -1
    override val simpleIntroduction: String
        get() = topic
    override val fullIntroduction: String
        get() = topic
    override lateinit var owner: GroupOwner
    override lateinit var admins: List<GroupAdmin>
}






