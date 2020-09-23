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

package love.forte.simbot.core.api.listener


/**
 *
 * 判断当前监听事件中，bot是否被at了。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public fun interface AtDetection {
    fun test(): Boolean
}