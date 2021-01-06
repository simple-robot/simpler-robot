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

package love.forte.simbot.core.filter

import love.forte.simbot.filter.FilterTargetManager
import love.forte.simbot.filter.FilterTargetProcessor
import love.forte.simbot.filter.FilterTargetProcessorChecker
import java.sql.SQLOutput
import java.util.concurrent.CopyOnWriteArrayList


/**
 * [FilterTargetManager] 的核心实现。
 *
 * @author ForteScarlet
 */
public class CoreFilterTargetManager : FilterTargetManager {

    /**
     * 获取已存在的处理器列表。 此列表为 [可变列表][MutableList],
     * 允许变更其中 [FilterTargetProcessorChecker] 的内容，例如顺序等。
     */
    override val checkers: MutableList<FilterTargetProcessorChecker> = CopyOnWriteArrayList()

    /**
     * 获取一个 [target] 所对应的解析器。会按照保存中的所有解析器的 [FilterTargetProcessorChecker.check] 进行判断，
     * 并取第一个 `check == true` 的结果。如果实例中不存在可用结果，
     * 最终会使用 [FilterTargetProcessor.DefaultProcessor] 的默认实例抛出异常。
     *
     */
    override fun getProcessor(target: String): FilterTargetProcessor {
        for (checker in checkers) {
            val processor = checker.check(target)
            if (processor != null) {
                return processor
            }
        }
        @Suppress("UNREACHABLE_CODE")
        return FilterTargetProcessorChecker.check(target)
    }
}