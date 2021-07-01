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

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.withTimeoutOrNull
import love.forte.common.ioc.annotation.Beans
import love.forte.simbot.annotation.Filter
import love.forte.simbot.annotation.FilterValue
import love.forte.simbot.annotation.Filters
import love.forte.simbot.annotation.OnGroup
import love.forte.simbot.api.message.events.GroupMsg
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
 *
 * @author ForteScarlet
 */
@Beans
@OnGroup
class TestListener2 {

    private val nameNeedMap = ConcurrentHashMap<String, Continuation<String>>()
    private val ageNeedMap = ConcurrentHashMap<String, Continuation<Int>>()


    private suspend fun needAge(from: String) = suspendCoroutine<Int> {
        ageNeedMap[from] = it
    }


    @OptIn(ExperimentalTime::class)
    @Filters(value = [Filter("t{{time,\\d+}}", matchType = MatchType.REGEX_MATCHES)],
        groups = ["1043409458"],
        bots = ["2370606773"])
    suspend fun needNameAge(sender: Sender, msg: GroupMsg, @FilterValue("time") time: Long) {
        val scope = CoroutineScope(coroutineContext)
        val code = msg.accountInfo.accountCode

        sender.sendGroupMsg(msg, "age=xx and name=xx in $time 秒")

        withTimeoutOrNull(Duration.seconds(time)) {
            val name = scope.async { suspendCoroutine<String> { c -> nameNeedMap[code] = c } }
            val age = scope.async { suspendCoroutine<Int> { c -> ageNeedMap[code] = c } }

            sender.sendGroupMsg(msg, "姓名：${name.await()}, 年龄：${age.await()}")
        } ?: run {
            nameNeedMap.remove(code)
            ageNeedMap.remove(code)
            sender.sendGroupMsg(msg, "喂喂喂超时了啦")
        }


    }

    @Filters(
        value = [Filter("age={{age,\\d+}}", matchType = MatchType.REGEX_MATCHES)],
        groups = ["1043409458"],
        bots = ["2370606773"]
    )
    fun GroupMsg.age(sender: Sender, @FilterValue("age") age: Int) {
        ageNeedMap.remove(this.accountInfo.accountCode)?.resume(age)?.also {
            sender.sendGroupMsg(this, "年龄记下了喔~")
        } ?: return
    }

    @Filters(
        value = [Filter("name={{name}}", matchType = MatchType.REGEX_MATCHES)],
        groups = ["1043409458"],
        bots = ["2370606773"]
    )
    fun GroupMsg.name(sender: Sender, @FilterValue("name") name: String) {
        nameNeedMap.remove(this.accountInfo.accountCode)?.resume(name)?.also {
            sender.sendGroupMsg(this, "名字记下了喔~")
        } ?: return

    }


}