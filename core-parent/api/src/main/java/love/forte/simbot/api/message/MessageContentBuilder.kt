/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
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
@file:JvmName("MessageContents")
@file:JvmMultifileClass

package love.forte.simbot.api.message

import love.forte.simbot.api.SimbotExperimentalApi
import love.forte.simbot.api.message.containers.AccountCodeContainer
import love.forte.simbot.api.message.containers.AccountContainer
import love.forte.simbot.mark.InstantInit
import love.forte.simbot.mark.LazyInit
import java.io.IOException
import java.io.InputStream


/**
 * [MessageContentBuilder] 构建器工厂。
 */
public fun interface MessageContentBuilderFactory {
    fun getMessageContentBuilder(): MessageContentBuilder
}


/**
 *
 * 用于构建一个 [MessageContent] 实例的构建器，
 * 可通过注入一个 [MessageContentBuilderFactory] 来获取当前组件所提供的工厂实现。
 *
 * 不同于使用[猫猫码工具][catcode.CatCodeUtil]，
 * 猫猫码作为一种中间化的消息格式，有着更为灵活的实现方式与更高兼容性与扩展性。
 *
 * 但是对于一种组件来讲，猫猫码的解析会增加对效率的损耗。
 *
 * 因此为了兼顾 **灵活与扩展** 和 **效率**，除了猫猫码以外，
 * 提供了 [MessageContentBuilder]，
 * 使得一些十分常见的消息类型（例如图片、at等）可以通过组件的实现来达到更低的解析成本。
 *
 *
 * 如果要达到更高效的消息构建，组件需要根据实际情况自行实现。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
public interface MessageContentBuilder {

    /**
     * 最基础的消息类型。向当前构建的消息中追加一个 文本消息。
     * 当同时调用多次[text]的时候，其效果应当等同于多次内容的拼接。例如：
     * ```
     * builder.text("aaa").text(",").text("bbb").text(",").text("ccc")
     * ```
     * 其效果应当等同于：
     * ```
     * builder.text("aaa,bbb,ccc")
     * ```
     */
    fun text(text: CharSequence): MessageContentBuilder

    /** 向当前构建的消息中追加一个 'at全体'的消息。 */
    fun atAll(): MessageContentBuilder

    /** 向当前构建的消息中追加一个 'at某人'的消息。 */
    fun at(code: String): MessageContentBuilder

    /** 向当前构建的消息中追加一个 'at某人'的消息。 */
    @JvmDefault
    fun at(code: Long): MessageContentBuilder = at(code.toString())

    /** 向当前构建的消息中追加一个 'at某人'的消息。 */
    @JvmDefault
    fun at(code: AccountCodeContainer): MessageContentBuilder = at(code.accountCode)

    /** 向当前构建的消息中追加一个 'at某人'的消息。 */
    @JvmDefault
    fun at(code: AccountContainer): MessageContentBuilder = at(code.accountInfo)

    /** 向当前构建的消息中追加一个 '表情'消息。 */
    fun face(id: String): MessageContentBuilder

    @JvmDefault
    fun face(id: Int): MessageContentBuilder = face(id.toString())

    /**
     * 向当前构建的消息中追加一个本地图片。
     */
    fun imageLocal(path: String, flash: Boolean): MessageContentBuilder

    /** 向当前构建的消息中追加一个网络图片。 */
    fun imageUrl(url: String, flash: Boolean): MessageContentBuilder

    /** 向当前构建的消息中追加一个本地图片。 */
    @JvmDefault
    fun imageLocal(path: String): MessageContentBuilder = imageLocal(path, false)

    /** 向当前构建的消息中追加一个网络图片。 */
    @JvmDefault
    fun imageUrl(url: String): MessageContentBuilder = imageUrl(url, false)

    /** 向当前构建的消息中追加一个本地图片或网络图片。 */
    @JvmDefault
    fun image(path: String, flash: Boolean): MessageContentBuilder =
        if (path.startsWith("http")) imageUrl(path, flash)
        else imageLocal(path, flash)

    /** 向当前构建的消息中追加一个本地图片或网络图片。 */
    @JvmDefault
    fun image(path: String): MessageContentBuilder =
        if (path.startsWith("http")) imageUrl(path)
        else imageLocal(path)

    /**
     * 向当前构建的消息中追加一个图片流。
     *
     * 此输入流会立即被使用，需要手动关闭 [输入流][input].
     */
    @InstantInit
    fun image(input: InputStream, flash: Boolean): MessageContentBuilder


    /**
     * 向当前构建的消息中追加一个图片流。
     *
     * 图片流实例需要在 [inputStreamMotionActuator] 函数中进行获取、执行并手动关闭.
     *
     * TODO
     *
     * 建议：
     * - 获取图片流的逻辑置于函数内部，以防止当此函数未调用时不会造成输入流溢出；
     * - 在函数体内部添加try-catch-finally以保证输入流会正确关闭。
     *
     * ```java
     * MessageContentBuilder c = //... ;
            c.image(action -> {
            // 简易获取输入流的逻辑.
            try (InputStream in = new ByteArrayInputStream(new byte[0])) {
            // 使用这个输入流执行逻辑.
            action.invoke(in);
            }

            });
     *
     * ```
     *
     */
    @LazyInit
    @SimbotExperimentalApi
    fun image(inputStreamMotionActuator: InputStreamMotionActuator<InputStream>, flash: Boolean): MessageContentBuilder = TODO()

    /**
     * 向当前构建的消息中追加一个图片流。
     *
     * TODO
     *
     * 图片流实例需要在 [inputStreamMotionActuator] 函数中进行获取、执行并手动关闭.
     *
     *
     * 建议：
     * - 获取图片流的逻辑置于函数内部，以防止当此函数未调用时不会造成输入流溢出；
     * - 在函数体内部添加try-catch-finally以保证输入流会正确关闭。
     *
     */
    @LazyInit
    @SimbotExperimentalApi
    fun image(inputStreamMotionActuator: InputStreamMotionActuator<InputStream>): MessageContentBuilder = image(inputStreamMotionActuator, false)


    /** 向当前构建的消息中追加一个图片字节数组。 */
    fun image(imgData: ByteArray, flash: Boolean): MessageContentBuilder

    /**
     * 向当前构建的消息中追加一个图片流。
     * 此输入流会立即被使用，需要手动关闭 [输入流][input].
     */
    @JvmDefault
    @InstantInit
    fun image(input: InputStream): MessageContentBuilder = image(input, false)

    /** 向当前构建的消息中追加一个图片字节数组。 */
    @JvmDefault
    fun image(imgData: ByteArray): MessageContentBuilder = image(imgData, false)

    // TODO 支持与其他 [MessageContent] 进行合并/拼接。
    // fun context(messageContent: MessageContent): MessageContentBuilder

    /** 得到当前构建的消息。 */
    fun build(): MessageContent
}


/**
 * 动作执行器。
 */
public fun interface InputStreamMotionActuator<T> {
    @Throws(IOException::class)
    fun invoke(action: (T) -> Unit)
}
