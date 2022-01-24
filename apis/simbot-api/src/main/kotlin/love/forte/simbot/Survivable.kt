/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
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


