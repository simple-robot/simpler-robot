/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     MiraiMessageSource.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

package love.forte.simbot.component.mirai.message

import net.mamoe.mirai.Mirai
import net.mamoe.mirai.message.data.MessageSourceKind
import net.mamoe.mirai.message.data.buildMessageSource


fun demo() {
    Mirai.buildMessageSource(1, MessageSourceKind.GROUP) {
        ids = intArrayOf(1,2,3)
        internalIds = intArrayOf(5,6,7)
        time = 555
    }


}