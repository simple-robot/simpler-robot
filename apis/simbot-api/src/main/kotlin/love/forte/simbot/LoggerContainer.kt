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

package love.forte.simbot

import org.slf4j.Logger

/**
 *
 * 日志容器，代表当前目标内存在一个可用的 [Logger] 日志对象。
 *
 * @author ForteScarlet
 */
public interface LoggerContainer {

    /**
     * 当前目标内存在的日志对象。
     */
    public val logger: Logger

}