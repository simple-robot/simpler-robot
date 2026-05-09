/*
 *     Copyright (c) 2021-2026. ForteScarlet.
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

package love.forte.simbot.kook.api.thread

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.kook.api.ApiResultType
import love.forte.simbot.kook.objects.SimpleUser
import love.forte.simbot.kook.objects.User

/**
 * 帖子分区详情。
 *
 * @since 4.3.0
 * @author Forte
 */
@Serializable
public data class Category @ApiResultType constructor(
    /** 帖子分区ID */
    public val id: String,
    /** 分区名 */
    public val name: String,
    /** 默认为 0,想要设置的允许的权限值 */
    public val allow: Int = 0,
    /** 默认为 0,想要设置的拒绝的权限值 */
    public val deny: Int = 0,
    /** 角色或者用户在该分区下的权限值 */
    public val roles: List<CategoryRole> = emptyList()
)

/**
 * 分区权限角色详情。
 *
 * @since 4.3.0
 * @author Forte
 */
@Serializable
public data class CategoryRole @ApiResultType constructor(
    /** 类型："user" 或 "role" */
    public val type: String,
    /** 角色ID，type为role时有值 */
    @SerialName("role_id")
    public val roleId: Long,
    /** 用户ID，type为user时有值 */
    @SerialName("user_id")
    public val userId: String,
    /** 允许权限值 */
    public val allow: Int
)

/**
 * 帖子详情。
 *
 * @since 4.3.0
 * @author Forte
 */
@Serializable
public data class Thread @ApiResultType constructor(
    /** 帖子 id */
    public val id: String,
    /** 帖子状态, `1`代表审核中，`2`代表审核通过, `3`代表编辑审核中 */
    public val status: Int,
    /** 标题 */
    public val title: String,
    /** 封面url */
    public val cover: String,
    /** 分区id */
    public val category: String
)

/**
 * 帖子的完整信息，包含主楼信息和扩展字段。
 *
 * @since 4.3.0
 * @author Forte
 */
@Serializable
public data class ThreadView @ApiResultType constructor(
    /** 帖子 id */
    public val id: String,
    /** 帖子状态, `1`代表审核中，`2`代表审核通过, `3`代表编辑审核中 */
    public val status: Int,
    /** 标题 */
    public val title: String,
    /** 封面url */
    public val cover: String,
    /** 主楼id */
    @SerialName("post_id")
    public val postId: String,
    /** 卡片消息附加图片数组 */
    public val medias: List<ThreadMedia> = emptyList(),
    /** 预览文本 */
    @SerialName("preview_content")
    public val previewContent: String,
    /** 创建帖子用户数据 */
    public val user: SimpleUser,
    /** 分区字段 */
    public val category: Category,
    /** 话题数组 */
    public val tags: List<ThreadTag> = emptyList(),
    /** 卡片消息内容 */
    public val content: String,
    /** `@特定用户` 的用户 ID 数组 */
    public val mention: List<String> = emptyList(),
    /** 是否含有 `@全体人员` */
    @SerialName("mention_all")
    public val mentionAll: Boolean,
    /** 是否含有 `@在线人员` */
    @SerialName("mention_here")
    public val mentionHere: Boolean,
    /** `@特定用户` 详情 */
    @SerialName("mention_part")
    public val mentionPart: List<String> = emptyList(),
    /** `@特定角色` 详情 */
    @SerialName("mention_role_part")
    public val mentionRolePart: List<String> = emptyList(),
    /** 频道详情 */
    @SerialName("channel_part")
    public val channelPart: List<String> = emptyList(),
    /** 物品详情 */
    @SerialName("item_part")
    public val itemPart: List<String> = emptyList(),
    /** 最后活跃时间 */
    @SerialName("latest_active_time")
    public val latestActiveTime: Long? = null,
    /** 创建时间 */
    @SerialName("create_time")
    public val createTime: Long? = null,
    /** 是否被编辑过 */
    @SerialName("is_updated")
    public val isUpdated: Boolean? = null,
    /** 内容是否被删除 */
    @SerialName("content_deleted")
    public val contentDeleted: Boolean? = null,
    /** 删除类型：1作者自己删除 2管理员删除 3审核删除 */
    @SerialName("content_deleted_type")
    public val contentDeletedType: Int? = null,
    /** 收藏数量 */
    @SerialName("collect_num")
    public val collectNum: Int? = null,
    /** 回复总数 */
    @SerialName("post_count")
    public val postCount: Int? = null
)

/**
 * 帖子媒体附件。
 *
 * @since 4.3.0
 * @author Forte
 */
@Serializable
public data class ThreadMedia @ApiResultType constructor(
    /** 媒体类型 */
    public val type: Int,
    /** 媒体URL */
    public val src: String,
    /** 媒体标题 */
    public val title: String
)

/**
 * 帖子标签。
 *
 * @since 4.3.0
 * @author Forte
 */
@Serializable
public data class ThreadTag @ApiResultType constructor(
    /** 标签ID */
    public val id: Long,
    /** 标签名称 */
    public val name: String,
    /** 标签图标 */
    public val icon: String
)

/**
 * 帖子回复/评论详情。
 *
 * @since 4.3.0
 * @author Forte
 */
@Serializable
public data class Post @ApiResultType constructor(
    /** 评论/回复 id */
    public val id: String,
    /** 分区id */
    @SerialName("category_id")
    public val categoryId: String? = null,
    /** 所属帖子id */
    @SerialName("thread_id")
    public val threadId: String,
    /** 回复对象的id（回复主贴为0） */
    @SerialName("reply_id")
    public val replyId: String,
    /** 所属的评论的post_id */
    @SerialName("belong_to_post_id")
    public val belongToPostId: String,
    /** 卡片消息 */
    public val content: String,
    /** 回复状态, `1`代表审核中，`2`代表审核通过, `3`代表编辑审核中 */
    public val status: Int? = null,
    /** `@特定用户` 的用户 ID 数组 */
    public val mention: List<String> = emptyList(),
    /** 是否含有 `@全体人员` */
    @SerialName("mention_all")
    public val mentionAll: Boolean = false,
    /** 是否含有 `@在线人员` */
    @SerialName("mention_here")
    public val mentionHere: Boolean = false,
    /** `@特定用户` 详情 */
    @SerialName("mention_part")
    public val mentionPart: List<String> = emptyList(),
    /** `@特定角色` 详情 */
    @SerialName("mention_role_part")
    public val mentionRolePart: List<String> = emptyList(),
    /** 频道详情 */
    @SerialName("channel_part")
    public val channelPart: List<String> = emptyList(),
    /** 物品详情 */
    @SerialName("item_part")
    public val itemPart: List<String> = emptyList(),
    /** 创建时间 */
    @SerialName("create_time")
    public val createTime: Long? = null,
    /** 是否被编辑过 */
    @SerialName("is_updated")
    public val isUpdated: Boolean? = null,
    /** 创建回复用户数据 */
    public val user: User? = null,
    /** 这条回复的下的楼中楼 */
    public val replies: List<Post> = emptyList()
)
