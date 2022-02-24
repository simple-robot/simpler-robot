package love.forte.simbot.logger.test

import org.slf4j.LoggerFactory
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

class LoggerFactoryTest

@OptIn(ExperimentalTime::class)
fun main() {
    val logger = LoggerFactory.getLogger(LoggerFactoryTest::class.java)
    val repeatTime = 400_000
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
            logger.info("Hello {}, I am {}, the time: {}", "World", User(), it)
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