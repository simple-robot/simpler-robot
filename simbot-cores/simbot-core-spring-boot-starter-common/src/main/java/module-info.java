module simbot.spring.common {
    requires kotlin.stdlib;
    requires static simbot.common.annotations;
    requires transitive simbot.quantcat.common;
    requires transitive simbot.core;
    requires static java.annotation;
    requires kotlinx.coroutines.core;

    exports love.forte.simbot.spring.common.application;
    exports love.forte.simbot.spring.common;
}
