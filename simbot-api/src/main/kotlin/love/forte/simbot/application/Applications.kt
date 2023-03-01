/*
 * Copyright (c) 2022-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

@file:JvmName("Applications")

package love.forte.simbot.application

import love.forte.plugin.suspendtrans.annotation.JvmAsync
import love.forte.plugin.suspendtrans.annotation.JvmBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.utils.runInNoScopeBlocking
import java.util.function.BiConsumer
import java.util.function.Consumer


/**
 * 构建并启用一个 [Application].
 *
 * e.g.
 *
 * 不进行配置:
 * ```kotlin
 * val app = createSimbotApplication(Tar)
 * app.join() // suspend join
 * ```
 *
 * 配置builder:
 * ```kotlin
 * val app = createSimbotApplication(Foo) {
 *      // build...
 * }
 * app.join() // suspend join
 * ```
 *
 * 配置config和builder:
 * ```kotlin
 * val app = createSimbotApplication(Bar, {
 *      // config...
 * }) {
 *      // build..
 * }
 * app.join()  // suspend join
 * ```
 *
 */
@JvmSynthetic
public suspend fun <Config : ApplicationConfiguration, Builder : ApplicationBuilder<A>, A : Application> createSimbotApplication(
    factory: ApplicationFactory<Config, Builder, A>,
    configurator: Config.() -> Unit = {},
    builder: suspend Builder.(Config) -> Unit = {},
): A {
    return factory.create(configurator, builder)
}

/**
 * 构建并启用一个 [Application].
 *
 * [createSimbotApplication]  的非挂起兼容API, 并使用Java友好的函数接口代替Kotlin函数类型。
 *
 * @see createSimbotApplication
 */
@JvmOverloads
@JvmName("createSimbotApplication")
public fun <Config : ApplicationConfiguration, Builder : ApplicationBuilder<A>, A : Application> createSimbotApplication4J(
    factory: ApplicationFactory<Config, Builder, A>,
    configurator: Consumer<Config>? = null,
    builder: BiConsumer<Builder, Config>? = null,
): A {
    return runInNoScopeBlocking {
        createSimbotApplication(factory, { configurator?.accept(this) }) { config ->
            builder?.accept(this, config)
        }
    }
}


/**
 * 构建一个 [ApplicationLauncher].
 *
 * e.g.
 *
 * 不进行配置:
 * ```kotlin
 * val launcher: ApplicationLauncher<TarApplication> = simbotApplication(Tar)
 * val app: TarApplication = launcher.launch() // suspend launch
 * app.join() // suspend join
 * ```
 *
 * 配置builder:
 * ```kotlin
 * val launcher = createSimbotApplication(Foo) {
 *      // build...
 * }
 * val app = launcher.launch() // suspend launch
 * app.join() // suspend join
 * ```
 *
 * 配置config和builder:
 * ```kotlin
 * val launcher createSimbotApplication(Bar, {
 *      // config...
 * }) {
 *      // build..
 * }
 * val app = launcher.launch() // suspend launch
 * app.join()  // suspend join
 * ```
 *
 */
@JvmSynthetic
public fun <Config : ApplicationConfiguration, Builder : ApplicationBuilder<A>, A : Application> simbotApplication(
    factory: ApplicationFactory<Config, Builder, A>,
    configurator: Config.() -> Unit = {},
    builder: suspend Builder.(Config) -> Unit = {},
): ApplicationLauncher<A> {
    return applicationLauncher { factory.create(configurator, builder) }
}


/**
 * 构建一个 [ApplicationLauncher].
 *
 * [simbotApplication] 的非挂起兼容API, 并使用Java友好的函数接口代替Kotlin函数类型。
 *
 * @see simbotApplication
 *
 */
@JvmOverloads
@JvmName("simbotApplication")
public fun <Config : ApplicationConfiguration, Builder : ApplicationBuilder<A>, A : Application> simbotApplication4J(
    factory: ApplicationFactory<Config, Builder, A>,
    configurator: Consumer<Config>? = null,
    builder: BiConsumer<Builder, Config>? = null,
): ApplicationLauncher<A> {
    return applicationLauncher {
        factory.create({ configurator?.accept(this) }) { config ->
            builder?.accept(this, config)
        }
    }
}


/**
 * 通过 [ApplicationDslBuilder] 来使用DSL风格或链式调用风格构建目标 [Application][A].
 *
 * 此api与 [createSimbotApplication] 的区别在于 [buildSimbotApplication] 将 `config` 和 `builder` 函数进行了简单的拆分，
 * 使得 [buildSimbotApplication] **相对于** [createSimbotApplication] 来讲更适合在 `config` 和 `builder`
 * 中都存在大量需要配置的内容的情况。
 *
 * e.g.
 * ```kotlin
 * val application = buildSimbotApplication(Foo) {
 *    config { /* ... */ }
 *    build { /* ... */ }
 * }
 *
 * // ...
 * ```
 *
 */
@JvmSynthetic
@ApplicationDslBuilderDsl
public suspend fun <Config : ApplicationConfiguration, Builder : ApplicationBuilder<A>, A : Application> buildSimbotApplication(
    factory: ApplicationFactory<Config, Builder, A>,
    block: ApplicationDslBuilder<Config, Builder, A>.() -> Unit = {},
): A {
    return createApplicationDslBuilder(factory).also(block).create()
}

/**
 * 通过 [ApplicationDslBuilder] 来使用DSL风格或链式调用风格构建目标 [ApplicationLauncher]<[A]>.
 *
 * 此api与 [simbotApplication] 的区别在于 [buildSimbotApplicationLauncher] 将 `config` 和 `builder` 函数进行了简单的拆分，
 * 使得 [buildSimbotApplicationLauncher] **相对于** [simbotApplication] 来讲更适合在 `config` 和 `builder`
 * 中都存在大量需要配置的内容的情况。
 *
 * e.g.
 * ```kotlin
 * val launcher = buildSimbotApplicationLauncher(Foo) {
 *     config { /* ... */ }
 *     build { /* ... */ }
 * }
 *
 * // ...
 *
 * ```
 *
 */
@JvmSynthetic
@ApplicationDslBuilderDsl
public fun <Config : ApplicationConfiguration, Builder : ApplicationBuilder<A>, A : Application> buildSimbotApplicationLauncher(
    factory: ApplicationFactory<Config, Builder, A>,
    block: ApplicationDslBuilder<Config, Builder, A>.() -> Unit = {},
): ApplicationLauncher<A> {
    val builder = createApplicationDslBuilder(factory).also(block)
    return applicationLauncher { builder.create() }
}


/**
 * 构建一个 [ApplicationDslBuilder] 来使用DSL风格或链式调用风格构建目标 [Application][A].
 *
 * 此api与 [createSimbotApplication] 的区别在于 [buildSimbotApplication] 将 `config` 和 `builder` 函数进行了简单的拆分，
 * 使得 [buildSimbotApplication] **相对于** [createSimbotApplication] 来讲更适合在 `config` 和 `builder`
 * 中都存在大量需要配置的内容的情况，或者相对于 [createSimbotApplication] 更适合Java用户来使用。
 *
 * 但是从Java的友好度上来讲，[factory][ApplicationFactory] 的实现是否针对Java用户有所考虑才是最主要的因素。
 * 例如在simbot中， 最基础的 [ApplicationFactory] 实现 [love.forte.simbot.core.application.Simple]
 * 就**不会** 过多考虑Java API的适配 ————
 * 因为在 [love.forte.simbot.core.application.Simple] （也就是simbot-core）模块中，绝大多数api无论如何优化，
 * 其友好程度都会不如 [love.forte.simboot.core.application.Boot] （也就是 simboot-core）模块 或者 SpringBoot 模块。
 *
 *
 *
 */
@JvmName("buildSimbotApplication")
public fun <Config : ApplicationConfiguration, Builder : ApplicationBuilder<A>, A : Application> createApplicationDslBuilder(
    factory: ApplicationFactory<Config, Builder, A>,
): ApplicationDslBuilder<Config, Builder, A> {
    return ApplicationDslBuilderImpl(factory)
}


@DslMarker
@Retention(AnnotationRetention.BINARY)
public annotation class ApplicationDslBuilderDsl

/**
 * 在 [buildSimbotApplication] 中提供允许通过DSL风格或链式调用风格来构建一个目标 [Application][A].
 *
 * e.g.
 * ```kotlin
 * val app = buildSimbotApplication(Foo) {
 *     config {
 *          // ...
 *     }
 *     build {
 *          // ...
 *     }
 * }
 * app.join() // suspend join
 * ```
 */
public interface ApplicationDslBuilder<Config : ApplicationConfiguration, Builder : ApplicationBuilder<A>, A : Application> {

    /**
     * 提供配置函数。
     *
     * ```kotlin
     * config { // this: Config
     *  propertyA = ...
     *  propertyB = ...
     *  // ...
     * }
     * ```
     *
     */
    @ApplicationDslBuilderDsl
    public fun config(config: ConfigFunction<Config>): ApplicationDslBuilder<Config, Builder, A>

    /**
     * 提供构建函数。
     *
     * ```kotlin
     * build { // this: Builder, it: Config
     *  // ...
     * }
     * ```
     */
    @ApplicationDslBuilderDsl
    @JvmSynthetic
    public fun build(builder: suspend Builder.(Config) -> Unit): ApplicationDslBuilder<Config, Builder, A>

    /**
     * 提供构建函数。
     *
     * ```java
     * builder.build((builder0, config) -> {});
     * ```
     */
    @Api4J
    public fun build(builder: BiConsumer<Builder, Config>): ApplicationDslBuilder<Config, Builder, A> {
        return build { c ->
            builder.accept(this, c)
        }
    }


    /**
     * 根据配置的函数构建目标 [Application][A].
     */
    @JvmBlocking
    @JvmAsync
    public suspend fun create(): A


    /**
     * 用于 [build] 兼容 Kotlin 和 Java.
     */
    public fun interface ConfigFunction<T> {
        public operator fun T.invoke()
    }
}


private class ApplicationDslBuilderImpl<Config : ApplicationConfiguration, Builder : ApplicationBuilder<A>, A : Application>(
    private val factory: ApplicationFactory<Config, Builder, A>,
) : ApplicationDslBuilder<Config, Builder, A> {
    private val configs = mutableListOf<Config.() -> Unit>()
    private val builders = mutableListOf<suspend Builder.(Config) -> Unit>()

    override fun config(config: ApplicationDslBuilder.ConfigFunction<Config>): ApplicationDslBuilder<Config, Builder, A> =
        also {
            configs.add { config.apply { invoke() } }
        }

    override fun build(builder: suspend Builder.(Config) -> Unit): ApplicationDslBuilder<Config, Builder, A> =
        also {
            builders.add(builder)
        }

    override suspend fun create(): A = createSimbotApplication(
        factory = factory,
        { configs.forEach { conf -> this.conf() } }
    ) { c ->
        builders.forEach { builder -> this.builder(c) }
    }

}






