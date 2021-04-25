/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

package love.forte.simbot.component.kaiheila.`object`

import love.forte.simbot.api.message.containers.OriginalDataContainer
import love.forte.simbot.api.message.results.Result

/*
    see https://developer.kaiheila.cn/doc/objects
 */


/**
 * 开黑啦文档中所定义的部分标准object规则的父接口。
 *
 * see https://developer.kaiheila.cn/doc/objects
 */
public interface KaiheilaObjects : Result, OriginalDataContainer
