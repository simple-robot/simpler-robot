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

@file:JvmName("_MiraiAdditionalApis")
@file:Suppress("unused")

package love.forte.simbot.component.mirai.additional

import love.forte.simbot.api.SimbotExperimentalApi
import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.containers.GroupCodeContainer
import love.forte.simbot.api.message.containers.GroupContainer
import love.forte.simbot.api.message.events.GroupMsg
import love.forte.simbot.api.message.results.CarrierResult
import love.forte.simbot.api.message.results.FileResult
import love.forte.simbot.api.message.results.FileResults
import love.forte.simbot.api.message.results.Result
import love.forte.simbot.api.sender.AdditionalApi
import love.forte.simbot.component.mirai.message.MiraiMessageCache
import love.forte.simbot.http.template.HttpTemplate
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.MessageSource


/**
 * mirai组件下的额外API的父接口
 */
public interface MiraiAdditionalApi<R : Result?> : AdditionalApi<R>


/**
 * mirai组件 getter 相关的额外API
 */
public interface MiraiGetterAdditionalApi<R : Result?> : MiraiAdditionalApi<R> {
    /**
     * 通过当前Getter中可提供的元素执行当前API.
     */
    fun execute(getterInfo: GetterInfo): R
}

/**
 * Getter中可提供的参数。
 */
public data class GetterInfo(val bot: Bot, val http: HttpTemplate?)

/**
 * mirai组件 setter 相关的额外API
 */
public interface MiraiSetterAdditionalApi<R : Result?> : MiraiAdditionalApi<R> {
    /**
     * 通过当前Getter中可提供的元素执行当前API.
     */
    fun execute(setterInfo: SetterInfo): R
}

/**
 * Setter中可提供的参数。
 */
public data class SetterInfo(val bot: Bot)

/**
 * mirai组件 sender 相关的额外API
 */
public interface MiraiSenderAdditionalApi<R : Result?> : MiraiAdditionalApi<R> {
    /**
     * 通过当前Getter中可提供的元素执行当前API.
     */
    fun execute(senderInfo: SenderInfo): R
}

/**
 * Sender中可提供的参数。
 */
public data class SenderInfo(
    val bot: Bot,
    val contact: Contact?,
    val message: MessageChain?,
    val cache: MiraiMessageCache,
)


//**************** api list ****************//

@Suppress("MemberVisibilityCanBePrivate")
@SimbotExperimentalApi
public object MiraiAdditionalApis {

    @JvmField
    public val GETTER = Getter
    @JvmField
    public val SETTER = Setter
    @JvmField
    public val SENDER = Sender

    /**
     * Additional api for Getter.
     */
    public object Getter {
        //region 群文件列表

        /**
         * 获取群文件根目录下的文件列表。
         */
        public fun getGroupFiles(group: Long): AdditionalApi<FileResults> = MiraiGroupFilesApi(group)
        public fun getGroupFiles(group: String): AdditionalApi<FileResults> = getGroupFiles(group.toLong())
        public fun getGroupFiles(group: GroupCodeContainer): AdditionalApi<FileResults> = getGroupFiles(group.groupCodeNumber)
        public fun getGroupFiles(group: GroupContainer): AdditionalApi<FileResults> = getGroupFiles(group.groupInfo)

        //endregion

        //region 群文件 byId

        /**
         * 根据ID寻找文件
         */
        @JvmOverloads
        public fun getGroupFileById(group: Long, id: String, deep: Boolean = true): AdditionalApi<FileResult> =
            MiraiGroupFileByIdApi(group, id, deep)

        @JvmOverloads
        public fun getGroupFileById(group: String, id: String, deep: Boolean = true): AdditionalApi<FileResult> =
            getGroupFileById(group.toLong(), id, deep)

        @JvmOverloads
        public fun getGroupFileById(
            group: GroupCodeContainer,
            id: String,
            deep: Boolean = true,
        ): AdditionalApi<FileResult> = getGroupFileById(group.groupCodeNumber, id, deep)

        @JvmOverloads
        public fun getGroupFileById(group: GroupContainer, id: String, deep: Boolean = true): AdditionalApi<FileResult> =
            getGroupFileById(group.groupInfo, id, deep)

        //endregion


        //region 群文件 byPath
        /**
         * 根据路径寻找文件
         */
        public fun getGroupFileByPath(group: Long, path: String): AdditionalApi<FileResult> =
            MiraiGroupFileByPathApi(group, path)

        public fun getGroupFileByPath(group: String, path: String): AdditionalApi<FileResult> =
            getGroupFileByPath(group.toLong(), path)

        public fun getGroupFileByPath(group: GroupCodeContainer, path: String): AdditionalApi<FileResult> =
            getGroupFileByPath(group.groupCodeNumber, path)

        public fun getGroupFileByPath(group: GroupContainer, path: String): AdditionalApi<FileResult> =
            getGroupFileByPath(group.groupInfo, path)

        //endregion


    }

    /**
     * Additional api for Setter.
     */
    public object Setter {

        //region 群精华消息

        /**
         * 设置群精华消息
         */
        public fun setGroupEssenceMessage(group: Long, flag: Flag<GroupMsg.FlagContent>): AdditionalApi<CarrierResult<Boolean>> =
            MiraiEssenceMessageApi(group, flag)
        public fun setGroupEssenceMessage(group: String, flag: Flag<GroupMsg.FlagContent>): AdditionalApi<CarrierResult<Boolean>> =
            setGroupEssenceMessage(group.toLong(), flag)
        public fun setGroupEssenceMessage(
            group: GroupCodeContainer,
            flag: Flag<GroupMsg.FlagContent>,
        ): AdditionalApi<CarrierResult<Boolean>> = setGroupEssenceMessage(group.groupCodeNumber, flag)
        public fun setGroupEssenceMessage(
            group: GroupContainer,
            flag: Flag<GroupMsg.FlagContent>,
        ): AdditionalApi<CarrierResult<Boolean>> = setGroupEssenceMessage(group.groupInfo, flag)

        /**
         * 设置群精华消息.
         *
         * [sourceBlock] 为一个消息源构建函数，通过一个botId得到一个Mirai原生的 [消息实例][MessageSource].
         */
        public fun setGroupEssenceMessage(group: Long, sourceBlock: (Long) -> MessageSource): AdditionalApi<CarrierResult<Boolean>> =
            MiraiEssenceMessageApi(group, sourceBlock)
        public fun setGroupEssenceMessage(group: String, sourceBlock: (Long) -> MessageSource): AdditionalApi<CarrierResult<Boolean>> =
            setGroupEssenceMessage(group.toLong(), sourceBlock)
        public fun setGroupEssenceMessage(
            group: GroupCodeContainer,
            sourceBlock: (Long) -> MessageSource
        ): AdditionalApi<CarrierResult<Boolean>> = setGroupEssenceMessage(group.groupCodeNumber, sourceBlock)
        public fun setGroupEssenceMessage(
            group: GroupContainer,
            sourceBlock: (Long) -> MessageSource
        ): AdditionalApi<CarrierResult<Boolean>> = setGroupEssenceMessage(group.groupInfo, sourceBlock)


        // endregion



    }

    /**
     * Additional api for Sender.
     */
    public object Sender {

    }


}







