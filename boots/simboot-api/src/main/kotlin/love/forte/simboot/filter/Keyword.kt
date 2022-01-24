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
 * 匹配关键词.
 *
 */
public interface Keyword {
    /** 匹配关键字对应的正则实例。 */
    public val regex: Regex

    /** 匹配关键字对应的原始文本。 */
    public val text: String

    /** 匹配关键字对应的参数提取器。 */
    public val matcherValue: MatcherValue
}




