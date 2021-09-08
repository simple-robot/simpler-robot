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

package love.forte.simbot.mark

import java.util.concurrent.TimeUnit

/**
 * 标记在一个数字类型的属性或参数上，来表示此类型所具体代表的时间。
 */
@Retention(AnnotationRetention.BINARY)
@Target(allowedTargets = [
    AnnotationTarget.TYPE_PARAMETER,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.FUNCTION,
])
@MustBeDocumented
annotation class TimeType(val value: TimeUnit)
