/*
 *  Copyright (c) 2021-2021 ForteScarlet <https://github.com/ForteScarlet>
 *
 *  根据 Apache License 2.0 获得许可；
 *  除非遵守许可，否则您不得使用此文件。
 *  您可以在以下网址获取许可证副本：
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   有关许可证下的权限和限制的具体语言，请参见许可证。
 */

package love.forte.simbot.definition

import love.forte.simbot.ID
import java.util.*

/**
 * 一个账号的 **信息**。
 *
 * 此处仅代表普通的通用信息。
 *
 * @author ForteScarlet
 */
public interface UserInfo {

    /**
     * 此用户的ID。
     */
    public val id: ID

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
 */
public interface UserStatus {

    /**
     * 是否是一个普通用户。
     * 一般来讲，[isNormal] 与 [isFakeUser] 是相互冲突的。
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
    public val isFakeUser: Boolean

    /**
     * 当前成员是否为匿名。
     */
    public val isAnonymous: Boolean

    /**
     * 当前用户是否为一个BOT。只有当能够被检测为BOT，才会标记为BOT。
     */
    public val isBot: Boolean

    public companion object {
        @JvmStatic
        public fun builder(): UserStatusBuilder = UserStatusBuilder()
    }
}


public class UserStatusBuilder {
    private val status = BitSet()
    public fun normal(): UserStatusBuilder = also {
        status.set(UserStatusImpl.IS_NORMAL)
    }
    public fun official(): UserStatusBuilder = also {
        status.set(UserStatusImpl.IS_OFFICIAL)
    }
    public fun fakeUser(): UserStatusBuilder = also {
        status.set(UserStatusImpl.IS_FAKE)
    }
    public fun anonymous(): UserStatusBuilder = also {
        status.set(UserStatusImpl.IS_ANONYMOUS)
    }
    public fun bot(): UserStatusBuilder = also {
        status.set(UserStatusImpl.IS_BOT)
    }
    public fun build(): UserStatus {
        return UserStatusImpl(status.clone() as BitSet).also {
            status.clear()
        }
    }
}




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
    override val isFakeUser: Boolean
        get() = status[IS_FAKE]
    override val isAnonymous: Boolean
        get() = status[IS_ANONYMOUS]
    override val isBot: Boolean
        get() = status[IS_BOT]
}