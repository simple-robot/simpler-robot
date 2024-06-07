module simbot.common.core {
    requires kotlin.stdlib;
    requires kotlinx.coroutines.core;
    requires static simbot.common.annotations;
    requires transitive simbot.common.suspendrunner;
    requires transitive simbot.common.collection;
    requires static kotlinx.coroutines.reactor;
    requires transitive kotlinx.serialization.core;
    requires static reactor.core;

    exports love.forte.simbot.common;
    exports love.forte.simbot.common.async;
    exports love.forte.simbot.common.attribute;
    exports love.forte.simbot.common.collectable;
    exports love.forte.simbot.common.coroutines;
    exports love.forte.simbot.common.exception;
    exports love.forte.simbot.common.function;
    exports love.forte.simbot.common.id;
    exports love.forte.simbot.common.serialization;
    exports love.forte.simbot.common.services;
    exports love.forte.simbot.common.text;
    exports love.forte.simbot.common.time;
    exports love.forte.simbot.common.weak;
}
