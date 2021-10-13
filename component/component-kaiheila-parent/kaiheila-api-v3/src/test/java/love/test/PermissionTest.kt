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

package love.test

import love.forte.simbot.kaiheila.objects.PermissionType
import love.forte.simbot.kaiheila.objects.combine


/**
 *
 * @author ForteScarlet
 */
class PermissionTest {

    @org.junit.jupiter.api.Test
    fun test1() {
        val a = (1u or 2u) or 4u
        // println(PermissionType.ADMIN + PermissionType.GUILD_MANAGEMENT)
        val b = combine(PermissionType.ADMIN,
            PermissionType.GUILD_MANAGEMENT,
            PermissionType.VIEW_MANAGEMENT_LOG)

        assert(a == b)

    }


}