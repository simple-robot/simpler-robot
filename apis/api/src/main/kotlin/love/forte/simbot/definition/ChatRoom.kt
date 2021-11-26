/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.definition

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.Limiter
import love.forte.simbot.action.MessageSendSupport
import java.util.stream.Stream


/**
 * 一个 **聊天室**。 聊天室是组织的一个子集，代表其是一个存在多人且允许相互交流“发送消息”的组织。
 *
 * 聊天室作为一个联系对象，允许发送消息.
 *
 * @author ForteScarlet
 */
public interface ChatRoom : Organization, MessageSendSupport {

    /**
     * 一般来讲，能够作为聊天室的组织不存在子集。
     */
    override suspend fun children(groupingId: ID?, limiter: Limiter): Flow<Organization> {
        return emptyFlow()
    }

    @Api4J
    override fun getChildren(groupingId: ID?, limiter: Limiter): Stream<Organization> {
        return Stream.empty()
    }
}