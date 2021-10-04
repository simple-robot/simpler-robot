@file:JvmName("MessageEventMessageContentUtil")

package love.forte.simbot.component.kaiheila.event.message

import catcode.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialKind
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import love.forte.simbot.api.message.MessageContent
import love.forte.simbot.component.kaiheila.khlJson
import love.forte.simbot.component.kaiheila.objects.Attachments
import love.forte.simbot.component.kaiheila.objects.Role
import love.forte.simbot.component.kaiheila.utils.TextNeko
import java.util.*

/**
 * [TextEventImpl]'s MessageContent. 纯文本与mention的消息正文。
 *
 */
public fun textEventMessageContent(content: String, extra: TextEventExtra): MessageContent {
    val mentioned: Boolean =
        extra.mentionHere || extra.mentionAll || extra.mention.isNotEmpty() || extra.mentionRoles.isNotEmpty()
    return if (mentioned) {
        TextEventMessageContentWithMention(content, extra)
    } else {
        TextEventMessageContent(content)
    }
}

/**
 * 仅有纯文本的消息正文。
 */
internal data class TextEventMessageContent(internal val content: String) : MessageContent {
    override val msg: String = CatEncoder.encodeText(content)
    override val cats: List<Neko> = listOf(TextNeko(content))
    override fun isEmpty(): Boolean = content.isEmpty()
}

/**
 * 纯文本与mention的消息正文。
 */
internal class TextEventMessageContentWithMention(private val content: String, private val extra: TextEventExtra) :
    MessageContent {

    private val mentionList: List<Neko> = extra.toNekoList()
    override val cats: List<Neko> = mentionList + TextNeko(content)
    override val msg: String = mentionList.joinToString("") + CatEncoder.encodeText(content)

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }

        if (other is TextEventMessageContent) {
            return if (mentionList.isEmpty()) content == other.content else false
        }

        if (other is TextEventMessageContentWithMention) {
            if (content == other.content) {
                val oMention = extra.mention.toSet()
                val oMentionAll = extra.mentionAll
                val oMentionHere = extra.mentionHere
                val oMentionRoles = extra.mentionRoles.mapTo(mutableSetOf(), Role::roleId)

                return oMentionAll == extra.mentionAll
                        && oMentionHere == extra.mentionHere
                        && oMention == extra.mention.toSet()
                        && oMentionRoles == extra.mentionRoles.mapTo(mutableSetOf(), Role::roleId)
            }

            return false
        }

        if (other is MessageContent) {
            return msg == other.msg
        }

        return false
    }


    override fun hashCode(): Int = Objects.hash(content, extra)
}


private fun TextEventExtra.toNekoList(): List<Neko> {
    // at all
    // at online
    // at members
    // at roles
    val list = mutableListOf<Neko>()
    if (mentionAll) {
        list.add(CatCodeUtil.nekoTemplate.atAll())
    }
    if (mentionHere) {
        list.add(CatCodeUtil.toNeko("at", "online" cTo true))
    }
    for (m in mention) {
        list.add(CatCodeUtil.nekoTemplate.at(m))
    }
    for (mr in mentionRoles) {
        list.add(mr.toMentionNeko())
    }

    return list
}


public inline fun <reified A : Attachments> attachmentsEventMessageContent(
    attachmentType: String,
    extra: AttachmentsMessageEventExtra<A>,
): MessageContent {
    return attachmentsEventMessageContent(attachmentType, extra, khlJson.serializersModule.serializer())

}


public fun <A : Attachments> attachmentsEventMessageContent(
    attachmentType: String,
    extra: AttachmentsMessageEventExtra<A>,
    serializationStrategy: SerializationStrategy<A>,
): MessageContent {
    return AttachmentsEventMessageContent(attachmentType, extra, serializationStrategy)
}



internal class AttachmentsEventMessageContent<A : Attachments>(
    private val type: String,
    private val extra: AttachmentsMessageEventExtra<A>,
    serializationStrategy: SerializationStrategy<A>,
) : MessageContent {

    override val msg: String
    override val cats: List<Neko>

    init {
        val neko = extra.attachments.toNeko(type, serializationStrategy)
        msg = neko.toString()
        cats = listOf(neko)
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }

        if (other is AttachmentsEventMessageContent<*>) {
            return type == other.type
                    && extra.attachments == other.extra.attachments
        }

        if (other is MessageContent) {
            return msg == other.msg
        }

        return false
    }

    override fun hashCode(): Int = Objects.hash(type, extra)
}


@OptIn(ExperimentalSerializationApi::class)
private fun <T : Attachments> T.toNeko(type: String, serializationStrategy: SerializationStrategy<T>): Neko {
    val builder = CatCodeUtil.getNekoBuilder(type, true)
    serializationStrategy.serialize(AttEncoder(builder), this)
    return builder.build()
}


@OptIn(ExperimentalSerializationApi::class)
private class AttEncoder(
    private val nekoBuilder: CodeBuilder<Neko>,
    override val serializersModule: SerializersModule = khlJson.serializersModule,
) : AbstractEncoder() {

    private var i = 0
    private lateinit var name: String
    private lateinit var kind: SerialKind

    override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
        i = index
        name = descriptor.getElementName(index)
        kind = descriptor.kind
        return true
    }

    override fun encodeValue(value: Any) {
        val key = when (kind) {
            // 暂时不序列化这些东西.
            // PolymorphicKind.OPEN -> "SER_O_$name"
            // PolymorphicKind.SEALED -> "SER_S_$name"
            else -> name
        }
        nekoBuilder.key(key).value(value.toString())
    }

    override fun encodeNull() {
        // Encode null, ignore.
    }

}


private fun Role.toMentionNeko(): Neko {
    return CatCodeUtil.getNekoBuilder("at", false)
        .key("role").value(roleId)
        .key("name").value(CatEncoder.encodeParams(name))
        .key("color").value(color)
        .key("position").value(position)
        .key("hoist").value(hoist)
        .key("mentionable").value(mentionable)
        .key("permissions").value(permissionsValue)
        .build()
}