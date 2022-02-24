/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     ColorTypes.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */
package love.forte.simbot.logger.color

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