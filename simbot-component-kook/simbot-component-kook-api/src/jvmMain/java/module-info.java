module simbot.component.kook.api {
    requires kotlin.stdlib;
    requires kotlinx.coroutines.core;
    requires transitive simbot.common.annotations;
    requires transitive simbot.logger;
    requires transitive simbot.common.apidefinition;
    requires transitive simbot.common.suspendrunner;
    requires transitive simbot.common.core;
    requires io.ktor.client.core;
    requires io.ktor.client.content.negotiation;
    requires io.ktor.io;
    requires io.ktor.utils;
    requires transitive kotlinx.serialization.core;
    requires transitive kotlinx.serialization.json;

    exports love.forte.simbot.kook;
    exports love.forte.simbot.kook.api;
    exports love.forte.simbot.kook.api.asset;
    exports love.forte.simbot.kook.api.blacklist;
    exports love.forte.simbot.kook.api.category;
    exports love.forte.simbot.kook.api.channel;
    exports love.forte.simbot.kook.api.guild;
    exports love.forte.simbot.kook.api.invite;
    exports love.forte.simbot.kook.api.member;
    exports love.forte.simbot.kook.api.message;
    exports love.forte.simbot.kook.api.role;
    exports love.forte.simbot.kook.api.template;
    exports love.forte.simbot.kook.api.thread;
    exports love.forte.simbot.kook.api.user;
    exports love.forte.simbot.kook.api.userchat;
    exports love.forte.simbot.kook.api.voice;
    exports love.forte.simbot.kook.event;
    exports love.forte.simbot.kook.messages;
    exports love.forte.simbot.kook.objects;
    exports love.forte.simbot.kook.objects.card;
    exports love.forte.simbot.kook.objects.kmd;
    exports love.forte.simbot.kook.util;
}
