package love.forte.simbot.util.stageloop

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
public expect open class StageLoop<S : Stage<S>>() {
    /**
     * 向事件队列尾部追加事件。
     */
    public open fun appendStage(stage: S)

    /**
     * 在 [run] 中**正在执行**的状态实例。
     * 如果为null则代表没有循环在进行。
     */
    public open val currentStage: S?

    /**
     * 根据提供的 [stage] 变更 [currentStage] 的状态并执行它（如果不为null的话）
     *
     */
    public open suspend operator fun invoke(stage: S?)

    /**
     * 取出下一个需要执行的状态。
     */
    public open fun poll(): S?
}

/**
 * 用于 [StageLoop] 中进行循环的状态集。
 * [Stage] 会被 [StageLoop] 的泛型所约束，通常情况下需要实现 [Stage]
 * 并提供特定的类型界限，例如：
 * ```
 * sealed class FooStage :  StageLoop.Stage<FooStage>() {
 *     object Start : FooStage() {
 *          override suspend fun invoke(loop: StageLoop<FooStage>) {
 *              loop.appendStage(Running("Hello", 1))
 *          }
 *     }
 *
 *     class Running(val value: String, val times: Int) : FooStage() {
 *         override suspend fun invoke(loop: StageLoop<FooStage>) {
 *             if (times <= 5) {
 *                 loop.appendStage(Running(value, times + 1))
 *             }
 *         }
 *     }
 * }
 * ```
 */
public abstract class Stage<S : Stage<S>> {
    public abstract suspend operator fun invoke(loop: StageLoop<S>)
}
