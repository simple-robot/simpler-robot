/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */
@file:JvmName("LovelyCatReceivedTransferEvents")
package love.forte.simbot.component.lovelycat.message.event

import love.forte.simbot.api.message.containers.AccountContainer
import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.containers.BotContainer
import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.component.lovelycat.LovelyCatApiTemplate
import love.forte.simbot.serialization.json.JsonSerializerFactory
import java.math.BigDecimal

public const val RECEIVED_TRANSFER_EVENT = "EventReceivedTransfer"

/**
 *
 * 可爱猫转账事件接口。
 *
 * 其继承 [Bot容器][BotContainer]、[账号容器][AccountContainer]
 *
 * @author ForteScarlet
 */
public interface LovelyCatReceivedTransfer: LovelyCatMsg, BotContainer, AccountContainer {
    /**
     * 转账金额。字符串类型。
     * 如果需要使用数字类型，可参考 [TransferInfo.money].
     * */
    val money: String

    /**
     * 转账信息。
     */
    val transferInfo: TransferInfo

    /**
     * 接受转账。
     */
    fun accept()

}


/**
 * 事件名=EventReceivedTransfer	收到转账事件（收到好友转账时，运行这里）
 * robot_wxid, 文本型, , 机器人账号ID（就是这条消息是哪个机器人的，因为可能登录多个机器人）
 * from_wxid, 文本型, , 来源用户ID
 * from_name, 文本型, , 来源用户昵称
 * to_wxid, 文本型, , 接收消息的人ID（一般都是机器人收到了，所以这里是机器人ID，如果是机器人发出的，也就是接收转账，这里就是对方的ID）
 * money, 文本型, , 金额
 * json_msg, 文本型, , 收到转账的详细JSON信息，具体JSON结构请查看日志
 */
public class LovelyCatReceivedTransferEvent(
    override val robotWxid: String,
    private val fromWxid: String,
    fromName: String,
    // toWxid: String,
    override val money: String,
    private val jsonMsg: String,
    override val transferInfo: TransferInfo,
    private val api: LovelyCatApiTemplate,
    originalData: String
) : BaseLovelyCatMsg(RECEIVED_TRANSFER_EVENT, originalData), LovelyCatReceivedTransfer {
    /**
     * 转账事件不存在文本
     */
    override val text: String?
        get() = null

    /**
     * bot信息
     */
    override val botInfo: BotInfo = lovelyCatBotInfo(robotWxid, api)

    /**
     * 账号的信息。一般来讲是不可能为null的，但是其中的信息就不一定了
     */
    override val accountInfo: AccountInfo = lovelyCatAccountInfo(fromWxid, fromName)

    /**
     * 接受转账。
     */
    override fun accept() {
        api.acceptTransfer(robotWxid, fromWxid, jsonMsg)
    }
}


/**
 * [转账事件][LovelyCatReceivedTransfer] 构建器。
 */
public object LovelyCatReceivedTransferEventParser : LovelyCatEventParser {
    override fun invoke(
        original: String,
        api: LovelyCatApiTemplate,
        jsonSerializerFactory: JsonSerializerFactory,
        params: Map<String, *>
    ): LovelyCatReceivedTransfer = LovelyCatReceivedTransferEvent(
        params.orParamErr("robot_wxid").toString(),
        params.orParamErr("from_wxid").toString(),
        params.orParamErr("from_name").toString(),
        // params.orParamErr("to_wxid").toString(),
        params.orParamErr("money").toString(),
        params.orParamErr("json_msg").toString(),
        jsonSerializerFactory.getJsonSerializer(TransferInfo::class.java).fromJson(params.orParamErr("json_msg").toString()),
        api, original
    )
}


/**
 * 转账json信息。
 *
 */
public data class TransferInfo(
    /*
    {
        "paysubtype": "3",
        "is_arrived": 1,
        "is_received": 1,
        "receiver_pay_id": "114514191981011451419198101145141919810",
        "payer_pay_id": "114514191981011451419198101145141919810",
        "money": "0.01",
        "remark": "备注"
    }
     */
    val paysubtype: String,
    val isArrived: Int,
    val isReceived: Int,
    val receiverPayId: String,
    val payerPayId: String,
    val money: BigDecimal,
    val remark: String,
)
