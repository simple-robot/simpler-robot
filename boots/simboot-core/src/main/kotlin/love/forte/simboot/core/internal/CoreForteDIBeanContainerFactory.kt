/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
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
