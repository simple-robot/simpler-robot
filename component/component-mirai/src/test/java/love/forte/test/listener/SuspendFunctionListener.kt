/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
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

package love.forte.test.listener

import love.forte.simbot.annotation.Filter
import love.forte.simbot.annotation.Filters
import love.forte.simbot.annotation.OnPrivate
import love.forte.simbot.annotation.OnlySession
import love.forte.simbot.api.message.events.PrivateMsg
import love.forte.simbot.api.sender.Sender
import love.forte.simbot.filter.MatchType
import love.forte.simbot.listener.ContinuousSessionScopeContext
import love.forte.simbot.listener.ListenerContext
import love.forte.simbot.listener.continuousSessionContext
import love.forte.simbot.listener.get


/**
 * 挂起对话示例
 * @author ForteScarlet
 */
// @Beans
@OnPrivate
class SuspendFunctionListener {

    companion object {
        const val key1 = "==tellMeYourNameAndPhone==PHONE=="
        const val key2 = "==tellMeYourNameAndPhone==NAME=="
    }


    @Filters(Filter("tellMe"))
    suspend fun PrivateMsg.tellMeYourNameAndPhone(context: ListenerContext, sender: Sender) {
        val k = accountInfo.accountCode
        val session = context.continuousSessionContext ?: return
        sender.privateMsg(this, "请输入手机号")
        val phone = session.waiting<Long>(key1, k) {
            println("$key1 : $k 被关闭了")
            it?.printStackTrace()
        }

        sender.privateMsg(this, "手机号为 $phone")
        sender.privateMsg(this, "请输入姓名")
        val name = session.waiting<String>(key2, k) {
            println("$key2 : $k 被关闭了")
            it?.printStackTrace()
        }

        sender.privateMsg(this, "姓名为 $name")

    }

    @OnlySession(group = key1)
    @Filters(Filter("\\d+", matchType = MatchType.REGEX_MATCHES))
    fun PrivateMsg.onPhone(context: ListenerContext) {
        val k = accountInfo.accountCode
        val session = context[ListenerContext.Scope.CONTINUOUS_SESSION]!! as ContinuousSessionScopeContext
        println("On phone: $text")
        session.push(key1, k, text.toLong())
    }

    @OnlySession(group = key2)
    fun PrivateMsg.onName(context: ListenerContext) {
        val k = accountInfo.accountCode
        val session = context[ListenerContext.Scope.CONTINUOUS_SESSION]!! as ContinuousSessionScopeContext
        println("On name: $text")
        session.push(key2, k, text)
    }



}

