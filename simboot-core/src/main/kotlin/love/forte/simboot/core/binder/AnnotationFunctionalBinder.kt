/*
 * Copyright (c) 2021-2023 ForteScarlet <ForteScarlet@163.com>
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

package love.forte.simboot.core.binder

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
    private val instanceGetter: (ParameterBinderFactory.Context) -> Any?,
    private val caller: (instance: Any?, ParameterBinderFactory.Context) -> ParameterBinderResult,
) : ParameterBinderFactory {
    
    override fun resolveToBinder(context: ParameterBinderFactory.Context): ParameterBinderResult {
        val instance = instanceGetter(context)
        return caller(instance, context)
    }
}


/**
 * 将一个标记了 [Binder] 注解的函数解析转化为 [AnnotationFunctionalBinderFactory] 实例。
 *
 * 此函数的返回值必须为 [ParameterBinderResult] 或者 [ParameterBinder].
 *
 * @param instanceGetter 这个binder执行所需的实例获取函数。
 *
 */
internal fun KFunction<*>.toBinderFactory(instanceGetter: (ParameterBinderFactory.Context) -> Any?): AnnotationFunctionalBinderFactory {
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
                instanceGetter
            ) { _, _ -> call() as ParameterBinderResult }
            
            // contextParameters != null
            instanceParameter0 == null -> AnnotationFunctionalBinderFactory(
                instanceGetter
            ) { _, context -> call(context) as ParameterBinderResult }
            
            // instance not null
            contextParameters == null -> AnnotationFunctionalBinderFactory(instanceGetter) { instance, _ ->
                call(instance) as ParameterBinderResult
            }
            
            // all not null
            else -> AnnotationFunctionalBinderFactory(instanceGetter) { instance, context ->
                callBy(
                    mapOf(instanceParameter0 to instance, contextParameters to context)
                ) as ParameterBinderResult
            }
            
            
        }
        
        // 2 ParameterBinder
        else -> when {
            instanceParameter0 == null && contextParameters == null -> AnnotationFunctionalBinderFactory(
                instanceGetter
            ) { _, _ ->
                val binder = call() as ParameterBinder?
                if (binder == null) ParameterBinderResult.empty()
                else ParameterBinderResult.normal(binder)
            }
            
            // contextParameters not null
            instanceParameter0 == null -> AnnotationFunctionalBinderFactory(
                instanceGetter
            ) { _, context ->
                val binder = call(context) as ParameterBinder?
                if (binder == null) ParameterBinderResult.empty()
                else ParameterBinderResult.normal(binder)
            }
            
            // instanceParameter0 not null
            contextParameters == null ->
                AnnotationFunctionalBinderFactory(
                    instanceGetter
                ) { instance, _ ->
                    val binder = call(instance) as ParameterBinder?
                    if (binder == null) ParameterBinderResult.empty()
                    else ParameterBinderResult.normal(binder)
                }
            
            // all not null
            else ->
                AnnotationFunctionalBinderFactory(
                    instanceGetter
                ) { instance, context ->
                    val binder = callBy(
                        mapOf(instanceParameter0 to instance, contextParameters to context)
                    ) as ParameterBinder?
                    if (binder == null) ParameterBinderResult.empty()
                    else ParameterBinderResult.normal(binder)
                }
        }
    }
    
}

