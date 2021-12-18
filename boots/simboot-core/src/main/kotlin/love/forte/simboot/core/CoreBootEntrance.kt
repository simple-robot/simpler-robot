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
import love.forte.di.allInstance
import love.forte.simboot.*
import love.forte.simboot.core.filter.KeywordBinderFactory
import love.forte.simboot.core.internal.CoreBootEntranceContextImpl
import love.forte.simboot.core.listener.EventParameterBinderFactory
import love.forte.simboot.factory.BeanContainerFactory
import love.forte.simboot.factory.BotRegistrarFactory
import love.forte.simboot.factory.ConfigurationFactory
import love.forte.simboot.listener.ParameterBinderFactory
import love.forte.simbot.*
import love.forte.simbot.event.EventListenerManager
import love.forte.simbot.utils.asCycleIterator
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
public class CoreBootEntrance : SimbootEntrance {
    public companion object {
        internal val annotationTool: KAnnotationTool = KAnnotationTool()
    }

    override fun run(context: SimbootEntranceContext): SimbootContext {
        val bootContext: CoreBootEntranceContext = context.toCoreBootEntranceContext()
        // 获取所有配置
        val configuration = bootContext.getConfigurationFactory()(context)

        // 初始化 bean container
        val beanContainer = bootContext.getBeanContainerFactory()(configuration)

        // 初始化 listener manager -> listener manager factory
        val manager = bootContext.getListenerManager(beanContainer)

        // 获取所有的 BotRegistrar -> BotRegistrarFactory
        val allBotRegistrarFactories = beanContainer.allInstance<BotRegistrarFactory>()

        // all registrars and group by component name.
        val allRegistrars = allBotRegistrarFactories.map { it(manager) }

        // group by component.
        val allGroupedRegistrars: Map<CharSequenceID, BotRegistrar> =
            allRegistrars.groupBy { r -> r.component.id }.mapValues { (key, values) ->
                if (values.size != 1) {
                    context.logger.warn("There are multiple registrars under the component [{}], and they will be registered sequentially in a balanced manner.", key)

                    TODO()
                } else values[0]
            }

        // 所有的binder factory
        val baseBinderFactoryList = mutableListOf<ParameterBinderFactory>(
            KeywordBinderFactory,
            //EventParameterBinderFactory, // beanContainer binder
            EventParameterBinderFactory, // event binder
        )

        // 所有的 listener function
        //     listener解析, 同时解析 filter, 以及部分拦截器等.
        //     listener binder组装,


        // 扫描.bot
        // 注册bot



        TODO("Not yet implemented")
    }
}


private fun SimbootEntranceContext.toCoreBootEntranceContext(): CoreBootEntranceContext {
    return when (val app = application) {
        null -> throw SimbootApplicationException("CoreBootEntrance does not allow application to be null.")
        is CoreBootEntranceContext -> app
        is KClass<*> -> app.classToCoreBootEntranceContext(this)
        is Class<*> -> app.kotlin.classToCoreBootEntranceContext(this)
        else -> throw SimbootApplicationException(
            """
            CoreBootEntrance application only supports the following possible types:
            - An instance of [love.forte.simboot.core.CoreBootEntranceContext].
            - A (K)Class instance annotated @SimBootApplication(...).
            But not $app (${app::class}) you provided.
        """.trimIndent()
        )
    }
}


private fun KClass<*>.classToCoreBootEntranceContext(context: SimbootEntranceContext): CoreBootEntranceContext {
    // get annotation
    val tool = CoreBootEntrance.annotationTool
    val applicationAnnotation = tool.getAnnotation(this, SimbootApplication::class)
        ?: throw SimbootApplicationException("Application [$this] is not annotated @SimBootApplication.")

    return CoreBootEntranceContextImpl(applicationAnnotation, this, context)


}

private class BalancedBotRegistrar(
    override val component: Component,
    private val registrars: List<BotRegistrar>
    ): BotRegistrar {
    init {
        if (registrars.isEmpty()) {
            throw SimbotIllegalArgumentException("Registrars cannot be empty.")
        }

        registrars.forEachIndexed { i, it ->
            Simbot.require(component like it.component) { "Component of registrar $it index $i != target component $component" }
        }
    }

    private val iter = registrars.toList().asCycleIterator()

    override fun register(verifyInfo: BotVerifyInfo): Bot {
        return iter.next().register(verifyInfo)
    }
}

