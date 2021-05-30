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
@ConfigurationProperties("simbot.core.logo")
@lombok.Getter
@lombok.Setter
public class SimbotLogoConfigurationProperties {

    /**
     * 是否开启启动日志中的logo展示。
     */
    private boolean enable = true;


}
