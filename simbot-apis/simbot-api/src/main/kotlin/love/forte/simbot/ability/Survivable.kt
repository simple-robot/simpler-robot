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
 */

package love.forte.simbot.ability

import kotlinx.coroutines.CompletionHandler
import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.utils.runInBlocking
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Future

/**
 * 可存活的。
 * 此接口提供 [join]、[invokeOnCompletion] 等函数来对生命周期提供一定操作。
 *
 * @author ForteScarlet
 */
@JvmBlocking
@JvmAsync
public interface Survivable : Switchable {
    
    /**
     * 挂起, 直到当前实例被 [cancel] 或完成.
     */
    // @JvmSynthetic
    public suspend fun join()
    
    /**
     * 当完成（或被cancel）时执行一段处理。
     */
    public fun invokeOnCompletion(handler: CompletionHandler)
    
    /**
     * 阻塞当前线程并等待 [join] 的挂起结束。
     *
     * 应当谨慎使用会造成阻塞的api，且在Kotlin中避免使用。
     *
     */
    @Api4J
    @Throws(InterruptedException::class)
    public fun waiting() {
        runInBlocking { join() }
    }
    
    /**
     * 得到一个 [Future], 其结果会在当前 [Survivable] 被终止后被推送。
     *
     * 返回值的 [Future.get] 得到的最终结果恒为 `0`。
     *
     */
    @Api4J
    public fun toAsync0(): Future<Unit> {
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
    
    // @JvmSynthetic
    override suspend fun start(): Boolean
    
    // @JvmSynthetic
    override suspend fun cancel(reason: Throwable?): Boolean
    
    
}

