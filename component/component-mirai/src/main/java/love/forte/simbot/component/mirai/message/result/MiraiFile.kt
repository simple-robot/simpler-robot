/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     MiraiFile.kt
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

package love.forte.simbot.component.mirai.message.result

import love.forte.simbot.api.message.containers.*
import love.forte.simbot.api.message.results.Result;


/**
 * 一个 **文件**。
 */
public interface File : Result, AccountContainer, TimeContainer {

    /**
     * 文件对应ID。
     */
    val id: String

    /**
     * 文件名称。
     */
    val name: String

    /**
     * 文件上传时间。
     * 获取不到则可能得到 `-1`.
     */
    override val time: Long


    /**
     * 到期时间。如果无法获取，则为null，
     * 如果无期限，则为-1.
     */
    val expireDate: Long?

    /**
     * 上传者信息。
     */
    override val accountInfo: AccountInfo


    /**
     * 上传者信息.
     * @see accountInfo
     */
    val uploader: AccountInfo get() = accountInfo

    /**
     * 是否为一个文件。
     */
    fun isFile(): Boolean


    /**
     * 是否为一个文件夹。
     */
    fun isDirectory(): Boolean

    /**
     * 文件大小。
     */
    fun size(): Long


    fun length(): Int


}






/**
 * 一个 **群文件**, 或者群文件目录.
 */
public interface GroupFile : File, GroupContainer {
    override val id: String
    override val name: String
    override val accountInfo: AccountInfo
    override val uploader: AccountInfo get() = accountInfo


    /**
     * 此文件所在群信息。
     */
    override val groupInfo: GroupInfo


    /**
     * 文件目录路径。
     */
    val path: String






}


/**
 * 一个**私发文件**。
 */
public interface PrivateFile : File {

}



