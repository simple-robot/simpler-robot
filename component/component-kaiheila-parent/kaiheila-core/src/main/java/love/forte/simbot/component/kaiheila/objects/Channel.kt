/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

package love.forte.simbot.component.kaiheila.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import love.forte.simbot.api.message.containers.GroupInfo
import love.forte.simbot.component.kaiheila.SerializerModuleRegistrar
import love.forte.simbot.component.kaiheila.api.BaseRespData


/**
 * 开黑啦objects - [频道Channel](https://developer.kaiheila.cn/doc/objects#%E9%A2%91%E9%81%93Channel)
 *
 * #### 示例
 *
 * ```json
 * {
 *     "id": "53002000000000",
 *     "name": "新的频道",
 *     "user_id": "2418239356",
 *     "guild_id": "6016389000000",
 *     "is_category": 0,
 *     "parent_id": "6016400000000000",
 *     "level": null,
 *     "slow_mode": 0,
 *     "topic": "新的频道的说明",
 *     "type": 1,
 *     "permission_overwrites": [
 *         {
 *             "role_id": 0,
 *             "allow": 0,
 *             "deny": 0
 *         }
 *     ],
 *     "permission_users": [],
 *     "permission_sync": 1
 * }
 * ```
 *
 * 频道也属于一种 [群信息][GroupInfo]
 *
 * @author ForteScarlet
 */
public interface Channel : KhlObjects, GroupInfo {

    /** 频道id */
    val id: String

    override val groupCode: String
        get() = "$guildId.$id"

    /** 频道名称 */
    val name: String

    override val groupName: String?
        get() = name

    override val groupAvatar: String? get() = null

    /** 创建者id */
    val userId: String

    /** 服务器id */
    val guildId: String

    /** 频道简介 */
    val topic: String

    /** 是否为分组 */
    val isCategory: Boolean

    /** 上级分组的id */
    val parentId: String

    /** 排序level */
    val level: Int

    /** 慢速模式下限制发言的最短时间间隔, 单位为秒(s) */
    val slowMode: Int

    /** 频道类型: 1 文字频道, 2 语音频道 */
    val type: Int

    /** 针对角色在该频道的权限覆写规则组成的列表 */
    val permissionOverwrites: List<ChannelPermissionOverwrites>

    /** 针对用户在该频道的权限覆写规则组成的列表 */
    val permissionUsers: List<String>

    /** 权限设置是否与分组同步, 1 or 0 */
    val permissionSync: Int

    companion object : SerializerModuleRegistrar {
        override fun SerializersModuleBuilder.serializerModule() {
            // Channel with impl
            polymorphic(Channel::class) {
                subclass(ChannelImpl::class)
                default { ChannelImpl.serializer() }
            }
            // ChannelPermissionOverwrites with impl
            ChannelPermissionOverwrites.apply {
                serializerModule()
            }
        }
    }

    /**
     * [Channel] 的类型，一般出现在事件中。
     */
    @Serializable
    public enum class Type {
        GROUP, PERSON,
    }
}


public inline val Channel.Type.isPrivate: Boolean get() = this == Channel.Type.PERSON
public inline val Channel.Type.isPerson: Boolean get() = isPrivate
public inline val Channel.Type.isGroup: Boolean get() = this == Channel.Type.GROUP



@Serializable
@SerialName(ChannelImpl.SERIAL_NAME)
public data class ChannelImpl(
    override val id: String,
    override val name: String,
    @SerialName("user_id")
    override val userId: String,
    @SerialName("guild_id")
    override val guildId: String,
    override val topic: String,
    @SerialName("is_category")
    override val isCategory: Boolean,
    @SerialName("parent_id")
    override val parentId: String,
    override val level: Int,
    @SerialName("slow_mode")
    override val slowMode: Int,
    override val type: Int,
    @SerialName("permission_overwrites")
    override val permissionOverwrites: List<ChannelPermissionOverwrites>,
    @SerialName("permission_users")
    override val permissionUsers: List<String>,
    @SerialName("permission_sync")
    override val permissionSync: Int,
) : Channel, BaseRespData() {

    override val originalData: String
        get() = toString()

    internal companion object {
        const val SERIAL_NAME = "CHANNEL_I"
    }

}


/**
 * 针对角色在该频道的权限覆写规则组成的列表.
 * ```json
 *     "permission_overwrites": [
 *         {
 *             "role_id": 0,
 *             "allow": 0,
 *             "deny": 0
 *         }
 *     ],
 *
 * ```
 *
 */
public interface ChannelPermissionOverwrites {
    val roleId: Int
    val allow: Int
    val deny: Int

    companion object : SerializerModuleRegistrar {
        override fun SerializersModuleBuilder.serializerModule() {
            polymorphic(ChannelPermissionOverwrites::class) {
                subclass(ChannelPermissionOverwritesImpl::class)
                default { ChannelPermissionOverwritesImpl.serializer() }
            }
        }
    }
}


@Serializable
@SerialName(ChannelPermissionOverwritesImpl.SERIAL_NAME)
public data class ChannelPermissionOverwritesImpl(
    @SerialName("role_id")
    override val roleId: Int,
    override val allow: Int,
    override val deny: Int,
) : ChannelPermissionOverwrites {
    internal companion object {
        const val SERIAL_NAME = "CHANNEL_PERMISSION_OVERWRITES_I"
    }
}
