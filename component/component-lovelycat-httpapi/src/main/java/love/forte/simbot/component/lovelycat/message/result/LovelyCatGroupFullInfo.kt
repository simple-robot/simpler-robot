/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
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

package love.forte.simbot.component.lovelycat.message.result

import love.forte.simbot.api.message.containers.AccountInfo
import love.forte.simbot.api.message.containers.GroupInfo
import love.forte.simbot.api.message.results.GroupAdmin
import love.forte.simbot.api.message.results.GroupFullInfo
import love.forte.simbot.api.message.results.GroupOwner
import love.forte.simbot.component.lovelycat.message.CatGroupInfo


/**
 *
 * @author ForteScarlet
 */
public class LovelyCatGroupFullInfo(
    override val originalData: String,
    private val catGroupInfo: CatGroupInfo,
    override val total: Int
): GroupFullInfo, GroupInfo by catGroupInfo {

    override fun toString(): String = catGroupInfo.toString()


    /**
     * 无法获取群人数上限。但是似乎微信群固定人数为500， 因此使用 `500` 。
     */
    override val maximum: Int
        get() = 500


    /**
     * 无法获取群创建时间。
     */
    override val createTime: Long
        get() = -1

    override val simpleIntroduction: String?
        get() = null
    override val fullIntroduction: String?
        get() = null

    /**
     * 无法定位群主信息。
     */
    override val owner: GroupOwner
        get() = NonGroupOwner

    override val admins: List<GroupAdmin>
        get() = emptyList()
}

private object NonGroupOwner : GroupOwner {
    override val accountInfo: AccountInfo
        get() = NonGroupOwnerAccountInfo
    override fun toString(): String {
        return "NonGroupOwner(Unable to determine the group owner information.)"
    }
}
private object NonGroupOwnerAccountInfo : AccountInfo {
    override val accountCode: String
        get() = ""
    override val accountNickname: String?
        get() = null
    override val accountRemark: String?
        get() = null
    override val accountAvatar: String?
        get() = null

    override fun toString(): String {
        return "NonGroupOwnerAccountInfo(Unable to determine the group owner information.)"
    }
}
