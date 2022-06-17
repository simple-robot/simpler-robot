package love.forte.simbot.core.scope

import love.forte.simbot.Attribute
import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.attribute
import love.forte.simbot.event.ContinuousSessionContext
import love.forte.simbot.event.ScopeContext

public object SimpleScope {
    
    /**
     * [Global] 的属性名。
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public const val GLOBAL_SCOPE_NAME: String = "\$simple-scope.global$"
    
    /**
     * 全局作用域。 一个 [ScopeContext], 此作用域下的内容在一个Application环境中保持一致。
     *
     */
    @JvmField
    public val Global: Attribute<ScopeContext> = attribute(GLOBAL_SCOPE_NAME)
    
    /**
     * [Global] 的属性名。
     */
    @Suppress("MemberVisibilityCanBePrivate")
    public const val CONTINUOUS_SESSION_SCOPE_NAME: String = "\$simple-scope.continuous-session$"
    
    /**
     * 持续会话作用域. 可以通过持续会话作用域来达成监听函数之间的信息通讯的目的。
     */
    @JvmField
    @ExperimentalSimbotApi
    public val ContinuousSession: Attribute<ContinuousSessionContext> =
        attribute(CONTINUOUS_SESSION_SCOPE_NAME)
}