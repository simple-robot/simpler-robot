/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     BotInfo.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.bot

import love.forte.simbot.core.api.message.containers.BotContainer
import love.forte.simbot.core.api.sender.BotSender
import java.io.Closeable


/**
 *
 * 一个Bot。
 * 可以获取bot信息，以及bot对应的送信器。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface Bot: BotContainer, Closeable {

    /**
     * bot 对应的送信器。
     */
    val sender: BotSender
    //
    //
    // /**
    //  * 尝试使这个bot重新登陆。
    //  * 如果使用的http接口相关服务，也有可能代表进行一次在线检测。
    //  */
    // fun reLogin(): Boolean

}
