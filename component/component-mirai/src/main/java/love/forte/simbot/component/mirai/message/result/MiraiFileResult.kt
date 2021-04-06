/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     MiraiFileResult.kt
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

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import love.forte.simbot.api.message.results.FileInfo
import love.forte.simbot.api.message.results.FileResult
import love.forte.simbot.api.message.results.NodeResult
import love.forte.simbot.component.mirai.utils.asStream
import net.mamoe.mirai.utils.RemoteFile
import java.util.stream.Stream


/**
 * mirai文件返回值
 */
public class MiraiFileResult(private val file: RemoteFile) : FileResult {
    override val originalData: String
        get() = "Result(file=$file)"

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

    /**
     * 重写stream，使得支持file.listFiles.
     */
    override fun stream(): Stream<NodeResult<FileInfo>> = runBlocking {
        if (file.exists()) {
            // 存在文件
            file.listFiles().map { f -> MiraiFileResult(f) }.asStream()
        } else Stream.empty()
    }
}
