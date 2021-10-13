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

package love.forte.simbot.kaiheila.objects

import kotlinx.serialization.Serializable

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

    constructor(permissionType: PermissionType) : this(permissionType.value)
    constructor(vararg permissionTypes: PermissionType) : this(combine(*permissionTypes))

    operator fun contains(permissionType: PermissionType): Boolean {
        return with(permissionType.value) {
            this == perm and this
        }
    }
}