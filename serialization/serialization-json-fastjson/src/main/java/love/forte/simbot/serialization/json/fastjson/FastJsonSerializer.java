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

package love.forte.simbot.serialization.json.fastjson;
import com.alibaba.fastjson.JSON;
import love.forte.simbot.serialization.json.JsonSerializer;

import java.lang.reflect.Type;

/**
 *
 * fast json 针对某个实例的具体序列化。
 * 由于内部没有什么特别的内容，因此此类不进行缓存，需要时进行构造即可。
 *
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public final class FastJsonSerializer<T> implements JsonSerializer<T> {

    private final Type type;

    public FastJsonSerializer(Class<T> type){
        this.type = type;
    }

    public FastJsonSerializer(Type type){
        this.type = type;
    }

    @Override
    public String toJson(T entity) {
        return JSON.toJSONString(entity);
    }

    @Override
    public T fromJson(String json) {
        return JSON.parseObject(json, type);
    }
}
