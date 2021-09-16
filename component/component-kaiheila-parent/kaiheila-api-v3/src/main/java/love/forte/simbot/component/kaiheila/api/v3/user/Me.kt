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

package love.forte.simbot.component.kaiheila.api.v3.user

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.`object`.User
import love.forte.simbot.component.kaiheila.api.*


/**
 * [获取当前用户信息](https://developer.kaiheila.cn/doc/http/user#%E8%8E%B7%E5%8F%96%E5%BD%93%E5%89%8D%E7%94%A8%E6%88%B7%E4%BF%A1%E6%81%AF)
 *
 * @author ForteScarlet
 */
public object MeReq : GetUserApiReq<ObjectResp<Me>>, BaseApiDataKey("user", "me") {
    override val key: ApiData.Req.Key
        get() = this

    override val dataSerializer: DeserializationStrategy<ObjectResp<Me>> = objectResp()

    override fun route(builder: RouteInfoBuilder) {
        builder.apiPath = route
    }

    override val body: Any?
        get() = null
}



@Serializable
public data class Me(
    override val id: String,
    override val username: String,
    @SerialName("identify_num")
    override val identifyNum: String,
    override val online: Boolean,
    override val status: Int,
    override val avatar: String,
    override val vipAvatar: String? = null,
    override val bot: Boolean,
    @SerialName("mobile_verified")
    override val mobileVerified: Boolean,
    override val nickname: String = username,
    override val roles: List<Int> = emptyList(),
) : User {
    override val originalData: String get() = toString()
}
