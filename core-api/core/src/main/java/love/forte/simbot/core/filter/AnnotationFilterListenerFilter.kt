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

import love.forte.simbot.annotation.Filter
import love.forte.simbot.annotation.Filters
import love.forte.simbot.api.message.containers.AccountContainer
import love.forte.simbot.api.message.containers.BotContainer
import love.forte.simbot.api.message.containers.GroupContainer
import love.forte.simbot.api.message.events.MessageGet
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.core.filter.TextKeyword

/**
 * 针对 [Filter] 注解的 单个 filter 实现实例。
 */
interface AnnotationFilterListenerFilter  : ListenerFilter {

    /**
     * 匹配关键词内容。
     */
    val keyword: Keyword?

    /**
     * 过滤器的目标处理器。
     */
    val filterTargetProcessor: FilterTargetProcessor

    /**
     * 匹配模式。
     */
    val matchType: MatchType

    /**
     * 此过滤器的过滤目标。
     */
    val target: String

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
    filter: Filter, filters: Filters,
    filterTargetManager: FilterTargetManager,
) : AnnotationFilterListenerFilter {

    /**
     * 匹配关键词内容。
     */
    override val keyword: Keyword? = with(filter.value) {
        if (isBlank()) null else TextKeyword(this)
    }


    /**
     * 文本匹配模式。
     */
    override val matchType: MatchType = filter.matchType


    /**
     * 过滤目标。
     */
    override val target: String = with(filter.target) {
        (if (isEmpty() && filter.targetByParent) {
            filters.target
        } else this).takeIf { it.isNotEmpty() } ?: FilterTargets.TEXT
    }


    /**
     * 过滤器的目标处理器。
     */
    override val filterTargetProcessor: FilterTargetProcessor = filterTargetManager.getProcessor(target)


    /**
     * 匹配这段消息的账号列表。
     */
    override val codes: Array<String> = with(filter.codes) {
        if (isEmpty() && filter.codesByParent)
            filters.codes
        else this
    }

    /**
     * 账号匹配函数。
     */
    private val codeTestFunc: (AccountContainer) -> Boolean = with(codes) {
        if (isEmpty()) {
            { true }
        } else {
            { it.accountInfo.accountCode in this }
        }
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
     * 群号匹配函数。
     */
    private val groupTestFunc: (GroupContainer) -> Boolean = with(groups) {
        if (isEmpty()) {
            { true }
        } else {
            { it.groupInfo.groupCode in this }
        }
    }


    /**
     * 匹配当前消息的bot列表。
     */
    override val bots: Array<String> = with(filter.bots) {
        if (isEmpty() && filter.botsByParent)
            filters.bots
        else this
    }

    /**
     * bot检测函数
     */
    private val botsTestFunc: (BotContainer) -> Boolean = with(bots) {
        if (isEmpty()) {
            { true }
        } else {
            { it.botInfo.botCode in this }
        }
    }

    /** 当at bot的时候才会触发。 */
    override val atBot: Boolean = with(filter.atBot) {
        if (!this && filter.atBotByParent) filters.atBot
        else this
    }


    /** at任意人就会触发。 */
    override val anyAt: Boolean = with(filter.anyAt) {
        if (!this && filter.anyAtByParent) filters.anyAt
        else this
    }


    /** 当at了指定的人的时候才会触发。 */
    override val at: Array<String> = with(filter.at) {
        if (isEmpty() && filter.atByParent) filters.at
        else this
    }


    private val atAll: Boolean = filter.at.contains("all")


    /** 消息文本前置处理函数 */
    private val textPre: (String) -> String = if (filter.trim) {
        { it.trim() }
    } else {
        { it }
    }


    /**
     * 判断某个消息是否能够进行监听。
     */
    override fun test(data: FilterData): Boolean {
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
        val atDetection: AtDetection = data.atDetection

        if (atBot && !atDetection.atBot()) {
            return false
        }
        // 4.2 any at.
        if (anyAt && !atDetection.atAny()) {
            return false
        }

        // 4.3 assign at.
        if (at.isNotEmpty() && !atDetection.at(at)) {
            return false
        }

        if (atAll && !atDetection.atAll()) {
            return false
        }


        // 5 msg matches


        // no msg. return true.
        // 如果是empty msg, 说明当前监听消息不支持文本匹配, 则不进行keyword匹配。
        if (msg.isEmptyMsg()) {
            return true
        }


        return if (keyword == null) {
            true
        } else {
            // 如果text为null，则认为其无法进行文本匹配。
            // val msgText: String = msg.text?.let(textPre) ?: let {

            val msgText: String = filterTargetProcessor.getTargetText(data)
                ?.let(textPre) ?: let {
                return if (msg is MessageGet) {
                    // 如果是messageGet实例，理论上text不应该为null。
                    // 此时将text视为空字符串进行匹配。
                    matchType.match("", keyword)
                } else false
            }
            matchType.match(msgText, keyword)
        }


    }


    /**
     * 根据动态遍历获取一个值。
     */
    override fun getFilterValue(name: String, text: String): String? {
        val matcher: FilterParameterMatcher = keyword?.parameterMatcher ?: return null
        val targetText = textPre(text)

        return if (matcher.matches(targetText)) {
            matcher.getParameters(targetText)[name]
        } else null
    }

}
