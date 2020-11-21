/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     LovelycatEventLogin.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

package love.forte.simbot.component.lovelycat.message.event

import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.api.message.containers.botAsAccountInfo
import love.forte.simbot.api.message.events.MsgGet

/**
 * 一个bot上线后触发的事件。
 */
public interface LovelyCatLogin : MsgGet {
    /**
     * 当前账号的JSON对象，具体JSON结构请查看日志.
     */
    val botJsonInfo: String
}


/**
 * 新的账号登录成功/下线时
 *
 */
public data class LovelyCatEventLogin(
    private val robWxid: String,
    private val robName: String,
    override val type: Int,
    /** 当前账号的JSON对象，具体JSON结构请查看日志 */
    override val msg: String,
    override val originalData: String
) : BaseLovelyCatMsg("EventLogin", originalData), LovelyCatLogin {
    /*
        事件名=EventLogin	新的账号登录成功/下线时，运行这里
        rob_wxid, 文本型
        rob_name, 文本型
        type, 整数型, , 0 登录成功 / 1 即将离线
        msg, 文本型, , 当前账号的JSON对象，具体JSON结构请查看日志
     */

    override val botJsonInfo: String
        get() = msg

    /**
     *  登录事件不存在text。
     */
    override val text: String?
        get() = null

    /**
     * bot信息
     */
    override val botInfo: BotInfo = lovelyCatBotInfo(robWxid, robName)

    /**
     * 账号的信息。等同于 [botInfo].
     */
    override val accountInfo: AccountInfo
        get() = botInfo.botAsAccountInfo()
}