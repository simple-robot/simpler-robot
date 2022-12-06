/**
 *
 * @author ForteScarlet
 */
class ConsoleTest {

    //@Test
    fun consoleTest() {
        console.log("[trace]",  "[ConsoleTest]:", "Hello World")
        console.log("[debug]",  "[ConsoleTest]:", "Hello World")
        console.info("[info]",  "[ConsoleTest]:", "Hello World")
        console.warn("[warn]",  "[ConsoleTest]:", "Hello World")
        console.error("[error]",  "[ConsoleTest]:", "Hello World")
    }
}
