package love.forte.simbot.kaiheila.event.system.guild.role

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import love.forte.simbot.component.kaiheila.objects.Permissions

/**
 *
 *
 * @author ForteScarlet
 */
@Serializable
public data class UpdatedRoleEventBody(
    /**
     * 角色id
     */
    @SerialName("role_id")
    val roleId: Long,
    /**
     * 角色名称
     */
    val name: String,
    /**
     * 颜色
     * Constraints:  Min 0┃Max 16777215
     */
    val color: Int,
    /**
     * 顺序，值越小载靠前
     */
    val position: Int,
    /**
     * 只能为0或者1，是否把该角色的用户在用户列表排到前面
     * Allowed: 0┃1
     */
    val hoist: Int,
    /**
     * 只能为0或者1，该角色是否可以被提及
     * Allowed: 0┃1
     */
    val mentionable: Int,
    /**
     * 允许的权限.
     * @see Permissions
     */
    val permissions: Permissions,
) : GuildRoleEventExtraBody {
    init {
        check(color in 0..0xffffff) { "Color must be in 0..16777215, but $color" }
        check(hoist in 0..1) { "Hoist must be 0 or 1, but $hoist" }
        check(mentionable in 0..1) { "Mentionable must be 0 or 1, but $mentionable" }
    }

    val permissionValue: Int get() = permissions.perm.toInt()

}


/**
 *
 *
 * `updated_role`
 *
 * @author ForteScarlet
 */
@Serializable
public data class UpdatedRoleEventExtra(override val body: UpdatedRoleEventBody) :
    GuildRoleEventExtra<UpdatedRoleEventBody> {
    override val type: String
        get() = "updated_role"
}