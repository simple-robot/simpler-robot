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

package love.forte.simbot.filter

import catcode.CatCodeUtil
import love.forte.simbot.annotation.Filters
import love.forte.simbot.api.message.containers.AccountContainer
import love.forte.simbot.api.message.containers.BotContainer
import love.forte.simbot.api.message.containers.GroupContainer
import love.forte.simbot.api.message.events.MessageGet
import love.forte.simbot.api.message.events.MsgGet

public interface AnnotationFiltersListenerFilter : ListenerFilter {

    /**
     * 进行过滤匹配。
     */
    override fun test(data: FilterData): Boolean

    /**
     * 获取所有的子过滤器。
     */
    val childrenFilter: List<ListenerFilter>

    /**
     * 子过滤器多项匹配规则。
     */
    val childrenMostMatchType: MostFilterMatcher

    /**
     * 获取所有的自定义过滤器。
     */
    val customFilter: List<ListenerFilter>

    /**
     * 自定义过滤器多项匹配规则。
     */
    val customFilterMostMatchType: MostFilterMatcher

    /**
     * 匹配当前消息的账号列表。
     */
    val codes: Array<String>

    /**
     * 匹配当前消息的群列表。
     */
    val groups: Array<String>

    /**
     * 匹配当前消息的bot列表。
     */
    val bots: Array<String>

    /**
     * 当bot被at的时候才会触发。
     */
    val atBot: Boolean

    /**
     * 有人被at了才会触发。其中可能不包括bot自身。
     */
    val anyAt: Boolean

    /**
     * 当下列账号中的人被at了才会触发。
     */
    val at: Array<String>
}




/**
 * [AnnotationFiltersListenerFilter] 实现。
 */
public class AnnotationFiltersListenerFilterImpl(
    filters: Filters,
    filterManager: FilterManager,
    filterTargetManager: FilterTargetManager,
) : AnnotationFiltersListenerFilter {

    /**
     * 全部的子过滤器。
     */
    private val _childrenFilter: List<AnnotationFilterListenerFilter> =
        filters.value.run {
            if (isEmpty()) emptyList()
            else map {
                AnnotationFilterListenerFilterImpl(it, filters, filterTargetManager)
            }
        }

    /**
     * 全部的自定义过滤器。
     */
    private val _customFilter: List<ListenerFilter> =
        filters.customFilter.run {
            if (isEmpty()) emptyList()
            else map {
                filterManager.getFilter(it) ?: throw NoSuchFilterException(it)
            }
        }

    /**
     * 获取所有的子过滤器。
     */
    override val childrenFilter: List<ListenerFilter>
        get() = _childrenFilter

    /**
     * 子过滤器多项匹配规则。
     */
    override val childrenMostMatchType: MostFilterMatcher = filters.mostMatchType.filterMatcher

    /**
     * 获取所有的自定义过滤器。
     */
    override val customFilter: List<ListenerFilter>
        get() = _customFilter

    /**
     * 自定义过滤器多项匹配规则。
     */
    override val customFilterMostMatchType: MostFilterMatcher = filters.customMostMatchType.filterMatcher

    /**
     * 匹配当前消息的账号列表。
     */
    override val codes: Array<String> = filters.codes

    /**
     * 账号匹配函数。
     */
    private val codeTestFunc: (AccountContainer) -> Boolean = with(codes) {
        if (isEmpty()) {
            { true }
        } else {
            { contains(it.accountInfo.accountCode) }
        }
    }

    /**
     * 匹配当前消息的群列表。
     */
    override val groups: Array<String> = filters.groups

    /**
     * 群号匹配函数。
     */
    private val groupTestFunc: (GroupContainer) -> Boolean = with(groups) {
        if (isEmpty()) {
            { true }
        } else {
            { contains(it.groupInfo.groupCode) }
        }
    }


    /**
     * 匹配当前消息的bot列表。
     */
    override val bots: Array<String> = filters.bots

    /**
     * bot检测函数
     */
    private val botsTestFunc: (BotContainer) -> Boolean = with(bots) {
        if (isEmpty()) {
            { true }
        } else {
            { contains(it.botInfo.botCode) }
        }
    }

    /**
     * 当bot被at的时候才会触发。
     */
    override val atBot: Boolean = filters.atBot

    /**
     * 有人被at了才会触发。其中可能不包括bot自身。
     */
    override val anyAt: Boolean = filters.anyAt


    /**
     * any at 检测函数。
     */
    private val anyAtTestFunc: (MsgGet) -> Boolean = with(anyAt) {
        if (this) {
            // 要匹配at
            f@{
                if (it is MessageGet) {
                    // 如果获取到的msg为null，则认为其无法匹配msg，直接放行。
                    val text: String = it.msg
                    CatCodeUtil.contains(text, "at")
                } else {
                    false
                }
            }

        } else {
            { true }
        }
    }


    /**
     * 当下列账号中的人被at了才会触发。
     */
    override val at: Array<String> = filters.at


    private val atTestFunc: (MsgGet) -> Boolean = with(at) {
        if (isEmpty()) {
            { true }
        } else {
            f@{
                if (it is MessageGet) {
                    // 如果获取到的为null，则认为其无法判断at，直接放行。
                    val text = it.msg
                    at.all { atCode ->
                        CatCodeUtil.contains(text, "at", "code", atCode)
                    }
                } else {
                    false
                }

            }
        }
    }


    /**
     * 判断某个消息是否能够进行监听。
     */
    override fun test(data: FilterData): Boolean {
        // 1. children
        val childrenMost: Boolean = with(_childrenFilter) {
            if (isNotEmpty()) {
                childrenMostMatchType.mostMatch(data, this)
            } else true
        }
        if (!childrenMost) {
            return false
        }


        // 2. this match.
        if (!thisMatches(data)) {
            return false
        }

        // 3. customs.
        return with(_customFilter) {
            if (isNotEmpty()) {
                customFilterMostMatchType.mostMatch(data, this)
            } else true
        }
    }

    /**
     * 尝试从文本中提取动态过滤参数。
     */
    override fun getFilterValue(name: String, text: String): String? {
        // 1. children
        _childrenFilter.forEach {
            val filterValue: String? = it.getFilterValue(name, text)
            if (filterValue != null) {
                return filterValue
            }
        }

        // 2. customs.
        _customFilter.forEach {
            val filterValue: String? = it.getFilterValue(name, text)
            if (filterValue != null) {
                return filterValue
            }
        }

        // nothing.
        return null
    }

    /**
     * this matches.
     */
    private fun thisMatches(data: FilterData): Boolean {
        val msg: MsgGet = data.msgGet
        // 1 bot
        if (!botsTestFunc(msg)) {
            return false
        }
        // 2 group (if can)
        if (msg is GroupContainer && !groupTestFunc(msg)) {
            return false
        }
        // 3 code
        if (!codeTestFunc(msg)) {
            return false
        }
        // 4 at
        // 4.1 bot at
        if (atBot && !data.atDetection.atBot()) {
            return false
        }
        // 4.2 any at.
        if (!anyAtTestFunc(msg)) {
            return false
        }
        // 4.3 assign at.
        if (!atTestFunc(msg)) {
            return false
        }

        return true
    }

}

