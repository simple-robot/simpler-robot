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

package love.forte.simbot.api.sender

import kotlinx.coroutines.runBlocking
import love.forte.simbot.api.message.containers.*
import love.forte.simbot.api.message.results.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 *
 * 信息获取器，
 * 用来获取一些必要的信息，例如 登录信息、群信息等。
 *
 * 一般 **列表类型** 的获取中都存在一个 **cache** 参数代表是否使用 **缓存**的信息。
 * 而是否真的存在缓存值，则以组件实际实现为准。
 *
 * 一般 **列表类型** 的获取中都存在一个 **limit** 参数代表是否仅获取**部分**信息。
 * 当limit <= 0的时候则认为获取所有。
 * 而是否真的可以获取**部分**，则以组件实际实现为准。
 *
 * 一般来讲，如果组件不支持某个API，则会直接 **抛出一个异常**。
 *
 * @author ForteScarlet <ForteScarlet@163.com>
 * @date 2020/9/2
 * @since
 */
public interface Getter : Communicator, BotContainer {

    /**
     * 一个标识用的接口，用于标记一个 [Getter] 接口的实现为 **默认** 送信器。
     */
    interface Def : Getter {
        override val coroutineContext: CoroutineContext get() = EmptyCoroutineContext
    }

    /**
     * 得到当前bot的权限信息。
     */
    val authInfo: AuthInfo

    /**
     * 获取当前bot的基础信息。
     */
    override val botInfo: BotInfo


    /**
     * 获取一个好友的信息。
     */
    @JvmSynthetic
    suspend fun friendInfo(code: String): FriendInfo

    @JvmSynthetic
    suspend fun friendInfo(code: Long): FriendInfo = friendInfo(code.toString())

    @JvmSynthetic
    suspend fun friendInfo(code: AccountCodeContainer): FriendInfo = friendInfo(code.accountCode)

    @JvmSynthetic
    suspend fun friendInfo(code: AccountContainer): FriendInfo = friendInfo(code.accountInfo)

    ////////// blocking ///////////
    fun getFriendInfo(code: String): FriendInfo = runBlocking { friendInfo(code) }

    fun getFriendInfo(code: Long): FriendInfo = runBlocking { friendInfo(code) }

    fun getFriendInfo(code: AccountCodeContainer): FriendInfo = runBlocking { friendInfo(code) }

    fun getFriendInfo(code: AccountContainer): FriendInfo = runBlocking { friendInfo(code) }


    /**
     * 获取一个群友信息。
     */
    @JvmSynthetic
    suspend fun memberInfo(group: String, code: String): GroupMemberInfo


    @JvmSynthetic
    suspend fun memberInfo(group: Long, code: Long): GroupMemberInfo = memberInfo(group.toString(), code.toString())


    @JvmSynthetic
    suspend fun memberInfo(group: GroupCodeContainer, code: AccountCodeContainer): GroupMemberInfo =
        memberInfo(group.groupCode, code.accountCode)


    @JvmSynthetic
    suspend fun memberInfo(group: GroupContainer, code: AccountContainer): GroupMemberInfo =
        memberInfo(group.groupInfo, code.accountInfo)


    @JvmSynthetic
    suspend fun <T> memberInfo(groupAndAccount: T): GroupMemberInfo
            where T : GroupCodeContainer,
                  T : AccountCodeContainer =
        memberInfo(groupAndAccount, groupAndAccount)


    @JvmSynthetic
    suspend fun <T> memberInfo(groupAndAccount: T): GroupMemberInfo
            where T : GroupContainer,
                  T : AccountContainer =
        memberInfo(groupAndAccount, groupAndAccount)


    ////////////// blocking /////////////////

    fun getMemberInfo(group: String, code: String): GroupMemberInfo =
        runBlocking { memberInfo(group, code) }


    fun getMemberInfo(group: Long, code: Long): GroupMemberInfo = runBlocking { memberInfo(group, code) }


    fun getMemberInfo(group: GroupCodeContainer, code: AccountCodeContainer): GroupMemberInfo =
        runBlocking { memberInfo(group, code) }


    fun getMemberInfo(group: GroupContainer, code: AccountContainer): GroupMemberInfo =
        runBlocking { memberInfo(group, code) }


    fun <T> getMemberInfo(groupAndAccount: T): GroupMemberInfo
            where T : GroupCodeContainer,
                  T : AccountCodeContainer =
        runBlocking { memberInfo(groupAndAccount) }


    fun <T> getMemberInfo(groupAndAccount: T): GroupMemberInfo
            where T : GroupContainer,
                  T : AccountContainer =
        runBlocking { memberInfo(groupAndAccount) }


    /**
     * 获取一个群详细信息
     */
    @JvmSynthetic
    suspend fun groupInfo(group: String): GroupFullInfo

    @JvmSynthetic
    suspend fun groupInfo(group: Long): GroupFullInfo = groupInfo(group.toString())

    @JvmSynthetic
    suspend fun groupInfo(group: GroupCodeContainer): GroupFullInfo = groupInfo(group.groupCode)

    @JvmSynthetic
    suspend fun groupInfo(group: GroupContainer): GroupFullInfo = groupInfo(group.groupInfo)

    ////////////// blocking /////////////

    fun getGroupInfo(group: String): GroupFullInfo = runBlocking { groupInfo(group) }

    fun getGroupInfo(group: Long): GroupFullInfo = runBlocking { groupInfo(group) }

    fun getGroupInfo(group: GroupCodeContainer): GroupFullInfo = runBlocking { groupInfo(group) }

    fun getGroupInfo(group: GroupContainer): GroupFullInfo = runBlocking { groupInfo(group) }


    /**
     * 获取好友列表
     * @param cache 是否使用缓存。
     */
    @JvmSynthetic
    suspend fun friendList(cache: Boolean, limit: Int): FriendList


    @JvmSynthetic
    suspend fun friendList(limit: Int): FriendList = friendList(false, limit)


    @JvmSynthetic
    suspend fun friendList(): FriendList = friendList(false, -1)


    /////////////// Blocking /////////////////

    fun getFriendList(cache: Boolean, limit: Int): FriendList =
        runBlocking { friendList(cache, limit) }


    fun getFriendList(limit: Int): FriendList =
        runBlocking { friendList(limit) }


    fun getFriendList(): FriendList =
        runBlocking { friendList() }


    /**
     * 获取群列表
     * @param cache 是否使用缓存。
     */
    @JvmSynthetic
    suspend fun groupList(cache: Boolean, limit: Int): GroupList


    @JvmSynthetic
    suspend fun groupList(limit: Int): GroupList = groupList(false, limit)


    @JvmSynthetic
    suspend fun groupList(): GroupList = groupList(false, -1)

    ///////////// blocking /////////////

    fun getGroupList(cache: Boolean, limit: Int): GroupList = runBlocking { groupList(cache, limit) }

    fun getGroupList(limit: Int): GroupList = runBlocking { groupList(limit) }

    fun getGroupList(): GroupList = runBlocking { groupList() }


    /**
     * 获取群成员列表
     */
    @JvmSynthetic
    suspend fun groupMemberList(group: String, cache: Boolean, limit: Int): GroupMemberList


    @JvmSynthetic
    suspend fun groupMemberList(group: String, limit: Int): GroupMemberList =
        groupMemberList(group, false, limit)


    @JvmSynthetic
    suspend fun groupMemberList(group: String): GroupMemberList =
        groupMemberList(group, false, -1)


    @JvmSynthetic
    suspend fun groupMemberList(group: Long, cache: Boolean, limit: Int): GroupMemberList =
        groupMemberList(group.toString(), cache, limit)


    @JvmSynthetic
    suspend fun groupMemberList(group: Long, limit: Int): GroupMemberList =
        groupMemberList(group.toString(), false, limit)


    @JvmSynthetic
    suspend fun groupMemberList(group: Long): GroupMemberList =
        groupMemberList(group.toString(), false, -1)


    @JvmSynthetic
    suspend fun groupMemberList(group: GroupCodeContainer, cache: Boolean, limit: Int): GroupMemberList =
        groupMemberList(group.groupCode, cache, limit)


    @JvmSynthetic
    suspend fun groupMemberList(group: GroupCodeContainer, limit: Int): GroupMemberList =
        groupMemberList(group, false, limit)


    @JvmSynthetic
    suspend fun groupMemberList(group: GroupCodeContainer): GroupMemberList =
        groupMemberList(group, false, -1)


    @JvmSynthetic
    suspend fun groupMemberList(group: GroupContainer, cache: Boolean, limit: Int): GroupMemberList =
        groupMemberList(group.groupInfo, cache, limit)


    @JvmSynthetic
    suspend fun groupMemberList(group: GroupContainer, limit: Int): GroupMemberList =
        groupMemberList(group.groupInfo, false, limit)


    @JvmSynthetic
    suspend fun groupMemberList(group: GroupContainer): GroupMemberList =
        groupMemberList(group.groupInfo, false, -1)

    //////////// blocking /////////////


    fun getGroupMemberList(group: String, cache: Boolean, limit: Int): GroupMemberList =
        runBlocking { groupMemberList(group, cache, limit) }


    fun getGroupMemberList(group: String, limit: Int): GroupMemberList =
        runBlocking { groupMemberList(group, limit) }


    fun getGroupMemberList(group: String): GroupMemberList =
        runBlocking { groupMemberList(group) }


    fun getGroupMemberList(group: Long, cache: Boolean, limit: Int): GroupMemberList =
        runBlocking { groupMemberList(group, cache, limit) }


    fun getGroupMemberList(group: Long, limit: Int): GroupMemberList =
        runBlocking { groupMemberList(group, limit) }


    fun getGroupMemberList(group: Long): GroupMemberList =
        runBlocking { groupMemberList(group) }


    fun getGroupMemberList(group: GroupCodeContainer, cache: Boolean, limit: Int): GroupMemberList =
        runBlocking { groupMemberList(group, cache, limit) }


    fun getGroupMemberList(group: GroupCodeContainer, limit: Int): GroupMemberList =
        runBlocking { groupMemberList(group, limit) }


    fun getGroupMemberList(group: GroupCodeContainer): GroupMemberList =
        runBlocking { groupMemberList(group) }


    fun getGroupMemberList(group: GroupContainer, cache: Boolean, limit: Int): GroupMemberList =
        runBlocking { groupMemberList(group, cache, limit) }


    fun getGroupMemberList(group: GroupContainer, limit: Int): GroupMemberList =
        runBlocking { groupMemberList(group, limit) }


    fun getGroupMemberList(group: GroupContainer): GroupMemberList =
        runBlocking { groupMemberList(group) }








    /**
     * 获取某群的被禁言人列表。
     * @param group 群号
     * @param cache 是否使用缓存
     */
    @JvmSynthetic
    suspend fun banList(group: String, cache: Boolean, limit: Int): MuteList


    @JvmSynthetic
    suspend fun banList(group: String, limit: Int): MuteList = banList(group, false, limit)


    @JvmSynthetic
    suspend fun banList(group: String): MuteList = banList(group, false, -1)


    @JvmSynthetic
    suspend fun banList(group: Long, cache: Boolean, limit: Int): MuteList = banList(group.toString(), cache, limit)


    @JvmSynthetic
    suspend fun banList(group: Long, limit: Int): MuteList = banList(group.toString(), false, limit)


    @JvmSynthetic
    suspend fun banList(group: Long): MuteList = banList(group.toString(), false, -1)


    @JvmSynthetic
    suspend fun banList(group: GroupCodeContainer, cache: Boolean, limit: Int): MuteList =
        banList(group.groupCode, cache, limit)


    @JvmSynthetic
    suspend fun banList(group: GroupCodeContainer, limit: Int): MuteList =
        banList(group, false, limit)


    @JvmSynthetic
    suspend fun banList(group: GroupCodeContainer): MuteList =
        banList(group, false, -1)


    @JvmSynthetic
    suspend fun banList(group: GroupContainer, cache: Boolean, limit: Int): MuteList =
        banList(group.groupInfo, cache, limit)


    @JvmSynthetic
    suspend fun banList(group: GroupContainer, limit: Int): MuteList =
        banList(group.groupInfo, false, limit)


    @JvmSynthetic
    suspend fun banList(group: GroupContainer): MuteList =
        banList(group.groupInfo, false, -1)


    //////////// blocking /////////////


    fun getBanList(group: String, cache: Boolean, limit: Int): MuteList =
        runBlocking { banList(group, cache, limit) }


    fun getBanList(group: String, limit: Int): MuteList =
        runBlocking { banList(group, limit) }


    fun getBanList(group: String): MuteList =
        runBlocking { banList(group) }


    fun getBanList(group: Long, cache: Boolean, limit: Int): MuteList =
        runBlocking { banList(group, cache, limit) }


    fun getBanList(group: Long, limit: Int): MuteList =
        runBlocking { banList(group, limit) }


    fun getBanList(group: Long): MuteList =
        runBlocking { banList(group) }


    fun getBanList(group: GroupCodeContainer, cache: Boolean, limit: Int): MuteList =
        runBlocking { banList(group, cache, limit) }


    fun getBanList(group: GroupCodeContainer, limit: Int): MuteList =
        runBlocking { banList(group, limit) }


    fun getBanList(group: GroupCodeContainer): MuteList =
        runBlocking { banList(group) }


    fun getBanList(group: GroupContainer, cache: Boolean, limit: Int): MuteList =
        runBlocking { banList(group, cache, limit) }


    fun getBanList(group: GroupContainer, limit: Int): MuteList =
        runBlocking { banList(group, limit) }


    fun getBanList(group: GroupContainer): MuteList =
        runBlocking { banList(group) }



    /**
     * 获取群公告列表
     */
    @JvmSynthetic
    suspend fun groupNoteList(group: String, cache: Boolean, limit: Int): GroupNoteList


    @JvmSynthetic
    suspend fun groupNoteList(group: String, limit: Int): GroupNoteList = groupNoteList(group, false, limit)


    @JvmSynthetic
    suspend fun groupNoteList(group: String): GroupNoteList = groupNoteList(group, false, -1)


    @JvmSynthetic
    suspend fun groupNoteList(group: Long, cache: Boolean, limit: Int) = groupNoteList(group.toString(), cache, limit)


    @JvmSynthetic
    suspend fun groupNoteList(group: Long, limit: Int): GroupNoteList = groupNoteList(group.toString(), false, limit)


    @JvmSynthetic
    suspend fun groupNoteList(group: Long): GroupNoteList = groupNoteList(group.toString(), false, -1)


    @JvmSynthetic
    suspend fun groupNoteList(group: GroupCodeContainer, cache: Boolean, limit: Int) =
        groupNoteList(group.groupCode, cache, limit)


    @JvmSynthetic
    suspend fun groupNoteList(group: GroupCodeContainer, limit: Int): GroupNoteList =
        groupNoteList(group.groupCode, false, limit)


    @JvmSynthetic
    suspend fun groupNoteList(group: GroupCodeContainer): GroupNoteList =
        groupNoteList(group.groupCode, false, -1)


    @JvmSynthetic
    suspend fun groupNoteList(group: GroupContainer, cache: Boolean, limit: Int) =
        groupNoteList(group.groupInfo, cache, limit)


    @JvmSynthetic
    suspend fun groupNoteList(group: GroupContainer, limit: Int): GroupNoteList =
        groupNoteList(group.groupInfo, false, limit)


    @JvmSynthetic
    suspend fun groupNoteList(group: GroupContainer): GroupNoteList =
        groupNoteList(group.groupInfo, false, -1)

    ///////////// blocking ///////////////

    fun getGroupNoteList(group: String, cache: Boolean, limit: Int): GroupNoteList =
        runBlocking { groupNoteList(group, cache, limit) }


    fun getGroupNoteList(group: String, limit: Int): GroupNoteList =
        runBlocking { groupNoteList(group, limit) }


    fun getGroupNoteList(group: String): GroupNoteList =
        runBlocking { groupNoteList(group) }


    fun getGroupNoteList(group: Long, cache: Boolean, limit: Int) =
        runBlocking { groupNoteList(group, cache, limit) }


    fun getGroupNoteList(group: Long, limit: Int): GroupNoteList =
        runBlocking { groupNoteList(group, limit) }


    fun getGroupNoteList(group: Long): GroupNoteList =
        runBlocking { groupNoteList(group) }


    fun getGroupNoteList(group: GroupCodeContainer, cache: Boolean, limit: Int) =
        runBlocking { groupNoteList(group, cache, limit) }


    fun getGroupNoteList(group: GroupCodeContainer, limit: Int): GroupNoteList =
        runBlocking { groupNoteList(group, limit) }


    fun getGroupNoteList(group: GroupCodeContainer): GroupNoteList =
        runBlocking { groupNoteList(group) }


    fun getGroupNoteList(group: GroupContainer, cache: Boolean, limit: Int) =
        runBlocking { groupNoteList(group, cache, limit) }


    fun getGroupNoteList(group: GroupContainer, limit: Int): GroupNoteList =
        runBlocking { groupNoteList(group, limit) }


    fun getGroupNoteList(group: GroupContainer): GroupNoteList =
        runBlocking { groupNoteList(group) }


}