/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
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



