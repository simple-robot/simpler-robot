import love.forte.simbot.component.ComponentFactoryProvider;
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBotManagerFactoryConfigurerProvider;
import love.forte.simbot.component.onebot.v11.core.bot.OneBotBotManagerFactoryProvider;
import love.forte.simbot.component.onebot.v11.core.component.OneBot11ComponentFactoryConfigurerProvider;
import love.forte.simbot.component.onebot.v11.core.component.OneBot11ComponentFactoryProvider;
import love.forte.simbot.plugin.PluginFactoryProvider;

module simbot.component.onebot11v.core {
    requires kotlin.stdlib;
    requires simbot.logger;
    requires simbot.common.suspendrunner;
    requires simbot.component.onebot.common;
    requires simbot.api;
    requires simbot.common.core;
    requires static simbot.common.annotations;
    requires transitive simbot.component.onebot11v.common;
    requires transitive simbot.component.onebot11v.message;
    requires transitive simbot.component.onebot11v.event;

    requires transitive kotlinx.coroutines.core;
    requires transitive kotlinx.serialization.core;
    requires transitive kotlinx.serialization.json;

    requires io.ktor.client.content.negotiation;
    requires io.ktor.client.core;
    requires io.ktor.client.websockets;
    requires io.ktor.http;
    requires io.ktor.io;

    exports love.forte.simbot.component.onebot.v11.core;
    exports love.forte.simbot.component.onebot.v11.core.actor;
    exports love.forte.simbot.component.onebot.v11.core.api;
    exports love.forte.simbot.component.onebot.v11.core.api.nonstandard;
    exports love.forte.simbot.component.onebot.v11.core.bot;
    exports love.forte.simbot.component.onebot.v11.core.component;
    exports love.forte.simbot.component.onebot.v11.core.event;
    exports love.forte.simbot.component.onebot.v11.core.event.message;
    exports love.forte.simbot.component.onebot.v11.core.event.meta;
    exports love.forte.simbot.component.onebot.v11.core.event.notice;
    exports love.forte.simbot.component.onebot.v11.core.event.request;
    exports love.forte.simbot.component.onebot.v11.core.event.stage;
    exports love.forte.simbot.component.onebot.v11.core.utils;
    exports love.forte.simbot.component.onebot.v11.core.message;

    provides ComponentFactoryProvider with OneBot11ComponentFactoryProvider;
    uses OneBot11ComponentFactoryConfigurerProvider;

    provides PluginFactoryProvider with OneBotBotManagerFactoryProvider;
    uses OneBotBotManagerFactoryConfigurerProvider;
}
