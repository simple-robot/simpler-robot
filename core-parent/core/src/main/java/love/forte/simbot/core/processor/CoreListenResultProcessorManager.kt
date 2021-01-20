/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     CoreListenResultProcessorManager.kt
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

package love.forte.simbot.core.processor

import love.forte.common.ioc.DependBeanFactory
import love.forte.common.ioc.annotation.SpareBeans
import love.forte.simbot.core.TypedCompLogger
import love.forte.simbot.processor.ListenResultProcessor
import love.forte.simbot.processor.ListenResultProcessorContext
import love.forte.simbot.processor.ListenResultProcessorManager


/**
 *
 * 监听响应处理管理器。
 *
 * @author ForteScarlet
 * @since 2.0.0-RC.3
 */
@SpareBeans("coreListenResultProcessorManager")
public class CoreListenResultProcessorManager(
    private val dependBeanFactory: DependBeanFactory
) : ListenResultProcessorManager {
    private companion object : TypedCompLogger(ListenResultProcessorManager::class.java)

    private var processorNames: List<String>? = null

    private val processors: List<ListenResultProcessor> by lazy {
        val processorNameList = processorNames ?: emptyList()
        processorNames = null
        processorNameList.map {
            (dependBeanFactory[it] as ListenResultProcessor).also { pro ->
                logger.debug("Init listen result processor: $pro")
            }
        }.sortedBy { it.priority }
    }

    init {
        val allBeans = dependBeanFactory.allBeans

        processorNames = allBeans.mapNotNull {
            val type = dependBeanFactory.getType(it)
            val isProcessor = ListenResultProcessor::class.java.isAssignableFrom(type)
            if (isProcessor) it else null
        }

    }


    /**
     * 通过监听函数处理上下文进行处理。
     */
    override fun processor(context: ListenResultProcessorContext) {
        processors.forEach { it.processor(context) }
    }
}