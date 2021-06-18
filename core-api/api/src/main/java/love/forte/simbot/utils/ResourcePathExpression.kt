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

import java.io.IOException
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.exists
import kotlin.io.path.name


/**
 * 资源路径表达式解析器。
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
 * 资源路径表达式的结果可能会得到两种：一个是多个结果，一个是不存在通配符、只能得到一个结果的。
 *
 * @author ForteScarlet
 */
public interface ResourcePathExpression {

    /**
     * 具体的表达式本体.
     */
    val expression: String

    /**
     * 资源类型。一般为 `file` 或 `classpath`
     */
    val type: String


    /**
     * 此表达式在一个根目录下所能够得到的所有资源路径。  如果为null，则目录即为当前工作目录。
     *
     * @return 能够找到的资源目录列表。
     */
    fun getResources(root: String? = null): List<Resource>


    // internal class FileResourcePathExpression(expression: String) : ResourcePathExpression(expression)
    // internal class ClasspathResourcePathExpression(expression: String) : ResourcePathExpression(expression)
    // internal class BothResourcePathExpression(expression: String) : ResourcePathExpression(expression)


    companion object {

        const val TYPE_FILE = "file"
        const val TYPE_CLASSPATH = "classpath"
        const val TYPE_FILE_FIRST = "file1st"

        @JvmStatic
        fun getInstance(expression: String): ResourcePathExpression {
            return when {
                // classpath
                expression.startsWith("classpath:") ->
                    if (expression.contains("*")) {
                        TODO()
                    }
                    else SingletonClasspathResourcePathExpression(expression.substring(10),
                        this::class.java.classLoader)
                expression.startsWith("resource:") ->
                    if (expression.contains("*")) {
                        TODO()
                    }
                    else SingletonClasspathResourcePathExpression(expression.substring(9),
                        this::class.java.classLoader)
                // file
                expression.startsWith("file:") ->
                    if (expression.contains("*")) MutableFileResourcePathExpression(expression.substring(5))
                    else SingletonFileResourcePathExpression(expression.substring(5))
                // BOTH?
                else ->
                    if (expression.contains("*")) {
                        TODO()
                    }
                    else SingletonFileFirstResourcePathExpression(expression, this::class.java.classLoader)
            }
        }
    }
}


/**
 * Base abstract class for [ResourcePathExpression].
 */
internal abstract class BaseResourcePathExpression(override val expression: String) : ResourcePathExpression {
    override fun toString(): String = "ResourcePathExpression(expression=$expression)"
}


/**
 * 只可能命中一个结果的（即不包含通配符的）资源表达式。
 */
internal sealed class SingletonResourcePathExpression(expression: String, override val type: String) :
    BaseResourcePathExpression(expression) {
    /**
     * 此表达式在一个根目录下所能够得到的资源路径。  如果为null，则目录即为当前工作目录。
     *
     * @return 能够找到的资源目录。
     *
     * @throws NoSuchResourceException No such resource.
     */
    abstract fun getResource(root: String? = null): Resource
    override fun getResources(root: String?): List<Resource> = listOf(getResource(root))
}

/**
 * 本地文件的路径查询表达式
 */
internal class SingletonFileResourcePathExpression(expression: String) :
    SingletonResourcePathExpression(expression, ResourcePathExpression.TYPE_FILE) {

    override fun getResource(root: String?): Resource {
        val path = root?.let { r -> Path(r) } ?: Path(".") / expression
        if (!path.exists()) {
            throw NoSuchResourceException("file: ${path.toRealPath()}")
        }

        return path.asResource()
    }
}

/**
 * 本地文件的路径查询表达式
 */
internal class SingletonClasspathResourcePathExpression(expression: String, private val classLoader: ClassLoader) :
    SingletonResourcePathExpression(expression, ResourcePathExpression.TYPE_CLASSPATH) {

    override fun getResource(root: String?): Resource {
        return classLoader.getResource(expression)?.asResource()
            ?: throw NoSuchResourceException("resource: $expression")
    }
}


/**
 * 优先尝试通过文件获取的表达式.
 * 一般为开头没有指定的时候使用。
 */
internal class SingletonFileFirstResourcePathExpression(expression: String, private val classLoader: ClassLoader) :
    SingletonResourcePathExpression(expression, ResourcePathExpression.TYPE_FILE_FIRST) {
    override fun getResource(root: String?): Resource {
        val path = root?.let { r -> Path(r) } ?: Path(".") / expression
        if (path.exists()) {
            return path.asResource()
        }
        return classLoader.getResource(expression)?.asResource() ?: throw NoSuchResourceException(expression)

    }
}


/**
 * 可能命中多个结果的（即不包含通配符的）资源表达式。
 */
internal sealed class MutableResourcePathExpression(expression: String, override val type: String) :
    BaseResourcePathExpression(expression)


/**
 * 根据表达式匹配多个文件资源。
 */
internal class MutableFileResourcePathExpression(expression: String) :
    MutableResourcePathExpression(expression, ResourcePathExpression.TYPE_FILE) {


    private val preList: List<Regex>
    private val postList: List<Regex>
    private val finalFile: Regex

    // init expression data
    init {
        // check expression
        val preList = mutableListOf<Regex>()
        val postList = mutableListOf<Regex>()
        var finalFile: Regex? = null

        var pre = true

        val splitList = expression.split('\\', '/')
        splitList.forEachIndexed { index, it ->
            when {
                it == "**" -> {
                    pre = false
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
            }
        }

        this.preList = preList.ifEmpty { emptyList() }
        this.postList = postList.ifEmpty { emptyList() }
        this.finalFile = finalFile!!

    }


    override fun getResources(root: String?): List<Resource> {
        val rootPath = root?.let { Path(it) } ?: Path(".")

        val found = mutableListOf<Resource>()

        Files.walkFileTree(rootPath, ExpressionFileVisitor(rootPath, preList, postList, finalFile) { path ->
            found.add(path.asResource())
        })

        return found
    }


}


internal class ExpressionFileVisitor(
    root: Path,
    private val preList: List<Regex>,
    private val postList: List<Regex>,
    private val finalFile: Regex,
    private val onMatchFile: (Path) -> Unit,
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




