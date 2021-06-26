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

import love.forte.simbot.api.message.containers.*
import love.forte.simbot.api.message.results.*

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
    interface Def : Getter

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
    fun getFriendInfo(code: String): FriendInfo

    
    fun getFriendInfo(code: Long): FriendInfo = getFriendInfo(code.toString())

    
    fun getFriendInfo(code: AccountCodeContainer): FriendInfo = getFriendInfo(code.accountCode)

    
    fun getFriendInfo(code: AccountContainer): FriendInfo = getFriendInfo(code.accountInfo)

    /**
     * 获取一个群友信息。
     */
    fun getMemberInfo(group: String, code: String): GroupMemberInfo

    
    fun getMemberInfo(group: Long, code: Long): GroupMemberInfo = getMemberInfo(group.toString(), code.toString())

    
    fun getMemberInfo(group: GroupCodeContainer, code: AccountCodeContainer): GroupMemberInfo =
        getMemberInfo(group.groupCode, code.accountCode)

    
    fun getMemberInfo(group: GroupContainer, code: AccountContainer): GroupMemberInfo =
        getMemberInfo(group, code)

    
    fun <T> getMemberInfo(groupAndAccount: T): GroupMemberInfo
            where T : GroupCodeContainer,
                  T : AccountCodeContainer =
        getMemberInfo(groupAndAccount, groupAndAccount)

    
    fun <T> getMemberInfo(groupAndAccount: T): GroupMemberInfo
            where T : GroupContainer,
                  T : AccountContainer =
        getMemberInfo(groupAndAccount, groupAndAccount)


    /**
     * 获取一个群详细信息
     */
    fun getGroupInfo(group: String): GroupFullInfo

    
    fun getGroupInfo(group: Long): GroupFullInfo = getGroupInfo(group.toString())
    
    fun getGroupInfo(group: GroupCodeContainer): GroupFullInfo = getGroupInfo(group.groupCode)
    
    fun getGroupInfo(group: GroupContainer): GroupFullInfo = getGroupInfo(group.groupInfo)


    /**
     * 获取好友列表
     * @param cache 是否使用缓存。
     */
    fun getFriendList(cache: Boolean, limit: Int): FriendList

    
    fun getFriendList(limit: Int): FriendList = getFriendList(false, limit)

    
    fun getFriendList(): FriendList = getFriendList(false, -1)


    /**
     * 获取群列表
     * @param cache 是否使用缓存。
     */
    fun getGroupList(cache: Boolean, limit: Int): GroupList

    
    fun getGroupList(limit: Int): GroupList = getGroupList(false, limit)

    
    fun getGroupList(): GroupList = getGroupList(false, -1)

    /**
     * 获取群成员列表
     */
    fun getGroupMemberList(group: String, cache: Boolean, limit: Int): GroupMemberList

    
    fun getGroupMemberList(group: String, limit: Int): GroupMemberList =
        getGroupMemberList(group, false, limit)

    
    fun getGroupMemberList(group: String): GroupMemberList =
        getGroupMemberList(group, false, -1)


    
    fun getGroupMemberList(group: Long, cache: Boolean, limit: Int): GroupMemberList =
        getGroupMemberList(group.toString(), cache, limit)

    
    fun getGroupMemberList(group: Long, limit: Int): GroupMemberList =
        getGroupMemberList(group.toString(), false, limit)

    
    fun getGroupMemberList(group: Long): GroupMemberList =
        getGroupMemberList(group.toString(), false, -1)

    
    fun getGroupMemberList(group: GroupCodeContainer, cache: Boolean, limit: Int): GroupMemberList =
        getGroupMemberList(group.groupCode, cache, limit)

    
    fun getGroupMemberList(group: GroupCodeContainer, limit: Int): GroupMemberList =
        getGroupMemberList(group, false, limit)

    
    fun getGroupMemberList(group: GroupCodeContainer): GroupMemberList =
        getGroupMemberList(group, false, -1)

    
    fun getGroupMemberList(group: GroupContainer, cache: Boolean, limit: Int): GroupMemberList =
        getGroupMemberList(group.groupInfo, cache, limit)

    
    fun getGroupMemberList(group: GroupContainer, limit: Int): GroupMemberList =
        getGroupMemberList(group.groupInfo, false, limit)

    
    fun getGroupMemberList(group: GroupContainer): GroupMemberList =
        getGroupMemberList(group.groupInfo, false, -1)

    /**
     * 获取某群的被禁言人列表。
     * @param group 群号
     * @param cache 是否使用缓存
     */
    fun getBanList(group: String, cache: Boolean, limit: Int): MuteList

    
    fun getBanList(group: String, limit: Int): MuteList = getBanList(group, false, limit)

    
    fun getBanList(group: String): MuteList = getBanList(group, false, -1)

    
    fun getBanList(group: Long, cache: Boolean, limit: Int): MuteList = getBanList(group.toString(), cache, limit)

    
    fun getBanList(group: Long, limit: Int): MuteList = getBanList(group.toString(), false, limit)

    
    fun getBanList(group: Long): MuteList = getBanList(group.toString(), false, -1)

    
    fun getBanList(group: GroupCodeContainer, cache: Boolean, limit: Int): MuteList =
        getBanList(group.groupCode, cache, limit)

    
    fun getBanList(group: GroupCodeContainer, limit: Int): MuteList =
        getBanList(group, false, limit)

    
    fun getBanList(group: GroupCodeContainer): MuteList =
        getBanList(group, false, -1)

    
    fun getBanList(group: GroupContainer, cache: Boolean, limit: Int): MuteList =
        getBanList(group.groupInfo, cache, limit)

    
    fun getBanList(group: GroupContainer, limit: Int): MuteList =
        getBanList(group.groupInfo, false, limit)

    
    fun getBanList(group: GroupContainer): MuteList =
        getBanList(group.groupInfo, false, -1)


    /**
     * 获取群公告列表
     */
    fun getGroupNoteList(group: String, cache: Boolean, limit: Int): GroupNoteList

    
    fun getGroupNoteList(group: String, limit: Int): GroupNoteList = getGroupNoteList(group, false, limit)

    
    fun getGroupNoteList(group: String): GroupNoteList = getGroupNoteList(group, false, -1)

    
    fun getGroupNoteList(group: Long, cache: Boolean, limit: Int) = getGroupNoteList(group.toString(), cache, limit)

    
    fun getGroupNoteList(group: Long, limit: Int): GroupNoteList = getGroupNoteList(group.toString(), false, limit)

    
    fun getGroupNoteList(group: Long): GroupNoteList = getGroupNoteList(group.toString(), false, -1)

    
    fun getGroupNoteList(group: GroupCodeContainer, cache: Boolean, limit: Int) =
        getGroupNoteList(group.groupCode, cache, limit)

    
    fun getGroupNoteList(group: GroupCodeContainer, limit: Int): GroupNoteList =
        getGroupNoteList(group.groupCode, false, limit)

    
    fun getGroupNoteList(group: GroupCodeContainer): GroupNoteList =
        getGroupNoteList(group.groupCode, false, -1)

    
    fun getGroupNoteList(group: GroupContainer, cache: Boolean, limit: Int) =
        getGroupNoteList(group.groupInfo, cache, limit)

    
    fun getGroupNoteList(group: GroupContainer, limit: Int): GroupNoteList =
        getGroupNoteList(group.groupInfo, false, limit)

    
    fun getGroupNoteList(group: GroupContainer): GroupNoteList =
        getGroupNoteList(group.groupInfo, false, -1)

}