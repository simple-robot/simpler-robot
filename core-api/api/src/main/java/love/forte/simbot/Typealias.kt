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

package love.forte.simbot

import kotlin.reflect.KProperty


/** 弱引用 */
typealias WeakRef<T> = java.lang.ref.WeakReference<T>
/** 软引用 */
typealias SoftRef<T> = java.lang.ref.SoftReference<T>
/** 虚引用 */
typealias PhantomRef<T> = java.lang.ref.PhantomReference<T>
/** 原子引用 */
typealias AtomicRef<T> = java.util.concurrent.atomic.AtomicReference<T>
/** Lock */
typealias Lock = java.util.concurrent.locks.Lock
/** Read write lock */
typealias ReadWriteLock = java.util.concurrent.locks.ReadWriteLock


public operator fun <R, T> WeakRef<T>.getValue(r: R, property: KProperty<*>): T? = get()
