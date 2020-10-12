/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MessageParser.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

@file:JvmName("MiraiMessageParser")
package love.forte.simbot.component.mirai.utils

import love.forte.catcode.CAT_HEAD
import love.forte.catcode.CatCodeUtil
import love.forte.catcode.Neko
import love.forte.catcode.codes.Nyanko
import love.forte.simbot.component.mirai.message.MiraiMessageContent
import love.forte.simbot.core.api.message.ExpectedMessageContent
import love.forte.simbot.core.api.message.MessageContent
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.message.data.MessageChain
import net.mamoe.mirai.message.data.SingleMessage


/**
 * 将一个 [MessageContent] 转化为一个 [MiraiMessageContent]。
 */
public fun MessageContent.toMiraiMessageContent() : MessageContent {
    when(this) {
        is MiraiMessageContent -> return this
        // 预期内的消息。
        is ExpectedMessageContent -> {

        }
        else -> {

        }
    }

    TODO()
}


/**
 * 将可能存在catcode的字符串文本转化为 [MiraiMessageContent]。
 */
public fun String.toMiraiMessageContent() : MessageContent {
    CatCodeUtil.split(this) {
        // is a cat code.
        when {
            startsWith(CAT_HEAD) -> Nyanko.byCode(this)

            else -> TODO()
        }

    }



    TODO()
}



public fun Neko.toMessageAsync(contact: Contact) {

}






/**
 * 将一个 [MessageChain] 转化为携带catcode的字符串。
 */
public fun MessageChain.toSimbotString() : String {
    return this.asSequence().map { it.toSimbotString() }.joinToString()
}


/**
 * 将一个 [SingleMessage] 转化为携带catcode的字符串。
 */
public fun SingleMessage.toSimbotString() : String {
    TODO()
}
