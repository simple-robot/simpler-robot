/*
 *  Copyright (c) 2022-2022 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x、simbot 3.x、simbot3) 的一部分。
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
 */

package love.forte.simbot.logger.test

import org.slf4j.LoggerFactory
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class LoggerFactoryTest

@OptIn(ExperimentalTime::class)
fun main() {
    val logger = LoggerFactory.getLogger(LoggerFactoryTest::class.java)
    val repeatTime = 40
    val time2 = measureTime {
        repeat(repeatTime) {
            println("Hello World, I am ${User()}, the time: $it")
        }
    }
    // val time3 = measureTime {
    //     repeat(repeatTime) {
    //         System.out.printf("Hello %s, I am %s, the time: %s\n", "world", User(), it)
    //     }
    // }

    val time1 = measureTime {
        repeat(repeatTime) {
            logger.debug("Hello {}, I am {}, the time: {}", "World", User(), it)
        }
    }


    /*
        time: 1.487475057s
        time: 1.415717011s



     */
    Thread.sleep(5000)

    println()
    println()
    println()
    println()
    println()
    println()

    println("time1( logger): $time1")
    println("time2(println): $time2")
    //println("time3( printf): $time3")
}

data class User(val name: String = "forte")

//
// /**
//  *
//  * @author ForteScarlet
//  */
// class LoggerFactoryTest {
//
//     private val logger = LoggerFactory.getLogger("Forte-Demo")
//
//     @Test
//     fun test() {
//         logger.info("Hello {}", "World")
//         logger.info("Hello {}, {}", "World", 666)
//         logger.info("Hello {}, {}, {}", "World", 666, LoggerFactoryTest())
//     }
//
//     @Test
//     fun stringGetOnMax() {
//         assert("ForteScarlet".getOnMax(1) == "t")
//         assert("ForteScarlet".getOnMax(7) == "Scarlet")
//         assert("ForteScarlet".getOnMax(20) == "ForteScarlet")
//         assert("ForteScarlet".getOnMax(0).isEmpty())
//     }
//
//
// }