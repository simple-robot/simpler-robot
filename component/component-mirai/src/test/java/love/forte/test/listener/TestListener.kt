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

import love.forte.simbot.annotation.OnGroup
import love.forte.simbot.annotation.OnPrivate
import love.forte.simbot.filter.AtDetection

/**
 * @author ForteScarlet
 */
// @Beans
class TestListener {


    @OnGroup
    fun a(at: AtDetection){
        println("A Gro: ${at.atBot()}")
    }

    @OnPrivate
    fun b(at: AtDetection){
        println("B Pri: ${at.atBot()}")
    }


}

