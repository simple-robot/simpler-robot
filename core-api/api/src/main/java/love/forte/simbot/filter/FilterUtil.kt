/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

@file:Suppress("unused")

package love.forte.simbot.filter

import love.forte.simbot.annotation.OnlySession
import love.forte.simbot.constant.PriorityConstant
import love.forte.simbot.listener.ListenerContext
import love.forte.simbot.listener.get

/**
 * 将一个 [ListenerFilter] 转化为仅会话状态有效。
 *
 */
public fun ListenerFilter.asOnlySession(onlySession: OnlySession): ListenerFilter =
    asOnlySession(onlySession.value, onlySession.mostMatchType)


/**
 * 将一个 [ListenerFilter] 转化为仅会话状态有效。
 *
 */
public fun ListenerFilter.asOnlySession(values: Array<String>, mostMatchType: MostMatchType): ListenerFilter {
    return OnlySessionDelegateFilter(values, mostMatchType, this)
}


private class OnlySessionDelegateFilter(
    private val values: Array<String>,
    private val mostMatchType: MostMatchType,
    private val realFilter: ListenerFilter,
) : ListenerFilter {
    override fun getFilterValue(name: String, text: String): String? = realFilter.getFilterValue(name, text)

    override val priority: Int by realFilter::priority

    override fun test(data: FilterData): Boolean {
        val session = data.listenerContext[ListenerContext.Scope.CONTINUOUS_SESSION] ?: return false
        val sessionMatch = mostMatchType.mostTest(values.asIterable()) { keyName -> session[keyName] != null }
        return if (sessionMatch) realFilter.test(data) else false
    }
}


/**
 * 得到一个仅会话有效的过滤器。
 *
 */
public fun onlySession(onlySession: OnlySession): ListenerFilter =
    onlySession(onlySession.value, onlySession.mostMatchType)


/**
 * 得到一个仅会话有效的过滤器。
 *
 * 会话有效过滤器的默认优先级是 [PriorityConstant.CORE_FIRST].
 *
 */
public fun onlySession(
    values: Array<String>,
    mostMatchType: MostMatchType,
    priority: Int = PriorityConstant.CORE_FIRST,
): ListenerFilter {
    return OnlySessionFilter(values, mostMatchType, priority)
}


private class OnlySessionFilter(
    private val values: Array<String>,
    private val mostMatchType: MostMatchType,
    override val priority: Int,
) : ListenerFilter {
    override fun test(data: FilterData): Boolean {
        val session = data.listenerContext[ListenerContext.Scope.CONTINUOUS_SESSION] ?: return false
        return mostMatchType.mostTest(values.asIterable()) { keyName -> session[keyName] != null }
    }
}


/**
 * 将一个可能存在的 [ListenerFilter] 转化为仅会话状态有效。
 *
 */
public fun ListenerFilter?.asOnlySessionOrDefault(onlySession: OnlySession): ListenerFilter =
    this?.asOnlySession(onlySession) ?: onlySession(onlySession)


/**
 * 将一个可能存在的 [ListenerFilter] 转化为仅会话状态有效。
 *
 */
public fun ListenerFilter?.asOnlySessionOrDefault(values: Array<String>, mostMatchType: MostMatchType): ListenerFilter =
    this?.asOnlySession(values, mostMatchType) ?: onlySession(values, mostMatchType)
