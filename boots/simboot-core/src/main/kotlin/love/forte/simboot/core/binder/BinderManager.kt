package love.forte.simboot.core.binder

import love.forte.simboot.listener.ParameterBinderFactory
import kotlin.reflect.KFunction

/**
 * Binder管理器。
 */
public interface BinderManager {
    public operator fun get(id: String): ParameterBinderFactory?
    
    public fun getGlobals(): List<ParameterBinderFactory>
    
    public fun resolveFunctionToBinderFactory(
        function: KFunction<*>,
        instanceGetter: (ParameterBinderFactory.Context) -> Any?,
    ): ParameterBinderFactory
}