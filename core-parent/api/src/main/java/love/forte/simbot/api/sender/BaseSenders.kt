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

package love.forte.simbot.api.sender

/**
 * 提供一个[Sender]的基础抽象类，并提供一个方法的默认实现.
 */
public abstract class BaseSender(private val sender: Sender) : Sender by sender
/**
 * 提供一个[Setter]的基础抽象类，并提供一个方法的默认实现.
 */
public abstract class BaseSetter(private val setter: Setter) : Setter by setter
/**
 * 提供一个[Getter]的基础抽象类，并提供一个方法的默认实现.
 */
public abstract class BaseGetter(private val getter: Setter) : Setter by getter






