/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.definition

import love.forte.simbot.Bot
import love.forte.simbot.Grouping
import love.forte.simbot.ID


/**
 * 一个 **好友**。
 */
public interface Friend : Contact, BotContainer, FriendInfo {
    override val id: ID
    override val bot: Bot

    //region from friend info
    override val remark: String?
    override val grouping: Grouping
    override val username: String
    override val avatar: String
    override val status: UserStatus
    //endregion
}


public interface FriendInfo : UserInfo {
    override val id: ID
    override val username: String
    override val avatar: String

    /**
     * 在Bot眼中，一个好友可能存在一个备注。
     */
    public val remark: String?

    /**
     * 对于Bot，好友可能存在于一个指定的分组中。
     */
    public val grouping: Grouping

}