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

package love.forte.simbot

import kotlinx.coroutines.CompletionHandler
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

/**
 * 可存活的。
 * 此接口提供 [join]、[invokeOnCompletion] 等函数来对生命周期提供一定操作。
 * @author ForteScarlet
 */
public interface Survivable : Switchable {

    /**
     * 挂起, 直到当前实例被 [cancel] 或完成.
     *
     * Java中考虑使用 [waiting] 或者通过 [toAsync] 得到 [Future] 来更灵活的操作。
     *
     * @see waiting
     * @see toAsync
     */
    @JvmSynthetic
    public suspend fun join()

    /**
     * 当完成（或被cancel）时执行一段处理。
     */
    public fun invokeOnCompletion(handler: CompletionHandler)

    /**
     * 通过 [toAsync] 并使用 [get] 来进行阻塞等待。
     *
     * 通过 [invokeOnCompletion] 实现线程终止，因此如果实现者不支持 [invokeOnCompletion],
     * 需要考虑重写此方法以提供更优解。
     *
     * 你应当在主线程等与调度无关的线程进行此操作。
     *
     */
    @Api4J
    @Throws(InterruptedException::class)
    public fun waiting() {
        toAsync().get()
    }

    /**
     * 得到一个 [Future], 其结果会在当前 [Survivable] 被终止后被推送。
     *
     * 返回值的 [Future.get] 得到的最终结果恒为 `0`。
     *
     */
    @Api4J
    public fun toAsync(): Future<Unit> {
        val future = CompletableFuture<Unit>()
        invokeOnCompletion { e ->
            if (e != null) {
                future.completeExceptionally(e)
            } else {
                future.complete(Unit)
            }
        }
        return future
    }

    @JvmSynthetic
    override suspend fun start(): Boolean

    @JvmSynthetic
    override suspend fun cancel(reason: Throwable?): Boolean


}


