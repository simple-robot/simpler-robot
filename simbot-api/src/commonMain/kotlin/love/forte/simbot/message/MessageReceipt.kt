/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.message

import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.DeleteSupport
import love.forte.simbot.ability.SendSupport
import love.forte.simbot.ability.StandardDeleteOption
import love.forte.simbot.common.id.ID
import kotlin.jvm.JvmSynthetic


/**
 * 通过 [SendSupport.send] 发送消息后得到的发送回执。
 *
 * 一个 [MessageReceipt] 可能代表一个（[SingleMessageReceipt]）或多个（[AggregatedMessageReceipt]）真实的消息，
 *
 * @see StandardMessageReceipt
 *
 * @author ForteScarlet
 */
public interface MessageReceipt : DeleteSupport {

    /**
     * 删除此回执所代表的消息。这通常代表为'撤回'相关消息。
     * 如果此回执不支持撤回则可能会抛出 [UnsupportedOperationException]。
     * 可通过 [options] 控制异常的产生，例如 [StandardDeleteOption.IGNORE_ON_UNSUPPORTED]。
     *
     * @throws Exception 可能产生的任何业务或状态异常
     */
    @JvmSynthetic
    override suspend fun delete(vararg options: DeleteOption)
}

/**
 * 由 [MessageReceipt] 衍生的标准消息回执类型, 提供用于表示独立元素回执的 [SingleMessageReceipt]
 * 和复合回执的 [AggregatedMessageReceipt].
 *
 * @see SingleMessageReceipt
 * @see AggregatedMessageReceipt
 */
public sealed class StandardMessageReceipt : MessageReceipt

/**
 * 明确代表为一个或零个（发送失败时）具体消息的消息回执，可以作为 [AggregatedMessageReceipt] 的元素进行聚合。
 *
 * @see StandardMessageReceipt
 * @see AggregatedMessageReceipt
 */
public abstract class SingleMessageReceipt : StandardMessageReceipt() {
    /**
     * 一个消息回执中存在一个ID.
     *
     * [id] 不一定具有实际含义，也有可能是仅仅只是一个随机值。
     *
     */
    public abstract val id: ID
}

/**
 * 聚合消息回执，代表多个 [SingleMessageReceipt] 的聚合体。
 *
 * @see StandardMessageReceipt
 * @see SingleMessageReceipt
 * @see aggregation
 */
public abstract class AggregatedMessageReceipt : StandardMessageReceipt(), Iterable<SingleMessageReceipt> {
    /**
     * 当前聚合消息中包含的所有 [MessageReceipt] 的数量。
     */
    public abstract val size: Int

    /**
     * 根据索引值获取到指定位置的 [SingleMessageReceipt]。
     *
     * @throws IndexOutOfBoundsException 索引越界时
     */
    public abstract operator fun get(index: Int): SingleMessageReceipt

    /**
     * 删除其所代表的所有消息回执。
     *
     * 如果希望明确得知删除成功内容的数量, 考虑使用 [deleteAll];
     * 如果希望精准控制每一个回执的删除情况, 考虑使用 [deleteAllSafely (Kotlin Only)][deleteAllSafely] 或循环控制。
     * ```kotlin
     * for (singleReceipt in aggregatedReceipt) {
     *      singleReceipt.delete(...);
     *      // ...
     * }
     * ```
     *
     * 如果过程中产生了异常，会导致过程中断而因此删除不完全。
     *
     * @see deleteAll
     */
    override suspend fun delete(vararg options: DeleteOption) {
        deleteAll(options = options)
    }

    /**
     * 删除其所代表的所有消息回执。
     *
     * [deleteAll] 会尝试依次删除当前复合回执中包含的每一个回执，
     * 但是这个过程可能会因为发送异常而**被中断**。
     *
     * 如果希望能够保证每一个结果都被正确尝试，参考使用 [deleteAllSafely (Kotlin Only)][deleteAllSafely] 或自行使用循环进行精准控制。
     *
     * @return 删除成功的数量
     */
    public open suspend fun deleteAll(vararg options: DeleteOption): Int {
        var count = 0
        for (receipt in this) {
            receipt.delete(options = options)
            count++
        }

        return count
    }
}


/**
 * 尝试删除其所代表的所有消息回执。
 *
 * [deleteAllSafely] 内会直接使用循环尝试进行删除，并且会通过 try-catch 保证每一次循环的结果都经由 [onResult] 回调函数处理。
 * 因此相比较于 [AggregatedMessageReceipt.deleteAll], [deleteAllSafely] 可以保证会尝试当前 [AggregatedMessageReceipt] 中的所有元素
 * 而不会被可能发生的异常打断。
 *
 * _当然，前提是用户指定的 [onResult] 逻辑中不会产生异常，否则依旧会被打断。_
 *
 * @param onResult 每一个元素被执行删除后的结果回执，也可能是存在异常的回执。
 * 默认情况下 [onResult] 会直接**忽略异常**。
 */
public suspend inline fun AggregatedMessageReceipt.deleteAllSafely(vararg options: DeleteOption, onResult: (Result<Unit>) -> Unit = { /* Ignore it. */ }) {
    for (receipt in this) {
        onResult(kotlin.runCatching { receipt.delete(options = options) })
    }
}
