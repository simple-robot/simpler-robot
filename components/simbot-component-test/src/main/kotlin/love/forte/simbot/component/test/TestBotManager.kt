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

import kotlinx.serialization.*
import kotlinx.serialization.json.*
import love.forte.simbot.*
import love.forte.simbot.component.test.ComponentTest.DEFAULT_AVATAR
import love.forte.simbot.component.test.internal.*
import love.forte.simbot.definition.*
import love.forte.simbot.event.*
import org.slf4j.*
import org.slf4j.LoggerFactory
import kotlin.coroutines.*
import kotlin.time.Duration.Companion.days

/**
 * test组件的bot管理器。
 *
 * @author ForteScarlet
 */
public abstract class TestBotManager : BotManager<TestBot>() {
    /**
     * 得到配置信息。
     */
    public abstract val configuration: TestBotManagerConfiguration

    /**
     * 通过 [BotVerifyInfo] 注册bot信息。
     */
    @OptIn(ExperimentalSerializationApi::class)
    override fun register(verifyInfo: BotVerifyInfo): TestBot {
        val json = Json {
            isLenient = true
            ignoreUnknownKeys = true
        }
        val info = verifyInfo.inputStream().use { json.decodeFromStream(TestBotVerifyInfo.serializer(), it) }
        if (info.component != ComponentTest.COMPONENT_ID) {
            throw ComponentMismatchException("${info.component} is not ${ComponentTest.COMPONENT_ID}")
        }

        return register(
            TestBotConfiguration(
                id = info.id.ID,
                username = info.username,
                avatar = info.avatar
            )
        )
    }

    /**
     * 通过 [TestBotConfiguration] 注册一个 [TestBot] 实例。
     */
    public abstract fun register(configuration: TestBotConfiguration): TestBot


    public companion object {
        @JvmStatic
        public fun getInstance(
            eventProcessor: EventProcessor,
            configuration: TestBotManagerConfiguration = TestBotManagerConfiguration()
        ): TestBotManager {
            return TestBotManagerImpl(eventProcessor, configuration)
        }
    }
}


@kotlinx.serialization.Serializable
internal data class TestBotVerifyInfo(
    val component: String,
    val id: String,
    val username: String,
    val avatar: String = "https://p.qlogo.cn/gh/$id/${id}/640",
)


@Retention(AnnotationRetention.SOURCE)
internal annotation class TestBotManagerDSL

/**
 * 使用DSL构建 [TestBotManager] 实例。
 *
 * ```kotlin
 * val manager = testBotManager(eventProcessor)
 * ```
 *
 * ```kotlin
 * val manager = testBotManager(eventProcessor) {
 *      // ...
 * }
 * ```
 */
@TestBotManagerDSL
public fun testBotManager(
    eventProcessor: EventProcessor,
    block: TestBotManagerConfiguration.() -> Unit = {}
): TestBotManager {
    val config = testBotManagerConfiguration(block)
    return TestBotManager.getInstance(eventProcessor, config)
}

/**
 * 以DSL风格构建和配置 [TestBotManagerConfiguration] 实例。
 *
 * ```kotlin
 * val config = testBotManagerConfiguration {
 *      apiDelay = 20L .. 50L
 *      // and other..
 * }
 * ```
 */
@TestBotManagerDSL
public inline fun testBotManagerConfiguration(block: TestBotManagerConfiguration.() -> Unit): TestBotManagerConfiguration {
    return TestBotManagerConfiguration().also(block)
}

/**
 * [TestBotManager] 的配置类.
 */
@Suppress("MemberVisibilityCanBePrivate")
public class TestBotManagerConfiguration {

    /**
     * 可以提供一个使用的协程上下文。如果其中存在 [kotlinx.coroutines.Job], 则会作为 parentJob 使用。
     */
    public var coroutineContext: CoroutineContext = EmptyCoroutineContext

    //region api delay

    /**
     * 所有可挂起api（例如 [Bot.friend]、[Friend.send] 等）的挂起时间的随机范围。
     * 这些api在进行模拟执行的时候会通过此参数进行一个随机的延时.
     *
     * @throws IllegalArgumentException 如果 [apiDelay][apiDelay] [.first][LongRange.first] 大于 [apiDelay][apiDelay] [.last][LongRange.last]
     */
    @TestBotManagerDSL
    public var apiDelay: LongRange = 10L..100L
        set(value) {
            require(value.first <= value.last) { "The range first value must <= the range last value. but first(${value.first}) > last(${value.last})." }
            field = value
        }

    /**
     * 设置 [apiDelay] 的随机周期。
     *
     * @param from 随机范围左端点
     * @param to 随机范围右端点，默认等于 [from]
     *
     * @see apiDelay
     */
    @JvmOverloads
    @TestBotManagerDSL
    public fun setApiDelay(from: Long, to: Long = from) {
        apiDelay = from..to
    }

    /**
     * 不让api进行延时，也就是说 [apiDelay] 的随机结果将恒为0。
     *
     * @see apiDelay
     */
    @TestBotManagerDSL
    public fun disableApiDelay() {
        apiDelay = 0L..0L
    }

    //endregion


    //region api callback
    /**
     * 部分api调用后的回调函数。
     * @see ApiCallback
     */
    @TestBotManagerDSL
    public var apiCallback: ApiCallback? = null

    /**
     * 部分api调用后的回调函数。
     * ```kotlin
     * apiCallback { bot, api, invoker, arguments ->
     *     // do...
     * }
     * ```
     *
     * @see ApiCallback
     */
    @TestBotManagerDSL
    public fun apiCallback(apiCallback: ApiCallback) {
        this.apiCallback = apiCallback
    }


    /**
     * test组件下调用挂起api时的回调函数类型。
     */
    public fun interface ApiCallback {

        /**
         *
         * @param bot 执行此api的相关bot。
         * @param api 此api的名称。一般为调用所在类.api函数，例如 `Friend.send` 等。部分阻塞api可能会采用原始的可挂起api名称。此名称仅供参考，不作为恒久不变的标准。
         * @param invoker 被调用api的对象。一般可能是指 [Bot]、[事件][love.forte.simbot.event.Event] 或 [对象][love.forte.simbot.definition.Objectives]。
         * 例如一个api调用为 `friend.send(..)`, 则 `friend` 即为此 invoker。
         * @param arguments 调用此api的参数列表。
         */
        public operator fun invoke(bot: TestBot, api: String, invoker: Any, vararg arguments: Any?)

    }
    //endregion

    //region api call logger
    /**
     * 部分API调用时会输出 debug 日志, 用于指定特定的输出日志。
     */
    @TestBotManagerDSL
    public var apiCallLoggerFactory: (TestBotConfiguration) -> Logger =
        { LoggerFactory.getLogger("love.forte.simbot.component.test.bot.${it.id}") }
    //endregion


    /**
     * 一些相关的对象生成器。
     */
    @TestBotManagerDSL
    public var generators: GeneratorsConfiguration = GeneratorsConfiguration()


    /**
     * 使用 [generators] 进行配置。
     *
     *
     * ```kotlin
     * generators {
     *     friendGenerator { managerConfig, bot ->
     *         // do...
     *     }
     * }
     * ```
     *
     */
    @TestBotManagerDSL
    public fun generators(block: GeneratorsConfiguration.() -> Unit) {
        generators.also(block)
    }


    //region internal fun
    internal suspend fun delay() {
        kotlinx.coroutines.delay(apiDelay.random())
    }

    @Throws(InterruptedException::class)
    internal fun sleep() {
        Thread.sleep(apiDelay.random())
    }

    internal fun callback(bot: TestBot, api: String, invoker: Any, vararg arguments: Any?) {
        apiCallback?.invoke(bot, api, invoker, *arguments)
    }

    //endregion

}


/**
 * [TestBotManagerConfiguration] 中用于记录所有 [TestGenerator] 的实例。
 */
@Suppress("MemberVisibilityCanBePrivate")
public class GeneratorsConfiguration {

    private val defaultUserStatus = UserStatus.builder().normal().build()

    //region 好友相关
    /**
     * 通过 [TestBotManagerConfiguration] 和 [TestBot] 构建 [TestFriend] 的生成器。
     *
     * 默认情况下，[friendGenerator] 最大产生100个好友。
     */
    public var friendGenerator: (managerConfig: TestBotManagerConfiguration, bot: TestBot) -> TestGenerator<TestFriend> =
        { managerConfig, bot ->
            testGenerator(100) {
                TestFriendImpl(
                    bot = bot,
                    id = (3L * it).ID,
                    remark = null,
                    grouping = Grouping.EMPTY,
                    username = "Friend No.$it",
                    avatar = ComponentTest.DEFAULT_AVATAR,
                    status = defaultUserStatus,
                    configuration = managerConfig,
                )
            }
        }

    /**
     * 配置 [friendGenerator]
     * ```kotlin
     * generators.friendGenerator { managerConfig, bot ->
     *    // do...
     *  }
     * ```
     *
     */
    public fun friendGenerator(block: (managerConfig: TestBotManagerConfiguration, bot: TestBot) -> TestGenerator<TestFriend>) {
        friendGenerator = block
    }
    //endregion


    /**
     * 通过 [TestBotManagerConfiguration] 和 [Group] 构建 [TestFriend] 的生成器。
     *
     * 默认情况下，[groupMemberGenerator] 最大产生100 ~ 700个群成员不等，且每次产生的群成员中，第一个成员为拥有者，前10个成员为管理者。
     */
    public var groupMemberGenerator: (managerConfig: TestBotManagerConfiguration, group: TestGroup) -> TestGenerator<TestGroupMember> =
        { managerConfig, group ->
            val ownerRole: List<Role> = listOf(TestRole.OWNER)
            val adminRole: List<Role> = listOf(TestRole.ADMIN)
            val memberRole: List<Role> = listOf(TestRole.MEMBER)
            testGenerator(100) {
                TestGroupMemberImpl(
                    id = (group.id.literal + it).ID,
                    bot = group.bot,
                    roleList = when {
                        it == 0 -> ownerRole
                        it < 10 -> adminRole
                        else -> memberRole
                    },
                    nickname = when {
                        it == 0 -> "Owner()"
                        it < 10 -> "Owner()"
                        else -> "Owner()"
                    },
                    // 2022-02-02 22:22:22 and then
                    joinTime = Timestamp.byMillisecond(BASE_JOIN_TIME + it.days.inWholeMilliseconds),
                    username = "GroupMember No.$it",
                    avatar = DEFAULT_AVATAR,
                    status = defaultUserStatus,
                    fromGroup = group,
                    configuration = managerConfig,
                )
            }
        }

    /**
     * 配置 [friendGenerator]
     * ```kotlin
     * generators.groupMemberGenerator { managerConfig, group ->
     *    // do...
     *  }
     * ```
     *
     */
    public fun groupMemberGenerator(block: (managerConfig: TestBotManagerConfiguration, group: TestGroup) -> TestGenerator<TestGroupMember>) {
        groupMemberGenerator = block
    }

    private companion object {
        const val DEFAULT_CREATE_TIME = 1643811742000L
        const val BASE_JOIN_TIME = DEFAULT_CREATE_TIME
    }

}

