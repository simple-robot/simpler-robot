/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     MiraiKtorConfiguration.kt
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

package love.forte.simbot.component.mirai.configuration

import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import love.forte.common.ioc.annotation.Beans
import love.forte.common.ioc.annotation.ConfigBeans


@ConfigBeans
public class MiraiKtorConfiguration {

    @Beans("ktorOkHttpClient")
    public fun ktorOkHttpClient() : HttpClientEngineFactory<*> = OkHttp

}