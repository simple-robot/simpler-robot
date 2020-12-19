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

package love.forte.simbot.serialization.json.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import love.forte.common.ioc.annotation.ConfigBeans;
import love.forte.common.ioc.annotation.SpareBeans;

/**
 * jackson json serialization 配置类。
 * @author ForteScarlet
 */
@ConfigBeans
public class JacksonSerializerConfiguration {


    @SpareBeans("objectMapper")
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }


    @SpareBeans("jacksonSerializerFactory")
    public JacksonSerializerFactory jacksonSerializerFactory(ObjectMapper objectMapper) {
        return new JacksonSerializerFactory(objectMapper);
    }


}
