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

package love.forte.simbot.component.mirai.message

import love.forte.simbot.SimbotIllegalStateException
import love.forte.simbot.api.message.MessageReconstructor


/**
 * mirai组件实现的 [MessageReconstructor].
 * @author ForteScarlet
 */
public interface MiraiMessageReconstructor : MessageReconstructor {

    override fun at(): MiraiMessageReconstructor.MiraiAtAction

    override fun face(): MiraiMessageReconstructor.MiraiFaceAction

    override fun image(): MiraiMessageReconstructor.MiraiImageAction

    override fun text(): MiraiMessageReconstructor.MiraiTextAction

    /**
     * mirai的重构器操作
     */
    public interface MiraiAction : MessageReconstructor.Action


    public interface MiraiAtAction : MiraiAction, MessageReconstructor.AtAction
    public interface MiraiFaceAction : MiraiAction, MessageReconstructor.FaceAction
    public interface MiraiImageAction : MiraiAction, MessageReconstructor.ImageAction
    public interface MiraiTextAction : MiraiAction, MessageReconstructor.TextAction

}



/**
 * mirai组件消息重构异常.
 */
public open class MiraiMessageRefactoringException : SimbotIllegalStateException {
    constructor() : super()
    constructor(s: String?) : super(s)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
}



