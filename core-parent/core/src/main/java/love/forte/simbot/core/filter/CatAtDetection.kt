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

package love.forte.simbot.core.filter

import love.forte.catcode.Neko
import love.forte.simbot.api.message.events.MessageGet
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.api.message.events.PrivateMsg
import love.forte.simbot.filter.AlwaysAllowedAtDetection
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
        // 不属于一个消息类型事件，无法获取cat特殊码，则获取不到at信息, 则一律放行
        if (msg !is MessageGet) {
            return AlwaysAllowedAtDetection
        }

        // val text: String = msg.msg ?: return AlwaysRefuseAtDetection
        //
        val botCode: String = msg.botInfo.botCode

        // 使用catCode检测。
        return CatAtDetection(msg, botCode)
    }
}

/**
 * neko at 检测。非线程安全。
 */
private data class CatAtDetection(private val msg: MessageGet, private val botCode: String) : AtDetection {

    private val messageContent get() = msg.msgContent

    private lateinit var _cats: List<Neko>
    private val cats: List<Neko>
    get() {
        if (!::_cats.isInitialized) {
            _cats = messageContent.cats
        }
        return _cats
    }


    override fun atBot(): Boolean {
        return msg is PrivateMsg || cats.any { neko -> neko.type == "at" && neko["code"] == botCode }
    }

    override fun atAll(): Boolean {
        return msg is PrivateMsg || cats.any { neko -> neko.type == "at" && (neko["all"] == "true" || neko["code"] == "all" ) }
    }

    override fun atAny(): Boolean {
        return msg is PrivateMsg || cats.any { neko -> neko.type == "at" }
    }

    override fun at(codes: Array<String>): Boolean {
        return codes.all {
            cats.any { neko -> neko.type == "at" && neko["code"] == it }
        }
    }
}