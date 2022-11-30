package love.forte.simbot.common.stageloop

import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext

/**
 * 通过提供的 [StageLoop] 进行循环。
 *
 * @param job 可通过提供一个 [Job] 来增加控制循环的条件
 * @param exceptionHandle 每个阶段循环到并执行时的异常处理器
 */
public suspend inline fun <S : Stage<S>> StageLoop<S>.loop(
    job: Job? = null,
    crossinline exceptionHandle: (Throwable) -> Unit = { e ->
        throw e
    },
) {
    val job0 = SupervisorJob(job)
    withContext(job0) {
        var next: S? = poll()
        while (job0.isActive && next != null) {
            try {
                invoke(next)
            } catch (e: Throwable) {
                exceptionHandle(e)
            }
            next = poll()
        }
        invoke(null)
    }
}

