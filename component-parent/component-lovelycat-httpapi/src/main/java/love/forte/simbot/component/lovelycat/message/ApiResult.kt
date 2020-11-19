/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     ApiResult.kt
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

package love.forte.simbot.component.lovelycat.message


public interface LovelyCatApiResult {
    val code: Int
    val result: String
}


/** bot name. */
public data class RobotNameResult(
    override val code: Int,
    override val result: String,
    val data: String
) : LovelyCatApiResult


/** bot head url */
public data class RobotHeadImgUrl(
    override val code: Int,
    override val result: String,
    val data: String
) : LovelyCatApiResult










