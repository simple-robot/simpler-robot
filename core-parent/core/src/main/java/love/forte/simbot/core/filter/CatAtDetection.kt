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
import love.forte.simbot.api.message.events.MessageEventGet
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.filter.AlwaysRefuseAtDetection
import love.forte.simbot.filter.AtDetection
import love.forte.simbot.filter.AtDetectionFactory

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
    override fun getAtDetection(msg: MsgGet): AtDetection {
        // 不属于一个消息类型事件，则获取不到at信息。
        if (msg !is MessageEventGet) {
            return AlwaysRefuseAtDetection
        }
        // 如果消息为null，直接返回false
        if (msg.isEmptyMsg()) {
            return AlwaysRefuseAtDetection
        }
        val text: String = msg.msg ?: return AlwaysRefuseAtDetection

        val botCode: String = msg.botInfo.botCode
        // 使用catCode检测。
        // CatCodeUtil.contains(text, "at", "code", botCode)
        return CatAtDetection(text, botCode)
    }
}

private data class CatAtDetection(private val text: String, private val botCode: String) : AtDetection {
    override fun atBot(): Boolean = CatCodeUtil.contains(text, "at", "code", botCode)

    override fun atAll(): Boolean =
        CatCodeUtil.contains(text, "at", "all", "true")
                || CatCodeUtil.contains(text, "at", "code", "all")

    override fun atAny(): Boolean  = CatCodeUtil.contains(text, "at")

    override fun at(codes: Array<String>): Boolean {
        return codes.all { atCode ->
            CatCodeUtil.contains(text, "at", "code", atCode)
        }
    }
}

    // /**
    //  * 根据一个msg实例构建一个 [AtDetection] 函数。
    //  * 在manager中，如果此方法返回了一个 null 则视为获取失败，会去尝试使用其他 factory 直至成功。
    //  *
    //  */
    // override fun getAtDetection(msg: MsgGet): AtDetection = AtDetection {
    //     // 不属于一个消息类型事件，则获取不到at信息。
    //     if (msg !is MessageEventGet) {
    //         return@AtDetection false
    //     }
    //     // 如果消息为null，直接返回false
    //     if (msg.isEmptyMsg()) {
    //         return@AtDetection false
    //     }
    //     val text: String = msg.msg ?: return@AtDetection false
    //
    //     val botCode: String = msg.botInfo.botCode
    //     // 使用catCode检测。
    //     CatCodeUtil.contains(text, "at", "code", botCode)
    // }