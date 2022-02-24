/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     FontColorTypes.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */
package love.forte.simbot.logger.color

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
            return values().find { it.colorIndex == index }
        }
    }
}