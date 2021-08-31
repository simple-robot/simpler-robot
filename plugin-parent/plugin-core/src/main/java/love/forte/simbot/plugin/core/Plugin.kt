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

@file:JvmName("Plugins")
@file:JvmMultifileClass
package love.forte.simbot.plugin.core

import love.forte.simbot.listener.ListenerFunction


/**
 *
 * 一个插件。[Plugin] 是所有插件的统一父类接口。
 *
 * **插件** 是允许动态拔插的额外内容（例如监听函数），他们普遍以Jar文件、脚本文件等外部文件的形式存在，
 * 且可能会随时被修改、删除。
 *
 * 插件存在的意义即允许存在这样的动态内容，并在其修改、删除的情况下做出相应的更新调整，以实现在不终止主要程序的情况下，追加新的内容，
 * 也就是类似于一种“热更新”。
 *
 * 插件系统中，一切通过插件追加的内容，均不允许向依赖注入中追加内容，也就是说，插件里的所有内容均**不会**交由运行时的依赖中心进行管理，
 * 但是允许在依赖中心中进行 **读取**。
 *
 *
 * TODO 会考虑为每一个插件环境提供一个独立的依赖中心来实现当前插件范围内的局部管理。
 *
 *
 * @author ForteScarlet
 */
public sealed interface Plugin : PluginInfoContainer


/**
 * 一个 [监听函数][ListenerFunction] 插件。此插件代表其允许插入监听函数。
 *
 */
public interface ListenerPlugin : Plugin {

    /**
     * 得到监听函数列表。初始载入以及每次Plugin的对应文件变化的时候都会获取一次。
     */
    fun getListeners(): List<ListenerFunction>
}

@get:JvmSynthetic
public val ListenerPlugin.listeners: List<ListenerFunction>
    get() = getListeners()
