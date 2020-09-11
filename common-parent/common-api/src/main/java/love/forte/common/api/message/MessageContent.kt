/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     MessageContent.kt
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

package love.forte.common.api.message

/*
 *
 * 定义消息正文接口及部分实现类
 *
 * @author ForteScarlet <ForteScarlet@163.com>
 * @date 2020/9/2
 * @since
 */


/**
 * **消息内容**。
 *
 * 它被使用在[MessageEventGet] 接口的 [MessageEventGet.msgContent] 上，表示当前消息的正文内容。
 *
 * 一个 [MessageContent] 实例至少应该保证能够得到当前消息的 [消息字符串文本][msg]。
 */
public interface MessageContent {
    /**
     * 消息字符串文本。一般来讲，如果存在一些特殊消息，
     * 那么他们会作为 **CQ码** 字符串存在于消息中。
     */
    var msg: String?

    /**
     * 除了使用字符串文本的 [setMsg][msg] 以外，你也可以通过 [setMsg] 来将一些可能为非字符串的内容设置到一个 [MessageContent] 中。
     * @param msgContent MessageContent
     */
    fun setMsg(msgContent: MessageContent)

    /**
     * 一个 [消息正文][MessageContent] 应当可以与其他消息进行拼接，并得到一个新的 [MessageContent]
     * @param msgContent MessageContent
     * @return MessageContent
     */
    operator fun plus(msgContent: MessageContent): MessageContent
}



/**
 * 一个以字符串消息为主体的 [MessageContent] 默认实现类
 */
public data class TextMessageContent(override var msg: String?) : MessageContent {
    /**
     * 通过其他的 [MessageContent] 来设置文本正文。
     * 将会直接将其中的 [msg] 赋值
     */
    override fun setMsg(msgContent: MessageContent) {
        msg = msgContent.msg
    }

    /**
     * 一个 [消息正文][MessageContent] 应当可以与其他消息进行拼接，并得到一个新的 [MessageContent]
     *
     * 将会直接进行字符串拼接
     */
    override fun plus(msgContent: MessageContent): MessageContent {
        return when {
            this.msg === null && msgContent.msg === null ->  TextMessageContent(null)
            this.msg === null -> TextMessageContent(msgContent.msg)
            msgContent.msg === null -> this.copy()
            else -> TextMessageContent(this.msg + msgContent.msg)
        }
    }
}






