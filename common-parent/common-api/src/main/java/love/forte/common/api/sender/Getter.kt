/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     Getter.kt
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

package love.forte.common.api.sender

import love.forte.simbot.common.api.message.results.*

/**
 *
 * 信息获取器，
 * 用来获取一些必要的信息，例如 登录信息、群信息等。
 *
 * 一般 **列表类型** 的获取中都存在一个 **cache** 参数代表是否使用 **缓存**的信息。
 * 而是否真的存在缓存值，则以组件实际实现为准。
 *
 * 一般 **列表类型** 的获取中都存在一个 **limit** 参数代表是否仅获取**部分**信息。
 * 当limit <= 0的时候则认为其无效。
 * 而是否真的可以获取**部分**，则以组件实际实现为准。
 *
 * 一般来讲，如果组件不支持某个API，则会直接抛出一个异常。
 *
 * @author ForteScarlet <ForteScarlet@163.com>
 * @date 2020/9/2
 * @since
 */
public interface Getter {


    /**
     * 得到当前bot的权限信息。
     */
    val authInfo: AuthInfo

    /**
     * 获取当前bot的基础信息。
     */
    val loginInfo: LoginInfo


    /**
     * 获取一个好友的信息。
     */
    fun getFriendInfo(code: String): FriendInfo


    /**
     * 获取一个群友信息。
     */
    fun getMemberInfo(group: String, code: String): GroupMemberInfo


    /**
     * 获取一个群详细信息
     */
    fun getGroupInfo(group: String): GroupFullInfo

    /**
     * 获取好友列表
     * @param cache 是否使用缓存
     */
    fun getFriendList(cache: Boolean, limit: Int): FriendList


    /**
     * 获取群列表
     * @param cache 是否使用缓存
     */
    fun getGroupList(cache: Boolean, limit: Int): GroupList


    /**
     * 获取群成员列表
     */
    fun getGroupMemberList(cache: Boolean, limit: Int): GroupMemberList


    /**
     * 获取某群的被禁言人列表。
     * @param group 群号
     * @param cache 是否使用缓存
     */
    fun getBanList(group: String, cache: Boolean, limit: Int): BanList


    /**
     * 获取群公告列表
     */
    fun getGroupNoteList(group: String, cache: Boolean, limit: Int): GroupNoteList






}