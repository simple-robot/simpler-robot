/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MiraiMessageSourceFlagContent.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

@file:JvmName("MiraiFlags")

package love.forte.simbot.component.mirai.message

import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.assists.FlagContent
import net.mamoe.mirai.message.data.MessageSource


/**
 * mirai下基于 [MessageSource] 的 [标识主体][FlagContent]
 */
public abstract class MiraiMessageSourceFlagContent : FlagContent {
    abstract val source: MessageSource
    override val id: String
        get() = "${source.fromId}.${source.id}"
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
public interface MiraiMessageFlag<C: MiraiMessageSourceFlagContent> : Flag<C>

/**
 * 标识类型为 [MiraiMessageSourceFlagContent] 的 [Flag] 实例，
 * 可用于mirai的撤回。
 */
internal data class MiraiMessageFlagData<C: MiraiMessageSourceFlagContent>(override val flag: C) : Flag<C>, MiraiMessageFlag<C>
