/*
 *  Copyright (c) 2021 ForteScarlet <https://github.com/ForteScarlet>
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