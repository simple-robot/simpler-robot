/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simpler-robot
 *  * File     JsonTest1.kt
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

package love.test

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import love.forte.simbot.component.kaiheila.kaiheilaJson
import kotlin.test.Test


@Serializable
object Bean {
    val code = 10
}


/**
 *
 * @author ForteScarlet
 */
class JsonTest1 {

    @Test
    fun test1() {

        println(kaiheilaJson.encodeToString(Bean))


    }









}