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
 * [Something] 是对人们的 [组织][Organization] 或一个具体的 [用户][User] 的统称。
 *
 * 不论 [组织][Organization] 还是 [用户][User]，它们均来自一个 [Bot].
 *
 * @author ForteScarlet
 */
public sealed interface Something {

    /**
     * 这个东西所对应的唯一ID。
     *
     * @see ID
     */
    public val id: ID

    /**
     * 当前 [Something] 来自的bot。
     */
    public val bot: Bot

}