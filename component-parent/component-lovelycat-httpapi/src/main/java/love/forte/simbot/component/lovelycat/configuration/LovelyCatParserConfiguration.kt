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

package love.forte.simbot.component.lovelycat.configuration

import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.simbot.component.lovelycat.message.event.DefaultLovelyCatParser
import love.forte.simbot.component.lovelycat.message.event.GROUP_EVENT_TYPE
import love.forte.simbot.component.lovelycat.message.event.LovelyCatEventGroupMsgParser
import love.forte.simbot.component.lovelycat.message.event.LovelyCatParser
import love.forte.simbot.core.configuration.ComponentBeans


/**
 *
 * @author ForteScarlet
 */
@ConfigBeans
public class LovelyCatParserConfiguration {


    @ComponentBeans("lovelyCatParser")
    fun lovelyCatParser(): LovelyCatParser {
        val defParser = DefaultLovelyCatParser()
        // group event.
        defParser.registerParser(GROUP_EVENT_TYPE, LovelyCatEventGroupMsgParser)
        return defParser
    }

}