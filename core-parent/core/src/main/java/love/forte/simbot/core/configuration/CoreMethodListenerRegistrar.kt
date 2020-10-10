/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     CoreMethodListenerRegistrar.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.configuration

import love.forte.common.configuration.annotation.AsConfig
import love.forte.common.configuration.annotation.ConfigInject
import love.forte.common.ioc.DependBeanFactory
import love.forte.common.ioc.annotation.Beans
import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.common.ioc.annotation.Depend
import love.forte.common.ioc.annotation.PostPass
import love.forte.common.utils.annotation.AnnotationUtil
import love.forte.common.utils.convert.ConverterManager
import love.forte.common.utils.scanner.HutoolClassesScanner
import love.forte.common.utils.scanner.Scanner
import love.forte.simbot.core.CompLogger
import love.forte.simbot.core.SimbotPackageScanEnvironment
import love.forte.simbot.core.annotation.Listens
import love.forte.simbot.core.filter.FilterManager
import love.forte.simbot.core.listener.ListenerRegistrar
import love.forte.simbot.core.listener.MethodListenerFunction


@ConfigBeans
@AsConfig(prefix = "simbot.core")
public class CoreMethodListenerRegistrar {
    private companion object : CompLogger("ListenerRegistrarConfiguration")


    @Depend
    private lateinit var packageScanEnvironment: SimbotPackageScanEnvironment

    @Depend
    private lateinit var dependBeanFactory: DependBeanFactory

    @Depend
    private lateinit var filterManager: FilterManager

    @Depend
    private lateinit var converterManager: ConverterManager

    /**
     * 扫描并注册监听函数。
     */
    @PostPass
    fun registerMethodListenerFunctions(registrar: ListenerRegistrar) {

        logger.debug("ready to scan listeners.")

        val scanner: Scanner<String, Class<*>> = HutoolClassesScanner()

        val scanPackages = packageScanEnvironment.scanPackages

        logger.debug("listener scan packages: {}", scanPackages.joinToString(", ", "[", "]"))

        // 扫描所有的class, 然后筛选method
        scanPackages.forEach {
            scanner.scan(it)
        }

        val collection = scanner.collection

        collection.asSequence().flatMap {
            // 只获取public方法
            it.methods.asSequence().filter { m ->
                AnnotationUtil.containsAnnotation(m, Listens::class.java)
            }
        }.map {
            MethodListenerFunction(it, dependBeanFactory, filterManager, converterManager)
        }.forEach {
            registrar.register(it)
            logger.debug("register listener: {0} for {1}", it.name, it.listenTypes.joinToString(", ", "[", "]"){ t -> t.simpleName })
        }
    }

}