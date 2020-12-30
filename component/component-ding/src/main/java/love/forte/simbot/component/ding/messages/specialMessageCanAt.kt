/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  component-ding
 * File     specialMessageCanAt.kt
 * Date  2020/8/8 上午12:02
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.component.ding.messages

/*
    此文件定义那些可以与at类型相结合的消息类型
    text
    markdown
 */


/**
 * 钉钉的At类型的消息。
 * 似乎只有在text和markdown类型的时候可以使用at
 * ```
 *  "at": {
 *      "atMobiles": [
 *      "150XXXXXXXX"
 *      ],
 *      "isAtAll": false
 *  }
```
 */
data class DingAt
@JvmOverloads
constructor(val atMobiles: Array<String>, val isAtAll: Boolean = false): BaseNormalDingSpecialMessage<DingAt>("at") {

    companion object {
        @JvmStatic
        val atAll = DingAt(arrayOf(), true)
        @JvmStatic
        val empty = DingAt(arrayOf())
    }


    /**
     * 一共就俩参数, 要么是`atMobiles`, 要么是`isAtAll`，其他都是null
     */
    override operator fun get(key: String): Any? {
        return when (key) {
            "atMobiles" -> atMobiles
            "isAtAll" -> isAtAll
            else -> null
        }
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DingAt

        if (!atMobiles.contentEquals(other.atMobiles)) return false
        if (isAtAll != other.isAtAll) return false

        return true
    }

    override fun hashCode(): Int {
        var result = atMobiles.contentHashCode()
        result = 31 * result + isAtAll.hashCode()
        return result
    }

    /**
     * 合并两个[DingAt]
     */
    override fun doPlus(other: DingAt): DingAt {
        val atAll = isAtAll || other.isAtAll
        val atMobiles = atMobiles.plus(other.atMobiles).distinct()
        return DingAt(atMobiles.toTypedArray(), atAll)
    }

    /**
     * compare by [isAtAll]
     */
    override fun compareTo(other: DingSpecialMessage): Int {
        return if(other is DingAt){
            isAtAll.compareTo(other.isAtAll)
        }else -1
    }
}



/**
 * 钉钉的Text类型的消息
 * 不应该与markdown类型[DingMarkdown]同时存在
 * `msgtype = text`
 * 可以存在at，也可以为null
 */
data class DingText(val content: String): BaseNormalDingSpecialMessage<DingText>("text") {

    /**
     * 只能获取到content的值
     */
    override operator fun get(key: String): Any? {
        return if(key == "content") content else null
    }

    /**
     * 合并两个[DingText]
     */
    override fun doPlus(other: DingText): DingText = DingText(this.content + other.content)

    /**
     * compare by [content]
     */
    override fun compareTo(other: DingSpecialMessage): Int {
        return if(other is DingText){
            return this.content.compareTo(other.content)
        }else 1
    }
}


fun String.toDingText(): DingText = DingText(this)


/**
 * markdown语法, 且不应该与text类型[DingText]同时存在
 * ```
 * {
    "msgtype": "markdown",
    "markdown": {
    "title":"杭州天气",
    "text": "#### 杭州天气 @150XXXXXXXX \n> 9度，西北风1级，空气良89，相对温度73%\n> ![screenshot](https://img.alicdn.com/tfs/TB1NwmBEL9TBuNjy1zbXXXpepXa-2400-1218.png)\n> ###### 10点20分发布 [天气](https://www.dingtalk.com) \n"
    },
    "at": {
    "atMobiles": [
    "150XXXXXXXX"
    ],
    "isAtAll": false
    }
}
 * ```
 *
 * 目前钉钉的markdown语法只支持一部分子集
 */
data class DingMarkdown(val title: String, val text: String): BaseNormalDingSpecialMessage<DingMarkdown>("markdown") {
    override fun get(key: String): Any? {
        return when(key) {
            "title" -> title
            "text" -> text
            else -> null
        }
    }

    /**
     * 会对title和text进行合并。text直接会有一个换行符(\n)
     */
    override fun doPlus(other: DingMarkdown): DingMarkdown = DingMarkdown(this.title + other.title, this.text + "\n" + other.text)

    /**
     * 根据标题排序
     */
    override fun compareTo(other: DingSpecialMessage): Int {
        return if(other is DingMarkdown){
            this.title.compareTo(other.title)
        }else  this compareWith other
    }

}


/**
 * [DingMarkdown]的builder
 * 官方文档中提到支持的语法：
 * - 1~6级标题
 * - 引用
 * - 文字加粗
 * - 链接
 * - 图片
 * - 有序/无序列表
 * 注意，线程不安全
 */
@Suppress("MemberVisibilityCanBePrivate")
class DingMarkdownBuilder(var title: String){
    private val textBuilder = StringBuilder(32)
    /** 最终的正文 */
    val text: String
    get() = textBuilder.toString()

    fun plus(v: String): DingMarkdownBuilder {
        textBuilder.append(v)
        return this
    }
    fun plus(v: Char): DingMarkdownBuilder {
        textBuilder.append(v)
        return this
    }
    fun newLine(): DingMarkdownBuilder = plus('\n')
    fun plusBlank(): DingMarkdownBuilder = plus(' ')


    /**
     * 标题与等级
     * 会自动拼接一个换行
     * @see h1
     * @see h2
     * @see h3
     * @see h4
     * @see h5
     * @see h6
     */
    fun h(head: String, level: Int): DingMarkdownBuilder {
        if(level <= 0){
            throw IllegalArgumentException("level less than 0")
        }
        for (i in 0 until level) {
            plus('#')
        }
        return plusBlank().plus(head).newLine()
    }
    fun h1(head: String): DingMarkdownBuilder = h(head, 1)
    fun h2(head: String): DingMarkdownBuilder = h(head, 2)
    fun h3(head: String): DingMarkdownBuilder = h(head, 3)
    fun h4(head: String): DingMarkdownBuilder = h(head, 4)
    fun h5(head: String): DingMarkdownBuilder = h(head, 5)
    fun h6(head: String): DingMarkdownBuilder = h(head, 6)

    /**
     * 引用
     */
    fun quote(quote: String): DingMarkdownBuilder = plus('>').plusBlank().plus(quote)

    /**
     * plus一个加粗的字
     */
    fun bold(bold: String): DingMarkdownBuilder = plus("**").plus(bold).plus("**")

    /**
     * plus一个斜体的字
     */
    fun italic(italic: String): DingMarkdownBuilder = plus('*').plus(italic).plus('*')

    /**
     * 追加一个链接
     */
    fun link(description: String, url: String): DingMarkdownBuilder =
            plus('[').plus(description).plus(']')
                    .plus('(').plus(url).plus(')')

    /**
     * 追加一个图片
     */
    fun image(url: String): DingMarkdownBuilder = plus("![](").plus(url).plus(')')

    /**
     * 追加一个描述为图片的链接
     */
    fun linkImage(imageUrl: String, linkUrl: String): DingMarkdownBuilder =
            plus('[')
                    .plus("![](").plus(imageUrl).plus(')')
                    .plus(']')
                    .plus('(').plus(linkUrl).plus(')')


    /**
     * 构建列表
     */
    private inline fun list(vararg list: String, left: (Int) -> String): DingMarkdownBuilder{
        newLine()
        for (i in list.indices) {
            plus(left(i)).plusBlank().plus(list[i]).newLine()
        }
        return newLine()
    }

    /**
     * 无序列表
     */
    fun unorderedList(vararg list: String): DingMarkdownBuilder = list(*list) { "-" }

    /**
     * 有序列表
     */
    fun orderedList(vararg list: String): DingMarkdownBuilder = list(*list) { "${it+1}." }

    /**
     * 构建一个[DingMarkdown]实例
      */
    fun build(): DingMarkdown = DingMarkdown(title, text)
}




