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

package love.forte.simbot


/**
 *
 * 一个可选接口。
 * 当你使用此接口的时候，则说明你的项目环境中存在 `slf4j-api` 依赖环境。
 *
 * @author ForteScarlet
 */
interface LogAble {
    /**
     * 取得logger。
     */
    val log: org.slf4j.Logger
}