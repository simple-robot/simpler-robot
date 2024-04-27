module simbot.spring2boot.starter {
    // Kotlin
    requires kotlin.stdlib;
    requires kotlin.reflect;
    requires kotlinx.coroutines.core;
    requires kotlinx.serialization.json;
    // spring
    requires spring.core;
    requires spring.context;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.beans;
    requires spring.aop;
    // simbot
    requires transitive simbot.logger;
    requires simbot.spring.common;
    requires simbot.common.core;
    requires static simbot.common.annotations;

    exports love.forte.simbot.spring2;
    exports love.forte.simbot.spring2.application;
    exports love.forte.simbot.spring2.configuration;
    exports love.forte.simbot.spring2.configuration.application;
    exports love.forte.simbot.spring2.configuration.binder;
    exports love.forte.simbot.spring2.configuration.config;
    exports love.forte.simbot.spring2.configuration.listener;
    exports love.forte.simbot.spring2.warn;
}
