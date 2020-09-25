/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     WildcatCodeUtil.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */


package love.forte.catcode

import love.forte.catcode.codes.MapNeko
import love.forte.catcode.codes.Nyanko


/**
 * 野良猫码的操作工具类。
 *
 * 构建此工具类需要提供一个 `codeType`参数以代表此野良猫码的类型。
 *
 * 所谓野良猫，即code类型不一定是`CAT`的cat码。
 * 例如：`[CAT:at,code=123]`, 此码的类型为`CAT`, 所以是标准猫猫码，
 * 而例如`[CQ:at,code=123]`, 此码的类型为`CQ`, 不是标准猫猫码，即为野良猫码。
 *
 * > 野良猫 -> のらねこ -> 野猫 -> wildcat
 *
 */
@Suppress("unused", "DeprecatedCallableAddReplaceWith")
public class WildcatCodeUtil(codeType: String) : NekoAibo(codeType) {

    companion object {
        @JvmStatic
        fun getInstance(codeType: String): WildcatCodeUtil = WildcatCodeUtil(codeType)
    }

    /**
     *  获取一个String为载体的[模板][CodeTemplate]
     *  @see StringTemplate
     */
    @Deprecated("TODO...")
    override val stringTemplate: CodeTemplate<String>
        get() = StringTemplate

    /**
     *  获取[Neko]为载体的[模板][CodeTemplate]
     *  @see NekoTemplate
     */
    @Deprecated("TODO...")
    override val nekoTemplate: CodeTemplate<Neko>
        get() = NekoTemplate

    /**
     * 构建一个String为载体类型的[构建器][CodeBuilder]
     */
    @Deprecated("TODO...")
    override fun getStringCodeBuilder(type: String): CodeBuilder<String> = CodeBuilder.stringCodeBuilder(type)

    /**
     * 构建一个[Neko]为载体类型的[构建器][CodeBuilder]
     */
    @Deprecated("TODO...")
    override fun getNekoBuilder(type: String): CodeBuilder<Neko> = CodeBuilder.nekoBuilder(type)


    /**
     * 获取无参数的[Neko]
     * @param type 猫猫码的类型
     */
    override fun toNeko(type: String): Neko = EmptyNoraNeko(codeType, type)

    /**
     * 根据[Map]类型参数转化为[Neko]实例
     *
     * @param type 猫猫码的类型
     * @param params 参数列表
     */
    override fun toNeko(type: String, params: Map<String, *>): Neko {
        return if (params.isEmpty()) {
            toNeko(type)
        } else {
            MapNeko(type, params.asSequence().map { it.key to it.value.toString() }.toMap())
        }
    }


    /**
     * 根据参数转化为[Neko]实例
     * @param type 猫猫码的类型
     * @param params 参数列表
     */
    override fun toNeko(type: String, vararg params: Pair<String, *>): Neko {
        return if (params.isEmpty()) {
            EmptyNeko(type)
        } else {
            MapNeko(type, params.asSequence().map { it.first to it.second.toString() }.toMap())
        }
    }


    /**
     * 根据参数转化为[Neko]实例
     * @param type 猫猫码的类型
     * @param paramText 参数列表, 例如："qq=123"
     */
    override fun toNeko(type: String, encode: Boolean, vararg paramText: String): Neko {
        return if (paramText.isEmpty()) {
            EmptyNeko(type)
        } else {
            if (encode) {
                Nyanko.byCode(toCat(type, encode, *paramText))
            } else {
                MapNeko.byParamString(type, *paramText)
            }
        }
    }



    /**
     * 提取出文本中的猫猫码，并封装为[Neko]实例。
     * @param text 存在猫猫码的正文
     * @param type 要获取的猫猫码的类型，默认为所有类型
     * @param index 获取的索引位的猫猫码，默认为0，即第一个
     */
    override fun getNeko(text: String, type: String, index: Int): Neko? {
        val cat: String = getCat(text, type, index) ?: return null
        return Neko.of(cat)
    }


}






