/*
 * Copyright (c) 2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.util.stageloop

import java.util.concurrent.ConcurrentLinkedDeque

/**
 * 可挂起的事件循环器。
 *
 * [StageLoop] 内部持有可变状态，一个 [StageLoop] 实例建议同时只进行一个循环任务。
 *
 * ```kotlin
 * sealed FooStage : Stage<FooStage>() {
 *     object Start : FooStage() { ... }
 *     // ...
 * }
 *
 * val stageLoop = ...
 * stageLoop.appendStage(FooStage.Start)
 *
 * stageLoop.loop()
 * ```
 *
 * @see StageLoop.loop
 *
 * @param S 用于界定一定范围内的状态
 *
 * @author ForteScarlet
 */
public actual open class StageLoop<S : Stage<S>> actual constructor() {

    private val deque = ConcurrentLinkedDeque<S>()

    @Volatile
    private var _currentStage: S? = null

    /**
     * 在 [run] 中**正在执行**的状态实例。
     * 如果为null则代表没有循环在进行。
     */
    public actual open val currentStage: S? get() = _currentStage

    /**
     * 向事件队列尾部追加事件。
     */
    public actual open fun appendStage(stage: S) {
        deque.add(stage)
    }

    /**
     * 根据提供的 [stage] 变更 [currentStage] 的状态并执行它（如果不为null的话）
     *
     */
    public actual open suspend operator fun invoke(stage: S?) {
        _currentStage = stage
        stage?.invoke(this)
    }

    /**
     * 取出下一个需要执行的状态。
     */
    public actual open fun poll(): S? {
        return deque.poll()
    }


}
