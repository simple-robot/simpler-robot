/*
 *  Copyright (c) 2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

import love.forte.simboot.core.internal.ResourcesScanner
import love.forte.simboot.core.internal.visitJarEntry
import love.forte.simboot.core.internal.visitPath

/*
 *  Copyright (c) 2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

private object O

fun main() {
    // from resources
    val pathReplace = Regex("[/\\\\]")


    val classes = ResourcesScanner<Class<*>>().use { scanner ->
        scanner.scan("love")
            .glob("love/forte**.class")
            .visitPath {
                println("path: $it")
                emptySequence()
            }
            .visitJarEntry { entry, _ ->
                val classname = entry.name.replace(pathReplace, ".").substringBefore(".class")
                sequenceOf(scanner.classLoader.loadClass(classname))
            }
            .collect(true, mutableListOf())



    }


}