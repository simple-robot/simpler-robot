/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     Builder.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

package love.forte.simbot.builder


/**
 * 一个没有参数的构建器接口。此接口定义一个构建器所需要的 [build] 方法。
 * @author ForteScarlet
 */
interface Builder<T> {
    /**
     * 根据种种方法之后，得到一个所需实例 [T].
     */
    fun build(): T
}