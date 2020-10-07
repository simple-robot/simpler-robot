/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     Factories.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.api.sender

import love.forte.simbot.core.api.message.MsgGet


/**
 * [Getter] factory.
 */
public interface GetterFactory {
    fun getGetter(msg: MsgGet): Getter
}


/**
 * [Setter] factory.
 */
public interface SetterFactory {
    fun getSetter(msg: MsgGet): Setter
}


/**
 * [Sender] factory.
 */
public interface SenderFactory {
    fun getSender(msg: MsgGet): Sender
}



