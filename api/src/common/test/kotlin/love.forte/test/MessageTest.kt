package love.forte.test

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import love.forte.simbot.Component
import love.forte.simbot.SimbotComponent
import love.forte.simbot.message.*
import kotlin.reflect.KClass
import kotlin.test.Test

@Serializable
object AtAll : SingleOnlyMessage<AtAll>(), Message.Key<AtAll> {
    override val key: Message.Key<AtAll>
        get() = this

    override fun safeCast(instance: Any?): AtAll? = doSafeCast<AtAll>(instance)

    override val component: Component get() = SimbotComponent

    override fun toString(): String = "AtAll"

    override fun equals(other: Any?): Boolean = other === AtAll

    /**
     * 得到此元素的 [KClass].
     */
    override val elementType: KClass<AtAll>
        get() = AtAll::class

}

@Serializable
@SerialName("test.at")
class At(val code: Long) : Message.Element<At> {
    override val key: Message.Key<At> get() = Key
    companion object Key : AbstractKey<At>(SimbotComponent, castFunc = { doSafeCast<At>(it) }) {
        override val elementType: KClass<At> get() = At::class
    }

    override fun toString(): String = "At(code=$code)"


    override fun equals(other: Any?): Boolean {
        return when (other) {
            other === this -> true
            is At -> code == other.code
            else -> false
        }
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}


/**
 *
 * @author ForteScarlet
 */
class MessageTest {

    @Test
    fun emptyMessageTest() {

        val a1 = At(2)

        val a2 = Text { "hi" }

        println(Json.encodeToString(a1))
        println(Json.encodeToString(a2))


    }


}