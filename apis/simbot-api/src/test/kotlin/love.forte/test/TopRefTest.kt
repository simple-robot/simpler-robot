package love.forte.test


const val name_0: String = ""

val enclos = object : Any(){}.javaClass.enclosingClass

object TopRefTest

fun main() {
    println(enclos)
    println(TopRefTest::class.java.protectionDomain)
    println("codeSource: " + TopRefTest::class.java.protectionDomain.codeSource)
    println("codeSource: " + TopRefTest::class.java.protectionDomain.codeSource.codeSigners?.joinToString(","))
    println("codeSource: " + TopRefTest::class.java.protectionDomain.codeSource.location)
}