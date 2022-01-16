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