package love.forte.simbot.component.kaiheila.event.message

import catcode.CatCodeUtil
import catcode.CatEncoder
import catcode.Neko
import catcode.cTo
import love.forte.simbot.api.message.MessageContent
import love.forte.simbot.component.kaiheila.objects.Role
import love.forte.simbot.component.kaiheila.utils.TextNeko
import java.util.*

/**
 * [TextEvent]'s MessageContent. 纯文本与mention的消息正文。
 *
 */
internal fun textEventMessageContent(content: String, extra: TextEventExtra): MessageContent {
    val mentioned: Boolean = extra.mentionHere || extra.mentionAll || extra.mention.isNotEmpty() || extra.mentionRoles.isNotEmpty()
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




internal class AttachmentsEventMessageContent(private val extra: AttachmentsMessageEventExtra): MessageContent {

    override val msg: String
        get() = TODO("Not yet implemented")


    override val cats: List<Neko>
        get() = TODO("Not yet implemented")

    override fun equals(other: Any?): Boolean {
        TODO("Not yet implemented")
    }

    override fun hashCode(): Int {
        return super.hashCode()
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