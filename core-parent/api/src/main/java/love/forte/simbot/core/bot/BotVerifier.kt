/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     BotVerifier.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.bot

import love.forte.simbot.core.api.message.containers.BotContainer
import love.forte.simbot.core.api.sender.MsgSenderFactories


/**
 *
 * 需要由组件进行实现的bot验证器。用于检测并注册一个bot。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface BotVerifier {
    /** 验证一个bot的注册信息，并转化为一个该组件对应的 [Bot] 实例。 */
    fun verity(botInfo: BotRegisterInfo, msgSenderFactories: MsgSenderFactories): Bot
}


// /**
//  * [BotVerifier] 基础抽象类，。
//  */
// public abstract class BaseBotVerifier : BotVerifier {
//     override fun verity(botInfo: BotRegisterInfo, msgSenderFactories: MsgSenderFactories): Bot {
//         TODO("Not yet implemented")
//     }
//
//     /**
//      *
//      */
//     abstract fun verify(botInfo: BotRegisterInfo) : BotContainer
// }
