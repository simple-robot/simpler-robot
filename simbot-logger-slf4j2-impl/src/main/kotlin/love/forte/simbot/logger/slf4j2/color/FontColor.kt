/*
 *     Copyright (c) 2022-2024. ForteScarlet.
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
 * 字体颜色
 * @author ForteScarlet <[163邮箱地址]ForteScarlet@163.com>
 * @since JDK1.8
 */
public enum class FontColor(
    /** 颜色代码  */
    override val colorIndex: Int,
    /**获取颜色代码  */
    private val toString: String
) : Color {
    //字体颜色：30:黑 31:红 32:绿 33:黄 34:蓝色 35:紫色 36:深绿 37:白色
    /** 黑  */
    BLACK(30, "\u001b[30mBLACK\u001b[0m"),
    
    /** 红  */
    RED(31, "\u001b[31mRED\u001b[0m"),
    
    /** 绿  */
    GREEN(32, "\u001b[32mGREEN\u001b[0m"),
    
    /** 黄  */
    YELLOW(33, "\u001b[33mYELLOW\u001b[0m"),
    
    /** 蓝色  */
    BLUE(34, "\u001b[34mBLUE\u001b[0m"),
    
    /** 紫色  */
    PURPLE(35, "\u001b[35mPURPLE\u001b[0m"),
    
    /** 深绿  */
    DARK_GREEN(36, "\u001b[36mDARK_GREEN\u001b[0m"),
    
    /** 白色  */
    WHITE(37, "\u001b[37mWHITE\u001b[0m");
    
    override val prefix: String = "\u001B[${colorIndex}m"
    override val suffix: String get() = "\u001B[0m"
    
    override fun toString(): String {
        return toString
    }
    
    public companion object {
        /** 通过颜色代码获取字体颜色枚举  */
        @JvmStatic
        public fun getColor(index: Int): FontColor? {
            return entries.find { it.colorIndex == index }
        }
    }
}
