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

import love.forte.simbot.Bot
import love.forte.simbot.Component
import love.forte.simbot.ID
import love.forte.simbot.component.tencentguild.TencentGuildBot
import love.forte.simbot.component.tencentguild.TencentGuildBotManager
import love.forte.simbot.component.tencentguild.TencentGuildBotManagerConfiguration
import love.forte.simbot.component.tencentguild.TencentGuildComponent

/**
 *
 * @author ForteScarlet
 */
internal class TencentGuildBotManagerImpl(
    internal val configuration: TencentGuildBotManagerConfiguration
) : TencentGuildBotManager() {

    override val component: Component
        get() = TencentGuildComponent.component


    override suspend fun doCancel() {
        TODO("Not yet implemented")
    }

    override fun get(id: ID): TencentGuildBot? {
        TODO("Not yet implemented")
    }

    override suspend fun register(properties: Map<String, String>): Bot {
        TODO("Not yet implemented")
    }
}