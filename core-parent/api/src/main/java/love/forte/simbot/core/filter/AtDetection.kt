/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     AtDetection.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.filter



/**
 * at检测器。用于判断bot是否被at了。
 */
public fun interface AtDetection {

    /**
     * 如果bot被at了，则返回true。
     */
    fun atBot(): Boolean
}