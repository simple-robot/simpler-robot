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

package love.forte.simbot.event

import love.forte.simbot.ID
import love.forte.simbot.Timestamp
import love.forte.simbot.message.doSafeCast
import love.forte.simbot.utils.runInBlocking

/**
 * 一个与 **变更** 有关的事件。
 *
 * 这个变更可能是正在变更、计划变更，或者已经变更。
 *
 * [SOURCE] 是本次变更的载体，是 [BEFORE] 与 [AFTER] 发生这样前后变化的舞台。
 * 举个例子，对于一种"用户名称变更事件"，用户即为载体[SOURCE], [BEFORE]则为变更前的名称，[AFTER]则为变更后的名称。
 *
 *
 * [BEFORE] 与 [AFTER] 作为变更状态前后的两个瞬态，应当伴随事件并作为属性直接提供。
 *
 * 当然，一般情况下，根据事件语义，如果目标尚未发生改变，[AFTER] 应当为非真实内容，[BEFORE] **可能是瞬时** 状态。
 * 如果变更已经发生，则 [AFTER]应当是当前状态（可能是瞬时），而 [BEFORE] 则为历史态。
 *
 * 变更前后的两个瞬态均无法准确定义其是否可空，因此对于可空情况由实现者自行约束。
 *
 * @param BEFORE 变更前的状态。
 * @param AFTER 变更后的状态。
 *
 * @see ChangedEvent
 */
public interface ChangeEvent<SOURCE, BEFORE, AFTER> : Event {
    override val id: ID


    /**
     * 变更载体，或者说变更内容的源。
     */
    @JvmSynthetic
    public suspend fun source(): SOURCE

    public val source: SOURCE get() = runInBlocking { source() }

    /**
     * 变更行为前的内容。
     */
    @JvmSynthetic
    public suspend fun before(): BEFORE

    public val before: BEFORE get() = runInBlocking { before() }

    /**
     * 变更行为后的内容。
     */
    @JvmSynthetic
    public suspend fun after(): AFTER

    public val after: AFTER get() = runInBlocking { after() }


    public companion object Key : BaseEventKey<ChangeEvent<*, *, *>>("api.change") {
        override fun safeCast(value: Any): ChangeEvent<*, *, *>? = doSafeCast(value)
    }
}

/**
 * 一个 **变更** 事件，代表一个已经变更结束的事件。
 * [ChangedEvent] 是 [ChangeEvent] 事件的 **事实** 描述，
 * 一般来讲代表了一个已经发生变更的事件。
 *
 * [SOURCE] 仍然为变更内容的载体，[BEFORE] 代表一个变更之前的瞬时状态， [AFTER]则代表为一个变更后的状态。
 *
 *
 * @see ChangeEvent
 * @author ForteScarlet
 */
public interface ChangedEvent<SOURCE, BEFORE, AFTER> : ChangeEvent<SOURCE, BEFORE, AFTER> {
    override suspend fun source(): SOURCE
    override suspend fun before(): BEFORE
    override suspend fun after(): AFTER

    /**
     * 此事件所代表的变更发生的时间。
     */
    public val changedTime: Timestamp

    /**
     * 通常情况下，事件时间就相当于其[变更时][changedTime]的时间。
     */
    override val timestamp: Timestamp get() = changedTime

    public companion object Key : BaseEventKey<ChangedEvent<*, *, *>>("api.changed", ChangeEvent) {
        override fun safeCast(value: Any): ChangedEvent<*, *, *>? =
            doSafeCast(value)

    }
}
