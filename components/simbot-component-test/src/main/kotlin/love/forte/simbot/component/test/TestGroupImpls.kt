/*
 *  Copyright (c) 2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

package love.forte.simbot.component.test

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import love.forte.simbot.*
import love.forte.simbot.definition.*
import love.forte.simbot.message.*
import java.util.stream.*
import kotlin.streams.*
import kotlin.time.*


/**
 * 最基础的三个角色类型。
 */
public enum class TestRole(override val id: ID, override val isAdmin: Boolean, override val isOwner: Boolean) : Role {
    /** 拥有者 */
    OWNER(10.ID, true, true),

    /** 管理者 */
    ADMIN(20.ID, true, false),

    /** 普通成员 */
    MEMBER(30.ID, false, false);

}


/**
 *
 * @author ForteScarlet
 */
public class TestGroupImpls(
    override val bot: TestBot,
    override val id: ID,
    override val ownerId: ID,
    override val name: String,
    override val icon: String = TestComponent.DEFAULT_AVATAR,
    override val description: String,
    override val createTime: Timestamp,
    override val maximumMember: Int,
    override val currentMember: Int,
    //
    private val fromGroup: TestGroup,
    private val configuration: TestBotManagerConfiguration
) : TestGroup {
    private val memberGenerator = configuration.generators

    override suspend fun send(message: Message): MessageReceipt {
        configuration.delay()

        return TestMessageReceipt()
    }

    override suspend fun owner(): GroupMember {
        TODO("Not yet implemented")
    }

    @Api4J
    override val owner: GroupMember
        get() = TODO("Not yet implemented")

    override suspend fun members(groupingId: ID?, limiter: Limiter): Flow<GroupMember> {
        TODO("Not yet implemented")
    }

    @Api4J
    override fun getMembers(groupingId: ID?, limiter: Limiter): Stream<out GroupMember> {
        TODO("Not yet implemented")
    }

    override suspend fun member(id: ID): GroupMember? {
        TODO("Not yet implemented")
    }

    override suspend fun mute(duration: Duration): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun unmute(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun previous(): Organization? {
        TODO("Not yet implemented")
    }

    override suspend fun roles(groupingId: ID?, limiter: Limiter): Flow<Role> {
        TODO("Not yet implemented")
    }

    @Api4J
    override fun getRoles(groupingId: ID?, limiter: Limiter): Stream<out Role> {
        TODO("Not yet implemented")
    }
}


public class TestGroupMemberImpl constructor(
    override val id: ID,
    override val bot: TestBot,
    private val roleList: List<Role>,
    override val nickname: String,
    override val joinTime: Timestamp,
    override val username: String,
    override val avatar: String,
    override val status: UserStatus,
    private val fromGroup: TestGroup,
    private val configuration: TestBotManagerConfiguration
) : TestGroupMember {

    @Api4J
    override val roles: Stream<out Role>
        get() = roleList.asSequence().asStream()

    override suspend fun unmute(): Boolean {
        configuration.delay()

        return true
    }

    override suspend fun mute(duration: Duration): Boolean {
        configuration.delay()
        bot.launch {
            delay(duration)
            unmute()
        }
        return true
    }

    override suspend fun roles(): Flow<Role> = roleList.asFlow()

    override suspend fun group(): Group = fromGroup
}