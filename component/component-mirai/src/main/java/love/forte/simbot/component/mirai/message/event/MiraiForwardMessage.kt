/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     MiraiForwardMessage.kt
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

package love.forte.simbot.component.mirai.message.event

import net.mamoe.mirai.message.data.ForwardMessage


public class MiraiForwardMessage(private val forwardMessage: ForwardMessage) {
    fun r(){
        forwardMessage.preview
    }
}