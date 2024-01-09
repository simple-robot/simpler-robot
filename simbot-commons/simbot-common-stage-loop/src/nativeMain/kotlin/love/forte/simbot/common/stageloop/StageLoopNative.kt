/*
 *     Copyright (c) 2023-2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

package love.forte.simbot.common.stageloop

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
private class StageLoopImpl<S : Stage<S>> : StageLoop<S> {

    private val deque = mutableListOf<S>()

    /**
     * 向事件队列尾部追加事件。
     */
    override fun appendStage(stage: S) {
        deque.add(stage)
    }

    private var _currentStage: S? = null

    /**
     * 在 [run] 中**正在执行**的状态实例。
     * 如果为null则代表没有循环在进行。
     */
    override val currentStage: S?
        get() = _currentStage

    /**
     * 根据提供的 [stage] 变更 [currentStage] 的状态并执行它（如果不为null的话）
     *
     */
    override suspend operator fun invoke(stage: S?) {
        _currentStage = stage
        stage?.invoke(this)
    }

    /**
     * 取出下一个需要执行的状态。
     */
    override fun poll(): S? {
        return deque.removeFirstOrNull()
    }

}


/**
 * 创建一个默认的 [StageLoop] 实现。
 */
@Suppress("FunctionName")
public actual fun <S : Stage<S>> DefaultStageLoop(): StageLoop<S> = StageLoopImpl()
