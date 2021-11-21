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

/**
 * 一个账号的 **信息**。
 *
 * 此处仅代表普通的通用信息。
 *
 * @author ForteScarlet
 */
public interface UserInfo {

    /**
     * 这个账号的用户名。
     * 在极端情况下，也许用户不存在用户名。那时，以 [id] 替之。
     */
    public val username: String


}

