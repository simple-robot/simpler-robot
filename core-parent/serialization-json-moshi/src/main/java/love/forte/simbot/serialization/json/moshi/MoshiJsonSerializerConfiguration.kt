/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     MoshiJsonSerializerConfiguration.kt
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.serialization.json.moshi

import com.squareup.moshi.Moshi
import love.forte.common.ioc.annotation.Beans
import love.forte.common.ioc.annotation.ConfigBeans

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
    @Beans("moshiBuilder")
    public fun moshiBuilder(): Moshi.Builder = Moshi.Builder()

    /**
     * 构建一个 [MoshiJsonSerializerFactory] 实例。需要注入一个 [Moshi.Builder] 实例。
     * 如果需要对 [Moshi.Builder] 进行自定义，可以通过 prePass 来对builder进行中间操作。
     */
    @Beans("moshiJsonSerializerFactory")
    public fun moshiJsonSerializerFactory(moshiBuilder: Moshi.Builder): MoshiJsonSerializerFactory = MoshiJsonSerializerFactory(moshiBuilder.build())


}