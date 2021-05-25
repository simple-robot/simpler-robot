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
import love.forte.simbot.api.message.events.MessageGet
import love.forte.simbot.api.message.events.PrivateMsg
import love.forte.simbot.component.mirai.message.event.MiraiGroupFlagContent
import love.forte.simbot.component.mirai.message.event.MiraiPrivateFlagContent
import net.mamoe.mirai.Bot
import net.mamoe.mirai.event.events.BotEvent
import net.mamoe.mirai.message.data.MessageSource
import net.mamoe.mirai.message.data.MessageSourceBuilder
import net.mamoe.mirai.message.data.MessageSourceKind
import net.mamoe.mirai.message.data.kind


/**
 * mirai下基于 [MessageSource] 的 [标识主体][FlagContent]
 */
public abstract class MiraiMessageSourceFlagContent : FlagContent {
    abstract val source: MessageSource?
    private lateinit var _id: String
    override val id: String
        // get() = source?.let { "${it.fromId}.${it.ids.joinToString(",")}.${it.internalIds.joinToString(",")}" } ?: "EmptyMiraiMessageFlagContent(source=null)"
        // get() = source?.cacheId ?: "EmptyFlagContent(source=null)"
        get() {
            if (!::_id.isInitialized) {
                _id = source?.cacheId ?: "EmptyFlagContent(source=null)"
            }
            return _id
        }
}

/**
 * mirai下基于事件的标识主体。
 * 提供一个 [事件主体][event] 和 [id获取器][idGetter].
 */
public abstract class MiraiEventFlagContent<E : BotEvent>(val event: E, private val idGetter: (E) -> String) :
    FlagContent {
    override val id: String
        get() = idGetter(event)

    override fun toString(): String {
        return "EventFlagContent(event=$event)"
    }
}


/**
 * 消息的缓存ID构成。
 *
 * 2.0.5及之前旧规则：`$fromId.$ids.$internalIds`
 * @since 2.0.6 规则：`$fromId-targetId-kind.ordinal-ids-internalIds-time`
 */
public val MessageSource.cacheId: String
    get() {
        // from
        // target
        // kind
        // ids
        // internalIds
        // time

        val fromIdStr = fromId.str
        val targetIdStr = targetId.str
        val kindStr = kind.ordinal.str
        val idsStr = ids.str
        val internalIdsStr = internalIds.str
        val timeStr = time.str

        return arrayOf(fromIdStr, targetIdStr, kindStr, idsStr, internalIdsStr, timeStr).joinToString(CACHE_ID_SP)
    }

private const val CACHE_ID_SP = "-"
private const val HEX_SP = ":"
private inline val Int.str get() = hexString
private inline val Long.str get() = hexString
private inline val IntArray.str: String get() = joinToString(HEX_SP) { i -> i.str }


public fun Bot.cacheIdToMessageSource(cacheId: String): MessageSource {
    return cacheIdToMessageSourceBuilder(cacheId).let { (builder, kind) -> builder.build(bot.id, kind) }
}


public data class CacheIdMessageSourceData(val builder: MessageSourceBuilder, val kind: MessageSourceKind) {
    fun build(botId: Long) = builder.build(botId, kind)
}

public fun cacheIdToMessageSource(cacheId: String, botId: Long): MessageSource {
    return cacheIdToMessageSourceBuilder(cacheId).build(botId)
}

public fun cacheIdToMessageSourceBuilder(cacheId: String): CacheIdMessageSourceData {
    val cachePart = cacheId.split(CACHE_ID_SP, limit = 6)
    require(cachePart.size == 6) { "CacheId '$cacheId' Part of the structure is missing and cannot be parsed as Mirai-MessageSource." }

    // from     // 0
    // target   // 1
    // kind     // 2
    // ids      // 3
    // internalIds // 4
    // time     // 5

    val kind = MessageSourceKind.values()[cachePart[2].hexInt]

    fun String.intArray(): IntArray = split(HEX_SP).mapToIntArray { s -> s.hexInt }

    val builder = MessageSourceBuilder().apply {
        fromId = cachePart[0].hexLong
        targetId = cachePart[1].hexLong
        ids = cachePart[3].intArray()
        internalIds = cachePart[4].intArray()
        time = cachePart[5].hexInt
    }

    return CacheIdMessageSourceData(builder, kind)
}


public fun <M : MessageGet.MessageFlagContent> Flag<M>.messageSource(botId: Long): MessageSource {
    // 目前仅 message content 可以序列化。事件相关(例如好友申请)无法序列化
    return flag.let { f ->
        if (f is MiraiMessageSourceFlagContent) {
            val s = f.source ?: throw IllegalStateException("Empty message source.")
            if (s.botId != botId) {
                MessageSourceBuilder().metadata(s).apply {
                    fromId = s.fromId
                    targetId = s.targetId
                }.build(botId, s.kind)
            } else s
        } else {
            cacheIdToMessageSource(f.id, botId)
        }
    }
}


private object EmptyMiraiGroupFlagContent : MiraiMessageSourceFlagContent(), GroupMsg.FlagContent {
    override val source: MessageSource? = null
}

public object EmptyMiraiGroupFlag : MessageGet.MessageFlag<GroupMsg.FlagContent> {
    override val flag: GroupMsg.FlagContent
        get() = EmptyMiraiGroupFlagContent
}

private object EmptyMiraiPrivateFlagContent : MiraiMessageSourceFlagContent(), PrivateMsg.FlagContent {
    override val source: MessageSource? = null
}

public object EmptyMiraiPrivateFlag : MessageGet.MessageFlag<PrivateMsg.FlagContent> {
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
public interface MiraiMessageFlag<C : MiraiMessageSourceFlagContent> {
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

public interface MiraiPrivateMsgFlag : MiraiMessageFlag<MiraiPrivateFlagContent>, MessageGet.MessageFlag<PrivateMsg.FlagContent>

/**
 * 标识类型为 [MiraiMessageSourceFlagContent] 的 [Flag] 实例，
 * 可用于mirai的撤回。
 */
internal data class MiraiPrivateMsgFlagData(override val flag: MiraiPrivateFlagContent) : MiraiPrivateMsgFlag {
    override val flagSource: MiraiPrivateFlagContent
        get() = flag
}

public interface MiraiGroupMsgFlag : MiraiMessageFlag<MiraiGroupFlagContent>, MessageGet.MessageFlag<GroupMsg.FlagContent>


internal data class MiraiGroupMsgFlagData(override val flag: MiraiGroupFlagContent) : MiraiGroupMsgFlag {
    override val flagSource: MiraiGroupFlagContent
        get() = flag
}


private inline fun <T> Collection<T>.mapToIntArray(mapper: (T) -> Int): IntArray {
    return IntArray(size).also { arr ->
        forEachIndexed { i, t -> arr[i] = mapper(t) }
    }
}

private inline val Long.hexString: String get() = java.lang.Long.toHexString(this)
private inline val Int.hexString: String get()  = Integer.toHexString(this)
private inline val String.hexInt: Int get() = Integer.parseUnsignedInt(this, 16)
private inline val String.hexLong: Long get() = java.lang.Long.parseUnsignedLong(this, 16)
