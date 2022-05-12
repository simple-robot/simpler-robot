package love.forte.simboot.core.utils

import love.forte.simboot.utils.Globs
import love.forte.simbot.InternalSimbotApi
import love.forte.simbot.SimbotIllegalStateException
import love.forte.simbot.resources.Resource
import love.forte.simbot.resources.Resource.Companion.toResource
import kotlin.math.min


private val pathReplace = Regex("[/\\\\]")

/**
 * 扫描所有的Class.
 */
internal fun <T> scanClass(
    classLoader: ClassLoader,
    targetPackages: List<String>,
    onFailure: (e: Throwable, className: String) -> Class<*>?,
    collector: Sequence<Class<*>>.() -> T,
): T {
    return ResourcesScanner<Class<*>>(classLoader).use { scanner ->
        for (scanPkg in targetPackages) {
            val scanPath = scanPkg.replace(".", "/")
            scanner.scan(scanPath)
            scanner.glob("$scanPath**.class")
        }
        scanner.visitJarEntry { entry, _ ->
            val className = entry.name.replace(pathReplace, ".").substringBeforeLast(".class")
            val loadClass = runCatching {
                scanner.classLoader.loadClass(className)
            }.getOrElse { onFailure(it, className) }
            if (loadClass != null) {
                sequenceOf(loadClass)
            } else {
                emptySequence()
            }
        }.visitPath { (_, r) ->
            // '/Xxx.class'
            val classname = r.replace(pathReplace, ".").substringBeforeLast(".class").let {
                if (it.startsWith(".")) it.substring(1) else it
            }
            val loadClass = runCatching {
                scanner.classLoader.loadClass(classname)
            }.getOrElse { e -> throw SimbotIllegalStateException("Class load filed: $classname", e) }
            sequenceOf(loadClass)
        }.collectSequence(true).collector()
    }
    
}

/**
 * 扫描所有疑似为kotlin顶层函数的类，但是无法保证它**绝对是**kotlin所生成的类。
 *
 * 会过滤出符合如下条件的所有类：
 * - [Class.getConstructors] 为空。
 * - [Class.getInterfaces] 为空。
 * - 此 class 存在 [@kotlin.Metadata][kotlin.Metadata] 注解。
 * - 是 `public`、`final` 的
 * - 不是抽象的。
 * - 不是枚举、不是注解、不是数组。
 */
@OptIn(InternalSimbotApi::class)
internal fun <T> scanTopClass(
    classLoader: ClassLoader,
    targetPackages: List<String>,
    onFailure: (e: Throwable, className: String) -> Class<*>?,
    collector: Sequence<Class<*>>.() -> T,
): T {
    return scanClass(classLoader, targetPackages, onFailure) {
        filter { c ->
            kotlin.runCatching { c.isTopClass() }.getOrElse { false }
        }.collector()
    }
}


internal fun <T> scanResources(
    classLoader: ClassLoader,
    globs: List<String>,
    collector: Sequence<Resource>.() -> T,
): T {
    return ResourcesScanner<Resource>(classLoader).use { scanner ->
        for (res in globs) {
            // from top
            scanner.scan(pathPrefix(res))
            scanner.glob(res)
        }
        scanner.visitJarEntry { entry, url ->
            sequenceOf(url.toResource(entry.name))
        }.visitPath { (path, name) ->
            sequenceOf(path.toResource(name))
        }.collectSequence(true).collector()
    }
}


internal fun pathPrefix(origin: String, globRegex: String = Globs.toRegex(origin)): String {
    if (origin.startsWith("*")) {
        return ""
    }
    val originSplit = origin.split("/")
    var glob0 = globRegex
    if (glob0.startsWith("^")) {
        glob0 = glob0.substring(1)
    }
    
    val globSplit = glob0.split("/")
    
    val rp = min(originSplit.size, globSplit.size)
    
    val builder = StringBuilder()
    for (i in 0 until rp) {
        val op = originSplit[i]
        val gp = globSplit[i]
        if (op != gp) {
            break
        }
        if (i > 0) {
            builder.append('/')
        }
        builder.append(op)
    }
    
    return builder.toString()
}

/**
 * 判断一个 [Class] 是否 **疑似为** 一个Kotlin中的顶层函数。
 *
 * 当一个 [Class] 符合如下条件时得到 `true`。
 * - [Class.getConstructors] 为空。
 * - [Class.getInterfaces] 为空。
 * - 此 class 存在 [@kotlin.Metadata][kotlin.Metadata] 注解。
 * - 是 `public`、`final` 的
 * - 不是抽象的。
 * - 不是枚举、不是注解、不是数组。
 *
 * _Note: 此函数不捕获异常。_
 */
@InternalSimbotApi
public fun Class<*>.isTopClass(): Boolean {
    // constructors must be empty.
    if (constructors.isNotEmpty()) {
        return false
    }
    // interfaces must be empty.
    if (interfaces.isNotEmpty()) {
        return false
    }
    
    if (getAnnotation(Metadata::class.java) == null) {
        return false
    }
    
    // public, final, not abstract, not array, not enum, not annotation
    return (isPublic && isFinal) && !isAbstract && !isArray && !isEnum && !isAnnotation
}