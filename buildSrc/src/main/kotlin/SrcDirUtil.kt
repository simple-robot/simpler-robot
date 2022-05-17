/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

import org.gradle.api.Project
import java.io.File

fun Project.src(base: String) = File(projectDir, "src/$base")

fun File.kt(name: String) = File(this, "$name/kotlin")
fun File.resources(name: String) = File(this, "$name/resources")

val File.main get() = kt("main")
val File.test get() = kt("test")

val File.mains get() = listOf(main)
val File.tests get() = listOf(test)

val File.resources get() = resources("main")
val File.resourcesList get() = listOf(resources)

fun Project.srcMain(base: String) = src(base).main
fun Project.srcTest(base: String) = src(base).test

fun Project.srcMains(base: String) = listOf(srcMain(base))
fun Project.srcTests(base: String) = listOf(srcTest(base))


fun Project.src(source: String, base: String) = src(base).kt(source)
fun Project.resources(source: String, base: String) = src(base).resources(source)

fun Project.srcList(source: String, base: String) = listOf(src(source, base))
fun Project.resourcesList(source: String, base: String) = listOf(resources(source, base))


data class TargetAndSource(val target: String, val source: String)


fun String.toTargetAndSource(): TargetAndSource {
    val pre = this.substring(0, length - 4)
    val end = this.substring(length - 4)

    return TargetAndSource(pre, end.toLowerCase())
}
