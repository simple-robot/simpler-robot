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

package love.forte.simbot.component.kaiheila.api.v3.server

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.BooleanAsIntSerializer


@Serializable
public class GuildListReq


/**
 *
 * [获取当前用户加入的服务器列表](https://developer.kaiheila.cn/doc/http/guild#%E8%8E%B7%E5%8F%96%E5%BD%93%E5%89%8D%E7%94%A8%E6%88%B7%E5%8A%A0%E5%85%A5%E7%9A%84%E6%9C%8D%E5%8A%A1%E5%99%A8%E5%88%97%E8%A1%A8)
 * api的返回值。
 *
 *
 *
 */
@Serializable
public data class GuildListResp(
    /**
     * 服务器id
     */
    val id: String,
    /**
     * 服务器名称
     */
    val name: String,
    /**
     * 服务器主题
     */
    val topic: String,
    /**
     * 服务器主的id
     */
    @SerialName("master_id")
    val masterId: String,
    /**
     * 	服务器icon的地址
     */
    val icon: String,

    /**
     * 通知类型,
     * - `0` 代表默认使用服务器通知设置
     * - `1` 代表接收所有通知
     * - `2` 代表仅@被提及
     * - `3` 代表不接收通知
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
    @Serializable(BooleanAsIntSerializer::class)
    val enableOpen: Boolean,
    /**
     * 公开服务器id
     */
    @SerialName("open_id")
    val openId: String,
    /**
     * 	默认频道id
     */
    @SerialName("default_channel_id")
    val defaultChannelId: String,
    /**
     * 欢迎频道id
     */
    @SerialName("welcome_channel_id")
    val welcomeChannelId: String,
) : GuildApiResp


@Serializable
public data class GuildApiRespSort(val id: Int)



