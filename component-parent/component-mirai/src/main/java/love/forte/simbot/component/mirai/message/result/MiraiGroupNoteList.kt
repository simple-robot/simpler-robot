/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MiraiGroupNoteList.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.component.mirai.message.result

import love.forte.simbot.core.api.message.results.GroupNote
import love.forte.simbot.core.api.message.results.GroupNoteList
import net.mamoe.mirai.contact.Group

/**
 * mirai公告列表。
 * 注：mirai目前只支持获取入群公告的文本。
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public class MiraiGroupNoteList(group: Group) : GroupNoteList {

    /** 入群公告。 */
    private val entranceAnnouncement: String = group.settings.entranceAnnouncement

    override val results: List<GroupNote> by lazy(LazyThreadSafetyMode.NONE) { listOf(MiraiGroupNote()) }

    override val originalData: String = "MiraiGroupNoteList(group=$group)"

    /** mirai入群公告实例。 */
    inner class MiraiGroupNote : GroupNote {
        override val title: String = "入群公告"
        override val text: String = entranceAnnouncement
        /** 无法确认，默认为false。 */
        override val top: Boolean = false
        /** 入群公告总是会发送给新成员。 */
        override val forNew: Boolean = true
        /** 无法确认，默认为false。 */
        override val confirm: Boolean = false
        /** 无法确认，默认为false。 */
        override val issuingTime: Long = -1
        override val originalData: String = "MiraiGroupNote(text=$text)"
    }
}
