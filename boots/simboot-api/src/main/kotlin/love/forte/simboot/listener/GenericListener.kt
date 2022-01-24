/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

package love.forte.simboot.listener

import love.forte.simbot.ID
import love.forte.simbot.event.Event
import love.forte.simbot.event.EventListener
import love.forte.simbot.event.EventListenerProcessingContext
import love.forte.simbot.event.EventResult
import love.forte.simbot.utils.runWithInterruptible

/**
 *
 * 通用监听函数接口，是在 boot-core 下解析生成的各类监听函数的统一标准接口。
 *
 * 所有通过扫描函数而得到的监听函数，其都建议使用可挂起的(`suspend`) 函数作为执行体。
 * 对于那些非可挂起的监听函数（普通函数、Java函数等），函数的执行逻辑 (KFunction.call(...)) 将会在 [runWithInterruptible] 中，默认以 [kotlinx.coroutines.Dispatchers.IO] 作为调度器被执行。
 *
 *
 *
 * @author ForteScarlet
 */
public interface GenericBootEventListener : EventListener {
    override val id: ID
    override val isAsync: Boolean
    override fun isTarget(eventType: Event.Key<*>): Boolean
    override suspend fun invoke(context: EventListenerProcessingContext): EventResult
}

