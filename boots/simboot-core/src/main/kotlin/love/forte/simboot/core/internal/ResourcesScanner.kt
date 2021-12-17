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

import love.forte.simboot.core.internal.ResourcesScanner.ResourceVisitValue.JarEntryValue
import love.forte.simboot.core.internal.ResourcesScanner.ResourceVisitValue.PathValue
import love.forte.simboot.utils.Globs
import java.io.Closeable
import java.net.JarURLConnection
import java.net.URL
import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import java.util.jar.JarEntry
import kotlin.io.path.Path
import kotlin.io.path.relativeTo
import kotlin.io.path.toPath

/**
 *
 * 通过 [ClassLoader.getResource] 加载符合匹配要求的内容。
 * 扫描可能是项目根目录下的内容或者jar内的内容。
 *
 *
 * @author ForteScarlet
 */
public class ResourcesScanner<T>(
    public var classLoader: ClassLoader = ResourcesScanner::class.java.classLoader
) : Closeable {
    private val globs = mutableSetOf<Regex>()
    private val scans = mutableSetOf<String>()
    private val lookups = mutableListOf<(URL) -> Sequence<T>>()
    private val visitors = mutableListOf<(ResourceVisitValue<*>) -> Sequence<T>>()

    override fun close() {
        clear()
    }

    private fun clear() {
        globs.clear()
        visitors.clear()
        scans.clear()
    }


    public fun glob(glob: String): ResourcesScanner<T> = also {
        if (globs.add(Regex(Globs.toRegex(glob), setOf(RegexOption.IGNORE_CASE)))) {
            lookups.add { url ->
                when (val protocol = url.protocol) {
                    "file" -> sequence {
                        // file, relative to ROOT
                        val startPath = url.toURI().toPath().relativeTo(PROJECT_ROOT)
                        val seqList = mutableListOf<Sequence<T>>()
                        Files.walkFileTree(startPath, object : SimpleFileVisitor<Path>() {
                            override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {

                                if (globs.any { r ->
                                        // println("[$r].matches($file) = ${r.matches(file.toString())}")
                                        r.matches(file.toString())
                                    }) {
                                    visitors.forEach { v ->
                                        seqList.add(v(PathValue(file)))
                                    }
                                }
                                return FileVisitResult.CONTINUE
                            }
                        })
                        seqList.forEach { seq -> yieldAll(seq) }
                    }
                    "jar" -> sequence {
                        val connection = url.openConnection() as? JarURLConnection
                            ?: throw IllegalStateException("Resource URL $url open failure: protocol is 'jar' but cannot open as JarURLConnection.")
                        val jarFile = connection.jarFile
                        jarFile.entries().asSequence().forEach { entry ->
                            if (globs.any { r ->
                                    r.matches(entry.name) || r.matches(entry.name.replace("/", "\\"))
                                }) {
                                visitors.forEach { v ->
                                    yieldAll(v(JarEntryValue(entry, url)))
                                }
                            }
                        }
                    }
                    else -> throw UnsupportedOperationException("Not support url protocol: $protocol")
                }
            }
        }
    }

    /**
     * 访问经由globs过滤后的资源。
     */
    public fun visit(visitor: (ResourceVisitValue<*>) -> Sequence<T>): ResourcesScanner<T> = also {
        visitors.add(visitor)
    }

    public fun scan(resource: String): ResourcesScanner<T> = also {
        scans.add(resource)
    }


    private fun doCollect(model: ResourceModel, classLoader: ClassLoader): Sequence<T> {
        return scans.asSequence().flatMap { resource ->
            model.getResources(classLoader, resource).flatMap { url ->
                lookups.asSequence().flatMap { lookup -> lookup(url) }
            }
        }
    }


    @JvmOverloads
    public fun <C : MutableCollection<T>> collect(
        allResources: Boolean,
        collection: C,
        classLoader: ClassLoader = this.classLoader
    ): C {
        if (allResources) {
            doCollect(ResourceModel.All, classLoader).forEach(collection::add)
        } else {
            doCollect(ResourceModel.Current, classLoader).forEach(collection::add)
        }
        return collection
    }

    @JvmOverloads
    public fun collectSequence(allResources: Boolean, classLoader: ClassLoader = this.classLoader): Sequence<T> {
        return if (allResources) {
            doCollect(ResourceModel.All, classLoader)
        } else {
            doCollect(ResourceModel.Current, classLoader)
        }
    }


    public companion object {
        private val PROJECT_ROOT = Path("").toAbsolutePath() // project resources root
    }

    private sealed class ResourceModel {
        abstract fun getResources(classLoader: ClassLoader, resource: String): Sequence<URL>

        object Current : ResourceModel() {
            override fun getResources(classLoader: ClassLoader, resource: String): Sequence<URL> {
                return classLoader.getResource(resource)?.let { sequenceOf(it) } ?: emptySequence()
            }
        }

        object All : ResourceModel() {
            override fun getResources(classLoader: ClassLoader, resource: String): Sequence<URL> {
                return classLoader.getResources(resource).asSequence()
            }
        }
    }


    /**
     * 访问被扫描的URL最终的内容。
     *
     * 此类型下目前有如下可能：
     * - 被访问的是本地的 [Path] 路径（相对）：[PathValue]
     * - 被访问的是一个[JarEntry]: [JarEntryValue]
     *
     */
    public sealed class ResourceVisitValue<T> {
        public abstract val value: T

        public class PathValue internal constructor(override val value: Path) : ResourceVisitValue<Path>()
        public class JarEntryValue internal constructor(override val value: JarEntry, public val url: URL) : ResourceVisitValue<JarEntry>()
    }

}


public fun <T> ResourcesScanner<T>.visitPath(visitor: (Path) -> Sequence<T>): ResourcesScanner<T> = visit {
    if (it is PathValue) visitor(it.value)
    else emptySequence()
}

public fun <T> ResourcesScanner<T>.visitJarEntry(visitor: (JarEntry, URL) -> Sequence<T>): ResourcesScanner<T> = visit {
    if (it is JarEntryValue) visitor(it.value, it.url)
    else emptySequence()
}

public fun <T> ResourcesScanner<T>.toMutableList(allResources: Boolean): MutableList<T> =
    collect(allResources, mutableListOf())

public fun <T> ResourcesScanner<T>.toList(allResources: Boolean): List<T> =
    collect(allResources, mutableListOf()).let { list ->
        when {
            list.isEmpty() -> emptyList()
            list.size == 1 -> listOf(list[0])
            else -> list
        }
    }