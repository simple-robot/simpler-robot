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
package love.forte.simbot.component.ding.configuration

import love.forte.common.configuration.annotation.AsConfig
import love.forte.common.ioc.annotation.ConfigBeans

/**
 *
 * 钉钉机器人配置文件内容。
 *
 * @author ForteScarlet
 */
@ConfigBeans
@AsConfig(prefix = "simbot.component.ding", allField = true)
class ComponentDingProperties {
    /**
     * suppress inspection "UnusedProperty" for whole file
     * 此处可支持多个钉钉bot，格式与simbot.core.bots格式类型
     * 关于secret和access_token, 分比对应code和path
     * access_token就是注册了bot之后，给你的webhook地址后的那个access_token参数。此参数是必须存在的
     * secret是钉钉机器人三种安全策略的第二种，可以生成，也可以不存在。
     * 具体请查看钉钉机器人官方文档：https://ding-doc.dingtalk.com/doc#/serverapi3/iydd5h/404d04c3
     * 例如：
     * simbot.ding.bots=secret:access_token
     * 其中，secret可省略，则为：
     * simbot.ding.bots=:access_token
     */
    var bots: List<String> = emptyList()


    /**
     * 钉钉bot的webhook地址，不要携带任何参数, 如果不填默认值为 https://oapi.dingtalk.com/robot/send
     */
    var webhook = "https://oapi.dingtalk.com/robot/send"
}