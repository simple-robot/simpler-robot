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

package love.forte.simbot.quantcat.common.binder

import love.forte.simbot.annotations.InternalSimbotAPI
import love.forte.simbot.common.PriorityConstant
import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KClassifier
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.javaMethod


/**
 * [ParameterBinder] 的解析工厂，通过提供部分预处理参数来解析得到 [ParameterBinder] 实例。
 */
@OptIn(InternalSimbotAPI::class)
public interface ParameterBinderFactory : BaseParameterBinderFactory<ParameterBinderFactory.Context> {

    /**
     * 工厂优先级.
     */
    override val priority: Int get() = PriorityConstant.DEFAULT

    /**
     * 根据 [Context] 提供的各项参数进行解析与预变异，并得到一个最终的 [ParameterBinder] 到对应的parameter中。
     * 如果返回 [ParameterBinderResult.Empty] ，则视为放弃对目标参数的匹配。
     *
     * 返回值最终会被整合，并按照 [ParameterBinderResult.priority] 的顺序作为此binder的执行顺序。
     *
     * 在监听函数被执行时将会通过解析的 [ParameterBinder] 对参数进行注入，
     * 会依次执行对应的binder取第一个执行成功的.
     *
     */
    override fun resolveToBinder(context: Context): ParameterBinderResult


    /**
     * [ParameterBinderFactory] 进行参数处理时的可用参数内容. 由解析注解监听函数的解析器进行提供。
     */
    public interface Context : BaseParameterBinderFactory.Context {
        /**
         * 目标监听函数所对应的函数体。
         */
        override val source: KFunction<*>

        /**
         * 当前的处理参数目标。
         */
        public val parameter: KParameter

        /**
         * 获取 [parameter] 中的 [type.classifier][KClassifier], 并尝试将其转化为 [Java Class][Class].
         * 如果 [classifier][KClassifier] 不是 [KClass] 类型或转化失败，则得到null。
         *
         * _Tips: 如果希望Java中将 [Kotlin Class][KClass] 转化为 [Java Class][Class], 或许可以使用 `JvmClassMappingKt`_
         *
         * @see parameter
         * @see KClassifier
         */
        public val parameterType: Class<*>?
            get() = (parameter.type.classifier as? KClass<*>)?.java

        /**
         * 获取 [source] 并尝试将其转化为 [Java Method][Method]. 无法转化的情况下得到null。
         *
         * _Tips: 如果希望Java中将 [Kotlin Function][KFunction] 转化为 [Java Method][Method], 或许可以使用 `ReflectJvmMapping`_
         *
         * @see source
         * @see KFunction.javaMethod
         */
        public val sourceMethod: Method?
            get() = source.javaMethod
    }
}


/**
 * [ParameterBinderFactory] 的容器，允许通过 ID 获取对应Binder。
 */
public interface ParameterBinderFactoryContainer {
    /**
     * 通过ID尝试获取对应 [ParameterBinderFactory] 实例。
     */
    public operator fun get(id: String): ParameterBinderFactory?

    /**
     * 获取所有的全局binder。
     */
    public fun getGlobals(): List<ParameterBinderFactory>

    /**
     * 将一个 [function] 解析为 [ParameterBinderFactory].
     *
     * 此 function必须遵循规则：
     * - 返回值类型必须是 [ParameterBinder] 或 [ParameterBinderResult] 类型。
     * - 参数或则receiver有且只能有一个，且类型必须是 [ParameterBinderFactory.Context]
     */
    public fun resolveFunctionToBinderFactory(beanId: String? = null, function: KFunction<*>): ParameterBinderFactory
}
