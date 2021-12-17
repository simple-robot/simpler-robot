package test

import love.forte.simbot.action.replyIfSupport
import love.forte.simbot.event.ChannelMessageEvent
import love.forte.simbot.message.AtAll
import love.forte.simbot.message.Text
import love.forte.simbot.message.plus


suspend fun ChannelMessageEvent.myListenerA1(): Unit {

    // reply
    replyIfSupport(AtAll + Text { "Hello World" })


}
