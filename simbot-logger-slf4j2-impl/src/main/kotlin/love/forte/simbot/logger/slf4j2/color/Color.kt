/*
 *     Copyright (c) 2020-2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
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
package love.forte.simbot.logger.slf4j2.color

/**
 * @author ForteScarlet
 */
public interface Color {
    public val colorIndex: Int
    public val isBackGround: Boolean get() = this is BackGroundColor
    public val isFont: Boolean get() = this is FontColor
    public val prefix: String
    public val suffix: String
}

public fun String.decorativeColor(color: Color): String {
    return "${color.prefix}$this${color.suffix}"
}

public fun StringBuilder.appendColor(color: Color, value: String): StringBuilder {
    return append(color.prefix).append(value).append(color.suffix)
}
public fun StringBuilder.appendColor(color: Color, value: Char): StringBuilder {
    return append(color.prefix).append(value).append(color.suffix)
}

public fun StringBuilder.appendColorPrefix(color: Color): StringBuilder {
    return append(color.prefix)
}

public fun StringBuilder.appendColorSuffix(color: Color): StringBuilder {
    return append(color.suffix)
}
