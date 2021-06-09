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

package love.forte.test

import love.forte.simbot.utils.ResourcePathExpression
import love.forte.simbot.utils.readToProperties
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class ResourcePathExpressionUtilTest {

    @Test
    fun classpathExpression1() {
        val e = "classpath:bots/this.bot"

        val expression = ResourcePathExpression.getInstance(e)

        println(expression)
        println(expression.expression)

        val resource = expression.getResources()[0]

        println(resource.readToProperties())


    }

    @Test
    fun classpathExpression2() {
        val e = "resource:bots/this2.bot"

        val expression = ResourcePathExpression.getInstance(e)

        println(expression)
        println(expression.expression)

        val resource = expression.getResources()[0]

        println(resource.readToProperties())

    }

    @Test
    fun fileExpression1() {
        val file = "file:test.bot"

        val expression = ResourcePathExpression.getInstance(file)

        println(expression)
        println(expression.expression)
        println(expression.type)

        val resource = expression.getResources()[0]

        println(resource.name)

        val prop = resource.readToProperties()

    }

    @Test
    fun mutableFileExpression1() {
        val ex = "file:bot*/**/*.bot"

        val instance = ResourcePathExpression.getInstance(ex)

        instance.getResources().forEach {
            println(it)
            println(it.name)
            println(it.inputStream.use { inp -> inp.reader().readText() })
            println("==============================================")
        }


    }



}