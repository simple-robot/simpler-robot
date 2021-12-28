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

package test

import love.forte.simbot.ID
import love.forte.simbot.concurrentIDMapOf


fun main() {
    val map = concurrentIDMapOf<Int>()

    map[1.ID] = 1
    map[2.ID] = 2
    map[3.ID] = 3
    map[4.ID] = 4

    map.forEach { id, value ->
        if (value == 2) {
            map.remove(3.ID)
        }
        println("id($id)=value($value)")
    }

}