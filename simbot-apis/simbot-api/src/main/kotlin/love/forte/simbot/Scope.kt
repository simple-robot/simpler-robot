/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (即 simple robot的v3版本，因此亦可称为 simple-robot v3 、simbot v3 等) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

package love.forte.simbot

import kotlinx.serialization.Serializable
import love.forte.simbot.definition.Category
import love.forte.simbot.definition.IDContainer


/**
 *
 * 一个作用域。
 *
 * 一个作用域可以用于判断另一个作用域是否被其囊括。
 *
 * _Deprecated: 意义不大，不再应用。_
 *
 * @author ForteScarlet
 */
@Deprecated("No longer used")
public interface Scope : IDContainer {
    
    /**
     * 作用域的标识。
     */
    override val id: ID
    
    /**
     * 作用域的名称。
     */
    public val name: String
    
    /**
     * 判断提供的 [作用域][scope] 是否囊括在当前作用域范围内。
     */
    @Suppress("DEPRECATION")
    public operator fun contains(scope: Scope): Boolean
    
}


/**
 * 这是一个最基本的 [作用域][Scope], 其代表了一个没有任何嵌套关系的**分组**。
 * 例如一个组织的分组，好友列表的分组，权限的分组（无嵌套关系的）等。
 *
 * 默认情况下，[Grouping.equals] 只进行 [id] 的匹配.
 *
 * _Deprecated: Grouping的定义较为模糊，且由于 [Scope] 已经弃用，Group 的职能将会由作为“分类”含义更加明确的 [love.forte.simbot.definition.Category] 代替。_
 *
 * @see love.forte.simbot.definition.Category
 */
@Suppress("DEPRECATION")
@Serializable
@Deprecated("No longer used, function replaced by Category", ReplaceWith("Category", "love.forte.simbot.definition.Category"))
public open class Grouping(
    @Serializable(ID.AsCharSequenceIDSerializer::class)
    override val id: ID,
    override val name: String,
) : Scope {
    override fun contains(scope: Scope): Boolean {
        return if (scope is Category) id == scope.id
        else false
    }
    
    /**
     * [Grouping.equals] 只进行 [id] 的匹配.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Category) return false
        
        return id == other.id
    }
    
    override fun hashCode(): Int = id.hashCode()
    override fun toString(): String = "Grouping(id=$id, name=$name)"
    
    /**
     * 代表一个全部内容都是空字符的 [Grouping].
     */
    public companion object {
        
        @Suppress("DEPRECATION")
        @JvmField
        public val EMPTY: Grouping = Grouping("".ID, "")
    }
}