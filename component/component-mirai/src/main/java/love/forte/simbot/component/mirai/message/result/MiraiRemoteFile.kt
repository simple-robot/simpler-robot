/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     MiraiFile.kt
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

package love.forte.simbot.component.mirai.message.result

import kotlinx.coroutines.runBlocking
import love.forte.common.utils.secondToMill
import love.forte.simbot.SimbotExpectedException
import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.results.FileInfo
import love.forte.simbot.component.mirai.message.asAccountInfo
import love.forte.simbot.component.mirai.sender.memberOrNull
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.utils.RemoteFile
import java.io.FileNotFoundException


/**
 * 一个 Mirai file。
 *
 * mirai file中的信息特性与 mirai的 [RemoteFile] 一致，部分信息为实时获取的。
 *
 */
public class MiraiRemoteFile(private val file: RemoteFile) : FileInfo {

    private val fileInfo get() = runBlocking { file.getInfo() }

    override val originalData: String
        get() = file.toString()

    /**
     * 文件ID。
     */
    override val id: String
        get() = name.let { n ->
            file.id.let { i ->
                if (i == null) n else "$n:$i"
            }
        }
    override val name: String
        get() = file.name

    /**
     * 文件的路径。
     */
    override val path: String
        get() = file.path

    /**
     * 文件上传时间。
     * 获取不到则可能得到 `-1`.
     */
    override val time: Long
        get() = fileInfo?.uploadTime?.secondToMill() ?: -1

    /**
     * 到期时间。null，
     */
    override val expireTime: Long?
        get() = null


    override fun exists(): Boolean = runBlocking { file.exists() }


    /**
     * 上传者信息。
     */
    override val accountInfo: AccountInfo
        get() {
            return fileInfo?.let { info ->
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
            } ?: throw FileNotFoundException("Cannot get file info (id=$id, name=$name, path=$path): result null.")
        }

    /**
     * 是否为一个文件.
     */
    override fun isFile(): Boolean = runBlocking { file.isFile() }

    /**
     * 是否为一个文件夹。
     */
    override fun isDirectory(): Boolean = runBlocking { file.isDirectory() }

    /**
     * 文件的大小。一般来讲，单位为字节。
     */
    override fun size(): Long = fileInfo?.length ?: -1
}
