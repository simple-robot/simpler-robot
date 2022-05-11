package love.forte.simboot.core.binder

import love.forte.simboot.listener.ParameterBinderFactory
import kotlin.reflect.KFunction

/**
 * Binder管理器。
 */
public interface BinderManager {
    
    /**
     * 普通的binder工厂的数量。
     */
    public val normalBinderFactorySize: Int
    
    /**
     * 全局性的binder工厂的数量。
     */
    public val globalBinderFactorySize: Int
    
    /**
     * 根据ID获取一个指定的普通binder工厂。
     */
    public operator fun get(id: String): ParameterBinderFactory?
    
    /**
     * 获取所有的全局binder工厂。
     */
    public fun getGlobals(): List<ParameterBinderFactory>
    
    /**
     * 解析一个函数并将其作为一个binder工厂。
     */
    public fun resolveFunctionToBinderFactory(
        function: KFunction<*>,
        instanceGetter: (ParameterBinderFactory.Context) -> Any?,
    ): ParameterBinderFactory
}