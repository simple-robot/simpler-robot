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

@file:JvmName("MiraiAdditionalApis")
@file:Suppress("unused")

package love.forte.simbot.component.mirai.additional

import love.forte.simbot.api.message.containers.GroupCodeContainer
import love.forte.simbot.api.message.containers.GroupContainer
import love.forte.simbot.api.message.results.FileResults
import love.forte.simbot.api.message.results.Result
import love.forte.simbot.api.sender.AdditionalApi
import love.forte.simbot.component.mirai.message.MiraiMessageCache
import love.forte.simbot.http.template.HttpTemplate
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.MessageChain


/**
 * mirai组件下的额外API的父接口
 */
public interface MiraiAdditionalApi<R : Result> : AdditionalApi<R> {


}


/**
 * mirai组件 getter 相关的额外API
 */
public interface MiraiGetterAdditionalApi<R : Result> : MiraiAdditionalApi<R> {
    /**
     * 通过当前Getter中可提供的元素执行当前API.
     */
    fun execute(getterInfo: GetterInfo) : R
}

public data class GetterInfo(val bot: Bot, val http: HttpTemplate?)

/**
 * mirai组件 setter 相关的额外API
 */
public interface MiraiSetterAdditionalApi<R : Result> : MiraiAdditionalApi<R> {
    /**
     * 通过当前Getter中可提供的元素执行当前API.
     */
    fun execute(setterInfo: SetterInfo) : R
}


public data class SetterInfo(val bot: Bot)

/**
 * mirai组件 sender 相关的额外API
 */
public interface MiraiSenderAdditionalApi<R : Result> : MiraiAdditionalApi<R> {
    /**
     * 通过当前Getter中可提供的元素执行当前API.
     */
    fun execute(senderInfo: SenderInfo) : R
}


public data class SenderInfo(val bot: Bot, val contact: Contact?, val message: MessageChain?, val cache: MiraiMessageCache)





//**************** api list ****************//


//region 群文件列表

public fun groupFiles(group: Long): AdditionalApi<FileResults> = MiraiGroupFilesApi(group)
public fun groupFiles(group: String): AdditionalApi<FileResults> = groupFiles(group.toLong())
public fun groupFiles(group: GroupCodeContainer): AdditionalApi<FileResults> = groupFiles(group.groupCodeNumber)
public fun groupFiles(group: GroupContainer): AdditionalApi<FileResults> = groupFiles(group.groupInfo)

//endregion













