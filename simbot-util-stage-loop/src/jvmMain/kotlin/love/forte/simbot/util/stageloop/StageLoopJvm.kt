/*
 * Copyright (c) 2023 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
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
