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



