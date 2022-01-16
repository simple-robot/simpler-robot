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
// @DisplayName("数值<=0匹配测试")
class CheckTest2 {

    // @AfterTest
    fun split() {
        println("====")
    }

    // @Test
    // @Ignore
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
         */
    }

    //@Test
    //@Ignore
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
         */
    }

    fun t1(a: Int, b: Int, c: Int): Boolean = a <= 0 && b <= 0 && c <= 0
    fun t2(a: Int, b: Int, c: Int): Boolean = (a + b + c) <= 0

}