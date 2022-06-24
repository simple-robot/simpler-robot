/*
 *  Copyright (c) 2022 ForteScarlet <ForteScarlet@163.com>
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

package love.forte.simbot.core.scope

import love.forte.simbot.Attribute
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.attribute
import love.forte.simbot.event.ContinuousSessionContext
import love.forte.simbot.event.EventProcessingContext
import love.forte.simbot.event.ScopeContext

/**
 *
 * 应用于 [EventProcessingContext.getAttribute] 中，提供部分标准作用域。
 *
 * simbot所提供的默认 [EventProcessingContext] 实现会支持
 * [SimpleScope] 中的作用域，并且建议第三方实现也针对 [SimpleScope] 提供支持。
 *
 */
public object SimpleScope {
    
    /**
     * [Global] 的属性名。
     */
    private const val GLOBAL_SCOPE_NAME: String = "\$simple-scope.global$"
    
    /**
     * 全局作用域。 一个 [ScopeContext], 此作用域下的内容在一个Application环境中保持一致。
     *
     */
    @JvmField
    public val Global: Attribute<ScopeContext> = attribute(GLOBAL_SCOPE_NAME)
    
    /**
     * [Global] 的属性名。
     */
    private const val CONTINUOUS_SESSION_SCOPE_NAME: String = "\$simple-scope.continuous-session$"
    
    /**
     * 持续会话作用域. 可以通过持续会话作用域来达成监听函数之间的信息通讯的目的。
     */
    @JvmField
    @ExperimentalSimbotApi
    public val ContinuousSession: Attribute<ContinuousSessionContext> =
        attribute(CONTINUOUS_SESSION_SCOPE_NAME)
}