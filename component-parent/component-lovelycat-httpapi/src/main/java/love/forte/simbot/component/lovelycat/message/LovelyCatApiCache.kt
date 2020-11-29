/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  simple-robot-S
 *  * File     LovelyCatApiCache.kt
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


/**
 * 针对 [love.forte.simbot.component.lovelycat.LovelyCatApiTemplate] 中部分接口的缓存方案。
 */
public interface LovelyCatApiCache {

    /**
     * 获取缓存或计算 [RobotName].
     */
    fun computeBotName(compute: () -> RobotName): RobotName
    /**
     * 获取缓存或计算 [RobotHeadImgUrl].
     */
    fun computeBotHeadImgUrl(compute: () -> RobotHeadImgUrl): RobotHeadImgUrl





}