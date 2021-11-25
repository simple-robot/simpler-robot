/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot

import kotlinx.serialization.Serializable


/**
 *
 * 一个作用域。
 *
 * 一个作用域可以用于判断另一个作用域是否被其囊括。
 *
 * @author ForteScarlet
 */
public interface Scope {

    /**
     * 作用域的标识。
     */
    public val id: ID

    /**
     * 作用域的名称。
     */
    public val name: String

    /**
     * 判断提供的 [作用域][scope] 是否囊括在当前作用域范围内。
     */
    public operator fun contains(scope: Scope): Boolean

}


/**
 * 这是一个最基本的 [作用域][Scope], 其代表了一个没有任何嵌套关系的**分组**。
 * 例如一个组织的分组，好友列表的分组，权限的分组（无嵌套关系的）等。
 *
 * 默认情况下，[Grouping.equals] 只进行 [id] 的匹配.
 *
 */
@Serializable
public open class Grouping(
    @Serializable(ID.AsCharSequenceIDSerializer::class)
    override val id: ID,
    override val name: String
) : Scope {
    override fun contains(scope: Scope): Boolean {
        return if (scope is Grouping) id == scope.id
        else false
    }

    /**
     * [Grouping.equals] 只进行 [id] 的匹配.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Grouping) return false

        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String = "Grouping(id=$id, name=$name)"

    /**
     * 代表一个全部内容都是空字符的 [Grouping].
     */
    public companion object Empty : Grouping("".ID, "") {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Grouping) return false
            return other.id.toCharSequenceID().length == 0
        }
    }
}