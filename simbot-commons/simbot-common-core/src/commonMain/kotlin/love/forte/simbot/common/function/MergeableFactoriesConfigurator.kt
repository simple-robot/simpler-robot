/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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

package love.forte.simbot.common.function

/**
 * 对一组 [MergeableFactory] 进行聚合组装的配置器。
 *
 * @author ForteScarlet
 */
public open class MergeableFactoriesConfigurator<CONTEXT : Any, V : Any, K : MergeableFactory.Key>(
    configurators: Map<K, ConfigurerFunction<Any>>,
    factories: Map<K, (CONTEXT) -> V>,
) {
    private val configurators = configurators.toMutableMap()
    private val factories = factories.toMutableMap()

    /**
     * Configurer fun type for [MergeableFactoriesConfigurator.add].
     */
    public fun interface Configurer<in CONF, in CONTEXT> {
        /**
         * invoker.
         */
        public operator fun CONF.invoke(context: CONTEXT)
    }

    /**
     * 向当前 [MergeableFactoriesConfigurator] 中添加一个 [factory] 对应的配置信息。
     * 添加的配置逻辑 [configurator] 会与 [factory] 对应的现有逻辑（如果有的话）合并。
     *
     */
    public fun <V1 : V, CONF : Any> add(
        factory: MergeableFactory<K, V1, CONF, CONTEXT>,
        configurator: ConfigurerFunction<CONF>
    ) {
        val key = factory.key
        val newConfig = newConfigurator(key, configurators, configurator)
        configurators[key] = newConfig

        if (key in factories) return

        factories[key] = { context ->
            val configurator0 = configurators[key]!!
            factory.create(context) {
                val conf = this
                configurator0.apply { conf.invoke() }
            }
        }
    }


    /**
     * 提供 [factory] 需求的配置信息 [CONF] 并构建出 [V1]。
     * 如果当前 [MergeableFactoriesConfigurator] 中从未配置过 [factory],
     * 则得到 null。
     *
     * @param context 配置所需上下文
     */
    public fun <K1 : K, V1 : V, CONF : Any> createOrNull(
        factory: MergeableFactory<K1, V1, CONF, CONTEXT>,
        context: CONTEXT
    ): V1? {
        val configurator = configurators[factory.key] ?: return null
        return factory.create(context) {
            configurator.apply { invoke() }
        }
    }

    /**
     * 提供 [factory] 需求的配置信息 [CONF] 并构建出 [V]。
     * 如果当前 [MergeableFactoriesConfigurator] 中从未配置过 [factory],
     * 则向 [MergeableFactory.create] 传递空逻辑（即使用默认配置形式）。
     *
     * @param context 配置所需上下文
     */
    public fun <K1 : K, V1 : V, CONF : Any> create(
        context: CONTEXT,
        factory: MergeableFactory<K1, V1, CONF, CONTEXT>
    ): V1 {
        val configurator = configurators[factory.key]
            ?: return factory.create(context)

        return factory.create(context) {
            val conf = this
            configurator.apply {
                conf.invoke()
            }
        }
    }

    /**
     * 根据当前已存的所有配置信息构建出所有的 [V] 。
     */
    public fun createAll(context: CONTEXT): List<V> {
        return factories.values.map { it.invoke(context) }
    }

    private fun <CONFIG : Any> newConfigurator(
        key: K,
        configurations: Map<K, ConfigurerFunction<Any>>,
        configurator: ConfigurerFunction<CONFIG>
    ): ConfigurerFunction<Any> {
        val oldConfig = configurations[key]

        @Suppress("UNCHECKED_CAST")
        return if (oldConfig != null) {
            ConfigurerFunction {
                val conf = this as CONFIG
                oldConfig.apply { conf.invoke() }
                configurator.apply { conf.invoke() }
            }
        } else {
            ConfigurerFunction {
                this as CONFIG
                configurator.apply { invoke() }
            }
        }
    }
}

/**
 * 一个可应用于 [MergeableFactoriesConfigurator] 的工厂。
 */
public interface MergeableFactory<out K : MergeableFactory.Key, out V : Any, CONF : Any, CTX : Any> {
    /**
     * 用于 [MergeableFactory] 在内部整合时的标识类型。
     * [Key] 的实现应用于 [MergeableFactory.key]。
     * [Key] 会被作为一个用于区分 [MergeableFactory] 的 `key` 使用，
     * 并可能会应用于诸如 HashMap 的键上。
     *
     * 因此，在 Kotlin 中，[Key] 的实现推荐为一个 `object` 类型
     * （例如 [MergeableFactory] 实现对应的伴生对象）。
     * 在 JVM 或其他实现中，[Key] 的实现至少应保证其实例唯一，
     * 或 [hashCode] 与 [equals] 直接具有正常的关联性。
     */
    public interface Key

    /**
     * 工厂函数的标识。
     * [key] 应当是一个针对当前类型的 [MergeableFactory] 的 **常量** 实例，
     * 比如一个 `Kotlin object`。
     *
     */
    public val key: K


    /**
     * 提供配置逻辑函数，并得到结果 [V] 。
     *
     * @param configurer 配置类的配置逻辑。
     */
    public fun create(context: CTX, configurer: ConfigurerFunction<CONF>): V

    /**
     * 使用默认的配置（没有额外配置逻辑）构建并得到结果 [V] 。
     */
    public fun create(context: CTX): V = create(context) {}
}

