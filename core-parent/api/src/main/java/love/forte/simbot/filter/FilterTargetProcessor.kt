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

import love.forte.simbot.Context
import love.forte.simbot.filter.FilterTargetProcessor.DefaultProcessor.getTargetText
import love.forte.simbot.processor.Processor
import java.util.regex.Pattern


/**
 * [FilterTargetProcessor] 检测器。
 */
public interface FilterTargetProcessorChecker {
    /**
     * 检测所提供的 `target` 的值是否符合此处理器的预期。
     * 如果符合预期，则会返回一个 [FilterTargetProcessor] 实例，否则返回null。
     */
    fun check(target: String): FilterTargetProcessor?

    /**
     * [FilterTargetProcessorChecker] 的无效化实例，一般在找不到可用解析器的时候才会使用此实例来提供异常信息。
     *
     * 此实例的 [check] 永远会失败并会抛出异常。
     */
    companion object DefaultChecker : FilterTargetProcessorChecker {
        /**
         * 检测所提供的 `target` 的值是否符合此处理器的预期。
         * 如果符合预期，则会返回一个 [FilterTargetProcessor] 实例，否则返回null。
         *
         * @throws IllegalStateException [check] 永远会抛出此异常。
         */
        override fun check(target: String): Nothing {
            throw NoSuchElementException("Cannot found any processor for target '$target'.")
        }
    }
}

public data class FilterTargetProcessorContext(override val mainValue: FilterData) : Context<FilterData>

/**
 * Filter的target处理器，同于根据 `target` 的值获取一个 `String` 类型的值以进行过滤匹配。
 *
 * [过滤器目标处理器][FilterTargetProcessor] 属于一种 [处理器][Processor]。
 */
public interface FilterTargetProcessor : Processor<FilterData, FilterTargetProcessorContext, String?> {

    @JvmDefault
    override fun processor(processContext: FilterTargetProcessorContext): String? = getTargetText(processContext.mainValue)

    /**
     * 获取目标匹配值，匹配方式与 [love.forte.simbot.annotation.Filter.matchType] 一致。
     *
     * 此值最终会与 [love.forte.simbot.annotation.Filter.value] 进行匹配比对。
     *
     * 如果返回值为null：
     *  - 如果当前事件类型为 [消息事件][love.forte.simbot.api.message.events.MessageGet]，
     *  那么理论上是不允许为null的，因此如果最终为null，则会视为**空字符串**(`''`) 进行匹配。
     *  - 如果是其他事件类型，直接认定为匹配失败。
     *
     * 当然，如果[love.forte.simbot.annotation.Filter.value]为空，则会直接放行。
     *
     */
    fun getTargetText(filterData: FilterData): String?


    /**
     * [DefaultProcessor] 无效的实现，一般在找不到可用解析器的时候才会使用此实例来提供异常信息。
     *
     * 此实例的 [getTargetText] 永远会失败并会抛出异常。
     *
     * @throws IllegalStateException [getTargetText] 永远会抛出此异常。
     */
    companion object DefaultProcessor : FilterTargetProcessor {
        /**
         * 永远会失败并抛出 [IllegalStateException].
         */
        override fun getTargetText(filterData: FilterData): Nothing {
            throw IllegalStateException("Default filter Processor cannot get any target text.")
        }
    }

}


/**
 * [FilterTargetProcessor] 的基础target抽象检测器，提供一个匹配函数进行检测。
 */
public abstract class BaseFilterTargetProcessorChecker(private val checkFunction: (String) -> FilterTargetProcessor?) :
    FilterTargetProcessorChecker {
    /** 通过检测函数进行检测。 */
    override fun check(target: String): FilterTargetProcessor? = checkFunction(target)
}


/**
 * [FilterTargetProcessor] 的固定target值抽象检测器，即提供一个固定的匹配值。
 */
public abstract class ConstFilterTargetProcessorChecker<T : FilterTargetProcessor>(
    constTarget: String,
    constProcessor: T,
    ignoreCase: Boolean = false,
) :
    BaseFilterTargetProcessorChecker({ t -> if (t.equals(constTarget, ignoreCase)) constProcessor else null })


/**
 * [FilterTargetProcessor] 的首部target值抽象检测器，即提供一个固定的匹配值。
 */
public abstract class StartsWithConstFilterTargetProcessorChecker<T : FilterTargetProcessor>(
    target: String,
    constProcessor: T,
    ignoreCase: Boolean = false,
) :
    BaseFilterTargetProcessorChecker({ t ->
        if (t.startsWith(target, ignoreCase)) {
            constProcessor
        } else null
    })


/**
 * [FilterTargetProcessor] 的正则匹配target值抽象检测器，提供一个正则实例并进行匹配。
 */
public abstract class RegexMatchesFilterTargetProcessorChecker<T : FilterTargetProcessor>(
    regex: Regex,
    constProcessor: T,
) :
    BaseFilterTargetProcessorChecker({ t ->
        if (regex.matches(t)) {
            constProcessor
        } else null
    }) {
    constructor(pattern: Pattern, constProcessor: T) : this(pattern.toRegex(), constProcessor)
    constructor(regexStr: String, constProcessor: T) : this(Regex(regexStr), constProcessor)
}



