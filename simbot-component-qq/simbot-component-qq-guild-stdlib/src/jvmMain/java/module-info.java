module simbot.component.qqguild.stdlib {
    requires kotlin.stdlib;
    requires transitive simbot.component.qqguild.api;
    // simbot
    requires transitive simbot.common.stageloop;
    requires transitive simbot.common.atomic;
    requires static simbot.common.annotations;
    // ktor
    requires io.ktor.client.content.negotiation;
    requires io.ktor.serialization.kotlinx.json;
    requires io.ktor.client.websockets;
    requires io.ktor.client.core;

    requires static org.jetbrains.annotations;

    exports love.forte.simbot.qguild.stdlib;
}
