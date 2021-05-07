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

import catcode.Neko
import love.forte.simbot.api.message.events.MessageGet
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.api.message.events.PrivateMsg
import love.forte.simbot.filter.*

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
private data class CatAtDetection(private val msg: MessageGet, private val botCode: String) : CacheableAtDetection() {
    private val messageContent get() = msg.msgContent
    private lateinit var _cats: List<Neko>
    private val cats: List<Neko>
        get() {
            if (!::_cats.isInitialized) {
                _cats = messageContent.cats.filter { n -> n.type == "at" }
            }
            return _cats
        }

    override fun atBotInit(): Boolean? = if (msg is PrivateMsg) true else null
    override fun atAllInit(): Boolean? = if (msg is PrivateMsg) true else null
    override fun atAnyInit(): Boolean? = if (msg is PrivateMsg) true else null

    override fun checkAtBot(): Boolean = cats.any { neko -> neko.type == "at" && neko["code"] == botCode }
    override fun checkAtAll(): Boolean =
        cats.any { neko -> neko.type == "at" && (neko["all"] == "true" || neko["code"] == "all") }

    override fun checkAtAny(): Boolean = cats.any { neko -> neko.type == "at" }

    override fun initCodes(adder: CodeAdder) {
        cats.forEach { n ->
            if (n.type == "at") {
                n["code"]?.let { c -> adder.add(c) }
            }
        }
    }


}
//
// /**
//  * neko at 检测。非线程安全。
//  */
// private data class CatAtDetection(private val msg: MessageGet, private val botCode: String) : AtDetection {
//
//     private sealed class Answer {
//         internal object NotInit : Answer()
//         internal sealed class Checked(val c: Boolean) : Answer() {
//             internal companion object {
//                 internal fun check(c: Boolean) = if (c) OK else NO
//             }
//
//             internal object OK : Checked(true)
//             internal object NO : Checked(false)
//         }
//     }
//
//     private var _atBot: Answer
//     private var _atAll: Answer
//     private var _atAny: Answer
//
//     init {
//         // if private, pass all.
//         if (msg is PrivateMsg) {
//             _atBot = Answer.Checked.OK
//             _atAll = Answer.Checked.OK
//             _atAny = Answer.Checked.OK
//         } else {
//             _atBot = Answer.NotInit
//             _atAll = Answer.NotInit
//             _atAny = Answer.NotInit
//         }
//     }
//
//     private val messageContent get() = msg.msgContent
//
//     private lateinit var _codeCache: Set<String>
//     private val codeCache: Set<String>
//         get() {
//             if (!::_codeCache.isInitialized) {
//                 _codeCache = TreeSet<String>().also { set ->
//                     cats.forEach { n ->
//                         if (n.type == "at") {
//                             n["code"]?.let { c -> set.add(c) }
//                         }
//                     }
//                 }
//
//             }
//             return _codeCache
//         }
//
//     private lateinit var _cats: List<Neko>
//     private val cats: List<Neko>
//         get() {
//             if (!::_cats.isInitialized) {
//                 _cats = messageContent.cats.filter { n -> n.type == "at" }
//             }
//             return _cats
//         }
//
//     override fun atBot(): Boolean {
//         return if (_atBot === Answer.NotInit) {
//             (cats.any { neko -> neko.type == "at" && neko["code"] == botCode }).also {
//                 _atBot = Answer.Checked.check(it)
//             }
//         } else (_atBot as Answer.Checked).c
//     }
//
//     override fun atAll(): Boolean {
//         return if (_atAll === Answer.NotInit) {
//             (cats.any { neko -> neko.type == "at" && (neko["all"] == "true" || neko["code"] == "all") }).also {
//                 _atAll = Answer.Checked.check(it)
//             }
//         } else (_atAll as Answer.Checked).c
//     }
//
//     override fun atAny(): Boolean {
//         return if (_atAny === Answer.NotInit) {
//             (cats.any { neko -> neko.type == "at" }).also {
//                 _atAny = Answer.Checked.check(it)
//             }
//         } else (_atAny as Answer.Checked).c
//     }
//
//     override fun at(codes: Array<String>): Boolean = codes.all { codeCache.contains(it) }
// }