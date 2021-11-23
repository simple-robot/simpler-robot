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

package love.forte.simbot

import love.forte.simbot.definition.User


/**
 *
 * 一个 [Bot]. 同时, [Bot] 也属于一个用户 [User]。
 *
 * @author ForteScarlet
 */
public interface Bot : User {
    /**
     * 每个bot都肯定会由一个 [BotManager] 进行管理。
     *
     */
    public val manager: BotManager<Bot>

    /**
     * 每个Bot都有一个所属组件。
     *
     */
    public val component: Component

    // other..?

}