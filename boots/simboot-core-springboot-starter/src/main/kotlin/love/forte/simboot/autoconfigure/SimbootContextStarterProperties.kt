/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simboot.autoconfigure

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 *
 * @author ForteScarlet
 */
@Suppress("ConfigurationProperties")
@ConfigurationProperties(prefix = "simbot.core")
public open class SimbootContextStarterProperties {

    /**
     * 是否在启动后以独立线程保持 [love.forte.simboot.SimbootContext] 实例的运行。
     */
    public var keepAlive: Boolean = false


}