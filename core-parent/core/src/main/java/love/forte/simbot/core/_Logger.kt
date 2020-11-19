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

@file:Suppress("NOTHING_TO_INLINE")
@file:JvmName("CoreLoggerHelper")

package love.forte.simbot.core

import love.forte.common.language.Language
import org.slf4j.Logger
import org.slf4j.LoggerFactory


public abstract class NamedCompLogger(name: String) {
    val logger: Logger = LoggerFactory.getLogger(name)
}

public abstract class TypedCompLogger(type: Class<*>) {
    val logger: Logger = LoggerFactory.getLogger(type)
}


public inline fun Logger.infof(target: String, vararg args: Any?) {
    info(Language.format(target, args))
}
public inline fun Logger.debugf(target: String, vararg args: Any?) {
    debug(Language.format(target, args))
}
public inline fun Logger.warnf(target: String, vararg args: Any?) {
    warn(Language.format(target, args))
}
public inline fun Logger.errorf(target: String, vararg args: Any?) {
    error(Language.format(target, args))
}
public inline fun Logger.tracef(target: String, vararg args: Any?) {
    trace(Language.format(target, args))
}


public inline fun Logger.infoEf(target: String, e: Throwable, vararg args: Any?) {
    info(Language.format(target, args), e)
}
public inline fun Logger.debugEf(target: String, e: Throwable, vararg args: Any?) {
    debug(Language.format(target, args), e)
}
public inline fun Logger.warnEf(target: String, e: Throwable, vararg args: Any?) {
    warn(Language.format(target, args), e)
}
public inline fun Logger.errorEf(target: String, e: Throwable, vararg args: Any?) {
    error(Language.format(target, args), e)
}
public inline fun Logger.traceEf(target: String, e: Throwable, vararg args: Any?) {
    trace(Language.format(target, args), e)
}





