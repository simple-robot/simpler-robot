/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.common.services

import kotlin.jvm.JvmStatic
import kotlin.jvm.JvmSynthetic
import kotlin.reflect.KClass


/**
 * 一个用于在多平台中兼容实现类似于 JVM 平台中的 `ServiceLoader` 的简单实现。
 * [Services] 类似一个 _全局性缓存_, 会允许主动注册某些类型的对应构建器。
 *
 */
public object Services {
    /**
     * 添加一个用于获取 [T] 的函数。
     */
    @JvmStatic
    public fun <T : Any> addProvider(type: KClass<T>, providerCreator: () -> T) {
        addProviderInternal(type, providerCreator)
    }

    /**
     * 添加一个用于获取 [T] 的函数。
     */
    @JvmSynthetic
    public inline fun <reified T : Any> addProvider(crossinline providerCreator: () -> T) {
        addProvider(T::class) { providerCreator() }
    }

    /**
     * 清理所有通过 [addProvider] 添加的某类型的 provider 构建器。
     */
    @JvmStatic
    public fun clearProviders(type: KClass<*>) {
        clearProvidersInternal(type)
    }

    /**
     * 清理所有通过 [addProvider] 添加的某类型的 provider 构建器。
     */
    @JvmSynthetic
    public inline fun <reified T : Any> clearProviders() {
        clearProviders(T::class)
    }

    /**
     * 获取所有通过 [addProvider] 注册的 [T] 类型的 provider 实例。
     */
    @JvmStatic
    public fun <T : Any> loadProviders(type: KClass<T>): Sequence<() -> T> = loadProvidersInternal(type)

    /**
     * 获取所有通过 [addProvider] 注册的 [T] 类型的 provider 实例。
     */
    @JvmSynthetic
    public inline fun <reified T : Any> loadProviders(): Sequence<() -> T> = loadProviders(T::class)
}

/**
 * 当满足条件 [condition] 时，添加 [providerCreator]。
 */
public inline fun <T : Any> Services.addProvider(condition: Boolean, type: KClass<T>, crossinline providerCreator: () -> T) {
    if (condition) {
        addProvider(type) { providerCreator() }
    }
}

/**
 * 只有在 **非Jvm** 平台上添加 [providerCreator]。
 */
public inline fun <T : Any> Services.addProviderExceptJvm(type: KClass<T>, crossinline providerCreator: () -> T) {
    addProvider(isJvm, type, providerCreator)
}

@PublishedApi
internal expect val isJvm: Boolean

/**
 * 添加一个用于获取 [T] 的函数。
 */
internal expect fun <T : Any> addProviderInternal(type: KClass<T>, providerCreator: () -> T)

/**
 * 清理所有通过 [addProviderInternal] 添加的某类型的 provider 构建器。
 */
internal expect fun clearProvidersInternal(type: KClass<*>)

/**
 * 获取所有通过 [addProviderInternal] 注册的 [T] 类型的 provider 实例。
 */
internal expect fun <T : Any> loadProvidersInternal(type: KClass<T>): Sequence<() -> T>
