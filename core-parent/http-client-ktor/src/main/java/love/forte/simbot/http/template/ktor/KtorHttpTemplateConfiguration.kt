/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     KtorHttpTemplateConfiguration.kt
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

package love.forte.simbot.http.template.ktor

import love.forte.common.ioc.annotation.Beans
import love.forte.common.ioc.annotation.ConfigBeans
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

    @Beans("ktorHttpTemplate")
    public fun ktorHttpTemplate(jsonSerializerFactory: JsonSerializerFactory) =
        KtorHttpTemplate(jsonSerializerFactory = jsonSerializerFactory)

}