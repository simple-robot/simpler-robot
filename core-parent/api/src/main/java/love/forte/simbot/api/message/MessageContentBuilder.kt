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

import love.forte.simbot.api.message.containers.AccountCodeContainer
import love.forte.simbot.api.message.containers.AccountContainer
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
     * 此方法内应当自动关闭[input]，但是并不会保证方法调用后立即对输入流进行读取或使用，也因此不会保证此方法调用后[input]会立即关闭。
     * 因此不建议对此方法的 [input] 进行后续的其他操作。
     */
    fun image(input: InputStream, flash: Boolean): MessageContentBuilder


    /** 向当前构建的消息中追加一个图片字节数组。 */
    fun image(imgData: ByteArray, flash: Boolean): MessageContentBuilder

    /**
     * 向当前构建的消息中追加一个图片流。
     * 此方法内应当自动关闭[input]，但是并不会保证方法调用后立即对输入流进行读取或使用，也因此不会保证此方法调用后[input]会立即关闭。
     * 因此不建议对此方法的 [input] 进行后续的其他操作。
     */
    @JvmDefault
    fun image(input: InputStream): MessageContentBuilder = image(input, false)

    /** 向当前构建的消息中追加一个图片字节数组。 */
    @JvmDefault
    fun image(imgData: ByteArray): MessageContentBuilder = image(imgData, false)

    // TODO 支持与其他 [MessageContent] 进行合并/拼接。
    // fun context(messageContent: MessageContent): MessageContentBuilder

    /** 得到当前构建的消息。 */
    fun build(): MessageContent
}
