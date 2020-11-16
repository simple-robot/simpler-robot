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

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * 不被实际使用的配置类，主要用于生成metadata并使得Springboot提供配置文件的快速提示。
 *
 * 对照了core的具体配置内容进行的配置。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@Component
@ConfigurationProperties(prefix = "simbot.core")
@lombok.Getter
@lombok.Setter
public class SimbotCoreConfigurationProperties {

    /**
     * 注册的bot列表信息。
     * 其格式为 xxxx:yyyy,xxxx:yyyy 。
     * 其中，xxxx一般代表为账号信息，yyyy一般代表为权限信息，例如密码，或者上报地址, 可以有多个参数。
     */
    private List<String> bots = null;


    /**
     * 可进行指定的包扫描路径。如果不指定则为启动器所在路径。
     */
    private List<String> scanPackage;




}
