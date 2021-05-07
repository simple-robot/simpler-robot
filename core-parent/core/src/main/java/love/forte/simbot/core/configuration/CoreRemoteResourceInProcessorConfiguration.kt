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

package love.forte.simbot.core.configuration

import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.common.ioc.annotation.SpareBeans
import love.forte.simbot.processor.RemoteResourceInProcessor


/**
 * 核心所提供的远程资源处理器配置类.
 * @author ForteScarlet
 */
@ConfigBeans
public class CoreRemoteResourceInProcessorConfiguration {

    /**
     * 默认使用 [RemoteResourceInProcessor] 的默认实现。
     */
    @SpareBeans("coreRemoteResourceInProcessor")
    fun remoteResourceInProcessor(): RemoteResourceInProcessor = RemoteResourceInProcessor
}