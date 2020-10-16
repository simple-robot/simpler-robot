/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MiraiListenerRegistered.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.component.mirai.configuration

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import love.forte.common.ioc.annotation.Beans
import love.forte.common.ioc.annotation.Depend
import love.forte.simbot.component.mirai.utils.registerSimbotEvents
import love.forte.simbot.core.CompLogger
import love.forte.simbot.core.listener.ListenerManager
import love.forte.simbot.core.listener.ListenerRegistered
import love.forte.simbot.core.listener.MsgGetProcessor
import net.mamoe.mirai.Bot
import net.mamoe.mirai.closeAndJoin

/**
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@Beans
public class MiraiListenerRegistered : ListenerRegistered {
    private companion object : CompLogger("ListenerRegistered")

    @Depend
    lateinit var msgGetProcessor: MsgGetProcessor

    /**
     * 当所有的监听函数都注册完成后,
     * 为所有的bot注册监听事件。
     */
    override fun onRegistered(manager: ListenerManager) {
        println("注册bots.")
        // 注册Mirai的所有bot事件。
        Bot.forEachInstance { it.registerSimbotEvents(msgGetProcessor) }
        // 注册一个 ctrl+c钩子来关闭所有的bot。
        Runtime.getRuntime().addShutdownHook(Thread {
            logger.debug("try to close all bots...")
            val waiting = mutableListOf<Pair<Long, Deferred<*>>>()
            // close all bot.
            Bot.botInstancesSequence.map {
                it.id to GlobalScope.async {
                    logger.debug("try to close bot(${it.id})")
                    it.closeAndJoin()
                }
            }.forEach {
                waiting.add(it)
            }
            runBlocking {
                waiting.forEach {
                    it.second.await()
                    logger.debug("bot(${it.first}) closed.")
                }

            }
            logger.debug(" all bots closed.")
        })
    }
}