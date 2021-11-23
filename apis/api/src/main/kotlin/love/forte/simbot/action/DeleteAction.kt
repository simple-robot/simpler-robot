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

package love.forte.simbot.action



/**
 * 允许一种删除行为。
 * 标记一个消息为可删除的，通常可理解为是可撤回的。
 *
 * 一般用于消息回执或者从远端接收到的消息事件上。
 * @author ForteScarlet
 */
public interface DeleteAction : Action {

    /**
     * 通常情况下，删除行为是一种主动行为。
     */
    override val actionType: ActionType
        get() = ActionType.PROACTIVE

    /**
     * 删除当前目标。
     *
     * @return 是否删除成功
     */
    public suspend fun delete(): Boolean

}

// 提供者