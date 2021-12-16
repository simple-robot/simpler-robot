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

package love.forte.simboot.core.internal

import love.forte.simboot.BeanContainerFactory
import love.forte.simboot.SimBootEntranceContext
import love.forte.simboot.core.CoreBootEntranceContext
import love.forte.simboot.core.SimbootApplication
import org.slf4j.Logger
import kotlin.reflect.KClass

/**
 *
 * 通过 class 解析得到的 [CoreBootEntranceContext]
 *
 * @author ForteScarlet
 */
internal class CoreBootEntranceContextImpl(
    private val simBootApplicationAnnotationInstance: SimbootApplication,
    private val applicationClass: KClass<*>,
    private val context: SimBootEntranceContext
) : CoreBootEntranceContext {


    override val beanContainerFactory: BeanContainerFactory
        get() = TODO("Not yet implemented")

    override val args: Array<String>
        get() = context.args

    override val logger: Logger
        get() = TODO("Not yet implemented")
}


private fun packagesToClassesGetter(vararg scannerPackages: String): () -> Collection<KClass<*>> {
    if (scannerPackages.isEmpty()) return { emptyList() }

    // scanner.

    return {


        TODO()
    }
}


