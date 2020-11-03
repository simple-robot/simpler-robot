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

package love.forte.simbot.serialization.json.moshi;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

/**
 * @author <a href="https://github.com/ForteScarlet"> ForteScarlet </a>
 */
public class Test {
    public static void main(String[] args) throws IOException {
        final Moshi moshi = new Moshi.Builder().build();

        String jsonStr = "{\"username\":\"jack\",\"password\":\"123\"}";


        final JsonAdapter<User> adapter = moshi.adapter(User.class);
        final User user = adapter.fromJson(jsonStr);

        // adapter.toJson();

        System.out.println(user);

    }
}
