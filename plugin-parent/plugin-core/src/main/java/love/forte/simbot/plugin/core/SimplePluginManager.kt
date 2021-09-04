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
import love.forte.simbot.core.TypedCompLogger
import love.forte.simbot.listener.ListenerManager
import java.net.URLClassLoader
import java.nio.file.Path
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
    parentLoader: ClassLoader,

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

    ) : PluginManager {

    private companion object LOG : TypedCompLogger(SimplePluginManager::class.java)

    /**
     * 公共lib loader.
     */
    override val globalLoader: ClassLoader

    init {
        val libJars = pluginGlobalLib.useDirectoryEntries("*.jar") { it.map { p -> p.toUri().toURL() }.toList() }
        globalLoader = URLClassLoader(libJars.toTypedArray(), parentLoader)
    }

    /**
     * 扫描 [pluginRoot] 下的所有插件。
     */
    fun scanPlugins(): List<Plugin> {
        check(pluginRoot.isDirectory()) { "Plugin root $pluginRoot is not directory." }

        // 寻找所有的 dir
        pluginRoot.useDirectoryEntries { paths ->
            paths.filter { it.isDirectory() }
                .mapNotNull { pluginDir ->
                    val pluginId = pluginDir.name
                    // main
                    //   - main.jar
                    //   - lib
                    val pluginJar = pluginDir / Path("$pluginId.jar")
                    if (!pluginDir.exists()) {
                        logger.debug("Cannot found main plugin named {} in Plugin dir {}", "$pluginId.jar", pluginId)
                        return@mapNotNull null
                    }
                    val pluginLib = pluginDir / Path(pluginLibName)

                    logger.debug("Plugin ID(dir): {}", pluginId)
                    val pluginDefinition = PluginDefinitionWithTemporarySubstitute(
                        root = pluginRoot,
                        mainPath = pluginDir,
                        mainFilePath = pluginJar,
                        librariesPath = pluginLib,

                        )

                    PluginLoader(
                        parent = globalLoader,
                        plugin = pluginDefinition
                    ) {
                        onMainCreated {
                            // 当Main Jar被创建


                        }
                        onMainEdited {

                        }
                        onMainDeleted {
                            // main deleted, remove listeners
                            // listenerManager.removeListenerById()
                        }

                        onLibCreated { }
                        onLibEdited { lib, edited -> }
                        onLibIncrease { lib, increased -> }
                        onLibReduce { lib, reduced -> }
                        onLibDeleted { }

                    }


                    null
                }
        }


        TODO()

    }

    override val plugins: List<Plugin>
        get() = TODO("Not yet implemented")


    override fun getPlugin(id: String): Plugin {
        TODO("Not yet implemented")
    }
}