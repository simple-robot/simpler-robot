/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     File.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

package love.forte.simbot.api.message.results

import love.forte.simbot.api.message.containers.AccountContainer
import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.containers.TimeContainer


/**
 * 文件/文件夹信息。
 */
public interface FileInfo : Result, TimeContainer, AccountContainer {

    /**
     * 文件ID。一般情况下，其应当为唯一的。
     */
    val id: String

    /**
     * 文件名称.
     */
    val name: String

    /**
     * 文件的路径。
     */
    val path: String


    /**
     * 文件上传时间。
     * 获取不到则可能得到 `-1`.
     */
    override val time: Long


    /**
     * 文件上传的时间。等同于 [time].
     */
    val uploadTime: Long get() = time


    /**
     * 到期时间。如果无法获取，则为null，
     * 如果无期限，则为-1.
     */
    val expireTime: Long?


    /**
     * 上传者信息。
     */
    override val accountInfo: AccountInfo

    /**
     * 上传者信息.
     * @see accountInfo
     */
    val uploader: AccountInfo get() = accountInfo

    /**
     * 是否为一个文件.
     */
    fun isFile(): Boolean


    /**
     * 是否为一个文件夹。
     */
    fun isDirectory(): Boolean

    /**
     * 文件的大小。一般来讲，单位为字节。
     * 目录得到 `0`。
     * 如果无法获取，得到-1.
     */
    fun size(): Long

    /**
     * 判断文件/目录是否存在。
     */
    fun exists(): Boolean

    /**
     * 文件MD5。不保证能够获取。
     */
    val md5: ByteArray?

    /**
     * 文件sha1。不保证能够获取。
     */
    val sha1: ByteArray?

    /**
     * 文件远程路径。不保证能够获取。
     */
    val url: String?

}


/**
 * 文件信息响应值。
 */
public interface FileResult : NodeResult<FileInfo> {

    /**
     * 当前文件。
     */
    override val value: FileInfo

    /**
     * 此文件夹下的文件列表。可能为空。
     */
    override val results: List<FileResult>

}

/**
 * 多文件信息响应值。
 */
public interface FileResults : MultipleResults<FileResult>





