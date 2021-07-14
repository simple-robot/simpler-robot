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

@file:JvmName("ListenerFunctionSwitchUtil")

package love.forte.simbot.listener

import love.forte.simbot.api.SimbotExperimentalApi
import org.jetbrains.annotations.Contract


/**
 *
 * 合并两个 [ListenerFunction.Switch], 使得一个 Switch 可以同时操作多个监听函数。
 * 合并后的switch中的 [ListenerFunction.Switch.isEnable] 得到的是 `all`. 也就是说只有当全部都为 `enable` 状态，才会得到 `true`.
 *
 */
@SimbotExperimentalApi
@Contract(pure = true)
@JvmSynthetic
public operator fun ListenerFunction.Switch.plus(otherSwitch: ListenerFunction.Switch): ListenerFunction.Switch =
    CombinedSwitch(this, otherSwitch)


/**
 * 合并两个 [ListenerFunction.Switch].
 *
 */
@OptIn(SimbotExperimentalApi::class)
private class CombinedSwitch(private val left: ListenerFunction.Switch, private val right: ListenerFunction.Switch) :
    ListenerFunction.Switch {
    @SimbotExperimentalApi
    override fun enable() {
        left.enable()
        right.enable()
    }

    @SimbotExperimentalApi
    override fun disable() {
        left.disable()
        right.disable()
    }

    override val isEnable: Boolean
        get() = left.isEnable && right.isEnable
}


// for Java
@SimbotExperimentalApi
@Contract(pure = true)
public fun merge(s1: ListenerFunction.Switch, s2: ListenerFunction.Switch): ListenerFunction.Switch = s1 + s2


/**
 * 为当前开关注册一个监听器，并返回一个注册了监听器后的 **新实例**。当使用返回的新实例再去调用 [ListenerFunction.Switch.enable] 或者 [ListenerFunction.Switch.disable] 便会触发监听事件。
 *
 * 不会影响到原本的 [ListenerFunction.Switch].
 *
 * 如果当前开关已经有监听器了，则新注册的监听器执行顺序会顺延（链式）。
 */
@SimbotExperimentalApi
@Contract(pure = true)
@JvmSynthetic
fun ListenerFunction.Switch.onSwitch(listener: ListenerFunction.Switch.Listener): ListenerFunction.Switch = ListenedAbleSwitch(this, listener)


@SimbotExperimentalApi
private class ListenedAbleSwitch(
    private val delegate: ListenerFunction.Switch,
    private val listener: ListenerFunction.Switch.Listener,
) : ListenerFunction.Switch {
    @SimbotExperimentalApi
    override fun enable() {
        listener.onSwitch(true)
        delegate.enable()
    }

    @SimbotExperimentalApi
    override fun disable() {
        listener.onSwitch(false)
        delegate.disable()
    }

    override val isEnable: Boolean
        get() = delegate.isEnable
}


// for Java
@SimbotExperimentalApi
@Contract(pure = true)
public fun registerListener(
    s: ListenerFunction.Switch,
    listener: ListenerFunction.Switch.Listener,
): ListenerFunction.Switch = s.onSwitch(listener)