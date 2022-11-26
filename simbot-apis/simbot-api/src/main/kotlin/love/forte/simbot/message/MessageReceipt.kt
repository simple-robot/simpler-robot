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

package love.forte.simbot.message

import love.forte.simbot.CharSequenceID
import love.forte.simbot.ID
import love.forte.simbot.action.DeleteSupport
import love.forte.simbot.definition.IDContainer
import love.forte.simbot.randomID


/**
 * 消息回执，当消息发出去后所得到的回执信息。
 *
 * 大多数情况下，[MessageReceipt] 的实现类应该由
 * [StandardMessageReceipt] 衍生而不是直接基于此类。
 * 此类的实现主要用于进一步地抽象组件所需的 receipt 类型。
 *
 * @see StandardMessageReceipt
 *
 * @author ForteScarlet
 */
public interface MessageReceipt : DeleteSupport {
    
    /**
     * 消息是否发送成功。此属性的 `false` 一般代表在排除其他所有的 **异常情况** 下，在正常流程中依然发送失败（例如发送的消息是空的）。
     * 不代表发送中出现了异常，仅代表在过程完全正常的情况下的发送结果。
     */
    public val isSuccess: Boolean
    
    /**
     * 删除此回执所代表的消息。这通常代表为'撤回'相关消息。
     * 如果此回执不支持撤回则可能会恒定的得到 `false`。
     *
     * 进行删除的过程中通常不会捕获任何异常，因此可能抛出任何可能涉及的
     * 业务或状态异常。
     *
     * @return 是否删除成功
     */
    @JvmSynthetic
    override suspend fun delete(): Boolean
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
public abstract class SingleMessageReceipt : IDContainer, StandardMessageReceipt() {
    /**
     * 一个消息回执中存在一个ID.
     *
     * [id] 不一定具有实际含义，也有可能是仅仅只是一个 [随机值][randomID] 或 [空值][CharSequenceID.EMPTY]。
     * 当不具有实际意义时，[随机值][randomID] 通常出现在成功的回执中，
     * 而 [空值][CharSequenceID.EMPTY] 通常出现在失败的回执中。
     *
     */
    abstract override val id: ID
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
     * 聚合消息中的 [isSuccess] 代表是否存在**任意**回执的 [MessageReceipt.isSuccess] 为 `true`。
     */
    abstract override val isSuccess: Boolean
    
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
     * @see deleteAll
     * @return 是否存在**任意**内容删除成功
     */
    override suspend fun delete(): Boolean {
        return deleteAll() > 0
    }
    
    /**
     * 删除其所代表的所有消息回执。
     */
    public suspend fun deleteAll(): Int {
        var count = 0
        for (receipt in this) {
            if (receipt.delete()) {
                count++
            }
        }
        return count
    }
}
