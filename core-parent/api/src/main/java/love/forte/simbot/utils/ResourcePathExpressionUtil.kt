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


/**
 *
 * 针对于资源路径表达式的封装。
 *
 * 资源路径表达式的结果可能会得到两种：一个是多个结果，一个是不存在通配符、只能得到一个结果的。
 *
 */
public interface ResourcePathExpression {

    /**
     * 具体的表达式本体.
     */
    val expression: String

    /**
     * 资源类型。一般为 `file` 、`classpath`或者 `both`
     */
    val type: String


    /**
     * 此表达式在一个根目录下所能够得到的所有资源路径。  如果为null，则目录即为当前工作目录。
     *
     * @return 能够找到的资源目录列表。
     */
    fun getResourcePaths(root: String? = null): List<String>





    // internal class FileResourcePathExpression(expression: String) : ResourcePathExpression(expression)
    // internal class ClasspathResourcePathExpression(expression: String) : ResourcePathExpression(expression)
    // internal class BothResourcePathExpression(expression: String) : ResourcePathExpression(expression)


    companion object {
        fun getInstance(expression: String): ResourcePathExpression {
            return when {
                // classpath
                expression.startsWith("classpath:") -> {
                    TODO()
                }
                // file
                expression.startsWith("file:") -> {
                    TODO()
                }
                // BOTH
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
internal sealed class SingletonResourcePathExpression(expression: String) : BaseResourcePathExpression(expression) {
    /**
     * 此表达式在一个根目录下所能够得到的资源路径。  如果为null，则目录即为当前工作目录。
     *
     * @return 能够找到的资源目录。
     */
    abstract fun getResourcePath(root: String? = null): String
    override fun getResourcePaths(root: String?): List<String> = listOf(getResourcePath(root))
}




/**
 * 可能命中多个结果的（即不包含通配符的）资源表达式。
 */
internal sealed class MutableResourcePathExpression(expression: String) : BaseResourcePathExpression(expression)



