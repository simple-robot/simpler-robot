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
import kotlinx.coroutines.future.future
import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.utils.runInNoScopeBlocking
import java.util.concurrent.CompletableFuture

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
    @JvmAsync(baseName = "asFuture", suffix = "")
    public suspend fun join()
    
    
    @Api4J
    @Deprecated("Just use join() or asFuture() for java", level = DeprecationLevel.ERROR,
        replaceWith = ReplaceWith("asFuture()")
    )
    public fun toAsync(): CompletableFuture<Unit> = future { join() }
    
    /**
     * 当完成（或被cancel）时执行一段处理。
     */
    public fun invokeOnCompletion(handler: CompletionHandler)
    
    /**
     * 阻塞当前线程并等待 [join] 的挂起结束。
     *
     * 等同于 `joinBlocking`。目前来看唯一的区别是 [waiting] 显示通过 [Throws] 指定了受检异常 [InterruptedException],
     * 而 joinBlocking 目前不会产生受检异常。
     */
    @Api4J
    @Throws(InterruptedException::class)
    public fun waiting() {
        runInNoScopeBlocking { join() }
    }
}

