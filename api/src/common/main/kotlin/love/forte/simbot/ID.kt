package love.forte.simbot

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.ID.*
import kotlin.js.JsName

/**
 * 唯一标识 [ID].
 * ID一般存在与各个元数据中，作为元数据的唯一标识。
 *
 * 一个 [ID]，其代表了两种数据：ID的类型，以及ID具体的值。
 * [ID] 的类型即为其自身，不同类型的ID无论如何也不应相同。
 * 而 [ID] 中具体的值千变万化，且不一定仅存一个，由实现者自行决定。
 *
 * [ID] 应当支持序列化。
 *
 * @see StringID
 * @see LongID
 * @see EmptyID
 *
 * @author ForteScarlet
 */
@Serializable
public abstract class ID {

    /** 以 [String] 作为唯一标识的[ID]。 */
    @Serializable
    @SerialName("id.str")
    public data class StringID(val id: String) : ID() {
        override fun toString(): String = id
    }


    /** 以 [Long] 作为唯一标识的[ID]。 */
    @Serializable
    @SerialName("id.long")
    public data class LongID(val id: Long) : ID() {
        override fun toString(): String = id.toString()
    }

    /**
     * 一个空的ID。
     */
    @Serializable
    @SerialName("id.empty")
    public object EmptyID : ID()
}


@JsName("getStringID")
public fun ID(id: String): ID = StringID(id)

@JsName("getLongID")
public fun ID(id: Long): ID = LongID(id)

@JsName("emptyID")
public fun emptyID(): ID = EmptyID

