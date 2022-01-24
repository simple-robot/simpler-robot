/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot

/**
 * 标记一个方法是提供给 Java 而方便使用的，对于Kotlin应存在其他更优的代替方法。
 */
@RequiresOptIn("为Java提供的阻塞API，不建议Kotlin使用", level = RequiresOptIn.Level.WARNING)
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
public annotation class Api4J


/**
 * 标记一个方法
 */
@RequiresOptIn("实验性的simbotAPI，可能会随时变更", level = RequiresOptIn.Level.WARNING)
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
public annotation class ExperimentalSimbotApi


/**
 * 表示被标记的Api是simbot内部api。
 */
@RequiresOptIn(message = "内部API，其可用性不会被保证，也不会有任何变更通知。", level = RequiresOptIn.Level.WARNING)
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.TYPEALIAS
)
@MustBeDocumented
public annotation class InternalSimbotApi


/**
 * 表示可能存在使用限制、严格要求或者存在特殊规则的API，需要仔细阅读说明且谨慎使用。
 */
@RequiresOptIn(message = "可能存在使用限制、严格要求或者存在特殊规则的API，需要仔细阅读说明且谨慎使用。", level = RequiresOptIn.Level.WARNING)
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.TYPEALIAS
)
@MustBeDocumented
public annotation class SimbotDiscreetApi


/**
 * 标记那些能够监听，但是不建议监听的事件类型，
 * 常见于一些携带泛型的事件类型。
 *
 * @see love.forte.simbot.event.Event
 */
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.FIELD,
    AnnotationTarget.LOCAL_VARIABLE,
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.CONSTRUCTOR,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.TYPEALIAS
)
@MustBeDocumented
public annotation class NotSuggestedEvent // todo?