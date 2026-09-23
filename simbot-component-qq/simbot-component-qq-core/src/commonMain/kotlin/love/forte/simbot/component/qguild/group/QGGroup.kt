/*
 *     Copyright (c) 2024-2026. ForteScarlet.
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
import love.forte.simbot.qguild.common.QGInternalInheritanceApi
import love.forte.simbot.qguild.event.GroupMessageAuthorRole
import love.forte.simbot.qguild.model.group.GroupBotState
import love.forte.simbot.qguild.model.group.GroupInfo
import love.forte.simbot.resource.Resource
import love.forte.simbot.suspendrunner.ST
import love.forte.simbot.suspendrunner.STP


/**
 * 一个QQ群。
 *
 * @author ForteScarlet
 */
@SubclassOptInRequired(QGInternalInheritanceApi::class)
public interface QGGroup : ChatGroup {
    /**
     * 这个群的openid。
     */
    override val id: ID

    /**
     * 查询机器人在本群中的状态，并作为成员对象返回。
     * 成员 [QGGroupMember.id] 使用群成员 OpenID，而不是 Bot 的 AppID。
     *
     * @throws love.forte.simbot.qguild.QQGuildApiException 可能在 API 调用过程中产生的异常
     */
    @STP
    override suspend fun botAsMember(): QGGroupBotMember

    /**
     * 群名称。
     *
     * 默认只有群 OpenID，无法得知名称，因此返回空字符串。
     * 可以通过 [includeInfo] 得到带有更多群基础信息的 [QGGroupWithInfo]。
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
    public suspend fun uploadMedia(url: String, type: Int): QGMedia

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
    public suspend fun uploadMedia(resource: Resource, type: Int): QGMedia


    /**
     * 得到当前群的入群申请列表的[收集器][Collectable]。收集时才请求，并按平台游标自动翻页。
     *
     * @since 5.0
     */
    public val joinRequests: Collectable<QGGroupJoinRequest>

    /**
     * 通过 API 查询当前 QQ 群的基本信息，返回本次查询的快照结果。
     *
     * @since 5.0
     */
    @ST
    public suspend fun groupInfo(): GroupInfo

    /**
     * 通过 API 查询机器人在当前 QQ 群内的原始状态快照。
     * 查询失败时透传平台或网络异常，不缓存查询结果。
     *
     * @since 5.0
     */
    @ST
    public suspend fun botState(): GroupBotState

    /**
     * 通过 API 查询群基本信息并基于此得到存在更详细信息的 [QGGroupWithInfo]。
     *
     * @throws love.forte.simbot.qguild.QQGuildApiException API 调用过程中可能产生的异常
     * @throws IllegalStateException 如果响应中的群 OpenID 与当前群 ID 不一致
     *
     * @since 5.0
     */
    @ST
    public suspend fun includeInfo(): QGGroupWithInfo

    /**
     * 如果当前对象已经是 [QGGroupWithInfo]，返回其已有的 [GroupInfo]；
     * 否则调用 [groupInfo] 查询。
     *
     * API 调用本身不会存在缓存，多次调用会多次请求。
     *
     * @throws love.forte.simbot.qguild.QQGuildApiException API 调用过程中可能产生的异常
     *
     * @since 5.0
     */
    @ST
    public suspend fun groupInfoOrQuery(): GroupInfo =
        (this as? QGGroupWithInfo)?.groupInfo ?: groupInfo()

    /**
     * 如果当前对象已经是 [QGGroupWithInfo]，返回自身；否则调用 [includeInfo] 查询。
     *
     * API 调用本身不会存在缓存，多次调用会多次请求。
     *
     * @throws love.forte.simbot.qguild.QQGuildApiException API 调用过程中可能产生的异常
     *
     * @since 5.0
     */
    @ST
    public suspend fun includeInfoOrSelf(): QGGroupWithInfo =
        this as? QGGroupWithInfo ?: includeInfo()
}

/**
 * 带有[群基本信息][groupInfo]的 [QGGroup]。
 *
 * @since 5.0
 */
@OptIn(QGInternalInheritanceApi::class)
@SubclassOptInRequired(QGInternalInheritanceApi::class)
public interface QGGroupWithInfo : QGGroup {
    /**
     * 群基本信息原始快照。
     */
    public val groupInfo: GroupInfo

    /**
     * 查询时的群名称。
     */
    override val name: String
        get() = groupInfo.groupName

    /**
     * 群简介。
     */
    public val groupFingerMemo: String
        get() = groupInfo.groupFingerMemo

    /**
     * 群分类。
     */
    public val groupClassText: String
        get() = groupInfo.groupClassText

    /**
     * 群标签列表。
     */
    public val groupTags: List<String>
        get() = groupInfo.groupTags

    /**
     * 群成员人数。
     */
    public val groupMemberNum: Int
        get() = groupInfo.groupMemberNum
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

/**
 * 将平台的群角色统一映射为 core 的群角色。
 *
 * 普通消息作者与机器人群内状态共用这一映射。
 */
internal fun GroupMessageAuthorRole.toQGGroupRole(): QGGroupRole = when (this) {
    GroupMessageAuthorRole.OWNER -> QGGroupRole.OWNER
    GroupMessageAuthorRole.ADMIN -> QGGroupRole.ADMIN
    GroupMessageAuthorRole.MEMBER -> QGGroupRole.MEMBER
}
