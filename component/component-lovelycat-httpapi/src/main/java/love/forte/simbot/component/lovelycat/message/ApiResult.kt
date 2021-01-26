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

import love.forte.simbot.api.message.containers.*
import love.forte.simbot.api.message.results.FriendInfo
import love.forte.simbot.api.message.results.SimpleGroupInfo
import love.forte.simbot.component.lovelycat.message.event.lovelyCatAccountInfo


public interface ApiResult<T> {
    val code: Int
    val result: String?
    val data: T?
}


// public class LovelyCatApiResult : ApiResult<String> {
//     @field:JvmName("code")
//     override var code: Int = -1
//     @field:JvmName("result")
//     override var result: String? = null
//     @field:JvmName("data")
//     override var data: String? = null
//
//     override fun toString(): String {
//         return "LovelyCatApiResult(code=$code, result='$result', data=$data)"
//     }
// }



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
    override val botAvatar: String
        get() = headImgUrl

}


/** bot head url */
public data class LoggedAccountList(
    val accountList: List<CatBotInfo> = emptyList()
)


/**
 * bot info
 */
public data class CatBotInfo(
    @Deprecated("update_desc: robot_wxid、head_url、wx_hand、这三个属性为兼容老版本出现的，建议更换新属性", ReplaceWith("wxid"))
    val robotWxid: String?,
    val wxid: String,
    val wxNum: String?,
    val nickname: String,
    @Deprecated("update_desc: robot_wxid、head_url、wx_hand、这三个属性为兼容老版本出现的，建议更换新属性", ReplaceWith("headimgurl"))
    val headUrl: String?,
    val headimgurl: String,
    val signature: String?,
    val backgroundimgurl: String?,
    // "robot_wxid、head_url、wx_hand、这三个属性为兼容老版本出现的，建议更换新属性",
    val update_desc: String?,
    val status: Int?,
    @Deprecated("update_desc: robot_wxid、head_url、wx_hand、这三个属性为兼容老版本出现的，建议更换新属性", ReplaceWith("wxWindHandle"))
    val wxHand: Int?,
    val wxWindHandle: Int?,
    val pid: Int?,
    val loginTime: Int?
) : BotInfo {
    override val botCode: String
        get() = wxid

    override val botName: String
        get() = nickname

    override val botAvatar: String
        get() = headimgurl
}


public data class CatFriendListResult(
    override val code: Int,
    override val result: String,
    override val data: List<CatFriendInfo> = emptyList()
) : ApiResult<List<CatFriendInfo>>



public data class CatFriendInfo(
    /*
        {
        "Name": "\u4ece\u4e36\u4e00",
        "wxid": "wxid_ji1yqs4v9meu22",
        "Note": "\u4e09\u76df-\u6210\u6653\u5251",
        "Robot_wxid": "wxid_bqy1ezxxkdat22"
    }
     */
    val name: String,
    val wxid: String,
    val note: String?,
    val robotWxid: String
) : FriendInfo, AccountInfo by lovelyCatAccountInfo(wxid, name, note), BotCodeContainer {

    /** 当前的bot的账号 */
    override val botCode: String
        get() = robotWxid

    /**
     * 无法获取好友分组信息。
     */
    override val grouping: String?
        get() = null

    /**
     * 得到原始数据字符串。
     * 数据不应该为null。
     */
    override val originalData: String
        get() = toString()
}





public data class CatGroupListResult(
    override val code: Int,
    override val result: String,
    override val data: List<CatGroupInfo> = emptyList()
) : ApiResult<List<CatGroupInfo>>


public data class CatGroupInfo(
    /*
    {
        "Wxid": "18338181572@chatroom",
        "Name": "\u5185\u90e8-\u5317\u4eac\u6797\u4e1a\u9879\u76ee\u7ec4",
        "Robot_wxid": "wxid_bqy1ezxxkdat22"
    }
     */
    val wxid: String,
    val name: String,
    val robotWxid: String
) : SimpleGroupInfo, BotCodeContainer {
    /** 群号 */
    override val groupCode: String
        get() = wxid

    /** 当前的bot的账号 */
    override val botCode: String
        get() = robotWxid

    /** 没有头像信息 */
    override val groupAvatar: String?
        get() = null

    /**
     * 群名称 可能出现无法获取的情况
     */
    override val groupName: String
        get() = name

    override val originalData: String
        get() = toString()
}


public data class GroupMemberDetailInfoResult(
    override val code: Int,
    override val result: String,
    override val data: CatGroupMemberInfo
) : ApiResult<CatGroupMemberInfo>


public data class CatGroupMemberInfo(
    /*
    {
        "img": "http://wx.qlogo.cn/mmcrhead/iabmAd5EnqEOWbCDANJNWuicPNIaj56KT6mC1wsWMiaSuz5JWslb63ujTZHr5X9a9GLUQmzz5M7icHapsMnibnPwnnYUybywmlmvH/0",
        "Name": "\u5c0f\u5206\u961f",
        "wxid": "11046274610@chatroom",
        "city": "",
        "Robot_wxid": "wxid_bqy1ezxxkdat22"
    }
     */
    val img: String,
    val name: String,
    val wxid: String,
    val city: String?,
    val robotWxid: String
) : GroupAccountInfo, BotCodeContainer {
    /**
     * 昵称。
     */
    override val accountNickname: String
        get() = name

    /** 当前的bot的账号 */
    override val botCode: String
        get() = robotWxid

    /** 好友备注或群名片。可能为null。 */
    override val accountRemark: String?
        get() = null

    /**
     * 得到账号的头像地址. 一般来讲为`null`的可能性很小
     */
    override val accountAvatar: String
        get() = img

    /**
     * 账号
     */
    override val accountCode: String
        get() = wxid

    /**
     * 群用户的头衔。在不支持的情况下将会得到 `null`。
     *
     * 有些情况下，支持获取但是可能会得到空字符串。
     */
    override val accountTitle: String?
        get() = null
}

public data class GroupMemberListResult(
    override val code: Int,
    override val result: String,
    override val data: List<CatSimpleGroupMemberInfo> = emptyList()
) : ApiResult<List<CatSimpleGroupMemberInfo>>


public data class CatSimpleGroupMemberInfo(
    /*
        {
            "Name": "\u674e\u5609\u56fe",
            "wxid": "wxid_tkst1wt4we1622",
            "Robot_wxid": "wxid_bqy1ezxxkdat22"
        }
     */
    val name: String,
    val wxid: String,
    val robotWxid: String

): GroupAccountInfo, BotCodeContainer {
    /**
     * 昵称。
     * 可能会出现为null的情况，但是一般情况下不会。
     */
    override val accountNickname: String
        get() = name

    /** 当前的bot的账号 */
    override val botCode: String
        get() = robotWxid

    /** 好友备注或群名片。可能为null。 */
    override val accountRemark: String?
        get() = null

    /**
     * 得到账号的头像地址. 一般来讲为`null`的可能性很小
     */
    override val accountAvatar: String?
        get() = null

    /**
     * 账号
     */
    override val accountCode: String
        get() = wxid

    /**
     * 群用户的头衔。在不支持的情况下将会得到 `null`。
     *
     * 有些情况下，支持获取但是可能会得到空字符串。
     */
    override val accountTitle: String?
        get() = null
}












