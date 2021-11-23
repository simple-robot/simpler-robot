package love.forte.simbot

import love.forte.simbot.utils.WeakMap
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass


/**
 * 一个属性。
 *
 * 此类型通常使用在 [Component] 中，
 *
 * 是api模块下为数不多使用了反射的地方。
 *
 */
public interface Attribute<V: Any> {
    public val name: String
    public val type: KClass<V>
}

private data class Attr<V : Any>(override val name: String, override val type: KClass<V>) : Attribute<V>


private val attributes = WeakMap<String, Attribute<*>>(ConcurrentHashMap())

public fun <V : Any> Attribute(name: String, type: KClass<V>): Attribute<V> = TODO()