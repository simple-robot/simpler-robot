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

package love.forte.simbot.component.mirai.configuration

import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.common.ioc.annotation.PrePass
import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.component.mirai.message.event.MiraiMessageMsgGet
import love.forte.simbot.constant.PriorityConstant
import love.forte.simbot.core.filter.CatAtDetectionFactory
import love.forte.simbot.filter.*
import net.mamoe.mirai.message.data.At
import net.mamoe.mirai.message.data.AtAll
import net.mamoe.mirai.message.data.MessageChain

/**
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@ConfigBeans("miraiAtDetectionRegistrar")
public class MiraiAtDetectionRegistrar {


    /**
     * 注册一个 [CatAtDetectionFactory]。
     */
    @PrePass(priority = PriorityConstant.COMPONENT_TENTH)
    fun registerMiraiAtDetectionFactory(registrar: AtDetectionRegistrar) {
        registrar.registryAtDetectionFirst(MiraiAtDetectionFactory)
    }

}


/**
 * mirai的at检测器。
 */
private object MiraiAtDetectionFactory : AtDetectionFactory {
    /**
     * mirai的at检测器，判断有没有atbot。
     */
    override fun getAtDetection(msg: MsgGet): AtDetection {
        return if (msg is MiraiMessageMsgGet<*>) {
            val botCode = msg.event.bot.id
            MiraiAtDetection(botCode, msg)
        } else {
            // 如果无法获取消息信息，则直接使用拒绝检测器。将判断交给下一个检测器。一般下一个检测器为 CatAtDetection.
            AlwaysRefuseAtDetection
        }
    }
}

/**
 * mirai at检测器。
 */
private class MiraiAtDetection(private val botCode: Long, msg: MiraiMessageMsgGet<*>) : CacheableAtDetection() {
    private val message: MessageChain = msg.message

    override fun atBotInit(): Boolean? = if (message.isEmpty()) false else null
    override fun atAllInit(): Boolean? = if (message.isEmpty()) false else null
    override fun atAnyInit(): Boolean? = if (message.isEmpty()) false else null


    override fun checkAtBot(): Boolean = message.any { m -> m is At && m.target == botCode }
    override fun checkAtAll(): Boolean = message.contains(AtAll)
    override fun checkAtAny(): Boolean = message.any { m -> m is At }
    override fun initCodes(adder: CodeAdder) {
        message.forEach { m ->
            if (m is At) {
                adder.add(m.target.toString())
            }
        }
    }
}
//
// /**
//  * mirai at检测器。
//  */
// private class MiraiAtDetection(private val botCode: Long, msg: MiraiMessageMsgGet<*>) : AtDetection {
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
//     private var _atBot: Answer = Answer.NotInit
//     private var _atAll: Answer = Answer.NotInit
//     private var _atAny: Answer = Answer.NotInit
//
//     private val message: MessageChain = msg.message
//
//
//     override fun atBot(): Boolean {
//         return message.any {
//             it is At && it.target == botCode
//         }
//     }
//
//     override fun atAll(): Boolean = message.contains(AtAll)
//
//     override fun atAny(): Boolean = message.any { it is At }
//
//     override fun at(codes: Array<String>): Boolean {
//         if (codes.isEmpty()) {
//             return true
//         }
//         val codesSet = codes.toMutableSet()
//         for (it in message) {
//             if (it is At) {
//                 codesSet.remove(it.target.toString())
//             }
//             if (codesSet.isEmpty()) return true
//         }
//         return false
//     }
// }
