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

package love.forte.simbot.core.configuration

import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.simbot.bot.CoreBotsDecoder
import love.forte.simbot.bot.CoreBotsEncoder

/**
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@ConfigBeans
public class CoreBotsCoderConfiguration {

    @CoreBeans
    public fun coreBotsEncoder(): CoreBotsEncoder = CoreBotsEncoder
    @CoreBeans
    public fun coreBotsDecoder(): CoreBotsDecoder = CoreBotsDecoder

}