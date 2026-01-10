package love.forte.simbot.qguild.ed25519

import kotlinx.coroutines.test.runTest
import love.forte.simbot.qguild.ed25519.annotations.InternalEd25519Api
import kotlin.test.Test
import kotlin.test.assertContentEquals


/**
 *
 * @author ForteScarlet
 */
@OptIn(InternalEd25519Api::class)
class Ed25519CommonTests {
    companion object {
        // 下面这些都是官方文档里提供的用于测试的数据，不包含敏感信息。

        private const val ASSERT_PUBLIC_HEX = "d7c362fe78aef81ff23287b493628b5db02a3c4fe30b215e4d19609b5d76673a"

        // 似乎 private bytes = seed.bytes + public.bytes
        private const val ASSERT_PRIVATE_HEX =
            "6e614f43306f635145337368574c41666666564c42317268595047376e614f43" +
                    "d7c362fe78aef81ff23287b493628b5db02a3c4fe30b215e4d19609b5d76673a"

        private const val SEED = "naOC0ocQE3shWLAfffVLB1rhYPG7naOC"

        private const val SECRET = "DG5g3B4j9X2KOErG"
        private const val PLAIN_TOKEN = "Arq0D5A61EgUu4OxUvOp"
        private const val TS = "1725442341"
        // val body = """{"d":{"plain_token":"Arq0D5A61EgUu4OxUvOp","event_ts":"1725442341"},"op":13}"""

        private const val SIGNATURE =
            "87befc99c42c651b3aac0278e71ada338433ae26fcb24307bdc5ad38c1adc2d01bcfcadc0842edac85e85205028a1132afe09280305f13aa6909ffc2d652c706"
    }

    @Test
    fun testCommonKeyPairGeneration() = runTest {
        val (pri, pub) = ed25519KeyPairGenerator().generate(SEED.encodeToByteArray())
        val publicKeyBytes = pub.bytes
        val privateKeyCombined = pri.bytes

        verifyKeyPair(publicKeyBytes, privateKeyCombined)
    }

    @OptIn(ExperimentalStdlibApi::class)
    private fun verifyKeyPair(publicKeyBytes: ByteArray, privateKeyBytes: ByteArray) {
        assertContentEquals(
            ASSERT_PUBLIC_HEX.hexToByteArray(),
            publicKeyBytes,
            "public key not equals"
        )

        assertContentEquals(
            ASSERT_PRIVATE_HEX.hexToByteArray(),
            privateKeyBytes,
            "private key not equals"
        )
    }
}
