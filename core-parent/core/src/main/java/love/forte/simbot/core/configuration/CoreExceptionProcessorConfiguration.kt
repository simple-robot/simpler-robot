/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     CoreExceptionPrecessorConfiguration.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.configuration

import love.forte.common.ioc.DependBeanFactory
import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.common.ioc.annotation.Depend
import love.forte.simbot.core.exception.ExceptionHandle
import love.forte.simbot.core.exception.ExceptionProcessor
import love.forte.simbot.core.exception.ExceptionProcessorBuilder
import love.forte.simbot.core.exception.ExceptionProcessorBuilderImpl


/**
 * 配置异常处理器。
 */
@ConfigBeans
public class CoreExceptionProcessorConfiguration {

    /**
     * 依赖工厂，用于获取所有的异常处理器。
     */
    @Depend
    private lateinit var dependBeanFactory: DependBeanFactory


    /**
     * 异常处理器builder [ExceptionProcessorBuilder]。
     */
    @CoreBeans
    fun coreExceptionProcessorBuilder(): ExceptionProcessorBuilder = ExceptionProcessorBuilderImpl()


    /**
     * 构建异常处理器。
     *
     * @see ExceptionProcessor
     *
     */
    @CoreBeans
    fun coreExceptionProcessor(builder: ExceptionProcessorBuilder): ExceptionProcessor {
        val eType = ExceptionHandle::class.java
        val handles: Collection<ExceptionHandle<*>> =
            dependBeanFactory.allBeans.mapNotNull {
                val type = dependBeanFactory.getType(it)
                if (eType.isAssignableFrom(type)) dependBeanFactory[it] as ExceptionHandle<*>
                else null
            }

        if (handles.isNotEmpty()) {
            builder.register(*handles.toTypedArray())
        }

        return builder.build()
    }


}