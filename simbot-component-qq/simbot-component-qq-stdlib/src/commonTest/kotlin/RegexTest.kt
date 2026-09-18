/*
 *     Copyright (c) 2022-2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
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
