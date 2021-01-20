/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     ListenResultProcessorConfiguration.kt
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

package love.forte.simbot.core.configuration

import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.simbot.api.message.MessageContentBuilderFactory
import love.forte.simbot.core.processor.QuickReplyProcessor


@ConfigBeans
public class ListenResultProcessorConfiguration {

    @CoreBeans
    fun quickReplyProcessor(messageContentBuilderFactory: MessageContentBuilderFactory) =
        QuickReplyProcessor(messageContentBuilderFactory)

}