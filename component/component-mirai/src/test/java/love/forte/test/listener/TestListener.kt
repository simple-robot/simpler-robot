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

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import love.forte.simbot.annotation.*
import love.forte.simbot.api.message.events.GroupMsg
import love.forte.simbot.api.message.events.PrivateMsg
import love.forte.simbot.api.sender.Sender
import love.forte.simbot.filter.MatchType
import kotlin.coroutines.coroutineContext


/**
 *
 * @author ForteScarlet
 */
// @Beans
class TestListener {

    @OptIn(ExperimentalStdlibApi::class)
    @Filters(value = [
        Filter(".h1", trim = true),
        Filter(".h2", trim = true),
        Filter(".h3", trim = true)],
        bots = ["2370606773"],
        groups = ["703454734"]
    )
    @OnGroup
    suspend fun GroupMsg.listen(sender: Sender) {
        println(coroutineContext[CoroutineDispatcher])
        println("${this.botInfo}: $this")
        sender.sendGroupMsg(this, "Yes!")
    }



    @Filters(value = [Filter(".h{{n,\\d+}}", matchType = MatchType.REGEX_MATCHES, codes = ["1149159218"])])
    @OnPrivate
    suspend fun PrivateMsg.listen2(sender: Sender, @FilterValue("n") n: Int) {
        println(coroutineContext[CoroutineName])
        sender.sendPrivateMsg(this, n.toString())
    }

}

