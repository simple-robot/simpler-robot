/*
 * Copyright (c) 2023 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
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

