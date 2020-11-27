/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     LovelyCatServerProperties.kt
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

package love.forte.simbot.component.lovelycat.configuration

import love.forte.common.configuration.annotation.AsConfig
import love.forte.common.ioc.annotation.ConfigBeans


@ConfigBeans("lovelyCatServerProperties")
@AsConfig(prefix = "simbot.component.lovelycat", allField = true)
public class LovelyCatServerProperties {
    /**
     * 是否开启http服务器。
     */
    var enableHttpServer: Boolean = true

    /**
     * 开启http服务的端口号
     */
    var port: Int = 8080

    /**
     * http服务器的监听地址
     */
    var path: String = "/lovelycat"

}