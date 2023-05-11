/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simboot.spring.autoconfigure

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
