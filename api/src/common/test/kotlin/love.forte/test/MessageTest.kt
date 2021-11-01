package love.forte.test

import kotlinx.serialization.Serializable
import love.forte.simbot.Component
import love.forte.simbot.SimbotComponent
import love.forte.simbot.message.*
import kotlin.reflect.KClass
import kotlin.test.Test

@Serializable
object AtAll : SingleOnlyMessage<AtAll>(), Message.Element<AtAll>, Message.Key<AtAll> {
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

    override fun singleMessage(): AtAll = this
}

@Serializable
class At(val code: Long) : AbstractMessageElement<At>(Key) {
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
        val t1 = "t1".toText()
        val t2 = "t2".toText()
        println(t1 === t2)

        val a1 = At(114514)
        val a2 = At(1919810)
        println(a1)
        println(a2)
        println(AtAll)

        val messages = listOf(t1, t2, a1, AtAll, a2, AtAll, t1, t2, AtAll).toMessages()


        println(t1 + t2 + a1 + AtAll + a2 + AtAll + t1 + t2 + AtAll)

        println(messages)
        println(messages == AtAll)
        println(messages === AtAll)


    }


}