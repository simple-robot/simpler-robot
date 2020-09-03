/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     Result.kt
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

package love.forte.simbot.common.api.messages.results

import love.forte.simbot.common.api.carrier.Carrier
import love.forte.simbot.common.api.sender.Getter

/**
 *
 * **返回值** 。
 *
 * 一般可以代表在 [获取器][Getter] 中所得到的信息的值。
 *
 * @param T 获取器中所得到的真正响应信息。
 *
 * @author ForteScarlet <ForteScarlet@163.com>
 * @date 2020/9/4
 * @since
 */
interface Result<T> {

    /**
     * 得到这个结果
     */
    val result: Carrier<T>

}



interface MultipleResults<T> : Result<Array<T>> {

}