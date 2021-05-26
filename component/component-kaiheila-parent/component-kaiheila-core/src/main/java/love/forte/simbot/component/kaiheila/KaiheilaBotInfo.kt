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

package love.forte.simbot.component.kaiheila


/**
 * 一个开黑啦Bot的信息实例。
 *
 * 参考 [开黑啦 - 机器人](https://developer.kaiheila.cn/bot)
 *
 *
 * 机器人连接模式有两种：webSocket 和 webhook.
 *
 *
 * @author ForteScarlet
 */
public interface KaiheilaBot {

    /**
     * Client id.
     */
    val clientId: String

    /**
     * token info. 可以重新生成，因此也允许运行时更新。
     */
    var token: String


    /**
     * client secret. 可以重新生成，因此也允许运行时更新。
     */
    var clientSecret: String

}






public enum class ConnectionMode {
    WEBSOCKET, WEBHOOK
}

