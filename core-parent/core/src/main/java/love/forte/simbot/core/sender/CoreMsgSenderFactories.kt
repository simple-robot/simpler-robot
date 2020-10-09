/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     CoreMsgSenderFactories.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.sender

import love.forte.simbot.core.api.sender.GetterFactory
import love.forte.simbot.core.api.sender.MsgSenderFactories
import love.forte.simbot.core.api.sender.SenderFactory
import love.forte.simbot.core.api.sender.SetterFactory

/**
 *
 * [MsgSenderFactories] 的默认数据类实现。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public data class CoreMsgSenderFactories(
    override val senderFactory: SenderFactory,
    override val setterFactory: SetterFactory,
    override val getterFactory: GetterFactory
) : MsgSenderFactories