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

package love.forte.di

import javax.inject.Provider
import kotlin.reflect.KClass
import kotlin.reflect.cast


/**
 *
 * 一个存于 [BeanManager] 中的 [Bean].
 *
 * [Bean] 是对一个依赖的定义, 他们最终都能够通过 [get] 来得到一个最终实例，并且不可为null。
 *
 *
 * @author ForteScarlet
 */
public interface Bean<T : Any> : Provider<T>, BeanDescription {

    /**
     * 是否首选的.
     * 当通过类型获取时，会尝试优先选择 [isPreferred] == true 的元素。
     *
     * 在bean管理器中，对应类型能够得到的所有结果中应当至多存在一个 [isPreferred] == true 的结果。
     */
    public val isPreferred: Boolean

    /**
     * 优先级。当通过类型获取的时候，存在多个结果但是没有任何 [isPreferred] == true 的内容的时候，尝试获取一个唯一且最大值的结果。
     */
    public val priority: Int get() = 1000

    /**
     * 此Bean是否为单例。如果是单例，则在调用一次 [get] 之后应由管理器保存其实例。
     */
    public val isSingleton: Boolean

    /**
     * 这个 [Bean] 的实际类型。
     */
    override val type: KClass<T>

    /**
     * 得到这个依赖的结果值。
     */
    override fun get(): T
}


public inline fun <T : Any> Bean<*>.getWithCast(type: () -> KClass<T>): T = type().cast(get())


/** [Bean]'s [value][Bean.get] */
public inline
val <T : Any> Bean<T>.value: T
    get() = get()


/**
 * 代理一个 [Bean] 并对他的返回值进行处理。
 */
public fun <T : Any> Bean<T>.postValue(block: (source: Bean<T>, value: T) -> T): Bean<T> = PostValueBean(block, this)


private class PostValueBean<T : Any>(private val processor: (Bean<T>, T) -> T, private val delegate: Bean<T>) :
    Bean<T> by delegate {
    override fun get(): T {
        return processor(delegate, delegate.get())
    }
}


@Suppress("UNCHECKED_CAST")
public fun <T : Any> T.asBean(isPreferred: Boolean = false, type: KClass<T> = this::class as KClass<T>): Bean<out T> =
    InstanceBean(this, isPreferred, type)


private class InstanceBean<T : Any>(
    private val instance: T,
    override val isPreferred: Boolean,
    override val type: KClass<T>
) : Bean<T> {
    override val isSingleton: Boolean get() = true

    override fun get(): T = instance
}


