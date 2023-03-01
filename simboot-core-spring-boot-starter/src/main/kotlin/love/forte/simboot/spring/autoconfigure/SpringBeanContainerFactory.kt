/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simboot.spring.autoconfigure

import love.forte.di.*
import love.forte.simboot.factory.BeanContainerFactory
import org.springframework.aop.support.AopUtils
import org.springframework.beans.factory.ListableBeanFactory
import org.springframework.beans.factory.NoSuchBeanDefinitionException
import org.springframework.beans.factory.NoUniqueBeanDefinitionException
import org.springframework.util.ClassUtils
import kotlin.reflect.KClass

// region bean container

/**
 * [BeanContainerFactory] 基于 [ListableBeanFactory] 的实现。
 */
public open class SpringBeanContainerFactory(private val listableBeanFactory: ListableBeanFactory) :
    BeanContainerFactory {
    override fun invoke(configuration: love.forte.simboot.Configuration): BeanContainer {
        return SpringBeanContainer(listableBeanFactory)
    }
}

/**
 * 基于 [ListableBeanFactory] 的 [love.forte.di.BeanContainer] 实现。
 */
public open class SpringBeanContainer(private val listableBeanFactory: ListableBeanFactory) : BeanContainer {
    
    //// ———————— container ———————— ////
    override fun contains(name: String): Boolean {
        return listableBeanFactory.containsBean(name)
    }
    
    override fun get(name: String): Any {
        return try {
            listableBeanFactory.getBean(name)
        } catch (e: NoSuchBeanDefinitionException) {
            throw NoSuchBeanException("named $name", e)
        }
    }
    
    
    override fun <T : Any> get(name: String, type: KClass<T>): T {
        return try {
            listableBeanFactory.getBean(name, type.java)
        } catch (e: NoSuchBeanDefinitionException) {
            throw NoSuchBeanException("named $name with type of $type", e)
        }
    }
    
    override fun <T : Any> getOrNull(name: String, type: KClass<T>): T? {
        return if (name in this) this[name, type] else null
    }
    
    @Api4J
    override fun <T : Any> get(name: String, type: Class<T>): T {
        return try {
            listableBeanFactory.getBean(name, type)
        } catch (e: NoSuchBeanDefinitionException) {
            throw NoSuchBeanException("named $name with type of $type", e)
        }
    }
    
    @Api4J
    override fun <T : Any> getOrNull(name: String, type: Class<T>): T? {
        return if (name in this) this[name, type] else null
    }
    
    override fun <T : Any> get(type: KClass<T>): T {
        return try {
            listableBeanFactory.getBean(type.java)
        } catch (e: NoSuchBeanDefinitionException) {
            throw NoSuchBeanException("type of $type", e)
        } catch (e: NoUniqueBeanDefinitionException) {
            throw MultiSameTypeBeanException(type.toString(), e)
        }
    }
    
    @Api4J
    override fun <T : Any> get(type: Class<T>): T {
        return try {
            listableBeanFactory.getBean(type)
        } catch (e: NoSuchBeanDefinitionException) {
            throw NoSuchBeanException("type of $type", e)
        } catch (e: NoUniqueBeanDefinitionException) {
            throw MultiSameTypeBeanException(type.toString(), e)
        }
    }
    
    @Api4J
    override fun <T : Any> getOrNull(type: Class<T>): T? {
        val names = listableBeanFactory.getBeanNamesForType(type)
        return when {
            names.isEmpty() -> null
            names.size == 1 -> this[names[0], type]
            else -> try {
                get(type)
            } catch (e: NoSuchBeanDefinitionException) {
                null
            } catch (e: NoUniqueBeanDefinitionException) {
                throw MultiSameTypeBeanException(type.toString(), e)
            }
        }
    }
    
    @Api4J
    override fun <T : Any> getAll(type: Class<T>?): List<String> {
        return listableBeanFactory.getBeanNamesForType(type).toList()
    }
    
    override fun getOrNull(name: String): Any? {
        return if (name in this) this[name] else null
    }
    
    override fun <T : Any> getOrNull(type: KClass<T>): T? {
        val names = listableBeanFactory.getBeanNamesForType(type.java)
        return when {
            names.isEmpty() -> null
            names.size == 1 -> this[names[0], type]
            else -> try {
                listableBeanFactory.getBean(type.java)
            } catch (e: NoSuchBeanDefinitionException) {
                null
            } catch (e: NoUniqueBeanDefinitionException) {
                throw MultiSameTypeBeanException(type.toString(), e)
            }
        }
    }
    
    override fun <T : Any> getAll(type: KClass<T>?): List<String> {
        return listableBeanFactory.getBeanNamesForType(type?.java).toList()
    }
    
    
    @OptIn(Api4J::class)
    override fun getTypeOrNull(name: String): KClass<*>? = getTypeClassOrNull(name)?.kotlin
    
    @OptIn(Api4J::class)
    override fun getType(name: String): KClass<*> = getTypeClass(name).kotlin
    
    @Api4J
    override fun getTypeClassOrNull(name: String): Class<*>? {
        return try {
            listableBeanFactory.getType(name)?.resolveProxy()
        } catch (e: NoSuchBeanDefinitionException) {
            null
        }
    }
    
    @Api4J
    override fun getTypeClass(name: String): Class<*> {
        return try {
            listableBeanFactory.getType(name)?.resolveProxy() ?: noSuchBeanDefine { name }
        } catch (e: NoSuchBeanDefinitionException) {
            noSuchBeanDefine(e) { name }
        }
    }
    
    
    /**
     * @see AopUtils.isAopProxy
     * @see AopUtils.getTargetClass
     */
    private fun Class<*>.resolveProxy(): Class<*> {
        if (isCgLibProxyClass()) {
            return cglibProxyTargetClass().resolveProxy()
        }
        
        return this
    }
    
    
    private fun Class<*>.isCgLibProxyClass(): Boolean {
        return this.name.contains(ClassUtils.CGLIB_CLASS_SEPARATOR)
    }
    
    private fun Class<*>.cglibProxyTargetClass(): Class<*> {
        return superclass
    }
    
    // private fun Class<*>.isJdkProxy(): Boolean {
    //     return Proxy.isProxyClass(this)
    // }
    //
    // private fun Class<*>.jdkProxyTargetClass(): Class<*> {
    //     // cannot get from jdk proxy instance.
    // }
}
// endregion
