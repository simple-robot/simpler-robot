/*
 *     Copyright (c) 2023-2026. ForteScarlet.
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

package love.forte.simbot.component.kook

import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.ability.DeleteSupport
import love.forte.simbot.ability.StandardDeleteOption
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.kook.bot.KookBot
import love.forte.simbot.definition.Channel
import love.forte.simbot.kook.api.channel.UpdateChannelApi
import love.forte.simbot.suspendrunner.ST
import kotlin.coroutines.CoroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import love.forte.simbot.kook.objects.Channel as KChannel


/**
 * 一个基于 Kook Channel 的类型定义。
 *
 * @see KookChatChannel
 * @see KookCategoryChannel
 *
 * @author ForteScarlet
 */
public interface KookChannel : Channel, DeleteSupport {
    /**
     * Channel 所属 bot
     * @since 4.2.0
     */
    public val bot: KookBot

    override val coroutineContext: CoroutineContext

    override val category: KookCategory?

    /**
     * 得到此实例内对应的 api 模块下的原始 channel 信息。
     *
     * @see KChannel
     */
    public val source: KChannel

    /**
     * 频道ID
     */
    override val id: ID
        get() = source.id.ID

    /**
     * 频道名称
     */
    override val name: String
        get() = source.name

    /**
     * 频道创建者id
     * @since 4.3.0
     */
    public val userId: ID
        get() = source.userId.ID

    /**
     * 频道所属服务器id
     * @since 4.3.0
     */
    public val guildId: ID
        get() = source.guildId.ID

    /**
     * 频道简介
     * @since 4.3.0
     */
    public val topic: String
        get() = source.topic

    /**
     * 上级分组的id
     * @since 4.3.0
     */
    public val parentId: ID
        get() = source.parentId.ID

    /**
     * 频道排序level
     * @since 4.3.0
     */
    public val level: Int
        get() = source.level

    /**
     * 慢速模式下限制发言的最短时间间隔, 单位为秒(s)
     * @since 4.3.0
     * @see slowModeDuration
     */
    public val slowMode: Int
        get() = source.slowMode

    /**
     * 权限设置是否与分组同步, 1 or 0
     * @since 4.3.0
     */
    public val permissionSync: Int
        get() = source.permissionSync

    /**
     * 是否有密码
     * @since 4.3.0
     */
    public val hasPassword: Boolean
        get() = source.hasPassword

    /**
     * 删除此频道。
     *
     * 如果 [options] 中不包括 [StandardDeleteOption.IGNORE_ON_FAILURE],
     * 则当API请求失败时错误会传播地抛出。
     *
     * 注意：[StandardDeleteOption.IGNORE_ON_FAILURE] 会捕获并隐藏任何产生的异常。
     * 因为 KOOK 文档中并未明确指出当"删除目标不存在"时的错误码或错误状态，因此 [StandardDeleteOption.IGNORE_ON_NO_SUCH_TARGET]
     * 暂时无法生效。
     * _如果实际上官方有相关说明或后续更新了相关说明，可以随时通过 ISSUES 或 PR 协助我们完善。_
     *
     *
     * @throws love.forte.simbot.kook.api.ApiResponseException
     * 如果 [options] 中不包括 [IGNORE_ON_FAILURE][StandardDeleteOption.IGNORE_ON_FAILURE],
     * 则当API请求失败时错误会传播地抛出。
     * @see love.forte.simbot.kook.api.channel.DeleteChannelApi
     * @since 4.1.6
     */
    @ST
    override suspend fun delete(vararg options: DeleteOption)

    /**
     * 获取一个频道更新器。
     * 提供需要修改的内容，然后使用 [KookChannelUpdater.execute] 更新频道数据。
     *
     * @since 4.3.0
     */
    public fun updater(): KookChannelUpdater
}

/**
 * 获取频道的慢速模式的持续时间。
 * @since 4.3.0
 */
public val KookChannel.slowModeDuration: Duration
    get() = when (slowMode) {
        0 -> Duration.ZERO
        else -> slowMode.seconds
    }

/**
 * 使用 DSL 直接配置 [KookChannelUpdater.builder] 并更新频道信息。
 * @since 4.3.0
 */
public suspend inline fun KookChannel.update(block: UpdateChannelApi.Builder.() -> Unit): KookChannel =
    updater().apply { builder.block() }.execute()
