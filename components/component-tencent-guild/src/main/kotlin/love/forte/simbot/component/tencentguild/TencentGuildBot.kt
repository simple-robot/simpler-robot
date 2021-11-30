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

package love.forte.simbot.component.tencentguild

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.isActive
import love.forte.simbot.*
import love.forte.simbot.action.NotSupportActionException
import love.forte.simbot.definition.Friend
import love.forte.simbot.definition.Group
import love.forte.simbot.definition.Guild
import love.forte.simbot.definition.UserStatus
import love.forte.simbot.event.EventProcessor
import love.forte.simbot.message.Image
import love.forte.simbot.resources.Resource
import love.forte.simbot.tencentguild.TencentBot
import love.forte.simbot.tencentguild.TencentBotInfo
import kotlin.coroutines.CoroutineContext

/**
 * 一个tencent频道BOT的接口实例。
 * @author ForteScarlet
 */
public abstract class TencentGuildBot : Bot, TencentBot {

    public abstract val sourceBot: TencentBot

    override val botInfo: TencentBotInfo
        get() = sourceBot.botInfo

    override val coroutineContext: CoroutineContext
        get() = sourceBot.coroutineContext

    override val id: ID
        get() = sourceBot.ticket.appId.ID

    override val username: String
        get() = botInfo.username

    override val avatar: String
        get() = botInfo.avatar

    abstract override val manager: TencentGuildBotManager

    abstract override val eventProcessor: EventProcessor

    override val component: Component
        get() = TencentGuildComponent.component

    override val status: UserStatus get() = BotStatus

    abstract override suspend fun friends(grouping: Grouping, limiter: Limiter): Flow<Friend>

    abstract override suspend fun groups(grouping: Grouping, limiter: Limiter): Flow<Group>

    abstract override suspend fun guilds(grouping: Grouping, limiter: Limiter): Flow<Guild>

    override suspend fun uploadImage(resource: Resource): Image {
        // TODO fake remote image.
        throw NotSupportActionException("upload Image")
    }



    override val isActive: Boolean
        get() = sourceBot.isActive
}


private object BotStatus : UserStatus {
    override val isNormal: Boolean
        get() = false
    override val isOfficial: Boolean
        get() = false
    override val isFakeUser: Boolean
        get() = true
    override val isAnonymous: Boolean
        get() = false
    override val isBot: Boolean
        get() = true

}