/*
 *  Copyright (c) 2021-2022 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
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