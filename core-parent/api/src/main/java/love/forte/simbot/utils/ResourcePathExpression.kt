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

import java.io.FileNotFoundException
import kotlin.io.path.Path
import kotlin.io.path.div
import kotlin.io.path.exists


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

        fun getInstance(expression: String): ResourcePathExpression {
            return when {
                // classpath
                expression.startsWith("classpath:") || expression.startsWith("resource:") -> {
                    if (expression.contains("*")) {
                        TODO()
                    } else SingletonClasspathResourcePathExpression(expression, this::class.java.classLoader)
                }
                // file
                expression.startsWith("file:") -> {
                    if (expression.contains("*")) {
                        TODO()
                    } else SingletonFileResourcePathExpression(expression)
                }
                // BOTH?
                else -> {
                    TODO()
                }
            }
        }
    }
}


/**
 * Base abstract class for [ResourcePathExpression].
 */
internal abstract class BaseResourcePathExpression(override val expression: String): ResourcePathExpression {

    // TODO

    override fun toString(): String = "ResourcePathExpression(expression=$expression)"

}



/**
 * 只可能命中一个结果的（即不包含通配符的）资源表达式。
 */
internal sealed class SingletonResourcePathExpression(expression: String, override val type: String) : BaseResourcePathExpression(expression) {
    /**
     * 此表达式在一个根目录下所能够得到的资源路径。  如果为null，则目录即为当前工作目录。
     *
     * @return 能够找到的资源目录。
     */
    abstract fun getResource(root: String? = null): Resource
    override fun getResources(root: String?): List<Resource> = listOf(getResource(root))
}

/**
 * 本地文件的路径查询表达式
 */
internal class SingletonFileResourcePathExpression(expression: String) : SingletonResourcePathExpression(expression, ResourcePathExpression.TYPE_FILE) {

    // file:xxx.xxx -> xxx.xxx
    /**
     * 真正的表达式，即 `file:` 之后的内容。
     * 由于是单值表达式，因此表达式中不会存在 `*`, 而就是指代一个具体的文件。
     */
    private val realExpression = if (expression.startsWith("file:")) expression.substring(5) else throw IllegalStateException("Expression not starts with 'file:' : $expression")

    override fun getResource(root: String?): Resource {
        val path = root?.let { r -> Path(r) } ?: Path(".") / realExpression
        if (!path.exists()) {
            throw FileNotFoundException(path.toString())
        }

        return path.asResource()
    }
}

/**
 * 本地文件的路径查询表达式
 */
internal class SingletonClasspathResourcePathExpression(expression: String, private val classLoader: ClassLoader) : SingletonResourcePathExpression(expression, ResourcePathExpression.TYPE_FILE) {

    // file:xxx.xxx -> xxx.xxx
    /**
     * 真正的表达式，即 `classpath:` 或 `resource:` 之后的内容。
     * 由于是单值表达式，因此表达式中不会存在 `*`, 而就是指代一个具体的文件。
     */
    private val realExpression = when {
        expression.startsWith("classpath:") -> expression.substring(10)
        expression.startsWith("resource:") -> expression.substring(9)
        else -> throw IllegalArgumentException("Expression not starts with 'classpath:' or 'resource:' : $expression")
    }

    override fun getResource(root: String?): Resource {
        return classLoader.getResource(realExpression)?.asResource() ?: throw NullPointerException("No such resource '$realExpression'")
    }
}



/**
 * 可能命中多个结果的（即不包含通配符的）资源表达式。
 */
internal sealed class MutableResourcePathExpression(expression: String) : BaseResourcePathExpression(expression)



