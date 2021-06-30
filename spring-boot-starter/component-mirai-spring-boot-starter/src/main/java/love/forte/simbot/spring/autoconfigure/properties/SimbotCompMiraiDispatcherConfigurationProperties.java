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
 * @author ForteScarlet
 */
@Component
@ConfigurationProperties(prefix = "simbot.component.mirai.dispatcher")
@lombok.Getter
@lombok.Setter
@Deprecated
public class SimbotCompMiraiDispatcherConfigurationProperties {
    @Deprecated
    private Integer corePoolSize = Runtime.getRuntime().availableProcessors() * 2;
    @Deprecated
    private Integer maximumPoolSize = Runtime.getRuntime().availableProcessors() * 4;
    @Deprecated
    private Long keepAliveTime = 1000L;
}
