package love.forte.simbot.api.message

import love.forte.simbot.api.Component
import love.forte.simbot.api.SimbotComponent


/**
 * 一些十分常见的标准 [Message].
 *
 * [StandardMessage] 目前仅作为标记用，无实际作用。
 *
 */
public sealed interface StandardMessage


/**
 * 文本消息.
 */
public class Text(public val text: String) : UniqueMessage<Text> {
    override val key: Message.Key<Text>
        get() = Key



    public companion object Key : BaseMessageKey<Text>() {
        override val component: Component get() = SimbotComponent
    }

}






