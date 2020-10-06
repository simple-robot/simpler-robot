/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     AtDetection.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.core.filter

import love.forte.simbot.core.api.message.MsgGet


/**
 * at检测器。用于判断bot是否被at了。
 */
public fun interface AtDetection {

    /**
     * 如果bot被at了，则返回true。
     */
    fun atBot(): Boolean
}


/**
 * [AtDetection] 工厂。
 */
public interface AtDetectionFactory {

    /**
     * 根据一个msg实例构建一个 [AtDetection] 函数。
     *
     * 在manager中，如果此方法返回了一个 null 则视为获取失败，会去尝试使用其他 factory 直至成功。
     *
     */
    fun getAtDetection(msg: MsgGet): AtDetection
}



/**
 * [AtDetection] 注册器。
 */
public interface AtDetectionRegistrar {
    /**
     * 注册一个 [AtDetection] 构建函数。
     */
    fun registryAtDetection(atDetectionFactory: AtDetectionFactory)
}
