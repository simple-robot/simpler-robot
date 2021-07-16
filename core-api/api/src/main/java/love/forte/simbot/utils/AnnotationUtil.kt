/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

@file:JvmName("AnnotationUtilForKt")

package love.forte.simbot.utils

import love.forte.common.utils.annotation.AnnotationUtil
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter
import kotlin.reflect.jvm.javaMethod


public inline fun <reified T : Annotation> KAnnotatedElement.getAnnotation(): T? {
    val got: Annotation? = when (this) {
        is KClass<*> -> AnnotationUtil.getAnnotation(this.java, T::class.java)
        is KFunction<*> -> this.javaMethod?.let { m -> AnnotationUtil.getAnnotation(m, T::class.java) }
        is KProperty<*> -> {
            this.javaGetter?.let { getter -> AnnotationUtil.getAnnotation(getter, T::class.java) }
                ?: this.javaField?.let { field -> AnnotationUtil.getAnnotation(field, T::class.java) }
        }

        else -> null
    }

    return (got as? T) ?: run {
        for (annotation in annotations) {
            val kAnnotation = annotation.annotationClass
            val jAnnotation = kAnnotation.java
            if (annotation is T) {
                return@run annotation
            }

            val found = AnnotationUtil.getAnnotation(jAnnotation, T::class.java)
            if (found != null) {
                return@run found
            }
        }
        return@run null
    }

}


public inline fun <reified T : Annotation> KAnnotatedElement.containsAnnotation(): Boolean =
    this.getAnnotation<T>() != null
