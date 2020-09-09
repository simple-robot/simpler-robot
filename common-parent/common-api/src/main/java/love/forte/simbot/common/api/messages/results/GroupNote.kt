package love.forte.simbot.common.api.messages.results


/**
 *
 * 群公告信息
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