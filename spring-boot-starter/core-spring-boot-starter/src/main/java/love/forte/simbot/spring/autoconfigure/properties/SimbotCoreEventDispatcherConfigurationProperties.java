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

import java.util.concurrent.TimeUnit;


/**
 * 不被实际使用的配置类，主要用于生成metadata并使得Springboot提供配置文件的快速提示。
 * <p>
 * 对照了core的具体配置内容进行的配置。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Component
@ConfigurationProperties(prefix = "simbot.core.dispatcher.event")
@lombok.Getter
@lombok.Setter
public class SimbotCoreEventDispatcherConfigurationProperties {

    /**
     * 核心线程数
     */
    private int corePoolSize = Runtime.getRuntime().availableProcessors() * 2 + 2;

    /**
     * 最大线程数
     */
    private int maximumPoolSize = Runtime.getRuntime().availableProcessors() * 8;


    /**
     * 存活时间
     */
    private long keepAliveTime = 0L;


    /**
     * 时间类型
     */
    private TimeUnit timeUnit = TimeUnit.MILLISECONDS;

}
