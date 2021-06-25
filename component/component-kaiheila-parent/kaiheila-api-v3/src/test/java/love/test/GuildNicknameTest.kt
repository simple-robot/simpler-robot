/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simpler-robot
 *  * File     GuildNicknameTest.kt
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

import kotlinx.coroutines.runBlocking
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class GuildNicknameTest {


    @Test
    fun nicknameTest() = runBlocking {
        val guildList = GuildApiTest().guildList()


    }

}