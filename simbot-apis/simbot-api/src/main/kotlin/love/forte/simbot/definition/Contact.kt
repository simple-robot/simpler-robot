/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

package love.forte.simbot.definition

import love.forte.simbot.Api4J
import love.forte.simbot.bot.Bot
import love.forte.simbot.action.SendSupport
import love.forte.simbot.message.Message
import love.forte.simbot.message.MessageReceipt


/**
 *
 * 一个联系目标。联系人是除 [Bot] 以外的可以进行信息交流的 [User], 例如 [好友][Friend], 或者一个[群成员][Member]。
 *
 * 虽然 [Contact] 实现了 [SendSupport], 但是并不是 [Bot] 以外的所有人都一定可以进行信息交流，比如对于一个群成员，可能会受限于权限，或者受限于组织类型（例如一个订阅型组织，参考tg的"频道"）.
 *
 *
 * @author ForteScarlet
 */
public interface Contact : User, SendSupport, BotContainer {
    
    /**
     * 联系人所属的 [Bot].
     */
    override val bot: Bot
    
    
    /**
     * 向此联系目标发送消息。
     *
     * @throws love.forte.simbot.action.UnsupportedActionException 当此对象不支持发生消息时
     */
    @JvmSynthetic
    override suspend fun send(message: Message): MessageReceipt
    
    /**
     * 直接使用 [sendBlocking].
     */
    @Api4J
    @Deprecated("Just use sendBlocking.", ReplaceWith("sendBlocking(message)"), level = DeprecationLevel.ERROR)
    override fun sendIfSupportBlocking(message: Message): MessageReceipt = sendBlocking(message)
    
}


