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

import love.forte.simbot.bot.BotResourceType;
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
@SuppressWarnings("ConfigurationProperties")
@Component
@ConfigurationProperties(prefix = "simbot.core")
@lombok.Getter
@lombok.Setter
public class SimbotCoreConfigurationProperties {

    /**
     *
     * 建议使用 simbot-bots/*.bot 进行bot注册
     *
     * 注册的bot列表信息。
     * 其格式为 xxxx:yyyy,xxxx:yyyy 。
     * 其中，xxxx一般代表为账号信息，yyyy一般代表为权限信息，例如密码，或者上报地址, 可以有多个参数。
     */
    @Deprecated
    private List<String> bots = null;

    /**
     * bot资源扫描时所使用的加载类型。
     *
     */
    private BotResourceType botResourceType;


    /**
     * 指定要启动的bots。在 .bots中通过 "action_name" 属性进行配置。
     */
    private String actionBots;


    /**
     * 可进行指定的包扫描路径。如果不指定则为启动器所在路径。
     */
    private List<String> scanPackage;




}
