/*
 * Copyright (c) 2021-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */

package love.forte.simboot.filter

public object EmptyFilterParameterMatcher : MatcherValue {
    override val original: String get() = ""
    override val regex: Regex = Regex("")
    override fun matches(text: String): Boolean = true
    override fun getParam(name: String, text: String): String? = null
    override fun getParameters(text: String?): MatchParameters = EmptyFilterParameters
}


public object EmptyKeyword : Keyword {
    override val regex: Regex
        get() = EmptyFilterParameterMatcher.regex

    override val text: String
        get() = ""

    override val matcherValue: MatcherValue
        get() = EmptyFilterParameterMatcher
}


public object EmptyFilterParameters : MatchParameters {
    override fun get(key: String): String? = null
}
