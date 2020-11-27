/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     Requestable.kt
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

package love.forte.simbot.api.message.events


/**
 * 可进行 **请求处理** 的。
 *
 * 提供一种新的方式来对请求进行处理，可用于当场进行处理而不是留存处理。
 *
 * 例如好友添加申请等。
 *
 * TODO 尚未整合
 *
 */
public interface Requestable {

    /**
     * 接收当前请求。可以追加一个 [备注][remark] (如果可以的话。)
     */
    fun accept(remark: String?)


    /**
     * 拒绝当前请求。可以追加一个 [备注/原因][remark] (如果可以的话。)
     */
    fun reject(remark: String?)

}



