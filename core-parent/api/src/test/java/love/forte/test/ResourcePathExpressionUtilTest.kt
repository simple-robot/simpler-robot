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

package love.forte.test

import love.forte.simbot.utils.ResourcePathExpression
import love.forte.simbot.utils.readToProperties
import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.Path
import kotlin.io.path.name
import kotlin.test.Test


/**
 *
 * @author ForteScarlet
 */
class ResourcePathExpressionUtilTest {

    @Test
    fun classpathExpression1() {
        val e = "classpath:bots/this.bot"

        val expression = ResourcePathExpression.getInstance(e)

        println(expression)
        println(expression.expression)

        val resource = expression.getResources()[0]

        println(resource.readToProperties())


    }

    @Test
    fun classpathExpression2() {
        val e = "resource:bots/this2.bot"

        val expression = ResourcePathExpression.getInstance(e)

        println(expression)
        println(expression.expression)

        val resource = expression.getResources()[0]

        println(resource.readToProperties())

    }

    @Test
    fun fileExpression1() {
        val file = "file:test.bot"

        val expression = ResourcePathExpression.getInstance(file)

        println(expression)
        println(expression.expression)
        println(expression.type)

        val resource = expression.getResources()[0]

        println(resource.name)

        val prop = resource.readToProperties()

    }


    @Test
    fun expression2FindTest() {
        val e = "b*/**/*.bot"

        val p = Path(".")

        // val ep = Path(e)

        val first = e.indexOf("**")
        if (e.indexOf("**", first + 2) >= 0) {
            error("'**' Must only one.")
        }

        val preList = mutableListOf<Regex>()
        val postList = mutableListOf<Regex>()
        var finalFile: Regex? = null

        var pre = true
        var allMatch = false

        val splitList = e.split('\\', '/')
        splitList.forEachIndexed { index, it ->
            when {
                it == "**" -> {
                    pre = false
                    allMatch = true
                } // "((?![\\.;]).)+"
                it.contains("**") -> {
                    throw IllegalArgumentException("'**' Can not match with other characters.")
                }
                it.contains("*") -> {
                    val r = Regex(it.replace("*", "((?![/\\\\\\.;]).)+"))
                    if (index == splitList.lastIndex) {
                        finalFile = r
                    } else {
                        if (pre) {
                            preList.add(r)
                        } else {
                            postList.add(r)
                        }
                    }
                }
                it.contains("?") -> {
                    val r = Regex(it.replace("?", "((?![/\\\\\\.;]).)"))
                    if (index == splitList.lastIndex) {
                        finalFile = r
                    } else {
                        if (pre) {
                            preList.add(r)
                        } else {
                            postList.add(r)
                        }
                    }
                }
                else -> {
                    val r = Regex(it)
                    when {
                        index == splitList.lastIndex -> {
                            finalFile = r
                        }
                        pre -> {
                            preList.add(r)
                        }
                        else -> {
                            postList.add(r)
                        }
                    }
                }
                // it == "**" -> "((?![\\.;]).)+"
                // it.contains("**") -> throw IllegalArgumentException("'**' Can not match with other characters.")
                // it.contains("*") -> it.replace("*", "((?![/\\\\\\.;]).)+")
                // it.contains("?") -> it.replace("?", "((?![/\\\\\\.;]).)")
                // else -> it
            }
        }

        println(preList)
        println(postList)
        println(finalFile)

        println("-----")

        // matchList.forEach { println(it) }

        // ** -> 任意路径，不能有其他
        // *  -> 任意内容，可以有其他字符
        // ?  -> 任意字符

        // println(p)
        // println(p.toRealPath())
        //
        // println(p.nameCount)
        // println(p.toRealPath().nameCount)
        //
        //
        // println(p.toRealPath().name)
        // println(p.toRealPath().getName(0))

        val found = mutableListOf<Path>()

        fun addFound(path: Path) {
            found.add(path)
        }

        Files.walkFileTree(p.normalize(), MyFileVisitor(p, preList, postList, finalFile!!, ::addFound))

        println("finished.")

        for (path in found) {
            println(path)
        }


    }


}

internal class MyFileVisitor(
    root: Path,
    private val preList: List<Regex>,
    private val postList: List<Regex>,
    private val finalFile: Regex,
    private val onMatchFile: (Path) -> Unit
    // 中间是否存在全部匹配
    // private val allMatch: Boolean
) : SimpleFileVisitor<Path>() {

    // 如果没有 ** ，则file通过正数索引来判断目录规范并匹配，然后判断文件本身
    // 如果有 **，则从 ** 位向后由整数转为倒数
    // 如果有多个 ** -> 不允许

    /** root path */
    private val rootPath = root.toRealPath(LinkOption.NOFOLLOW_LINKS).normalize()

    /** root的起始name索引 */
    private val rootNameIndex = rootPath.nameCount

    // **/*.bot

    override fun preVisitDirectory(dir: Path?, attrs: BasicFileAttributes?): FileVisitResult {
        requireNotNull(dir)

        val normalizeDir = dir.toRealPath(LinkOption.NOFOLLOW_LINKS).normalize()

        if (normalizeDir == rootPath) {
            return FileVisitResult.CONTINUE
        }

        // 前缀匹配
        // println()
        // println(rootNameIndex)
        // println(normalizeDir.nameCount)
        // print("Get name: ")
        // for (i in preList.indices) {
        //     print(normalizeDir.getName(rootNameIndex + i))
        //     print("/")
        // }

        if (preList.isEmpty() && postList.isEmpty()) {
            return FileVisitResult.CONTINUE
        }
        // 如果后缀比nameCount还多，直接pass
        // println(normalizeDir)
        // println(normalizeDir.nameCount)
        // println("${normalizeDir.nameCount} < ${postList.size} + $rootNameIndex: ${normalizeDir.nameCount < postList.size + rootNameIndex}")
        if (normalizeDir.nameCount < postList.size + rootNameIndex) {
            return FileVisitResult.CONTINUE
        }

        // 前缀匹配
        for (i in preList.indices) {
            val fileName = normalizeDir.getName(rootNameIndex + i)
            val matcher = preList[i]

            val matched = matcher.matches(fileName.toString())
            // println("pre : '${matcher}'.matches('${fileName}'): ${matcher.matches(fileName.toString())}")
            if (!matched) {
                return FileVisitResult.SKIP_SUBTREE
            }
        }

        return FileVisitResult.CONTINUE
    }

    override fun visitFile(file: Path?, attrs: BasicFileAttributes?): FileVisitResult {
        requireNotNull(file)

        val normalizeFile = file.toRealPath(LinkOption.NOFOLLOW_LINKS).normalize()

        if (preList.isNotEmpty() && normalizeFile.parent == rootPath) {
            return FileVisitResult.CONTINUE
        }


        // println(normalizeFile)
        // println("Count: ${normalizeFile.nameCount}")
        val nameCount = normalizeFile.nameCount

        if (normalizeFile.nameCount < rootNameIndex + postList.size) {
            return FileVisitResult.CONTINUE
        }

        for (i in postList.indices) {
            // print(i)
            // print(" ")
            // -2 for skip real file name.
            val fileName = normalizeFile.getName(nameCount - 2 - i)
            val matcher = postList[postList.lastIndex - i]

            val matched = matcher.matches(fileName.toString())
            // println("$i-${rootNameIndex + i}: post: '${matcher}'.matches('${fileName}'): ${matcher.matches(fileName.toString())}")
            if (!matched) {
                return FileVisitResult.CONTINUE
            }
        }


        if (finalFile.matches(normalizeFile.name)) {
            onMatchFile(normalizeFile)
        }

        return FileVisitResult.CONTINUE
    }

    override fun visitFileFailed(file: Path?, exc: IOException?): FileVisitResult {
        return super.visitFileFailed(file, exc)
    }

    override fun postVisitDirectory(dir: Path?, exc: IOException?): FileVisitResult {
        return super.postVisitDirectory(dir, exc)
    }
}