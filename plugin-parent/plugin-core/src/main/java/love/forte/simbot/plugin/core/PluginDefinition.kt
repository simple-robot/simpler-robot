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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.file.Path
import java.nio.file.WatchEvent
import java.nio.file.WatchKey
import java.nio.file.WatchService
import kotlin.io.path.Path
import kotlin.io.path.name


/**
 *
 * 插件定义，其定义一个插件本体实例。
 *
 * @author ForteScarlet
 */
public interface PluginDefinition {

    /**
     * 这个插件的标识ID，一般来讲就是 [main] 的 [name][Path.getName]。
     */
    val id: String get() = main.name

    /**
     * 这个插件的主体所在目录。
     */
    val main: Path

    /**
     * 这个插件的主体Jar File所在位置。
     */
    val mainFile: Path

    /**
     * 这个插件所对应的 *独有的* lib目录。
     */
    val libraries: Path
}


/**
 *
 * [PluginDefinition] 基于 [FileWithTemporarySubstitute] 的实现。
 *
 */
public class PluginDefinitionWithTemporarySubstitute(
    /**
     * 插件的根目录，例如 `xxx/plugin`
     */
    root: Path,
    mainPath: Path,
    mainFilePath: Path,
    librariesPath: Path,
) : PluginDefinition {
    override val main: Path = mainPath

    private val logger: Logger = LoggerFactory.getLogger("plugin:${main.name}")
    internal val tempMainFile: PathWithTemporarySubstitute
    override val mainFile: Path get() = tempMainFile.temporarySubstitute
    internal val tempLibraries: PathWithTemporarySubstitute
    override val libraries: Path get() = tempLibraries.temporarySubstitute

    init {
        val tempRoot = root.resolveSibling(".${root.name}") //.parent / Path()
        tempMainFile = mainFilePath.withTemporarySubstitute(root, tempRoot)
        tempLibraries = librariesPath.withTemporarySubstitute(root, tempRoot)
        tempMainFile.cleanTemp()
        tempLibraries.cleanTemp()
        tempMainFile.sync()
        tempLibraries.sync()
    }

    @Synchronized
    fun sync(main: Boolean, lib: Boolean) {
        if (main) {
            tempMainFile.sync()
            logger.debug("Main file sync.")
        }
        if (lib) {
            tempLibraries.sync()
            logger.debug("Lib sync.")
        }
    }

    /**
     * 注册一个针对于 [mainFile] 的文件监听
     * 这里实际上注册的是 `mainFile.parent` 的文件夹监听，需要判断变更文件是否为 [mainFile] 本身
     */
    fun watchMainFile(watchService: WatchService, vararg events: WatchEvent.Kind<*>): WatchKey {
        return tempMainFile.realPath.parent.register(watchService, *events)
    }


    /**
     * 注册一个针对于 [libraries] 的文件目录变动监听
     */
    fun watchLibraries(watchService: WatchService, vararg events: WatchEvent.Kind<*>): WatchKey {
        return tempLibraries.realPath.register(watchService, *events)
    }
}
