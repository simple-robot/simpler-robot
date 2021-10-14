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
import kotlinx.serialization.Transient
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import love.forte.simbot.api.message.assists.Permissions
import love.forte.simbot.api.message.containers.GroupAccountInfo
import love.forte.simbot.kaiheila.SerializerModuleRegistrar
import love.forte.simbot.kaiheila.api.BaseRespData
import love.forte.simbot.kaiheila.api.logger


/**
 *
 * 开黑啦objects - [用户User](https://developer.kaiheila.cn/doc/objects#%E7%94%A8%E6%88%B7User)
 *
 *
 * 官方示例数据：
 * ```json
 * {
 *     "id": "2418200000",
 *     "username": "tz-un",
 *     "identify_num": "5618",
 *     "online": false,
 *     "avatar": "https://img.kaiheila.cn/avatars/2020-02/xxxx.jpg/icon",
 *     "bot": false,
 *     "mobile_verified": true,
 *     "system": false,
 *     "mobile_prefix": "86",
 *     "mobile": "123****7890",
 *     "invited_count": 33,
 *     "nickname": "12316993",
 *     "roles": [
 *         111,
 *         112
 *     ]
 * }
 * ```
 *
 *
 * @author ForteScarlet
 */
public interface User : KhlObjects, GroupAccountInfo {

    /** 用户的id */
    val id: String

    override val accountCode: String get() = id

    /** 用户名称 */
    val username: String

    override val accountNickname: String get() = username

    /**
     * 用户在当前服务器的昵称
     */
    val nickname: String?

    override val accountRemark: String? get() = nickname

    /**
     * 用户名的认证数字，用户名正常为：user_name#identify_num
     */
    val identifyNum: String

    /**
     * 当前是否在线
     */
    val isOnline: Boolean

    /**
     * 用户是否为机器人
     */
    val isBot: Boolean

    /**
     * 用户的状态, 0代表正常，10代表被封禁
     */
    val status: Int

    /**
     * 用户的头像的url地址
     */
    val avatar: String


    override val accountAvatar: String get() = avatar

    /**
     * vip用户的头像的url地址，可能为gif动图
     */
    val vipAvatar: String?

    /**
     * 是否手机号已验证
     */
    val mobileVerified: Boolean


    /**
     * 用户在当前服务器中的角色 id 组成的列表。
     */
    val roles: List<Int>


    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Not support.")
    override val permission: Permissions
        get() = Permissions.MEMBER

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Not support.")
    override val accountTitle: String?
        get() = null

    companion object : SerializerModuleRegistrar {
        override fun SerializersModuleBuilder.serializerModule() {
            polymorphic(User::class) {
                subclass(SimpleUser::class)
                default { SimpleUser.serializer() }
            }
        }
    }

}


/**
 * [User] 数据类实现。
 */
@Serializable
@SerialName(SimpleUser.SERIAL_NAME)
public data class SimpleUser(
    override val id: String,
    override val username: String,
    /**
     * 用户名的认证数字，用户名正常为：user_name#identify_num
     */
    @SerialName("identify_num")
    override val identifyNum: String,
    @SerialName("online")
    override val isOnline: Boolean,
    /**
     * 用户的状态, 0代表正常，10代表被封禁
     * Allowed: 0┃10
     */
    override val status: Int,
    @SerialName("bot")
    override val isBot: Boolean,
    override val avatar: String,

    // Maybe not exists.

    @SerialName("vip_avatar")
    override val vipAvatar: String? = null,
    override val nickname: String = "",
    override val mobileVerified: Boolean = false,
    override val roles: List<Int> = emptyList(),
) : User, BaseRespData() {
    init {
        if (!(status == 0 || status == 10)) {
            logger.warn("Parameter status must be 0(normal) or 10(banned), but {}", status)
        }
        // check(status == 0 || status == 10) { "Parameter status must be 0(normal) or 10(banned), but $status" }
    }

    override val originalData: String get() = toString()

    internal companion object {
        const val SERIAL_NAME = "USER_I"
    }
}

/**
 *
 * Mention part info.
 *
 * @see toUser
 */
@Suppress("MemberVisibilityCanBePrivate")
@Serializable
public data class MentionPart(
    val id: String,
    val username: String,
    @SerialName("full_name")
    val fullName: String,
    val avatar: String,
) {

    /**
     * 将这个 mention part 转化为 [User] 实例。
     */
    fun toUser(): User = MentionPartUser(
        id = id,
        username = username,
        avatar = avatar,
    ).also { it.originalData = this.toString() }
}


@Serializable
internal data class MentionPartUser(
    override val id: String,
    override val username: String,
    override val avatar: String,
) : User, BaseRespData() {
    @Transient
    override lateinit var originalData: String
        internal set

    override val nickname: String?
        get() = null
    override val identifyNum: String
        get() = ""
    override val isOnline: Boolean
        get() = false
    override val isBot: Boolean
        get() = false
    override val status: Int
        get() = 0
    override val vipAvatar: String?
        get() = null
    override val mobileVerified: Boolean
        get() = false
    override val roles: List<Int>
        get() = emptyList()
}








