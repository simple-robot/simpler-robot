/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  component-ding
 * File     dingBuilder.kt
 * Date  2020/8/8 下午8:41
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.component.ding.sender

import love.forte.simbot.component.ding.sceret.DingSecretCalculator
import love.forte.simbot.http.template.HttpTemplate
import love.forte.simbot.serialization.json.JsonSerializerFactory


/**
 * [DingSenderBuilder]使用的参数承载类
 */
data class DingSenderInfo(
    val accessToken: String,
    val webhook: String,
    val secret: String?,
    val secretCalculator: DingSecretCalculator,
    val httpTemplate: HttpTemplate,
    val jsonSerializerFactory: JsonSerializerFactory
)

/**
 * DingSender构建器
 */
interface DingSenderBuilder {
    fun getDingSender(info: DingSenderInfo): DingSender
}

/**
 * [DingSenderBuilder]的默认实现类，使用的是[DingSenderImpl]
 */
object DingSenderBuilderImpl : DingSenderBuilder {
    override fun getDingSender(info: DingSenderInfo): DingSender {
        // 完整的webhook，携带access_token
        val webhookIntegral = "${info.webhook}?access_token=${info.accessToken}"

        return DingSenderImpl(webhookIntegral, info.secret, info.secretCalculator, info.httpTemplate, info.jsonSerializerFactory)
    }
}