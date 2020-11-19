/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     BaseLovelyCatMsg.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */
@file:JvmName("LovelyCatEvents")

package love.forte.simbot.component.lovelycat.message.event

import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.api.message.events.MsgGet
import kotlin.reflect.KClass


public val LovelyCatEventTypes: Map<String, KClass<out BaseLovelyCatMsg>> =
    mapOf(
        "EventLogin" to LovelyCatEventLogin::class,
    )


/**
 * lovelyCat 基础的事件父类。
 *
 * @property event 事件的名称
 */
public abstract class BaseLovelyCatMsg(val event: String, override val originalData: String) : MsgGet {
    // abstract val event: String

    /** bot id */
    abstract val botId: String

    /**
     * 类型。如果是-999则代表没有此值。
     */
    open val type: Int = NON_TYPE

    /**
     * 接收到的消息类型。某些事件中也可能是 'json_msg'
     */
    abstract val msg: String


    /** 当前监听事件消息的ID。一般情况下应当是一个唯一ID。 */
    override val id: String = ""

    // /**
    //  * 可以得到一个 **文本**。
    //  *
    //  *  这个文本应当是不包含任何 CAT码 的纯文本消息。
    //  *
    //  *  而关于存在CAT码的特殊消息，可参考 [MessageGet.msg].
    //  *
    //  */
    // override val text: String?
    //     get() = TODO("Not yet implemented")

    /** 消息接收到的时候的事件戳。可能会与实际接收到消息的时间有所偏差。 */
    override val time: Long = System.currentTimeMillis()

    /** 应当重写toString方法 */
    override fun toString(): String {
        return "LovelyCat()"
    }

    // /**
    //  * 得到原始数据字符串。
    //  * 数据不应该为null。
    //  */
    // override val originalData: String get() = msg

    // /**
    //  * bot信息
    //  */
    // override val botInfo: BotInfo
    //     get() = TODO("Not yet implemented")

    // /**
    //  * 账号的信息。一般来讲是不可能为null的，但是其中的信息就不一定了
    //  */
    // override val accountInfo: AccountInfo
    //     get() = TODO("Not yet implemented")


    companion object {
        /** 当不存在一个 'type' 的时候使用此值。 */
        const val NON_TYPE = -999
    }
}






/**
 * 根据具体参数构建一个data class.
 * @param accountCode String
 * @param accountNickname String?
 * @param accountRemark String?
 * @param accountAvatar String?
 * @return AccountInfo
 */
@JvmOverloads
public fun lovelyCatAccountInfo(
    accountCode: String,
    accountNickname: String? = null,
    accountRemark: String? = null,
    accountAvatar: String? = null
): AccountInfo = LovelyCatAccountInfo(accountCode, accountNickname, accountRemark, accountAvatar)


/**
 * simple data class instance for [AccountInfo].
 *
 * @property accountCode String
 * @property accountNickname String?
 * @property accountRemark String?
 * @property accountAvatar String?
 * @constructor
 */
private data class LovelyCatAccountInfo(
    override val accountCode: String,
    override val accountNickname: String?,
    override val accountRemark: String?,
    override val accountAvatar: String?
) : AccountInfo


/**
 * 根据具体参数构建一个data class.
 * @param botCode String
 * @param botName String
 * @param botAvatar String?
 * @return BotInfo
 */
@JvmOverloads
public fun lovelyCatBotInfo(
    botCode: String,
    botName: String,
    botAvatar: String? = null
) : BotInfo = LovelyCatBotInfo(botCode, botName, botAvatar)


/**
 * simple data instance for [BotInfo]
 * @property botCode String
 * @property botName String
 * @property botAvatar String?
 * @constructor
 */
private data class LovelyCatBotInfo(
    override val botCode: String,
    override val botName: String,
    override val botAvatar: String?
) : BotInfo