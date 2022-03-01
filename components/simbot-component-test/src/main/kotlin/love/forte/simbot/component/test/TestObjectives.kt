/*
 *  Copyright (c) 2022-2022 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x、simbot 3.x、simbot3) 的一部分。
 *
 *  simply-robot 是自由软件：你可以再分发之和/或依照由自由软件基金会发布的 GNU 通用公共许可证修改之，无论是版本 3 许可证，还是（按你的决定）任何以后版都可以。
 *
 *  发布 simply-robot 是希望它能有用，但是并无保障;甚至连可销售和符合某个特定的目的都不保证。请参看 GNU 通用公共许可证，了解详情。
 *
 *  你应该随程序获得一份 GNU 通用公共许可证的复本。如果没有，请看:
 *  https://www.gnu.org/licenses
 *  https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *  https://www.gnu.org/licenses/lgpl-3.0-standalone.html
 *
 */

package love.forte.simbot.component.test

import kotlinx.coroutines.flow.*
import love.forte.simbot.*
import love.forte.simbot.definition.*
import love.forte.simbot.message.*


/**
 * 用于生成对应测试组件对象的生成器。
 *
 * 需要注意生成器会在以下情况直接使用：
 * - 需要查询列表
 * - 需要查询指定id的对象
 *
 * 也就是说test模块下相关对象不会缓存生成器的相关结果，因此如果非必要，不应考虑设置为无限量的值或完全随机的值。
 *
 */
public interface TestGenerator<T> {
    /**
     * 最大产生数量。
     */
    public val max: Int

    /**
     * 产生一个对象。
     *
     * @param index 所产生的元素索引。最大的 [index] 应该等于 [max] - 1.
     */
    public fun generate(index: Int): T

}

/**
 * 构建一个 [TestGenerator] 实例。
 */
public fun <T> testGenerator(max: Int, generator: (index: Int) -> T): TestGenerator<T> =
    object : TestGenerator<T> {
        override val max: Int = max
        override fun generate(index: Int): T = generator(index)
    }


/**
 * 将 [TestGenerator] 转化为 [Flow].
 */
public inline fun <T> TestGenerator<T>.asFlow(
    batch: Int = 0,
    crossinline preBatch: suspend FlowCollector<T>.(index: Int) -> Unit = {}
): Flow<T> =
    if (batch == 0) {
        flow {
            repeat(max) {
                emit(generate(it))
            }
        }
    } else {
        flow {
            repeat(max) {
                if (it == 0 || it % batch == 0) {
                    preBatch(it)
                }
                emit(generate(it))
            }
        }
    }

/**
 * 将 [TestGenerator] 转化为 [Flow].
 */
public inline fun <T> TestGenerator<T>.asSequence(
    batch: Int = 0,
    crossinline preBatch: SequenceScope<T>.(index: Int) -> Unit = {}
): Sequence<T> =
    if (batch == 0) {
        sequence {
            repeat(max) {
                yield(generate(it))
            }
        }
    } else {
        sequence {
            repeat(max) {
                if (it == 0 || it % batch == 0) {
                    preBatch(it)
                }
                yield(generate(it))
            }
        }
    }


/**
 * test模块下使用的 [Friend] 类型。
 * @author ForteScarlet
 *
 * @see TestFriendImpl
 */
public interface TestFriend : Friend {
    override val bot: TestBot

    override val id: ID
    override val remark: String?
    override val grouping: Grouping
    override val username: String
    override val avatar: String
    override val status: UserStatus

    override suspend fun send(message: MessageContent): MessageReceipt
    override suspend fun send(text: String): MessageReceipt
    override suspend fun send(message: Message): MessageReceipt


    @Api4J
    override fun sendIfSupportBlocking(message: Message): MessageReceipt = sendBlocking(message)

}


/**
 * test模块下使用的 [Group] 实现。
 * @author ForteScarlet
 */
public interface TestGroup : Group {
    override val bot: TestBot

    override val id: ID
    override val ownerId: ID
    override val name: String
    override val icon: String
    override val description: String
    override val createTime: Timestamp

    @Api4J
    override val previous: Organization?
        get() = null


}


/**
 * test模块下使用的 [GroupMember] 实现。
 * @author ForteScarlet
 */
public interface TestGroupMember : GroupMember {
    override val bot: TestBot
}


/**
 * test模块下使用的 [Guild] 实现。
 * @author ForteScarlet
 */
public interface TestGuild : Guild {
    override val bot: TestBot
}


/**
 * test模块下使用的 [Guild] 实现。
 * @author ForteScarlet
 */
public interface TestChannel : Channel {
    override val bot: TestBot
}


/**
 * test模块下使用的 [GuildMember] 实现。
 * @author ForteScarlet
 */
public interface TestGuildMember : GuildMember {
    override val bot: TestBot
}







