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
import java.io.File
import java.nio.file.Path
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.io.path.Path
import kotlin.io.path.isSameFileAs


/**
 * 用于 [SimpleFileAlterationListener] 的参数。
 *
 * ```java
 * WatchPath("lib/xxx.jar", FileAlterationListener.OPTION_ALL)
 * WatchPath("lib/xxx.jar")
 * WatchPath(Paths.get("lib/xxx.jar"), FileAlterationListener.OPTION_ALL)
 * WatchPath(Paths.get("lib/xxx.jar"))
 * ```
 *
 * ```kotlin
 * WatchPath("lib/xxx.jar", ListenOption.edit)
 * WatchPath("lib/xxx.jar")
 * WatchPath(Path("lib/xxx.jar"), ListenOption.edit + ListenOption.create)
 * WatchPath(Path("lib/xxx.jar"))
 *
 * ```
 *
 */
data class WatchPath @JvmOverloads constructor(val path: Path, val option: Int = FileAlterationListener.OPTION_ALL) {
    @JvmOverloads
    constructor(path: String, option: Int = FileAlterationListener.OPTION_ALL) : this(Path(path), option)
}


/**
 *
 * 基础的 [FileAlterationListener] 实现。
 *
 * @param observationFrequency 观测频率，即每多少毫秒检测一次对应的文件
 *
 * @author ForteScarlet
 */
class SimpleFileAlterationListener(
    watchFiles: List<WatchPath> = emptyList(),
    private val observers: MutableSet<FileAlterationObserver> = mutableSetOf(),
    private val mainObserver: MainFileAlterationObserver = MainFileAlterationObserver,
    private val observationFrequency: Long = 100,
    _coroutineContext: CoroutineContext = EmptyCoroutineContext,
) : FileAlterationListener, CoroutineScope {


    override val coroutineContext: CoroutineContext =
        if (_coroutineContext[Job] != null) _coroutineContext else _coroutineContext + Job()

    /**
     * 检测的文件列表.
     */
    private val watching: MutableList<Watch> =
        // TODO 文件监听 和 目录监听
        watchFiles.map { FileWatch(it.path, ListenOption(it.option)) }.toMutableList() // listOf<Watch>()

    // private val observers = mutableSetOf<FileAlterationObserver>()
    private val observerLock = ReentrantReadWriteLock()

    @Suppress("NOTHING_TO_INLINE")
    private inline fun checkActive() {
        if (!isActive) {
            throw IllegalStateException("Already closed.")
        }
    }

    override fun addObserver(observer: FileAlterationObserver) {
        checkActive()
        observerLock.write {
            observers.add(observer)
        }
    }

    override fun removeObserver(observer: FileAlterationObserver) {
        checkActive()
        observerLock.write {
            observers.remove(observer)
        }
    }


    @Synchronized
    override fun addFileListen(file: Path, option: Int) {
        checkActive()
        val foundWatch = watching.find { it.path.isSameFileAs(file) }
        if (foundWatch == null) {
            watching.add(FileWatch(file, ListenOption(option)))
        } else {
            TODO("Not yet implemented")
        }
    }

    @Synchronized
    override fun removeFileListen(file: Path, option: Int) {
        checkActive()
        TODO("Not yet implemented")
    }

    @Synchronized
    override fun removeFileListen(file: Path) {
        checkActive()
        watching.removeIf { it.path.isSameFileAs(file) }
    }

    override fun close() {
        this.cancel()
        observerLock.read {
            for (observer in observers) {
                observer.onListenerClosed(this)
            }
        }
    }

    private fun onCreate(path: Path) {
        mainObserver.onFileCreate(path) {
            observerLock.read {
                for (observer in observers) {
                    observer.onFileCreate(path)
                }
            }
        }
    }

    private fun onEdit(path: Path) {
        mainObserver.onFileCreate(path) {
            observerLock.read {
                for (observer in observers) {
                    observer.onFileEdit(path)
                }
            }
        }
    }

    private fun onDelete(path: Path) {
        mainObserver.onFileCreate(path) {
            observerLock.read {
                for (observer in observers) {
                    observer.onFileDelete(path)
                }
            }
        }
    }

    internal interface Watch {
        val path: Path
        val option: ListenOption
    }

    internal inner class FileWatch(
        override val path: Path,
        @Volatile
        override var option: ListenOption,
    ) : Watch, Closeable {
        private val file: File = path.toFile()

        var createJob: Job? = null
        var deleteJob: Job? = null
        var editJob: Job? = null

        lateinit var subScope: CoroutineScope

        private val job: Job = this@SimpleFileAlterationListener.launch {
            subScope = this
            if (option.isCreate || option.isDelete) {
                // 上一次是否存在
                if (option.isCreate) {
                    createJob = createListen()
                }
                if (option.isDelete) {
                    createJob = deleteListen()
                }
            }

            if (option.isEdit) {
                editJob = editListen()
            }
        }


        private fun CoroutineScope.createListen(): Job {
            var lastExist = file.exists()
            return launch {
                while (isActive) {
                    delay(observationFrequency)
                    val last = lastExist
                    val nowExist = file.exists().also { lastExist = it }
                    // 如果上次检测还不存在但是现在存在了
                    if (!last && nowExist) {
                        onCreate(path)
                    }
                }
            }
        }

        private fun CoroutineScope.deleteListen(): Job {
            var lastExist = file.exists()
            return launch {
                while (isActive) {
                    delay(observationFrequency)
                    val last = lastExist
                    val nowExist = file.exists().also { lastExist = it }
                    // 如果上次检测存在但是现在不存在了
                    // println("last: $last")
                    // println("nowExist: $nowExist")
                    // println("last && !nowExist: ${last && !nowExist}")
                    if (last && !nowExist) {
                        onDelete(path)
                    }
                }
            }
        }

        private fun CoroutineScope.editListen(): Job {
            // 上一次的最终修改时间
            var lastModifierTime = file.lastModified()

            return launch {
                while (isActive) {
                    delay(observationFrequency)
                    val last = lastModifierTime
                    val nowModifierTime = file.lastModified().also { lastModifierTime = it }
                    if (last == 0L || nowModifierTime == 0L) {
                        continue
                    }
                    if (last != nowModifierTime) {
                        onEdit(path)
                    }
                }
            }
        }


        override fun close() {
            job.cancel()
        }
    }
}


suspend fun SimpleFileAlterationListener.join() {

    coroutineContext[Job]?.join()
}