//import com.ionspin.kotlin.crypto.LibsodiumInitializer
//import com.ionspin.kotlin.crypto.signature.Signature
//import com.ionspin.kotlin.crypto.signature.crypto_sign_BYTES
//import io.ktor.client.engine.mock.*
//import io.ktor.utils.io.core.*
//import kotlinx.coroutines.test.runTest
//import love.forte.simbot.qguild.stdlib.BotFactory
//import love.forte.simbot.qguild.stdlib.EmitResult
//import kotlin.test.Test
//import kotlin.test.assertContentEquals
//import kotlin.test.assertEquals
//import kotlin.test.assertIs
//
///**
// *
// * @author ForteScarlet
// */
//class Ed25519TestsCommon {
//
//
////    @OptIn(ExperimentalStdlibApi::class)
////    @Test
////    fun ed25519KeyGenTest() {
////        // val secret = "naOC0ocQE3shWLAfffVLB1rhYPG7"
////        val seed = "naOC0ocQE3shWLAfffVLB1rhYPG7naOC"
////
////
////
////        val pk = Ed25519.keyFromSeed(seed.toByteArray())
////
////        assertContentEquals(
////            ASSERT_PUBLIC_HEX.hexToByteArray(),
////            pk.publicKey().toByteArray()
////        )
////
////        assertContentEquals(
////            ASSERT_PRIVATE_HEX.hexToByteArray(),
////            pk.toByteArray()
////        )
////    }
//
////    @BeforeTest
////    fun initLibsodiumInitializer() = runTest {
////        if (!LibsodiumInitializer.isInitialized()) {
////            LibsodiumInitializer.initialize()
////        }
////    }
//
//    /**
//     * https://bot.q.qq.com/wiki/develop/api-v2/dev-prepare/interface-framework/sign.html
//     *
//     * 验证签名过程:
//     * 根据开发者平台的 Bot Secret 值进行repeat操作得到签名32字节的 seed ,
//     * 根据 seed 调用 Ed25519 算法生成32字节公钥
//     */
//    @OptIn(ExperimentalUnsignedTypes::class, ExperimentalStdlibApi::class)
//    @Test
//    fun libsodiumEd25519KeyGenTest() = runTest {
//        LibsodiumInitializer.initialize()
//        // val secret = "naOC0ocQE3shWLAfffVLB1rhYPG7"
//        val seed = "naOC0ocQE3shWLAfffVLB1rhYPG7naOC"
//
//        val pk = Signature.seedKeypair(seed.toByteArray().asUByteArray())
//
//        assertContentEquals(
//            ASSERT_PUBLIC_HEX.hexToByteArray(),
//            pk.publicKey.asByteArray()
//        )
//
//        assertContentEquals(
//            ASSERT_PRIVATE_HEX.hexToByteArray(),
//            pk.secretKey.asByteArray()
//        )
//    }
//
//    @Test
//    fun ed25519SeedPadding() {
//        assertEquals(
//            "DG5g3B4j9X2KOErGDG5g3B4j9X2KOErG",
//            "DG5g3B4j9X2KOErG".paddingEd25519Seed()
//        )
//        assertEquals(
//            "DG5g3B4j9X2KOErGDG5g3B4j9X2KOErG",
//            "DG5g3B4j9X2KOErGDG5g3B4j9X2KOErGDG5g3B4j9X2KOErGDG5g3B4j9X2KOErG".paddingEd25519Seed()
//        )
//    }
//
////    @OptIn(ExperimentalStdlibApi::class)
////    @Test
////    fun ed25519VerifyTest() {
////        // val appId = "11111111"
////        val secret = "DG5g3B4j9X2KOErG"
////        val seed = secret.paddingEd25519Seed()
////        val plainToken = "Arq0D5A61EgUu4OxUvOp"
////        val ts = "1725442341"
////        // val body = """{"d":{"plain_token":"Arq0D5A61EgUu4OxUvOp","event_ts":"1725442341"},"op":13}"""
////
////        val pk = Ed25519.keyFromSeed(seed.toByteArray())
////        val msg = "$ts$plainToken"
////
////        val signature = pk.sign(msg.toByteArray())
////
////        assertEquals(
////            "87befc99c42c651b3aac0278e71ada338433ae26fcb24307bdc5ad38c1adc2d01bcfcadc0842edac85e85205028a1132afe09280305f13aa6909ffc2d652c706",
////            signature.toHexString()
////        )
////    }
//
//    @OptIn(ExperimentalUnsignedTypes::class, ExperimentalStdlibApi::class)
//    @Test
//    fun libsodiumEd25519VerifyTest() {
//        // val appId = "11111111"
//        val secret = "DG5g3B4j9X2KOErG"
//        val seed = secret.paddingEd25519Seed()
//        val plainToken = "Arq0D5A61EgUu4OxUvOp"
//        val ts = "1725442341"
//        // val body = """{"d":{"plain_token":"Arq0D5A61EgUu4OxUvOp","event_ts":"1725442341"},"op":13}"""
//
//        val pk = Signature.seedKeypair(seed.toByteArray().asUByteArray())
////        val pk = Ed25519.keyFromSeed(seed.toByteArray())
//
//        val msg = "$ts$plainToken"
//
//        // 得到的是加密的密文 (size=crypto_sign_BYTES)
//        // 和原文 (size=msg.size)
//        val signature = Signature.sign(
//            msg.toByteArray().asUByteArray(),
//            pk.secretKey
//        )
//
//        val plain = signature.copyOfRange(crypto_sign_BYTES, signature.size)
//
//        assertEquals(
//            msg.toByteArray().toHexString(),
//            plain.toHexString()
//        )
//
////        val signature = pk.sign(msg.toByteArray())
//
//        assertEquals(
//            "87befc99c42c651b3aac0278e71ada338433ae26fcb24307bdc5ad38c1adc2d01bcfcadc0842edac85e85205028a1132afe09280305f13aa6909ffc2d652c706",
//            signature.copyOf(crypto_sign_BYTES).toHexString()
//        )
//    }
//
//    @Test
//    fun opcode13ProcessTest() = runTest {
//        val bot = BotFactory.create("11111111", "DG5g3B4j9X2KOErG", "") {
//            disableWs = true
//            apiClientEngine = MockEngine {
//                respondOk()
//            }
//        }
//
//
//        val result = bot.emitEvent(
//            """{"d":{"plain_token":"Arq0D5A61EgUu4OxUvOp","event_ts":"1725442341"},"op":13}""",
//        )
//
//        assertIs<EmitResult.Verified>(result)
//        assertEquals(
//            "87befc99c42c651b3aac0278e71ada338433ae26fcb24307bdc5ad38c1adc2d01bcfc" +
//                    "adc0842edac85e85205028a1132afe09280305f13aa6909ffc2d652c706",
//            result.verified.signature,
//            "Verified signature not equal"
//        )
//        assertEquals(
//            "Arq0D5A61EgUu4OxUvOp",
//            result.verified.plainToken,
//            "Verified plainToken not equal"
//        )
//    }
//
//    companion object {
//        private const val ASSERT_PUBLIC_HEX = "d7c362fe78aef81ff23287b493628b5db02a3c4fe30b215e4d19609b5d76673a"
//        private const val ASSERT_PRIVATE_HEX =
//            "6e614f43306f635145337368574c41666666564c42317268595047376e614f43" +
//                    "d7c362fe78aef81ff23287b493628b5db02a3c4fe30b215e4d19609b5d76673a"
//    }
//}
//
