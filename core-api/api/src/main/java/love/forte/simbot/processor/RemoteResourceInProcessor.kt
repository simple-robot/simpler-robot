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

@file:JvmName("RemoteResourceInProcessors")
package love.forte.simbot.processor

import kotlinx.coroutines.runBlocking
import love.forte.simbot.BaseContext
import love.forte.simbot.Context
import java.io.IOException
import java.io.InputStream
import java.net.URL


/**
 * 一个远程资源信息，实际上就是一个 [链接][link]。
 *
 * 远程资源信息也属于一种 [上下文][Context], 用于在 [远程资源处理器][RemoteResourceInProcessor] 中使用。
 *
 * @property link 一般代表一个远程资源的链接。
 * @property id 有可能会存在的ID, 这取决于组件或发送者所发送的CAT码参数。
 *
 */
public data class RemoteResourceContext(val link: String, val id: String? = null) : BaseContext<String>(link)


/**
 *
 * 一个 **远程资源** 处理器，服务于组件，用于自定义获取一个远程的数据资源。
 * 目前可使用于获取远程图片的输入流。
 *
 *
 *
 *
 * @see SuspendRemoteResourceInProcessor 可支持携程的处理器。需要组件自行判断。
 *
 */
public interface RemoteResourceInProcessor : Processor<String, RemoteResourceContext, InputStream> {
    /**
     * 通过远程资源信息得到一个输入流。
     */
    @Throws(IOException::class)
    override fun processor(processContext: RemoteResourceContext): InputStream

    /** Default */
    companion object Default : RemoteResourceInProcessor by BlockingProcessor
}


/**
 * [RemoteResourceInProcessor] 的子实现，提供 [suspendableProcessor] 以支持携程。
 */
public interface SuspendRemoteResourceInProcessor : RemoteResourceInProcessor, SuspendableProcessor<String, RemoteResourceContext, InputStream> {

    /**
     * 通过远程资源信息得到一个输入流。
     * 默认的实现是通过阻塞 [suspendableProcessor] 实现的。
     */
    override fun processor(processContext: RemoteResourceContext): InputStream = runBlocking { suspendableProcessor(processContext) }


    /**
     * 一个支持 suspend 函数的处理器，根据参数获取一个 [InputStream].
     */
    override suspend fun suspendableProcessor(processContext: RemoteResourceContext): InputStream
}


/**
 * 默认的通过 [URL].[openStream][URL.openStream] 来获取一个远程连接的输入流。
 */
private object BlockingProcessor : RemoteResourceInProcessor {
    @Throws(IOException::class)
    override fun processor(processContext: RemoteResourceContext): InputStream = URL(processContext.link).openStream()
}


/**
 * 如果是一个 [SuspendRemoteResourceInProcessor] 则使用 [SuspendRemoteResourceInProcessor.suspendableProcessor],
 * 否则使用 [RemoteResourceInProcessor.processor]
 */
@Suppress("BlockingMethodInNonBlockingContext")
public suspend fun RemoteResourceInProcessor.doProcess(context: RemoteResourceContext): InputStream =
    if (this is SuspendRemoteResourceInProcessor) suspendableProcessor(context)
    else processor(context)

