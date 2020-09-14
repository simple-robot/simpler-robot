/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     NekoLogConfiguration.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.nekolog


/**
 * neko log 配置
 */
data class NekoLogConfiguration(
    // 是否开启language
    var enableLanguage: Boolean = true,
    // 是否开启颜色
    var enableColor: Boolean = true
)
