/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ListenerFunctionDataImpl.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.listener

import love.forte.simbot.core.api.message.MsgGet
import love.forte.simbot.core.api.sender.MsgSender
import love.forte.simbot.core.bot.Bot
import love.forte.simbot.core.filter.AtDetection

/**
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public data class ListenerFunctionInvokeDataImpl(
    override val msgGet: MsgGet,
    override val context: ListenerContext,
    override val atDetection: AtDetection,
    override val bot: Bot,
    override val msgSender: MsgSender
) : ListenerFunctionInvokeData