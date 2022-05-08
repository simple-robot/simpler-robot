/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
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

package love.forte.simboot.core.application

import love.forte.simboot.annotation.Binder
import love.forte.simboot.core.listener.toBinderFactory
import love.forte.simboot.listener.ParameterBinder
import love.forte.simboot.listener.ParameterBinderFactory
import love.forte.simboot.listener.ParameterBinderResult
import love.forte.simbot.SimbotIllegalStateException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.reflect.KFunction


/**
 *
 * @author ForteScarlet
 */
public interface ParameterBinderBuilder {
    
    /**
     * 提供一个 [binderFactory].
     *
     * 如果不指定 [id]，则为对全局所有监听函数生效的binder。如果指定id，那么只有当一个监听函数上标记了 [Binder] 注解的时候才会被使用。
     */
    public fun binder(id: String? = null, binderFactory: ParameterBinderFactory)
    
    
    /**
     * 将一个 [KFunction][function] 解析为 [ParameterBinderFactory].
     *
     * 此 function必须遵循规则：
     * - 返回值类型必须是 [ParameterBinder] 或 [ParameterBinderResult] 类型。
     * - 参数或则receiver有且只能有一个，且类型**必须是** [ParameterBinderFactory.Context]
     *
     * @param function 解析目标
     * @param instanceGetter 获取 [function] 执行实例的对象
     * @return 解析的结果。此结果已经被添加到当前环境中。
     */
    public fun binder(
        id: String? = null,
        function: KFunction<*>,
        instanceGetter: (ParameterBinderFactory.Context) -> Any?,
    ): ParameterBinderFactory
    
}


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
    
    fun build(): BinderManagerImpl {
        return BinderManagerImpl(globalBinders = globalBinders.toList(), idBinders = idBinders)
    }
    
}


public interface BinderManager {
    public operator fun get(id: String): ParameterBinderFactory?
    
    public fun getGlobals(): List<ParameterBinderFactory>
    
    public fun resolveFunctionToBinderFactory(
        function: KFunction<*>,
        instanceGetter: (ParameterBinderFactory.Context) -> Any?,
    ): ParameterBinderFactory
}


internal class BinderManagerImpl(
    private val globalBinders: List<ParameterBinderFactory> = emptyList(),
    private val idBinders: MutableMap<String, ParameterBinderFactory> = mutableMapOf(),
) : BinderManager {
    override fun get(id: String): ParameterBinderFactory? {
        return idBinders[id]
    }
    
    override fun getGlobals(): List<ParameterBinderFactory> {
        return globalBinders.toList()
    }
    
    override fun resolveFunctionToBinderFactory(
        function: KFunction<*>,
        instanceGetter: (ParameterBinderFactory.Context) -> Any?,
    ): ParameterBinderFactory {
        return function.toBinderFactory(instanceGetter)
    }
}