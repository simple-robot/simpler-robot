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
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import love.forte.simbot.Bot
import love.forte.simbot.ID


/**
 * 一个组织下的成员,
 */
public interface Member : User {

    override val id: ID
    override val bot: Bot

    @JvmSynthetic
    public suspend fun roles(): Flow<Role>
    public val roles: List<Role> get() = runBlocking { roles().toList() }

}