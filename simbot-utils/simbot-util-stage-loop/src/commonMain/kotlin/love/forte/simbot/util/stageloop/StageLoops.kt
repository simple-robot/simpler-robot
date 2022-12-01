package love.forte.simbot.util.stageloop


/**
 * 通过提供的 [StageLoop] 进行循环。
 *
 * @param condition 每次循环时的条件判断。默认为 `next != null`。
 * 可以配合其他条件实现声明周期控制，例如 `Job`
 * ```kotlin
 * val job: Job = ...
 * stageLoop.loop(condition = { next -> job.isActive && next != null })
 * ```
 *
 * @param exceptionHandle 每个阶段循环到并执行时的异常处理器
 */
public suspend inline fun <S : Stage<S>> StageLoop<S>.loop(
    crossinline condition: (next: S?) -> Boolean = { next -> next != null },
    crossinline exceptionHandle: (Throwable) -> Unit = { e ->
        throw e
    },
) {
    var next: S? = poll()
    while (condition(next)) {
        try {
            invoke(next)
        } catch (e: Throwable) {
            exceptionHandle(e)
        }
        next = poll()
    }
    invoke(null)
}

