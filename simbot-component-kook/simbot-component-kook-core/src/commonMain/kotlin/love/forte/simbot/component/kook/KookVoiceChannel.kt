/*
 *     Copyright (c) 2025-2026. ForteScarlet.
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

import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.suspendrunner.ST

/**
 * 一个 KOOK 中的语音子频道。
 *
 * 语音子频道也是 [KookChatChannel] 的一种，因为它也可以发送消息。
 *
 * @since 4.2.0
 * @author ForteScarlet
 */
public interface KookVoiceChannel : KookChatChannel {
    /**
     * 获取当前语音频道中在线的用户列表。
     */
    public val members: Collectable<KookVoiceMember>

    /**
     * 语音频道之间移动用户
     *
     * 只能在语音频道之间移动，用户也必须在其他语音频道在线才能够移动到目标频道。
     *
     * @throws love.forte.simbot.kook.api.ApiResponseException 如果API的相应结果不是正确结果
     * @throws love.forte.simbot.kook.api.ApiResultException 如果API的相应结果不是正确结果
     */
    @ST
    public suspend fun moveMember(targetChannel: ID, vararg targetMembers: ID) {
        moveMember(targetChannel, targetMembers.asList())
    }

    /**
     * 语音频道之间移动用户
     *
     * 只能在语音频道之间移动，用户也必须在其他语音频道在线才能够移动到目标频道。
     *
     * @throws love.forte.simbot.kook.api.ApiResponseException 如果API的相应结果不是正确结果
     * @throws love.forte.simbot.kook.api.ApiResultException 如果API的相应结果不是正确结果
     */
    @ST
    public suspend fun moveMember(targetChannel: ID, targetMembers: Iterable<ID>)

    /**
     * 踢出语音频道中的用户。
     *
     * @see KookVoiceMember.kickout
     *
     * @throws love.forte.simbot.kook.api.ApiResponseException 如果API的相应结果不是正确结果
     * @throws love.forte.simbot.kook.api.ApiResultException 如果API的相应结果不是正确结果
     */
    @ST
    public suspend fun kickoutMember(targetMember: ID)
}
