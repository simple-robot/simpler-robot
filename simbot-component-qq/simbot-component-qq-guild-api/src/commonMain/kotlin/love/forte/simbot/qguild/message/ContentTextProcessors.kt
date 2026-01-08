/*
 * Copyright (c) 2023. ForteScarlet.
 *
 * This file is part of simbot-component-qq-guild.
 *
 * simbot-component-qq-guild is free software: you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * simbot-component-qq-guild is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with simbot-component-qq-guild.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simbot.qguild.message


private const val S_LT: Char = '<'
private const val S_GT: Char = '>'
private const val S_AMP: Char = '&'

private const val E_LT: String = "&lt;"
private const val E_GT: String = "&gt;"
private const val E_AMP: String = "&amp;"

/**
 * QQ频道[内嵌格式](https://bot.q.qq.com/wiki/develop/api/openapi/message/message_format.html)解码器。
 *
 * 用于将**转义后**字符解码为**源字符**。
 *
 * | **源字符** | **转义后** |
 * |----------|----------|
 * | `&` <- | `&amp;` |
 * | `<` <- | `&lt;` |
 * | `>` <- | `&gt;` |
 */
public object ContentTextDecoder {
    private val targets = listOf(E_LT, E_GT, E_AMP)
    private val targetsMapping = charArrayOf(S_LT, S_GT, S_AMP)

    //    private val replaceRegex = Regex("$eLtEscape|$eGtEscape|$eAmpEscape")
    private val matcher = SimpleMatcher(targets)

    private fun appendTo(targetIndex: Int, appendable: Appendable) {
        appendable.append(targetsMapping[targetIndex])
    }


    /**
     * 将 [format] 中的被转义的内容解码为源字符
     */
    public fun decode(format: String): String {
        return buildString(format.length) {
            matcher.match(format, onMatch = { ti, _, _ ->
                appendTo(ti, this)
            }) { s, e ->
                append(format, s, e)
            }
        }
    }


    /**
     * 将 [format] 中的被转义的内容解码为源字符，并追加到 [appendable] 中。
     *
     * @param format 需要被转义的文本，长度不能为0
     *
     * @throws IndexOutOfBoundsException [startIndex] 或 [endIndex] 超出 [format] 可承受范围
     */
    public fun <A : Appendable> decodeTo(format: CharSequence, startIndex: Int, endIndex: Int, appendable: A): A {
        // 小于最小长度
        if (format.length < 4) {
            return appendable.also { it.append(format, startIndex, endIndex) }
        }

        matcher.match(format, start = startIndex, end = endIndex, onMatch = { ti, _, _ ->
            appendTo(ti, appendable)
        }) { s, e ->
            appendable.append(format, s, e)
        }


        return appendable
    }

}


/**
 * 用于高效率从某字符串中寻找固定的匹配目标的匹配器，并且支持自定义起始索引。
 * 用于替换在 [ContentTextDecoder] 中原本的使用正则进行内容替换的方案。
 *
 * 匹配结果会使用与 `matchers` 对应的元素索引来替代频繁拼接的字符串。
 *
 * 避免出现大小子集都存在的情况，例如 `['fort', 'forte']`，[SimpleMatcher] 永远只会匹配最小子集 `'fort'` 。
 *
 * @param matchers 固定的匹配目标，其中元素索引与最终匹配结果的 `targetIndex` 对应。
 *
 */
private class SimpleMatcher(matchers: List<String>) {
    /*
                 [__________________]
       matchers: ['forte' | 'forliy']
    targetIndex: [   0    |     1   ]
                 [==================]
                          |
                          |
                          V

                      [node: 'f']
                          |-- [node: 'o']
                                  |-- [node: 'r']
                                          |-- [node: 't']
                                          |       |-- [node: 'e', targetIndex: 0]
                                          |
                                          |-- [node: 'l']
                                                  |-- [node: 'i']
                                                          |-- [node: 'y', targetIndex: 1]
                                   (maxLength: 6)

        简单来说就是棵很简单的树。

        Benchmark                          Mode  Cnt    Score   Error   Units
        DecoderBenchmark.decodeByMatcher  thrpt    2  107.182          ops/ms
        DecoderBenchmark.decodeByRegex    thrpt    2   47.239          ops/ms
        ↑ 126.8930333%

        多少有点儿效果吧。要不然感觉写这玩意儿浪费这么多时间有点儿...
     */

    val nodes: Nodes = Nodes().also {
        matchers.forEachIndexed { i, str ->
            it.append(str, i)
        }
    }


    /**
     * 从 [input] 中寻找与当前 matchers 匹配的结果。
     *
     * @param onMatch 当寻找到匹配的目标子字符串时触发此函数。
     * 此函数的三个参数函数分别为：
     * - `targetIndex`: 匹配到的目标在 `matchers` 中对应的索引
     * - `startIndex`: 匹配到的目标在 [input] 中的起始索引
     * - `endIndex`: 匹配到的目标在 [input] 中的结尾索引（不包含）
     *
     * @param onNormal 其他未匹配到目标结果的字符串片段被触发时的函数。
     * 在 [onMatch] 触发之前和匹配结束且有剩余片段时触发。
     *
     */
    inline fun match(
        input: CharSequence,
        start: Int = 0,
        end: Int = input.length,
        onMatch: (targetIndex: Int, startIndex: Int, endIndex: Int) -> Unit,
        onNormal: (startIndex: Int, endIndex: Int) -> Unit
    ) {
        var currentNode: Node? = null
        var lastNormalIndex = start
        var lastMatchIndex = -1
        for (index in start until end) {
            val char = input[index]
            val gotNode: Node? = if (currentNode == null) nodes[char] else currentNode.matchNext(char)
            if (gotNode != null) {
                currentNode = gotNode
                if (lastMatchIndex < 0) {
                    lastMatchIndex = index
                }

                if (gotNode.isEnd) {
                    onNormal(lastNormalIndex, lastMatchIndex)
                    onMatch(gotNode.targetIndex, lastMatchIndex, index + 1)
                    lastMatchIndex = -1
                    lastNormalIndex = index + 1
                    currentNode = null
                }
            } else {
                currentNode = null
            }
        }
        if (lastNormalIndex < end) {
            onNormal(lastNormalIndex, end)
        }
    }


    override fun toString(): String {
        return "DecodeMatcher(nodes=$nodes)"
    }

    class Nodes {
        private val nodes = LinkedHashMap<Char, Node>(4)

        operator fun get(value: Char): Node? = nodes[value]

        fun append(value: String, i: Int) {
            if (value.isEmpty()) {
                return
            }
            val firstChar = value[0]
            var node: Node = nodes.getOrPut(firstChar) { Node(firstChar) }
            var index = 1
            while (index < value.length) {
                node = node.append(value[index]) { Node(it) }
                index += 1
            }
            // the lastOne
            node.targetIndex = i
        }

        override fun toString(): String = nodes.toString()

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Nodes) return false

            if (nodes != other.nodes) return false

            return true
        }

        override fun hashCode(): Int {
            return nodes.hashCode()
        }

    }

    data class Node(val value: Char, var children: Map<Char, Node> = emptyMap()) {
        internal var targetIndex: Int = -1
        val isEnd: Boolean get() = targetIndex >= 0 // children.isEmpty()

        fun matchNext(value: Char): Node? = children[value]

        inline fun append(char: Char, nodeFactory: (Char) -> Node): Node {
            return when {
                children.isEmpty() -> Node(char).also {
                    children = mapOf(char to it)
                }

                children.size == 1 -> {
                    val map = children.toMutableMap().also {
                        children = it
                    }
                    map.getOrPut(char) { nodeFactory(char) }
                }

                else -> {
                    val c = children as MutableMap
                    c.getOrPut(char) { nodeFactory(char) }
                }
            }
        }
    }
}


/**
 * QQ频道[内嵌格式](https://bot.q.qq.com/wiki/develop/api/openapi/message/message_format.html)编码器。
 *
 * 用于将**源字符**编码为**转义后**字符。
 *
 * | **源字符** | **转义后** |
 * |----------|----------|
 * | `&` -> | `&amp;` |
 * | `<` -> | `&lt;` |
 * | `>` -> | `&gt;` |
 */
public object ContentTextEncoder {

    /**
     * 将 [text] 中的源字符转义
     */
    public fun encode(text: String): String {
        if (text.isEmpty()) return text
        if (text.length == 1) {
            return when (text[0]) {
                S_LT -> E_LT
                S_GT -> E_GT
                S_AMP -> E_AMP
                else -> text
            }
        }

        return buildString(text.length) {
            encodeTo0(text, this)
        }
    }

    /**
     * 将 [text] 中的源字符转义并将结果拼接至目标 [appendable]。
     *
     * @return [appendable] 自身
     */
    public fun <A : Appendable> encodeTo(text: String, appendable: A): A {
        if (text.isEmpty()) return appendable
        if (text.length == 1) {
            return appendable.apply {
                when (text[0]) {
                    S_LT -> append(E_LT)
                    S_GT -> append(E_GT)
                    S_AMP -> append(E_AMP)
                    else -> append(text)
                }
            }
        }

        return appendable.also { encodeTo0(text, it) }
    }

    private fun encodeTo0(text: String, appendable: Appendable) {
        var lastStartIndex = 0

        fun flush(index: Int, target: String) {
            appendable.append(text, lastStartIndex, index)
            lastStartIndex = index + 1
            appendable.append(target)
        }

        for (index in text.indices) {
            when (text[index]) {
                S_LT -> flush(index, E_LT)
                S_GT -> flush(index, E_GT)
                S_AMP -> flush(index, E_AMP)
            }
        }

        if (lastStartIndex < text.lastIndex) {
            appendable.append(text, lastStartIndex, text.length)
        }
    }
}
