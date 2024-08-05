module simbot.core {
    requires kotlin.stdlib;
    requires transitive simbot.api;
    requires static simbot.common.annotations;
    requires transitive simbot.common.collection;
    requires kotlinx.coroutines.core;
    requires kotlinx.serialization.core;

    exports love.forte.simbot.core.application;
    exports love.forte.simbot.core.event;
    exports love.forte.simbot.core.event.impl;
}
