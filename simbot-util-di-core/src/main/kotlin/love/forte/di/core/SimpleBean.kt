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

package love.forte.di.core

import love.forte.di.Bean
import kotlin.reflect.KClass

@DslMarker
@Retention(AnnotationRetention.BINARY)
internal annotation class SimpleBeanBuilderDSL

/**
 *
 * 基础的 [Bean] 注册器。
 *
 * @author ForteScarlet
 */
public class SimpleBeanBuilder<T : Any>(
    @Suppress("MemberVisibilityCanBePrivate")
    public val type: KClass<T>
) {

    @SimpleBeanBuilderDSL
    public var isPreferred: Boolean = false

    @SimpleBeanBuilderDSL
    public var isSingleton: Boolean = true


    @SimpleBeanBuilderDSL
    public var priority: Int = 1000



    @SimpleBeanBuilderDSL
    public fun preferred(): SimpleBeanBuilder<T> = also {
        isPreferred = true
    }

    @SimpleBeanBuilderDSL
    public fun singleton(): SimpleBeanBuilder<T> = also {
        isSingleton = true
    }

    @SimpleBeanBuilderDSL
    public var factory: (() -> T)? = null

    @SimpleBeanBuilderDSL
    public fun factory(block: () -> T): SimpleBeanBuilder<T> = also {
        this.factory = block
    }

    public fun build(): Bean<T> = SimpleBean(
        type, isPreferred, isSingleton, priority, factory.ifNull { "Bean's factory function was null" }
    )
}

internal class SimpleBean<T : Any>(
    override val type: KClass<T>,
    override val isPreferred: Boolean,
    override val isSingleton: Boolean = true,
    override val priority: Int,
    private val getter: () -> T,
) : Bean<T> {
    override fun get(): T = getter()
}

