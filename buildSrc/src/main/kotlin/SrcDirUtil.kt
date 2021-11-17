import org.gradle.api.Project
import java.io.File

fun Project.src(base: String) = File(projectDir, "src/$base")

fun File.kt(name: String) = File(this, "$name/kotlin")
fun File.resources(name: String) = File(this, "$name/resources")

val File.main get() = kt("main")
val File.test get() = kt("test")

val File.mains get() = listOf(main)
val File.tests get() = listOf(test)

val File.resources get() = resources("main")
val File.resourcesList get() = listOf(resources)

fun Project.srcMain(base: String) = src(base).main
fun Project.srcTest(base: String) = src(base).test

fun Project.srcMains(base: String) = listOf(srcMain(base))
fun Project.srcTests(base: String) = listOf(srcTest(base))


fun Project.src(source: String, base: String) = src(base).kt(source)
fun Project.resources(source: String, base: String) = src(base).resources(source)

fun Project.srcList(source: String, base: String) = listOf(src(source, base))
fun Project.resourcesList(source: String, base: String) = listOf(resources(source, base))


data class TargetAndSource(val target: String, val source: String)


fun String.toTargetAndSource(): TargetAndSource {
    val pre = this.substring(0, length - 4)
    val end = this.substring(length - 4)

    return TargetAndSource(pre, end.toLowerCase())
}
