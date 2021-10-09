package love.forte.simbot.api.message

import love.forte.simbot.api.Component
import love.forte.simbot.api.SimbotComponent
import kotlin.jvm.JvmStatic


/**
 * 一些十分常见的标准 [Message].
 *
 * [StandardMessage] 目前仅作为标记用，无实际作用。
 *
 */
public sealed interface StandardMessage


public fun String.toText(): Text = Text.getText(this)
public fun Text(): Text = Text.getEmptyText()
public inline fun Text(block: () -> String): Text = block().toText()

/**
 * 文本消息.
 */
public data class Text internal constructor(public val text: String) : MergeableMessage<Text> {
    override val key: MergeableMessage.Key<Text>
        get() = Key

    override fun merge(other: Text): Text {
        if (text.isEmpty()) return other
        if (other == empty) return this

        return Text(text + other.text)
    }

    public operator fun plus(other: Text): Text = merge(other)
    public operator fun plus(other: String): Text = if (text.isEmpty()) Text(other) else Text(text + other)

    override fun toString(): String = "Text(text=$text)"

    public companion object Key : BaseMessageKey<Text>(), MergeableMessage.Key<Text> {
        private val empty = Text("")
        override val component: Component get() = SimbotComponent

        @JvmStatic
        public fun getText(text: String): Text {
            return if (text.isEmpty()) empty
            else Text(text)
        }

        @JvmStatic
        public fun getEmptyText(): Text = empty
    }

}






