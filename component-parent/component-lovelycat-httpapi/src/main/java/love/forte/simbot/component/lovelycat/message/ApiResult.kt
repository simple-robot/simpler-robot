/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     ApiResult.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

package love.forte.simbot.component.lovelycat.message

import love.forte.simbot.api.message.containers.BotAvatarContainer
import love.forte.simbot.api.message.containers.BotInfo
import love.forte.simbot.api.message.containers.BotNameContainer


// public interface LovelyCatApiResult {
//     val code: Int
//     val result: String
//     val data: String
// }


public data class LovelyCatApiResult(
    val code: Int,
    val result: String,
    val data: String?
)



/** bot name. */
public data class RobotName(
    val name: String
) : BotNameContainer {
    override val botName: String
        get() = name
}


/** bot head url */
public data class RobotHeadImgUrl(
    val headImgUrl: String
) : BotAvatarContainer {
    override val botAvatar: String?
        get() = headImgUrl

}


/** bot head url */
public data class LoggedAccountList(
    val accountList: List<CatBotInfo>
)


/**
 * bot info
 */
public data class CatBotInfo(
    @Deprecated("update_desc: robot_wxid、head_url、wx_hand、这三个属性为兼容老版本出现的，建议更换新属性", ReplaceWith("wxid"))
    val robotWxid: String,
    val wxid: String,
    val wxNum: String,
    val nickname: String,
    @Deprecated("update_desc: robot_wxid、head_url、wx_hand、这三个属性为兼容老版本出现的，建议更换新属性", ReplaceWith("headimgurl"))
    val headUrl: String,
    val headimgurl: String,
    val signature: String,
    val backgroundimgurl: String,
    // "robot_wxid、head_url、wx_hand、这三个属性为兼容老版本出现的，建议更换新属性",
    val update_desc: String,
    val status: Int,
    @Deprecated("update_desc: robot_wxid、head_url、wx_hand、这三个属性为兼容老版本出现的，建议更换新属性", ReplaceWith("wxWindHandle"))
    val wxHand: Int?,
    val wxWindHandle: Int,
    val pid: Int,
    val loginTime: Int
) : BotInfo {
    override val botCode: String
        get() = wxid

    override val botName: String
        get() = nickname

    override val botAvatar: String
        get() = headimgurl
}











