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
     * 判断提供的 [作用域][scope] 是否囊括在当前作用域范围内。
     */
    public operator fun contains(scope: Scope): Boolean

}