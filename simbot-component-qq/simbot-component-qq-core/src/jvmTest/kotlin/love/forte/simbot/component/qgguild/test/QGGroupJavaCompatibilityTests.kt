/*
 *     Copyright (c) 2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.component.qgguild.test

import love.forte.simbot.component.qguild.group.QGGroup
import love.forte.simbot.component.qguild.group.QGGroupBotMember
import love.forte.simbot.component.qguild.group.QGGroupMember
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * 验证生成的 Java 成员查询入口保留旧返回类型描述符。
 */
class QGGroupJavaCompatibilityTests {
    /**
     * 旧入口保留 QGGroupMember，新入口暴露带状态的具体类型。
     */
    @Test
    fun blockingMemberQueryPreservesReturnDescriptor() {
        assertEquals(
            QGGroupMember::class.java,
            QGGroup::class.java.getDeclaredMethod("getBotAsMember").returnType,
        )
        assertEquals(
            QGGroupBotMember::class.java,
            QGGroup::class.java.getDeclaredMethod("getBotAsMemberWithState").returnType,
        )
    }
}
