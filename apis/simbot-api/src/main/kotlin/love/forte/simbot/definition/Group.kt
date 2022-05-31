/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
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
 *
 */

package love.forte.simbot.definition

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.Limiter
import love.forte.simbot.Timestamp
import love.forte.simbot.utils.runInBlocking
import java.util.stream.Stream


/**
 * 一个群。
 * @author ForteScarlet
 */
public interface Group : ChatRoom, GroupInfo {

    override val bot: GroupMemberBot
    override val id: ID
    override val name: String
    override val icon: String
    override val description: String
    override val createTime: Timestamp
    override val ownerId: ID
    override suspend fun owner(): GroupMember

    @Api4J
    override val owner: GroupMember
    override val maximumMember: Int
    override val currentMember: Int

    //region members

    override suspend fun members(groupingId: ID?, limiter: Limiter): Flow<GroupMember>

    @Api4J
    override fun getMembers(groupingId: ID?, limiter: Limiter): Stream<out GroupMember>

    @Api4J
    override fun getMembers(groupingId: ID?): Stream<out GroupMember> = getMembers(groupingId, Limiter)

    @Api4J
    override fun getMembers(limiter: Limiter): Stream<out GroupMember> = getMembers(null, limiter)

    @Api4J
    override fun getMembers(): Stream<out GroupMember> = getMembers(null, Limiter)
    //endregion


    //region member
    override suspend fun member(id: ID): GroupMember?

    @Api4J
    override fun getMember(id: ID): GroupMember? = runInBlocking { member(id) }
    //endregion

    /**
     * 一般来讲，群不存在子集。
     */
    override suspend fun children(groupingId: ID?, limiter: Limiter): Flow<Organization> {
        return emptyFlow()
    }

    @OptIn(Api4J::class)
    override fun getChildren(groupingId: ID?, limiter: Limiter): Stream<Organization> = Stream.empty()
}



/**
 * 群组信息。
 */
public interface GroupInfo : OrganizationInfo
