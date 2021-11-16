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

package love.forte.simbot.component.mirai.additional

import kotlinx.coroutines.runBlocking
import love.forte.common.utils.secondToMill
import love.forte.simbot.SimbotExpectedException
import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.results.FileInfo
import love.forte.simbot.component.mirai.message.asAccountInfo
import love.forte.simbot.component.mirai.sender.memberOrNull
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.file.AbsoluteFile
import net.mamoe.mirai.contact.file.AbsoluteFileFolder


internal fun AbsoluteFileFolder.toFileInfo(): MiraiRemoteFile {
    if (this is AbsoluteFile) {
        return MiraiRemoteFile.File(this)
    }
    return MiraiRemoteFile.Floder(this)
}

/**
 * 一个 Mirai file。
 *
 */
public sealed class MiraiRemoteFile(protected val file: AbsoluteFileFolder) : FileInfo {

    internal class File(private val _file: AbsoluteFile): MiraiRemoteFile(_file) {
        override val md5: ByteArray get() = _file.md5
        override val sha1: ByteArray get() = _file.sha1
        override val url: String? get() = runBlocking { _file.getUrl() }
        override val expireTime: Long get() = _file.expiryTime.secondToMill()
        /**
         * 文件的大小。一般来讲，单位为字节。
         */
        override fun size(): Long = _file.size
        override val originalData: String get() = "RemoteFile(id=$id, name=$name, path=$path)"
    }

    internal class Floder(file: AbsoluteFileFolder): MiraiRemoteFile(file) {
        override val md5: ByteArray? get() = null
        override val sha1: ByteArray? get() = null
        override val url: String? get() = null
        override val expireTime: Long get() = -1
        override val originalData: String get() = "RemoteFolder(id=$id, name=$name, path=$path)"
    }


    override fun toString(): String = originalData

    /**
     * 文件ID。
     */
    override val id: String get() = file.id
    override val name: String get() = file.name
    /**
     * 文件的路径。
     */
    override val path: String get() = file.absolutePath

    /**
     * 文件上传时间。
     * 获取不到则可能得到 `-1`.
     */
    override val time: Long get() = file.uploadTime.secondToMill()
    override fun exists(): Boolean = runBlocking { file.exists() }




    /**
     * 上传者信息。
     */
    override val accountInfo: AccountInfo
        get() {
            return file.let { info ->
                val uploaderId = info.uploaderId
                when (val contact = file.contact) {
                    is Group -> {
                        // 群文件
                        val member =
                            contact.memberOrNull(uploaderId) ?: throw NoSuchElementException("Uploader by $uploaderId")
                        member.asAccountInfo()
                    }

                    // for feature
                    else -> throw SimbotExpectedException("This is an expected exception. Feedback this issue from the https://github.com/ForteScarlet/simpler-robot/issues .\n" +
                            "Undefined file contact. File contact type: ${file.contact::class.java}")
                }
            }
        }

    /**
     * 是否为一个文件.
     */
    override fun isFile(): Boolean = file.isFile

    /**
     * 是否为一个文件夹。
     */
    override fun isDirectory(): Boolean = file.isFolder

    /**
     * 文件的大小。一般来讲，单位为字节。
     */
    override fun size(): Long = -1
}
