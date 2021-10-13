/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simpler-robot
 *  * File     MessageReqFlag.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

package love.forte.simbot.kaiheila.api.v3.message


/**
 *
 * 对消息进行查询时的 `flag` 参数范围。
 *
 * @author ForteScarlet
 */
enum class MessageReqFlag(val flag: String) {

    /**
     * 查询参考消息之前的消息，不包括参考消息。
     */
    BEFORE("before"),

    /**
     * 查询以参考消息为中心，前后一定数量的消息。
     */
    AROUND("around"),

    /**
     * 查询参考消息之后的消息，不包括参考消息。
     */
    AFTER("after"),
}