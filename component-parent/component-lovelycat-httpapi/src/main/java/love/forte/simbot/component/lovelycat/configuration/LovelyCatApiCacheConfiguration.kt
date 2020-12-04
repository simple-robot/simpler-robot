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
import love.forte.simbot.component.lovelycat.message.FakeLovelyCatApiCache
import love.forte.simbot.component.lovelycat.message.LovelyCatApiCache
import love.forte.simbot.component.lovelycat.message.ReadWriteLovelyCatApiCache
import love.forte.simbot.constant.PriorityConstant
import love.forte.simbot.core.configuration.ComponentBeans


/**
 *
 * @author ForteScarlet
 */
@ConfigBeans
public class LovelyCatApiCacheConfiguration(
    private val cacheProperties: LovelyCatApiCacheProperties
) {


    @ComponentBeans("lovelyCatApiCache", priority = PriorityConstant.LAST)
    fun lovelyCatApiCache(): LovelyCatApiCache {
        return if (cacheProperties.enable) {
            ReadWriteLovelyCatApiCache(cacheProperties.durationUnit.toMillis(cacheProperties.duration))
        } else {
            FakeLovelyCatApiCache
        }
    }

}
