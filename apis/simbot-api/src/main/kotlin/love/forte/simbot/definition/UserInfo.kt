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
 */

package love.forte.simbot.definition

import love.forte.simbot.ExperimentalSimbotApi
import love.forte.simbot.ID
import love.forte.simbot.definition.UserStatus.Companion.refactor
import java.util.*

/**
 * 一个用户的 **信息**。
 *
 * 此处仅代表普通的通用信息。
 *
 * @author ForteScarlet
 */
public interface UserInfo : IDContainer {
    
    /**
     * 此用户的ID。
     */
    override val id: ID
    
    /**
     * 这个账号的用户名。
     */
    public val username: String
    
    /**
     * 这个账户的头像。
     * 这年头了，应该不会有什么聊天平台的用户没有头像信息了吧。
     *
     */
    public val avatar: String
    
}

/**
 * 一个用户的状态属性。
 *
 * ## ExperimentalSimbotApi
 * [UserStatus] 目前来看实际作用不大，很可能属于过度设计的副产物。考虑后续弃用或移除。
 *
 */
@ExperimentalSimbotApi
public interface UserStatus {
    
    /**
     * 是否是一个普通用户。
     * 一般来讲，[isNormal] 与 [isFake] 是相互冲突的。
     */
    public val isNormal: Boolean
    
    /**
     * 是否是一个官方用户。
     *
     * 例如一个系统通知用户或者官方BOT。
     *
     * 假如当前平台存在"官方认证"的说法，那么也可以表示为 [isOfficial].
     */
    public val isOfficial: Boolean
    
    /**
     * 是否为一个 "虚假用户"，一般用来描述其是否为一个匿名用户，或者一个BOT用户。
     */
    public val isFake: Boolean
    
    /**
     * 当前成员是否为匿名。
     */
    public val isAnonymous: Boolean
    
    /**
     * 当前用户是否为一个BOT。只有当能够被检测为BOT，才会标记为BOT。
     */
    public val isBot: Boolean
    
    public companion object {
        
        /**
         * 得到一个用于构建 [UserStatus] 的构建器。
         */
        @JvmStatic
        public fun builder(): UserStatusBuilder = UserStatusBuilder()
        
        
        /**
         * 基于一个原始的 [UserStatusBuilder] 得到一个用于重构它的 [UserStatus] 构建器。
         */
        @JvmStatic
        @JvmName("builder")
        public fun UserStatus.refactor(): UserStatusBuilder {
            if (this is UserStatusImpl) return this.refactor()
            
            return UserStatusBuilder().also {
                if (isNormal) {
                    it.normal()
                }
                if (isOfficial) {
                    it.official()
                }
                if (isFake) {
                    it.fakeUser()
                }
                if (isAnonymous) {
                    it.anonymous()
                }
                if (isBot) {
                    it.bot()
                }
            }
            
        }
        
    }
}

@DslMarker
@Retention(AnnotationRetention.BINARY)
internal annotation class UserStatusBuilderDsl


/**
 * 通过DSL Builder构建一个 [UserStatus].
 *
 * ```kotlin
 * val status = buildUserStatus {
 *     bot()
 *     fakeUser()
 *     // ...
 * }
 * ```
 *
 * @see UserStatus
 *
 */
@UserStatusBuilderDsl
@ExperimentalSimbotApi
public inline fun buildUserStatus(base: UserStatus? = null, block: UserStatusBuilder.() -> Unit): UserStatus {
    return base?.refactor()?.also(block)?.build() ?: UserStatusBuilder().also(block).build()
}


@ExperimentalSimbotApi
public class UserStatusBuilder(private val status: BitSet = BitSet()) {
    
    @JvmOverloads
    public fun normal(yes: Boolean = true): UserStatusBuilder = also {
        if (yes) {
            status.set(UserStatusImpl.IS_NORMAL)
        } else {
            status.clear(UserStatusImpl.IS_NORMAL)
        }
    }
    
    @JvmOverloads
    public fun official(yes: Boolean = true): UserStatusBuilder = also {
        if (yes) {
            status.set(UserStatusImpl.IS_OFFICIAL)
        } else {
            status.clear(UserStatusImpl.IS_OFFICIAL)
        }
    }
    
    @JvmOverloads
    public fun fakeUser(yes: Boolean = true): UserStatusBuilder = also {
        if (yes) {
            status.set(UserStatusImpl.IS_FAKE)
        } else {
            status.clear(UserStatusImpl.IS_FAKE)
        }
    }
    
    @JvmOverloads
    public fun anonymous(yes: Boolean = true): UserStatusBuilder = also {
        if (yes) {
            status.set(UserStatusImpl.IS_ANONYMOUS)
        } else {
            status.clear(UserStatusImpl.IS_ANONYMOUS)
        }
    }
    
    @JvmOverloads
    public fun bot(yes: Boolean = true): UserStatusBuilder = also {
        if (yes) {
            status.set(UserStatusImpl.IS_BOT)
        } else {
            status.clear(UserStatusImpl.IS_BOT)
        }
    }
    
    public fun build(): UserStatus {
        return UserStatusImpl(status.clone() as BitSet)
    }
    
    public fun clear(): UserStatusBuilder = also {
        status.clear()
    }
}


@ExperimentalSimbotApi
private class UserStatusImpl(private val status: BitSet) : UserStatus {
    
    companion object {
        internal const val IS_NORMAL = 1
        internal const val IS_OFFICIAL = 2
        internal const val IS_FAKE = 3
        internal const val IS_ANONYMOUS = 4
        internal const val IS_BOT = 5
    }
    
    override val isNormal: Boolean
        get() = status[IS_NORMAL]
    override val isOfficial: Boolean
        get() = status[IS_OFFICIAL]
    override val isFake: Boolean
        get() = status[IS_FAKE]
    override val isAnonymous: Boolean
        get() = status[IS_ANONYMOUS]
    override val isBot: Boolean
        get() = status[IS_BOT]
    
    fun refactor(): UserStatusBuilder = UserStatusBuilder(status.clone() as BitSet)
}