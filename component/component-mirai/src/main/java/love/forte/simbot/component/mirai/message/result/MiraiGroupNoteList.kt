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

package love.forte.simbot.component.mirai.message.result

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import love.forte.common.utils.secondToMill
import love.forte.simbot.api.message.results.GroupNote
import love.forte.simbot.api.message.results.GroupNoteList
import net.mamoe.mirai.contact.Group


public suspend fun MiraiGroupNoteList(group: Group, limit: Int): MiraiGroupNoteList {
    val results = group.announcements.asFlow().let { f ->
        if (limit > 0) f.take(limit) else f
    }.map { an ->
        val content = an.content
        val time = an.publicationTime.secondToMill()
        val p = an.parameters
        val confirm = p.requireConfirmation
        val top = p.isPinned
        val forNew = p.sendToNewMember

        MiraiGroupNoteList.MiraiGroupNote(
            text = content,
            issuingTime = time,
            top = top,
            confirm = confirm,
            forNew = forNew
        )
    }.toList()

    return MiraiGroupNoteList(results, group)
}


/**
 * mirai公告列表。
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public class MiraiGroupNoteList internal constructor(override val results: List<GroupNote>, group: Group) : GroupNoteList {


    override val originalData: String = "MiraiGroupNoteList(group=$group)"

    /** mirai入群公告实例。 */
    data class MiraiGroupNote(
        override val text: String,
        override val issuingTime: Long,
        override val top: Boolean,
        override val confirm: Boolean,
        override val forNew: Boolean,
    ) : GroupNote {
        override val title: String = "公告"

        override val originalData: String get() = toString()
    }
}
