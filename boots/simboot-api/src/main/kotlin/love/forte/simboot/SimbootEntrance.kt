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

package love.forte.simboot

import kotlinx.coroutines.CompletionHandler
import love.forte.simbot.Api4J
import love.forte.simbot.ability.Survivable
import org.slf4j.Logger
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future

/**
 *
 * Boot下的入口接口。入口接收一个任意类型的参数和args参数集，并在一系列的流程后得到一个最终的结果 [SimbootContext].
 *
 * 对于参数的类型的判断与使用，以及最终返回值的实现细节，将全权交由实现者自行决断。
 *
 * @author ForteScarlet
 */
public interface SimbootEntrance {

    /**
     * 提供一个可能的启动器实例和启动参数，通过一系列流程执行后得到最终的 [SimbootContext].
     */
    public fun run(context: SimbootEntranceContext): SimbootContext

}


/**
 * 为 [SimbootEntrance.run] 提供参数的参数。
 */
public interface SimbootEntranceContext {
    /**
     * 调用者所提供的启动参数。
     *
     * 对于参数的类型的判断与使用，以及最终返回值的实现细节，将全权交由实现者自行决断。
     */
    public val application: Any?

    /**
     * 启动命令参数。
     */
    public val args: Array<String>

    /**
     * 由boot所提供的日志。
     */
    public val logger: Logger
}


/**
 * Boot启动流程结束后得到的最终结果。
 */
public interface SimbootContext : Survivable {

    /**
     * 挂起context直至其被彻底终止。
     */
    @JvmSynthetic
    override suspend fun join()

    /**
     * 终止simbot程序。
     */
    @JvmSynthetic
    override suspend fun cancel(reason: Throwable?): Boolean


    /**
     * 注册一个在simbot程序结束后执行逻辑.
     */
    override fun invokeOnCompletion(handler: CompletionHandler)


    /**
     * 通过 [toAsync] 得到一个 [Future] 并阻塞直到程序结束。
     */
    @Api4J
    @Throws(InterruptedException::class, ExecutionException::class)
    public fun joinBlocking() {
        toAsync().get()
    }

    /**
     * 将当前 simbot context 的执行情况转化为一个 [Future],
     * 可以自由的选择是否需要通过 [Future.get] 来等待直到程序终止。
     *
     * 返回值的 [Int] 值在终止后始终得到 `0`.
     *
     */
    @Api4J
    override fun toAsync(): Future<Unit> {
        val future = CompletableFuture<Unit>()
        invokeOnCompletion { reason ->
            if (reason != null) {
                future.completeExceptionally(reason)
            } else {
                future.complete(Unit)
            }
        }
        return future
    }

}