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
@file:Suppress("unused")

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
 * ## 目录结构
 * 在插件根目录中的结构为：
 * ```
 * <plugin-root>
 *       └ plugins  (默认)
 *            ├ libraries  (默认目录，所有的插件共享的额外依赖库。)
 *            │      └ Lib jars  (各依赖Jar文件)
 *            └ plugin-id  (目录，例如 forte.example-plugin.demo1)
 *                  ├ Plugin Jar File  (与plugin-id **同名** 的jar,
 *                  │         例如 forte.example-plugin.demo1.jar)
 *                  └ libraries  (目录，如果这个Jar有独立的依赖库，放在这里)
 *                        └ Lib jars  (各依赖Jar文件)
 *
 *```
 *
 * @see ListenerPlugin
 *
 * @author ForteScarlet
 */
public interface Plugin : PluginInfoContainer {

    /**
     * 在插件范围内，需要进行扫描的顶级包路径。
     * 在追加支持插件环境的依赖中心后，此扫描后的内容将会被置于此插件的依赖中心中进行管理。
     *
     * 插件的依赖注入的标识（id）会在字段名称或默认名称之前追加当前插件的 [id][PluginInfo.id].
     * 例如, 假设 [plugin id][PluginInfo.id] 为 `simbot.example.plugin1`,
     * 注入的某个类的依赖标识叫做 `MyDemoBean`, 则如果你想要在插件中通过ID获取他，那么你应该写 `simbot.example.plugin1.MyDemoBean`
     *
     * 也同时因此，建议你使用常量类来记录这些几乎不会变更的常量字符串，例如 [plugin id][PluginInfo.id]、[plugin name][PluginInfo.name] 等。
     *
     */
    val scanPackages: List<String>

    /**
     * 此插件所对应的类加载器。
     * 为了避免内存泄漏，请不要长时间强引用此类加载器，更不要进行对其缓存等操作。
     */
    val pluginLoader: PluginLoader

}


/**
 * 插件的定义，交由用户实现。
 */
public sealed interface PluginDetails



/**
 * 一个 [监听函数][ListenerFunction] 插件。此插件代表其允许插入监听函数。
 *
 */
public interface ListenerPlugin : PluginDetails {

    /**
     * 得到监听函数列表。初始载入以及每次Plugin的对应文件变化的时候都会获取一次。
     */
    fun getListeners(): List<ListenerFunction>
}




@get:JvmSynthetic
public val ListenerPlugin.listeners: List<ListenerFunction>
    get() = getListeners()
