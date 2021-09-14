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

@file:JvmName("PluginLoaders")
@file:JvmMultifileClass
package love.forte.simbot.plugin.core

import kotlinx.coroutines.CoroutineScope
import java.io.Closeable
import java.io.InputStream
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import java.nio.file.WatchService
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.io.path.exists
import kotlin.io.path.useDirectoryEntries


/**
 *
 * 一个插件的类加载器.
 *
 * 插件的类加载器应当对应一个指定插件，包括其中的主体插件和专属依赖。
 *
 * 插件加载器是 [ClassLoader] 的一种，通过加载Jar文件来加载插件文件。
 * 需要做到当插件文件的内容发生变动的时候，类加载器也进行相应变化。
 *
 * 由于当更新的时候涉及到对类加载器以及其加载类的释放，尽可能避免对此类及其衍生类的强引用。
 *
 *
 * @author ForteScarlet
 */
public class PluginLoader(
    parent: ClassLoader = getSystemClassLoader(),
    override val coroutineContext: CoroutineContext = EmptyCoroutineContext,
    private val plugin: PluginDefinitionWithTemporarySubstitute,
    fileWatcher: WatchService = FileSystems.getDefault().newWatchService(),
    observerBuilderBlock: PluginAlterationObserverBuilder.(loader: PluginLoader) -> Unit,
) : CoroutineScope, ClassLoader(), Closeable {
    @Volatile
    private var _realLoader: URLClassLoader?

    private lateinit var observer: PluginAlterationObserver

    private val lock = ReentrantReadWriteLock()

    private inline fun <T> realLoader(block: (realLoader: URLClassLoader) -> T): T {
        return lock.read {
            _realLoader?.let(block) ?: throw IllegalStateException("Loader was closed.")
        }
    }

    private var startBlock: (() -> Unit)?

    init {
        val paths = mutableListOf<Path>()
        check(plugin.mainFile.exists()) { "Plugin main file ${plugin.tempMainFile.realPath} not exists." }
        paths.add(plugin.mainFile)
        if (plugin.libraries.exists()) {
            paths.addAll(plugin.libraries.useDirectoryEntries("*.jar") { it.toList() })
        }

        _realLoader = ThisFirstURLClassLoader(paths.map { it.toUri().toURL() }.toTypedArray(), parent)
        // _realLoader = URLClassLoader(paths.map { it.toUri().toURL() }.toTypedArray(), parent)

        startBlock = {
            observer = PluginAlterationObserverBuilder(plugin, coroutineContext, fileWatcher,
                arrayOf(
                    StandardWatchEventKinds.ENTRY_CREATE,
                    StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY,
                )
            ).also { builder -> observerBuilderBlock(builder, this) }.build()
        }


        // edit:
        // 1 close loader
        // 2 remove listener?
        // 3 load new loader
        // 4 add new listener?

    }

    /**
     * 启用文件监听
     */
    fun start() {
        startBlock?.invoke()?.also {
            startBlock = null
        }
    }

    override fun loadClass(name: String?): Class<*> = realLoader { it.loadClass(name) }
    override fun getResource(name: String?): URL? = realLoader { it.getResource(name) }
    override fun getResources(name: String?): Enumeration<URL> = realLoader { it.getResources(name) }
    override fun getResourceAsStream(name: String?): InputStream? = realLoader { it.getResourceAsStream(name) }
    override fun setDefaultAssertionStatus(enabled: Boolean) = realLoader { it.setDefaultAssertionStatus(enabled) }
    override fun setPackageAssertionStatus(packageName: String?, enabled: Boolean) = realLoader { it.setPackageAssertionStatus(packageName, enabled) }
    override fun setClassAssertionStatus(className: String?, enabled: Boolean) = realLoader { it.setClassAssertionStatus(className, enabled) }
    override fun clearAssertionStatus() = realLoader { it.clearAssertionStatus() }


    fun resetLoader(main: Boolean, lib: Boolean) {
        lock.write {
            closeLoader()

            plugin.sync(main, lib)

            val paths = mutableListOf<Path>()
            if (plugin.mainFile.exists()) {
                paths.add(plugin.mainFile)
            }
            if (plugin.libraries.exists()) {
                paths.addAll(plugin.libraries.useDirectoryEntries("*.jar") { it.toList() })
            }
            _realLoader = ThisFirstURLClassLoader(paths.asSequence().filter { it.exists() }.map { it.toUri().toURL() }.toList()
                .toTypedArray(), parent)
        }
    }

    /**
     * 仅仅只是关闭 [_realLoader] 以使得释放URL。
     * 使用后需要通过 [resetLoader] 来重新加载。
     */
    fun closeLoader() {
        _realLoader?.close().also { _realLoader = null }
    }


    override fun close() {
        lock.write {
            observer.close()
            _realLoader?.close().also { _realLoader = null }
        }
    }

}


public fun PluginLoader.resetLoaderByMain() = resetLoader(main = true, lib = false)
public fun PluginLoader.resetLoaderByLib() = resetLoader(main = false, lib = true)
public fun PluginLoader.resetLoaderBoth() = resetLoader(main = true, lib = true)

