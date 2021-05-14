/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
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

package love.forte.simbot.filter

import love.forte.simbot.api.message.events.MsgGet
import love.forte.simbot.mark.ThreadUnsafe
import java.util.*


/**
 * at检测器。用于判断bot是否被at了。
 */
@ThreadUnsafe
public interface AtDetection {

    /**
     * 如果bot被at了，则返回true。
     *
     * ### since 2.0.0-RC.2
     *
     * 如果当前消息类型为 [私聊消息][love.forte.simbot.api.message.events.PrivateMsg], 则直接视为 `true`。
     */
    fun atBot(): Boolean

    /**
     * at了全体。
     *
     * ### since 2.0.0-RC.2
     *
     * 如果当前消息类型为 [私聊消息][love.forte.simbot.api.message.events.PrivateMsg], 则直接视为 `true`。
     */
    fun atAll(): Boolean

    /**
     * at了任意一个人。
     *
     * ### since 2.0.0-RC.2
     *
     * 如果当前消息类型为 [私聊消息][love.forte.simbot.api.message.events.PrivateMsg], 则直接视为 `true`。
     */
    fun atAny(): Boolean

    /**
     * at了指定的这些用户。
     */
    fun at(codes: Array<String>): Boolean
}

/**
 * 总是允许的 [AtDetection] 实例。
 */
public object AlwaysAllowedAtDetection : AtDetection {
    override fun atBot(): Boolean = true
    override fun atAll(): Boolean = true
    override fun atAny(): Boolean = true
    override fun at(codes: Array<String>): Boolean = true
}

/**
 * 总是拒绝的 [AtDetection] 实例。
 */
public object AlwaysRefuseAtDetection : AtDetection {
    override fun atBot(): Boolean = false
    override fun atAll(): Boolean = false
    override fun atAny(): Boolean = false
    override fun at(codes: Array<String>): Boolean = false
}

/**
 * 使用固定常量值的 [AtDetection] 实例。
 */
public data class ConstantAtDetection(
    private val atBot: Boolean,
    private val atAll: Boolean,
    private val atAny: Boolean,
    private val at: Boolean,
) : AtDetection {
    override fun atBot(): Boolean = atBot
    override fun atAll(): Boolean = atAll
    override fun atAny(): Boolean = atAny
    override fun at(codes: Array<String>): Boolean = at
}


/**
 *
 * 一个内置缓存的 [AtDetection] 抽象类。
 *
 * 此类中，[atBot]、[atAny]、[atAll] 在最多调用一次后将会被缓存，后续不会再进行判断。
 *
 * 首次判断的逻辑通过实现对应抽象方法[checkAtBot]、[checkAtAny]、[checkAtAll] 实现。
 *
 * 此类提供了一些方法可用于初始化那些可能存在默认值的属性, 例如可能出现AtAll永远等于true的情况。
 * 这时候，重写 [initAtBot]、[initAtAll]、[initAtAny] 中对应的方法来实现初始化一个默认值。
 *
 * 这三个函数默认情况下不会初始化一个默认值。
 *
 *
 * [at] 也提供了一个用于初始化一个以存 At-Set 的方法来用于获取所有的 at-code: [initCodes]
 *
 * 当 [at] 第一次被调用的时候会执行 [initCodes] 并初始化当前消息中所有已存在的账号信息，以便后续使用。
 *
 *
 */
public abstract class CacheableAtDetection : AtDetection {
    private sealed class Answer {
        internal object NotInit : Answer()
        internal sealed class Checked(val c: Boolean) : Answer() {
            internal companion object {
                internal fun check(c: Boolean) = if (c) OK else NO
            }

            internal object OK : Checked(true)
            internal object NO : Checked(false)
        }
    }

    private var _atBot: Answer = Answer.NotInit
    private var _atAll: Answer = Answer.NotInit
    private var _atAny: Answer = Answer.NotInit

    // init {
    //     _atBot = initAtBot()
    //     _atAll = initAtAll()
    //     _atAny = initAtAny()
    // }

    private lateinit var _codeCache: Set<String>
    private val codeCache: Set<String>
        get() {
            if (!::_codeCache.isInitialized) {
                _codeCache = TreeSet<String>().also { set ->
                    initCodes { code -> set.add(code) }
                }

            }
            return _codeCache
        }

    // private fun initAtBot(): Answer = atBotInit()?.let { b -> Answer.Checked.check(b) } ?: Answer.NotInit
    // private fun initAtAll(): Answer = atAllInit()?.let { b -> Answer.Checked.check(b) } ?: Answer.NotInit
    // private fun initAtAny(): Answer = atAnyInit()?.let { b -> Answer.Checked.check(b) } ?: Answer.NotInit

    /** 如果atBot有一个固定的初始默认状态，则返回，否则返回null。 */
    protected open fun atBotInit(): Boolean? = null
    /** 如果atAll有一个固定的初始默认状态，则返回，否则返回null。 */
    protected open fun atAllInit(): Boolean? = null
    /** 如果atAny有一个固定的初始默认状态，则返回，否则返回null。 */
    protected open fun atAnyInit(): Boolean? = null

    /** 判断是否atBot的函数。 */
    protected abstract fun checkAtBot(): Boolean
    /** 判断是否atAll的函数。 */
    protected abstract fun checkAtAll(): Boolean
    /** 判断是否atAny的函数。 */
    protected abstract fun checkAtAny(): Boolean


    final override fun atBot(): Boolean {
        return if (_atBot === Answer.NotInit) {
            checkAtBot().also {
                _atBot = Answer.Checked.check(it)
            }
        } else (_atBot as Answer.Checked).c
    }

    final override fun atAll(): Boolean {
        return if (_atAll === Answer.NotInit) {
            checkAtAll().also {
                _atAll = Answer.Checked.check(it)
            }
        } else (_atAll as Answer.Checked).c
    }

    final override fun atAny(): Boolean {
        return if (_atAny === Answer.NotInit) {
            checkAtAny().also {
                _atAny = Answer.Checked.check(it)
            }
        } else (_atAny as Answer.Checked).c
    }


    /**
     * 提供一个账号添加器，通过实现此函数来初始化当前消息中所有已存的账号信息。
     */
    protected abstract fun initCodes(adder: CodeAdder)


    final override fun at(codes: Array<String>): Boolean = codes.all { codeCache.contains(it) }

}


/**
 * 用于 [CacheableAtDetection] 的缓存code添加器。
 */
public fun interface CodeAdder {
    fun add(code: String): Boolean
}









/**
 * [AtDetection] 工厂。
 */
public interface AtDetectionFactory {

    /**
     * 根据一个msg实例构建一个 [AtDetection] 函数。
     *
     * 在manager中，如果此方法返回了一个 null 则视为获取失败，会去尝试使用其他 factory 直至成功。
     *
     */
    fun getAtDetection(msg: MsgGet): AtDetection
}



/**
 * [AtDetection] 注册器。
 */
public interface AtDetectionRegistrar {
    /**
     * 注册一个 [AtDetection] 构建函数。
     * 默认为向尾部注册。
     */
    fun registryAtDetection(atDetectionFactory: AtDetectionFactory)

    /**
     * 向首部注册一个 [AtDetection] 构建函数。
     */
    fun registryAtDetectionFirst(atDetectionFactory: AtDetectionFactory)

}
