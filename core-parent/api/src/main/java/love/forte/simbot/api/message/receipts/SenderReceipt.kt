/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     SenderReceipt.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.api.message.receipts

import love.forte.common.utils.Carrier


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
 */
public interface SenderReceipt<T> : Receipt<T>


/**
 * 群消息回执。
 *
 * 其中 [receipt] 以 [字符串][String] 作为载体，应代表发出去的消息的ID
 *
 * @see GroupMsgReceiptData
 */
public interface GroupMsgReceipt : SenderReceipt<String>


public data class GroupMsgReceiptData
// @JvmOverloads
constructor(
    override val receipt: Carrier<String>,
    // override val failed: Throwable? = null
) : GroupMsgReceipt

/**
 * 私信消息回执。
 *
 * 其中 [receipt] 以 [字符串][String] 作为载体，应代表发出去的消息的ID
 *
 * @see PrivateMsgReceiptData
 */
public interface PrivateMsgReceipt : SenderReceipt<String>


public data class PrivateMsgReceiptData
// @JvmOverloads
constructor(
    override val receipt: Carrier<String>,
    // override val failed: Throwable? = null
) : PrivateMsgReceipt

/**
 * 公告发布回执。
 *
 * 其中 [receipt] 以 [布尔值][Boolean] 作为载体，应代表其是否发布成功。
 *
 * @see GroupNoticeReceiptData
 */
public interface GroupNoticeReceipt : SenderReceipt<Boolean>


public data class GroupNoticeReceiptData
// @JvmOverloads
constructor(
    override val receipt: Carrier<Boolean>,
    // override val failed: Throwable? = null
) : GroupNoticeReceipt

/**
 * 群签到回执。
 *
 * 其中 [receipt] 以 [布尔值][Boolean] 作为载体，应代表其是否签到成功。
 *
 * @see GroupNoticeReceiptData
 */
public interface GroupSignReceipt : SenderReceipt<Boolean>


public data class GroupSignReceiptData
// @JvmOverloads
constructor(
    override val receipt: Carrier<Boolean>,
    // override val failed: Throwable? = null
) : GroupSignReceipt