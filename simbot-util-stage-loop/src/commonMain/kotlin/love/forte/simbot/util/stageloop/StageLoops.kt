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

