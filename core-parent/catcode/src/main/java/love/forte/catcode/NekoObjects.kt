/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     NekoObjects.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.catcode

import love.forte.catcode.codes.Nyanko

/*
    提供一些可以作为单例使用的[KQCode]实例
 */

/**
 * at all
 * `[CAT:at,code=all]`
 */
object NekoAtAll : Neko by Nyanko.byCode("${CAT_HEAD}at,code=all$CAT_END")

/**
 * rps 猜拳
 * 发送用的猜拳
 * `[CAT:rps]`
 */
object NekoRps : Neko by EmptyNeko("rps")


/**
 * dice 骰子
 * 发送用的骰子
 * `[CAT:dice]`
 */
object NekoDice : Neko by EmptyNeko("dice")


/**
 * 窗口抖动，戳一戳
 * `[CAT:shake]`
 */
object NekoShake : Neko by EmptyNeko("shake")






