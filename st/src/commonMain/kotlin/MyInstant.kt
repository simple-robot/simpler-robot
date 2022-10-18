expect class MyInstant



object Foo {
    @JvmStatic
    fun printInstant(instant: MyInstant) {
        println("instant: $instant")
    }
}