/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
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

package love.forte.simbot.core

import org.slf4j.Logger
import org.slf4j.LoggerFactory


public abstract class NamedCompLogger(name: String) {
    val logger: Logger = LoggerFactory.getLogger(name)
}

public abstract class TypedCompLogger(type: Class<*>) {
    val logger: Logger = LoggerFactory.getLogger(type)
}








