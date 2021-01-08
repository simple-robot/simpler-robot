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

package love.forte.simbot.spring.autoconfigure.properties;

import love.forte.simbot.core.configuration.DefaultSenderType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


/**
 * 不被实际使用的配置类，主要用于生成metadata并使得Springboot提供配置文件的快速提示。
 *
 * 对照了core的具体配置内容进行的配置。
 *
 * @see love.forte.simbot.core.configuration.CoreDefaultMsgSenderConfiguration
 * @see love.forte.simbot.core.configuration.DefaultSenderType
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Component
@ConfigurationProperties(prefix = "simbot.core.sender.default")
@lombok.Getter
@lombok.Setter
public class SimbotCoreDefaultMsgSenderFactoriesConfigurationProperties {

    /**
     * 默认（当出现不支持API时使用的）送信器类型
     */
    private DefaultSenderType type = DefaultSenderType.ERROR;




}
