/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ColorBuilderFactory.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

@file:JvmName("ColorBuilderFactories")

package love.forte.nekolog

import love.forte.nekolog.color.ColorBuilder

/**
 *
 * 构建一个ColorBuilder
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public fun interface ColorBuilderFactory {
    fun getColorBuilder(): ColorBuilder
}

/**
 * 获取普通ColorBuilder的工厂
 */
@get:JvmName("getNormalFactory")
val NormalColorBuilderFactory: ColorBuilderFactory = ColorBuilderFactory { ColorBuilder.getInstance() }

/**
 * 获取无色ColorBuilder的工厂
 */
@get:JvmName("getNocolorFactory")
val NocolorBuilderFactory: ColorBuilderFactory = ColorBuilderFactory { ColorBuilder.getNocolorInstance() }
