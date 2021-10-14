import kotlinx.serialization.json.*
import java.io.Writer
import kotlin.io.path.*

fun main() {


    val resource = ClassLoader.getSystemClassLoader().getResourceAsStream("jsons.json")!!
    val json = resource.use { r -> Json.parseToJsonElement(r.bufferedReader().readText()) }
    json.jsonArray.forEach {
        genFromJson(it,
            "love.forte.simbot.kaiheila.event.system.guild.role",
            "GuildRoleEventExtraBody",
            "GuildRoleEventExtra")
    }


}


fun genFromJson(json: JsonElement, p: String, bodyParent: String, extraParent: String) {
    val extra = json.jsonObject["d"]!!.jsonObject["extra"]!!
    val type = extra.jsonObject["type"]!!.jsonPrimitive.content
    val fileName = type.toUpperBase() + "Event.kt"
    println("FileName: $fileName")

    val fields = mutableMapOf<String, String>()
    val bodyJson = extra.jsonObject["body"]!!.jsonObject
    bodyJson.forEach { k, v ->
        val vType = when (v) {
            is JsonArray -> "List<?>, // " + v.toString().replace(Regex("\r\n"), " ")
            is JsonPrimitive -> when {
                v.isString -> "String,"
                else -> "Long,"
            }
            is JsonObject -> "obj, // " + v.toString().replace(Regex("\r\n"), " ")
            is JsonNull -> "?, // null" // + v.toString().replace(Regex("\r\n"), " ")
        }
        fields[k] = vType
        println("$space4 $k : $vType")
    }

    val out = Path("g_out/$fileName")
    out.parent.createDirectories()
    out.deleteIfExists()
    out.createFile()
    out.bufferedWriter().use { writer ->
        writer.writeBody(type, p, fields, bodyParent)
        writer.writeExtra(type, extraParent)
    }


    println()


}

fun Writer.writeBody(type: String, p: String, fields: Map<String, String>, bodyParent: String) {
    write("package ")
    write(p)
    write("\n\n")
    write("""
        import kotlinx.serialization.SerialName
        import kotlinx.serialization.Serializable
        
        
    """.trimIndent())
    write("""
        /**
         *
         *
         * @author ForteScarlet
         */
         
    """.trimIndent())
    write("@Serializable\n")
    write("public data class ")
    write(type.toUpperBase())
    write("EventBody(\n")
    fields.forEach { (k, v) ->
        write("""
            1${space4}/**
            1$space4 *
            1$space4 */
        """.trimMargin("1"))
        write("\n$space4")
        val fn = k.toUpperBase2()
        if (fn != k) {
            write("@SerialName(\"")
            write(k)
            write("\")\n")
        }
        write("${space4}val ")
        write(fn)
        write(": ")
        write(v)
        write("\n")
    }
    write(") : ")
    write(bodyParent)
    write("\n\n\n")
}


fun Writer.writeExtra(type: String, extraParent: String) {
    write("""
        /**
         *
         *
         * `$type`
         *
         * @author ForteScarlet
         */
         
    """.trimIndent())
    write("@Serializable\n")
    write("public data class ")
    write(type.toUpperBase())
    write("EventExtra(override val body: ")
    write(type.toUpperBase())
    write("EventBody) : \n")
    write(space4)
    write(extraParent)
    write("<")
    write(type.toUpperBase())
    write("EventBody> { \n")
    write("${space4}override val type: String\n")
    write("${space4}${space4}get() = \"")
    write(type)
    write("\"\n")
    write("}")
}

/*

/**
 * 新成员加入服务器
 *
 * `joined_guild`
 * @author ForteScarlet
 */
@Serializable
public data class JoinedGuildEventExtra(override val body: JoinedGuildEventBody) :
    GuildMemberEventExtra<JoinedGuildEventBody> {
    override val type: String
        get() = "joined_guild"
}
 */



internal fun String.toUpperBase(): String {
    // var i = 0
    return split('_').joinToString("") { s ->
        // if (i++ > 0)
        s.first().uppercase() + s.substring(1)
        // else s
    }
}
internal fun String.toUpperBase2(): String {
    var i = 0
    return split('_').joinToString("") { s ->
        if (i++ > 0)
        s.first().uppercase() + s.substring(1)
        else s
    }
}

internal const val space4 = "    "


