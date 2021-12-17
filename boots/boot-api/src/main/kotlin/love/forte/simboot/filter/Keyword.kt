/*
 *  Copyright (c) 2020-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
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




