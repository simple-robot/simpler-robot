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

package love.forte.simbot.api.sender


/**
 * 异常送信器工厂
 */
public object ErrorFactories : DefaultMsgSenderFactories {
    override val defaultSenderFactory: DefaultSenderFactory
        get() = ErrorSenderFactory
    override val defaultSetterFactory: DefaultSetterFactory
        get() = ErrorSetterFactory
    override val defaultGetterFactory: DefaultGetterFactory
        get() = ErrorGetterFactory
}