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

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import love.forte.simbot.api.message.results.FileInfo
import love.forte.simbot.api.message.results.FileResult
import love.forte.simbot.api.message.results.FileResults
import love.forte.simbot.api.message.results.NodeResult
import net.mamoe.mirai.utils.RemoteFile
import java.util.stream.Stream


/**
 * mirai文件返回值
 */
public class MiraiFileResult(private val file: RemoteFile) : FileResult {
    override val originalData: String
        get() = "Result(file=$file)"

    override fun toString(): String = originalData

    /**
     * 当前文件。
     */
    override val value: FileInfo = MiraiRemoteFile(file)

    /**
     * 此文件夹下的文件列表。可能为空。
     */
    override val results: List<FileResult>
        get() = runBlocking {
            if (file.exists()) {
                // 存在文件
                file.listFiles().map { f -> MiraiFileResult(f) }.toList()
            } else emptyList()
        }


    override fun stream(): Stream<NodeResult<FileInfo>> = runBlocking {
        if (file.exists()) {
            // 存在文件
            file.listFiles().map { f -> MiraiFileResult(f) }.asStream()
        } else Stream.empty()
    }
}


/**
 * mirai文件列表返回值，取参数file的子文件列表。
 */
public class MiraiFileResults(private val file: RemoteFile) : FileResults {
    override val originalData: String
        get() = "Results(root=$file)"

    override fun toString(): String = originalData

    /**
     * 结果值没有进行缓存，应当由使用者来进行缓存。
     */
    override val results: List<FileResult>
        get() = runBlocking {
            if (file.exists()) {
                file.listFiles().map { f -> MiraiFileResult(f) }.toList()
            } else emptyList()
        }
}







/**
 * [Flow] as [Stream].
 */
internal suspend fun <T> Flow<T>.asStream(): Stream<T> {
    return this.toList().stream()
}