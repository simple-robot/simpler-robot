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

import love.forte.simbot.component.lovelycat.LovelyCatApiTemplate
import love.forte.simbot.serialization.json.JsonSerializerFactory

/**
 * lovely cat 类型转化函数
 */
public interface LovelyCatParser {
    /**
     * 根据event类型转化为一个 [LovelyCatMsg] 实例。如果返回null则说明没有此类型。
     * @param event String 类型。
     * @param params Map<String, *> 接收到的参数列表
     * @return BaseLovelyCatMsg?
     */
    fun parse(
        event: String,
        originalData: String,
        api: LovelyCatApiTemplate,
        jsonSerializerFactory: JsonSerializerFactory,
        params: Map<String, *>
    ): LovelyCatMsg?

    /**
     * 根据类型获取一个对应的解析器。如果为null则说明没有对应的解析器。
     */
    fun parser(event: String): LovelyCatEventParser?

    /**
     * 判断此事件对应的类型。
     */
    fun type(event: String): Class<out LovelyCatMsg>?

}


/**
 * 对某个类型进行解析的解析器。
 */
public interface LovelyCatEventParser :
        (String, LovelyCatApiTemplate, JsonSerializerFactory, Map<String, *>) -> LovelyCatMsg {
    override operator fun invoke(
        original: String,
        api: LovelyCatApiTemplate,
        jsonSerializerFactory: JsonSerializerFactory,
        params: Map<String, *>
    ): LovelyCatMsg

    /**
     * 得到对应的事件类型。可能为 `null`。当为null的时候代表此消息可能存在多个类型，需要实例化后进行判断。
     */
    fun type(): Class<out LovelyCatMsg>?

}


/**
 * 默认解析器实例
 */
public class DefaultLovelyCatParser : LovelyCatParser {

    private val parserMap: MutableMap<String, LovelyCatEventParser> = mutableMapOf()

    /**
     * 注册一个解析器。
     */
    fun registerParser(eventName: String, parser: LovelyCatEventParser): LovelyCatEventParser? {
        return parserMap.put(eventName, parser)
    }

    /**
     * 根据event类型转化为一个 [LovelyCatMsg] 实例。如果返回null则说明没有此类型。
     * @param event String 类型。
     * @param params Map<String, *> 接收到的参数列表
     * @return BaseLovelyCatMsg?
     */
    override fun parse(
        event: String,
        originalData: String,
        api: LovelyCatApiTemplate,
        jsonSerializerFactory: JsonSerializerFactory,
        params: Map<String, *>
    ): LovelyCatMsg? {
        return parser(event)?.invoke(originalData, api, jsonSerializerFactory, params)
    }


    /**
     * 根据类型获取一个对应的解析器。如果为null则说明没有对应的解析器。
     */
    override fun parser(event: String): LovelyCatEventParser? {
        return parserMap[event]
    }

    /**
     * 判断此事件对应的类型。
     */
    override fun type(event: String) = parserMap[event]?.type()

}