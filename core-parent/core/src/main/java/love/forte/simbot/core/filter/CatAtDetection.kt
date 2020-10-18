/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     CatAtDetection.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.filter

import love.forte.catcode.CatCodeUtil
import love.forte.simbot.core.api.message.events.MsgGet

/**
 *
 * 以CatCode实现的at检测。也是默认会注册的一个检测。
 *
 * @author ForteScarlet
 */
public object CatAtDetectionFactory : AtDetectionFactory {
    /**
     * 根据一个msg实例构建一个 [AtDetection] 函数。
     * 在manager中，如果此方法返回了一个 null 则视为获取失败，会去尝试使用其他 factory 直至成功。
     *
     */
    override fun getAtDetection(msg: MsgGet): AtDetection = AtDetection {
        // 如果消息为null，直接返回false
        val text: String = msg.msg ?: return@AtDetection false

        val botCode: String = msg.botInfo.botCode
        // 使用catCode检测。
        CatCodeUtil.contains(text, "at", "code", botCode)
    }
}