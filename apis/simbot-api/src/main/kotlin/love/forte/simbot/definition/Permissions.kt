/*
 *  Copyright (c) 2021-2022 ForteScarlet <ForteScarlet@163.com>
 *
 *  本文件是 simply-robot (或称 simple-robot 3.x 、simbot 3.x ) 的一部分。
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
 *
 */

package love.forte.simbot.definition

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList
import love.forte.simbot.Api4J
import love.forte.simbot.ID
import love.forte.simbot.utils.runInBlocking
import java.util.*


/**
 * 各式各样的组件中，可能的权限有数不清的类型，
 * 此接口规定一部分常见的广义含义的权限类型判断接口。
 *
 * 对于状态的定义，虽然存在含义上可能会冲突的属性，但是builder提供的默认实现不会进行相关检测。
 *
 * [PermissionStatus] 不建议在 [Permission] 中参与 [Permission.equals] 匹配。
 */
public interface PermissionStatus {

    /**
     * 是否为当前权限涉及场所的拥有者。
     * 绝大多数情况下拥有者只可能有一个，
     * 但是假若在一个guild和channel中存在不同的拥有者，那么它们都算"拥有者"。
     */
    public val isOwner: Boolean

    /**
     * 是否为一个管理员。
     * 只要下述结尾为 `Admin` 的相关权限中存在任意true，那么此处就应当为true。
     */
    public val isAdmin: Boolean

    /**
     * 是否为一个 "超级管理员"，即拥有 **所有** 敏感权限。
     *
     * 一般来讲，一个 `owner` 很有可能是一个超级管理员。
     */
    public val isSuperAdmin: Boolean

    /**
     * 是否为一个 "成员管理员"，即拥有与成员管理相关的权限。
     * 比如对用户进行封禁、踢出、邀请等操作。
     */
    public val isMemberAdmin: Boolean


    /**
     * 是否能够对一个组织进行管理。
     * 例如修改组织简介等。
     *
     * 组织不论是[群][Group]、[频道][Guild]还是[子频道][Channel]，都视为组织。
     */
    public val isOrganizationAdmin: Boolean


    /**
     * 是否是一个[子频道][Channel]管理员。
     */
    public val isChannelAdmin: Boolean

    public companion object {
        @JvmStatic
        public fun builder(): PermissionStatusBuilder = PermissionStatusBuilder()
    }
}

@Suppress("MemberVisibilityCanBePrivate")
public class PermissionStatusBuilder {
    private val status = BitSet()
    public fun with(status: PermissionStatus): PermissionStatusBuilder = also {
        if (status is PermissionStatusImpl) {
            this.status.or(status.status)
        } else {
            when {
                status.isOwner -> owner()
                status.isAdmin -> admin()
                status.isSuperAdmin -> superAdmin()
                status.isMemberAdmin -> memberAdmin()
                status.isOrganizationAdmin -> organizationAdmin()
                status.isChannelAdmin -> channelAdmin()
            }
        }
    }

    public fun owner(): PermissionStatusBuilder = also {
        status.set(PermissionStatusImpl.IS_OWNER)
    }

    public fun admin(): PermissionStatusBuilder = also {
        status.set(PermissionStatusImpl.IS_ADMIN)
    }

    public fun superAdmin(): PermissionStatusBuilder = also {
        status.set(PermissionStatusImpl.IS_SUPER_ADMIN)
    }

    public fun memberAdmin(): PermissionStatusBuilder = also {
        status.set(PermissionStatusImpl.IS_MEMBER_ADMIN)
    }

    public fun organizationAdmin(): PermissionStatusBuilder = also {
        status.set(PermissionStatusImpl.IS_ORGANIZATION_ADMIN)
    }

    public fun channelAdmin(): PermissionStatusBuilder = also {
        status.set(PermissionStatusImpl.IS_CHANNEL_ADMIN)
    }

    public fun build(): PermissionStatus {
        return PermissionStatusImpl(status.clone() as BitSet).also {
            status.clear()
        }
    }
}


@Suppress("MemberVisibilityCanBePrivate")
private class PermissionStatusImpl(val status: BitSet) : PermissionStatus {
    companion object {
        internal const val IS_OWNER = 1
        internal const val IS_ADMIN = 2
        internal const val IS_SUPER_ADMIN = 3
        internal const val IS_MEMBER_ADMIN = 4
        internal const val IS_ORGANIZATION_ADMIN = 5
        internal const val IS_CHANNEL_ADMIN = 6
    }

    override val isOwner: Boolean
        get() = status[IS_OWNER]
    override val isAdmin: Boolean
        get() = status[IS_ADMIN]
    override val isSuperAdmin: Boolean
        get() = status[IS_SUPER_ADMIN]
    override val isMemberAdmin: Boolean
        get() = status[IS_MEMBER_ADMIN]
    override val isOrganizationAdmin: Boolean
        get() = status[IS_ORGANIZATION_ADMIN]
    override val isChannelAdmin: Boolean
        get() = status[IS_CHANNEL_ADMIN]

    operator fun contains(status: PermissionStatus): Boolean {
        fun check(tar: PermissionStatusImpl): Boolean = this.status.intersects(tar.status)
        if (status is PermissionStatusImpl) {
            return check(status)
        }
        val target = PermissionStatusBuilder().with(status).build()
        return check(target as PermissionStatusImpl)
    }

}


/**
 * 一个**权限**信息，通常 存在于 [Role] 并使用在 [Organization] 与 [Member] 中，为组织和其成员限定权限。
 */
public interface Permission : IDContainer {

    /**
     * 权限
     */
    override val id: ID


    /**
     * 这个权限的一部分基础属性。
     */
    public val status: PermissionStatus


}


/**
 * 一个组织中的成员"角色"，角色承担了为成员分配权限的能力。
 *
 * 一个角色可能包含多重 "权限".
 *
 */
public interface Role : IDContainer {
    /**
     * 这个角色的ID
     */
    override val id: ID

    /**
     * 这个角色的名称。
     */
    public val name: String


    /**
     * 角色所属权限集。
     */
    @JvmSynthetic
    public suspend fun permissions(): Flow<Permission> // TODO


    @Api4J
    public val permissions: List<Permission> get() = runInBlocking { permissions().toList() }

    /**
     * 判断当前角色是否存在某个指定权限。
     */
    @Api4J
    public operator fun contains(permission: Permission): Boolean =
        permissions.any { it == permission }

    /**
     * 此角色中是否包含管理者权限。
     *
     * @see PermissionStatus.isAdmin
     */
    public suspend fun isAdmin(): Boolean = permissions().firstOrNull { it.status.isAdmin } != null

    /**
     * 此角色中是否包含所有者权限。
     *
     * @see PermissionStatus.isOwner
     */
    public suspend fun isOwner(): Boolean = permissions().firstOrNull { it.status.isOwner } != null

    /**
     * 此角色中是否包含管理员权限。
     */
    @Api4J
    public val isAdmin: Boolean get() = permissions.any { it.status.isAdmin }


    /**
     * 此角色中是否包含所有者权限。
     */
    @Api4J
    public val isOwner: Boolean get() = permissions.any { it.status.isOwner }

}