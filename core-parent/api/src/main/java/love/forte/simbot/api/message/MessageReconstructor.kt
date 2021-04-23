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



package love.forte.simbot.api.message

import love.forte.simbot.api.message.MessageReconstructor.*

/**
 * 消息重构器所用参数, 其可以执行一些中间操作，例如根据某些规则进行移除等。
 *
 * 消息重构器目前仅提供一些 **常见** 类型，仅针对下述类型提供针对性的操作以保证兼容性。
 *
 * - [AT][AtAction]
 * - [表情][FaceAction]
 * - [image][ImageAction]
 * - [纯文本][TextAction]
 *
 *
 */
@Suppress("unused")
public interface MessageReconstructor {

    /**
     * 得到at相关的重构操作。
     */
    fun at(): AtAction

    /**
     * 表情相关的重构操作。
     */
    fun face(): FaceAction


    /**
     * 图片相关的重构操作。
     */
    fun image(): ImageAction

    /**
     * 纯文本相关的重构操作。
     */
    fun text(): TextAction


    /**
     * 在重构某一消息类型的的时候可以做的事情。
     */
    public interface Action {
        /**
         * 当前操作的类型。
         */
        val type: String

        /**
         * 直接移除此类型的全部消息。
         */
        fun remove()

        /**
         * 根据参数移除当前类型的消息.
         *
         */
        fun remove(params: Map<String, String>)
    }

    /**
     * at相关的操作。
     */
    public interface AtAction : Action {
        override val type: String
            get() = "at"

        /**
         * 移除at全体的消息。
         */
        fun removeAtAll()

        /**
         * 根据code移除at消息。
         */
        fun removeByCode(code: String)
    }


    /**
     * 表情相关的操作。
     */
    public interface FaceAction : Action {
        override val type: String
            get() = "face"

        /**
         * 移除指定ID的表情消息。
         */
        fun removeById(id: String)

    }


    /**
     * 图片相关的操作。
     */
    public interface ImageAction : Action {
        override val type: String
            get() = "image"

        /**
         * 根据ID移除图片消息
         */
        fun removeById(id: String)

        /**
         * 根据 `file` 参数移除图片消息
         */
        fun removeByFile(file: String)

        /**
         * 根据 `url` 移除图片消息
         */
        fun removeByUrl(file: String)

    }


    /**
     * 纯文本相关的操作。
     */
    public interface TextAction : Action {
        override val type: String
            get() = "text"

        /**
         * 根据提供的纯文本内容进行匹配验证，得到一个 [Boolean] 结果来决定是否进行移除。
         */
        fun removeBy(match: (String) -> Boolean)


    }

}