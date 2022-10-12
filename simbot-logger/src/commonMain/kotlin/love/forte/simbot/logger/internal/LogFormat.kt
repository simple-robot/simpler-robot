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