/*
 *  Copyright (c) 2020-2021 ForteScarlet <https://github.com/ForteScarlet>
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

import love.forte.simbot.SimbotIllegalStateException
import java.io.File
import java.io.IOException
import java.net.JarURLConnection
import java.net.URI
import java.net.URISyntaxException
import java.util.function.Predicate
import java.util.jar.JarFile

/**
 * 资源文件扫描器。
 *
 *
 * // 需要优化。
 *
 * @author ForteScarlet
 */
public class ResourcesScanner(private val classLoader: ClassLoader) {
    /**
     * 储存结果的Set集合。
     */
    private var eleStrategySet: MutableSet<URI> = HashSet()


    /**
     * 构造，指定默认的类加载器为当前线程的类加载器。
     */
    public constructor() : this(Thread.currentThread().contextClassLoader)


    /**
     * 根据过滤规则查询
     *
     * @param classFilter class过滤规则
     */
    public fun scan(path: String?, classFilter: Predicate<URI>): ResourcesScanner {
        var path1 = path
        if (path1 == null || path1.isEmpty()) {
            path1 = "." + File.separator
        }
        eleStrategySet.addAll(addFile(path1, classFilter))
        return this
    }

    /**
     * 获取包下所有
     *
     * @param filter 过滤器
     */
    private fun addFile(path: String, filter: Predicate<URI>): Set<URI> {
        val url =
            classLoader.getResource(path) ?: throw SimbotIllegalStateException("Resource path does not exist: $path")
        //如果路径为null，抛出异常



        //路径字符串
        val protocol = url.protocol
        try {
            //如果是文件类型，使用文件扫描
            if ("file" == protocol) {
                return findLocal(path, filter)
                //如果是jar包类型，使用jar包扫描
            } else if ("jar" == protocol) {
                return findJar(path, filter)
            }
        } catch (e: Exception) {
            throw SimbotIllegalStateException("Unable to scan path: $path", e)
        }
        return emptySet()
    }

    /**
     * 本地查找
     */
    private fun findLocal(path: String, filter: Predicate<URI>): Set<URI> {
        val set: MutableSet<URI> = HashSet()
        val uri: URI = try {
            classLoader.getResource(path)!!.toURI()
        } catch (e: NullPointerException) {
            throw SimbotIllegalStateException("File resource not found: $path", e)
        } catch (e: URISyntaxException) {
            throw SimbotIllegalStateException("File resource not found: $path", e)
        }
        val file = File(uri)
        if (file.isDirectory) {
            file.listFiles { chiFile: File ->
                if (chiFile.isDirectory) {
                    //如果是文件夹，递归扫描
                    if (path.length == 0) {
                        set.addAll(findLocal(chiFile.name, filter))
                    } else {
                        set.addAll(findLocal(path + File.separator + chiFile.name, filter))
                    }
                } else {
                    val childUri = chiFile.toURI()
                    if (filter.test(childUri)) {
                        set.add(childUri)
                    }
                }
                true
            }
        }
        return set
    }

    /**
     * jar包查找
     *
     * @param path
     */
    private fun findJar(path: String, filter: Predicate<URI>): Set<URI> {
        val set: MutableSet<URI> = HashSet()
        val jarFile: JarFile = try {
            val url = classLoader.getResource(path)!!
            val jarURLConnection = url.openConnection() as JarURLConnection
            jarURLConnection.jarFile
        } catch (e: NullPointerException) {
            throw SimbotIllegalStateException("Jar resource not found: $path", e)
        } catch (e: IOException) {
            throw SimbotIllegalStateException("Jar resource not found: $path", e)
        } catch (e: ClassCastException) {
            throw SimbotIllegalStateException("Jar resource not found: $path", e)
        }

        // jarFile
        try {
            // 遍历
            val entries = jarFile.entries()
            while (entries.hasMoreElements()) {
                val jarEntry = entries.nextElement()
                val jarEntryName = jarEntry.name
                println("jarEntryName: $jarEntryName")
                if (!jarEntry.isDirectory && jarEntryName.contains(path) && jarEntryName != path + File.separator) {
                    // 不是目录，且符合要求
                    val entryUri = URI.create(jarEntry.name)
                    if (filter.test(entryUri)) {
                        set.add(entryUri)
                    }
                }
            }
        } catch (e: Exception) {
            throw SimbotIllegalStateException("Cannot open jar file $jarFile from path $path", e)
        }
        return set
    }// reset.

    /**
     * 获取最终的扫描结果，并作为一个集合返回。
     *
     * @return 最终的扫描结果
     */
    public fun collect(): Set<URI> {
        val uriSet: Set<URI> = eleStrategySet
        // reset.
        eleStrategySet = HashSet()
        return uriSet
    }
}