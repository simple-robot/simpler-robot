module simbot.component.qqguild.api {
    requires kotlin.stdlib;

    requires transitive kotlinx.coroutines.core;
    requires transitive kotlinx.serialization.core;
    requires transitive kotlinx.serialization.json;
    // simbot
    requires static simbot.common.annotations;
    requires transitive simbot.logger;
    requires transitive org.slf4j;
    requires transitive simbot.common.apidefinition;
    requires transitive simbot.common.suspendrunner;
    requires transitive simbot.common.core;
    // ktor
    requires io.ktor.http;
    requires io.ktor.client.core;
    requires io.ktor.client.content.negotiation;
    requires io.ktor.utils;
    requires io.ktor.io;


    exports love.forte.simbot.qguild;
    exports love.forte.simbot.qguild.event;
    exports love.forte.simbot.qguild.message;
    exports love.forte.simbot.qguild.model;
    exports love.forte.simbot.qguild.model.forum;
    exports love.forte.simbot.qguild.time;
    exports love.forte.simbot.qguild.api;
    exports love.forte.simbot.qguild.api.files;
    exports love.forte.simbot.qguild.api.app;
    exports love.forte.simbot.qguild.api.announces;
    exports love.forte.simbot.qguild.api.apipermission;
    exports love.forte.simbot.qguild.api.channel;
    exports love.forte.simbot.qguild.api.channel.permissions;
    exports love.forte.simbot.qguild.api.channel.pins;
    exports love.forte.simbot.qguild.api.channel.schedules;
    exports love.forte.simbot.qguild.api.forum;
    exports love.forte.simbot.qguild.api.guild;
    exports love.forte.simbot.qguild.api.guild.mute;
    exports love.forte.simbot.qguild.api.member;
    exports love.forte.simbot.qguild.api.message;
    exports love.forte.simbot.qguild.api.message.direct;
    exports love.forte.simbot.qguild.api.message.setting;
    exports love.forte.simbot.qguild.api.message.group;
    exports love.forte.simbot.qguild.api.message.user;
    exports love.forte.simbot.qguild.api.role;
    exports love.forte.simbot.qguild.api.user;
}
