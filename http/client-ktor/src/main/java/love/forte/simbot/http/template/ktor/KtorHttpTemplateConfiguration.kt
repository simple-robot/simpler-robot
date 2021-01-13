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

package love.forte.simbot.http.template.ktor

import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.common.ioc.annotation.SpareBeans
import love.forte.simbot.serialization.json.JsonSerializerFactory

/**
 *
 *
 * 配置 ktor http template.
 *
 * @author ForteScarlet <ForteScarlet@163.com>
 * @date 2020/11/4
 */
@ConfigBeans
public class KtorHttpTemplateConfiguration {

    @SpareBeans("ktorHttpClientEngineFactory")
    public fun ktorHttpClientEngineFactory(): HttpClientEngineFactory<*> = CIO

    @SpareBeans("ktorHttpTemplate")
    public fun ktorHttpTemplate(engineFactory: HttpClientEngineFactory<*>, jsonSerializerFactory: JsonSerializerFactory) =
        KtorHttpTemplate(engineFactory, jsonSerializerFactory = jsonSerializerFactory)

}