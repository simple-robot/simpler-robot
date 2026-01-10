/*
 *  Copyright (c) 2022-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simbot-component-tencent-guild 的一部分。
 *
 *  simbot-component-tencent-guild 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simbot-component-tencent-guild 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *
 */

import kotlin.test.Test

/**
 *
 * @author ForteScarlet
 */
class RegexTest {

    @Test
    fun test() {
        val regex = Regex("<@!(?<uid>\\d+)>|<#!(?<cid>\\d+)>")

        val text = "<@!15568778634248196340> aaa <@!123123123123><#!666666666> abcd"

        var lastTextIndex = 0

        regex.findAll(text).forEach {
            if (it.range.first != lastTextIndex) {
                println("text:  '${text.substring(lastTextIndex until it.range.first)}'")

            }
            lastTextIndex = it.range.last + 1
            println("regex: '${it.value}'")
        }
        if (lastTextIndex != text.length) {
            println("text:  '${text.substring(lastTextIndex)}'")
        }


    }

}