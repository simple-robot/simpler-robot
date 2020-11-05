package love.forte.test.lis

import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.assists.FlagImpl
import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.api.message.events.MessageContent
import love.forte.simbot.api.message.events.PrivateMsg
import love.forte.simbot.api.message.events.TextMessageContent


public object TestPrivateMsg : PrivateMsg {
    override var msg: String?
        get() = msgContent.msg
        set(value) {}
    override val privateMsgType: PrivateMsg.Type = PrivateMsg.Type.FRIEND
    override val flag: Flag<PrivateMsg.FlagContent> = FlagImpl(TestPrivateMsgFlagContent)
    override val id: String = "test-pri"
    override val time: Long = System.currentTimeMillis()
    override fun toString(): String = "TestPrivateMsg(...)"
    override var msgContent: MessageContent = TextMessageContent("hello! [CAT:at,code=123456]")
    override val originalData: String = toString()
    override val botInfo: BotInfo = TestBotInfo
    override val accountInfo: AccountInfo = TestAccountInfo
}


/**
 * flag id.
 */
public object TestPrivateMsgFlagContent : PrivateMsg.FlagContent {
    override val id: String = "1"
}


public object TestBotInfo : BotInfo {
    /** 当前的bot的账号 */
    override val botCode: String = "123456"

    /**
     * 机器人的名称
     */
    override val botName: String = "forte"

    /** 机器人的头像 */
    override val botAvatar: String? = ""
}

public object TestAccountInfo : AccountInfo {
    /**
     * 账号
     */
    override val accountCode: String = "666666"

    /** 昵称。可能会出现为null的情况 */
    override val accountNickname: String? = "forli"

    /** 好友备注或群名片。可能为null */
    override val accountRemark: String? = null

    /**
     * 得到账号的头像地址. 一般来讲为`null`的可能性很小
     */
    override val accountAvatar: String? = ""
}