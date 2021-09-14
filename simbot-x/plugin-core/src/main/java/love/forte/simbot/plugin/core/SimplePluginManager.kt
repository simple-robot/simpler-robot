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
import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.locks.ReadWriteLock
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


    override fun start() {
        // clean old


        val libJars = pluginGlobalLib
            .takeIf { it.exists() && it.isDirectory() }
            ?.useDirectoryEntries("*.jar") { it.map { p -> p.toUri().toURL() }.toList() } ?: emptyList()

        // globalLoader = URLClassLoader(libJars.toTypedArray(), parentLoader)
        // globalLoader = parentLoader //ThisFirstURLClassLoader(libJars.toTypedArray(), parentLoader)
        globalLoader = ThisFirstURLClassLoader(libJars.toTypedArray(), parentLoader)

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

        pluginDefinition.tempMainFile.temporarySubstitute.deleteIfExists()
        pluginDefinition.tempLibraries.temporarySubstitute.deleteDeep()
        pluginDefinition.sync(main = true, lib = true)

        val loader = PluginLoader(
            parent = globalLoader,
            plugin = pluginDefinition
        ) { _ ->
            var deleted: Plugin? = null
            onMainCreated {
                // 当Main Jar被创建
                logger.debug("Plugin {} onMainCreated: {}", pluginId, it.name)
                try {
                    deleted?.also { deletedPlugin ->
                        deletedPlugin.pluginLoader.resetLoaderBoth()
                        deletedPlugin.reset()
                    }?.let(::loadPlugin)

                    // reloadPlugin(pluginId, main = true, lib = false)
                } catch (e: Exception) {
                    logger.error("onMainCreated error", e)
                }
                logger.debug("Plugin {} onMainCreated event finished.", pluginId)
            }
            onMainEdited {
                // 当Main Jar被修改
                logger.debug("Plugin {} onMainEdited: {}", pluginId, it.name)
                try {
                    reloadPlugin(pluginId, main = true, lib = false)
                } catch (e: Exception) {
                    logger.error("onMainEdited error", e)
                }
                // reloadPlugin(pluginId)
                logger.debug("Plugin {} onMainEdited event finished.", pluginId)
            }
            onMainDeleted {
                // main deleted, remove listeners
                // listenerManager.removeListenerById()
                logger.debug("Plugin {} onMainDeleted: {}", pluginId, it.name)
                try {
                    deleted = unloadPlugin(pluginId)
                } catch (e: Exception) {
                    logger.error("onMainDeleted error", e)
                }
                logger.debug("Plugin {} onMainDeleted event finished.", pluginId)
            }

            onLibCreated {
                logger.debug("Plugin {} lib created: {}", pluginId, it.name)
                try {
                    // 如果是 lib, 那说明是内部有东西变更了，可以不在这个事件中处理
                    // if (it.name == pluginLibName) {
                    // ignore.
                    // }
                    reloadPlugin(pluginId, main = false, lib = true)
                } catch (e: Exception) {
                    logger.error("onLibCreated error", e)
                }
                logger.debug("Plugin {} lib created event finished.", pluginId)

            }
            onLibEdited { _, edited ->
                // Unknown event.
                logger.debug("Plugin {} lib edited: {}", pluginId, edited.name)
                try {
                    reloadPlugin(pluginId, main = false, lib = true)
                    // thisLoader.resetLoaderByLib()
                } catch (e: Exception) {
                    logger.error("onLibEdited error", e)
                }
                logger.debug("Plugin {} lib edited event finished.", pluginId)
            }
            onLibIncrease { _, increased ->
                logger.debug("Plugin {} lib increase: {}", pluginId, increased.name)
                try {
                    reloadPlugin(pluginId, main = false, lib = true)
                    // thisLoader.resetLoaderByLib()
                } catch (e: Exception) {
                    logger.error("onLibIncrease error", e)
                }
                logger.debug("Plugin {} lib increase event finished.", pluginId)
            }
            onLibReduce { _, reduced ->
                logger.debug("Plugin {} lib reduced: {}", pluginId, reduced.name)
                try {
                    reloadPlugin(pluginId, main = false, lib = true)
                    // thisLoader.resetLoaderByLib()
                } catch (e: Exception) {
                    logger.error("onLibReduce error", e)
                }
                logger.debug("Plugin {} lib reduced event finished.", pluginId)
            }
            onLibDeleted {
                logger.debug("Plugin {} lib deleted: {}", pluginId, it.name)
                try {
                    reloadPlugin(pluginId, main = false, lib = true)
                    // thisLoader.resetLoaderByLib()
                } catch (e: Exception) {
                    logger.error("onLibDeleted error", e)
                }
                logger.debug("Plugin {} lib deleted event finished.", pluginId)
            }

        }

        // val pluginDetails = loader.extractDetails()
        // val pluginInfo = pluginDetails.extractInformation()

        // Default do reset first
        return SimplePlugin(loader)
    }

    @Synchronized
    private fun loadPlugin(plugin: Plugin) {
        val pId = plugin.pluginInfo.id
        val pName = plugin.pluginInfo.name
        logger.info("Load plugin {}(ID={})", pName, pId)
        logger.debug("Plugin info: {}", plugin.pluginInfo)
        // val detailsClass = plugin.pluginInfo.pluginDetails
        val details = plugin.pluginDetails  // pluginLoader.loadClass(detailsClass).newInstance() as PluginDetails
        if (details is ListenerPluginDetails) {
            // 监听函数
            // val listeners = .map { WeakListenerFunction(it) }

            // Register listeners
            val listeners: Array<ListenerFunction> = details.getListeners().toTypedArray()
            if (logger.isDebugEnabled) {
                logger.debug("Plugin({}) listeners: {}", pId, listeners.joinToString(", ", "[", "]") { l -> l.name })
            }
            listenerManager.register(*listeners)
        }
        pluginMap.merge(plugin.id, plugin) { _, _ ->
            throw IllegalArgumentException("Plugin id ${plugin.id} already exists.")
        }

        // do init
        logger.debug("Init plugin({}) details.", pId)
        plugin.pluginDetails.init(dependBeanFactory)
        logger.debug("Init plugin({}) details finished.", pId)

        logger.debug("Start plugin({}) loader.", pId)
        plugin.pluginLoader.start()
        logger.debug("Plugin({}) loader started.", pId)

    }


    @Synchronized
    private fun unloadPlugin(id: String): Plugin? {
        logger.debug("Unload plugin {}", id)
        return pluginMap.remove(id)?.also {
            it.pluginLoader.closeLoader()
            logger.debug("Plugin's loader closed.")
            val details = it.pluginDetails
            if (details is ListenerPluginDetails) {
                listenerManager.listenerEditLock.write {
                    for (listener in details.listeners) {
                        val lisId = listener.id
                        listenerManager.removeListenerById(lisId)
                        logger.debug("Removed listener {}", lisId)
                    }
                }
            }
        }
    }


    @Suppress("SameParameterValue")
    @Synchronized
    private fun reloadPlugin(id: String, main: Boolean, lib: Boolean) {
        val needReload = pluginMap[id] ?: run {
            logger.debug("Reload plugin failed. Cannot found loaded plugin id called {}, skip", id)
            return
        }
        logger.debug("Reload plugin {} with main({}) | lib({})", id, main, lib)
        val details = needReload.pluginDetails
        if (details is ListenerPluginDetails) {
            listenerManager.listenerEditLock.write {
                for (listener in details.listeners) {
                    val lisId = listener.id
                    listenerManager.removeListenerById(listener.id)
                    logger.debug("Removed listener {}", lisId)
                }
            }
        }
        logger.debug("Reset plugin({})'s loader ", needReload.id)
        needReload.pluginLoader.resetLoader(main = main, lib = lib)
        logger.debug("Reset plugin({})'s loader finished.", needReload.id)

        pluginMap.remove(id)
        loadPlugin(needReload.also { it.reset() })
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


private inline fun <T> ReadWriteLock.write(block: () -> T): T {
    val lock = writeLock()
    lock.lock()
    try {
        return block()
    } finally {
        lock.unlock()
    }
}