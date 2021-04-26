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

@file:JvmName("MiraiFlags")
package love.forte.simbot.component.mirai.message

import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.assists.FlagContent
import love.forte.simbot.api.message.events.GroupMsg
import love.forte.simbot.api.message.events.PrivateMsg
import love.forte.simbot.component.mirai.message.event.MiraiGroupFlagContent
import love.forte.simbot.component.mirai.message.event.MiraiPrivateFlagContent
import net.mamoe.mirai.message.data.MessageSource




/**
 * mirai下基于 [MessageSource] 的 [标识主体][FlagContent]
 */
public abstract class MiraiMessageSourceFlagContent : FlagContent {
    abstract val source: MessageSource?
    override val id: String
        // get() = source?.let { "${it.fromId}.${it.ids.joinToString(",")}.${it.internalIds.joinToString(",")}" } ?: "EmptyMiraiMessageFlagContent(source=null)"
        get() = source?.cacheId ?: "EmptyMiraiMessageFlagContent(source=null)"
}


public val MessageSource.cacheId get() = "${fromId}.${ids.joinToString(",")}.${internalIds.joinToString(",")}"


private object EmptyMiraiGroupFlagContent : MiraiMessageSourceFlagContent(), GroupMsg.FlagContent {
    override val source: MessageSource? = null
}

public object EmptyMiraiGroupFlag : GroupMsg.MessageFlag {
    override val flag: GroupMsg.FlagContent
        get() = EmptyMiraiGroupFlagContent
}

private object EmptyMiraiPrivateFlagContent : MiraiMessageSourceFlagContent(), PrivateMsg.FlagContent {
    override val source: MessageSource? = null
}

public object EmptyMiraiPrivateFlag : PrivateMsg.MessageFlag {
    override val flag: PrivateMsg.FlagContent
        get() = EmptyMiraiPrivateFlagContent
}


/**
 * 获取标识主体的字符串ID。
 */
public val <T : FlagContent> Flag<T>.flagId: String get() = flag.id

//
// public fun <C: MiraiMessageSourceFlagContent> miraiMessageFlag(flag: C): Flag<C> {
//     return MiraiMessageFlagData(flag)
// }
//
// /* for kt. */
// public inline fun <C: MiraiMessageSourceFlagContent> miraiMessageFlag(flag: () -> C): Flag<C> {
//     return miraiMessageFlag(flag())
// }

public fun miraiGroupFlag(flag: MiraiGroupFlagContent): MiraiGroupMsgFlag {
    return MiraiGroupMsgFlagData(flag)
}

/* for kt. */
public inline fun miraiGroupFlag(flag: () -> MiraiGroupFlagContent): MiraiGroupMsgFlag {
    return miraiGroupFlag(flag())
}


public fun miraiPrivateFlag(flag: MiraiPrivateFlagContent): MiraiPrivateMsgFlag {
    return MiraiPrivateMsgFlagData(flag)
}

/* for kt. */
public inline fun miraiPrivateFlag(flag: () -> MiraiPrivateFlagContent): MiraiPrivateMsgFlag {
    return miraiPrivateFlag(flag())
}

/**
 * mirai 消息标识, 此接口应当与 [Flag] 相关接口一同实现。
 */
public interface MiraiMessageFlag<C: MiraiMessageSourceFlagContent> {
    /**
     * 获取一个 [mirai消息标识主体][MiraiMessageSourceFlagContent].
     */
    val flagSource: C
}

//
// /**
//  * mirai 消息标识。
//  */
// public interface MiraiMessageFlag<C: MiraiMessageSourceFlagContent> : Flag<C> {
//     /**
//      * 获取一个 [mirai消息标识主体][MiraiMessageSourceFlagContent].
//      */
//     override val flag: C
// }

public interface MiraiPrivateMsgFlag : MiraiMessageFlag<MiraiPrivateFlagContent>, PrivateMsg.MessageFlag

/**
 * 标识类型为 [MiraiMessageSourceFlagContent] 的 [Flag] 实例，
 * 可用于mirai的撤回。
 */
internal data class MiraiPrivateMsgFlagData(override val flag: MiraiPrivateFlagContent) : MiraiPrivateMsgFlag {
    override val flagSource: MiraiPrivateFlagContent
        get() = flag
}

public interface MiraiGroupMsgFlag : MiraiMessageFlag<MiraiGroupFlagContent>, GroupMsg.MessageFlag


internal data class MiraiGroupMsgFlagData(override val flag: MiraiGroupFlagContent) : MiraiGroupMsgFlag {
    override val flagSource: MiraiGroupFlagContent
        get() = flag
}

