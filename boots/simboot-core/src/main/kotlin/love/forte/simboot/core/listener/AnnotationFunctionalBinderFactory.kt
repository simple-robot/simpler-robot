/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simboot.core.listener

import love.forte.di.BeanContainer
import love.forte.simboot.annotation.Binder
import love.forte.simboot.listener.ParameterBinder
import love.forte.simboot.listener.ParameterBinderFactory
import love.forte.simboot.listener.ParameterBinderResult
import love.forte.simbot.SimbotIllegalStateException
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.extensionReceiverParameter
import kotlin.reflect.full.instanceParameter
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.valueParameters


internal class AnnotationFunctionalBinderFactory(
    private val instanceGetter: (BeanContainer) -> Any?,
    private val caller: (instance: Any?, ParameterBinderFactory.Context) -> ParameterBinderResult
) : ParameterBinderFactory {

    override fun resolveToBinder(context: ParameterBinderFactory.Context): ParameterBinderResult {
        val instance = instanceGetter.invoke(context.beanContainer)
        return caller(instance, context)
    }
}


/**
 * 将一个标记了 [Binder] 注解的函数解析转化为 [AnnotationFunctionalBinderFactory] 实例。
 *
 * 此函数的返回值必须为 [ParameterBinderResult] 或者 [ParameterBinder].
 *
 *
 */
internal fun KFunction<*>.toBinderFactory(beanId: String?): AnnotationFunctionalBinderFactory {
    val classifier = this.returnType.classifier
    if (classifier !is KClass<*>) throw SimbotIllegalStateException("Binder's return type must be clear, and be of type [ParameterBinderResult] or [ParameterBinder], but $classifier")

    val type: Int = when {
        classifier.isSubclassOf(ParameterBinderResult::class) -> 1
        classifier.isSubclassOf(ParameterBinder::class) -> 2
        else -> throw SimbotIllegalStateException("Binder's return type must be clear, and be of type [ParameterBinderResult] or [ParameterBinder], but $classifier")
    }

    val instanceParameter0 = instanceParameter
    val extensionReceiverParameter0 = extensionReceiverParameter
    val valueParameters0 = valueParameters

    val contextParameters: KParameter? = when {
        // both
        extensionReceiverParameter0 != null && valueParameters0.isNotEmpty() -> throw SimbotIllegalStateException("The binder function has and can only have one parameter of type [ParameterBinderFactory.Context]. but receiver: $extensionReceiverParameter0 and value parameters size: ${valueParameters0.size}")

        // nothing
        extensionReceiverParameter0 == null && valueParameters0.isEmpty() -> null // no parameter
        // throw SimbotIllegalStateException("The binder function has and can only have one parameter of type [ParameterBinderFactory.Context]. but parameters was empty.")

        // more values
        extensionReceiverParameter0 == null && valueParameters0.size > 1 -> throw SimbotIllegalStateException("The binder function has and can only have one parameter of type [ParameterBinderFactory.Context]. but parameters was more than 1: ${valueParameters0.size}.")

        extensionReceiverParameter0 != null -> {
            val typeClass = extensionReceiverParameter0.type.classifier
            if (typeClass !is KClass<*>) {
                throw SimbotIllegalStateException("The binder function has and can only have one parameter of type [ParameterBinderFactory.Context]. but type of the receiver was: $typeClass")
            }

            if (!typeClass.isSubclassOf(ParameterBinderFactory.Context::class)) {
                throw SimbotIllegalStateException("The binder function has and can only have one parameter of type [ParameterBinderFactory.Context]. but type of the receiver was: $typeClass")
            }

            extensionReceiverParameter0
        }

        // extensionReceiverParameter0 == null, values size = 1
        else -> {
            val singleParameter = valueParameters0.first()
            val typeClass = singleParameter.type.classifier
            if (typeClass !is KClass<*>) {
                throw SimbotIllegalStateException("The binder function has and can only have one parameter of type [ParameterBinderFactory.Context]. but type of the single parameter was: $typeClass")
            }

            if (!typeClass.isSubclassOf(ParameterBinderFactory.Context::class)) {
                throw SimbotIllegalStateException("The binder function has and can only have one parameter of type [ParameterBinderFactory.Context]. but type of the single parameter was: $typeClass")
            }
            singleParameter
        }
    }




    return when (type) {
        // ParameterBinderResult
        // return type is ParameterBinderResult type.
        1 -> when {
            instanceParameter0 == null && contextParameters == null -> AnnotationFunctionalBinderFactory(
                { null },
                { _, _ -> call() as ParameterBinderResult }
            )
            // contextParameters != null
            instanceParameter0 == null -> AnnotationFunctionalBinderFactory(
                { null },
                { _, context -> call(context) as ParameterBinderResult }
            )
            // instance not null
            contextParameters == null ->
                if (beanId != null) {
                    AnnotationFunctionalBinderFactory({ container -> container[beanId] }, { instance, _ ->
                        call(instance) as ParameterBinderResult
                    })
                } else {
                    val beanType = instanceParameter0.type as KClass<*>
                    AnnotationFunctionalBinderFactory({ container -> container[beanType] }, { instance, _ ->
                        call(instance) as ParameterBinderResult
                    })
                }
            // all not null
            else -> {
                if (beanId != null) {
                    AnnotationFunctionalBinderFactory({ container -> container[beanId] }, { instance, context ->
                        callBy(
                            mapOf(instanceParameter0 to instance, contextParameters to context)
                        ) as ParameterBinderResult
                    })
                } else {
                    val beanType = instanceParameter0.type as KClass<*>
                    AnnotationFunctionalBinderFactory({ container -> container[beanType] }, { instance, context ->
                        callBy(
                            mapOf(instanceParameter0 to instance, contextParameters to context)
                        ) as ParameterBinderResult
                    })
                }
            }
        }

        //2 ParameterBinder
        else -> when {
            instanceParameter0 == null && contextParameters == null -> AnnotationFunctionalBinderFactory(
                { null },
                { _, _ ->
                    val binder = call() as ParameterBinder?
                    if (binder == null) ParameterBinderResult.empty()
                    else ParameterBinderResult.normal(binder)
                }
            )

            // contextParameters not null
            instanceParameter0 == null -> AnnotationFunctionalBinderFactory(
                { null },
                { _, context ->
                    val binder = call(context) as ParameterBinder?
                    if (binder == null) ParameterBinderResult.empty()
                    else ParameterBinderResult.normal(binder)
                }
            )

            // instanceParameter0 not null
            contextParameters == null ->
                if (beanId != null) {
                    AnnotationFunctionalBinderFactory(
                        { container -> container[beanId] },
                        { instance, _ ->
                            val binder = call(instance) as ParameterBinder?
                            if (binder == null) ParameterBinderResult.empty()
                            else ParameterBinderResult.normal(binder)
                        }
                    )
                } else {
                    val beanType = instanceParameter0.type as KClass<*>
                    AnnotationFunctionalBinderFactory(
                        { container -> container[beanType] },
                        { instance, _ ->
                            val binder = call(instance) as ParameterBinder?
                            if (binder == null) ParameterBinderResult.empty()
                            else ParameterBinderResult.normal(binder)
                        }
                    )
                }

            // all not null
            else -> if (beanId != null) {
                AnnotationFunctionalBinderFactory(
                    { container -> container[beanId] },
                    { instance, context ->
                        val binder = callBy(
                            mapOf(instanceParameter0 to instance, contextParameters to context)
                        ) as ParameterBinder?
                        if (binder == null) ParameterBinderResult.empty()
                        else ParameterBinderResult.normal(binder)
                    }
                )
            } else {
                val beanType = instanceParameter0.type.classifier as KClass<*>
                AnnotationFunctionalBinderFactory(
                    { container -> container[beanType] },
                    { instance, context ->
                        val binder = callBy(
                            mapOf(instanceParameter0 to instance, contextParameters to context)
                        ) as ParameterBinder?
                        if (binder == null) ParameterBinderResult.empty()
                        else ParameterBinderResult.normal(binder)
                    }
                )
            }
        }
    }

}

