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

package love.forte.test.listener

import kotlinx.coroutines.delay
import love.forte.common.ioc.annotation.Beans
import love.forte.simbot.annotation.Async
import love.forte.simbot.annotation.OnPrivate


/**
 * 挂起对话示例
 * @author ForteScarlet
 */
@Beans
@OnPrivate
class SuspendFunctionListener {

    @Async
    suspend fun test1() {
        delay(100)
        println("Test1!")
    }

    @Async
    suspend fun test2() {
        delay(100)
        println("Test2!")
    }

    suspend fun test3() {
        delay(100)
        println("Test3!")
    }

    suspend fun test4() {
        delay(100)
        println("Test4!")
    }

    suspend fun test5() {
        delay(100)
        println("Test5!")
    }

    @Async
    suspend fun test6() {
        delay(100)
        println("Test6!")
    }

    @Async
    suspend fun test7() {
        delay(100)
        println("Test7!")
    }

}

