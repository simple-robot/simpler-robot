/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
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

@file:JvmMultifileClass
@file:JvmName("Results")
package love.forte.simbot.api.message.results


/**
 *
 * 群公告信息。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
interface GroupNote : Result {
    /**
     * 公告标题
     */
    val title: String

    /**
     * 公告正文
     */
    val text: String

    /**
     * 是否为置顶
     */
    val top: Boolean

    /**
     * 是否发给新成员
     */
    val forNew: Boolean

    /**
     * 需要被确认
     */
    val confirm: Boolean

    /**
     * 发表时间
      */
    val issuingTime: Long
}


/**
 * 群公告列表
 */
interface GroupNoteList : MultipleResults<GroupNote>


/**
 * [GroupNote] 无效化实现。
 */
public fun emptyGroupNote(): GroupNote = object : GroupNote {
    override val originalData: String
        get() = "{}"
    override val title: String
        get() = ""
    override val text: String
        get() = ""
    override val top: Boolean
        get() = false
    override val forNew: Boolean
        get() = false
    override val confirm: Boolean
        get() = false
    override val issuingTime: Long
        get() = -1

    override fun toString(): String {
        return "EmptyGroupNote"
    }
}


/**
 * [GroupNoteList] 无效化实现。
 */
public fun emptyGroupNoteList(): GroupNoteList = object : GroupNoteList {
    override val originalData: String
        get() = "[]"
    override val results: List<GroupNote>
        get() = emptyList()

    override fun toString(): String {
        return "EmptyGroupNoteList"
    }
}