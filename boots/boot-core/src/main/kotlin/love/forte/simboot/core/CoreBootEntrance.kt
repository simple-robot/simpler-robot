/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simboot.core

import love.forte.annotationtool.core.KAnnotationTool
import love.forte.di.BeanContainer
import love.forte.simboot.*
import love.forte.simboot.core.internal.CoreBootEntranceContextImpl
import love.forte.simboot.factory.BeanContainerFactory
import love.forte.simboot.factory.BotRegistrarFactory
import love.forte.simboot.factory.ConfigurationFactory
import love.forte.simbot.event.EventListenerManager
import org.slf4j.Logger
import kotlin.reflect.KClass


public interface CoreBootEntranceContext {


    /**
     * [Configuration] 工厂.
     */
    public fun getConfigurationFactory(): ConfigurationFactory

    /**
     * [BeanContainer] 工厂.
     */
    public fun getBeanContainerFactory(): BeanContainerFactory


    /**
     * 读取所有的bot配置文件信息。
     */
    public fun getAllBotInfos(
        configuration: Configuration,
        beanContainer: BeanContainer
    ): Map<String, List<Map<String, String>>>


    /**
     * 通过 [BeanContainer] 最终得到一个 [EventListenerManager].
     */
    public fun getListenerManager(beanContainer: BeanContainer): EventListenerManager


    /**
     * 启动命令参数。
     */
    public val args: Array<String>

    /**
     * 由boot所提供的日志。
     */
    public val logger: Logger
}


/**
 *
 * 由 `boot-core` 提供的基础boot入口。
 *
 * [CoreBootEntrance] 提供Java包路径扫描，并解析加载一切内容，提供依赖注入功能.
 *
 * @author ForteScarlet
 */
public class CoreBootEntrance : SimBootEntrance {
    public companion object {
        internal val annotationTool: KAnnotationTool = KAnnotationTool()
    }

    override fun run(context: SimBootEntranceContext): SimbootContext {
        val bootContext: CoreBootEntranceContext = context.toCoreBootEntranceContext()
        // 获取所有配置
        val configuration = bootContext.getConfigurationFactory()(context)

        // 初始化 bean container
        val beanContainer = bootContext.getBeanContainerFactory()(configuration)

        // 初始化 listener manager -> listener manager factory
        val manager = bootContext.getListenerManager(beanContainer)

        // 获取所有的 BotRegistrar -> BotRegistrarFactory
        val allBotRegistrarFactoryName = beanContainer.getAll(BotRegistrarFactory::class)
        // all registrars and group by component name.
        val allRegistrars = allBotRegistrarFactoryName
            .map { name -> beanContainer[name, BotRegistrarFactory::class] }
            .map { it() }

        // 所有的binder

        // 所有的 listener function
        //     listener解析, 同时解析 filter, 以及部分拦截器等.
        //     listener binder组装,


        // 注册bot


        TODO("Not yet implemented")
    }
}


private fun SimBootEntranceContext.toCoreBootEntranceContext(): CoreBootEntranceContext {
    return when (val app = application) {
        null -> throw SimBootApplicationException("CoreBootEntrance does not allow application to be null.")
        is CoreBootEntranceContext -> app
        is KClass<*> -> app.classToCoreBootEntranceContext(this)
        is Class<*> -> app.kotlin.classToCoreBootEntranceContext(this)
        else -> throw SimBootApplicationException(
            """
            CoreBootEntrance application only supports the following possible types:
            - An instance of [love.forte.simboot.core.CoreBootEntranceContext].
            - A (K)Class instance annotated @SimBootApplication(...).
            But not $app (${app::class}) you provided.
        """.trimIndent()
        )
    }
}


private fun KClass<*>.classToCoreBootEntranceContext(context: SimBootEntranceContext): CoreBootEntranceContext {
    // get annotation
    val tool = CoreBootEntrance.annotationTool
    val applicationAnnotation = tool.getAnnotation(this, SimbootApplication::class)
        ?: throw SimBootApplicationException("Application [$this] is not annotated @SimBootApplication.")

    return CoreBootEntranceContextImpl(applicationAnnotation, this, context)


}

