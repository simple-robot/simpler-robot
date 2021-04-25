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

package love.forte.simbot.component.kaiheila.`object`

import love.forte.simbot.api.message.containers.AccountInfo


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
public interface KaiheilaUser : KaiheilaObjects, AccountInfo {

    /** 用户的id */
    val id: String

    /** 用户名称 */
    val username: String

    @JvmDefault
    override val accountNickname: String get() = username

    /**
     * 用户名的认证数字，用户名正常为：user_name#identify_num
     */
    val identifyNum: String

    /**
     * 当前是否在线
     */
    val online: Boolean

    /**
     * 用户的状态, 0代表正常，10代表被封禁
     */
    val status: Int

    /**
     * 用户的头像的url地址
     */
    val avatar: String

    @JvmDefault
    override val accountAvatar: String get() = avatar

    /**
     * 用户是否为机器人
     */
    val bot: Boolean

    /**
     * 是否手机号已验证
     */
    val mobileVerified: Boolean

    /**
     * 是否为官方账号
     */
    val system: Boolean

    /**
     * 手机区号,如中国为86
     */
    val mobilePrefix: String

    /**
     * 用户手机号，带掩码
     */
    val mobile: String

    /**
     * 当前邀请注册的人数
     */
    val invitedCount: Int

    /**
     * 用户在当前服务器的昵称
     */
    val nickname: String

    @JvmDefault
    override val accountRemark: String get() = nickname

    /**
     * 用户在当前服务器中的角色 id 组成的列表
     */
    val roles: List<Int>

}