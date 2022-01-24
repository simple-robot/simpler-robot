/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
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
 *
 */

package love.forte.test

import kotlin.system.measureNanoTime
import kotlin.time.Duration.Companion.nanoseconds

/**
 *
 * @author ForteScarlet
 */
// @DisplayName("数值==0匹配测试")
class CheckTest {

    // @Test @Ignore
    fun test1() {
        repeat(10000) {
            t1(1, 0, 1)
        }
        repeat(10000) {
            t1(0, 0, 0)
        }
        repeat(10000) {
            t2(1, 0, 1)
        }
        repeat(10000) {
            t2(0, 0, 0)
        }
        val time1 = measureNanoTime {
            repeat(1000_0000) {
                t1(1, 0, 1)
                t1(0, 0, 0)
            }
        }
        val time2 = measureNanoTime {
            repeat(1000_0000) {
                t2(1, 0, 1)
                t2(0, 0, 0)
            }
        }

        println("t1 nacos: $time1, mill: ${time1.nanoseconds.inWholeMilliseconds}")
        println("t2 nacos: $time2, mill: ${time2.nanoseconds.inWholeMilliseconds}")
        /*
            t1 nacos: 23512084, mill: 23
            t2 nacos: 4308553, mill: 4

            t1 nacos: 5080353, mill: 5
            t2 nacos: 4587869, mill: 4

            t1 nacos: 4633980, mill: 4
            t2 nacos: 14598967, mill: 14

            t1 nacos: 16813503, mill: 16
            t2 nacos: 3540340, mill: 3

            t1 nacos: 4915480, mill: 4
            t2 nacos: 14649509, mill: 14
         */
    }

    // @Test @Ignore
    fun test2() {
        repeat(10000) {
            t2(1, 0, 1)
        }
        repeat(10000) {
            t2(0, 0, 0)
        }
        repeat(10000) {
            t1(1, 0, 1)
        }
        repeat(10000) {
            t1(0, 0, 0)
        }
        val time2 = measureNanoTime {
            repeat(1000_0000) {
                t2(1, 0, 1)
                t2(0, 0, 0)
            }
        }
        val time1 = measureNanoTime {
            repeat(1000_0000) {
                t1(1, 0, 1)
                t1(0, 0, 0)
            }
        }


        println("t1 nacos: $time1, mill: ${time1.nanoseconds.inWholeMilliseconds}")
        println("t2 nacos: $time2, mill: ${time2.nanoseconds.inWholeMilliseconds}")
        /*
            t1 nacos: 14411892, mill: 14
            t2 nacos: 3916956, mill: 3

            t1 nacos: 15158076, mill: 15
            t2 nacos: 4370029, mill: 4

            t1 nacos: 14570882, mill: 14
            t2 nacos: 5025647, mill: 5

            t1 nacos: 2495299, mill: 2
            t2 nacos: 3965718, mill: 3

            t1 nacos: 4006027, mill: 4
            t2 nacos: 5327382, mill: 5
         */
    }

    fun t1(a: Int, b: Int, c: Int): Boolean = a == 0 && b == 0 && c == 0
    fun t2(a: Int, b: Int, c: Int): Boolean = (a or b or c) == 0

}