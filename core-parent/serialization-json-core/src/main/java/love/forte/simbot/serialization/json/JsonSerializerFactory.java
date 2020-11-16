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

package love.forte.simbot.serialization.json;

import java.lang.reflect.Type;

/**
 * Json序列化器 {@link JsonSerializer} 构建工厂，用于构建对应类型的json解析器。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public interface JsonSerializerFactory {

    /**
     * 获取一个对应类型的json解析器。
     * @param type 类型。
     * @return 对应的解析器。
     */
    <T> JsonSerializer<T> getJsonSerializer(Type type);

    /**
     * 获取一个对应类型的json解析器。
     * @param type 类型。
     * @return 对应的解析器。
     */
    <T> JsonSerializer<T> getJsonSerializer(Class<T> type);



}
