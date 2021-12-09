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

package love.forte.simboot.annotation


/**
 * 标记一个函数为监听器/监听函数，并自动检测其监听类型。
 *
 * 当标记在一个kotlin扩展函数上的时候，此函数的 receiver 应当是你想要监听的事件类型，eg：
 * ```kotlin
 *
 * @Listener
 * suspend fun ChannelMessageEvent.myListener() { ... }
 *
 * ```
 * 默认情况下，此函数的ID为其全限定名，你可以通过 [Listener.id] 指定一个ID。
 * 当不指定的时候默认为当前标记对象的全限定二进制名称。
 *
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@MustBeDocumented
public annotation class Listener(val id: String = "")
