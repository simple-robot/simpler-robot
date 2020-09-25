/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MapNeko.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

@file:Suppress("unused")

package love.forte.catcode.codes

import love.forte.catcode.*
import java.util.function.BiConsumer


/* ******************************************************
 *
 *  kq code by map
 *  基于[Map]作为载体的[KQCode]实例
 *
 *******************************************************/

private val MAP_SPLIT_REGEX = Regex("=")

/**
 * 猫猫码封装类, 以[Map]作为参数载体
 *
 * [MapNeko]通过[Map]保存各项参数，其对应的[可变类型][MutableNeko]实例为[MutableMapNeko],
 * 通过[mutable]与[immutable]进行相互转化。
 *
 * 相比较于[Nyanko], [MapNeko]在进行获取、迭代与遍历的时候表现尤佳，
 * 尤其是参数获取相比较于[Nyanko]的参数获取速度有好几百倍的差距。
 *
 * 但是在实例构建与静态参数获取的时候相比于[Nyanko]略逊一筹。
 *
 * @since 1.0-1.11
 * @since 1.8.0
 */
open class MapNeko
protected constructor(open val params: Map<String, String>, override var type: String) :
        Neko,
        Map<String, String> by params {
    constructor(type: String) : this(emptyMap(), type)
    constructor(type: String, params: Map<String, String>) : this(params.toMap(), type)
    constructor(type: String, vararg params: Pair<String, String>) : this(mapOf(*params), type)
    constructor(type: String, vararg params: String) : this(mapOf(*params.map {
        val split = it.split(MAP_SPLIT_REGEX, 2)
        split[0] to split[1]
    }.toTypedArray()), type)

    // /** internal constructor for mutable kqCode */
    // constructor(mutableKQCode: MutableNeko) : this(mutableKQCode.toMap(), mutableKQCode.type)

    /**
     * Returns the length of this character sequence.
     */
    override val length: Int
        get() = toString().length



    /**
     * 获取转义后的字符串
     */
    override fun getNoDecode(key: String) = CatEncoder.encodeParamsOrNull(this[key])

    /**
     * Returns the character at the specified [index] in this character sequence.
     * @throws [IndexOutOfBoundsException] if the [index] is out of bounds of this character sequence.
     * Note that the [String] implementation of this interface in Kotlin/JS has unspecified behavior
     * if the [index] is out of its bounds.
     */
    override fun get(index: Int): Char = toString()[index]

    /**
     * Returns a new character sequence that is a subsequence of this character sequence,
     * starting at the specified [startIndex] and ending right before the specified [endIndex].
     *
     * @param startIndex the start index (inclusive).
     * @param endIndex the end index (exclusive).
     */
    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence = toString().subSequence(startIndex, endIndex)


    /**
     * toString的值记录。因为是不可变类，因此toString是不会变的
     * 在获取的时候才会去实际计算，且仅计算一次。
     */
    private val _toString: String by lazy { CatCodeUtil.toCat(type, map = this) }

    /** toString */
    override fun toString(): String = _toString


    /**
     * 转化为参数可变的[MutableNeko]
     */
    override fun mutable(): MutableNeko = MutableMapNeko(type, this.toMutableMap())

    /**
     * 转化为不可变类型[Neko]
     */
    override fun immutable(): Neko = this


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MapNeko

        if (params != other.params) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result: Int = params.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }

    /** [MapNeko] companion object. */
    companion object Of {
        /** 参数切割用的正则 */
        private val TEMP_SPLIT_REGEX = Regex(" *, *")
        /**
         * 将猫猫码字符串切割为参数列表
         * 返回的键值对为 `type to split`
         */
        @Suppress("NOTHING_TO_INLINE")
        private inline fun splitCode(code: String): Pair<String, List<String>> {
            var tempText = code.trim()
            // 不是[CAT:开头，或者不是]结尾都不行
            if (!tempText.startsWith(CAT_HEAD) || !tempText.endsWith(CAT_END)) {
                throw IllegalArgumentException("not starts with '$CAT_HEAD' or not ends with '$CAT_END'")
            }
            // 是[CAT:开头，]结尾，切割并转化
            tempText = tempText.substring(4, tempText.lastIndex)

            val split = tempText.split(TEMP_SPLIT_REGEX)
            val type = split[0]
            return type to split
        }

        /**
         * 根据猫猫码字符串获取[MapNeko]实例
         */
        @JvmStatic
        @JvmOverloads
        fun byCode(code: String, decode: Boolean = true): MapNeko {
            val (type, split) = splitCode(code)

            return if (split.size > 1) {
                if (decode) {
                    // 参数解码
                    val map = split.subList(1, split.size).map {
                        val sp = it.split(Regex("="), 2)
                        sp[0] to CatDecoder.decodeParams(sp[1])
                    }.toMap()
                    MapNeko(map, type)
                } else {
                    MapNeko(type, *split.subList(1, split.size).toTypedArray())
                }
            } else {
                MapNeko(type)
            }
        }

        /** 通过map参数获取 */
        @JvmStatic
        fun byMap(type: String, params: Map<String, String>): MapNeko = MapNeko(type, params)
        /** 通过键值对获取 */
        @JvmStatic
        fun byPair(type: String, vararg params: Pair<String, String>): MapNeko = MapNeko(type, *params)
        /** 通过键值对字符串获取 */
        @JvmStatic
        fun byParamString(type: String, vararg params: String): MapNeko = MapNeko(type, *params)

        /**
         * 根据猫猫码字符串获取[MapNeko]实例
         */
        @JvmStatic
        @JvmOverloads
        fun mutableByCode(code: String, decode: Boolean = true): MutableMapNeko {
            val (type, split) = splitCode(code)

            return if (split.size > 1) {
                if (decode) {
                    // 参数解码
                    val map: MutableMap<String, String> = split.subList(1, split.size).map {
                        val sp = it.split(Regex("="), 2)
                        sp[0] to CatDecoder.decodeParams(sp[1])
                    }.toMap().toMutableMap()
                    MutableMapNeko(type, map)
                } else {
                    MutableMapNeko(type, *split.subList(1, split.size).toTypedArray())
                }
            } else {
                MutableMapNeko(type)
            }
        }


        /** 通过map参数获取 */
        @JvmStatic
        fun mutableByMap(type: String, params: Map<String, String>): MutableMapNeko = MutableMapNeko(type, params)
        /** 通过键值对获取 */
        @JvmStatic
        fun mutableByPair(type: String, vararg params: Pair<String, String>): MutableMapNeko = MutableMapNeko(type, *params)
        /** 通过键值对字符串获取 */
        @JvmStatic
        fun mutableByParamString(type: String, vararg params: String): MutableMapNeko = MutableMapNeko(type, *params)
    }

}

/**
 * [Neko]对应的可变类型, 以[MutableMap]作为载体
 *
 * 目前来讲唯一的[MutableNeko]实例. 通过[MutableMap]作为参数载体需要一定程度的资源消耗，
 * 因此我认为最好应该避免频繁大量的使用[可变类型][MutableMap].
 *
 * 如果想要动态的构建一个[Neko], 也可以试试[CodeBuilder],
 * 其中[StringCodeBuilder]则以字符串操作为主而避免了构建内部[Map]
 *
 * 但是无论如何, 都最好在构建之前便决定好参数
 *
 * @since 1.8.0
 */
@Suppress("DELEGATED_MEMBER_HIDES_SUPERTYPE_OVERRIDE")
class MutableMapNeko
private constructor(override val params: MutableMap<String, String>, type: String) :
        MapNeko(params, type),
    MutableNeko,
        MutableMap<String, String> by params {
    constructor(type: String) : this(mutableMapOf(), type)
    constructor(type: String, params: Map<String, String>) : this(params.toMutableMap(), type)
    constructor(type: String, vararg params: Pair<String, String>) : this(mutableMapOf(*params), type)
    constructor(type: String, vararg params: String) : this(mutableMapOf(*params.map {
        val split = it.split(MAP_SPLIT_REGEX, 2)
        split[0] to split[1]
    }.toTypedArray()), type)

    // /** internal constructor for kqCode */
    // internal constructor(neko: Neko) : this(neko.toMutableMap(), neko.type)

    /**
     * 转化为参数可变的[MutableNeko]
     */
    override fun mutable(): MutableNeko = this

    /**
     * 转化为不可变类型[Neko]
     */
    override fun immutable(): Neko = MapNeko(type, this)

    /** toString */
    override fun toString(): String = CatCodeUtil.toCat(type, map = this)


    override fun forEach(action: BiConsumer<in String, in String>) {
        super<MapNeko>.forEach(action)
    }
}
