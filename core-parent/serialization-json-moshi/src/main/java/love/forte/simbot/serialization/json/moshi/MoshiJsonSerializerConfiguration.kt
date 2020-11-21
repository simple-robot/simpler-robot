/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
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

package love.forte.simbot.serialization.json.moshi

import com.squareup.moshi.Moshi
import love.forte.common.ioc.annotation.ConfigBeans
import love.forte.common.ioc.annotation.SpareBeans

/**
 *
 * 配置[MoshiJsonSerializerFactory]。
 *
 * @author ForteScarlet -> https://github.com/ForteScarlet
 */
@ConfigBeans
public class MoshiJsonSerializerConfiguration {

    /**
     * 一个 [Moshi.Builder]。
     * 可以通过 prePass 来对builder进行中间操作。
     */
    @SpareBeans("moshiBuilder")
    public fun moshiBuilder(): Moshi.Builder = Moshi.Builder()

    /**
     * 构建一个 [MoshiJsonSerializerFactory] 实例。需要注入一个 [Moshi.Builder] 实例。
     * 如果需要对 [Moshi.Builder] 进行自定义，可以通过 prePass 来对builder进行中间操作或直接注入一个 [Moshi.Builder] 实例。
     */
    @SpareBeans("moshiJsonSerializerFactory")
    public fun moshiJsonSerializerFactory(moshiBuilder: Moshi.Builder): MoshiJsonSerializerFactory = MoshiJsonSerializerFactory(moshiBuilder.build())


}