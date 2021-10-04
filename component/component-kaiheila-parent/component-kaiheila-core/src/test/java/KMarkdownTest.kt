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

import love.forte.simbot.component.kaiheila.objects.buildKMarkdown
import love.forte.simbot.component.kaiheila.objects.preLine
import kotlin.test.Test

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


/**
 *
 * @author ForteScarlet
 */
class KMarkdownTest {

    @Test
    fun test1() {
        val md = buildKMarkdown {
            preLine {
                link(url = "https://bilibili.com")
            }


        }


        println(md)


    }

}