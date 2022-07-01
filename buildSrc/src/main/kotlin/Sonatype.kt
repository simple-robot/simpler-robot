@Suppress("ClassName")
sealed class Sonatype {
    abstract val name: String
    abstract val url: String
    
    object Central : Sonatype() {
        const val NAME = "central"
        const val URL = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
        override val name: String get() = NAME
        override val url: String get() = URL
    }
    
    object Snapshot : Sonatype() {
        const val NAME = "snapshot"
        const val URL = "https://oss.sonatype.org/content/repositories/snapshots/"
        override val name: String get() = NAME
        override val url: String get() = URL
    }
}