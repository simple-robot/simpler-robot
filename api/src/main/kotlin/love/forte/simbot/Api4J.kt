package love.forte.simbot

/**
 * 标记一个方法是提供给 Java 而方便使用的，对于Kotlin应存在其他更优的代替方法。
 */
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
@Retention(AnnotationRetention.BINARY)
@MustBeDocumented
public annotation class Api4J
