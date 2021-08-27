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

import love.forte.common.annotation.Ignore
import love.forte.common.ioc.DependBeanFactory
import love.forte.common.ioc.annotation.Beans
import love.forte.common.ioc.annotation.Depend
import love.forte.common.utils.annotation.AnnotationUtil
import love.forte.common.utils.convert.ConverterManager
import love.forte.simbot.annotation.Listens
import love.forte.simbot.api.SimbotInternalApi
import love.forte.simbot.core.TypedCompLogger
import love.forte.simbot.core.listener.FunctionFromClassListenerFunction
import love.forte.simbot.core.strict.StrictManager
import love.forte.simbot.filter.FilterManager
import love.forte.simbot.listener.ListenerGroupManager
import love.forte.simbot.listener.ListenerRegistrar
import love.forte.simbot.listener.ListenerResultFactory
import love.forte.simbot.listener.PostListenerRegistrar
import love.forte.simbot.utils.containsAnnotation
import kotlin.reflect.KVisibility
import kotlin.reflect.full.functions
import kotlin.reflect.full.valueParameters


private data class BeanNameType<T>(val name: String, val type: Class<T>)


@Beans("coreMethodPostListenerRegistrar")
public class CoreMethodPostListenerRegistrar : PostListenerRegistrar {
    private companion object : TypedCompLogger(CoreMethodPostListenerRegistrar::class.java)

    // @Depend
    // private lateinit var packageScanEnvironment: SimbotPackageScanEnvironment

    @Depend
    private lateinit var dependBeanFactory: DependBeanFactory

    @Depend
    private lateinit var filterManager: FilterManager

    @Depend
    private lateinit var converterManager: ConverterManager

    @Depend
    private lateinit var listenerResultFactory: ListenerResultFactory

    @OptIn(SimbotInternalApi::class, love.forte.simbot.api.SimbotExperimentalApi::class)
    @Depend
    private lateinit var listenerGroupManager: ListenerGroupManager

    @Depend
    private lateinit var strictManager: StrictManager
    /**
     * 扫描并注册监听函数。
     */
    override fun registerListenerFunctions(registrar: ListenerRegistrar) {

        logger.info("Ready to register method listeners.")
        if (!logger.isDebugEnabled) {
            logger.info("If you want to view the details, please enable log debug.")
        }

        // 获取所有已经加载的依赖信息并扫描
        val allBeans: Set<String> = dependBeanFactory.allBeans

        logger.debug("Number of beans to be scanned: {}", allBeans.size)

        allBeans.asSequence().mapNotNull { beanName ->
            runCatching {
                BeanNameType(beanName, dependBeanFactory.getType(beanName))
            }.getOrElse { e ->
                logger.warn("Can not get type from depend '{}'. This may be an environmental issue or a class loader issue.",
                    beanName)
                logger.warn("The scan of the listener function for '{}' will be ignored.", beanName)
                logger.debug("Get type from depend '$beanName' failed.", e)
                null
            }
        }.distinct().flatMap { (name, type) ->
            val kType = type.kotlin

            kotlin.runCatching { kType.functions }.getOrElse { e1 ->
                if (logger.isDebugEnabled) {
                    logger.warn("Cannot get type $kType functions, skip.", e1)
                } else {
                    logger.warn("Cannot get type {} functions because {}. skip.", kType, e1.toString())
                }
                emptyList()
            }.mapNotNull { f ->
                // 类上是否有
                val typeListen = kType.containsAnnotation<Listens>()
                // 函数上是否有
                val funcListen = (f.containsAnnotation<Listens>() && !f.containsAnnotation<Ignore>())

                // 如果函数上没有，类上有，函数叫toString或者hashcode，跳过
                if (typeListen && !funcListen) {
                    if(
                        (f.name == "toString" && f.valueParameters.isEmpty()) ||
                        (f.name == "hashCode" && f.valueParameters.isEmpty()) ||
                        (f.name == "equals" && with(f.valueParameters) { this.size == 1 })
                    ) {
                        return@mapNotNull null
                    }
                }

                val isListener = typeListen || funcListen

                if (isListener) {
                    if (f.visibility != KVisibility.PUBLIC) {
                        logger.warn("Function $f is marked as a Listener, but it is not a public function, ignored.")
                        null
                    } else {
                        FunctionFromClassListenerFunction(
                            function = f,
                            instanceName = name,
                            type = type,
                            dependBeanFactory = dependBeanFactory,
                            filterManager = filterManager,
                            converterManager = converterManager,
                            listenerResultFactory = listenerResultFactory,
                            listenerGroupManager = listenerGroupManager,
                            strictManager.coreStrict()
                        )
                    }
                } else null
            }
        }.forEach {
            registrar.register(it)
            logger.debug(
                "Register listener: [{}] for {}, id={}, groups={}",
                it.name,
                it.listenTypes.joinToString(", ", "[", "]") { t -> t.simpleName },
                it.id,
                it.groups.toSet()
            )
        }

        AnnotationUtil.cleanCache()

        logger.info("Method listeners Registration is complete.")


    }

}