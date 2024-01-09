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
 * 背景颜色
 * @author ForteScarlet <[163邮箱地址]ForteScarlet@163.com>
 * @since JDK1.8
 */
public enum class BackGroundColor(
    /** 颜色代码  */
    override val colorIndex: Int,
    /**获取颜色代码  */
    private val toString: String
) : Color {
    //背景：40:黑 41:深红 42:绿 43:黄色 44:蓝色 45:紫色 46:深绿 47:白色。
    
    /** 黑  */
    BLACK(40, "\u001b[40mBLACK\u001b[0m"),
    
    /** 深红  */
    DARK_RED(41, "\u001b[41mDARK_RED\u001b[0m"),
    
    /** 绿  */
    GREEN(42, "\u001b[42mGREEN\u001b[0m"),
    
    /** 黄色  */
    YELLOW(43, "\u001b[43mYELLOW\u001b[0m"),
    
    /** 蓝色  */
    BLUE(44, "\u001b[44mBLUE\u001b[0m"),
    
    /** 紫色  */
    PURPLE(45, "\u001b[45mPURPLE\u001b[0m"),
    
    /** 深绿  */
    DARK_GREEN(46, "\u001b[46mDARK_GREEN\u001b[0m"),
    
    /** 白色  */
    WHITE(47, "\u001b[47mWHITE\u001b[0m");
    
    override val prefix: String = "\u001B[${colorIndex}m"
    override val suffix: String get() = "\u001B[0m"
    
    override fun toString(): String {
        return toString
    }
    
    public companion object {
        /** 通过颜色代码获取字体颜色枚举  */
        @JvmStatic
        public fun getColor(index: Int): BackGroundColor? {
            return entries.find { it.colorIndex == index }
        }
    }
}
