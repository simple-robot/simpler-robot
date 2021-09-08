/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

@file:JvmName("URLUtil")

package love.forte.simbot.utils

import java.io.InputStream
import java.io.Reader
import java.net.JarURLConnection
import java.net.URL
import java.net.URLConnection
import java.nio.charset.Charset
import java.util.*


/**
 * 针对一个文件相关的 [URL] 来读取其中的内容。
 * 如果这个 [URL.openConnection] 的类型为 [JarURLConnection] 时，将会执行 [java.util.jar.JarFile.close].
 *
 */
public fun <T> URL.useJarStream(block: (InputStream) -> T): T {
    val connection: URLConnection = openConnection().also { c -> c.useCaches = false }
    return connection.getInputStream().use(block)
}


/**
 * 通过 [URL.openConnection] 得到一个 [InputStream] 实例。
 */
public fun URL.newInputStream(): InputStream = openConnection().also { it.useCaches = false }.getInputStream()


/**
 * 针对一个文件相关的 [URL] 来读取其中的内容。
 * 如果这个 [URL.openConnection] 的类型为 [JarURLConnection] 时，将会执行 [java.util.jar.JarFile.close].
 *
 */
@JvmOverloads
public fun <T> URL.useJarReader(charset: Charset = Charsets.UTF_8, block: (Reader) -> T): T =
    useJarStream { input -> input.reader(charset).use(block) }


/**
 * 针对一个文件相关的 [URL] 来读取其中的内容。
 * 如果这个 [URL.openConnection] 的类型为 [JarURLConnection] 时，将会执行 [java.util.jar.JarFile.close].
 *
 */
@JvmOverloads
public fun <T> URL.useJarBufferedReader(charset: Charset = Charsets.UTF_8, block: (Reader) -> T): T =
    useJarStream { input -> input.bufferedReader(charset).use(block) }


/**
 * 讲一个 [URL] 读取并转化为一个 [Properties] 实例。
 */
public fun URL.toProperties(): Properties {
    return Properties().also { p ->
        useJarBufferedReader { reader -> p.load(reader) }
    }
}


