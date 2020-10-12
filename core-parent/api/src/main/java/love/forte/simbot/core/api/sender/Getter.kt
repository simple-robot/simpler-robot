/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     Getter.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.api.sender

import love.forte.simbot.core.api.message.containers.BotContainer
import love.forte.simbot.core.api.message.containers.BotInfo
import love.forte.simbot.core.api.message.results.*

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
 * 一般来讲，如果组件不支持某个API，则会直接抛出一个异常。
 *
 * @author ForteScarlet <ForteScarlet@163.com>
 * @date 2020/9/2
 * @since
 */
public interface Getter : BotContainer {


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

    @JvmDefault
    fun getFriendInfo(code: Long): FriendInfo = getFriendInfo(code.toString())

    /**
     * 获取一个群友信息。
     */
    fun getMemberInfo(group: String, code: String): GroupMemberInfo

    @JvmDefault
    fun getMemberInfo(group: Long, code: Long): GroupMemberInfo = getMemberInfo(group.toString(), code.toString())


    /**
     * 获取一个群详细信息
     */
    fun getGroupInfo(group: String): GroupFullInfo

    @JvmDefault
    fun getGroupInfo(group: Long): GroupFullInfo = getGroupInfo(group.toString())


    /**
     * 获取好友列表
     * @param cache 是否使用缓存。
     */
    fun getFriendList(cache: Boolean, limit: Int): FriendList

    @JvmDefault
    fun getFriendList(limit: Int): FriendList = getFriendList(false, limit)

    @JvmDefault
    fun getFriendList(): FriendList = getFriendList(false, -1)


    /**
     * 获取群列表
     * @param cache 是否使用缓存。
     */
    fun getGroupList(cache: Boolean, limit: Int): GroupList

    @JvmDefault
    fun getGroupList(limit: Int): GroupList = getGroupList(false, limit)

    @JvmDefault
    fun getGroupList(): GroupList = getGroupList(false, -1)

    /**
     * 获取群成员列表
     */
    fun getGroupMemberList(group: String, cache: Boolean, limit: Int): GroupMemberList

    @JvmDefault
    fun getGroupMemberList(group: String, limit: Int): GroupMemberList = getGroupMemberList(group, false, limit)
    @JvmDefault
    fun getGroupMemberList(group: String, ): GroupMemberList = getGroupMemberList(group, false, -1)
    @JvmDefault
    fun getGroupMemberList(group: Long, cache: Boolean, limit: Int): GroupMemberList = getGroupMemberList(group.toString(), cache, limit)
    @JvmDefault
    fun getGroupMemberList(group: Long, limit: Int): GroupMemberList = getGroupMemberList(group.toString(), false, limit)
    @JvmDefault
    fun getGroupMemberList(group: Long, ): GroupMemberList = getGroupMemberList(group.toString(), false, -1)

    /**
     * 获取某群的被禁言人列表。
     * @param group 群号
     * @param cache 是否使用缓存
     */
    fun getBanList(group: String, cache: Boolean, limit: Int): BanList
    @JvmDefault
    fun getBanList(group: String, limit: Int): BanList = getBanList(group, false, limit)
    @JvmDefault
    fun getBanList(group: String): BanList = getBanList(group, false, -1)
    @JvmDefault
    fun getBanList(group: Long, cache: Boolean, limit: Int): BanList = getBanList(group.toString(), cache, limit)
    @JvmDefault
    fun getBanList(group: Long, limit: Int): BanList = getBanList(group.toString(), false, limit)
    @JvmDefault
    fun getBanList(group: Long): BanList = getBanList(group.toString(), false, -1)


    /**
     * 获取群公告列表
     */
    fun getGroupNoteList(group: String, cache: Boolean, limit: Int): GroupNoteList
    @JvmDefault
    fun getGroupNoteList(group: String, limit: Int): GroupNoteList = getGroupNoteList(group, false, limit)
    @JvmDefault
    fun getGroupNoteList(group: String): GroupNoteList = getGroupNoteList(group, false, -1)
    @JvmDefault
    fun getGroupNoteList(group: Long, cache: Boolean, limit: Int) = getGroupNoteList(group.toString(), cache, limit)
    @JvmDefault
    fun getGroupNoteList(group: Long, limit: Int): GroupNoteList = getGroupNoteList(group.toString(), false, limit)
    @JvmDefault
    fun getGroupNoteList(group: Long): GroupNoteList = getGroupNoteList(group.toString(), false, -1)




}