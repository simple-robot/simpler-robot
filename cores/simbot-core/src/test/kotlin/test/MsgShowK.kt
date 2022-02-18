package test

import love.forte.simbot.ID
import love.forte.simbot.message.*


fun main() {

    // 通过用 '+' 拼接消息元素来得到结果。
    val messages: Messages = "abc".toText() + At(123.ID) + AtAll
    // 同上
    val messages2: Messages = "abc".toText() + At(123.ID) + AtAll

    // 通过拼接两个消息连来得到新的消息链
    val combinedMessage = messages + messages2

    val messageElementList = listOf(
        "simbot".toText(),
        At("666".ID),
        AtAll
    )

    // 通过 "消息列表(List<Message.Element>)" 转化为 "消息列表(Messages)"
    val messagesOfList = messageElementList.toMessages()

    // 空实现
    val emptyMessage = EmptyMessages

}