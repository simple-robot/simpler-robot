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

package love.forte.simbot.quantcat.common.binder

import love.forte.simbot.annotations.ExperimentalSimbotAPI
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.extensionReceiverParameter
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.valueParameters

/**
 * [BinderManager] 的基础实现，提供基本功能。
 */
@ExperimentalSimbotAPI
public class SimpleBinderManager(
    private val globalBinderFactories: List<ParameterBinderFactory> = emptyList(),
    private val idBinderFactories: MutableMap<String, ParameterBinderFactory> = mutableMapOf(),
) : BinderManager {
    override val normalBinderFactorySize: Int
        get() = idBinderFactories.size

    override val globalBinderFactorySize: Int
        get() = globalBinderFactories.size

    override fun get(id: String): ParameterBinderFactory? {
        return idBinderFactories[id]
    }

    override val globals: List<ParameterBinderFactory>
        get() = globalBinderFactories.toList()
}

/**
 * [ParameterBinderFactory] 的简单实现
 */
@ExperimentalSimbotAPI
public class SimpleFunctionalBinderFactory(
    private val instanceGetter: (ParameterBinderFactory.Context) -> Any?,
    private val caller: (instance: Any?, ParameterBinderFactory.Context) -> ParameterBinderResult,
) : ParameterBinderFactory {

    override fun resolveToBinder(context: ParameterBinderFactory.Context): ParameterBinderResult {
        val instance = instanceGetter(context)
        return caller(instance, context)
    }
}

/**
 * 将一个函数解析转化为 [SimpleFunctionalBinderFactory] 实例。
 *
 * 此函数的返回值必须为 [ParameterBinderResult] 或者 [ParameterBinder]，否则将会抛出 [IllegalArgumentException] 异常。
 *
 * @param instanceGetter 这个binder执行所需的实例获取函数。
 * @throws IllegalArgumentException 参数不符合条件时
 */
@ExperimentalSimbotAPI
@Suppress("ThrowsCount")
public fun KFunction<*>.toBinderFactory(
    instanceGetter: (ParameterBinderFactory.Context) -> Any?
): SimpleFunctionalBinderFactory {
    val classifier = this.returnType.classifier
    if (classifier !is KClass<*>) {
        throw IllegalArgumentException(
            "Binder's return type must be clear, and be of type [ParameterBinderResult] or [ParameterBinder], " +
                "but $classifier"
        )
    }

    val type: Int = when {
        classifier.isSubclassOf(ParameterBinderResult::class) -> 1
        classifier.isSubclassOf(ParameterBinder::class) -> 2
        else -> throw IllegalArgumentException(
            "Binder's return type must be clear, and be of type [ParameterBinderResult] or [ParameterBinder], " +
                "but $classifier"
        )
    }

    val instanceParameter0 = instanceParameter
    val extensionReceiverParameter0 = extensionReceiverParameter
    val valueParameters0 = valueParameters

    val contextParameters: KParameter? = when {
        // both
        extensionReceiverParameter0 != null && valueParameters0.isNotEmpty() -> throw IllegalStateException(
            "The binder function has and can only have one parameter of type [ParameterBinderFactory.Context]. " +
                "but receiver: $extensionReceiverParameter0 and value parameters size: ${valueParameters0.size}"
        )

        // nothing
        extensionReceiverParameter0 == null && valueParameters0.isEmpty() -> null // no parameter
        // throw SimbotIllegalStateException("The binder function has and can only have one parameter of type [ParameterBinderFactory.Context]. but parameters was empty.")

        // more values
        extensionReceiverParameter0 == null && valueParameters0.size > 1 -> throw IllegalStateException(
            "The binder function has and can only have one parameter of type [ParameterBinderFactory.Context]. " +
                "but parameters was more than 1: ${valueParameters0.size}."
        )

        extensionReceiverParameter0 != null -> {
            val typeClass = extensionReceiverParameter0.type.classifier
            if (typeClass !is KClass<*>) {
                throw IllegalArgumentException(
                    "The binder function has and can only have one parameter " +
                        "of type [ParameterBinderFactory.Context]. " +
                        "but type of the receiver was: $typeClass"
                )
            }

            if (!typeClass.isSubclassOf(ParameterBinderFactory.Context::class)) {
                throw IllegalArgumentException(
                    "The binder function has and can only have one parameter " +
                        "of type [ParameterBinderFactory.Context]. " +
                        "but type of the receiver was: $typeClass"
                )
            }

            extensionReceiverParameter0
        }

        // extensionReceiverParameter0 == null, values size = 1
        else -> {
            val singleParameter = valueParameters0.first()
            val typeClass = singleParameter.type.classifier
            if (typeClass !is KClass<*>) {
                throw IllegalArgumentException(
                    "The binder function has and can only have one parameter " +
                        "of type [ParameterBinderFactory.Context]. " +
                        "but type of the single parameter was: $typeClass"
                )
            }

            if (!typeClass.isSubclassOf(ParameterBinderFactory.Context::class)) {
                throw IllegalArgumentException(
                    "The binder function has and can only have one parameter " +
                        "of type [ParameterBinderFactory.Context]. " +
                        "but type of the single parameter was: $typeClass"
                )
            }
            singleParameter
        }
    }


    return when (type) {
        // ParameterBinderResult
        // return type is ParameterBinderResult type.
        1 -> when {
            instanceParameter0 == null && contextParameters == null -> SimpleFunctionalBinderFactory(
                instanceGetter
            ) { _, _ -> call() as ParameterBinderResult }

            // contextParameters != null
            instanceParameter0 == null -> SimpleFunctionalBinderFactory(
                instanceGetter
            ) { _, context -> call(context) as ParameterBinderResult }

            // instance not null
            contextParameters == null -> SimpleFunctionalBinderFactory(instanceGetter) { instance, _ ->
                call(instance) as ParameterBinderResult
            }

            // all not null
            else -> SimpleFunctionalBinderFactory(instanceGetter) { instance, context ->
                callBy(
                    mapOf(instanceParameter0 to instance, contextParameters to context)
                ) as ParameterBinderResult
            }


        }

        // 2 ParameterBinder
        else -> when {
            instanceParameter0 == null && contextParameters == null -> SimpleFunctionalBinderFactory(
                instanceGetter
            ) { _, _ ->
                val binder = call() as ParameterBinder?
                if (binder == null) {
                    ParameterBinderResult.empty()
                } else {
                    ParameterBinderResult.normal(binder)
                }
            }

            // contextParameters not null
            instanceParameter0 == null -> SimpleFunctionalBinderFactory(
                instanceGetter
            ) { _, context ->
                val binder = call(context) as ParameterBinder?
                if (binder == null) {
                    ParameterBinderResult.empty()
                } else {
                    ParameterBinderResult.normal(binder)
                }
            }

            // instanceParameter0 not null
            contextParameters == null ->
                SimpleFunctionalBinderFactory(
                    instanceGetter
                ) { instance, _ ->
                    val binder = call(instance) as ParameterBinder?
                    if (binder == null) {
                        ParameterBinderResult.empty()
                    } else {
                        ParameterBinderResult.normal(binder)
                    }
                }

            // all not null
            else ->
                SimpleFunctionalBinderFactory(
                    instanceGetter
                ) { instance, context ->
                    val binder = callBy(
                        mapOf(instanceParameter0 to instance, contextParameters to context)
                    ) as ParameterBinder?
                    if (binder == null) {
                        ParameterBinderResult.empty()
                    } else {
                        ParameterBinderResult.normal(binder)
                    }
                }
        }
    }

}

