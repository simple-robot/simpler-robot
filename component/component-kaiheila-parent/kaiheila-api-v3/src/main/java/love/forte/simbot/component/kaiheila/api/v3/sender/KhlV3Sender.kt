@file:JvmName("KhlV3SenderUtil")

package love.forte.simbot.component.kaiheila.api.v3.sender

import love.forte.common.utils.Carrier
import love.forte.common.utils.toCarrier
import love.forte.simbot.api.message.MessageContent
import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.assists.flag
import love.forte.simbot.api.message.events.GroupMsg
import love.forte.simbot.api.message.events.GroupMsgIdFlagContent
import love.forte.simbot.api.message.events.PrivateMsg
import love.forte.simbot.api.message.events.PrivateMsgIdFlagContent
import love.forte.simbot.api.sender.Sender
import love.forte.simbot.component.kaiheila.KhlBot
import love.forte.simbot.component.kaiheila.KhlMessageContent
import love.forte.simbot.component.kaiheila.MessageType
import love.forte.simbot.component.kaiheila.api.ApiData
import love.forte.simbot.component.kaiheila.api.KhlSender
import love.forte.simbot.component.kaiheila.api.doRequestForData
import love.forte.simbot.component.kaiheila.api.v3.message.MessageCreateReq
import love.forte.simbot.component.kaiheila.api.v3.message.direct.DirectMessageCreateReq


private fun String.toGroupReqs(targetId: String): List<ApiData.Req<*>> {


    TODO()
}

private fun MessageContent.toGroupReqs(targetId: String): List<ApiData.Req<*>> {
    if (this !is KhlMessageContent) {
        return msg.toGroupReqs(targetId)
    }

    TODO()
}


/**
 *
 * @author ForteScarlet
 */
public class KhlV3Sender(
    private val bot: KhlBot,
    private val def: Sender.Def
) : KhlSender.Sender {


    override suspend fun groupMsg(
        parent: String?,
        group: String,
        msg: String,
    ): Carrier<out Flag<GroupMsg.FlagContent>> {
        // Parent cannot be null.
        // requireNotNull(parent) { "Required param 'parent' was null." }


        val resp = MessageCreateReq(
            type = MessageType.TEXT,
            targetId = group,
            content = msg, // TODO
        ).doRequestForData(bot)

        val flag = resp?.msgId?.let {
            flag { GroupMsgIdFlagContent(it) }
        }

        return flag.toCarrier()
    }

    override suspend fun privateMsg(
        code: String,
        group: String?,
        msg: String,
    ): Carrier<out Flag<PrivateMsg.FlagContent>> {
        // 如果group存在，视为chat code，否则使用code
        val req = if (group != null) {
            DirectMessageCreateReq.byChatCode(
                type = MessageType.TEXT, // TODO
                chatCode = group,
                content = msg, // TODO
            )
        } else {
            DirectMessageCreateReq.byTargetId(
                type = MessageType.TEXT, // TODO
                targetId = code,
                content = msg, // TODO
            )

        }

        val resp = req.doRequestForData(bot)

        val flag = resp?.msgId?.let {
            flag { PrivateMsgIdFlagContent(it) }
        }

        return flag.toCarrier()
    }

    @Deprecated("No support in kaiheila.")
    override suspend fun groupNotice(
        group: String,
        title: String?,
        text: String?,
        popUp: Boolean,
        top: Boolean,
        toNewMember: Boolean,
        confirm: Boolean,
    ) = def.groupNotice(group, title, text, popUp, top, toNewMember, confirm)
}