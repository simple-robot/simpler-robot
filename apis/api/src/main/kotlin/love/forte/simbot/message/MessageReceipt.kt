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

package love.forte.simbot.message

import love.forte.simbot.ID
import love.forte.simbot.action.DeleteSupport


/**
 * 消息回执，当消息发出去后所得到的回执信息。
 * @author ForteScarlet
 */
public interface MessageReceipt {

    /**
     * 一个消息回执中存在一个ID.
     */
    public val id: ID
}


/**
 * 如果此回执单是可删除的, 执行删除。
 *
 * @return 删除成功为true，失败或不可删除均为null。
 */
public suspend fun MessageReceipt.deleteIfSupport(): Boolean = if (this is DeleteSupport) delete() else false
