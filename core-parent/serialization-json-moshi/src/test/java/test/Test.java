/*
 * Copyright (c) 2020. ForteScarlet All rights reserved.
 * Project  parent
 * File     Test.java
 *
 * You can contact the author through the following channels:
 * github https://github.com/ForteScarlet
 * gitee  https://gitee.com/ForteScarlet
 * email  ForteScarlet@163.com
 * QQ     1149159218
 */

package test;

import com.squareup.moshi.Moshi;
import love.forte.simbot.serialization.json.JsonSerializer;
import love.forte.simbot.serialization.json.moshi.MoshiJsonSerializerFactory;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public class Test {
    public static void main(String[] args) {

        final MoshiJsonSerializerFactory factory = new MoshiJsonSerializerFactory(new Moshi.Builder().build());

        String jsonStr = "{\"username\":\"jack\",\"password\":\"123\"}";

        final JsonSerializer<User> jsonSerializer = factory.getJsonSerializer(User.class);

        final User user = jsonSerializer.fromJson(jsonStr);

        System.out.println(user);

        final String json = jsonSerializer.toJson(user);

        System.out.println(json);


    }
}
