module simbot.component.qq.common {
    requires kotlin.stdlib;

    // simbot
    requires static simbot.common.annotations;
    requires transitive simbot.logger;
    requires transitive org.slf4j;
    requires transitive simbot.common.core;


    exports love.forte.simbot.qguild;
}
