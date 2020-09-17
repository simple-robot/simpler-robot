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

import love.forte.common.configuration.annotation.AsConfig
import org.slf4j.event.Level


/**
 * neko log 配置
 */
@AsConfig(prefix = "nekolog", allField = true)
data class NekoLogConfiguration(
    // 是否开启language
    var enableLanguage: Boolean = true,
    // 是否开启颜色
    var enableColor: Boolean = true,
    // 使用的日志等级
    var level: Level = Level.INFO
)
