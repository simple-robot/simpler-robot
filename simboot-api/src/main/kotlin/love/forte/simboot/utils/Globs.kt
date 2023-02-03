/*
 * Copyright (c) 2008-2023 ForteScarlet <ForteScarlet@163.com>
 *
 * 本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x 、simbot3 等) 的一部分。
 * simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 * 发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 * 你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 * https://www.gnu.org/licenses
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 * https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 */
package love.forte.simboot.utils

import java.util.regex.PatternSyntaxException


@Suppress("MemberVisibilityCanBePrivate")
public object Globs {
    private const val regexMetaChars = ".^$+{[]|()"
    private const val globMetaChars = "\\*?[{"
    private fun isRegexMeta(c: Char): Boolean {
        return c in regexMetaChars
        //return regexMetaChars.indexOf(c) != -1
    }

    private fun isGlobMeta(c: Char): Boolean {
        return c in globMetaChars
    }

    private const val EOL = 0 //TBD
        .toChar()

    private fun next(glob: String, i: Int): Char {
        return if (i < glob.length) glob[i] else EOL
    }

    /**
     * Creates a regex pattern from the given glob expression.
     *
     * @throws  PatternSyntaxException
     */
    private fun toRegex(globPattern: String, isDos: Boolean): String {
        var inGroup = false
        var i = 0
        return buildString(globPattern.length) {
            append("^")
            while (i < globPattern.length) {
                var c = globPattern[i++]
                when (c) {
                    '\\' -> {
                        // escape special characters
                        if (i == globPattern.length) {
                            throw PatternSyntaxException(
                                "No character to escape",
                                globPattern, i - 1
                            )
                        }
                        val next = globPattern[i++]
                        if (isGlobMeta(next) || isRegexMeta(next)) {
                            append('\\')
                        }
                        append(next)
                    }
                    '/' -> if (isDos) {
                        append("\\\\")
                    } else {
                        append(c)
                    }
                    '[' -> {
                        // don't match name separator in class
                        if (isDos) {
                            append("[[^\\\\]&&[")
                        } else {
                            append("[[^/]&&[")
                        }
                        if (next(globPattern, i) == '^') {
                            // escape the regex negation char if it appears
                            append("\\^")
                            i++
                        } else {
                            // negation
                            if (next(globPattern, i) == '!') {
                                append('^')
                                i++
                            }
                            // hyphen allowed at start
                            if (next(globPattern, i) == '-') {
                                append('-')
                                i++
                            }
                        }
                        var hasRangeStart = false
                        var last = 0.toChar()
                        while (i < globPattern.length) {
                            c = globPattern[i++]
                            if (c == ']') {
                                break
                            }
                            if (c == '/' || isDos && c == '\\') {
                                throw PatternSyntaxException(
                                    "Explicit 'name separator' in class",
                                    globPattern, i - 1
                                )
                            }
                            // TBD: how to specify ']' in a class?
                            if (c == '\\' || c == '[' || c == '&' && next(globPattern, i) == '&') {
                                // escape '\', '[' or "&&" for regex class
                                append('\\')
                            }
                            append(c)
                            if (c == '-') {
                                if (!hasRangeStart) {
                                    throw PatternSyntaxException(
                                        "Invalid range",
                                        globPattern, i - 1
                                    )
                                }
                                if (next(globPattern, i++).also { c = it } == EOL || c == ']') {
                                    break
                                }
                                if (c < last) {
                                    throw PatternSyntaxException(
                                        "Invalid range",
                                        globPattern, i - 3
                                    )
                                }
                                append(c)
                                hasRangeStart = false
                            } else {
                                hasRangeStart = true
                                last = c
                            }
                        }
                        if (c != ']') {
                            throw PatternSyntaxException("Missing ']", globPattern, i - 1)
                        }
                        append("]]")
                    }
                    '{' -> {
                        if (inGroup) {
                            throw PatternSyntaxException(
                                "Cannot nest groups",
                                globPattern, i - 1
                            )
                        }
                        append("(?:(?:")
                        inGroup = true
                    }
                    '}' -> if (inGroup) {
                        append("))")
                        inGroup = false
                    } else {
                        append('}')
                    }
                    ',' -> if (inGroup) {
                        append(")|(?:")
                    } else {
                        append(',')
                    }
                    '*' -> if (next(globPattern, i) == '*') {
                        // crosses directory boundaries
                        append(".*")
                        i++
                    } else {
                        // within directory boundary
                        if (isDos) {
                            append("[^\\\\]*")
                        } else {
                            append("[^/]*")
                        }
                    }
                    '?' -> if (isDos) {
                        append("[^\\\\]")
                    } else {
                        append("[^/]")
                    }
                    else -> {
                        if (isRegexMeta(c)) {
                            append('\\')
                        }
                        append(c)
                    }
                }
            }
            if (inGroup) {
                throw PatternSyntaxException("Missing '}", globPattern, i - 1)
            }
            append('$')
        }
    }

    public fun toUnixRegex(glob: String): String {
        return toRegex(glob, false)
    }

    public fun toWindowsRegex(glob: String): String {
        return toRegex(glob, true)
    }

    public fun toRegex(glob: String): String {
        val isWindows = System.getProperty("os.name").contains("windows", true)
        return if (isWindows) {
            toWindowsRegex(glob)
        } else {
            toUnixRegex(glob)
        }

    }
}

