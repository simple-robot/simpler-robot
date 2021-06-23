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

package love.forte.simbot.component.mirai.configuration

import love.forte.common.configuration.annotation.AsConfig
import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.simbot.component.mirai.message.LRUMiraiMessageCache
import love.forte.simbot.component.mirai.message.MiraiMessageCache
import love.forte.simbot.component.mirai.message.emptyMiraiMessageCache
import love.forte.simbot.core.configuration.ComponentBeans
import org.slf4j.Logger
import org.slf4j.LoggerFactory


/**
 *
 * @author ForteScarlet
 */
@ConfigBeans
@AsConfig(prefix = "simbot.component.mirai.message.cache", allField = true)
public class MiraiEventCacheConfiguration {

    private val logger: Logger = LoggerFactory.getLogger(MiraiEventCacheConfiguration::class.java)

    /**
     * 是否启动缓存。默认为false，即不启用。
     */
    var enable: Boolean = false

    /** 私聊消息Map最大容量 */
    var priCapacity: Int = 16

    /** 私聊消息Map初始容量 */
    var priInitialCapacity: Int = 16

    /** 群消息Map最大容量 */
    var groCapacity: Int = 128

    /** 群消息Map初始容量 */
    var groInitialCapacity: Int = 128

    /** 私聊缓存Map负载因子。默认为 0.75。 */
    var priLoadFactor: Float = 0.75F

    /** 群消息缓存Map负载因子。默认为 0.75。 */
    var groLoadFactor: Float = 0.75F


    /**
     * mirai 消息缓存器实例。
     */
    @ComponentBeans("miraiMessageCache")
    fun miraiMessageCache(): MiraiMessageCache {
        return if (!enable) {
            logger.info("Disable default mirai message cache.")
            emptyMiraiMessageCache()
        } else {
            logger.info("Enable default mirai message cache.")
            LRUMiraiMessageCache(
                priInitialCapacity = priInitialCapacity, priLoadFactor = priLoadFactor, priCapacity = priCapacity,
                groInitialCapacity = groInitialCapacity, groLoadFactor = groLoadFactor, groCapacity = groCapacity
            )
        }
    }

}