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

package love.forte.simbot.component.mirai.message

import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.Image.Key.queryUrl


/**
 * mirai组件针对消息链 [MiraiMessageChainContent] 进行重构的重构器。
 */
internal class MiraiMessageChainReconstructor(private val messageChainContent: MiraiMessageChainContent) :
    MiraiMessageReconstructor {

    private var changed = false

    private lateinit var chainAtAction: ChainAtAction
    private lateinit var chainFaceAction: ChainFaceAction
    private lateinit var chainImageAction: ChainImageAction
    private lateinit var chainTextAction: ChainTextAction

    private var newChainSequence: Sequence<SingleMessage>? = null

    override fun at(): MiraiMessageReconstructor.MiraiAtAction {
        if (!::chainAtAction.isInitialized) {
            chainAtAction = ChainAtAction()
        }
        return chainAtAction
    }

    override fun face(): MiraiMessageReconstructor.MiraiFaceAction {
        if (!::chainFaceAction.isInitialized) {
            chainFaceAction = ChainFaceAction()
        }
        return chainFaceAction
    }

    override fun image(): MiraiMessageReconstructor.MiraiImageAction {
        if (!::chainImageAction.isInitialized) {
            chainImageAction = ChainImageAction()
        }
        return chainImageAction
    }

    override fun text(): MiraiMessageReconstructor.MiraiTextAction {
        if (!::chainTextAction.isInitialized) {
            chainTextAction = ChainTextAction()
        }
        return chainTextAction
    }

    fun build(): MiraiMessageContent {
        return if (changed) {
            return MiraiMessageChainContent(newChainSequence!!.toMessageChain())
        } else messageChainContent
    }

    /**
     * 对消息链进行操作。
     */
    private inline fun changeChain(block: Sequence<SingleMessage>.() -> Sequence<SingleMessage>) {
        newChainSequence = newChainSequence?.block()
    }

    private abstract class BaseChainAction : MiraiMessageReconstructor.MiraiAction {
        protected abstract fun removeByParams(params: Map<String, String>)
        final override fun remove(params: Map<String, String>) {
            if (params.isEmpty()) remove() else removeByParams(params)
        }
    }

    /**
     * 消息链中对at类型进行的操作
     */
    private inner class ChainAtAction : BaseChainAction(), MiraiMessageReconstructor.MiraiAtAction {

        override fun remove() {
            changeChain { filter { m -> m !is At } }
        }

        override fun removeByParams(params: Map<String, String>) {
            val code = params["code"] ?: params["target"] ?: params["qq"] ?: return
            removeByCode(code)
        }

        override fun removeAtAll() {
            changeChain { filter { m -> m !is AtAll } }
        }

        override fun removeByCode(code: String) {
            changeChain {
                filter { m ->
                    if (m is At) {
                        m.target != code.toLong()
                    } else true
                }
            }
        }
    }

    /**
     * 消息链中对表情类型进行的操作
     */
    private inner class ChainFaceAction : BaseChainAction(), MiraiMessageReconstructor.MiraiFaceAction {
        override fun remove() {
            changeChain { filter { m -> m !is Face } }
        }

        override fun removeByParams(params: Map<String, String>) {
            val id = params["id"] ?: return
            removeById(id)
        }

        override fun removeById(id: String) {
            changeChain {
                filter { sm ->
                    if (sm is Face) {
                        sm.id != id.toInt()
                    } else true
                }
            }
        }
    }

    /**
     * 消息链中对图片类型进行的操作
     */
    private inner class ChainImageAction : BaseChainAction(), MiraiMessageReconstructor.MiraiImageAction {
        override fun remove() {
            changeChain { filter { m -> m !is Image } }
        }

        override fun removeByParams(params: Map<String, String>) {
            params["id"]?.let(::removeById)
            (params["file"] ?: params["path"])?.let(::removeByFile)
            params["url"]?.let(::removeByUrl)
        }

        private fun changeChainImageFilter(block: Image.() -> Boolean) {
            changeChain {
                filter { m ->
                    if (m is Image) m.block() else true
                }
            }
        }

        override fun removeById(id: String) {
            changeChainImageFilter { imageId != id }
        }

        override fun removeByUrl(url: String) {
            changeChainImageFilter { runBlocking { queryUrl() == url } }
        }

        /**
         * file 在接收image的时候与url相同含义。
         */
        override fun removeByFile(file: String) {
            removeByUrl(file)
        }
    }

    /**
     * 消息链中对文本类型进行的操作
     */
    private inner class ChainTextAction : BaseChainAction(), MiraiMessageReconstructor.MiraiTextAction {
        override fun removeByParams(params: Map<String, String>) {
            val text = params["text"] ?: return
            removeBy { m -> m == text }
        }

        override fun remove() {
            changeChain { filter { m -> m !is PlainText } }
        }

        override fun removeBy(match: (String) -> Boolean) {
            changeChain {
                filter { m ->
                    if (m is PlainText) {
                        !match(m.content)
                    } else true
                }
            }
        }
    }


}












