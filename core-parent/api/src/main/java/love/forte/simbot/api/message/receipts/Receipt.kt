/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

package love.forte.simbot.api.message.receipts

import love.forte.common.utils.Carrier


/**
 * **回执**消息接口。
 *
 * @author ForteScarlet <ForteScarlet@163.com>
 * @date 2020/9/4
 * @since
 */
public interface Receipt<T> {
    /**
     * 得到一个回执的载体
     */
    val receipt: Carrier<T>

    // /**
    //  * 如果失败，则可能存在一个异常。
    //  */
    // val failed: Throwable?
    //
    // /**
    //  * 如果 [failed] 存在, 则抛出此异常
    //  */
    // @JvmDefault
    // fun orThrow() {
    //     failed?.run { throw this }
    // }
}