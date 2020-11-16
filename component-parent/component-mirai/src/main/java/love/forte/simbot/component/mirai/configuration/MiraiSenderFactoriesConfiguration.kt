/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
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

package love.forte.simbot.component.mirai.configuration

import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.simbot.api.sender.GetterFactory
import love.forte.simbot.api.sender.SenderFactory
import love.forte.simbot.api.sender.SetterFactory
import love.forte.simbot.component.mirai.sender.MiraiGetterFactory
import love.forte.simbot.component.mirai.sender.MiraiSenderFactory
import love.forte.simbot.component.mirai.sender.MiraiSetterFactory

/**
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@ConfigBeans
public class MiraiSenderFactoriesConfiguration {


    @ComponentBeans
    fun miraiSenderFactory(): SenderFactory = MiraiSenderFactory
    @ComponentBeans
    fun miraiSetterFactory(): SetterFactory = MiraiSetterFactory
    @ComponentBeans
    fun miraiGetterFactory(): GetterFactory = MiraiGetterFactory





}