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

package love.forte.simboot.core.utils

import love.forte.simboot.core.utils.ResourcesScanner.ResourceVisitValue.JarEntryValue
import love.forte.simboot.core.utils.ResourcesScanner.ResourceVisitValue.PathValue
import love.forte.simboot.utils.Globs
import java.io.Closeable
import java.io.File
import java.net.JarURLConnection
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.util.jar.JarEntry
import kotlin.io.path.*
import kotlin.streams.asSequence

/**
 *
 * 通过 [ClassLoader.getResource] 加载符合匹配要求的内容。
 * 扫描可能是项目根目录下的内容或者jar内的内容。
 *
 *
 * @author ForteScarlet
 */
public class ResourcesScanner<T>(
    public var classLoader: ClassLoader = ResourcesScanner::class.java.classLoader,
) : Closeable {
    private val fileSep get() = File.separator
    private val fileSepChar get() = File.separatorChar
    private val globs = mutableSetOf<Regex>()
    private val scans = mutableSetOf<String>()
    private val lookups =
        mutableListOf<(model: ResourceModel, resource: String, loader: ClassLoader, url: URL) -> Sequence<T>>()
    private val visitors = mutableListOf<(ResourceVisitValue<*>) -> Sequence<T>>()
    
    override fun close() {
        clear()
    }
    
    private fun clear() {
        globs.clear()
        visitors.clear()
        scans.clear()
    }
    
    private fun lookup(model: ResourceModel, resource: String, loader: ClassLoader, url: URL): Sequence<T> {
        return when (val protocol = url.protocol) {
            "file" -> sequence {
                val resourceReplace = resource.replace("/", fileSep)
                val startPath = url.toURI().toPath() //.relativeTo(base)
                if (startPath.isDirectory()) {
                    val deep = Files.list(startPath).asSequence().flatMap { path ->
                        if (path.isRegularFile()) {
                            val resourceFileName = "$resourceReplace$fileSep${path.name}"
                            // real file
                            if (globs.any { r ->
                                    r.matches(resourceFileName)
                                }) {
                                visitors.asSequence().flatMap { v ->
                                    v(PathValue(path, resourceFileName))
                                }
                            } else {
                                emptySequence()
                            }
                        } else {
                            val newResource = "$resourceReplace$fileSep${path.name}".let {
                                if (it.startsWith(fileSepChar)) it.substringAfter(fileSepChar) else it
                            }
                            doResourcesLookup(model, loader, newResource).flatMap { url ->
                                lookup(model, newResource, loader, url)
                            }
                        }
                    }
                    yieldAll(deep)
                } else {
                    // println("is file. $resource, $startPath")
                    // is File.
                    if (globs.any { r -> r.matches(resource) }) {
                        visitors.forEach { v ->
                            yieldAll(v(PathValue(startPath, resource)))
                        }
                    }
                }
                
            }
            "jar" -> sequence {
                val connection = url.openConnection() as? JarURLConnection
                    ?: throw IllegalStateException("Resource URL $url open failure: protocol is 'jar' but cannot open as JarURLConnection.")
                val jarFile = connection.jarFile
                jarFile.entries().asSequence().forEach { entry ->
                    // println("entry: $entry")
                    if (globs.any { r ->
                            // println("r: $r")
                            // println("r.matches(${entry.name}): ${r.matches(entry.name)}")
                            // println("r.matches(${entry.name.replace("/", "\\")}): ${r.matches(entry.name.replace("/", "\\"))}")
                            // println()
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
    
    
    public fun glob(glob: String): ResourcesScanner<T> = also {
        if (globs.add(Regex(Globs.toRegex(glob), setOf(RegexOption.IGNORE_CASE)))) {
            lookups.add(::lookup) // { loader, url -> lookup(loader, url) }
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
            doResourcesLookup(model, classLoader, resource).flatMap { url ->
                lookups.asSequence().flatMap { lookup -> lookup(model, resource, classLoader, url) }
            }
        }
    }
    
    private fun doResourcesLookup(model: ResourceModel, classLoader: ClassLoader, resource: String): Sequence<URL> {
        return model.getResources(classLoader, resource)
    }
    
    
    @JvmOverloads
    public fun <C : MutableCollection<T>> collect(
        allResources: Boolean,
        collection: C,
        classLoader: ClassLoader = this.classLoader,
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
    
    
    public companion object;
    
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
        
        public data class PathValue internal constructor(override val value: Path, public val resource: String) :
            ResourceVisitValue<Path>()
        
        public data class JarEntryValue internal constructor(override val value: JarEntry, public val url: URL) :
            ResourceVisitValue<JarEntry>()
    }
    
}


public fun <T> ResourcesScanner<T>.visitPath(visitor: (PathValue) -> Sequence<T>): ResourcesScanner<T> =
    visit {
        if (it is PathValue) visitor(it)
        else emptySequence()
    }

public fun <T> ResourcesScanner<T>.visitJarEntry(visitor: (JarEntry, URL) -> Sequence<T>): ResourcesScanner<T> = visit {
    if (it is JarEntryValue) visitor(it.value, it.url)
    else emptySequence()
}

public fun <T> ResourcesScanner<T>.toMutableList(allResources: Boolean): MutableList<T> =
    collect(allResources, mutableListOf())

public fun <T> ResourcesScanner<T>.toList(allResources: Boolean): List<T> =
    toMutableList(allResources).let { list ->
        when {
            list.isEmpty() -> emptyList()
            list.size == 1 -> listOf(list[0])
            else -> list
        }
    }