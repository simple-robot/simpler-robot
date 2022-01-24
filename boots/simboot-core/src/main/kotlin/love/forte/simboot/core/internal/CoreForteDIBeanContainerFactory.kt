/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simboot.core.internal

import love.forte.annotationtool.core.KAnnotationTool
import love.forte.di.BeanContainer
import love.forte.di.core.coreBeanClassRegistrar
import love.forte.di.core.coreBeanManager
import love.forte.di.core.internal.AnnotationGetter
import love.forte.simboot.Configuration
import love.forte.simboot.factory.BeanContainerFactory
import love.forte.simbot.LoggerFactory
import javax.inject.Named
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

/**
 *
 * 基于 `forte-DI` 的 bean container.
 *
 * @author ForteScarlet
 */
internal class CoreForteDIBeanContainerFactory(
    private val annotationGetter: AnnotationGetter,
    private val classGetter: () -> Collection<KClass<*>>,
    private val includeClasses: Set<String>
) : BeanContainerFactory {
    private val logger = LoggerFactory.getLogger(CoreForteDIBeanContainerFactory::class)

    override fun invoke(configuration: Configuration): BeanContainer {
        val annotationTool = KAnnotationTool(mutableMapOf(), mutableMapOf())
        val loader = CoreForteDIBeanContainerFactory::class.java.classLoader

        val registrar = coreBeanClassRegistrar(annotationGetter)
        val manager = coreBeanManager { }
        if (includeClasses.isNotEmpty()) {
            registrar.register(*includeClasses.mapNotNull {
                val kClass = loader.loadClass(it).kotlin
                if (annotationTool.getAnnotation(kClass, Named::class) != null) kClass.also {
                    logger.debug("Include class [{}]", kClass.qualifiedName ?: kClass.jvmName)
                }
                else null
            }.toTypedArray())
        }
        registrar.register(*classGetter().filter { annotationTool.getAnnotation(it, Named::class) != null }.toTypedArray()).inject(manager)
        return manager
    }
}
