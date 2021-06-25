/*
 *
 *  * Copyright (c) 2021. ForteScarlet All rights reserved.
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

package love.test.guild;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import love.forte.simbot.component.lovelycat.message.LovelyCatApiResult;
import love.forte.simbot.serialization.json.JsonSerializer;
import love.forte.simbot.serialization.json.jackson.JacksonSerializerFactory;

/**
 * @author ForteScarlet
 */
public class JsonTest {
    public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);


        JacksonSerializerFactory jacksonSerializerFactory = new JacksonSerializerFactory(objectMapper);

        String json = "{\"Code\":-5,\"result\":\"OK\",\"Data\":\"[{\\\"robot_wxid\\\":\\\"wxid_bqy1ezxxkdat22\\\",\\\"wxid\\\":\\\"wxid_bqy1ezxxkdat22\\\",\\\"wx_num\\\":\\\"\\\",\\\"nickname\\\":\\\"\\u6cd5\\u6b27\\u7279\\u65af\\u5361\\u96f7\\u7279\\\",\\\"head_url\\\":\\\"http:\\\\/\\\\/wx.qlogo.cn\\\\/mmhead\\\\/ver_1\\\\/cvDnHzv1zoqg8icfY2sL8ohWcen7dLNIHhOE5uIZPdO7RmrnsFqqyHK9pkIk75Ckic58N3BS117Vu3nO4ga9T6CmpxibfCT9aw67u21ALXH50Q\\\\/132\\\",\\\"headimgurl\\\":\\\"http:\\\\/\\\\/wx.qlogo.cn\\\\/mmhead\\\\/ver_1\\\\/cvDnHzv1zoqg8icfY2sL8ohWcen7dLNIHhOE5uIZPdO7RmrnsFqqyHK9pkIk75Ckic58N3BS117Vu3nO4ga9T6CmpxibfCT9aw67u21ALXH50Q\\\\/132\\\",\\\"signature\\\":\\\"\\\",\\\"backgroundimgurl\\\":\\\"http:\\\\/\\\\/shmmsns.qpic.cn\\\\/mmsns\\\\/FrdAUicrPIibfEYyBu87rvf1330drw5BK8tyibNPFmlFu5690dEVgE6icjjB9sVhBK9F4K7uHVPdEM0\\\\/0\\\",\\\"update_desc\\\":\\\"robot_wxid\\u3001head_url\\u3001wx_hand\\u3001\\u8fd9\\u4e09\\u4e2a\\u5c5e\\u6027\\u4e3a\\u517c\\u5bb9\\u8001\\u7248\\u672c\\u51fa\\u73b0\\u7684\\uff0c\\u5efa\\u8bae\\u66f4\\u6362\\u65b0\\u5c5e\\u6027\\\",\\\"status\\\":1,\\\"wx_hand\\\":396154,\\\"wx_wind_handle\\\":396154,\\\"pid\\\":15380,\\\"login_time\\\":1610796044}]\"}";

        JsonSerializer<LovelyCatApiResult> jsonSerializer = jacksonSerializerFactory.getJsonSerializer(LovelyCatApiResult.class);

        LovelyCatApiResult result = jsonSerializer.fromJson(json);

        System.out.println(result);


    }
}
