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

import love.forte.simbot.ID

/**
 *
 * 一个 **人**。所有与人有关或者说类似于人的，都是 [人][People].
 *
 * 比如说，一个账号，他可能会对应一个人，一个机器人，也类似于一个人——一个虚拟的人。
 *
 * @author ForteScarlet
 */
public interface People {
    /**
     * 这个人所对应的唯一ID。
     *
     * @see ID
     */
    public val id: ID
}