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

import java.io.Closeable
import java.nio.file.WatchKey


/**
 *
 * 插件内容发生变动的回调
 *
 * @author ForteScarlet
 */
@Suppress("MemberVisibilityCanBePrivate", "unused")
public abstract class PluginAlterationObserver(
    plugin: PluginDefinitionWithTemporarySubstitute
    // private val
) : Closeable {

    val mainWatchKey: WatchKey get() = TODO()
    val libWatchKey: WatchKey get() = TODO()

    protected abstract fun onMainCreated()

    protected abstract fun onMainEdited()

    protected abstract fun onMainDeleted()

    // // // // // // // // // // // // //

    protected abstract fun onLibCreated()

    protected abstract fun onLibEdited()

    protected abstract fun onLibReduce()

    protected abstract fun onLibIncrease()

    protected abstract fun onLibDeleted()


    override fun close() {
        mainWatchKey.cancel()
        libWatchKey.cancel()
    }

}