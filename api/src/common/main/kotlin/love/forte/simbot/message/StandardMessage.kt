package love.forte.simbot.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.Component
import love.forte.simbot.SimbotComponent
import kotlin.jvm.JvmStatic
import kotlin.reflect.KClass


/**
 * 一些十分常见的标准 [Message].
 *
 * [StandardMessage] 目前仅作为标记用，无实际作用。
 *
 */
@Serializable
public sealed class StandardMessage<E : Message.Element<E>> : Message.Element<E>


public fun String.toText(): Text = Text.getText(this)
public fun Text(): Text = Text.getEmptyText()
public inline fun Text(block: () -> String): Text = block().toText()

/**
 * 文本消息.
 */
@Serializable
@SerialName("std.text")
public data class Text internal constructor(public val text: String) : StandardMessage<Text>() {



    override val key: Message.Key<Text>
        get() = Key

    public operator fun plus(other: Text): Text = when {
        text.isEmpty() -> other
        other.text.isEmpty() -> this
        else -> Text(text + other.text)
    }

    public operator fun plus(other: String): Text = if (text.isEmpty()) Text(other) else Text(text + other)
    override fun toString(): String = "Text($text)"

    public companion object Key : Message.Key<Text> {
        private val empty = Text("")
        override val component: Component get() = SimbotComponent
        override fun isReject(otherKeys: Set<Message.Key<*>>): Boolean = false
        override fun case(instance: Any): Text? = if (instance is Text) instance else null
        override val elementType: KClass<Text> get() = Text::class

        @JvmStatic
        public fun getText(text: String): Text {
            return if (text.isEmpty()) empty
            else Text(text)
        }

        @JvmStatic
        public fun getEmptyText(): Text = empty
    }

}





