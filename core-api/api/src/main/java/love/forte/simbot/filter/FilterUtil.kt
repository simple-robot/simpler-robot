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
import love.forte.simbot.listener.continuousSessionContext

/**
 * 将一个 [ListenerFilter] 转化为仅会话状态有效。
 *
 */
public fun ListenerFilter.asOnlySession(onlySession: OnlySession): ListenerFilter =
    asOnlySession(onlySession.group, onlySession.key)


/**
 * 将一个 [ListenerFilter] 转化为仅会话状态有效。
 *
 */
public fun ListenerFilter.asOnlySession(
    group: String,
    key: String,
): ListenerFilter = OnlySessionDelegateFilter(group, key.takeIf { it.isNotEmpty() }, this)


private class OnlySessionDelegateFilter(
    private val group: String,
    private val key: String?,
    private val realFilter: ListenerFilter,
) : ListenerFilter {
    override fun getFilterValue(name: String, text: String): String? = realFilter.getFilterValue(name, text)

    override val priority: Int by realFilter::priority

    override fun test(data: FilterData): Boolean {
        val session = data.listenerContext.continuousSessionContext ?: return false
        val check = if (key == null) session[group] != null else session[group, key] != null
        return if (check) realFilter.test(data) else false
    }
}


/**
 * 得到一个仅会话有效的过滤器。
 *
 */
public fun onlySession(onlySession: OnlySession): ListenerFilter =
    onlySession(onlySession.group, onlySession.key)


/**
 * 得到一个仅会话有效的过滤器。
 *
 * 会话有效过滤器的默认优先级是 [PriorityConstant.CORE_FIRST].
 *
 */
public fun onlySession(
    group: String,
    key: String,
    priority: Int = PriorityConstant.CORE_FIRST,
): ListenerFilter {
    return OnlySessionFilter(group, key.takeIf { it.isNotEmpty() }, priority)
}


private class OnlySessionFilter(
    private val group: String,
    private val key: String?,
    override val priority: Int,
) : ListenerFilter {
    override fun test(data: FilterData): Boolean {
        val session = data.listenerContext.continuousSessionContext ?: return false
        return if (key == null) session[group] != null else session[group, key] != null
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
public fun ListenerFilter?.asOnlySessionOrDefault(group: String, key: String): ListenerFilter =
    this?.asOnlySession(group, key) ?: onlySession(group, key)
