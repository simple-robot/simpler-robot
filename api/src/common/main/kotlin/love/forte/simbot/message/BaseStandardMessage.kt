/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.message

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.Component
import love.forte.simbot.SimbotComponent
import kotlin.js.JsName
import kotlin.jvm.JvmStatic
import kotlin.reflect.KClass

/**
 * 一些由核心提供的标准 [Message] 实例或标准.
 */
public sealed interface StandardMessage<E : Message.Element<E>> : Message.Element<E>


@Suppress("CanBeParameter")
public sealed class BaseStandardMessage<E : Message.Element<E>> : StandardMessage<E>


/** 判断一个 [Message.Element] 是否为一个标准 [Message] 下的实现。 */
public inline val Message.Element<*>.isStandard: Boolean get() = this is StandardMessage


//region Text
/**
 * 一个文本消息 [Text].
 */
@Serializable
@SerialName("std.text")
public open class Text protected constructor(public val text: String) : BaseStandardMessage<Text>() {
    override val key: Message.Key<Text> get() = Key

    public operator fun plus(other: Text): Text = when {
        text.isEmpty() -> other
        other.text.isEmpty() -> this
        else -> Text(text + other.text)
    }

    public operator fun plus(other: String): Text = if (text.isEmpty()) Text(other) else Text(text + other)
    override fun toString(): String = "Text($text)"
    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is Text) return false
        return text == other.text
    }

    override fun hashCode(): Int = text.hashCode()

    public companion object Key : Message.Key<Text> {
        private val empty = Text("")
        override val component: Component get() = SimbotComponent
        override val elementType: KClass<Text> get() = Text::class

        override fun safeCast(instance: Any?): Text? = doSafeCast<Text>(instance)

        @JvmStatic
        public fun getText(text: String): Text {
            return if (text.isEmpty()) empty
            else Text(text)
        }

        @JvmStatic
        public fun getEmptyText(): Text = empty
    }

}


public fun String.toText(): Text = Text.getText(this)

@JsName("emptyText")
public fun Text(): Text = Text.getEmptyText()

@JsName("getText")
public inline fun Text(block: () -> String): Text = block().toText()
//endregion


//region At
/**
 * 一个 `at` 的标准接口。
 * at、或者说一个通知信息，其无论如何都应有一个通知目标 [target].
 *
 * 此At所代表的是针对一个人或者一个用户所发出的通知消息，如果存在例如通知全体、通知某权限下所有人等消息，
 * 可以考虑基于此实现多个。
 *
 * 应当支持[序列化][Serializable]。
 *
 */
public interface At<TARGET, A : Message.Element<A>> : StandardMessage<A> {
    public val target: TARGET
}
//endregion







