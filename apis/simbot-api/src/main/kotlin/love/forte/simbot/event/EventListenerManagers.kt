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

package love.forte.simbot.event

import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.ID


/**
 *
 * 监听函数容器。
 *
 * @author ForteScarlet
 */
public interface EventListenerContainer {

    /**
     * 通过一个ID得到一个当前监听函数下的对应函数。
     */
    public operator fun get(id: ID): EventListener?

}


/**
 * 监听函数注册器
 */
public interface EventListenerRegistrar {

    /**
     * 注册一个监听函数。
     *
     * 注册监听函数并不一定是原子的，可能会存在注册结果的滞后性（注册不一定会立即生效）。
     *
     * ## 滞后性
     * 例如你在时间点A注册一个监听函数，而时间点B（=时间点A + 10ms）时推送了一个事件，
     *
     * 这时候无法保证在时间点A之后的这个事件能够触发此监听函数。
     *
     * @throws IllegalStateException 如果出现ID重复
     */
    public fun register(listener: EventListener)

}


/**
 * 事件监听器管理器标准接口。
 *
 *
 */
public interface EventListenerManager :
    EventProcessor, EventListenerContainer, EventListenerRegistrar {

    /**
     * 获取当前监听函数管理器中所使用的全局作用域的context。
     * 其获取结果相当于在监听函数中使用 [EventProcessingContext.Scope.Global] 进行获取而得到的作用域。
     *
     * 全局作用域上下文与每一个独立的事件处理流程无关，理应能够直接通过当前 [EventListenerManager] 进行获取。
     *
     */
    @ExperimentalSimbotApi
    public val globalScopeContext: ScopeContext


    /**
     * 获取当前监听函数管理器中所使用的持续会话context。
     * 其获取结果相当于在监听函数中使用 [EventProcessingContext.Scope.ContinuousSession] 进行获取而得到的作用域。
     *
     * 持续会话上下文与每一个独立的事件流程无关，其生命周期应该与当前监听函数管理器有关，理应能够直接通过当前 [EventListenerManager] 进行获取以便在监听流程之外进行会话触发。
     *
     * ```kotlin
     * fun process(listenerManager: EventListenerManager) {
     *      // 取得一个provider
     *      val provider = listenerManager.continuousSessionContext.getProvider<Int>(123.ID)
     *      provider?.push(666)
     * }
     * ```
     *
     */
    @ExperimentalSimbotApi
    public val continuousSessionContext: ContinuousSessionContext


}

