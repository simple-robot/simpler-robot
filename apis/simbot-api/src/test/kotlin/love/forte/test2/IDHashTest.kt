package love.forte.test2

import love.forte.simbot.ID
import love.forte.simbot.mutableIDMapOf


fun main() {
    val map = mutableIDMapOf<Int>()


    map[1.ID] = 1
    map["1".ID] = 2
    map[1.00.ID] = 3
    map[1.00F.ID] = 4

    println(map.size)
    println(map)


}