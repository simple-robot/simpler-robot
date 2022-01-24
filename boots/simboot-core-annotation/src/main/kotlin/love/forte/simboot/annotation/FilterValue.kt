/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  根据 GNU LESSER GENERAL PUBLIC LICENSE 3 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simboot.annotation

/**
 * 指定一个参数，此参数为通过 [love.forte.simboot.filter.Keyword]
 * 解析而得到的动态参数提取器中的内容。
 *
 * @property value 所需动态参数的key。
 * @property required 对于参数绑定器来讲其是否为必须的。
 *  如果不是必须的，则在无法获取参数后传递null作为结果，否则将会抛出异常并交由后续绑定器处理。
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
public annotation class FilterValue(val value: String, val required: Boolean = true)
