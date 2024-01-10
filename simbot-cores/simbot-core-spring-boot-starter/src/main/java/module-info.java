module simbot.spring.starter {
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

    exports love.forte.simbot.spring;
    exports love.forte.simbot.spring.application;
    exports love.forte.simbot.spring.configuration;
    exports love.forte.simbot.spring.configuration.application;
    exports love.forte.simbot.spring.configuration.binder;
    exports love.forte.simbot.spring.configuration.config;
    exports love.forte.simbot.spring.configuration.listener;
}
