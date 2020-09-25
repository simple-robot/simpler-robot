/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     CatIterators.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.catcode


/**
 * 基于字符串操作的kq迭代器父类
 */
internal abstract class BaseCatIterator<T>(protected val code: String): Iterator<T> {
    init {
        if (!code.startsWith(CAT_HEAD) || !code.endsWith(CAT_END)) {
            throw IllegalArgumentException("text '$code' is not a cat code text.")
        }
    }

    // 从索引0开始寻找
    /** 下一个开始的查询索引 */
    protected var index: Int = 0

    /** 下一个索引位 */
    protected abstract fun nextIndex(): Int


    /**
     * 判断是否还有下一个参数
     */
    override fun hasNext(): Boolean {
        // 如果有逗号，说明还有键值对
        return index >= 0 && nextIndex() > 0
    }

}

/**
 * 文本猫猫码迭代器，从一串文本中迭代出其中的猫猫码
 * @since 1.1-1.11
 */
internal class CatTextIterator(private val text: String, type: String = "") : Iterator<String> {
    private var i = -1
    private var ti = 0
    private var e = 0
    private val het = CAT_HEAD + type
    private val ent = CAT_END

    private var next: String? = null
    private var get = true

    /**
     * Returns `true` if the iteration has more elements.
     */
    override fun hasNext(): Boolean {
        if (!get) {
            return next != null
        }
        get = false
        do {
            ti = text.indexOf(het, e)
            if (ti >= 0) {
                e = text.indexOf(ent, ti)
                if (e >= 0) {
                    i++
                    next = text.substring(ti, e + 1)
                } else {
                    e = ti + 1
                }
            }
        } while (next == null && (ti >= 0 && e >= 0))

        return next != null
    }

    /**
     * Returns the next element in the iteration.
     */
    override fun next(): String {
        val n = next
        next = null
        get = true
        return n!!
    }
}


/**
 * 一串儿猫猫码字符串中的键迭代器
 * @since 1.8.0
 */
internal class CatParamKeyIterator(code: String): BaseCatIterator<String>(code) {


    override fun nextIndex(): Int {
        return code.indexOf(CAT_PV, if (index == 0) 0 else index + 1)
    }

    /**
     * Returns the next element in the iteration.
     */
    override fun next(): String {
        if(!hasNext()) throw NoSuchElementException()

        // 下一个逗号所在处
        index = nextIndex()
        // 下一个kv切割符所在
        val nextKv = code.indexOf(CAT_KV, index)
        return code.substring(index+1, nextKv)
    }

}



/**
 * 一串儿猫猫码字符串中的值迭代器
 * 得到的值会进行反转义。
 * @since 1.8.0
 */
internal class CatParamValueIterator(code: String): BaseCatIterator<String>(code) {


    override fun nextIndex(): Int {
        return code.indexOf(CAT_KV, if(index == 0) 0 else index + 1)
    }

    /**
     * 判断是否还有下一个参数
     */
    override fun hasNext(): Boolean {
        // 如果有逗号，说明还有键值对
        return index >= 0 && nextIndex() > 0
    }

    /**
     * Returns the next element in the iteration.
     */
    override fun next(): String {
        if(!hasNext()) throw NoSuchElementException()

        // 下一个逗号所在处
        index = nextIndex()
        // 下一个逗号或结尾符所在处
        var nextSplit = code.indexOf(CAT_PV, index)
        if(nextSplit < 0){
            nextSplit = code.lastIndex
        }
        return CatDecoder.decodeParams(code.substring(index+1, nextSplit))
    }

}




/**
 * 一串儿猫猫码字符串中的键值对迭代器
 * 得到的值会进行反转义。
 * @since 1.8.0
 */
internal class CatParamPairIterator(code: String): BaseCatIterator<Pair<String, String>>(code) {


    override fun nextIndex(): Int {
        return code.indexOf(CAT_PV, if(index == 0) 0 else index + 1)
    }


    /**
     * Returns the next element in the iteration.
     */
    override fun next(): Pair<String, String> {
        if(!hasNext()) throw NoSuchElementException()

        // 下一个逗号所在处
        index = nextIndex()
        // 下下一个逗号或结尾符所在处
        var nextSplit = code.indexOf(CAT_PV, index + 1)
        if(nextSplit < 0){
            nextSplit = code.lastIndex
        }
        val substr = code.substring(index + 1, nextSplit)
        val keyValue = substr.split(CAT_KV)
        return keyValue[0] to CatDecoder.decodeParams(keyValue[1])
    }

}


internal class CatParamEntryIterator(code: String): BaseCatIterator<Map.Entry<String, String>>(code) {

    override fun nextIndex(): Int {
        return code.indexOf(CAT_PV, if(index == 0) 0 else index + 1)
    }

    /**
     * 判断是否还有下一个参数
     */
    override fun hasNext(): Boolean {
        // 如果有逗号，说明还有键值对
        return index >= 0 && nextIndex() > 0
    }

    /**
     * Returns the next element in the iteration.
     */
    override fun next(): Map.Entry<String, String> {
        if(!hasNext()) throw NoSuchElementException()

        // 下一个逗号所在处
        index = nextIndex()
        // 下下一个逗号或结尾符所在处
        var nextSplit = code.indexOf(CAT_PV, index + 1)
        if(nextSplit < 0){
            nextSplit = code.lastIndex
        }
        val substr = code.substring(index + 1, nextSplit)
        val keyValue = substr.split(CAT_KV)
        return KqEntry(keyValue[0], CatDecoder.decodeParams(keyValue[1]))
    }

}


/**
 * 针对于[Map.Entry]的简易实现
 */
internal data class KqEntry(override val key: String, override val value: String) : Map.Entry<String, String>
