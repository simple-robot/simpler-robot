/*
 *
 *  * Copyright (c) 2020. ForteScarlet All rights reserved.
 *  * Project  component-onebot
 *  * File     LovelyCatServer.java
 *  *
 *  * You can contact the author through the following channels:
 *  * github https://github.com/ForteScarlet
 *  * gitee  https://gitee.com/ForteScarlet
 *  * email  ForteScarlet@163.com
 *  * QQ     1149159218
 *  *
 *  *
 *
 */

package love.forte.simbot.spring.lovelycat.server;

import love.forte.simbot.bot.NoSuchBotException;
import love.forte.simbot.component.lovelycat.LovelyCatApiManager;
import love.forte.simbot.component.lovelycat.LovelyCatApiTemplate;
import love.forte.simbot.component.lovelycat.message.event.LovelyCatEventParser;
import love.forte.simbot.component.lovelycat.message.event.LovelyCatMsg;
import love.forte.simbot.component.lovelycat.message.event.LovelyCatParser;
import love.forte.simbot.core.SimbotContext;
import love.forte.simbot.listener.MsgGetProcessor;
import love.forte.simbot.serialization.json.JsonSerializer;
import love.forte.simbot.serialization.json.JsonSerializerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

/**
 * 可爱猫监听服务器。
 * @author ForteScarlet
 */
@RestController
@ConditionalOnBean(SimbotContext.class)
public class LovelyCatServer {

    private final SimbotContext simbotContext;
    private final LovelyCatParser lovelyCatParser;
    private final MsgGetProcessor msgGetProcessor;
    private final JsonSerializerFactory jsonSerializerFactory;
    private final JsonSerializer<Map> jsonMapSerializer;
    private final LovelyCatApiManager lovelyCatApiManager;

    @Autowired
    public LovelyCatServer(SimbotContext simbotContext, MsgGetProcessor msgGetProcessor) {
        this.simbotContext = simbotContext;
        this.lovelyCatParser = simbotContext.get(LovelyCatParser.class);
        this.msgGetProcessor = simbotContext.get(MsgGetProcessor.class);
        this.jsonSerializerFactory = simbotContext.get(JsonSerializerFactory.class);
        this.jsonMapSerializer = jsonSerializerFactory.getJsonSerializer(Map.class);
        this.lovelyCatApiManager = simbotContext.get(LovelyCatApiManager.class);
    }

    @PostMapping("${simbot.component.lovelycat.server.path:/lovelycat}")
    public Object cat(@RequestBody Map<String, Object> params){
        Object event = params.get("Event");
        if (event == null) {
            throw new NullPointerException("No param 'Event'.");
        }
        String eventStr = event.toString();
        Object botId = params.get("robot_wxid");
        if (botId == null) {
            throw new NoSuchBotException("no param 'robot_wxid' or 'rob_wxid' in lovelycat request param.");
        }
        String botIdStr = botId.toString();
        LovelyCatApiTemplate api = lovelyCatApiManager.getApi(botIdStr);
        if (api == null) {
            throw new IllegalStateException("cannot found Bot("+ botIdStr +")'s api template.");
        }

        String originalJson = jsonMapSerializer.toJson(params);

        LovelyCatEventParser parser = lovelyCatParser.parser(eventStr);

        if (parser != null) {
            Class<? extends LovelyCatMsg> type = parser.type();
            if (type != null) {
                return msgGetProcessor.onMsgIfExist(type, () -> parser.invoke(originalJson, api, jsonSerializerFactory, params));
            } else {
                LovelyCatMsg lovelyCatMsg = parser.invoke(originalJson, api, jsonSerializerFactory, params);
                return msgGetProcessor.onMsgIfExist(lovelyCatMsg.getClass(), lovelyCatMsg);
            }
        }

        return Collections.singletonMap("code", 200);
    }

}
