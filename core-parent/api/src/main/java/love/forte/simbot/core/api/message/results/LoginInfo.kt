/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     LoginInfo.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.api.message.results

import love.forte.simbot.core.api.message.containers.BotInfo


/**
 *
 * bot的基础登录信息，以及一个可能存在的 **等级** 信息。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@Deprecated("May be useless.")
public interface LoginInfo: BotInfo {
    // /**
    //  * 等级信息。如果无法获取，则默认值为-1
    //  */
    // val level: Int
    //
    // /**
    //  * 用于展示一个等级信息的。
    //  * 例如当level不支持获取的时候，返回一个 "无法获取"。
    //  */
    // fun showLevel(): String
}