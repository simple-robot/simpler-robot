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
 * 真正使用到的配置类为 {@link love.forte.simbot.component.mirai.configuration.MiraiEventCacheConfiguration}
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Component
@ConfigurationProperties(prefix = "simbot.component.mirai.message.cache")
@lombok.Getter
@lombok.Setter
public class SimbotCompMiraiEventCacheConfigurationProperties {

    /**
     * 是否启动缓存。默认为false，即不启用。
     */
    private Boolean enable = false;

    /** 私聊消息Map最大容量 */
    private Integer priCapacity = 16;

    /** 私聊消息Map初始容量 */
    private Integer priInitialCapacity = 16;

    /** 群消息Map最大容量 */
    private Integer groCapacity = 128;

    /** 群消息Map初始容量 */
    private Integer groInitialCapacity = 128;

    /** 私聊缓存Map负载因子。默认为 0.75。 */
    private Float priLoadFactor = 0.75F;

    /** 群消息缓存Map负载因子。默认为 0.75。 */
    private Float groLoadFactor = 0.75F;
}
