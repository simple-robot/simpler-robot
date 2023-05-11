/*
 * Copyright (c) 2020-2023 ForteScarlet.
 *
 * This file is part of Simple Robot.
 *
 * Simple Robot is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Simple Robot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Simple Robot. If not, see <https://www.gnu.org/licenses/>.
 */
package love.forte.simbot.logger.slf4j.color

/**
 * @author ForteScarlet <[163邮箱地址]ForteScarlet@163.com>
 * @since JDK1.8
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
