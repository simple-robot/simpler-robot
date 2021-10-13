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

package love.forte.simbot.kaiheila.objects

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import love.forte.simbot.api.message.containers.GroupInfo
import love.forte.simbot.api.message.results.SimpleGroupInfo
import love.forte.simbot.component.kaiheila.SerializerModuleRegistrar
import love.forte.simbot.component.kaiheila.api.BaseRespData


/**
 *
 * 开黑啦objects - [服务器Guild](https://developer.kaiheila.cn/doc/objects#%E6%9C%8D%E5%8A%A1%E5%99%A8Guild)
 *
 * 官方数据结构示例：
 * ```json
 * {
 *   "id": "2405000000000",
 *   "name": "工具",
 *   "topic": "",
 *   "master_id": "9200000000",
 *   "icon": "",
 *   "notify_type": 1,
 *   "region": "beijing",
 *   "enable_open": false,
 *   "open_id": "0",
 *   "default_channel_id": "2369000000000",
 *   "welcome_channel_id": "0",
 *   "roles": [
 *       {
 *           "role_id": 109472,
 *           "name": "管理员",
 *           "color": 0,
 *           "position": 1,
 *           "hoist": 0,
 *           "mentionable": 0,
 *           "permissions": 1
 *       }
 *   ],
 *   "channels": [
 *       {
 *           "id": "2369000000000",
 *           "master_id": "9200000000",
 *           "parent_id": "",
 *           "name": "你好",
 *           "type": 1,
 *           "level": 1,
 *           "limit_amount": 0,
 *           "is_category": false
 *       },
 *   ]
 * }
 * ```
 *
 * 服务器信息也属于一种 [群信息][GroupInfo]
 *
 * @author ForteScarlet
 */
public interface Guild : KhlObjects, SimpleGroupInfo {

    /** 服务器id */
    val id: String

    override val groupCode: String get() = id

    /** 服务器名称 */
    val name: String

    override val groupName: String? get() = name


    /** 服务器主题 */
    val topic: String

    /** 服务器主的id */
    val masterId: String

    /** 服务器icon的地址 */
    val icon: String

    override val groupAvatar: String? get() = icon

    /** 通知类型, 0代表默认使用服务器通知设置，1代表接收所有通知, 2代表仅@被提及，3代表不接收通知 */
    val notifyType: Int

    /** 服务器默认使用语音区域 */
    val region: String

    /** 是否为公开服务器 */
    val enableOpen: Boolean

    /** 公开服务器id */
    val openId: String

    /** 默认频道id */
    val defaultChannelId: String

    /** 欢迎频道id */
    val welcomeChannelId: String

    /** 角色列表 */
    val roles: List<Role>

    /** 频道列表 */
    val channels: List<Channel>

    @JvmSynthetic
    suspend fun channels(): List<Channel>

    companion object : SerializerModuleRegistrar {
        override fun SerializersModuleBuilder.serializerModule() {
            polymorphic(Guild::class) {
                subclass(SimpleGuild::class)
                default { SimpleGuild.serializer() }
            }
        }
    }

}




@Serializable
@SerialName(SimpleGuild.SERIAL_NAME)
public data class SimpleGuild(
    override val id: String,
    override val name: String,
    override val topic: String,
    @SerialName("master_id")
    override val masterId: String,
    override val icon: String,
    @SerialName("notify_type")
    override val notifyType: Int,
    override val region: String,
    @SerialName("enable_open")
    override val enableOpen: Boolean,
    @SerialName("open_id")
    override val openId: String,
    @SerialName("default_channel_id")
    override val defaultChannelId: String,
    @SerialName("welcome_channel_id")
    override val welcomeChannelId: String,
    override val roles: List<Role>,
    override val channels: List<Channel>
) : Guild, BaseRespData() {
    override val originalData: String
        get() = toString()

    override suspend fun channels(): List<Channel> = channels

    internal companion object {
        const val SERIAL_NAME = "GUILD_I"
    }
}