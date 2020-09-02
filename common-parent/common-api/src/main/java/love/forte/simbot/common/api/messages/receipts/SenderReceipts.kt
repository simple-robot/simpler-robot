/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     SenderReceipts.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

package love.forte.simbot.common.api.messages.receipts

import love.forte.simbot.common.api.carrier.Carrier

/*
 * 送信器回执
 *
 * 回执一般就是 群聊送信回执 私聊送信回执
 *
 * @author ForteScarlet <ForteScarlet@163.com>
 * @date 2020/9/2
 * @since
 */


/**
 * 送信器回执的统一父接口。
 *
 * 一个回执可能是一个可挂起的，也可能是一个没有挂起的。
 *
 * 每一个回执获取
 */
public interface SenderReceipts<T> {
    /**
     * 得到一个回执的载体
     */
    val receipt: Carrier<T>

    /**
     * 如果失败，则可能存在一个异常。
     */
    val failed: Throwable?

    /**
     * 如果 [failed] 存在, 则抛出此异常
     */
    @JvmDefault
    fun orThrow() {
        failed?.run { throw this }
    }
}


/**
 * 群消息回执
 * 其中 [receipt] 以字符串作为载体，代表了发出去的消息的ID
 */
public interface GroupMsgReceipts : SenderReceipts<String>


/**
 * 私信消息回执
 * 其中 [receipt] 以字符串作为载体，代表了发出去的消息的ID
 */
public interface PrivateMsgReceipts : SenderReceipts<String>
