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
import love.forte.simbot.SimbotRuntimeException
import love.forte.simbot.api.message.results.FileResult
import love.forte.simbot.api.message.results.FileResults


/**
 * 用于获取群文件列表的额外API，属于一个 [mirai getter额外API][MiraiGetterAdditionalApi]
 */
public data class MiraiGroupFilesApi(val group: Long) : MiraiGetterAdditionalApi<FileResults> {
    /**
     * 群文件列表API
     */
    override val additionalApiName: String
        get() = "GroupFiles"

    /**
     * 获取群文件列表。
     */
    override fun execute(getterInfo: GetterInfo): FileResults {
        val rootFile = getterInfo.bot.getGroupOrFail(group).filesRoot
        return MiraiFileResults(rootFile)
    }
}


/**
 * 用于通过某文件的[id]获取[指定群][group]文件的额外API。
 *
 *
 * 可通过 [deep] 决定是否深入子文件夹获取。
 *
 */
public data class MiraiGroupFileByIdApi(val group: Long, val id: String, val deep: Boolean = true) :
    MiraiGetterAdditionalApi<FileResult> {
    override val additionalApiName: String
        get() = "GroupFileById"


    override fun execute(getterInfo: GetterInfo): FileResult = runBlocking {
        val root = getterInfo.bot.getGroupOrFail(group).filesRoot
        val resolveById = root.resolveById(id, deep) ?: throw NoSuchRemoteFileException("Id '$id' from group $group")
        MiraiFileResult(resolveById)
    }
}

/**
 * 用于通过某文件的 [路径][path] 获取 [指定群][group] 文件的额外API。
 *
 */
public data class MiraiGroupFileByPathApi(val group: Long, val path: String) : MiraiGetterAdditionalApi<FileResult> {
    override val additionalApiName: String
        get() = "GroupFileByPath"


    override fun execute(getterInfo: GetterInfo): FileResult = runBlocking {
        val root = getterInfo.bot.getGroupOrFail(group).filesRoot
        MiraiFileResult(root.resolve(path))
    }
}


public open class NoSuchRemoteFileException : SimbotRuntimeException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) : super(
        message,
        cause,
        enableSuppression,
        writableStackTrace)
}
