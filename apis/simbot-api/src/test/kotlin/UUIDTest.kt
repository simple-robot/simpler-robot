/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

import love.forte.simbot.utils.RandomIDUtil
import kotlin.test.Test
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime

/**
 *
 * @author ForteScarlet
 */
class UUIDTest {
    @OptIn(ExperimentalTime::class)
    @Test
    fun test() {
        val time = measureTime {
            repeat(1_000_000) {
                RandomIDUtil.randomID()
            }
        }
        println(time)
        repeat(10) {
            println(RandomIDUtil.randomID())
        }
    }
}