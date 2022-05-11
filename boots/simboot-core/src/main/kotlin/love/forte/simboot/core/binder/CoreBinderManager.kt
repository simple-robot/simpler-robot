package love.forte.simboot.core.binder

import love.forte.simboot.listener.ParameterBinderFactory
import kotlin.reflect.KFunction


/**
 * [BinderManager] 的基础实现，提供基本功能。
 */
public class CoreBinderManager(
    private val globalBinderFactories: List<ParameterBinderFactory> = emptyList(),
    private val idBinderFactories: MutableMap<String, ParameterBinderFactory> = mutableMapOf(),
) : BinderManager {
    override val normalBinderFactorySize: Int
        get() = idBinderFactories.size
    
    override val globalBinderFactorySize: Int
        get() = globalBinderFactories.size
    
    override fun get(id: String): ParameterBinderFactory? {
        return idBinderFactories[id]
    }
    
    override fun getGlobals(): List<ParameterBinderFactory> {
        return globalBinderFactories.toList()
    }
    
    override fun resolveFunctionToBinderFactory(
        function: KFunction<*>,
        instanceGetter: (ParameterBinderFactory.Context) -> Any?,
    ): ParameterBinderFactory {
        return function.toBinderFactory(instanceGetter)
    }
}