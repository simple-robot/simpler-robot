/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

@file:JvmName("ContinuousSessionContexts")
@file:JvmMultifileClass

package love.forte.simbot.extension.continuous.session

import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.jvm.JvmSynthetic

/**
 * 使用于 [ContinuousSessionContext.session] 中的 `receiver` 逻辑函数。
 *
 * 在 Java 中，可以使用 `InSessions` 中提供的各种静态工厂函数构建它，例如
 * `InSessions.async`、`InSessions.mono` 等。
 *
 * 在 `ContinuousSession` 中使用时，我们强烈建议使用非阻塞的 [InSession] 实现，
 * 或者为 `ContinuousSession` 的调度器配置为 **虚拟线程调度器** 。
 *
 * ```java
 * var dispatcher = ExecutorsKt.from(Executors.newVirtualThreadPerTaskExecutor());
 * ```
 */
public fun interface InSession<T, R> {
    @JvmSynthetic
    public suspend fun ContinuousSessionReceiver<T, R>.invoke()
}

/**
 * 持续会话(`continuous session`)管理器，
 * 用于承载一组 [ContinuousSessionProvider] 和 [ContinuousSessionReceiver]
 * 的上下文。用于构建与管理 `ContinuousSession`。
 *
 * ## 持续会话
 *
 * **持续会话(`continuous session`)** 是一种用于解决在同一组逻辑中连续处理多个'事件 [T]'、并响应 '结果 [R]' 的解决方案。
 * [ContinuousSessionContext] 本身可灵活定制这个所谓 '事件' 的类型，
 * 其主要应用场景为借助子类型 [EventContinuousSessionContext] 在 simbot 的事件调度中使用。
 *
 * 假如，有一个事件类型为 [Int]、响应结果为 [String] 的一组持续会话:
 *
 * ```kotlin
 * // 假设这个 session context 的'事件'类型为 Int, '结果'类型为 String
 * val context = ContinuousSessionContext<Int, String> = ...
 * val session = context.session(Key()) { // this: ContinuousSessionReceiver
 *     // receiver 逻辑在异步中，等待外界的事件推送
 *     val next = await { it -> it.toString() }
 * }        ↑                       |
 *          |-------------|         |
 *                        |         |
 *       |--------------- | --------|
 *       |                |
 *       ↓                |
 * val result = session.push(1) // 推送 '事件', 得到 '结果'
 *
 * session.join() // session 内逻辑结束后便会正常终止
 * assertTrue(session.isCompleted)
 * ```
 *
 * 在 simbot 的事件调度中的简单应用:
 *
 * ```kotlin
 * suspend fun handle(handleEvent: Event, sessionContext: EventContinuousSessionContext): EventResult {
 *    // event: 接收到的事件
 *    // sessionContext: 假设它是在 application 的 plugins 中获取到的. 可参见 `EventContinuousSessionContext` 的文档说明。
 *
 *    val key = computeKey(handleEvent) // 根据 handleEvent, 分配一个与它的唯一会话对应的 key.
 *    val session = context.session(key, EXISTING) {
 *        // this: ContinuousSessionReceiver<Event, EventResult>
 *        // receiver 逻辑在异步中，等待外界的事件推送
 *        // 此处的逻辑：
 *        //   如果收到的事件 event 经过 check 的判断后符合要求，
 *        //   则返回 EventResult.empty(isTruncated = true), 代表此会话已经截取此事件，
 *        //   不要让事件再向后续的其他处理器传递；
 *        //   否则（即不符合你的业务逻辑判断的条件）则返回一个 EventResult.invalid(), 代表无效的结果。
 *        val next = await { event ->
 *             ↑           if (check(event)) EventResult.empty(isTruncated = true)
 *             |           else EventResult.invalid()
 *             |     }
 *    }        |     \-----------------------------------------------------------/
 *             |                       |
 *             |-------------|         |
 *                           |         |
 *          |--------------- | --------|
 *          ↓                |
 *    return session.push(handleEvent) // 推送 '事件', 得到 '结果'
 *    // 直接返回这个结果
 * }
 * ```
 *
 *
 * @see EventContinuousSessionContext
 *
 * @author ForteScarlet
 */
public interface ContinuousSessionContext<T, R> {

    /**
     * 尝试创建一组 `ContinuousSession` 并返回其中的 [ContinuousSessionProvider]。
     * 在出现 [key] 冲突时基于 [strategy] 策略处理冲突。
     *
     * @param key session 会话的标识。[key] 的类型应当是一个可以保证能够作为一个 hash key  的类型，
     * 例如基础数据类型(例如 [Int]、[String])、数据类类型(data class)、object 类型等。
     * @param strategy 当 [key] 出现冲突时的处理策略
     * @param inSession 在**异步**中进行会话逻辑的函数实例。
     * 在 Java 中可使用 `InSessions` 中提供的静态工厂函数构建实例，
     * 例如 `InSessions.async`、`InSessions.mono` 等。
     * 在 `ContinuousSession` 中使用时，我们强烈建议使用非阻塞的 [InSession] 实现，
     * 或者为 `ContinuousSession` 的调度器配置为 **虚拟线程调度器** 。
     *
     * @throws ConflictSessionKeyException 如果 [strategy] 为 [ConflictStrategy.FAILURE] 并且出现了冲突
     */
    public fun session(
        key: Any,
        strategy: ConflictStrategy = ConflictStrategy.FAILURE,
        inSession: InSession<T, R>
    ): ContinuousSessionProvider<T, R>

    /**
     * 尝试创建一组 `ContinuousSession`, 并在出现 [key] 冲突时使用 [ConflictStrategy.FAILURE] 作为冲突解决策略。
     */
    public fun session(
        key: Any,
        inSession: InSession<T, R>
    ): ContinuousSessionProvider<T, R> = session(key, ConflictStrategy.FAILURE, inSession)

    /**
     * 根据 [key] 获取指定的 [ContinuousSessionProvider] 并在找不到时返回 `null`。
     */
    public operator fun get(key: Any): ContinuousSessionProvider<T, R>?

    /**
     * 判断是否包含某个 [key] 对应的会话。
     */
    public operator fun contains(key: Any): Boolean

    /**
     * 移除某个指定 [key] 的会话。
     * [remove] 仅会从记录中移除，不会使用 [ContinuousSessionProvider.cancel]，
     * 需要由调用者主动使用。
     */
    public fun remove(key: Any): ContinuousSessionProvider<T, R>?

    /**
     * 创建会话时的冲突策略
     */
    public enum class ConflictStrategy {
        /**
         * 如果已经存在相同 `key` 的值，抛出异常 [ConflictSessionKeyException]。
         *
         */
        FAILURE,

        /**
         * 关闭旧的现存值，并用提供的新值取代。
         */
        REPLACE,

        /**
         * 直接返回旧的现存值，忽略新值
         */
        EXISTING
    }
}


