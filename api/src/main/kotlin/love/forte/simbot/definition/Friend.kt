/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.definition

import love.forte.simbot.Bot
import love.forte.simbot.ID


/**
 * 一个 **朋友**。
 */
public interface Friend : User, BotContainer {
    override val id: ID
    override val bot: Bot
    override val username: String

    /**
     * 在Bot眼中，一个朋友可能存在一个备注。
     * TODO 备注修改？
     */
    public val remark: String?



}