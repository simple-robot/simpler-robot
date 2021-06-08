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

@file:JvmName("ResourceUtil")
package love.forte.simbot.utils

import java.io.InputStream
import java.net.URL
import java.nio.charset.Charset
import java.nio.file.Path
import java.util.*
import kotlin.io.path.inputStream

/**
 * 资源。
 * 一个资源，需要获取他的输入流，以及名字。
 */
public interface Resource {

    /**
     * 这个资源的名称。
     */
    val name: String

    /**
     * 打开一个这个资源的输入流。
     */
    val inputStream: InputStream

}


/**
 * 基于 [URL] 的 [Resource] 实现。
 */
public data class URLResource(private val url: URL) : Resource {
    override val name: String = url.toString()

    override val inputStream: InputStream
        get() = url.openStream()
}

/**
 * 基于 [Path] 的 [Resource] 实现。
 */
public data class PathResource(private val path: Path) : Resource {
    override val name: String = path.toRealPath().toString()

    override val inputStream: InputStream
        get() = path.inputStream()
}



public fun Path.asResource(): Resource = PathResource(this)
public fun URL.asResource(): Resource = URLResource(this)

/**
 * 将 [Resource.inputStream] 转化为 [Properties].
 */
public fun Resource.readToProperties(charset: Charset = Charsets.UTF_8): Properties = Properties().also { p ->
    inputStream.bufferedReader(charset).use { p.load(it) }
}



public open class NoSuchResourceException : NoSuchElementException {
    constructor() : super()
    constructor(s: String?) : super(s)
}
