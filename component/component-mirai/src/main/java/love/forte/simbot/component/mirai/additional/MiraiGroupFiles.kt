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

import love.forte.simbot.api.message.results.FileResults


/**
 * 用于获取群文件列表的额外API，属于一个 [mirai getter额外API][MiraiGetterAdditionalApi]
 */
public class MiraiGroupFilesApi(val group: Long) : MiraiGetterAdditionalApi<FileResults> {
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