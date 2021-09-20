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

import love.forte.simbot.api.SimbotExperimentalApi
import love.forte.simbot.api.message.events.MessageGet
import love.forte.simbot.listener.instantOrGlobal


/**
 * 通过 [MsgGet.text][love.forte.simbot.api.message.events.MsgGet.text] 的值作为匹配规则。
 */
public object TextFilterTargetProcessor :
    FilterTargetProcessor by TextTarget,
    ConstFilterTargetProcessorChecker<FilterTargetProcessor>(FilterTargets.TEXT, TextTarget) {

    private object TextTarget : FilterTargetProcessor {
        /** 获取目标匹配值。*/
        override fun getTargetText(filterData: FilterData): String? = filterData.msgGet.text
    }

}


/**
 * 通过 [MessageGet.msg] 的值作为匹配规则。
 * 可能会不支持获取msg（例如非 [MessageGet] 实例 ），当不支持获取msg的时候返回null。
 */
public object MsgFilterTargetProcessor :
    FilterTargetProcessor by MsgTarget,
    ConstFilterTargetProcessorChecker<FilterTargetProcessor>(FilterTargets.MSG, MsgTarget) {
    private object MsgTarget : FilterTargetProcessor {
        /** 获取目标匹配值。当不支持获取msg的时候返回null。*/
        override fun getTargetText(filterData: FilterData): String? = with(filterData.msgGet) {
            if (this is MessageGet) msg else null
        }
    }

}


/**
 * 将 `target` 根据 `.` 切割后的结果。
 * split的长度应该为 `4`.
 */
@JvmInline
internal value class ContextFilterTargetSplit(private val split: List<String>) {
    val targetMap: String get() = split[1]
    val nullable: Boolean get() = with(split[2]) { this == FilterTargets.NULLABLE }
    val valueName: String get() = split[3]
    internal companion object {
        private val splitRegex = Regex("\\.")
        fun getInstance(target: String) : ContextFilterTargetSplit {
            val split = target.split(splitRegex, limit = 4)
            // check
            return ContextFilterTargetSplit(split).apply {
                if (valueName.isBlank()) {
                    throw IllegalStateException("There are no context target value name in '$target'")
                }
            }
        }
    }

}


/**
 * 通过 [love.forte.simbot.listener.ContextMap] 获取实例。
 */
public sealed class ListenContextFilterTargetProcessor(
    protected val nullable: Boolean,
    private val targetName: String,
) : FilterTargetProcessor {
    /** 针对 [ListenContextFilterTargetProcessor] 的检测器实现。  */
    companion object Checker : FilterTargetProcessorChecker {
        /**
         * 检测所提供的 `target` 的值是否符合此处理器的预期。
         */
        override fun check(target: String): ListenContextFilterTargetProcessor? {
            val split = if (
                target.startsWith(FilterTargets.CONTEXT_INSTANT) ||
                target.startsWith(FilterTargets.CONTEXT_GLOBAL) ||
                target.startsWith(FilterTargets.CONTEXT_BOTH)
            ) {
                ContextFilterTargetSplit.getInstance(target)
            } else return null

            return when (split.targetMap) {
                FilterTargets.GLOBAL -> GlobalListenContextFilterTargetProcessor
                FilterTargets.INSTANT -> InstantListenContextFilterTargetProcessor
                FilterTargets.BOTH -> BothListenContextFilterTargetProcessor
                else -> return null
            }.getContextFilterInstance(split)

        }

    }

    /**
     * 进行匹配的首部判断。
     */
    protected abstract val startsWith: String

    /**
     * 当找不到的时候，抛出的异常中使用的目标名称。例如 `global` 或 `instant` 等。
     */
    protected abstract val failTarget: String

    /**
     * 从 [FilterData] 中获取 ContextMap
     */
    abstract fun getContextValue(filterData: FilterData, targetName: String): Any?

    /**
     * 获取上下文中的值并 `toString` .
     */
    override fun getTargetText(filterData: FilterData): String? {
        val value = getContextValue(filterData, targetName)
        return value?.toString()
            ?: if (nullable) null else throw NoSuchElementException("Cannot get target value '$targetName' from '$failTarget'.")
    }


    /**
     * 由 [ListenContextFilterTargetProcessor] 几个实现类的伴生对象进行实现的接口，通过参数构建对应的 [FilterTargetProcessor] 实例。
     */
    private interface ContextInstanceAble {
        fun getContextFilterInstance(split: ContextFilterTargetSplit): ListenContextFilterTargetProcessor
    }


    /**
     * 使用 [love.forte.simbot.listener.ListenerContext] 进行过滤的处理器。
     */
    private sealed class GlobalListenContextFilterTargetProcessor(nullable: Boolean, targetName: String) :
        ListenContextFilterTargetProcessor(nullable, targetName) {

        internal companion object : ContextInstanceAble {
            override fun getContextFilterInstance(split: ContextFilterTargetSplit): GlobalListenContextFilterTargetProcessor {
                return if (split.nullable) NullableListenGlobalContextFilterTargetProcessor(split.valueName)
                else NonnullListenGlobalContextFilterTargetProcessor(split.valueName)
            }
        }

        override val startsWith: String
            get() = if (nullable) FilterTargets.CONTEXT_GLOBAL_NULLABLE else FilterTargets.CONTEXT_GLOBAL_NONNULL

        override val failTarget: String
            get() = FilterTargets.CONTEXT_GLOBAL

        /**
         * 获取实际使用的监听上下文值。
         */
        @OptIn(SimbotExperimentalApi::class)
        override fun getContextValue(filterData: FilterData, targetName: String): Any? {
            return filterData.listenerContext.global(targetName)
        }


        /**
         * 可以为null的 [GlobalListenContextFilterTargetProcessor] 实例。
         */
        internal class NullableListenGlobalContextFilterTargetProcessor(targetName: String) :
            GlobalListenContextFilterTargetProcessor(true, targetName)

        /**
         * nonnull的 [GlobalListenContextFilterTargetProcessor] 实例。
         */
        internal class NonnullListenGlobalContextFilterTargetProcessor(targetName: String) :
            GlobalListenContextFilterTargetProcessor(false, targetName)
    }


    /**
     * 使用 [love.forte.simbot.listener.ListenerContext] 进行过滤的处理器。
     */
    private sealed class InstantListenContextFilterTargetProcessor(nullable: Boolean, targetName: String) :
        ListenContextFilterTargetProcessor(nullable, targetName) {

        internal companion object : ContextInstanceAble {
            override fun getContextFilterInstance(split: ContextFilterTargetSplit): InstantListenContextFilterTargetProcessor {
                return if (split.nullable) NullableListenInstantContextFilterTargetProcessor(split.valueName)
                else NonnullListenInstantContextFilterTargetProcessor(split.valueName)
            }
        }

        override val startsWith: String
            get() = if (nullable) FilterTargets.CONTEXT_GLOBAL_NULLABLE else FilterTargets.CONTEXT_GLOBAL_NONNULL

        override val failTarget: String
            get() = FilterTargets.CONTEXT_INSTANT

        /**
         * 获取实际使用的监听上下文值。
         */
        @OptIn(SimbotExperimentalApi::class)
        override fun getContextValue(filterData: FilterData, targetName: String): Any? {
            return filterData.listenerContext.instant(targetName)
        }

        /**
         * 可以为null的 [InstantListenContextFilterTargetProcessor] 实例。
         */
        internal class NullableListenInstantContextFilterTargetProcessor(targetName: String) :
            InstantListenContextFilterTargetProcessor(true, targetName)

        /**
         * nonnull的 [InstantListenContextFilterTargetProcessor] 实例。
         */
        internal class NonnullListenInstantContextFilterTargetProcessor(targetName: String) :
            InstantListenContextFilterTargetProcessor(false, targetName)
    }


    private sealed class BothListenContextFilterTargetProcessor(nullable: Boolean, targetName: String) :
        ListenContextFilterTargetProcessor(nullable, targetName) {

        internal companion object : ContextInstanceAble {
            override fun getContextFilterInstance(split: ContextFilterTargetSplit): BothListenContextFilterTargetProcessor {
                return if (split.nullable) NullableListenBothContextFilterTargetProcessor(split.valueName)
                else NonnullListenBothContextFilterTargetProcessor(split.valueName)
            }
        }

        override val startsWith: String
            get() = if (nullable) FilterTargets.CONTEXT_GLOBAL_NULLABLE else FilterTargets.CONTEXT_GLOBAL_NONNULL

        override val failTarget: String
            get() = FilterTargets.CONTEXT_INSTANT

        /**
         * 获取实际使用的监听上下文值。
         */
        @OptIn(SimbotExperimentalApi::class)
        override fun getContextValue(filterData: FilterData, targetName: String): Any? {
            return filterData.listenerContext.instantOrGlobal(targetName)
        }

        /**
         * 可以为null的 [InstantListenContextFilterTargetProcessor] 实例。
         */
        internal class NullableListenBothContextFilterTargetProcessor(targetName: String) :
            BothListenContextFilterTargetProcessor(true, targetName)

        /**
         * nonnull的 [InstantListenContextFilterTargetProcessor] 实例。
         */
        internal class NonnullListenBothContextFilterTargetProcessor(targetName: String) :
            BothListenContextFilterTargetProcessor(false, targetName)

    }


}

