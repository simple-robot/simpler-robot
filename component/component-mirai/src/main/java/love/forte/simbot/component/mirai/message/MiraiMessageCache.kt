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

@file:JvmName("MiraiMessageCaches")
package love.forte.simbot.component.mirai.message

import love.forte.common.collections.LRULinkedHashMap
import love.forte.simbot.api.message.events.GroupMsg
import love.forte.simbot.api.message.events.PrivateMsg
import net.mamoe.mirai.event.events.MessageRecallEvent
import java.util.concurrent.locks.StampedLock


/**
 *
 * mirai 消息缓存。
 *
 * 由于 mirai 的 [消息撤回事件][MessageRecallEvent] 不会提供被撤回的消息内容实体，
 * 因此需要对接收过的消息进行缓存。
 *
 * 目前提供两个类型的消息缓存：私聊消息与群消息。
 *
 * @author ForteScarlet
 */
public interface MiraiMessageCache {

    /**
     * 缓存一个群消息实例。
     */
    fun cacheGroupMsg(id: String, msg: GroupMsg)


    /**
     * 缓存一个私聊消息实例。
     */
    fun cachePrivateMsg(id: String, msg: PrivateMsg)


    /**
     * 获取群聊消息缓存。
     */
    fun getGroupMsg(id: String): GroupMsg?


    /**
     * 获取私聊消息缓存。
     */
    fun getPrivateMsg(id: String): PrivateMsg?

}

public fun emptyMiraiMessageCache(): MiraiMessageCache = EmptyMiraiMessageCache

/**
 * [MiraiMessageCache] 的无效化实现，不存在实例缓存。
 */
private object EmptyMiraiMessageCache : MiraiMessageCache {
    override fun cacheGroupMsg(id: String, msg: GroupMsg) { }
    override fun cachePrivateMsg(id: String, msg: PrivateMsg) { }
    override fun getGroupMsg(id: String): GroupMsg? = null
    override fun getPrivateMsg(id: String): PrivateMsg? = null
}


/**
 * 基于 `LRUHashMap` 的简易缓存器。需要提供 [最大容量][category] 和 初始化 [Map] 用的初始化容量 `initialCapacity`。
 * 会存在整体性读写锁竞争，效率一般。
 */
public class LRUMiraiMessageCache(priCapacity: Int, priInitialCapacity: Int, priLoadFactor: Float,
                                  groCapacity: Int, groInitialCapacity: Int, groLoadFactor: Float) : MiraiMessageCache {

    /**
     * 群消息缓存器。
     */
    private val privateCacheMap: LRULinkedHashMap<String, PrivateMsg> = LRULinkedHashMap(priCapacity, priInitialCapacity, priLoadFactor)

    /**
     * 私聊消息缓存器。
     */
    private val groupCacheMap: LRULinkedHashMap<String, GroupMsg> = LRULinkedHashMap(groCapacity, groInitialCapacity, groLoadFactor)


    private val privateLock: StampedLock = StampedLock()
    private val groupLock: StampedLock = StampedLock()


    override fun cacheGroupMsg(id: String, msg: GroupMsg) {
        val stamp = groupLock.writeLock()
        try {
            groupCacheMap[id] = msg
        } finally {
            groupLock.unlock(stamp)
        }
    }

    override fun cachePrivateMsg(id: String, msg: PrivateMsg) {
        val stamp = privateLock.writeLock()
        try {
            privateCacheMap[id] = msg
        } finally {
            privateLock.unlock(stamp)
        }
    }



    override fun getGroupMsg(id: String): GroupMsg? {
        var stamp = groupLock.tryOptimisticRead()
        val read = groupCacheMap[id]
        return if (groupLock.validate(stamp)) {
            read
        } else {
            stamp = groupLock.readLock()
            try {
                groupCacheMap[id]
            } finally {
                groupLock.unlockRead(stamp)
            }
        }
    }

    override fun getPrivateMsg(id: String): PrivateMsg? {
        var stamp = privateLock.tryOptimisticRead()
        val read = privateCacheMap[id]
        return if (privateLock.validate(stamp)) {
            read
        } else {
            stamp = privateLock.readLock()
            try {
                privateCacheMap[id]
            } finally {
                privateLock.unlockRead(stamp)
            }
        }
    }
}





