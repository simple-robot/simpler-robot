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

package love.forte.simbot.spring.autoconfigure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * simbot配置文件对应实例。实际上没有被使用到，仅用作提供springboot的配置文件快捷提醒。
 *
 * 真正使用到的配置类为 {@link love.forte.simbot.component.mirai.utils.MiraiBotEventRegistrar}
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Component
@ConfigurationProperties(prefix = "simbot.component.mirai.dispatcher")
@lombok.Getter
@lombok.Setter
public class SimbotCompMiraiDispatcherConfigurationProperties {

    /**
     * 核心线程数。
     */
    private Integer corePoolSize = Runtime.getRuntime().availableProcessors() * 2;

    /**
     * 最大线程数。
     */
    private Integer maximumPoolSize = Runtime.getRuntime().availableProcessors() * 4;

    /**
     * 最大存活时间
     */
    private Long keepAliveTime = 1000L;


}
