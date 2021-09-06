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

package love.forte.simbot.plugin.core

import love.forte.common.ioc.DependBeanFactory
import love.forte.simbot.core.SimbotContext
import love.forte.simbot.core.SimbotContextClosedHandle
import love.forte.simbot.core.TypedCompLogger
import love.forte.simbot.listener.ListenerFunction
import love.forte.simbot.listener.ListenerManager
import java.net.URLClassLoader
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.path.*


/**
 * [PluginManager] 基础实现类。
 *
 * @author ForteScarlet
 */
public class SimplePluginManager(
    /**
     * 父级classloader
     */
    private val parentLoader: ClassLoader,

    /**
     * 监听函数管理器
     */
    private val listenerManager: ListenerManager,

    /**
     * 依赖获取器
     */
    private val dependBeanFactory: DependBeanFactory,

    /** 插件所处的目录 */
    private val pluginRoot: Path,

    /**
     * 插件公共依赖所处lib目录
     */
    private val pluginGlobalLib: Path,

    private val pluginLibName: String = "lib",

    ) : PluginManager, SimbotContextClosedHandle {


    private companion object LOG : TypedCompLogger(SimplePluginManager::class.java)

    /**
     * 记录所有的 [Plugin] 实例。
     */
    private val pluginMap = ConcurrentHashMap<String, Plugin>()

    /**
     * 公共lib loader.
     */
    override lateinit var globalLoader: ClassLoader

    init {

    }

    override fun start() {
        val libJars = pluginGlobalLib.useDirectoryEntries("*.jar") { it.map { p -> p.toUri().toURL() }.toList() }
        globalLoader = URLClassLoader(libJars.toTypedArray(), parentLoader)
        scanPlugins()
            .asSequence()
            .distinctBy { p -> p.id }
            .forEach { p ->
                loadPlugin(p)
            }
    }

    /**
     * 扫描 [pluginRoot] 下的所有插件。
     */
    private fun scanPlugins(): List<Plugin> {
        check(pluginRoot.isDirectory()) { "Plugin root $pluginRoot is not directory." }

        // 寻找所有的 dir
        return pluginRoot.useDirectoryEntries { paths ->
            paths.asSequence()
                .filter { it.isDirectory() }
                .mapNotNull(::resolvePathToPlugin).toList()
        }
    }

    @Synchronized
    private fun resolvePathToPlugin(pluginDir: Path): Plugin? {
        val pluginId = pluginDir.name
        // main
        //   - main.jar
        //   - lib
        val pluginJar = pluginDir / Path("$pluginId.jar")
        if (!pluginDir.exists()) {
            logger.debug("Cannot found main plugin named {} in Plugin dir {}", "$pluginId.jar", pluginId)
            return null
        }
        val pluginLib = pluginDir / Path(pluginLibName)

        logger.debug("Plugin ID(dir): {}", pluginId)
        val pluginDefinition = PluginDefinitionWithTemporarySubstitute(
            root = pluginRoot,
            mainPath = pluginDir,
            mainFilePath = pluginJar,
            librariesPath = pluginLib,
        )

        val loader = PluginLoader(
            parent = globalLoader,
            plugin = pluginDefinition
        ) { thisLoader ->
            onMainCreated {
                // 当Main Jar被创建
                reloadPlugin(pluginId)
            }
            onMainEdited {
                // 当Main Jar被修改
                thisLoader.resetLoaderByMain()
                // reloadPlugin(pluginId)
            }
            onMainDeleted {
                // main deleted, remove listeners
                // listenerManager.removeListenerById()
                unloadPlugin(pluginId)
                thisLoader.close()
            }

            onLibCreated {
                logger.debug("Plugin {} lib created: {}", pluginId, it.name)
                thisLoader.resetLoaderByLib()
            }
            onLibEdited { _, edited ->
                logger.debug("Plugin {} lib edited: {}", pluginId, edited.name)
                thisLoader.resetLoaderByLib()
            }
            onLibIncrease { _, increased ->
                logger.debug("Plugin {} lib increase: {}", pluginId, increased.name)
                thisLoader.resetLoaderByLib()
            }
            onLibReduce { _, reduced ->
                logger.debug("Plugin {} lib reduced: {}", pluginId, reduced.name)
                thisLoader.resetLoaderByLib()
            }
            onLibDeleted {
                logger.debug("Plugin {} lib deleted: {}", pluginId, it.name)
                thisLoader.resetLoaderByLib()
            }

        }

        val pluginDetails = loader.extractDetails()
        val pluginInfo = pluginDetails.extractInformation()

        return SimplePlugin(loader, pluginInfo, pluginDetails)
    }

    @Synchronized
    private fun loadPlugin(plugin: Plugin) {
        // val detailsClass = plugin.pluginInfo.pluginDetails
        val details = plugin.pluginDetails  // pluginLoader.loadClass(detailsClass).newInstance() as PluginDetails
        if (details is ListenerPluginDetails) {
            // 监听函数
            // val listeners = .map { WeakListenerFunction(it) }

            // Register listeners
            val listeners: Array<ListenerFunction> = details.getListeners().toTypedArray()
            listenerManager.register(*listeners)
        }
        pluginMap.merge(plugin.id, plugin) { _, _ ->
            throw IllegalArgumentException("Plugin id ${plugin.id} already exists.")
        }

        // do init
        plugin.pluginDetails.init(dependBeanFactory)
        plugin.pluginLoader.start()
    }


    @Synchronized
    private fun unloadPlugin(id: String) {
        pluginMap.remove(id)?.also {
            it.pluginLoader.close()
        }
    }


    @Synchronized
    private fun reloadPlugin(id: String) {
        pluginMap[id]?.pluginLoader?.resetLoader(main = true, lib = true)
    }


    override val plugins: List<Plugin>
        get() = pluginMap.values.toList()


    override fun getPlugin(id: String): Plugin? = pluginMap[id]

    @Synchronized
    override fun close() {
        for (plugin in pluginMap.values) {
            plugin.pluginLoader.close()
        }
        pluginMap.clear()
    }

    @Synchronized
    override fun simbotClose(context: SimbotContext) = close()
}