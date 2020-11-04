/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     FastJsonSerializerConfiguration.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package love.forte.simbot.serialization.json.fastjson;

import love.forte.common.ioc.annotation.ConfigBeans;
import love.forte.common.ioc.annotation.SpareBeans;

/**
 *
 * fastjson serializer 配置类。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
@ConfigBeans
public class FastJsonSerializerConfiguration {

    @SpareBeans("fastJsonSerializerFactory")
    public FastJsonSerializerFactory fastJsonSerializerFactory(){
        return new FastJsonSerializerFactory();
    }

}
