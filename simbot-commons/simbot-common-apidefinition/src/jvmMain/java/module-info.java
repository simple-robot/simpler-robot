module simbot.common.apidefinition {
    requires kotlin.stdlib;
    requires io.ktor.http;
    requires transitive kotlinx.serialization.core;

    exports love.forte.simbot.common.apidefinition;
}
