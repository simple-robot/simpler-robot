package love.test

import love.forte.simbot.component.kaiheila.`object`.PermissionType
import love.forte.simbot.component.kaiheila.`object`.combine


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