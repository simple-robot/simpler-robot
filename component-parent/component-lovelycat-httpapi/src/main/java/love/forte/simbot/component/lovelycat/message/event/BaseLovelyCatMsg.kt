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
import love.forte.simbot.api.message.containers.GroupInfo
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.component.lovelycat.LovelyCatApiTemplate
import love.forte.simbot.component.lovelycat.message.event.LovelyCatMsg.Companion.NON_TYPE
import love.forte.simbot.serialization.json.JsonSerializerFactory
import kotlin.reflect.KClass


public val lovelyCatEventTypes: Map<String, KClass<out BaseLovelyCatMsg>> =
    mapOf(
        "EventLogin" to LovelyCatEventLogin::class,
    )


/**
 * LovelyCat msg.
 * @property event String
 */
public interface LovelyCatMsg : MsgGet {
    val event: String

    /**
     * 类型。如果是-999则代表没有此值。
     */
    val type: Int


    companion object {
        /** 当不存在一个 'type' 的时候使用此值。 */
        const val NON_TYPE = -999
    }
}


/**
 * lovelyCat 基础的事件父类。
 *
 * @property event 事件的名称
 */
public abstract class BaseLovelyCatMsg(override val event: String, override val originalData: String) : LovelyCatMsg {

    override val type: Int
        get() = NON_TYPE

    // /**
    //  * 接收到的消息。某些事件中也可能是 'json_msg'
    //  */
    // abstract val msg: String


    /** 当前监听事件消息的ID。一般情况下应当是一个唯一ID。 */
    override val id: String = ""


    /** 消息接收到的时候的事件戳。可能会与实际接收到消息的时间有所偏差。 */
    override val time: Long = System.currentTimeMillis() - 10


    /** 应当重写toString方法 */
    override fun toString(): String {
        return "LovelyCat(event=$event, originalData=$originalData)"
    }


    /** DataMapping. */
    abstract class LovelyCatDataMapping<out T : LovelyCatMsg> {
        abstract fun mapTo(
            originalData: String,
            api: LovelyCatApiTemplate?,
            jsonSerializerFactory: JsonSerializerFactory
        ): T
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
 * lovelyCat groupInfo.
 */
@JvmOverloads
public fun lovelyCatGroupInfo(
    groupCode: String,
    groupName: String? = null,
    groupAvatar: String? = null
): GroupInfo = LovelyCatGroupInfo(groupCode, groupName, groupAvatar)


/**
 * simple data class instance for [GroupInfo].
 */
private data class LovelyCatGroupInfo(
    override val groupCode: String,
    override val groupName: String?,
    override val groupAvatar: String?
) : GroupInfo


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
): BotInfo = LovelyCatBotInfo(botCode, botName, botAvatar)


public fun lovelyCatBotInfo(
    botCode: String, api: LovelyCatApiTemplate?
): BotInfo = LovelyCatBotInfo(
    botCode,
    api?.getRobotName(botCode)?.botName ?: "",
    api?.getRobotHeadImgUrl(botCode)?.botAvatar
)

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



@Suppress("NOTHING_TO_INLINE")
public inline fun Map<String, *>.orParamErr(param: String): Any {
    return this[param] ?: throw IllegalStateException("missing param: $param")
}
