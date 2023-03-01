/*
 * Copyright (c) 2008-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
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

