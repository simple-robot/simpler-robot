/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     LovelyCatServer.java
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
package love.forte.simbot.spring.lovelycat.server

import kotlinx.coroutines.runBlocking
import love.forte.simbot.bot.NoSuchBotException
import love.forte.simbot.component.lovelycat.LovelyCatApiManager
import love.forte.simbot.component.lovelycat.message.event.LovelyCatParser
import love.forte.simbot.core.SimbotContext
import love.forte.simbot.listener.MsgGetProcessor
import love.forte.simbot.serialization.json.JsonSerializer
import love.forte.simbot.serialization.json.JsonSerializerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*

/**
 * 可爱猫监听服务器。
 * @author ForteScarlet
 */
@RestController
@ConditionalOnBean(SimbotContext::class)
class LovelyCatServer @Autowired constructor(
    simbotContext: SimbotContext,
    private val msgGetProcessor: MsgGetProcessor,
) {

    private val lovelyCatParser: LovelyCatParser = simbotContext.get(LovelyCatParser::class.java)
    // private val msgGetProcessor: MsgGetProcessor = simbotContext.get(MsgGetProcessor::class.java)
    private val jsonSerializerFactory: JsonSerializerFactory = simbotContext.get(JsonSerializerFactory::class.java)
    private val jsonMapSerializer: JsonSerializer<Map<*, *>> = jsonSerializerFactory.getJsonSerializer<Map<*, *>>(MutableMap::class.java)
    private val lovelyCatApiManager: LovelyCatApiManager = simbotContext.get(LovelyCatApiManager::class.java)


    @PostMapping("\${simbot.component.lovelycat.server.path:/lovelycat}")
    fun cat(@RequestBody params: Map<String, *>): Any? {
        val event = params["Event"] ?: throw NullPointerException("No param 'Event'.")
        val eventStr = event.toString()
        val botId = params["robot_wxid"]
            ?: throw NoSuchBotException("no param 'robot_wxid' or 'rob_wxid' in lovelycat request param.")
        val botIdStr = botId.toString()
        val api = lovelyCatApiManager.getApi(botIdStr)
            ?: throw IllegalStateException("cannot found Bot($botIdStr)'s api template.")
        val originalJson = jsonMapSerializer.toJson(params)
        val parser = lovelyCatParser.parser(eventStr)
        if (parser != null) {
            val type = parser.type()
            return runBlocking {
                if (type != null) {
                    msgGetProcessor.onMsgIfExist(type) { parser.invoke(originalJson, api, jsonSerializerFactory, params) }
                } else {
                    val lovelyCatMsg = parser.invoke(originalJson, api, jsonSerializerFactory, params)
                    msgGetProcessor.onMsgIfExist(lovelyCatMsg.javaClass, lovelyCatMsg)
                }
            }
        }
        return Collections.singletonMap("code", 200)
    }

}