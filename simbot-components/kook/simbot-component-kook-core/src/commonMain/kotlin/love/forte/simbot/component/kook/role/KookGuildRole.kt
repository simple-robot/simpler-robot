/*
 *     Copyright (c) 2023-2026. ForteScarlet.
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

package love.forte.simbot.component.kook.role

import love.forte.simbot.ability.DeleteOption
import love.forte.simbot.annotations.ExperimentalSimbotAPI
import love.forte.simbot.common.id.ID
import love.forte.simbot.component.kook.KookGuild
import love.forte.simbot.component.kook.KookMember
import love.forte.simbot.definition.Member
import love.forte.simbot.kook.api.ApiResponseException
import love.forte.simbot.kook.api.ApiResultException
import love.forte.simbot.kook.objects.PermissionType
import love.forte.simbot.kook.objects.Permissions
import love.forte.simbot.suspendrunner.ST
import kotlin.jvm.JvmSynthetic

/**
 * 代表一个频道服务器所拥有的角色。
 *
 * ## 查询
 *
 * [KookGuildRole] 通过 [KookGuild.roles] 进行列表查询。
 *
 * ## 删除
 *
 * 当权限足够时，可以通过 [delete] 对某个角色进行删除。
 *
 * ## 更新
 *
 * 当权限足够时，可通过 [updater] 获取更新器进行更新。
 * 当通过 [updater] 更新信息成功后，**当前** [KookGuildRole] 内部的信息会随之改变。
 *
 * 对Java来讲可以使用惯用的链式API：
 *
 * ```java
 * role.updater()
 *         .name("文丑")
 *         .isHoist(true)
 *         .permissions(123)
 *         // other...
 *         .updateBlocking(); // or updateAsync
 * ```
 *
 * Kotlin则可以选择通过 [`update { ... }`][KookGuildRole.update]
 * 使用DSL风格的API
 *
 * ```kotlin
 * val updated = role.update { // suspend
 *     name = "老生"
 *     isHoist = true
 *     color = 123
 *     // other...
 * }
 * ```
 *
 * ## 赋予
 *
 * 一个 [KookGuildRole] 可以通过 [grantTo] 赋予给一个指定的成员，
 * 并得到一个赋予后的 [KookMemberRole] 实例。
 *
 * ## 创建
 *
 * 通过 [KookGuild.roleCreator] 可以得到一个角色的构造器，
 * 并在通过API创建角色成功后得到一个 [KookGuildRole] 实例。
 *
 * Java可以使用惯用的链式API:
 *
 * ```java
 * KookGuildRole newRole = guild.roleCreator()
 *         .name("青衣")
 *         // other...
 *         .createBlocking(); // or createAsync
 * ```
 *
 * Kotlin可以通过 [`createRole { ... }`][KookGuild.createRole] 使用 DSL 风格的API:
 *
 * ```kotlin
 * val newRole = role.createRole {
 *     name = "花旦"
 *     // other...
 * }
 * ```
 *
 * <hr />
 *
 * > 统一性说明参考 [KookRole]
 *
 * @see KookRole
 *
 * @author ForteScarlet
 */
@ExperimentalSimbotAPI
@ST
public interface KookGuildRole : KookRole {

    /**
     * 当前角色所属的频道服务器。
     */
    public val guild: KookGuild

    /**
     * 将当前角色赋予给指定 [memberId] 的用户。
     *
     * @throws ApiResultException API请求过程中产生的任何异常
     * @throws ApiResponseException API请求过程中产生的任何异常
     */
    public suspend fun grantTo(memberId: ID): KookMemberRole

    /**
     * 将当前角色赋予给指定用户 [member] 。
     *
     * @throws ApiResultException API请求过程中产生的任何异常
     * @throws ApiResponseException API请求过程中产生的任何异常
     */
    public suspend fun grantTo(member: KookMember): KookMemberRole


    /**
     * 将当前角色赋予给指定用户 [member] 。
     *
     * @throws ApiResultException API请求过程中产生的任何异常
     * @throws ApiResponseException API请求过程中产生的任何异常
     * @throws ClassCastException [member] 的类型不是 [KookMember] 时
     */
    public suspend fun grantTo(member: Member): KookMemberRole {
        // KookGuildRole.grantTo 只支持 KookMember 类型的 member
        val kookMember = member as? KookMember
            ?: throw ClassCastException("KookGuildRole.grantTo only support member of type KookMember, but ${member::class}")
        return grantTo(kookMember)
    }

    /**
     * 删除当前频道中对应的角色。
     *
     * 删除一个 [KookGuildRole] 后不应再进行其他操作。组件目前不会进行过多判断，但是可能会造成任何预期外的异常。
     *
     * @throws ApiResultException API请求过程中产生的任何异常
     * @throws ApiResponseException API请求过程中产生的任何异常
     */
    @JvmSynthetic
    override suspend fun delete(vararg options: DeleteOption)

    /**
     * 构建一个用于更新当前角色信息的 [KookGuildRoleUpdater].
     *
     * @see update
     */
    public fun updater(): KookGuildRoleUpdater

}

/**
 * 使用 Kotlin DSL 更新当前服务器角色信息。
 *
 * ```kotlin
 * val updated = role.update { // suspend
 *     name = "老生"
 *     isHoist = true
 *     color = 123
 *     // other ...
 * }
 * ```
 *
 * @see KookGuildRole.updater
 *
 * @return receiver 自身
 */
@ExperimentalSimbotAPI
public suspend inline fun <R : KookGuildRole> R.update(block: KookGuildRoleUpdater.() -> Unit): R {
    updater().also(block).update()
    return this
}

/**
 * 用于修改 [KookGuildRole] 的更新器，提供Kotlin DSL风格的API和Java的惯用API。
 *
 * @author ForteScarlet
 *
 */
@ExperimentalSimbotAPI
public interface KookGuildRoleUpdater {
    //region DSL API
    /**
     * 名称
     */
    public var name: String?

    /**
     * 颜色.
     */
    public var color: Int?

    /**
     * 是否把该角色的用户在用户列表排到前面
     */
    public var isHoist: Boolean?

    /**
     * 该角色是否可以被提及
     */
    public var isMentionable: Boolean?

    /**
     * 权限
     */
    @get:JvmSynthetic
    @set:JvmSynthetic
    public var permissions: Permissions?

    //endregion


    //region Java API
    /**
     * 名称
     */
    public fun name(value: String): KookGuildRoleUpdater = also { name = value }

    /**
     * 颜色.
     */
    public fun color(value: Int): KookGuildRoleUpdater = also { color = value }

    /**
     * 是否把该角色的用户在用户列表排到前面
     */
    public fun isHoist(value: Boolean): KookGuildRoleUpdater = also { isHoist = value }

    /**
     * 该角色是否可以被提及
     */
    public fun isMentionable(value: Boolean): KookGuildRoleUpdater = also { isMentionable = value }

    /**
     * 权限
     */
    public fun permissions(value: PermissionType): KookGuildRoleUpdater = also { permissions = Permissions(value) }

    /**
     * 权限
     *
     * _Note: [permissionsValue] 会被转化为 [Kotlin UInt][UInt]_
     */
    public fun permissions(permissionsValue: Int): KookGuildRoleUpdater =
        also { permissions = Permissions(permissionsValue.toUInt()) }
    //endregion

    /**
     * 根据当前配置的内容发起一次 Guild Role 更新。
     *
     * 如果更新成功，更新后的信息将会生效并刷新到当前对象中。
     *
     * 如果没有配置任何属性，则 [update] 不会产生请求。
     */
    @ST
    public suspend fun update()
}
