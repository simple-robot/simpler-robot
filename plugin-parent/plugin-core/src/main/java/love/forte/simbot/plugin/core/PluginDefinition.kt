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

import java.nio.file.Path
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
public class PluginDefinitionWithTemporarySubstitute : PluginDefinition {
    override val main: Path
        get() = TODO("Not yet implemented")
    override val mainFile: Path
        get() = TODO("Not yet implemented")
    override val libraries: Path
        get() = TODO("Not yet implemented")
}
