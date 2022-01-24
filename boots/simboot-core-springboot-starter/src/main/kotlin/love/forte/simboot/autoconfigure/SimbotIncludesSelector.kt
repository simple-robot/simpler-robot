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