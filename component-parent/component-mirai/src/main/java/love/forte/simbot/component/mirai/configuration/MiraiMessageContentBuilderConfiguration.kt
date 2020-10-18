/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MiraiMessageContentBuilderConfiguration.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.component.mirai.configuration

import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.simbot.component.mirai.message.MiraiMessageContentBuilderFactory
import love.forte.simbot.core.api.message.events.MessageContentBuilderFactory

/**
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@ConfigBeans
public class MiraiMessageContentBuilderConfiguration {

    /**
     * mirai的content builder factory.
     */
    @ComponentBeans(init = false)
    fun miraiMessageContentBuilderFactory(): MessageContentBuilderFactory = MiraiMessageContentBuilderFactory


}