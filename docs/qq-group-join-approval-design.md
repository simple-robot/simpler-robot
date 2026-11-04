# QQ 入群自动审批接口与 core 设计

API 模块实现六个官方接口。本文中的 core interface 为后续实现设计，当前交付仅包含设计文档。

## 官方接口与 API 模块

API 位于 `love.forte.simbot.qguild.api.group`，模型位于 `love.forte.simbot.qguild.model.group`。
策略由机器人创建，可关联多个群；仅在机器人具有群管理员身份的群中生效。

| 官方文档 | API 类型 | 请求与结果 |
| --- | --- | --- |
| [查询策略列表](https://bot.q.qq.com/wiki/develop/api-v2/autogen/api/v2_groups_join_approval_strategy.get.html) | `GetGroupJoinApprovalStrategyListApi` | `cursor/limit` → `GroupJoinApprovalStrategyPage` |
| [创建策略](https://bot.q.qq.com/wiki/develop/api-v2/autogen/api/v2_groups_join_approval_strategy.post.html) | `CreateGroupJoinApprovalStrategyApi` | `GroupJoinApprovalStrategyCreate` → `GroupJoinApprovalStrategyCreated` |
| [修改策略](https://bot.q.qq.com/wiki/develop/api-v2/autogen/api/v2_groups_join_approval_strategy_strategy_id.patch.html) | `ModifyGroupJoinApprovalStrategyApi` | 策略 ID、`GroupJoinApprovalStrategyModify` → `GroupJoinApprovalStrategyModified` |
| [删除策略](https://bot.q.qq.com/wiki/develop/api-v2/autogen/api/v2_groups_join_approval_strategy_strategy_id.delete.html) | `DeleteGroupJoinApprovalStrategyApi` | 策略 ID → `Unit` |
| [执行策略](https://bot.q.qq.com/wiki/develop/api-v2/autogen/api/v2_groups_join_approval_strategy_strategy_id_execute.post.html) | `ExecuteGroupJoinApprovalStrategyApi` | 策略 ID → `Unit` |
| [修改白名单号码](https://bot.q.qq.com/wiki/develop/api-v2/autogen/api/v2_groups_join_approval_strategy_strategy_id_whitelist_users.post.html) | `ModifyGroupJoinApprovalStrategyWhitelistApi` | 策略 ID、`op`、QQ 号码 → `GroupJoinApprovalStrategyWhitelistModified` |

各接口使用现有 `requestData` 请求管道与 `QQGuildApiException` 错误处理。六个接口的官方频率限制均为 60 QPM。

### 请求模型

- 创建策略及关联群变更支持 `groupOpenids`、`groupIds`，二选一且列表非空。
- `groupOpenids`、`groupIds` 在请求和响应中均使用 `List<String>?`，默认 `null`。群号编码为 JSON 字符串，本地仅校验非空白。
- 请求与响应的 `isEnable` 使用 Boolean，映射 `true → "on"`、`false → "off"`。未知响应值产生反序列化错误。
- 时间使用 `kotlin.time.Instant`；可选请求时间使用 `Instant?`，编码为 RFC3339 字符串，解析保留时间点与纳秒精度。
- 可选请求字段为 `null` 时省略。字段省略规则适用于调用方自定义的 `encodeDefaults/explicitNulls` 配置。
- 创建时省略 `isEnable` 和 `expireAt`，由平台应用默认启用、一年过期的规则。
- 修改请求至少设置一个字段；`false` 和空备注字符串均照常发送。空备注的业务效果由平台决定。
- `GroupJoinApprovalStrategyOp.ADD/DEL` 用于关联群及白名单操作，分别编码为 `add/del`。
- 白名单参数是 QQ 号码字符串。集合构建时复制，保留顺序及重复项。

本地校验包含非空白策略 ID、非空集合、非空白 OpenID/QQ 群号/白名单号码和正数分页数量，以及群标识互斥约束。
平台规定的群数量、白名单数量、备注长度、策略总数和权限等交由服务端校验。
当前官方创建上限为 100 群、每机器人 20 个策略；白名单单次最多 10000 个，策略白名单号码上限 10 万。
API 按调用方提供的集合发起一次请求。

创建、修改及关联群操作使用独立请求模型、Java builder 和 Kotlin DSL，沿用现有命令面板构建方式。

```kotlin
val created = CreateGroupJoinApprovalStrategyApi.create {
    addGroupOpenid("群OpenID")
    isEnable = true
    remark = "活动报名"
}.requestData(client, token)

ModifyGroupJoinApprovalStrategyWhitelistApi.create(
    created.strategyId,
    GroupJoinApprovalStrategyOp.ADD,
    listOf("1234567", "1234568"),
).requestData(client, token)

ExecuteGroupJoinApprovalStrategyApi.create(created.strategyId).requestData(client, token)
```

```java
GroupJoinApprovalStrategyCreate input = GroupJoinApprovalStrategyCreate.builder()
    .addGroupId("123456789")
    .isEnable(true)
    .remark("活动报名")
    .build();
CreateGroupJoinApprovalStrategyApi api = CreateGroupJoinApprovalStrategyApi.create(input);
// 使用现有 ApiRequests 请求入口。
```

### 响应与分页

创建响应包含策略 ID、启用状态、过期时间；修改响应包含启用状态、过期时间；
白名单响应包含策略 ID、估算的白名单号码数、更新时间。三个结果分别建模，保留各接口实际返回的信息。

列表记录保留完整远端快照。`groupIds` 使用普通字符串列表；
未返回的关联群列表和备注使用 `null`；关联群字段明确返回空数组时保留空列表。必需的状态、数量、时间等字段缺失时产生反序列化错误。
白名单数量是估算值，官方说明可能存在少量误差。

`cursor/limit` 通过 URL query 发送。默认单页数量为 20，最大 50；空 `next_cursor` 表示末页。
`createFlow` 为冷流，收集时请求，空页仍依下一页游标继续，下一页游标为空时结束。
取消收集会终止后续翻页，请求异常向调用方传播。

执行策略会对全部关联群进行全量扫描，自动通过匹配白名单的入群申请。
官方说明异步执行、约 10 分钟完成；`requestData` 成功返回表示服务端已接受请求。

## core module 的 interface 设计

### 入口与类型

在 `QGBot` 上增加 `groupJoinApprovalStrategies: QGGroupJoinApprovalStrategyManager`。
采用与 `commandPanels` 一致的默认 getter 构建 manager，依赖当前 `QGBot`；远端资源由平台持有。
拟使用现有 `love.forte.simbot.component.qguild.group` 包，内部 adapter 位于对应的 `internal` 包。

| 类型 | 职责 |
| --- | --- |
| `QGGroupJoinApprovalStrategyManager` | 创建、按 ID 构造操作句柄、分页查询 |
| `QGGroupJoinApprovalStrategyHandle` | 保存策略 ID，提供修改、白名单增删、执行、删除操作 |
| `QGGroupJoinApprovalStrategyRecord` | 列表查询获得的完整快照，实现 handle，并通过 `source` 保留 API 模型 |
| `QGGroupJoinApprovalStrategyCreated` | 创建结果，实现 handle，通过 `source` 保留创建响应中的状态与过期时间 |

这些类型将分页与 ID 绑定操作集中在机器人级 module 中，使调用方可从创建结果或列表记录继续操作同一策略。
record 与 created 分别保留不同来源的数据，查询记录的内容在操作后保持其获取时的快照。

### 拟议声明

以下代码用于表达后续 interface，省略包名、导入及继承限制注解。

```kotlin
interface QGGroupJoinApprovalStrategyManager {
    fun handle(strategyId: ID): QGGroupJoinApprovalStrategyHandle

    @ST
    suspend fun create(input: GroupJoinApprovalStrategyCreate): QGGroupJoinApprovalStrategyCreated

    fun list(
        startCursor: String? = null,
        limit: Int? = null,
        autoPagination: Boolean = true,
    ): Collectable<QGGroupJoinApprovalStrategyRecord>
}

interface QGGroupJoinApprovalStrategyHandle : DeleteSupport {
    val id: ID

    @ST
    suspend fun update(input: GroupJoinApprovalStrategyModify): GroupJoinApprovalStrategyModified

    @ST
    suspend fun addWhitelistUsers(users: Collection<String>): GroupJoinApprovalStrategyWhitelistModified

    @ST
    suspend fun removeWhitelistUsers(users: Collection<String>): GroupJoinApprovalStrategyWhitelistModified

    @ST
    suspend fun execute()

    @ST
    override suspend fun delete(vararg options: DeleteOption)
}

abstract class QGGroupJoinApprovalStrategyRecord :
    QGObjectiveContainer<GroupJoinApprovalStrategy>, QGGroupJoinApprovalStrategyHandle

abstract class QGGroupJoinApprovalStrategyCreated :
    QGObjectiveContainer<GroupJoinApprovalStrategyCreated>, QGGroupJoinApprovalStrategyHandle
```

manager 的 `handle` 仅构造本地句柄。`create` 使用一次创建请求，结果可直接执行后续操作，并保留创建回执。
`list` 返回冷 `Collectable`，复用 API 分页流；`autoPagination=false` 时只读取一页。
record 和 created 公开常用字段的只读属性，操作统一委托给按 ID 绑定的内部 operator。
`update` 与白名单操作直接返回 API 结果，便于读取平台实际返回的状态、过期时间、估算数量及更新时间。

core 复用 API 模块的输入模型，Kotlin 提供配置 builder 的扩展函数，Java 使用 builder 与 `@ST` 生成的阻塞、异步和 reserve 入口。
关联群增删通过 `update` 的 `groupAction` 表达；调用方须使用创建策略时的群标识形式。

### 请求、异常与生命周期

- 内部 adapter 通过当前 bot 的请求入口使用鉴权信息，复用 `QQGuildApiException`、序列化错误与取消传播。
- manager、handle 持有 bot 与 ID；列表和创建结果持有对应 `source`，远端数据按需请求。
- 删除沿用 `DeleteSupport` 和 `StandardDeleteOption.IGNORE_ON_FAILURE` 的项目约定，处理异常时保留协程取消语义。
- 执行成功返回 `Unit`，其语义为提交执行请求。平台目前提供的六个接口未包含任务进度与完成状态查询。
- 多群关联策略的操作会作用于指定策略。权限、群标识形式及实际生效情况均由平台判定。

### 后续 core 验证范围

实现时验证 manager 获取与 handle 构造的请求次数、创建回执字段保留、冷分页及单页模式、
record/created 的 ID 操作委托、快照稳定性、更新和白名单结果透传、删除选项、取消语义及 Java 生成入口。

## 文档歧义记录

- GET 官方将分页字段放在“请求体”表中；当前采用仓库同类接口的 query 方式，尚无真实平台调用验证。
- 创建请求示例含空 `expire_at`，字段说明只明确省略时默认一年过期。本实现提供 `Instant?` 参数及省略语义。
- 列表文档称“生效中的策略”，关闭、过期策略是否可查尚无进一步说明。
- 备注空串、空 PATCH、多策略重叠、重复执行和执行中的策略删除等业务效果需以平台行为为准。
- 平台当前接口清单未提供单条策略详情、白名单号码列表及执行进度查询；core 设计覆盖已有远端能力。
