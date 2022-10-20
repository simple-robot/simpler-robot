/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

@file:JvmSynthetic
@file:JvmMultifileClass
@file:JvmName("LoggerFactories")

package love.forte.simbot

import love.forte.simbot.logger.Logger
import love.forte.simbot.logger.logger
import kotlin.reflect.KClass

/**
 * 日志工厂, 用于得到一个日志实例.
 * @author ForteScarlet
 */
@Deprecated(
    "Use love.forte.simbot.logger.LoggerFactory",
    ReplaceWith("love.forte.simbot.logger.LoggerFactory"),
    DeprecationLevel.ERROR
)
public object LoggerFactory {
    
    /**
     * 根据名称得到一个 [Logger].
     *
     * @see LoggerFactory.getLogger
     */
    @JvmStatic
    public fun getLogger(name: String): Logger = love.forte.simbot.logger.LoggerFactory.getLogger(name)
    
    /**
     * 根据 [KClass]（的全限定名称）构建一个 [Logger].
     *
     */
    @JvmStatic
    public fun getLogger(type: KClass<*>): Logger =
        love.forte.simbot.logger.LoggerFactory.getLogger(type)
    
    /**
     * 根据 [T]（的全限定名称）构建一个 [Logger].
     *
     */
    public inline fun <reified T : Any> getLogger(): Logger = love.forte.simbot.logger.LoggerFactory.logger<T>()
    
    
    /**
     * 根据 [Class] 构建一个 [Logger].
     *
     * @see LoggerFactory.getLogger
     */
    @JvmStatic
    public fun getLogger(type: Class<*>): Logger = love.forte.simbot.logger.LoggerFactory.getLogger(type)
    
}
