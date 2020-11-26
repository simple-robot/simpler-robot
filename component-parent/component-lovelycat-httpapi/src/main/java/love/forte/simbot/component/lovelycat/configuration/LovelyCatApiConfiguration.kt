/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     LovelyCatApiConfiguration.kt
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

package love.forte.simbot.component.lovelycat.configuration

import love.forte.common.ioc.annotation.Beans
import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.simbot.component.lovelycat.LovelyCatApiTemplate
import love.forte.simbot.component.lovelycat.LovelyCatApiTemplateImpl
import love.forte.simbot.http.template.HttpTemplate
import love.forte.simbot.serialization.json.JsonSerializerFactory


@ConfigBeans
public class LovelyCatApiConfiguration {


    // TODO 一个bot一个api
    fun lovelyCatApiTemplate(
        httpTemplate: HttpTemplate,
        jsonSerializerFactory: JsonSerializerFactory
    ): LovelyCatApiTemplate {
        return LovelyCatApiTemplateImpl(
            httpTemplate,
            //TODO
            "http://127.0.0.1:88/httpAPI",
            jsonSerializerFactory
        )
    }

}