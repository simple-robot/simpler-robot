/*
 *     Copyright (c) 2024. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library.
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     Lesser GNU General Public License for more details.
 *
 *     You should have received a copy of the Lesser GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package love.forte.simbot.test.bot

import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import love.forte.simbot.bot.*
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.UUID
import love.forte.simbot.component.Component
import love.forte.simbot.test.component.TestComponent
import love.forte.simbot.test.component.TestComponentConfiguration
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


/**
 * 用于测试的 [Bot] 实现。
 * @author ForteScarlet
 */
@Suppress("MemberVisibilityCanBePrivate")
public open class TestBot(
    override var component: Component = TestComponent(TestComponentConfiguration()),
    public var configuration: TestBotConfiguration = TestBotConfiguration(),
    coroutineContext: CoroutineContext = configuration.coroutineContext
) : JobBasedBot() {
    final override val coroutineContext: CoroutineContext
    final override val job: Job

    init {
        val j = coroutineContext[Job]
        val c = coroutineContext.minusKey(Job)
        this.job = SupervisorJob(j)
        this.coroutineContext = c
    }

    override var id: ID = configuration.id.ID
    override var name: String = configuration.name

    override fun isMe(id: ID): Boolean = this.id == id

    override suspend fun start() {
        isStarted = true
    }

    override var guildRelation: GuildRelation? = null
    override var groupRelation: GroupRelation? = null
    override var contactRelation: ContactRelation? = null
}

/**
 * [TestBot] 的配置类。
 */
@Serializable
@SerialName(TestComponent.ID_VALUE)
public data class TestBotConfiguration(
    var id: String = UUID.random().toString(),
    var name: String = id,
) : SerializableBotConfiguration() {
    @Transient
    var coroutineContext: CoroutineContext = EmptyCoroutineContext
}
