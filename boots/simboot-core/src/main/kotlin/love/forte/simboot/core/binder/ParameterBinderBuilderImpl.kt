package love.forte.simboot.core.binder

import love.forte.simboot.core.application.ParameterBinderBuilder
import love.forte.simboot.listener.ParameterBinderFactory
import love.forte.simbot.InternalSimbotApi
import love.forte.simbot.SimbotIllegalStateException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.reflect.KFunction

internal class ParameterBinderBuilderImpl : ParameterBinderBuilder {
    private val globalBinders = ConcurrentLinkedQueue<ParameterBinderFactory>()
    private val idBinders = ConcurrentHashMap<String, ParameterBinderFactory>()
    
    override fun binder(id: String?, binderFactory: ParameterBinderFactory) {
        if (id == null) {
            globalBinders.add(binderFactory)
        } else {
            idBinders.merge(id, binderFactory) { old, curr ->
                // id为A的binder已经存在.
                throw SimbotIllegalStateException("The binder factory with id [$id] already exists. old: $old, current: $curr")
            }
        }
    }
    
    override fun binder(
        id: String?,
        function: KFunction<*>,
        instanceGetter: (ParameterBinderFactory.Context) -> Any?,
    ): ParameterBinderFactory {
        val factory = function.toBinderFactory(instanceGetter)
        binder(id, factory)
        return factory
    }
    
    @OptIn(InternalSimbotApi::class)
    fun build(): BinderManager {
        return CoreBinderManager(globalBinderFactories = globalBinders.toList(), idBinderFactories = idBinders)
    }
    
}