module simbot.component.qq.model {
    requires kotlin.stdlib;

    requires transitive kotlinx.serialization.core;
    // simbot
    requires transitive simbot.component.qq.common;
    requires static simbot.common.annotations;
    requires transitive simbot.logger;
    requires transitive org.slf4j;
    requires transitive simbot.common.core;

}
