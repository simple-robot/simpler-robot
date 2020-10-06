/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     AnnotationListenerFilter.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.filter

import love.forte.catcode.CatCodeUtil
import love.forte.simbot.core.annotation.Filter
import love.forte.simbot.core.annotation.Filters


public class AnnotationFiltersListenerFilter() {

}


/**
 * 针对 [Filter] 注解的 单个 filter 实现实例。
 */
public interface AnnotationFilterListenerFilter : ListenerFilter {
    /**
     * 匹配关键词内容。
     */
    val keyword: Keyword

    /**
     * 匹配模式。
     */
    val matchType: MatchType

    /**
     * 匹配这段消息的账号列表。
     *
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

    /** 当at bot的时候才会触发。 */
    val atBot: Boolean

    /** 当at任意人的时候才会触发。 */
    val anyAt: Boolean

    /** 当at了指定的人的时候才会触发。 */
    val at: Array<String>

}

/**
 * [AnnotationFilterListenerFilter] 实现。
 */
public class AnnotationFilterListenerFilterImpl(
    filter: Filter, filters: Filters
) : AnnotationFilterListenerFilter {

    /**
     * 匹配关键词内容。
     */
    override val keyword: Keyword = with(filter.value) {
        if (isBlank()) EmptyKeyword else TextKeyword(this)
    }

    /**
     * 匹配模式。
     */
    override val matchType: MatchType = filter.matchType

    /**
     * 匹配这段消息的账号列表。
     */
    override val codes: Array<String> = with(filter.codes) {
        if (isEmpty() && filter.codesByParent)
            filters.codes
        else this
    }

    /**
     * 匹配当前消息的群列表。
     */
    override val groups: Array<String> = with(filter.groups) {
        if (isEmpty() && filter.groupsByParent)
            filters.groups
        else this
    }

    /**
     * 匹配当前消息的bot列表。
     */
    override val bots: Array<String> = with(filter.bots) {
        if (isEmpty() && filter.botsByParent)
            filters.bots
        else this
    }

    /** 当at bot的时候才会触发。 */
    override val atBot: Boolean = with(filter.atBot) {
        if (!this && filter.atBotByParent) filters.atBot
        else this
    }

    /** 当at全体的时候才会触发。 */
    override val anyAt: Boolean = with(filter.anyAt) {
        if(!this && filter.anyAtByParent) filters.anyAt
        else filters.anyAt
    }

    /** 当at了指定的人的时候才会触发。 */
    override val at: Array<String> = with(filter.at) {
        if(isEmpty() && filter.atByParent) filters.at
        else this
    }

    /** 消息文本前置处理函数 */
    private val textPre: (String) -> String


    // 初始化text前置处理函数
    init {
        val filterClearAllCode = filter.clearAllCode
        textPre = if (filter.trim) {
            when {
                filterClearAllCode -> {
                    {
                        it.trim().let { trimText ->
                            CatCodeUtil.remove(trimText)
                        }
                    }
                }
                filter.clearCode.isNotEmpty() -> {
                    val filterClearCodes = filter.clearCode
                    {
                        it.trim().let { trimText ->
                            filterClearCodes.fold(trimText) { m, t ->
                                CatCodeUtil.removeByType(t, m)
                            }
                        }
                    }
                }
                // 不清除code
                else -> {
                    { it.trim() }
                }
            }
        } else {
            when {
                filterClearAllCode -> {
                    { CatCodeUtil.remove(it) }
                }
                filter.clearCode.isNotEmpty() -> {
                    val filterClearCodes = filter.clearCode
                    {
                        filterClearCodes.fold(it) { m, t ->
                            CatCodeUtil.removeByType(t, m)
                        }
                    }
                }
                // 不清除code
                else -> {
                    { it }
                }
            }
        }
    }

    /**
     * 判断某个消息是否能够进行监听。
     */
    override fun test(data: FilterData): Boolean {
        // 1 bot
        // 2 group (if can)
        // 3 code
        // 4 at
        // 5 msg matches


        TODO("Not yet implemented")
    }
}
