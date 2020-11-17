/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  component-ding
 * File     dingSenderManager.kt
 * Date  2020/8/8 下午8:15
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.component.ding.sender

import love.forte.simbot.component.ding.exception.DingModuleException


/**
 * 钉钉送信管理器
 */
interface DingSenderManager {
    /**
     * 根据access_token获取对应的[DingSender]
     */
    operator fun get(token: String): DingSender?

    /**
     * 获取一个默认的送信器
     */
    val default: DingSender
}


@Suppress("DIFFERENT_NAMES_FOR_THE_SAME_PARAMETER_IN_SUPERTYPES")
open class DingSenderManagerImpl(map: MutableMap<String, DingSender> = mutableMapOf()):
        DingSenderManager, MutableMap<String, DingSender> by map {

    private lateinit var _default: DingSender

    /**
     * 获取一个默认的值
     */
    override val default: DingSender
    get(){
        if(!::_default.isInitialized){
            _default = this.values.firstOrNull() ?: throw DingModuleException("noDefaultSender")
        }
        return _default
    }
}