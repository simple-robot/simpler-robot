package love.forte.simbot.annotation

import kotlin.reflect.KClass

/**
 * 标记一个注解为可重复的。
 *
 *  也许只有在JVM上才有效
 */
@Target(AnnotationTarget.ANNOTATION_CLASS)
public actual annotation class Repeat actual constructor(actual val value: KClass<out Annotation>)