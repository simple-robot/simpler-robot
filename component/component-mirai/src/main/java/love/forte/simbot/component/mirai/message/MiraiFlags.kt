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
import net.mamoe.mirai.message.data.MessageSource




/**
 * mirai下基于 [MessageSource] 的 [标识主体][FlagContent]
 */
public abstract class MiraiMessageSourceFlagContent : FlagContent {
    abstract val source: MessageSource?
    override val id: String
        // get() = source?.let { "${it.fromId}.${it.ids.joinToString(",")}.${it.internalIds.joinToString(",")}" } ?: "EmptyMiraiMessageFlagContent(source=null)"
        get() = source?.let { it.cacheId } ?: "EmptyMiraiMessageFlagContent(source=null)"
}


public val MessageSource.cacheId get() = "${fromId}.${ids.joinToString(",")}.${internalIds.joinToString(",")}"


private object EmptyMiraiGroupFlagContent : MiraiMessageSourceFlagContent(), GroupMsg.FlagContent {
    override val source: MessageSource? = null
}

public object EmptyMiraiGroupFlag : Flag<GroupMsg.FlagContent> {
    override val flag: GroupMsg.FlagContent
        get() = EmptyMiraiGroupFlagContent
}

private object EmptyMiraiPrivateFlagContent : MiraiMessageSourceFlagContent(), PrivateMsg.FlagContent {
    override val source: MessageSource? = null
}

public object EmptyMiraiPrivateFlag : Flag<PrivateMsg.FlagContent> {
    override val flag: PrivateMsg.FlagContent
        get() = EmptyMiraiPrivateFlagContent
}


/**
 * 获取标识主体的字符串ID。
 */
public val <T : FlagContent> Flag<T>.flagId: String get() = flag.id


public fun <C: MiraiMessageSourceFlagContent> miraiMessageFlag(flag: C): Flag<C> {
    return MiraiMessageFlagData(flag)
}

/* for kt. */
public inline fun <C: MiraiMessageSourceFlagContent> miraiMessageFlag(flag: () -> C): Flag<C> {
    return miraiMessageFlag(flag())
}

/**
 * mirai 消息标识。
 */
public interface MiraiMessageFlag<C: MiraiMessageSourceFlagContent> : Flag<C> {
    /**
     * 获取一个 [mirai消息标识主体][MiraiMessageSourceFlagContent].
     */
    override val flag: C
}

/**
 * 标识类型为 [MiraiMessageSourceFlagContent] 的 [Flag] 实例，
 * 可用于mirai的撤回。
 */
internal data class MiraiMessageFlagData<C: MiraiMessageSourceFlagContent>(override val flag: C) : Flag<C>, MiraiMessageFlag<C>
