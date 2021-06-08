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
        val e = "bots/*.bot"

        val p = Path(".")

        // val ep = Path(e)


        val matchList = e.split('\\', '/').map {
            when {
                it == "**" -> "((?![\\.;]).)+"
                it.contains("**") -> throw IllegalArgumentException("**Can not match other characters.")
                it.contains("*") -> it.replace("*", "((?![/\\\\\\.;]).)+")
                it.contains("?") -> it.replace("?", "((?![/\\\\\\.;]).)")
                else -> it
            }
        }

        val regex = Regex(e.replace(".", "\\.")
            .replace("*", "((?![/\\\\\\.;]).)+")
            .replace("**", "((?![\\.;]).)+")
        )

        // ** -> 任意路径，不能有其他
        // *  -> 任意内容，可以有其他字符
        // ?  -> 任意字符

        println(regex)

        println(regex.matches("bots/forte.bot"))
        println(regex.matches("bots/and/forte.bot"))
        println(regex.matches("bots/forli.bot"))

        println(p)
        println(p.toRealPath())

        println(p.nameCount)
        println(p.toRealPath().nameCount)


        println(p.toRealPath().name)
        println(p.toRealPath().getName(0))

        Files.walkFileTree(p.normalize(), MyFileVisitor(p))

        // val l = Files.find(p, 3, { pp, attr ->
        //     println(pp)
        //     println(pp.toRealPath())
        //
        //     println(attr.fileKey())
        //     true
        // }).toList()
        //
        // println("----")
        //
        // l.forEach { println(it) }

    }






}

internal class MyFileVisitor(root: Path) : SimpleFileVisitor<Path>() {

    // 如果没有 ** ，则file通过正数索引来判断目录规范并匹配，然后判断文件本身
    // 如果有 **，则从 ** 位向后由整数转为倒数
    // 如果有多个 ** TODO

    /** root path */
    private val rootPath = root.toRealPath(LinkOption.NOFOLLOW_LINKS).normalize()

    /** root的起始name索引 */
    private val rootNameIndex = rootPath.nameCount

    // **/*.bot

    override fun preVisitDirectory(dir: Path?, attrs: BasicFileAttributes?): FileVisitResult {
        requireNotNull(dir)
        requireNotNull(attrs)


        println(" --- ")
        println(dir)
        println(dir.normalize())
        println()

        return FileVisitResult.CONTINUE
    }

    override fun visitFile(file: Path?, attrs: BasicFileAttributes?): FileVisitResult {
        return super.visitFile(file, attrs)
    }

    override fun visitFileFailed(file: Path?, exc: IOException?): FileVisitResult {
        return super.visitFileFailed(file, exc)
    }

    override fun postVisitDirectory(dir: Path?, exc: IOException?): FileVisitResult {
        return super.postVisitDirectory(dir, exc)
    }
}