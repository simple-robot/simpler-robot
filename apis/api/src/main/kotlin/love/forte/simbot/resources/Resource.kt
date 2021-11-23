/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.resources

import java.nio.channels.ByteChannel

/**
 *
 * 一个[资源][Resource].
 *
 * 此资源通常指文件或者数据流资源。
 *
 * 资源可能是配置文件，亦或是一个[多媒体资源][MultimediaResource]。
 *
 *
 * @author ForteScarlet
 */
public interface Resource {

    /**
     * 得到资源名称。
     */
    public val name: String

    /**
     * 得到资源的byte数据。
     */
    public suspend fun data(): ByteChannel

}

