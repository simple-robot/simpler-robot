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

package love.forte.simbot.core.configuration

import love.forte.common.annotation.Ignore
import love.forte.common.ioc.DependBeanFactory
import love.forte.common.ioc.annotation.Beans
import love.forte.common.ioc.annotation.Depend
import love.forte.common.utils.annotation.AnnotationUtil
import love.forte.common.utils.convert.ConverterManager
import love.forte.simbot.annotation.Listens
import love.forte.simbot.core.TypedCompLogger
import love.forte.simbot.core.listener.MethodListenerFunction
import love.forte.simbot.filter.FilterManager
import love.forte.simbot.listener.ListenerRegistrar
import love.forte.simbot.listener.ListenerResultFactory
import love.forte.simbot.listener.PostListenerRegistrar


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

    /**
     * 扫描并注册监听函数。
     */
    override fun registerListenerFunctions(registrar: ListenerRegistrar) {

        logger.info("Ready to register method listeners.")
        if (!logger.isDebugEnabled) {
            logger.info("If you want to view the details, please enable log debug.")
        }

        // val scanner: Scanner<String, Class<*>> = HutoolClassesScanner()
        //
        // val scanPackages = packageScanEnvironment.scanPackages

        // logger.debug("listener scan packages: {}", scanPackages.joinToString(", ", "[", "]"))

        // 扫描所有的class, 然后筛选method
        // scanPackages.forEach {
        //     scanner.scan(it)
        // }

        // 获取所有已经加载的依赖信息并扫描
        val allBeans = dependBeanFactory.allBeans

        logger.debug("Number of beans to be scanned: {}", allBeans.size)

        allBeans.asSequence().mapNotNull { beanName ->
            runCatching {
                dependBeanFactory.getType(beanName)
            }.getOrElse { e ->
                logger.warn("Can not get type from depend '{}'. This may be an environmental issue or a class loader issue.",
                    beanName)
                logger.warn("The scan of the listener function for '{}' will be ignored.", beanName)
                logger.debug("Get type from depend '$beanName' failed.", e)
                null
            }
        }.distinct().flatMap {
            // 只获取public方法
            it.methods.asSequence().filter { m ->
                AnnotationUtil.containsAnnotation(m.declaringClass, Listens::class.java) ||
                        (!AnnotationUtil.containsAnnotation(m, Ignore::class.java) &&
                                AnnotationUtil.containsAnnotation(m, Listens::class.java))
            }
        }.map {
            MethodListenerFunction(it, dependBeanFactory, filterManager, converterManager, listenerResultFactory)
        }.forEach {
            registrar.register(it)
            logger.debug(
                "Register listener: [{}] for {}, id={}",
                it.name,
                it.listenTypes.joinToString(", ", "[", "]") { t -> t.simpleName },
                it.id
            )
        }

        AnnotationUtil.cleanCache()

        logger.info("Method listeners Registration is complete.")


    }

}