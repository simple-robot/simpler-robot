/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
 *  * Project  simple-robot
 *  * File     MiraiAvatar.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *
 */

package love.forte.simbot.component.kaiheila.api.v3.guild.role

import kotlinx.serialization.Serializable
import love.forte.simbot.api.message.assists.Permissions
import love.forte.simbot.component.kaiheila.api.ApiData
import love.forte.simbot.component.kaiheila.api.EmptyResp


/**
 * [服务器角色权限相关接口](https://developer.kaiheila.cn/doc/http/guild-role) 请求实例接口。
 *
 *
 */
public interface GuildRoleApiReq<RESP : ApiData.Resp<*>> : ApiData.Req<RESP>
public interface GetGuildRoleApiReq<RESP : ApiData.Resp<*>> : GuildRoleApiReq<RESP>, ApiData.Req.Get<RESP>
public interface PostGuildRoleApiReq<RESP : ApiData.Resp<*>> : GuildRoleApiReq<RESP>, ApiData.Req.Post<RESP>

public interface EmptyRespGuildRoleApiReq : GuildRoleApiReq<EmptyResp>, ApiData.Req.Empty
public interface EmptyRespGetGuildRoleApiReq : EmptyRespGuildRoleApiReq, GetGuildRoleApiReq<EmptyResp>
public interface EmptyRespPostGuildRoleApiReq : EmptyRespGuildRoleApiReq, PostGuildRoleApiReq<EmptyResp>


/**
 * [服务器角色权限相关接口](https://developer.kaiheila.cn/doc/http/guild-role) 响应实例接口。
 */
public interface GuildRoleApiRespData : ApiData.Resp.Data


/**
 * ## 权限说明
 *
 * 权限是一个unsigned int值，由比特位代表是否拥有对应的权限。
 * 权限值与对应比特位进行按位与操作，判断是否拥有该权限。
 *
 * ### 判断是否有某权限
 * 其中: permissions代表权限值，bitValue代表某权限比特位，1 << bitValue 代表某权限值。
 * `permissions & (1 << bitValue)  == (1 << bitValue);`
 * @author ForteScarlet
 */
@Suppress("MemberVisibilityCanBePrivate")
@JvmInline
@Serializable
public value class Permissions(val perm: UInt) {

    constructor(permissionType: PermissionType): this(permissionType.value)
    constructor(vararg permissionTypes: PermissionType): this(combine(*permissionTypes))

    operator fun contains(permissionType: PermissionType): Boolean {
        return with(permissionType.value) {
            this == perm and this
        }
    }
}









