/*
 *     Copyright (c) 2023-2024. ForteScarlet.
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

package love.forte.simbot.logger.internal

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

@Suppress("RegExpRedundantEscape")
private val regex = Regex("\\{\\}")


/**
 * format log string with args.
 *
 * `"{} world, number {}" ('hello', 23)` -> `hello world, number 23`.
 *
 * @param onRemainingArgNumber the number or remaining args. For example, if it is 0, it means there is nothing remain.
 *
 */
@OptIn(ExperimentalContracts::class)
internal inline fun String.logFormat(args: Array<*>, onRemainingArgNumber: (Int) -> Unit = {}): String {
    contract {
        callsInPlace(onRemainingArgNumber, InvocationKind.EXACTLY_ONCE)
    }
    if (args.isEmpty()) {
        onRemainingArgNumber(0)
        return this
    }
    var match: MatchResult? = regex.find(this)
    if (match == null) {
        onRemainingArgNumber(args.size)
        return this
    }

    var lastStart = 0
    val length = this.length
    val sb = StringBuilder(length)
    var argIndex = 0
    val lastIndex = args.lastIndex
    do {
        val foundMatch = match!!
        sb.append(this, lastStart, foundMatch.range.first)
        sb.append(args[argIndex++])
        lastStart = foundMatch.range.last + 1
        match = foundMatch.next()
    } while (lastStart < length && match != null && argIndex <= lastIndex)

    if (lastStart < length) {
        sb.append(this, lastStart, length)
    }

    onRemainingArgNumber(args.size - argIndex)

    return sb.toString()
}
