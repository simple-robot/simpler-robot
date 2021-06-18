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

/**
 *
 * 为 [love.forte.simbot.annotation.Filter.target] 提供一些常量默认值。
 *
 * @author ForteScarlet
 */
public object FilterTargets {
    const val TEXT = "text"
    const val MSG = "msg"

    // context.[g|i|b].[n|a].value

    internal const val CONTEXT = "context"
    internal const val GLOBAL = "global"
    internal const val INSTANT = "instant"
    internal const val BOTH = "both"

    internal const val CONTEXT_GLOBAL = "$CONTEXT.$GLOBAL"
    internal const val CONTEXT_INSTANT = "$CONTEXT.$INSTANT"

    /** 优先尝试使用instant，如果没有则尝试global。 */
    internal const val CONTEXT_BOTH = "$CONTEXT.$BOTH"

    internal const val NULLABLE = "nullable"
    internal const val NONNULL = "nonnull"

    const val CONTEXT_GLOBAL_NULLABLE = "$CONTEXT_GLOBAL.$NULLABLE."
    const val CONTEXT_GLOBAL_NONNULL = "$CONTEXT_GLOBAL.$NONNULL."
    const val CONTEXT_INSTANT_NULLABLE = "$CONTEXT_INSTANT.$NULLABLE."
    const val CONTEXT_INSTANT_NONNULL = "$CONTEXT_INSTANT.$NONNULL."
    const val CONTEXT_BOTH_NULLABLE = "$CONTEXT_BOTH.$NULLABLE."
    const val CONTEXT_BOTH_NONNULL = "$CONTEXT_BOTH.$NONNULL."
}

