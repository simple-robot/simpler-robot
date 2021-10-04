package love.forte.simbot.component.kaiheila.api.v3.sender

import love.forte.common.utils.Carrier
import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.events.GroupMsg
import love.forte.simbot.api.message.events.PrivateMsg
import love.forte.simbot.component.kaiheila.KhlBot
import love.forte.simbot.component.kaiheila.api.KhlSender


/**
 *
 * @author ForteScarlet
 */
public class KhlV3Sender(private val khlBot: KhlBot) : KhlSender.Sender {

    override suspend fun groupMsg(
        parent: String?,
        group: String,
        msg: String,
    ): Carrier<out Flag<GroupMsg.FlagContent>> {
        TODO("Not yet implemented")
    }

    override suspend fun privateMsg(
        code: String,
        group: String?,
        msg: String,
    ): Carrier<out Flag<PrivateMsg.FlagContent>> {
        TODO("Not yet implemented")
    }

    override suspend fun groupNotice(
        group: String,
        title: String?,
        text: String?,
        popUp: Boolean,
        top: Boolean,
        toNewMember: Boolean,
        confirm: Boolean,
    ): Carrier<Boolean> {
        TODO("Not yet implemented")
    }
}