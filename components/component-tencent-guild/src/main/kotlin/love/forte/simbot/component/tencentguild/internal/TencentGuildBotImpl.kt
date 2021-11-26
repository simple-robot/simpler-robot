/*
 *  Copyright (c) 2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.component.tencentguild.internal

import io.ktor.client.*
import kotlinx.coroutines.flow.Flow
import love.forte.simbot.*
import love.forte.simbot.component.tencentguild.TencentGuildBot
import love.forte.simbot.component.tencentguild.TencentGuildBotConfiguration
import love.forte.simbot.component.tencentguild.TencentGuildBotID
import love.forte.simbot.component.tencentguild.TencentGuildComponent
import love.forte.simbot.definition.Friend
import love.forte.simbot.definition.Group
import love.forte.simbot.definition.UserStatus
import love.forte.simbot.message.Image
import love.forte.simbot.resources.Resource
import kotlin.coroutines.CoroutineContext

/**
 *
 * @author ForteScarlet
 */
internal class TencentGuildBotImpl(
    configuration: TencentGuildBotConfiguration,
    override val coroutineContext: CoroutineContext,
    override val status: UserStatus,
    override val username: String,
    override val avatar: String,
    override val isStarted: Boolean,
    override val isActive: Boolean,
    override val isCancelled: Boolean,
) : TencentGuildBot() {



    override val client: HttpClient = configuration.client

    override val component: Component
        get() = TencentGuildComponent.component

    override val id: TencentGuildBotID = TencentGuildBotID(configuration.ticket!!)

    override val manager: BotManager<Bot>
        get() = TODO("Not yet implemented")


    override suspend fun friends(grouping: Grouping, limiter: Limiter): Flow<Friend> {
        TODO("Not yet implemented")
    }

    override suspend fun groups(grouping: Grouping, limiter: Limiter): Flow<Group> {
        TODO("Not yet implemented")
    }

    override suspend fun guilds(grouping: Grouping, limiter: Limiter): Flow<Group> {
        TODO("Not yet implemented")
    }

    override suspend fun uploadImage(resource: Resource): Image {
        TODO("Not yet implemented")
    }

    override suspend fun start(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun cancel(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun join() {
        TODO("Not yet implemented")
    }

}