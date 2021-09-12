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

import kotlinx.coroutines.*
import java.io.Closeable
import java.nio.file.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.name


private class WatchFrequency(val frequency: Long) : CoroutineContext.Element {
    companion object Key : CoroutineContext.Key<WatchFrequency>

    override val key: CoroutineContext.Key<*> get() = Key
}


/**
 *
 * 插件内容发生变动的回调
 *
 * @author ForteScarlet
 */
@Suppress("MemberVisibilityCanBePrivate", "unused", "unused")
public abstract class PluginAlterationObserver(
    plugin: PluginDefinitionWithTemporarySubstitute,
    coroutineContext: CoroutineContext = EmptyCoroutineContext,
    watchService: WatchService,
    vararg events: WatchEvent.Kind<Path>,
) : Closeable, CoroutineScope {

    override val coroutineContext: CoroutineContext = coroutineContext + WatchFrequency(100)

    val mainFileWatchKey: WatchKey
    val libWatchKey: WatchKey
    private val job: Job

    init {
        mainFileWatchKey = plugin.watchMainFile(watchService, *events)
        libWatchKey = plugin.watchLibraries(watchService, *events)
        job = launch {
            // mainFileWatch
            launch {
                val frequency = coroutineContext[WatchFrequency]?.frequency ?: 100
                while (isActive) {
                    val mainEvents: List<WatchEvent<*>> =
                        mainFileWatchKey.pollEvents().also { mainFileWatchKey.reset() }
                    mainEvents.forEach { e ->
                        val context = e.context() as Path
                        if (context.name == plugin.mainFile.name) {
                            when (e.kind()) {
                                StandardWatchEventKinds.ENTRY_CREATE -> {
                                    // main created
                                    onMainCreated(context)
                                }
                                StandardWatchEventKinds.ENTRY_MODIFY -> {
                                    // main modified
                                    onMainEdited(context)
                                }
                                StandardWatchEventKinds.ENTRY_DELETE -> {
                                    // main deleted
                                    onMainDeleted(context)
                                }
                            }
                        }
                    }
                    delay(frequency)
                }
            }

            // libWatch
            launch {
                val frequency = coroutineContext[WatchFrequency]?.frequency ?: 100
                while (isActive) {
                    val libEvents = libWatchKey.pollEvents().also { libWatchKey.reset() }

                    // // 如果先是 reduce 然后是　increase, 那么就是lib内某文件的文件名修改
                    // if (libEvents.size == 2) {
                    //     val event1 = libEvents[0]
                    //     val context1 = plugin.tempLibraries.realPath / event1.context() as Path
                    //     val event2 = libEvents[1]
                    //     val context2 = plugin.tempLibraries.realPath / event2.context() as Path
                    //
                    // }


                        libEvents.forEach { e ->
                            val context = e.context() as Path
                            // println("kind: ${e.kind()}")
                            // println("context.name: ${context.name}")
                            // println("plugin.libraries.name: ${plugin.libraries.name}")
                            // println("context.isDirectory(): ${context.isDirectory()}")
                            // println("context.isRegularFile(): ${context.isRegularFile()}")
                            when (e.kind()) {
                                StandardWatchEventKinds.ENTRY_CREATE -> {
                                    val realFile = plugin.tempLibraries.realPath / context
                                    // dir
                                    if (realFile.isDirectory()) {
                                        // Is dir, do nothing.
                                        // lib created.
                                        // onLibCreated(context)
                                    } else {
                                        // New file created.
                                        onLibIncrease(plugin.tempLibraries.realPath, realFile)
                                    }
                                }
                                StandardWatchEventKinds.ENTRY_MODIFY -> {
                                    // 直接删目录
                                    /*
                                        kind: ENTRY_MODIFY
                                        context.name: plugins-core-0.1-SNAPSHOT.jar
                                        plugin.libraries.name: libs
                                        context.isDirectory(): false
                                        context.isRegularFile(): false


                                        kind: ENTRY_MODIFY
                                        context.name: plugins-core-0.1-SNAPSHOT.jar
                                        plugin.libraries.name: libs
                                        context.isDirectory(): false
                                        context.isRegularFile(): false
                                     */
                                    if (plugin.tempLibraries.realPath.exists()) {
                                        onLibCreated(plugin.tempLibraries.realPath)
                                    } else {
                                        // onLibEdited(plugin.tempLibraries.realPath, plugin.tempLibraries.realPath)
                                        onLibDeleted(plugin.tempLibraries.realPath)
                                    }
                                    // modify
                                    // if (context.isDirectory() && context.isSameFileAs(plugin.libraries)) {
                                    //     // lib created.
                                    //     // onLibCreated(context)
                                    // } else
                                    // if (context.isRegularFile()) {
                                    //     // new file created.
                                    //     onLibEdited(plugin.tempLibraries.realPath, context)
                                    // }
                                }
                                StandardWatchEventKinds.ENTRY_DELETE -> {
                                    val realFile = plugin.tempLibraries.realPath / context
                                    if (realFile.isDirectory()) {
                                        // Is dir, do nothing.
                                    } else {
                                        // new file created.
                                        onLibReduce(plugin.tempLibraries.realPath, realFile)
                                    }
                                }
                            }
                        }
                    delay(frequency)
                }
            }

        }

    }

    protected abstract fun onMainCreated(main: Path)

    protected abstract fun onMainEdited(main: Path)

    protected abstract fun onMainDeleted(main: Path)

    // // // // // // // // // // // // //

    protected abstract fun onLibCreated(lib: Path)

    protected abstract fun onLibReduce(lib: Path, reduced: Path)

    protected abstract fun onLibEdited(lib: Path, edited: Path)

    protected abstract fun onLibIncrease(lib: Path, increased: Path)

    protected abstract fun onLibDeleted(lib: Path)


    override fun close() {
        job.cancel()
        mainFileWatchKey.cancel()
        libWatchKey.cancel()
    }

}


@Target(AnnotationTarget.PROPERTY, AnnotationTarget.FUNCTION)
@DslMarker
public annotation class PluginAlterationObserverBuilderDSL

@Suppress("unused")
public class PluginAlterationObserverBuilder(
    private val plugin: PluginDefinitionWithTemporarySubstitute,
    private val coroutineContext: CoroutineContext = EmptyCoroutineContext,
    private val watchService: WatchService,
    private val events: Array<WatchEvent.Kind<Path>>,
) {

    private var onMainCreated: (main: Path) -> Unit = {}
    private var onMainEdited: (main: Path) -> Unit = {}
    private var onMainDeleted: (main: Path) -> Unit = {}
    private var onLibCreated: (lib: Path) -> Unit = {}
    private var onLibReduce: (lib: Path, reduced: Path) -> Unit = { _, _ -> }
    private var onLibEdited: (lib: Path, edited: Path) -> Unit = { _, _ -> }
    private var onLibIncrease: (lib: Path, increased: Path) -> Unit = { _, _ -> }
    private var onLibDeleted: (lib: Path) -> Unit = {}

    @PluginAlterationObserverBuilderDSL
    fun onMainCreated(onMainCreated: (main: Path) -> Unit) {
        this.onMainCreated = onMainCreated
    }

    @PluginAlterationObserverBuilderDSL
    fun onMainEdited(onMainEdited: (main: Path) -> Unit) {
        this.onMainEdited = onMainEdited
    }

    @PluginAlterationObserverBuilderDSL
    fun onMainDeleted(onMainDeleted: (main: Path) -> Unit) {
        this.onMainDeleted = onMainDeleted
    }

    @PluginAlterationObserverBuilderDSL
    fun onLibCreated(onLibCreated: (lib: Path) -> Unit) {
        this.onLibCreated = onLibCreated
    }

    @PluginAlterationObserverBuilderDSL
    fun onLibReduce(onLibReduce: (lib: Path, reduced: Path) -> Unit) {
        this.onLibReduce = onLibReduce
    }

    @PluginAlterationObserverBuilderDSL
    fun onLibEdited(onLibEdited: (lib: Path, edited: Path) -> Unit) {
        this.onLibEdited = onLibEdited
    }

    @PluginAlterationObserverBuilderDSL
    fun onLibIncrease(onLibIncrease: (lib: Path, increased: Path) -> Unit) {
        this.onLibIncrease = onLibIncrease
    }

    @PluginAlterationObserverBuilderDSL
    fun onLibDeleted(onLibDeleted: (lib: Path) -> Unit) {
        this.onLibDeleted = onLibDeleted
    }


    fun build() = object : PluginAlterationObserver(plugin, coroutineContext, watchService, *events) {
        override fun onMainCreated(main: Path) = onMainCreated.invoke(main)
        override fun onMainEdited(main: Path) = onMainEdited.invoke(main)
        override fun onMainDeleted(main: Path) = onMainDeleted.invoke(main)
        override fun onLibCreated(lib: Path) = onLibCreated.invoke(lib)
        override fun onLibReduce(lib: Path, reduced: Path) = onLibReduce.invoke(lib, reduced)
        override fun onLibEdited(lib: Path, edited: Path) = onLibEdited.invoke(lib, edited)
        override fun onLibIncrease(lib: Path, increased: Path) = onLibIncrease.invoke(lib, increased)
        override fun onLibDeleted(lib: Path) = onLibDeleted.invoke(lib)
    }
}

