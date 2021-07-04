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

@file:Suppress("unused")
package love.forte.simbot.core.strict


/**
 *
 * 严格模式管理器。
 *
 * 目前 [StrictManager] 的功能只有一个：用于获取当前项目环境下是否开启了严格模式。
 *
 * 后续的更新中，严格模式会根据组件进行区别设置，目前仅提供核心的严格模式设置。
 *
 * 严格模式的设置在初始化后确定，然后无法进行更改。
 *
 * 严格模式顾名思义，在开启的情况下会针对一些“也行可以这样”、“那样也行”、“怎样都行”的内容进行严格要求并**不进行妥协**，
 * 相对的，严格模式关闭之下则会更多的对一些摩棱两可的内容尝试着进行高容错。
 *
 * 但是过高的容错率带来的只会是更加底下的效率与愈发不严谨的规范，因此核心与官方组件实现在默认情况下严格模式为 **开启(true)** 状态，
 * 并且建议所有的组件实现如果存在
 *
 * ## core
 * 通过 `simbot.core.strict=true/false` 开启/关闭严格模式。
 *
 * ### since v2.2.0
 * 严格模式 会影响到的内容为 [love.forte.simbot.listener.ListenerFunction] 下的
 * [love.forte.simbot.core.listener.FunctionFromClassListenerFunction] 和
 * [love.forte.simbot.core.listener.MethodListenerFunction] 中，对于参数的注入是否需要标注明确的注解（例如 [@FilterValue][love.forte.simbot.annotation.FilterValue] 或 [@ContextValue][love.forte.simbot.annotation.ContextValue]）
 *
 *
 * ## component
 * 组件中严格模式会影响到的内容由组件自身实现。
 *
 *
 * @author ForteScarlet
 */
public interface StrictManager {

    /**
     * 核心的严格模式开关情况。
     */
    fun coreStrict(): Boolean


    // wait for component implement
    // fun componentStrict()

}



