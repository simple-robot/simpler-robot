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

import java.net.URL

/**
 *
 * 扫描所有的resources并根据blobs匹配结果。
 *
 * @author ForteScarlet
 */
public class ResourcesScanner2(private val classLoader: ClassLoader = ResourcesScanner2::class.java.classLoader) {
    private val blobs = mutableSetOf<String>()
    private val scans = mutableSetOf<String>()



    public fun <C: MutableCollection<C>> collect(allResources: Boolean, collection: C): C {



        return collection
    }

    private sealed class ResourceType {
        public abstract fun getResources(classLoader: ClassLoader, resource: String): Sequence<URL>

        private object Single : ResourceType() {
            override fun getResources(classLoader: ClassLoader, resource: String): Sequence<URL> {
                return classLoader.getResource(resource)?.let { sequenceOf(it) } ?: emptySequence()
            }
        }

        private object All : ResourceType() {
            override fun getResources(classLoader: ClassLoader, resource: String): Sequence<URL> {
                return classLoader.getResources(resource).asSequence()
            }
        }

    }
}