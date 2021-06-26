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
@file:JvmName("DingSenders")
package love.forte.simbot.component.ding.sender

import love.forte.simbot.component.ding.messages.DingSpecialMessage
import love.forte.simbot.component.ding.messages.DingSpecialMessageChain
import love.forte.simbot.component.ding.messages.toDingChain
import love.forte.simbot.component.ding.sceret.DingSecretCalculator
import love.forte.simbot.component.ding.utils.DingCatUtil
import love.forte.simbot.http.template.HttpHeaders
import love.forte.simbot.http.template.HttpTemplate
import love.forte.simbot.serialization.json.JsonSerializerFactory


/**
 * 钉钉送信器的接口定义
 */
interface DingSender {
    fun sendMsg(msg: String): String
    fun sendMsg(msg: DingSpecialMessageChain): String
    
    fun sendMsg(msg: DingSpecialMessage): String = sendMsg(msg.toDingChain())
}

private val jsonHeaders = HttpHeaders.instance.apply { add("Content-Type", "application/json; charset=utf-8") }

/**
 * 钉钉机器人仅支持发送群消息。
 * 因为钉钉机器人发送消息是不需要群号的，因此群号可以为null且会被忽略
 * @param webhook webhook, 此处为携带了access_token的完整路径
 * @param secret 密钥
 * @param secretCalculator 签名计算器
 */
open class DingSenderImpl(
        // private val accessToken: String,
        private val webhook: String,
        private val secret: String?,
        private val secretCalculator: DingSecretCalculator,
        private val http: HttpTemplate,
        private val jsonSerializerFactory: JsonSerializerFactory
): DingSender {

    /**
     * 获取实际送信的url
     */
    private val url: String

    get() {
        val calculationResults = secretCalculator.calculate(System.currentTimeMillis(), secret ?: return webhook)
        return "$webhook&timestamp=${calculationResults.timestamp}&sign=${calculationResults.sign}"
    }

    /**
     * http发送消息
     */
    private fun send(json: String): String? {
        return http.post(url, jsonHeaders, cookies = null, json, String::class.java).body
    }

    /**
     * 发送消息
     * @param msg 发送的消息, 其中可以包含可解析的CQ码
     */
    override fun sendMsg(msg: String): String {
        val msgChain = DingCatUtil.msgToDing(msg)
        return sendMsg(msgChain)
    }

    /**
     * 发送消息
     * @param msg 发送的消息.[DingSpecialMessageChain]
     */
    override fun sendMsg(msg: DingSpecialMessageChain): String {
        val jsonSerializer = jsonSerializerFactory.getJsonSerializer(Map::class.java)
        return send(jsonSerializer.toJson(msg.data)) ?: ""
    }


}

