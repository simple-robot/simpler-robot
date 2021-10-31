package love.forte

import love.forte.simbot.message.StandardMessage
import java.util.*
import java.util.concurrent.ConcurrentHashMap


val map: MutableMap<String, String> = ConcurrentHashMap<String, String>()

fun main() {

    val c = StandardMessage::class
    c.sealedSubclasses

}