/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  component-ding
 * File     specialMessage.kt
 * Date  2020/8/7 下午9:56
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.component.ding.messages

import love.forte.simbot.component.ding.exception.DingSpecialMessageException
import java.util.*


/**
 * 最终的消息json汇总链
 * 最终的msgtype会根据[DingSpecialMessageChain]的构造参数`spMessageChain`的第一个**非`at`**类型的消息为准。
 * 如果要作为发送消息的json串，请获取并转化[data]而不是此类自身
 */
@Suppress("JoinDeclarationAndAssignment", "MemberVisibilityCanBePrivate")
open class DingSpecialMessageChain(spMessageChain: Array<DingSpecialMessage>) {
    val msgType: String
    val data: Map<String, Any>
    init {
        msgType = spMessageChain.firstOrNull { it.type != "at" }?.type ?: throw DingSpecialMessageException("no msg type")
        val mutableMap = mutableMapOf<String, Any>("msgtype" to msgType)
        spMessageChain.asSequence().sortedBy { it.type == "at" }.map { it.type to it }.forEach {
            // merge
            mutableMap.merge(it.first, it.second) {
                oldVal, newVal ->
                if(oldVal is DingSpecialMessage && newVal is DingSpecialMessage) {
                    oldVal + newVal
//                    DingSpecialMessageMerger.merge(oldVal, newVal)
                }else {
                    newVal
                }
            }
        }

        data = mutableMap
    }

}



/**
 * 钉钉的特殊消息格式
 * [ding doc](https://ding-doc.dingtalk.com/doc#/serverapi3/iydd5h)
 * 目前似乎有以下几种消息类型：
 *  - text
 *  - link
 *  - markdown
 *  - 整体跳转ActionCard
 *  - 独立跳转ActionCard
 *  - FeedCard
 *
 *  一般来讲，不出意外的话以上几个类型的实现都是`data class`的形式
 *
 *  @param T 类型自己
 * @author ForteScarlet <ForteScarlet></ForteScarlet>@163.com>
 * @date 2020/8/7
 */
interface DingSpecialMessage: Comparable<DingSpecialMessage> {
    /**
     * 获取此特殊消息的消息类型
     * 在[DingSpecialMessageChain]中，当[type]相同的时候则认为其可以进行[plus]
     * 因此请尽可能在使用[DingSpecialMessageChain]的时候保证相同[type]所对应的类型唯一
     */
    val type: String

    /**
     * 获取他的一些参数
     * 其中，一般来讲，获取'msgtype'则为获取[.getType]
     * @param key 参数的key值
     * @return 获取到的值，nullable
     */
    fun get(key: String): Any?

    /**
     * 提供与相同类型的消息进行合并的能力
     * @param other 合并目标
     * @return 合并结果
     * @throws ClassCastException 如果经过父类/接口进行合并可能会导致类型转化异常
     */
    operator fun plus(other: DingSpecialMessage): DingSpecialMessage

    /**
     * 排序
     */
    override fun compareTo(other: DingSpecialMessage): Int
}

/** msgtype key */
internal const val MSG_TYPE_KEY = "msgtype"


/**
 * 统一的抽象父类，针对[DingSpecialMessage.plus]实现了同类型合并的方法
 * @param T type must be the current type, or override [checkType] and [transform]
 */
abstract class BaseDingSpecialMessage<T: DingSpecialMessage>(override val type: String): DingSpecialMessage {
    /**
     * 子类进行的同类型合并方法
     * @param other 其他的同类型类
     */
    abstract fun doPlus(other: T): T

    /**
     * 判断类型
     */
    open fun checkType(other: DingSpecialMessage): Boolean = other === this || this::class == other::class

    /**
     * 将一个额外的类型转化为当前类型
     */
    open fun transform(other: DingSpecialMessage): T = other as T

    /**
     * 继承了父接口[DingSpecialMessage]的合并，对类型进行判断
     */
    override fun plus(other: DingSpecialMessage): T {
        if(checkType(other)){
            return doPlus(transform(other))
        }else{
            throw DingSpecialMessageException("plus: ${other::class.qualifiedName}, ${this::class.qualifiedName}")
        }
    }
}

/**
 * 基于[MutableMap]的抽象类，[DingSpecialMessage.get]方法委托于[MutableMap]
 */
abstract class BaseMapDingSpecialMessage<T: DingSpecialMessage>(type: String, delegateMap: MutableMap<String, Any> = mutableMapOf()): BaseDingSpecialMessage<T>(type), MutableMap<String, Any> by delegateMap


/**
 * 无特殊实现的抽象类，需要自行实现[DingSpecialMessage.get]方法
 */
abstract class BaseNormalDingSpecialMessage<T: DingSpecialMessage>(type: String): BaseDingSpecialMessage<T>(type)


/**
 * 组成一对儿[Map.Entry]
 */
internal infix fun <K, V> K.with(other: V): Map.Entry<K, V> = AbstractMap.SimpleEntry(this, other)

/**
 * 与其他类型进行排序
 */
internal infix fun DingSpecialMessage.compareWith(other: DingSpecialMessage): Int = if(other.type == "at") 1 else this.type.compareTo(other.type)

/**
 * 单个消息转化为[DingSpecialMessageChain]
 */
fun DingSpecialMessage.toChain(): DingSpecialMessageChain = DingSpecialMessageChain(arrayOf(this))

/**
 * 多个消息转化为[DingSpecialMessageChain]
 */
fun Array<DingSpecialMessage>.toChain(): DingSpecialMessageChain = DingSpecialMessageChain(this)
