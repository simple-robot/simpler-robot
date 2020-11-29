/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     LockStandard.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */
@file:JvmName("LockStandard")

package love.forte.simbot


/**
 * use lock.
 */
public inline fun <T> Lock.use(block: () -> T): T {
    lock()
    return try {
        block()
    } finally {
        unlock()
    }
}


/**
 * 读锁执行
 */
public inline fun <T> ReadWriteLock.read(block: () -> T): T {
    readLock().lock()
    return try {
        block()
    } finally {
        readLock().unlock()
    }
}


/**
 * write lock.
 */
public inline fun <T> ReadWriteLock.write(block: () -> T): T {
    writeLock().lock()
    return try {
        block()
    } finally {
        writeLock().unlock()
    }
}
