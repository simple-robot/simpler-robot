/*
 * Copyright (c) 2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.di.core.internal

import love.forte.di.*
import love.forte.di.core.CoreBeanManager
import love.forte.di.core.CoreBeanManagerBeanRegisterPostProcessor
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.reflect.KClass
import kotlin.reflect.cast
import kotlin.reflect.full.isSubclassOf

/**
 * [CoreBeanManager] 基础实现.
 *
 * [CoreBeanManager] 不关系 [Bean] 的初始化、依赖关系、初始化或者单例情况，这一切都应由注册者决定。
 *
 * 修改之间线程安全，但是获取的时候不会。
 *
 *
 * @author ForteScarlet
 *
 * @param processorList 后置处理器列表
 * @param parentContainer 父容器。任何获取与注册检测都会优先使用父容器。
 */
internal class CoreBeanManagerImpl(
    override val parentContainer: BeanContainer = BeanContainer,
    processorList: List<CoreBeanManagerBeanRegisterPostProcessor>,
) : CoreBeanManager, HierarchicalBeanContainer {
    private val processors = processorList.sorted()

    private val locker = ReentrantReadWriteLock()

    /** Bean与其对应唯一名称的Map。 */
    private val nameBeanMap = ConcurrentHashMap<String, Bean<*>>()

    /** 从 [nameBeanMap] 中获取对应类型的 [Bean] 并记录其类型对应关系。 */
    private val typeBeanMap = ConcurrentHashMap<KClass<*>, Bean<*>>()

    /** 一次实例化后记录其值。 */
    private val instanceNamedMap = ConcurrentHashMap<String, Any>()

    private inner class DelegateBean<T : Any>(private val name: String, private val delegate: Bean<T>) : Bean<T> by delegate {
        private val getter: () -> T = if (delegate.isSingleton) {
            {
                locker.read {
                    val instance = instanceNamedMap[name]
                    if (instance != null) {
                        return@read delegate.type.cast(instance)
                    } else {
                        locker.write {
                            val instance0 = instanceNamedMap[name]
                            if (instance0 != null) {
                                return@write delegate.type.cast(instance0)
                            } else {
                                val value = delegate.get()
                                instanceNamedMap[name] = value
                                return@write value
                            }
                        }
                    }
                }

            }
        } else delegate::get

        override fun get(): T = getter()
    }

    /**
     * 直接注册一个 [Bean]. 进行处理后验证其名称.
     */
    override fun register(name: String, bean: Bean<*>) = locker.write {
        var beanProcessed: Bean<*>? = bean
        for (processor in processors) {
            @Suppress("UNCHECKED_CAST")
            beanProcessed = processor.process(beanProcessed!! as Bean<Any>, this)
            if (beanProcessed == null) {
                return
            }
        }

        beanProcessed as Bean<*>

        // check name
        if (contains(name)) {
            throw MultiSameTypeBeanException(name)
        }

        // not container, register to local
        nameBeanMap[name] = DelegateBean(name, beanProcessed)
    }


    override fun contains(name: String): Boolean {
        return locker.read { name in parentContainer || containsLocal(name) }
    }


    override fun containsLocal(name: String): Boolean {
        return locker.read { nameBeanMap.containsKey(name) }
    }

    override fun getOrNull(name: String): Any? {
        return locker.read { nameBeanMap[name]?.get() }
    }

    override fun <T : Any> getOrNull(type: KClass<T>): T? {
        return typeBeanMap[type]?.let { type.cast(it.get()) }
            ?: locker.write {
                // get again
                typeBeanMap[type]?.let { type.cast(it.get()) } ?: run {
                    val subTypes = nameBeanMap.values.filter { b -> b.type.isSubclassOf(type) }
                    when {
                        subTypes.isEmpty() -> return null
                        subTypes.size == 1 -> {
                            val only = subTypes.first()
                            return only.getWithCast { type }.also {
                                typeBeanMap[type] = only
                            }
                        }
                        else -> {
                            // try find
                            val isPreferredList = subTypes.filter(Bean<*>::isPreferred)
                            if (isPreferredList.size == 1) {
                                val only = isPreferredList.first()
                                return only.getWithCast { type }.also {
                                    typeBeanMap[type] = only
                                }
                            } else {
                                val sortedBy = subTypes.sortedBy { it.priority }
                                val first = sortedBy[0]
                                val second = sortedBy[1]
                                if (first.priority == second.priority) {
                                    throw MultiSameTypeBeanException("$type")
                                } else {
                                    first.getWithCast { type }.also {
                                        typeBeanMap[type] = first
                                    }
                                }
                            }
                        }
                    }
                }
            }

    }

    override fun getTypeOrNull(name: String): KClass<*>? {
        return locker.read { nameBeanMap[name]?.type }
    }

    override fun <T : Any> getAll(type: KClass<T>?): List<String> {
        return locker.read {
            if (type == null) nameBeanMap.keys().toList()
            else nameBeanMap.mapNotNull { (k, v) -> k.takeIf { v.type.isSubclassOf(type) } }
        }
    }
}
