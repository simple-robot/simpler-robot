/*
 *  Copyright (c) 2020-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */
package love.forte.simboot.filter

/**
 * 匹配器动态参数获取器
 * @author ForteScarlet
 */
public interface MatchParameters {

    /**
     * 根据指定参数名称获取对应的提取参数。
     * @param key Key
     * @return Value or null.
     */
    public operator fun get(key: String): String?
}