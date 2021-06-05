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

package love.forte.simbot.utils


/**
 * 资源路径表达式解析器工具。
 *
 * 资源路径表达式即为类似于：`classpath:\*.properties`、`file:log/\*\*.logs` 这样的表达式。
 *
 * 将这种表达式转化为正则并进行匹配解析。
 *
 * 其中：
 * - `*` 则代表一个任意的文件名称，其中不包含 `\`, 也就是说只允许一层目录。
 *  例如： `*.log` 则只能匹配到当前根目录下的所有 xxx.log 文件，但是得不到 `log/` 目录下的log文件。
 *
 *
 * - `**` 则包含 `\`，即允许多层级目录。
 *  例如：`**.log` 则可以得到根目录下的所有 `.log` 文件，也会去所有的子目录下寻找。
 *
 *
 * @author ForteScarlet
 *
 */
public object ResourcePathExpressionUtil {



}