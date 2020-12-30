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

import love.forte.common.configuration.annotation.AsConfig
import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.simbot.component.lovelycat.message.LovelyCatApiCache
import java.util.concurrent.TimeUnit


/**
 * 可爱猫api缓存配置信息。
 *
 * @see LovelyCatApiCacheConfiguration
 * @see LovelyCatApiCache
 *
 * @author ForteScarlet
 */
@ConfigBeans
@AsConfig(prefix = "simbot.component.lovelycat.api.cache", allField = true)
public class LovelyCatApiCacheProperties {

    /**
     * 是否启用api缓存。
     */
    var enable = true
        get() = field && duration > 0

    /**
     * 每个值的缓存时长。默认为一分钟。小于等于0则等同于 `enable = false`。
     */
    var duration: Long = 1L


    /**
     * 缓存时长单位。默认为分钟。
     */
    var durationUnit = TimeUnit.MINUTES

}