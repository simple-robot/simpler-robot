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

@file:JvmName("QuickReplyProcessors")

package love.forte.simbot.core.processor

import love.forte.common.utils.Carrier
import love.forte.simbot.api.message.MessageContent
import love.forte.simbot.api.message.MessageContentBuilderFactory
import love.forte.simbot.api.message.ReplyAble
import love.forte.simbot.api.message.ability.PureAcceptDecideAbility
import love.forte.simbot.api.message.ability.PureRejectDecideAbility
import love.forte.simbot.api.message.assists.Flag
import love.forte.simbot.api.message.containers.GroupContainer
import love.forte.simbot.api.message.events.*
import love.forte.simbot.api.sender.Sender
import love.forte.simbot.processor.ListenResultProcessor
import love.forte.simbot.processor.ListenResultProcessorContext


/**
 *
 * [ListenResultProcessor] 的默认实现之一，用于解析响应值并进行快速回复操作。
 *
 * @see ReplyAble
 *
 * @author ForteScarlet
 */
public class QuickReplyProcessor(private val messageContentBuilderFactory: MessageContentBuilderFactory) :
    ListenResultProcessor {

    private companion object {
        private const val NOTHING = -1
        private const val GROUP_MSG = 1
        private const val PRIVATE_MSG = 2

        private val booleanArray = arrayOf("true", "false")

        private val MsgGet.modifier: Int
            get() = when (this) {
                is PrivateMsg -> PRIVATE_MSG
                is GroupMsg -> GROUP_MSG
                else -> NOTHING
            }
    }


    /**
     * 接收 [ListenResultProcessorContext] 进行处理（例如解析并进行自动回复等）。
     *
     * @return 是否处理成功。
     */
    override fun processor(processContext: ListenResultProcessorContext): Boolean {
        val listenerFunctionInvokeData = processContext.listenerFunctionInvokeData

        val msgGet = listenerFunctionInvokeData.msgGet
        val sender = listenerFunctionInvokeData.msgSender.SENDER
        val setter = listenerFunctionInvokeData.msgSender.SETTER

        val result = processContext.listenResult.result
        if (result == null || result == ReplyAble.Empty) {
            return false
        }

        when (msgGet) {
            // a message get.
            is MessageGet -> {
                return when (result) {
                    is ReplyAble -> {
                        // 如果是一个快速回复实例, 判断at和reply
                        val reply = result.reply
                        val at = result.at

                        reply(msgGet, sender, reply, at, messageContentBuilderFactory)
                    }
                    is Map<*, *> -> {
                        // 是个Map，获取 'reply' 和 'at'
                        val replyGet = result["reply"]
                        val atGet = result["at"]
                        if (replyGet == null && atGet == null) {
                            return false
                        }

                        val reply: CharSequence? = replyGet?.let {
                            if (it !is CharSequence) {
                                it.toString()
                            } else it
                        }

                        val at: Boolean = atGet?.let {
                            if (it !is Boolean) {
                                when {
                                    it is CharSequence && it.toString().equals("true", true) -> true
                                    it is CharSequence && it.toString().equals("false", true) -> false
                                    it is Number -> it.toInt() == 0
                                    else -> null
                                }

                            } else it
                        } ?: false


                        reply(msgGet, sender, reply, at, messageContentBuilderFactory)
                    }
                    is MessageContent -> {
                        // message content.
                        // just reply.
                        reply(msgGet, sender, result)
                    }
                    else -> false
                }
            }

            // 纯粹决策: 接受
            is PureAcceptDecideAbility<*> -> {
                return when (result) {
                    is ReplyAble -> {
                        // 如果是一个快速回复实例
                        // 如果存在决策，并同意决策
                        if (result.process == true) msgGet.accept() else false
                    }
                    is Map<*, *> -> {
                        val process = result.process
                        if (process == true) msgGet.accept() else false
                    }
                    else -> false
                }
            }

            // 纯粹决策：拒绝
            is PureRejectDecideAbility<*> -> {
                return when (result) {
                    is ReplyAble -> {
                        // 如果是一个快速回复实例
                        // 如果存在决策，并同意决策
                        if (result.process == false) msgGet.reject() else false
                    }
                    is Map<*, *> -> {
                        val process = result.process
                        if (process == false) msgGet.reject() else false
                    }
                    else -> false
                }
            }

            is RequestGet -> {
                fun doProcess(process: Boolean): Boolean {
                    val flag = msgGet.flag
                    return if (process) {
                        @Suppress("UNCHECKED_CAST")
                        when (flag.flag) {
                            is GroupAddRequest.FlagContent -> setter.acceptGroupAddRequest(flag as Flag<GroupAddRequest.FlagContent>)
                            is FriendAddRequest.FlagContent -> setter.acceptFriendAddRequest(flag as Flag<FriendAddRequest.FlagContent>)
                            else -> Carrier.empty()
                        }.orElse(false)
                    } else {
                        @Suppress("UNCHECKED_CAST")
                        when (flag.flag) {
                            is GroupAddRequest.FlagContent -> setter.rejectGroupAddRequest(flag as Flag<GroupAddRequest.FlagContent>)
                            is FriendAddRequest.FlagContent -> setter.rejectFriendAddRequest(flag as Flag<FriendAddRequest.FlagContent>)
                            else -> Carrier.empty()
                        }.orElse(false)
                    }
                }

                // a request get.
                return when (result) {
                    // 如果是一个快速回复实例
                    is ReplyAble -> result.process?.let { doProcess(it) } ?: false

                    is Map<*, *> -> result.process?.let { doProcess(it) } ?: false

                    else -> false
                }
            }
            else -> return false
        }

    }


}


internal fun reply(
    msgGet: MessageGet,
    sender: Sender,
    reply: CharSequence?,
    at: Boolean,
    messageContentBuilderFactory: MessageContentBuilderFactory,
): Boolean {
    return if (reply == null && !at) {
        false
    } else {
        if (reply is MessageContent) {
            val atReply = if (at) {
                val builder = messageContentBuilderFactory.getMessageContentBuilder()
                val atMsg = builder.at(msgGet.accountInfo).build()
                reply(msgGet, sender, atMsg)
            } else true

            // reply msg.
            reply(msgGet, sender, reply) && atReply
        } else {
            val builder = messageContentBuilderFactory.getMessageContentBuilder()

            if (at) {
                builder.at(msgGet.accountInfo)
            }

            if (reply != null) {
                builder.text(reply.toString())
            }

            val replyMsg = builder.build()

            // do reply.
            reply(msgGet, sender, replyMsg)
        }
    }
}


internal val Map<*, *>.process: Boolean?
    get() {
        return this["process"]?.let { pro ->
            when {
                pro is Boolean -> pro
                pro is CharSequence && pro.toString().equals("true", true) -> true
                pro is CharSequence && pro.toString().equals("false", true) -> false
                pro is Number -> pro.toInt() != 0
                else -> false
            }
        } ?: false
    }


/**
 * 回复消息。
 */
internal fun reply(messageGet: MessageGet, sender: Sender, msg: MessageContent): Boolean {
    return if (messageGet is PrivateMsg) {
        if (messageGet is GroupContainer) {
            sender.sendPrivateMsg(messageGet.accountInfo, messageGet.groupInfo, msg).map { true }.orElse(false)
        } else {
            sender.sendPrivateMsg(messageGet, msg).map { true }.orElse(false)
        }
    } else if (messageGet is GroupMsg) {
        sender.sendGroupMsg(messageGet.groupInfo, msg).map { true }.orElse(false)
    } else {
        false
    }
}


