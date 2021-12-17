/*
 *  Copyright (c) 2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

import love.forte.simboot.filter.RegexMatcherValue
import org.junit.Test

/*
 *  Copyright (c) 2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

/**
 *
 * @author ForteScarlet
 */
class MatcherTest {

    @Test
    fun test() {
        val regex = "age={{age,\\d+}},num=(?<num>\\d+)"

        val value = RegexMatcherValue(regex)

        val parameters = value.getParameters("age=18,num=100")

        assert("18" == parameters["age"])
        assert("100" == parameters["num"])


    }

}