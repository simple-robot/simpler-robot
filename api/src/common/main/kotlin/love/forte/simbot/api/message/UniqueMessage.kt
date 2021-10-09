package love.forte.simbot.api.message

import love.forte.simbot.api.Component


// UniqueMessage

/**
 * 唯一的 [Message], 代表在 [Messages] 中, 如果出现了冲突内容（一般即相同的key），
 * 则根据策略进行元素替换、舍弃、合并等操作。
 *
 * 冲突可能有多种可能：
 * - 向消息中追加 [UniqueMessage], 并发现了同类或冲突元素
 * - 向消息中追加 [PluralMessage], 原消息列表中存在 [UniqueMessage].
 * - 仅允许单元素存在的 [UniqueMessage].
 */
public interface UniqueMessage<out K : AbsoluteMessage> : AbsoluteMessage {
    override val component: Component get() = key.component

    override val key: Message.Key<out K>

    /**
     * 解决冲突。 默认情况下为覆盖替换。
     *
     * @return 解决冲突的策略。
     */
    public fun solve(other: @UnsafeVariance K) : SolveStatus = SolveStatus.Overwrite
}


/**
 * 解决策略。
 *
 */
public sealed class SolveStatus {
    /**
     * 覆盖替换。即使新值替换旧值。
     */
    public object Overwrite : SolveStatus()

    /**
     * 使用给定的值替换旧值。一般为合并后的值。
     */
    public data class UseThis(val value: AbsoluteMessage): SolveStatus()

    /**
     * 保持不变，即无视新值，维持旧值。
     */
    public object Keep : SolveStatus()

    /**
     * 丢弃，移除掉旧值，且无视新值。
     */
    public object Drop : SolveStatus()

    /**
     * 列表中，只保留旧值，移除其他所有。
     */
    public object OnlyOld : SolveStatus()

    /**
     * 列表中，只保留新值，移除其他所有。
     */
    public object OnlyNew : SolveStatus()
}


/**
 *
 * @return addable
 */
internal fun SolveStatus.option(list: MutableList<AbsoluteMessage>, i: Int, new: AbsoluteMessage): Boolean {
    when(this) {
        SolveStatus.Overwrite -> {
            list[i] = new
        }
        is SolveStatus.UseThis -> {
            list[i] = value
        }
        SolveStatus.Keep -> return true // Do nothing.
        SolveStatus.Drop -> list.removeAt(i)
        SolveStatus.OnlyOld -> {
            val old = list[i]
            list.clear()
            list.add(old)
        }
        SolveStatus.OnlyNew -> {
            list.clear()
            list.add(new)
        }
    }
    return false
}





/**
 * 一个可合并的消息。[MergeableMessage] 在 消息列表中的时候，如果添加了相互冲突的消息，则会将其进行合并。
 *
 * `| M1 | M2 | M3 | + M2.1 -> | M1 | (M2 + M2.1) | M3`
 *
 * 合并有多种可能，一是真正的合并，其他的则有可能为直接替换为后来者等等。
 *
 */
public interface MergeableMessage<K : AbsoluteMessage> : UniqueMessage<K> {
    override val key: Message.Key<K>
    override fun solve(other: K): SolveStatus = SolveStatus.UseThis(merge(other))

    /**
     * 合并 当前消息与 [other].
     *
     */
    public fun merge(other: K): K
}