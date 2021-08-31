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


@file:JvmName("FileListeners")
@file:JvmMultifileClass
package love.forte.simbot.plugin.core

import java.io.Closeable
import java.nio.file.Path


/**
 * 文件监听的类型选项，即你要监听那些类型。
 */
@JvmInline
value class ListenOption(val option: Int) {

    val isCreate: Boolean get() = FileAlterationListener.OPTION_FILE_CREATE in this
    val isEdit: Boolean get() = FileAlterationListener.OPTION_FILE_EDIT in this
    val isDelete: Boolean get() = FileAlterationListener.OPTION_FILE_DELETE in this
    operator fun contains(option: Int): Boolean = this.option and option != 0

    companion object {
        val all get() = ListenOption(FileAlterationListener.OPTION_ALL)
        val create get() = ListenOption(FileAlterationListener.OPTION_FILE_CREATE)
        val edit get() = ListenOption(FileAlterationListener.OPTION_FILE_EDIT)
        val delete get() = ListenOption(FileAlterationListener.OPTION_FILE_DELETE)
    }
}


internal operator fun ListenOption.plus(other: ListenOption): ListenOption = ListenOption(option or other.option)
internal operator fun ListenOption.minus(other: ListenOption): ListenOption = ListenOption(option xor other.option)



/**
 * 文件变更监听器.
 */
interface FileAlterationListener : Closeable {
    /**
     * 追加一个观察者
     */
    fun addObserver(observer: FileAlterationObserver)

    /**
     * 移除一个观察者
     */
    fun removeObserver(observer: FileAlterationObserver)

    /**
     * 追加一个文件的监听.
     *
     * e.g.
     * ```java
     * addFileListen(path, FileAlterationListener.OPTION_FILE_EDIT);
     *
     * addFileListen(path, FileAlterationListener.options(FileAlterationListener.OPTION_FILE_CREATE, FileAlterationListener.OPTION_FILE_EDIT));
     * ```
     *
     */
    fun addFileListen(file: Path, option: Int)


    /**
     * 移除一个文件的监听.
     */
    fun removeFileListen(file: Path)

    /**
     * 移除一个文件某类型的监听.
     */
    fun removeFileListen(file: Path, option: Int)

    /**
     * 当[文件监听器][FileAlterationListener]被关闭的时候的时候，
     * 将无法再使用变更操作: [addObserver]、[removeObserver]、[addFileListen]、[removeFileListen]
     *
     */
    override fun close()


    companion object Options {
        const val OPTION_FILE_CREATE = 0b0001
        const val OPTION_FILE_EDIT =   0b0010
        const val OPTION_FILE_DELETE = 0b0100
        const val OPTION_ALL = 0b0111

        fun options(vararg options: Int): Int {
            var option = ListenOption(0)
            for (o in options) {
                option += ListenOption(o)
            }
            return option.option
        }
    }

}


/**
 * 文件变更观察器, 通过 [FileAlterationListener] 来针对某个文件的监听。
 *
 */
interface FileAlterationObserver {

    /**
     * 文件被创建的回调
     */
    fun onFileCreate(file: Path)

    /**
     * 文件被修改的回调
     */
    fun onFileEdit(file: Path)

    /**
     * 文件被删除的回调
     */
    fun onFileDelete(file: Path)

    /**
     * 当[文件监听器][FileAlterationListener]被关闭的时候被调用。
     * 监听器被关闭的时候，将无法再使用变更操作，例如追加文件监听等。
     */
    fun onListenerClosed(listener: FileAlterationListener)
}



/**
 * 主要的文件变更观察器, 此监听器的主要作用是用于 [FileAlterationListener] 中的文件发生变更后需要执行的事情，
 * 每个 [FileAlterationListener] 只应存在一个 [MainFileAlterationObserver]
 *
 */
interface MainFileAlterationObserver {
    /**
     * 文件被创建的回调.
     * @param pusher 需要执行此函数来推送本次事件给所有的监听器。
     */
    fun onFileCreate(file: Path, pusher: () -> Unit)

    /**
     * 文件被修改的回调
     * @param pusher 需要执行此函数来推送本次事件给所有的监听器。
     */
    fun onFileEdit(file: Path, pusher: () -> Unit)

    /**
     * 文件被删除的回调
     * @param pusher 需要执行此函数来推送本次事件给所有的监听器。
     */
    fun onFileDelete(file: Path, pusher: () -> Unit)

    companion object Default : MainFileAlterationObserver {
        override fun onFileCreate(file: Path, pusher: () -> Unit) {
            pusher()
        }

        override fun onFileEdit(file: Path, pusher: () -> Unit) {
            pusher()
        }

        override fun onFileDelete(file: Path, pusher: () -> Unit) {
            pusher()
        }
    }

}