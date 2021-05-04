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

package love.forte.simbot.component.mirai.configuration

import io.ktor.client.*
import io.ktor.client.features.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.utils.io.jvm.javaio.*
import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.simbot.api.sender.GetterFactory
import love.forte.simbot.api.sender.SenderFactory
import love.forte.simbot.api.sender.SetterFactory
import love.forte.simbot.component.mirai.message.MiraiMessageCache
import love.forte.simbot.component.mirai.sender.MiraiGetterFactory
import love.forte.simbot.component.mirai.sender.MiraiSenderFactory
import love.forte.simbot.component.mirai.sender.MiraiSetterFactory
import love.forte.simbot.core.SimbotContext
import love.forte.simbot.core.SimbotContextClosedHandle
import love.forte.simbot.core.TypedCompLogger
import love.forte.simbot.core.configuration.ComponentBeans
import love.forte.simbot.http.template.HttpTemplate
import love.forte.simbot.processor.RemoteResourceContext
import love.forte.simbot.processor.RemoteResourceInProcessor
import love.forte.simbot.processor.SuspendRemoteResourceInProcessor
import love.forte.simbot.utils.onShutdown
import java.io.InputStream

/**
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@ConfigBeans("miraiSenderFactoriesConfiguration")
public class MiraiSenderFactoriesConfiguration {

    /**
     * 默认的资源处理器使用 [MiraiRemoteResourceInProcessor]
     */
    @ComponentBeans("miraiRemoteResourceInProcessor")
    fun miraiRemoteResourceInProcessor(): MiraiRemoteResourceInProcessor = MiraiRemoteResourceInProcessor

    @ComponentBeans("miraiSenderFactory")
    fun miraiSenderFactory(
        cache: MiraiMessageCache,
        remoteResourceInProcessor: RemoteResourceInProcessor,
    ): SenderFactory = MiraiSenderFactory(cache, remoteResourceInProcessor)

    @ComponentBeans("miraiSetterFactory")
    fun miraiSetterFactory(): SetterFactory = MiraiSetterFactory

    @ComponentBeans("miraiGetterFactory")
    fun miraiGetterFactory(http: HttpTemplate): GetterFactory = MiraiGetterFactory(http)


}


/**
 * mirai组件下所使用的资源获取器。
 */
public object MiraiRemoteResourceInProcessor : TypedCompLogger(MiraiRemoteResourceInProcessor::class.java),
    SuspendRemoteResourceInProcessor, SimbotContextClosedHandle {
    /**
     * ktor http client
     */
    private val httpClient: HttpClient by lazy {
        HttpClient() {
            install(HttpTimeout) {
                requestTimeoutMillis = 30_000
                connectTimeoutMillis = 20_000
            }
        }.also {
            onShutdown { it.close() }
        }
    }

    override val handleName: String
        get() = "MiraiRemoteResourceInProcessorHandle"


    override fun simbotClose(context: SimbotContext) {
        httpClient.close()
    }

    override suspend fun suspendableProcessor(processContext: RemoteResourceContext): InputStream {
        val urlString = processContext.link
        val url = Url(processContext.link)
        val response = httpClient.get<HttpResponse>(url)
        val status = response.status
        if (status.value < 300) {
            // success
            return response.content.toInputStream()
        } else {
            throw IllegalStateException("Connection to '$urlString' failed. ${status.value}: ${status.description}")
        }
    }

}

