import org.gradle.api.attributes.Attribute

object SimbotAttributes {

    @JvmField
    val MODULE_NAME = Attribute.of("simbot.module.name", String::class.java)


}