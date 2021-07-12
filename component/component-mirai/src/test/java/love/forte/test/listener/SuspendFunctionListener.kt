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

import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import love.forte.common.ioc.annotation.Beans
import love.forte.simbot.annotation.Filter
import love.forte.simbot.annotation.FilterValue
import love.forte.simbot.annotation.Filters
import love.forte.simbot.annotation.OnPrivate
import love.forte.simbot.api.message.events.PrivateMsg
import love.forte.simbot.api.sender.Sender
import love.forte.simbot.filter.MatchType
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.Continuation
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration
import kotlin.time.ExperimentalTime


/**
 * 挂起对话示例
 * @author ForteScarlet
 */
@Beans
class SuspendFunctionListener {

    /**
     * 记录等待状态。
     * 需要注意，如果你是多Bot环境，需要多考虑区分BOT的问题（如果需要的话）
     */
    private val userPhoneWaitingMap: MutableMap<String, Continuation<Long>> = ConcurrentHashMap()

    /**
     * 等待账号。
     */
    private suspend fun waitForPhone(accountCode: String) = suspendCoroutine<Long> { continuation ->
        userPhoneWaitingMap.merge(accountCode, continuation) { _, _ ->
            throw IllegalStateException("Account $accountCode was still waiting.")
        }
    }

    /**
     * 开启 “绑定手机号” 对话.
     */
    @OptIn(ExperimentalTime::class)
    @OnPrivate
    @Filters(Filter("绑定手机号")) // kotlin中，必须使用 @Filters包裹@Filter
    suspend fun PrivateMsg.bindMobilePhoneNumber(sender: Sender) {
        // 当前作用域的上下文。
        val coroutineContext = coroutineContext
        // val scope = CoroutineScope(coroutineContext)

        // 账号
        val code = this.accountInfo.accountCode
        sender.sendPrivateMsg(this@bindMobilePhoneNumber, "请在30s内提供手机号")
        // wait for phone
        // 30s内提供手机号，否则超时。
        withTimeoutOrNull(Duration.seconds(30)) {

            // 这里要使用外部的coroutineContext
            val phone = withContext(coroutineContext) { waitForPhone(code) }
            sender.sendPrivateMsg(this@bindMobilePhoneNumber, "手机号 $phone 绑定成功")

        } ?: run {
                // 超时了
                userPhoneWaitingMap.remove(code)
                sender.sendPrivateMsg(this, "超时了哦！")
                return
            }


    }


    /**
     * 监听一个纯数字的私聊，当 [userPhoneWaitingMap] 中有对应的账号所对应的挂起点，会提供此值。
     */
    @OnPrivate
    @Filters(Filter("{{phone,\\d+}}", matchType = MatchType.REGEX_MATCHES)) // 使用正则匹配截取手机号信息.
    fun PrivateMsg.getPhone(@FilterValue("phone") phone: Long) {
        // 如果能够remove，则说明存在挂起，提供此值。
        userPhoneWaitingMap.remove(this.accountInfo.accountCode)?.resume(phone)
    }


}

