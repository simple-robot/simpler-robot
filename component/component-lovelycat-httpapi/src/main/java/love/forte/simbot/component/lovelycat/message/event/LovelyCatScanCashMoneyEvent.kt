/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
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

@file:JvmName("LovelyCatScanCashMoneyEvents")
package love.forte.simbot.component.lovelycat.message.event

import love.forte.common.utils.Millis
import love.forte.common.utils.timeAs
import love.forte.common.utils.timeBy
import love.forte.simbot.api.message.containers.AccountContainer
import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.containers.BotContainer
import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.component.lovelycat.LovelyCatApiTemplate
import love.forte.simbot.serialization.json.JsonSerializerFactory
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

public const val SCAN_CASH_MONEY_EVENT = "EventScanCashMoney"



/**
 * 面对面收款事件。
 *
 * 在被扫码、退出支付页面、进行支付三个情况下都会触发事件，
 * 具体的区别可能在于 [payInfo] 和 [paySourceInfo] 的部分数据细节。
 *
 * 事件名=EventScanCashMoney
 * @author ForteScarlet
 */
public interface LovelyCatScanCashMoney: LovelyCatMsg, BotContainer, AccountContainer {


    /** 收款金额。 */
    val money: String

    /** 支付来源账号信息。 */
    val paySourceInfo: ScanCashMoneyPaySourceInfo

    /** 支付详细信息。 */
    val payInfo: ScanCashMoneyPayInfo

    /**
     * 文本消息即为 [payInfo] 中的 [备注信息][ScanCashMoneyPayInfo.remark]。
     * 可能为null。
     *
     * */
    
    override val text: String?
        get() = payInfo.remark

    /** 账号信息等同于 [paySourceInfo]. */
    
    override val accountInfo: AccountInfo
        get() = paySourceInfo

}

/**
 * 支付消息来源信息。可作为一个 [AccountInfo]。
 */
public data class ScanCashMoneyPaySourceInfo(
    /** 消息来源id */
    val payWxid: String,
    /** 消息来源昵称 */
    val payName: String
): AccountInfo {
    /**
     * 账号
     */
    override val accountCode: String
        get() = payWxid

    /**
     * 昵称。
     */
    override val accountNickname: String
        get() = payName
    override val accountRemark: String?
        get() = null
    override val accountAvatar: String?
        get() = null
}

/*
{
	"to_wxid": "wxid_bqy1ezxxkdat22",
	"msgid": 1706997797,
	"received_money_index": "1" // null able,
	"money": "0.01",
	"total_money": "0.01",
	"remark": "这可是备注",
	"scene_desc": "个人收款完成",
	"scene": 3,
	"timestamp": 1606402472
}
 */

/**
 * 转账详细信息
 */
public data class ScanCashMoneyPayInfo(
    val toWxid: String,
    val msgid: Long,
    val receivedMoneyIndex: String?,
    val money: BigDecimal?,
    val totalMoney: BigDecimal?,
    val remark: String?,
    val sceneDesc: String,
    val scene: Int,
    /** 秒值时间戳 */
    val timestamp: Long
) {
    /** 毫秒值时间戳 */
    val milliTimestamp: Long = timestamp timeBy TimeUnit.SECONDS timeAs Millis
}


/*
事件名=EventScanCashMoney	面对面收款（二维码收款时，运行这里）
robot_wxid, 文本型, , 收钱的人
pay_wxid, 文本型, , 消息来源id
pay_name, 文本型, , 消息来源昵称
money, 文本型, , 金额
json_msg, 文本型, , 更多详细的收款信息，具体JSON结构请查看日志
 */

public class LovelyCatScanCashMoneyEvent(
    override val robotWxid: String,
    payWxid: String,
    payName: String,
    override val money: String,
    override val payInfo: ScanCashMoneyPayInfo,
    api: LovelyCatApiTemplate,
    originalData: String
) : BaseLovelyCatMsg(SCAN_CASH_MONEY_EVENT, originalData), LovelyCatScanCashMoney {

    /**
     * bot信息
     */
    override val botInfo: BotInfo = lovelyCatBotInfo(robotWxid, api)

    /** 支付来源账号信息。 */
    override val paySourceInfo: ScanCashMoneyPaySourceInfo = ScanCashMoneyPaySourceInfo(payWxid, payName)
}

/**
 * [LovelyCatScanCashMoneyEvent] 解析器。
 */
public object LovelyCatScanCashMoneyEventParser: LovelyCatEventParser {
    override fun invoke(
        original: String,
        api: LovelyCatApiTemplate,
        jsonSerializerFactory: JsonSerializerFactory,
        params: Map<String, *>
    ): LovelyCatScanCashMoney {

        val jsonMsg = params.orParamErr("json_msg").toString()

        return LovelyCatScanCashMoneyEvent(
            params.orParamErr("robot_wxid").toString(),
            params.orParamErr("pay_wxid").toString(),
            params.orParamErr("pay_name").toString(),
            params.orParamErr("money").toString(),
            jsonSerializerFactory.getJsonSerializer(ScanCashMoneyPayInfo::class.java).fromJson(jsonMsg),
            // jsonMsg,
            api, original
        )
    }

    override fun type(): Class<out LovelyCatMsg> = LovelyCatScanCashMoney::class.java
}