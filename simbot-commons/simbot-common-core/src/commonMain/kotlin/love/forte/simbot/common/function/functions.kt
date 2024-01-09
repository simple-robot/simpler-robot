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


// fun interface in JS?

/**
 * Represents an action that can be performed on a value of type T.
 *
 * @param T the type of the value that the action operates on
 */
public fun interface Action<in T> {
    /**
     * Invokes the operator function with the given value.
     *
     * @param value the value to be used in the operator function
     */
    public operator fun invoke(value: T)
}

/**
 * 根据提供的 [T] 判断其是否符合某种“条件”。
 */
public fun interface Condition<in T> {
    /**
     * Check the [value].
     */
    public operator fun invoke(value: T): Boolean
}

/**
 * 一个"配置"函数接口。通过 `receiver` 接收配置信息参数。
 *
 * 类似于 `CONF.() -> Unit` 。
 *
 * @author ForteScarlet
 */
public fun interface ConfigurerFunction<in CONF> {
    /**
     * 配置逻辑。
     */
    public operator fun CONF.invoke()
}

/**
 * `CONF.() -> Unit` to [ConfigurerFunction]。
 */
public inline fun <CONF> toConfigurerFunction(crossinline block: CONF.() -> Unit): ConfigurerFunction<CONF> {
    return ConfigurerFunction {
        block()
    }
}

/**
 * Invoke [ConfigurerFunction] with [conf]。
 */
public fun <CONF> ConfigurerFunction<CONF>.invokeWith(conf: CONF) {
    conf.apply { invoke() }
}

/**
 * Invoke [configurer] with [CONF]。
 */
public fun <CONF> CONF.invokeBy(configurer: ConfigurerFunction<CONF>?): CONF {
    return this.also { configurer?.invokeWith(it) }
}

/**
 * Merge double [ConfigurerFunction]
 *
 */
public operator fun <CONF> ConfigurerFunction<CONF>.plus(other: ConfigurerFunction<CONF>): ConfigurerFunction<CONF> {
    val old = this
    return ConfigurerFunction {
        val value = this
        old.invokeWith(value)
        other.invokeWith(value)
    }
}

