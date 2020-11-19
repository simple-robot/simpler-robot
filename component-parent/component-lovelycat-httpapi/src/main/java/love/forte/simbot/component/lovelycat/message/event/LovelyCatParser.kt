/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     LovelyCatParser.kt
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

package love.forte.simbot.component.lovelycat.message.event

/**
 * lovely cat 类型转化函数
 */
public fun interface LovelyCatParser {
    /**
     * 根据event类型转化为一个 [LovelyCatMsg] 实例。如果返回null则说明没有此类型。
     * @param event String 类型。
     * @param params Map<String, *> 接收到的参数列表
     * @return BaseLovelyCatMsg?
     */
    fun parse(event: String, params: Map<String, *>) : LovelyCatMsg?
}