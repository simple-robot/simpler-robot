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

import kotlinx.coroutines.runBlocking
import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.action.DeleteAction


/**
 * 消息回执，当消息发出去后所得到的回执信息。
 * @author ForteScarlet
 */
public interface MessageReceipt {

    /**
     * 一个消息回执中存在一个ID.
     */
    public val id: ID

    /**
     * 消息是否发送成功。此属性的 `false` 一般代表在排除其他所有的 **异常情况** 下，在正常流程中依然发送失败（例如发送的消息是空的）。
     * 不代表发送中出现了异常，仅代表在过程完全正常的情况下的发送结果。
     *
     * 假若 [isSuccess] 为 `false`, 那么 [id] 可能会是一个空值。
     */
    public val isSuccess: Boolean


    /**
     * 如果此回执单是可删除的, 执行删除。
     *
     * 不会也不应捕获异常。
     *
     * @return 删除成功为true，失败或不可删除均为null。
     */
    @Api4J
    public fun deleteIfSupportBlocking(): Boolean = runBlocking { if (this is DeleteAction) delete() else false }
}


/**
 * 如果此回执单是可删除的, 执行删除。
 *
 * @return 删除成功为true，失败或不可删除均为null。
 */
@JvmSynthetic
public suspend fun MessageReceipt.deleteIfSupport(): Boolean = if (this is DeleteAction) delete() else false
