/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     _Logger.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core

import org.slf4j.Logger
import org.slf4j.LoggerFactory


public abstract class CompLogger(name: String) {
    val logger: Logger = LoggerFactory.getLogger(name)
}








