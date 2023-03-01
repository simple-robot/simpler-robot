/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

@file:JvmName("VersionUtil")

package love.forte.simboot.utils

import java.util.*

/**
 * 通过 [dependencyInfo] 得到的依赖信息。
 */
public class DependencyInfo(public val properties: Properties) {
    public val version: String get() = properties.getProperty("version")
}


/**
 * 尝试获取指定依赖的版本信息。如果获取不到或出现异常，将会直接得到null。
 */
public fun dependencyInfo(group: String, id: String, loader: ClassLoader? = null): DependencyInfo? {
    val classLoader = loader ?: loader()
    return runCatching {
        val path = "META-INF/maven/$group/$id/pom.properties"

        val pomProperties: Properties? =
            (classLoader.getResource(path) ?: classLoader.getResource("/$path"))?.openStream()
                ?.use { input -> Properties().apply { load(input) } }

        if (pomProperties != null) {
            return@runCatching DependencyInfo(pomProperties)
        }

        // TODO simbot support

        null
    }.getOrNull()
}

private fun loader(): ClassLoader {
    return Thread.currentThread().contextClassLoader ?: ClassLoader.getSystemClassLoader()
}



