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

@file:JvmName("Applications")

package love.forte.simbot.application


/**
 * 构建并启用一个 [Application].
 *
 * e.g.
 * ```kotlin
 * val app = simbotApplication(Tar)
 * app.join() // suspend join
 * ```
 *
 *
 * ```kotlin
 * val app = simbotApplication(Foo) {
 *      // build...
 * }
 * app.join() // suspend join
 * ```
 *
 *
 * ```kotlin
 * val app = simbotApplication(Bar, {
 *      // config...
 * }) {
 *      // build..
 * }
 * app.join()  // suspend join
 * ```
 *
 *
 *
 *
 */
@JvmOverloads
public fun <Config : ApplicationConfiguration, Builder : ApplicationBuilder<A>, A : Application> simbotApplication(
    factory: ApplicationFactory<Config, Builder, A>,
    configurator: Config.() -> Unit = {},
    builder: Builder.(Config) -> Unit = {},
): A {
    return factory.create(configurator, builder)
}

/**
 * 通过 [ApplicationDslBuilder] 来使用DSL风格或链式调用风格构建目标 [Application][A].
 *
 * 此api与 [simbotApplication] 的区别在于 [buildSimbotApplication] 将 `config` 和 `builder` 函数进行了简单的拆分，
 * 使得 [buildSimbotApplication] **相对于** [simbotApplication] 来讲更适合在 `config` 和 `builder`
 * 中都存在大量需要配置的内容的情况，或者相对于 [simbotApplication] 更适合Java用户来使用。
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
@JvmSynthetic
@ApplicationDslBuilderDsl
public fun <Config : ApplicationConfiguration, Builder : ApplicationBuilder<A>, A : Application> buildSimbotApplication(
    factory: ApplicationFactory<Config, Builder, A>,
    block: ApplicationDslBuilder<Config, Builder, A>.() -> Unit = {},
): A {
    return createApplicationDslBuilder(factory).also(block).create()
}


/**
 * 构建一个 [ApplicationDslBuilder] 来使用DSL风格或链式调用风格构建目标 [Application][A].
 *
 * 此api与 [simbotApplication] 的区别在于 [buildSimbotApplication] 将 `config` 和 `builder` 函数进行了简单的拆分，
 * 使得 [buildSimbotApplication] **相对于** [simbotApplication] 来讲更适合在 `config` 和 `builder`
 * 中都存在大量需要配置的内容的情况，或者相对于 [simbotApplication] 更适合Java用户来使用。
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
 * **Kotlin**
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
 *
 *
 * **Java**
 * ```java
 * final FooApplication app = Applications.buildSimbotApplication(Foo.INSTANCE)
 *         .config(configuration -> {
 *             // ...
 *         })
 *         .build((builder, configuration) -> {
 *             // ...
 *         })
 *         .create();
 *
 * app.joinBlocking(); // blocking join
 * ```
 *
 *
 */
public interface ApplicationDslBuilder<Config : ApplicationConfiguration, Builder : ApplicationBuilder<A>, A : Application> {
    
    /**
     * 提供配置函数。
     *
     * **Kotlin**
     * ```kotlin
     * config { // this: Config
     *  propertyA = ...
     *  propertyB = ...
     *  // ...
     * }
     * ```
     *
     * **Java**
     * ```java
     * config((configuration) -> {
     *  configuration.setPropertyA(...);
     *  configuration.setPropertyB(...);
     *  // ...
     * })
     * ```
     *
     */
    @ApplicationDslBuilderDsl
    public fun config(config: ConfigDslFunction<Config>): ApplicationDslBuilder<Config, Builder, A>
    
    /**
     * 提供构建函数。
     *
     * **Kotlin**
     * ```kotlin
     * build { // this: Builder, it: Config
     *  // ...
     * }
     * ```
     *
     * **Java**
     * ```java
     * build((builder, config) -> {
     *  // ...
     * })
     * ```
     *
     */
    @ApplicationDslBuilderDsl
    public fun build(builder: BuildDslFunction<Config, Builder, A>): ApplicationDslBuilder<Config, Builder, A>
    
    
    /**
     * 根据配置的函数构建目标 [Application][A].
     */
    public fun create(): A
    
    
    /**
     * 为 [config] 提供更兼容 Java 的 `Config.() -> Unit` 函数类型。
     */
    public fun interface ConfigDslFunction<Config : ApplicationConfiguration> {
        public operator fun Config.invoke()
    }
    
    
    /**
     * 为 [build] 提供更兼容 Java 的 `Builder.(Config) -> Unit` 函数类型。
     */
    public fun interface BuildDslFunction<Config : ApplicationConfiguration, Builder : ApplicationBuilder<A>, A : Application> {
        public operator fun Builder.invoke(configuration: Config)
    }
}




private class ApplicationDslBuilderImpl<Config : ApplicationConfiguration, Builder : ApplicationBuilder<A>, A : Application>(
    private val factory: ApplicationFactory<Config, Builder, A>,
) : ApplicationDslBuilder<Config, Builder, A> {
    private val configs = mutableListOf<ApplicationDslBuilder.ConfigDslFunction<Config>>()
    private val builders = mutableListOf<ApplicationDslBuilder.BuildDslFunction<Config, Builder, A>>()
    
    override fun config(config: ApplicationDslBuilder.ConfigDslFunction<Config>): ApplicationDslBuilder<Config, Builder, A> =
        also {
            configs.add(config)
        }
    
    override fun build(builder: ApplicationDslBuilder.BuildDslFunction<Config, Builder, A>): ApplicationDslBuilder<Config, Builder, A> =
        also {
            builders.add(builder)
        }
    
    override fun create(): A = simbotApplication(
        factory = factory,
        { configs.forEach { it.run { invoke() } } }
    ) { c ->
        builders.forEach { it.run { invoke(c) } }
    }
    
}






