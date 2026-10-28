# internal ed25519 module

一个内部使用的用于提供对 ed25519 签名的支持的模块。
通过 `ed25519KeyPairGenerator()` 获取一个当前平台支持的 `Ed25519KeyPairGenerator` 实例。

```Kotlin
val generator = ed25519KeyPairGenerator()
val (privateKey, publicKey) = generator.generate(seed)
```

非 JVM 平台中，均使用 [libsodium bindings](https://github.com/ionspin/kotlin-multiplatform-libsodium)
作为实现。

JVM 中优先尝试检测是否存在以下类：

- `org.bouncycastle.crypto.params.Ed25519PrivateKeyParameters`
- `org.bouncycastle.crypto.params.Ed25519PublicKeyParameters`
- `org.bouncycastle.crypto.signers.Ed25519Signer`

如果存在，则使用这些类进行签名操作，也就是使用 [BouncyCastle](https://www.bouncycastle.org/) 
(编译时使用的是 `org.bouncycastle:bcprov-jdk18on:1.80`)
进行密钥的生成或签名/验签。

当 BouncyCastle 的相关类存在时，使用 `ed25519KeyPairGenerator().generate(seed)`  相当于：

```Kotlin
val (privateKey, publicKey) = BouncyCastleEd25519KeyPair(seed)
```

如果 BouncyCastle 的相关类不存在，则使用 [ed25519-java](https://github.com/str4d/ed25519-java)
作为实现库。此库默认作为 `implementation` 加载于 classpath 中。
相比于 BouncyCastle (≈8,480 KB ≈8.48 MB)，它轻量得多 (≈62 KB)。 
