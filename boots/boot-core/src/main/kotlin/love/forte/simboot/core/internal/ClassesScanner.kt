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

import java.io.File
import java.io.IOException
import java.net.JarURLConnection
import java.net.URI
import java.net.URISyntaxException
import java.util.function.Predicate
import java.util.jar.JarFile

/**
 * 类扫描器。
 *
 * @author ForteScarlet
 */
public class ClassesScanner {
    /**
     * 储存结果的Set集合
     */
    private var eleStrategySet: MutableSet<Class<*>> = HashSet()

    /**
     * 默认使用的类加载器
     */
    private val classLoader: ClassLoader

    /**
     * 构造，指定默认的类加载器为当前线程的类加载器
     */
    public constructor() {
        classLoader = Thread.currentThread().contextClassLoader
    }

    /**
     * 使用指定的类加载器
     */
    public constructor(classLoader: ClassLoader) {
        this.classLoader = classLoader
    }

    /**
     * 根据过滤规则查询
     *
     * @param classFilter class过滤规则
     */
    public fun scan(packageName: String, classFilter: Predicate<Class<*>>): ClassesScanner {
        eleStrategySet.addAll(addClass(packageName, classFilter))
        return this
    }
    // /**
    //  * 根据过滤规则查询, 查询全部
    //  */
    // @Override
    // public ClassesScanner scan(String packageName) {
    //     eleStrategySet.addAll(addClass(packageName, c -> true));
    //     return this;
    // }
    /**
     * 获取包下所有实现了superStrategy的类并加入list
     *
     * @param classFilter class过滤器
     */
    private fun addClass(packageName: String, classFilter: Predicate<Class<*>>): Set<Class<*>> {
        val url = classLoader.getResource(packageName.replace(".", "/"))
            ?: throw RuntimeException("The package path does not exist: $packageName")
        //如果路径为null，抛出异常

        //路径字符串
        val protocol = url.protocol
        //如果是文件类型，使用文件扫描
        if ("file" == protocol) {
            // 本地自己可见的代码
            return findClassLocal(packageName, classFilter)
            //如果是jar包类型，使用jar包扫描
        } else if ("jar" == protocol) {
            // 引用jar包的代码
            return findClassJar(packageName, classFilter)
        }
        return emptySet()
    }

    /**
     * 本地查找
     *
     * @param packName
     */
    private fun findClassLocal(packName: String, classFilter: Predicate<Class<*>>): Set<Class<*>> {
        val set: MutableSet<Class<*>> = HashSet()
        val uri: URI = try {
            // TODO
            classLoader.getResource(packName.replace(".", "/"))!!.toURI()
        } catch (e: URISyntaxException) {
            throw IllegalStateException("Strategy resource not found.", e)
        } catch (e: NullPointerException) {
            throw IllegalStateException("Strategy resource not found.", e)
        }
        val file = File(uri)
        file.listFiles { chiFile: File ->
            if (chiFile.isDirectory) {
                //如果是文件夹，递归扫描
                if (packName.length == 0) {
                    set.addAll(findClassLocal(chiFile.name, classFilter))
                } else {
                    set.addAll(findClassLocal(packName + "." + chiFile.name, classFilter))
                }
            } else if (chiFile.name.endsWith(".class")) {
                val clazz: Class<*>?
                clazz = try {
                    classLoader.loadClass(packName + "." + chiFile.name.replace(".class", ""))
                } catch (e: ClassNotFoundException) {
                    throw IllegalStateException("Packet scan is abnormal.", e)
                }
                if (clazz != null && classFilter.test(clazz)) {
                    set.add(clazz)
                }
            }
            false
        }
        return set
    }

    /**
     * jar包查找
     */
    private fun findClassJar(packName: String, classFilter: Predicate<Class<*>>): Set<Class<*>> {
        val set: MutableSet<Class<*>> = HashSet()
        val pathName = packName.replace(".", "/")
        val jarFile: JarFile
        jarFile = try {
            val url = classLoader.getResource(pathName)
            val jarURLConnection = url.openConnection() as JarURLConnection
            jarURLConnection.jarFile
        } catch (e: IOException) {
            throw IllegalStateException("Strategy resource not found.", e)
        } catch (e: NullPointerException) {
            throw IllegalStateException("Strategy resource not found.", e)
        }
        val jarEntries = jarFile.entries()
        while (jarEntries.hasMoreElements()) {
            val jarEntry = jarEntries.nextElement()
            val jarEntryName = jarEntry.name
            if (jarEntryName.contains(pathName) && jarEntryName != "$pathName/") {
                //递归遍历子目录
                if (jarEntry.isDirectory) {
                    val clazzName = jarEntry.name.replace("/", ".")
                    val endIndex = clazzName.lastIndexOf(".")
                    var prefix = ""
                    if (endIndex > 0) {
                        prefix = clazzName.substring(0, endIndex)
                    }
                    set.addAll(findClassJar(prefix, classFilter))
                }
                if (jarEntry.name.endsWith(".class")) {
                    var clazz: Class<*>?
                    clazz = try {
                        classLoader.loadClass(jarEntry.name.replace("/", ".").replace(".class", ""))
                    } catch (e: ClassNotFoundException) {
                        throw IllegalStateException(
                            "An exception occurred during package scan: class could not be loaded.",
                            e
                        )
                    }
                    //判断，如果符合，添加
                    if (clazz != null && classFilter.test(clazz)) {
                        set.add(clazz)
                    }
                }
            }
        }
        return set
    }

    /**
     * 获取最终的扫描结果，并作为一个集合返回。
     *
     * @return 最终的扫描结果
     */
    val collection: Collection<Class<*>>
        get() {
            val classSet: Set<Class<*>> = eleStrategySet
            eleStrategySet = HashSet()
            return classSet
        }
}