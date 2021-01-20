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

package love.forte.simbot.filter

import love.forte.simbot.api.message.events.MsgGet


/**
 * at检测器。用于判断bot是否被at了。
 */
public interface AtDetection {

    /**
     * 如果bot被at了，则返回true。
     *
     * ### since 2.0.0-RC.2
     *
     * 如果当前消息类型为 [私聊消息][love.forte.simbot.api.message.events.PrivateMsg], 则直接视为 `true`。
     */
    fun atBot(): Boolean

    /**
     * at了全体。
     *
     * ### since 2.0.0-RC.2
     *
     * 如果当前消息类型为 [私聊消息][love.forte.simbot.api.message.events.PrivateMsg], 则直接视为 `true`。
     */
    fun atAll(): Boolean

    /**
     * at了任意一个人。
     *
     * ### since 2.0.0-RC.2
     *
     * 如果当前消息类型为 [私聊消息][love.forte.simbot.api.message.events.PrivateMsg], 则直接视为 `true`。
     */
    fun atAny(): Boolean

    /**
     * at了指定的这些用户。
     */
    fun at(codes: Array<String>): Boolean
}

/**
 * 总是允许的 [AtDetection] 实例。
 */
public object AlwaysAllowedAtDetection : AtDetection {
    override fun atBot(): Boolean = true
    override fun atAll(): Boolean = true
    override fun atAny(): Boolean = true
    override fun at(codes: Array<String>): Boolean = true
}

/**
 * 总是拒绝的 [AtDetection] 实例。
 */
public object AlwaysRefuseAtDetection : AtDetection {
    override fun atBot(): Boolean = false
    override fun atAll(): Boolean = false
    override fun atAny(): Boolean = false
    override fun at(codes: Array<String>): Boolean = false
}

/**
 * 使用固定常量值的 [AtDetection] 实例。
 */
public data class ConstantAtDetection(
    private val atBot: Boolean,
    private val atAll: Boolean,
    private val atAny: Boolean,
    private val at: Boolean,
) : AtDetection {
    override fun atBot(): Boolean = atBot
    override fun atAll(): Boolean = atAll
    override fun atAny(): Boolean = atAny
    override fun at(codes: Array<String>): Boolean = at
}






/**
 * [AtDetection] 工厂。
 */
public interface AtDetectionFactory {

    /**
     * 根据一个msg实例构建一个 [AtDetection] 函数。
     *
     * 在manager中，如果此方法返回了一个 null 则视为获取失败，会去尝试使用其他 factory 直至成功。
     *
     */
    fun getAtDetection(msg: MsgGet): AtDetection
}



/**
 * [AtDetection] 注册器。
 */
public interface AtDetectionRegistrar {
    /**
     * 注册一个 [AtDetection] 构建函数。
     */
    fun registryAtDetection(atDetectionFactory: AtDetectionFactory)
}
