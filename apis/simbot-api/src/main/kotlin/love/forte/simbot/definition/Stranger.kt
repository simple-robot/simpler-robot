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
import love.forte.simbot.ID


/**
 * 陌生人。
 * 一个并非好友或群成员的人。
 *
 * 陌生人并不一定就是一个 [联系人][Contact], 无法保证能够与其能够交流。
 *
 * @author ForteScarlet
 */
public interface Stranger : User {
    override val id: ID
    override val bot: Bot

}