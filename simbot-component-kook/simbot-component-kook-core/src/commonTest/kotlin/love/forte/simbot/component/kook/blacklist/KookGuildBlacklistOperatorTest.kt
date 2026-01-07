/*
 *     Copyright (c) 2025-2026. ForteScarlet.
 *
 *     Project    https://github.com/simple-robot/simpler-robot
 *     Email      ForteScarlet@163.com
 *
 *     This file is part of the Simple Robot Library (Alias: simple-robot, simbot, etc.).
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

package love.forte.simbot.component.kook.blacklist

import io.ktor.client.engine.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import love.forte.simbot.ability.OnCompletion
import love.forte.simbot.ability.StandardDeleteOption
import love.forte.simbot.common.id.ID
import love.forte.simbot.common.id.StringID.Companion.ID
import love.forte.simbot.common.id.literal
import love.forte.simbot.component.kook.blacklist.internal.KookGuildBlacklistOperatorImpl
import love.forte.simbot.component.kook.bot.KookBot
import love.forte.simbot.kook.stdlib.BotFactory
import love.forte.simbot.kook.stdlib.BotFactory.create
import love.forte.simbot.kook.stdlib.Ticket
import love.forte.simbot.logger.Logger
import love.forte.simbot.logger.LoggerFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/**
 * 针对 [KookGuildBlacklistOperator] 功能的集成测试。
 *
 * 使用 ktor-client-mock 模拟底层 HTTP 请求，验证黑名单操作的完整流程。
 *
 * @author ForteScarlet
 */
class KookGuildBlacklistOperatorTest {

    /**
     * 创建一个用于测试的 mock KookBot
     */
    private fun createMockBot(client: HttpClientEngine): KookBot {
        val sourceBot = BotFactory.create(Ticket.botWsTicket("test_client_id", "test_token")) {
            clientEngine = client
            wsEngine = client
        }

        return object : KookBot {
            override val sourceBot = sourceBot
            override val id: ID = "test_bot_id".ID
            override val logger: Logger = LoggerFactory.getLogger("test")

            override fun isMe(id: ID): Boolean = id.literal == this.id.literal

            override val isActive: Boolean = true
            override val isCompleted: Boolean = false
            override val isStarted: Boolean = true
            override val component get() = error("Not implemented for test")
            override val coroutineContext get() = error("Not implemented for test")
            override val guildRelation get() = error("Not implemented for test")
            override val contactRelation get() = error("Not implemented for test")
            override suspend fun join() = error("Not implemented for test")
            override fun cancel(reason: Throwable?): Unit = error("Not implemented for test")
            override suspend fun start() = error("Not implemented for test")
            override fun onCompletion(handle: OnCompletion) {
                TODO("Not yet implemented")
            }
        }
    }

    @Test
    fun testListBlacklist() = runTest {
        // Mock HTTP 响应数据
        val mockClientEngine = MockEngine { request ->
            // 验证请求参数
            assertTrue(request.url.toString().contains("blacklist/list"))
            assertEquals("test_guild_123", request.url.parameters["guild_id"])

            // 返回 KOOK API 格式的响应
            respond(
                content = """{
                    "code": 0,
                    "message": "操作成功",
                    "data": {
                        "items": [
                            {
                                "user_id": "26954123",
                                "created_time": 1640340668000,
                                "remark": "测试黑名单原因",
                                "user": {
                                    "id": "26954123",
                                    "username": "testuser",
                                    "identify_num": "2826",
                                    "online": true,
                                    "status": 1,
                                    "avatar": "avatar_url",
                                    "vip_avatar": "vip_avatar_url",
                                    "banner": "",
                                    "nickname": "Test User",
                                    "roles": [],
                                    "is_vip": false,
                                    "bot": false
                                }
                            }
                        ],
                        "meta": {
                            "page": 1,
                            "page_total": 1,
                            "page_size": 50,
                            "total": 1
                        },
                        "sort": {}
                    }
                }""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val bot = createMockBot(mockClientEngine)
        val guildId = "test_guild_123".ID
        val operator = KookGuildBlacklistOperatorImpl(bot, guildId)

        // 执行测试
        val listData = operator.list(page = null, size = null)

        // 验证结果
        assertNotNull(listData)
        assertEquals(1, listData.items.size)
        assertEquals(1, listData.meta.total)

        val item = listData.items[0]
        assertEquals("26954123".ID, item.userId)
        assertEquals(guildId, item.guildId)
        assertEquals("测试黑名单原因", item.remark)
        assertEquals(1640340668000L, item.createdTime.milliseconds)

        val user = item.userInfo
        assertEquals("26954123", user.id)
        assertEquals("testuser", user.username)
        assertEquals("Test User", user.nickname)
    }

    @Test
    fun testListBlacklistWithPagination() = runTest {
        // 测试带分页参数的黑名单列表查询
        val mockClientEngine = MockEngine { request ->
            assertTrue(request.url.toString().contains("blacklist/list"))
            assertEquals("test_guild_456", request.url.parameters["guild_id"])
            assertEquals("2", request.url.parameters["page"])
            assertEquals("25", request.url.parameters["page_size"])

            respond(
                content = """{
                    "code": 0,
                    "message": "操作成功",
                    "data": {
                        "items": [],
                        "meta": {
                            "page": 2,
                            "page_total": 1,
                            "page_size": 25,
                            "total": 0
                        },
                        "sort": {}
                    }
                }""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val bot = createMockBot(mockClientEngine)
        val guildId = "test_guild_456".ID
        val operator = KookGuildBlacklistOperatorImpl(bot, guildId)

        val listData = operator.list(page = 2, size = 25)

        assertNotNull(listData)
        assertTrue(listData.items.isEmpty())
        assertEquals(2, listData.meta.page)
        assertEquals(25, listData.meta.pageSize)
    }

    @Test
    fun testFlowBlacklist() = runTest {
        var requestCount = 0

        // Mock 多次请求以测试 flow 分页
        val mockClientEngine = MockEngine { request ->
            assertTrue(request.url.toString().contains("blacklist/list"))
            requestCount++

            // 第一页返回数据，第二页返回空
            val pageContent = if (requestCount == 1) {
                """{
                    "items": [
                        {
                            "user_id": "user_001",
                            "created_time": 1640340668000,
                            "remark": "第一页用户",
                            "user": {
                                "id": "user_001",
                                "username": "user1",
                                "identify_num": "0001",
                                "online": false,
                                "status": 0,
                                "avatar": "",
                                "vip_avatar": "",
                                "banner": "",
                                "nickname": "User 1",
                                "roles": [],
                                "is_vip": false,
                                "bot": false
                            }
                        }
                    ],
                    "meta": {
                        "page": 1,
                        "page_total": 2,
                        "page_size": 1,
                        "total": 2
                    },
                    "sort": {}
                }"""
            } else {
                """{
                    "items": [
                        {
                            "user_id": "user_002",
                            "created_time": 1640340669000,
                            "remark": "第二页用户",
                            "user": {
                                "id": "user_002",
                                "username": "user2",
                                "identify_num": "0002",
                                "online": false,
                                "status": 0,
                                "avatar": "",
                                "vip_avatar": "",
                                "banner": "",
                                "nickname": "User 2",
                                "roles": [],
                                "is_vip": false,
                                "bot": false
                            }
                        }
                    ],
                    "meta": {
                        "page": 2,
                        "page_total": 2,
                        "page_size": 1,
                        "total": 2
                    },
                    "sort": {}
                }"""
            }

            respond(
                content = """{
                    "code": 0,
                    "message": "操作成功",
                    "data": $pageContent
                }""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val bot = createMockBot(mockClientEngine)
        val guildId = "test_guild_flow".ID
        val operator = KookGuildBlacklistOperatorImpl(bot, guildId)

        // 执行测试
        val items = operator.flow(batchSize = 1).toList()

        // 验证结果
        assertEquals(2, items.size)
        assertEquals("user_001".ID, items[0].userId)
        assertEquals("user_002".ID, items[1].userId)
        assertEquals("第一页用户", items[0].remark)
        assertEquals("第二页用户", items[1].remark)
    }

    @Test
    fun testAddToBlacklist() = runTest {
        // 测试添加用户到黑名单
        val mockClientEngine = MockEngine { request ->
            assertTrue(request.url.toString().contains("blacklist/create"))

            respond(
                content = """{
                    "code": 0,
                    "message": "操作成功",
                    "data": {}
                }""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val bot = createMockBot(mockClientEngine)
        val guildId = "test_guild_add".ID
        val operator = KookGuildBlacklistOperatorImpl(bot, guildId)

        // 测试基本添加
        operator.add(targetId = "target_user_123".ID)

        // 测试带完整参数的添加
        operator.add(
            targetId = "target_user_456".ID,
            remark = "违规行为",
            delMsgDays = 3
        )
    }

    @Test
    fun testDeleteFromBlacklist() = runTest {
        // 测试从黑名单中移除用户
        val mockClientEngine = MockEngine { request ->
            assertTrue(request.url.toString().contains("blacklist/delete"))

            respond(
                content = """{
                    "code": 0,
                    "message": "操作成功",
                    "data": {}
                }""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val bot = createMockBot(mockClientEngine)
        val guildId = "test_guild_delete".ID
        val operator = KookGuildBlacklistOperatorImpl(bot, guildId)

        // 测试删除
        operator.delete(targetId = "target_user_789".ID)
    }

    @Test
    fun testDeleteWithIgnoreOnFailure() = runTest {
        // 测试使用 IGNORE_ON_FAILURE 选项删除
        var requestCount = 0

        val mockClientEngine = MockEngine { request ->
            requestCount++
            assertTrue(request.url.toString().contains("blacklist/delete"))

            // 模拟失败响应
            respond(
                content = """{
                    "code": 40000,
                    "message": "请求失败",
                    "data": {}
                }""",
                status = HttpStatusCode.BadRequest,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val bot = createMockBot(mockClientEngine)
        val guildId = "test_guild_ignore".ID
        val operator = KookGuildBlacklistOperatorImpl(bot, guildId)

        // 使用 IGNORE_ON_FAILURE 选项，即使失败也不应抛出异常
        operator.delete(
            targetId = "non_existent_user".ID,
            StandardDeleteOption.IGNORE_ON_FAILURE
        )

        assertEquals(1, requestCount)
    }

    @Test
    fun testBlacklistItemDelete() = runTest {
        // 测试 KookBlacklistItem 的 delete 方法
        val mockClientEngine = MockEngine { request ->
            when {
                request.url.toString().contains("blacklist/list") -> {
                    respond(
                        content = """{
                            "code": 0,
                            "message": "操作成功",
                            "data": {
                                "items": [
                                    {
                                        "user_id": "item_user_123",
                                        "created_time": 1640340668000,
                                        "remark": "待删除的黑名单项",
                                        "user": {
                                            "id": "item_user_123",
                                            "username": "itemuser",
                                            "identify_num": "1234",
                                            "online": false,
                                            "status": 0,
                                            "avatar": "",
                                            "vip_avatar": "",
                                            "banner": "",
                                            "nickname": "Item User",
                                            "roles": [],
                                            "is_vip": false,
                                            "bot": false
                                        }
                                    }
                                ],
                                "meta": {
                                    "page": 1,
                                    "page_total": 1,
                                    "page_size": 50,
                                    "total": 1
                                },
                                "sort": {}
                            }
                        }""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }

                request.url.toString().contains("blacklist/delete") -> {
                    respond(
                        content = """{
                            "code": 0,
                            "message": "操作成功",
                            "data": {}
                        }""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, "application/json")
                    )
                }

                else -> error("Unexpected request: ${request.url}")
            }
        }

        val bot = createMockBot(mockClientEngine)
        val guildId = "test_guild_item".ID
        val operator = KookGuildBlacklistOperatorImpl(bot, guildId)

        // 先获取黑名单列表
        val listData = operator.list(page = null, size = null)
        assertEquals(1, listData.items.size)

        val item = listData.items[0]
        assertEquals("item_user_123".ID, item.userId)

        // 调用 item 的 delete 方法
        item.delete()
    }

    @Test
    fun testAllBlacklist() = runTest {
        // 测试获取全量黑名单列表
        var requestCount = 0

        val mockClientEngine = MockEngine { request ->
            assertTrue(request.url.toString().contains("blacklist/list"))
            requestCount++

            // 第一页返回2条数据，第二页返回空
            val pageContent = if (requestCount == 1) {
                """{
                    "items": [
                        {
                            "user_id": "all_user_001",
                            "created_time": 1640340668000,
                            "remark": "用户1",
                            "user": {
                                "id": "all_user_001",
                                "username": "user1",
                                "identify_num": "0001",
                                "online": false,
                                "status": 0,
                                "avatar": "",
                                "vip_avatar": "",
                                "banner": "",
                                "nickname": "User 1",
                                "roles": [],
                                "is_vip": false,
                                "bot": false
                            }
                        },
                        {
                            "user_id": "all_user_002",
                            "created_time": 1640340669000,
                            "remark": "用户2",
                            "user": {
                                "id": "all_user_002",
                                "username": "user2",
                                "identify_num": "0002",
                                "online": false,
                                "status": 0,
                                "avatar": "",
                                "vip_avatar": "",
                                "banner": "",
                                "nickname": "User 2",
                                "roles": [],
                                "is_vip": false,
                                "bot": false
                            }
                        }
                    ],
                    "meta": {
                        "page": 1,
                        "page_total": 1,
                        "page_size": 50,
                        "total": 2
                    },
                    "sort": {}
                }"""
            } else {
                """{
                    "items": [],
                    "meta": {
                        "page": 2,
                        "page_total": 1,
                        "page_size": 50,
                        "total": 2
                    },
                    "sort": {}
                }"""
            }

            respond(
                content = """{
                    "code": 0,
                    "message": "操作成功",
                    "data": $pageContent
                }""",
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json")
            )
        }

        val bot = createMockBot(mockClientEngine)
        val guildId = "test_guild_all".ID
        val operator = KookGuildBlacklistOperatorImpl(bot, guildId)

        // 执行测试
        val allItems = operator.all()

        // 验证结果
        assertEquals(2, allItems.size)
        assertEquals("all_user_001".ID, allItems[0].userId)
        assertEquals("all_user_002".ID, allItems[1].userId)
    }
}
