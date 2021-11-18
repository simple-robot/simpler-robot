package love.forte.simbot.annotation

import kotlin.reflect.KClass


@Retention(AnnotationRetention.RUNTIME)
@Repeatable
@Repeat(Listens::class)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
public annotation class Listen(val type: KClass<*>) // TODO type: Event

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
public annotation class Listens(val value: Array<Listen>)