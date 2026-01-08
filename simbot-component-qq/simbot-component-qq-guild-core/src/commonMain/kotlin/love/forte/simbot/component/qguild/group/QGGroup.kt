/*
 * Copyright (c) 2024. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.component.qguild.group

import love.forte.simbot.common.collectable.Collectable
import love.forte.simbot.common.collectable.asCollectable
import love.forte.simbot.common.collectable.emptyCollectable
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.component.qguild.bot.QGBot
import love.forte.simbot.component.qguild.message.QGMedia
import love.forte.simbot.definition.ChatGroup
import love.forte.simbot.definition.Role
import love.forte.simbot.qguild.ExperimentalQGMediaApi
import love.forte.simbot.resource.Resource
import love.forte.simbot.suspendrunner.ST
import love.forte.simbot.suspendrunner.STP


/**
 * 一个群at消息事件中的群信息对象。
 *
 * @author ForteScarlet
 */
public interface QGGroup : ChatGroup {
    /**
     * 这个群的openid。
     */
    override val id: ID

    @STP
    override suspend fun botAsMember(): QGGroupMember

    /**
     * 群消息事件中，只存在群的openid，无法得知群名称，
     * 将始终得到空字符串。
     */
    override val name: String
        get() = ""

    /**
     * 无法查询群成员列表，将始终得到空结果。
     */
    override val members: Collectable<QGGroupMember>
        get() = emptyCollectable()

    /**
     * 无法查询群成员，将始终得到 `null`。
     */
    @ST(blockingBaseName = "getMember", blockingSuffix = "", asyncBaseName = "getMember", reserveBaseName = "getMember")
    override suspend fun member(id: ID): QGGroupMember? = null

    /**
     * 无法得知群主ID，始终得到 `null`
     */
    override val ownerId: ID?
        get() = null

    /**
     * 得到 [QGGroupRole] 的元素集。
     */
    override val roles: Collectable<QGGroupRole>
        get() = QGGroupRole.entries.asCollectable()

    /**
     * 上传一个资源为用于向QQ群发送的 [QGMedia], 可用于后续的发送。
     *
     * 目前上传仅支持使用链接，QQ平台会对此链接进行转存。
     *
     * @param url 目标链接
     * @param type 媒体类型。
     *
     * > 1 图片，2 视频，3 语音，4 文件（暂不开放） 资源格式要求: 图片：png/ jpg，视频：mp4，语音：silk
     *
     * @see QGBot.uploadGroupMedia
     */
    @ST
    public suspend fun uploadMedia(
        url: String,
        type: Int,
    ): QGMedia

    /**
     * 上传一个资源为用于向QQ群发送的 [QGMedia], 可用于后续的发送。
     *
     * 目前上传仅支持使用链接，QQ平台会对此链接进行转存。
     *
     * @param resource 目标资源
     * @param type 媒体类型。
     *
     * > 1 图片，2 视频，3 语音，4 文件（暂不开放） 资源格式要求: 图片：png/ jpg，视频：mp4，语音：silk
     *
     * @see QGBot.uploadGroupMedia
     *
     * @since 4.1.1
     */
    @ST
    @ExperimentalQGMediaApi
    public suspend fun uploadMedia(
        resource: Resource,
        type: Int,
    ): QGMedia
}

/**
 * 一个QQ群的角色，老生常谈的那三个。
 */
public enum class QGGroupRole(override val isAdmin: Boolean) : Role {
    OWNER(true),
    ADMIN(true),
    MEMBER(false);

    override val id: ID
        get() = name.ID
}
