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
 */

package love.forte.simboot.autoconfigure

import org.springframework.beans.factory.BeanClassLoaderAware
import org.springframework.context.annotation.ImportSelector
import org.springframework.core.io.UrlResource
import org.springframework.core.io.support.PropertiesLoaderUtils
import org.springframework.core.type.AnnotationMetadata
import java.util.*

/**
 * 追加载入 `META-INF/simbot.factories` 中 `simbot.includes` 的数据.
 * @author ForteScarlet
 */
public open class SimbotIncludesSelector : ImportSelector, BeanClassLoaderAware {
    private lateinit var classLoader: ClassLoader

    override fun selectImports(importingClassMetadata: AnnotationMetadata): Array<String> {
        val includes = mutableListOf<String>()

        // simbot.includes
        val resources = classLoader.getResources(SIMBOT_FACTORIES_PATH)
        resources.asSequence().map(::UrlResource).forEach {
            val properties: Properties = PropertiesLoaderUtils.loadProperties(it)
            (properties["simbot.includes"] as? String)
                ?.split(",")
                ?.mapNotNull { s -> s.trim().takeIf(String::isNotEmpty) }
                ?.also(includes::addAll)
        }

        return includes.toTypedArray()
    }

    override fun setBeanClassLoader(classLoader: ClassLoader) {
        this.classLoader = classLoader
    }

    public companion object {
        private const val SIMBOT_FACTORIES_PATH = "META-INF/simbot.factories"
    }
}