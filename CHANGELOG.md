# v4.4.0

> Release & Pull Notes: [v4.4.0](https://github.com/simple-robot/simpler-robot/releases/tag/v4.4.0) 

- fix(quantcat): 优化统一MergedBinder对null结果、失败结果的处理 ([`d741b8617`](https://github.com/simple-robot/simpler-robot/commit/d741b8617))
- feat(common-streamable): 增加模块与新的公共类型 Streamable, 用来简化部分针对 Sequence 类型的转化操作，例如在JVM中转为 Stream 和在 JS 中转为数组。 ([`1ba898c57`](https://github.com/simple-robot/simpler-robot/commit/1ba898c57))
- fix(quantcat): 优化统一MergedBinder对null结果、失败结果的处理 ([`9c5777847`](https://github.com/simple-robot/simpler-robot/commit/9c5777847))
- feat(common-streamable): 增加模块与新的公共类型 Streamable, 用来简化部分针对 Sequence 类型的转化操作，例如在JVM中转为 Stream 和在 JS 中转为数组。 ([`ca3eaa02b`](https://github.com/simple-robot/simpler-robot/commit/ca3eaa02b))

# v4.3.1

> Release & Pull Notes: [v4.3.1](https://github.com/simple-robot/simpler-robot/releases/tag/v4.3.1) 

- fix(quantcat): 修复使用KeywordBinder时无法正确处理参数类型的问题 ([`ef31604c7`](https://github.com/simple-robot/simpler-robot/commit/ef31604c7))
- optimize(api): 优化、调整MessagesBuilder实现结构 ([`628c1fe0c`](https://github.com/simple-robot/simpler-robot/commit/628c1fe0c))
- build(deps): bump ktor from 2.3.11 to 2.3.12 ([`28ed531e1`](https://github.com/simple-robot/simpler-robot/commit/28ed531e1))
- build(deps): bump kotlinx-serialization from 1.6.3 to 1.7.1 ([`be15aaef5`](https://github.com/simple-robot/simpler-robot/commit/be15aaef5))
- Kdoc: homepage ([`21bd244df..ad2f21260`](https://github.com/simple-robot/simpler-robot/compare/21bd244df..v4.3.0))

    <details><summary><code>21bd244df..ad2f21260</code></summary>

    - [`21bd244df`](https://github.com/simple-robot/simpler-robot/commit/21bd244df)
    - [`ad2f21260`](https://github.com/simple-robot/simpler-robot/commit/ad2f21260)

    </details>


# v4.3.0

> Release & Pull Notes: [v4.3.0](https://github.com/simple-robot/simpler-robot/releases/tag/v4.3.0) 

- feat(api): 增加接口 RichMediaMessage 用以描述一个富媒体消息元素 ([`eceb41528..6ec2a1a53`](https://github.com/simple-robot/simpler-robot/compare/eceb41528..7e52e17bc))

    <details><summary><code>eceb41528..6ec2a1a53</code></summary>

    - [`eceb41528`](https://github.com/simple-robot/simpler-robot/commit/eceb41528)
    - [`fcd2fd670`](https://github.com/simple-robot/simpler-robot/commit/fcd2fd670)
    - [`c38c80270`](https://github.com/simple-robot/simpler-robot/commit/c38c80270)
    - [`6ec2a1a53`](https://github.com/simple-robot/simpler-robot/commit/6ec2a1a53)

    </details>

- feat(api): 增加接口 BinaryDataAwareMessage 用以描述能够获取到二进制数据的消息元素 ([`7e52e17bc`](https://github.com/simple-robot/simpler-robot/commit/7e52e17bc))
- optimize(api): 增加接口 UrlAwareMessage 用以描述能够获取到 URL 信息的消息元素 ([`933095e9d`](https://github.com/simple-robot/simpler-robot/commit/933095e9d))

# v4.2.0

> Release & Pull Notes: [v4.2.0](https://github.com/simple-robot/simpler-robot/releases/tag/v4.2.0) 

- test: try to fix test timeout ([`7e1cd274`](https://github.com/simple-robot/simpler-robot/commit/7e1cd274))
- optimize(api): 提供更多Application和Bot的辅助扩展API ([`f5802fa9`](https://github.com/simple-robot/simpler-robot/commit/f5802fa9))
- optimize(api): 为BotManager和BotManagers提供更多辅助的获取API ([`84bfef6f`](https://github.com/simple-robot/simpler-robot/commit/84bfef6f))
- build(deps): bump org.jetbrains.kotlinx.binary-compatibility-validator ([`dad1db51`](https://github.com/simple-robot/simpler-robot/commit/dad1db51))
- build(deps): bump org.springframework.boot from 3.3.0 to 3.3.1 ([`392d3be7`](https://github.com/simple-robot/simpler-robot/commit/392d3be7))

# v4.1.0

> Release & Pull Notes: [v4.1.0](https://github.com/simple-robot/simpler-robot/releases/tag/v4.1.0) 

- fix(spring): 修复使用多个Filter时会失效的问题 ([`668ef449`](https://github.com/simple-robot/simpler-robot/commit/668ef449))
- build(deps): bump org.jetbrains.kotlinx.kover from 0.8.0 to 0.8.1 ([`a1c54b59`](https://github.com/simple-robot/simpler-robot/commit/a1c54b59))
- feat(api): JVM中的EventListener兼容类型增加一个 nonBlock 类型用于简化响应式结果的使用 ([`19aa56bc`](https://github.com/simple-robot/simpler-robot/commit/19aa56bc))
- build(deps): bump io.mockk:mockk from 1.13.10 to 1.13.11 ([`a7b5307e`](https://github.com/simple-robot/simpler-robot/commit/a7b5307e))
- feat(api): JVM中的EventListener兼容类型增加一个 nonBlock 类型用于简化响应式结果的使用 ([`beb3c7b3`](https://github.com/simple-robot/simpler-robot/commit/beb3c7b3))

# v4.0.1

> Release & Pull Notes: [v4.0.1](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.1) 

- build(deps): bump ktor from 2.3.8 to 2.3.11 ([`7d1e505a`](https://github.com/simple-robot/simpler-robot/commit/7d1e505a))
- pref(gradle): 优化Gradle的挂起函数辅助插件的配置内容 ([`461b95c1`](https://github.com/simple-robot/simpler-robot/commit/461b95c1))

# v4.0.0

> Release & Pull Notes: [v4.0.0](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0) 

- fix: 修复部分面向Java未被正确隐藏/转化的挂起函数 ([`70e86fe0`](https://github.com/simple-robot/simpler-robot/commit/70e86fe0))
- deps: 更新 suspend-transform 编译器插件版本并借此修复 #849 ([`2d5afd5d`](https://github.com/simple-robot/simpler-robot/commit/2d5afd5d))

# v4.0.0-RC3

> Release & Pull Notes: [v4.0.0-RC3](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0-RC3) 

- build(deps): bump kotlinxBenchmark from 0.4.10 to 0.4.11 ([`b7f4db13`](https://github.com/simple-robot/simpler-robot/commit/b7f4db13))
- build(deps): bump com.squareup:kotlinpoet-ksp from 1.16.0 to 1.17.0 ([`239d5dcc`](https://github.com/simple-robot/simpler-robot/commit/239d5dcc))
- build(deps): bump slf4j from 2.0.12 to 2.0.13 ([`cc77e33b`](https://github.com/simple-robot/simpler-robot/commit/cc77e33b))
- build(deps): bump io.gitlab.arturbosch.detekt from 1.23.3 to 1.23.6 ([`48588934`](https://github.com/simple-robot/simpler-robot/commit/48588934))
- fix: Application的coroutineContext应当始终有一个Job ([`539774e0`](https://github.com/simple-robot/simpler-robot/commit/539774e0))

# v4.0.0-RC2

> Release & Pull Notes: [v4.0.0-RC2](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0-RC2) 

- fix: Services.addProviderExceptJvm 的jvm判断条件与实际情况相反 ([`ba555b22`](https://github.com/simple-robot/simpler-robot/commit/ba555b22))

# v4.0.0-RC1

> Release & Pull Notes: [v4.0.0-RC1](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0-RC1) 

- build(deps): bump io.mockk:mockk from 1.13.9 to 1.13.10 ([`e3b846ec`](https://github.com/simple-robot/simpler-robot/commit/e3b846ec))
- build(deps): bump org.jetbrains.kotlinx:lincheck from 2.26 to 2.30 ([`443e1f0b`](https://github.com/simple-robot/simpler-robot/commit/443e1f0b))

# v4.0.0-beta3

> Release & Pull Notes: [v4.0.0-beta3](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0-beta3) 


# v4.0.0-beta2

> Release & Pull Notes: [v4.0.0-beta2](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0-beta2) 

- feat(ID): 提供更多与 ID 相关的API ([`72719264`](https://github.com/simple-robot/simpler-robot/commit/72719264))
- fix: 修复ContinuousSession测试问题 ([`3dd75162`](https://github.com/simple-robot/simpler-robot/commit/3dd75162))
- fix: 修复ContinuousSession测试超时问题 ([`6086471d`](https://github.com/simple-robot/simpler-robot/commit/6086471d))
- deps: 更新Kotlin到1.9.23 ([`a93c8d0f`](https://github.com/simple-robot/simpler-robot/commit/a93c8d0f))
- module: 增加一个模块 common-ktor-inputfile 用来提供更简单的向 Ktor 提供表单文件信息的类型 `InputFile` ([`7df76e50`](https://github.com/simple-robot/simpler-robot/commit/7df76e50))
- build(deps): bump dokka from 1.9.10 to 1.9.20 ([`12648620`](https://github.com/simple-robot/simpler-robot/commit/12648620))

# v4.0.0-beta1

> Release & Pull Notes: [v4.0.0-beta1](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0-beta1) 

- build(deps): bump org.jetbrains.kotlinx:lincheck from 2.25 to 2.26 ([`143dd695`](https://github.com/simple-robot/simpler-robot/commit/143dd695))
- refactor: 优化 **持续会话** 模块内部分API、Java友好API和注释,并配置其发布; refactor: 在 common-core 模块中增加与虚拟线程相关的辅助API ([`ddbc6494`](https://github.com/simple-robot/simpler-robot/commit/ddbc6494))
- refactor: 优化/改变 EventProcessor.push 默认实现中的行为：现在会直接使用 flowOn 来指定事件处理器所处的协程上下文 ([`6b67bf98`](https://github.com/simple-robot/simpler-robot/commit/6b67bf98))
- build(deps): bump kotlinx-serialization from 1.6.2 to 1.6.3 ([`dde23bd5`](https://github.com/simple-robot/simpler-robot/commit/dde23bd5))
- build(deps): bump kotlinx-coroutines from 1.8.0-RC2 to 1.8.0 ([`1517823e`](https://github.com/simple-robot/simpler-robot/commit/1517823e))
- refactor: 改善 session 相关API，支持“延后恢复” ([`0d806e3d..613b688c`](https://github.com/simple-robot/simpler-robot/compare/0d806e3d..cc292184))

    <details><summary><code>0d806e3d..613b688c</code></summary>

    - [`0d806e3d`](https://github.com/simple-robot/simpler-robot/commit/0d806e3d)
    - [`613b688c`](https://github.com/simple-robot/simpler-robot/commit/613b688c)

    </details>

- refactor: (WIP) 调整 session 相关API ([`cc292184`](https://github.com/simple-robot/simpler-robot/commit/cc292184))
- refactor: 改善 session 相关API，支持“延后恢复” ([`0c053dc9`](https://github.com/simple-robot/simpler-robot/commit/0c053dc9))
- fix: 修复 `flowCollectable` 错误的返回值类型问题 ([`b6717346`](https://github.com/simple-robot/simpler-robot/commit/b6717346))
- fix: suspend test timeout ([`0dcbd707`](https://github.com/simple-robot/simpler-robot/commit/0dcbd707))
- refactor: (WIP) 调整 session 相关API ([`d819f1dc`](https://github.com/simple-robot/simpler-robot/commit/d819f1dc))
- fix: 修复 `flowCollectable` 错误的返回值类型问题 ([`3d12243f`](https://github.com/simple-robot/simpler-robot/commit/3d12243f))
- fix: Unit test timeout ([`57be7f21..da5c4b9b`](https://github.com/simple-robot/simpler-robot/compare/57be7f21..eaa1bd7e))

    <details><summary><code>57be7f21..da5c4b9b</code></summary>

    - [`57be7f21`](https://github.com/simple-robot/simpler-robot/commit/57be7f21)
    - [`da5c4b9b`](https://github.com/simple-robot/simpler-robot/commit/da5c4b9b)

    </details>

- refactor: 将 suspend-transformer 模块的异步相关API和 `Collectable` 的异步相关API内所有的 `CoroutineScope` 参数默认值调整为 `GlobalScope` 并增加与之相关的部分警告或说明 ([`eaa1bd7e`](https://github.com/simple-robot/simpler-robot/commit/eaa1bd7e))
- fix: 修复 `flowCollectable` 错误的返回值类型问题 ([`4390ca65`](https://github.com/simple-robot/simpler-robot/commit/4390ca65))
- refactor: 将 suspend-transformer 模块的异步相关API和 `Collectable` 的异步相关API内所有的 `CoroutineScope` 参数默认值调整为 `GlobalScope` 并增加与之相关的部分警告或说明 ([`014d6564`](https://github.com/simple-robot/simpler-robot/commit/014d6564))
- feat: 实现持续会话的基本内容 ([`c36b9c47`](https://github.com/simple-robot/simpler-robot/commit/c36b9c47))
- fix: concurrentMap在native上改为使用可重入同步锁实现 ([`7a79552e`](https://github.com/simple-robot/simpler-robot/commit/7a79552e))
- pref: 优化针对v4.0.0-dev16及以下版本的JVM二进制兼容性 ([`2ed0524a`](https://github.com/simple-robot/simpler-robot/commit/2ed0524a))
- test: 调整JVMConfig的test相关配置 ([`b83ab3c0`](https://github.com/simple-robot/simpler-robot/commit/b83ab3c0))
- test: 增加几个使用 lincheck 针对并发相关实现的测试 ([`b52dd18f`](https://github.com/simple-robot/simpler-robot/commit/b52dd18f))
- pref: 为 Services 增加一个可用来区分JVM的扩展 ([`70ccda3d`](https://github.com/simple-robot/simpler-robot/commit/70ccda3d))

# v4.0.0-dev18
> [!warning]
> 这是一个尚在开发中的**预览版**，它可能不稳定，可能会频繁变更，且没有可用性保证。


> Release & Pull Notes: [v4.0.0-dev18](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0-dev18) 

- fix: 优化/修复 ConcurrentMutableMap 在 Js、WasmJs 下会出现 ConcurrentModificationException 的问题，并为 MutableMap 增加一个扩展 API removeValue(key, value) ([`cec18a17`](https://github.com/simple-robot/simpler-robot/commit/cec18a17))
- pref: 为 Image 增加更多可扩展的子类型 ([`dfb50514`](https://github.com/simple-robot/simpler-robot/commit/dfb50514))

# v4.0.0-dev17
> [!warning]
> 这是一个尚在开发中的**预览版**，它可能不稳定，可能会频繁变更，且没有可用性保证。


> Release & Pull Notes: [v4.0.0-dev17](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0-dev17) 

- CI: Upgrade gradle/gradle-build-action@v2 to gradle/gradle-build-action@v3 ([`f93df852`](https://github.com/simple-robot/simpler-robot/commit/f93df852))
- CI: Upgrade actions/checkout@v3 to actions/checkout@v4、actions/setup-java@v3 to actions/setup-java@v4 ([`6dcadb17`](https://github.com/simple-robot/simpler-robot/commit/6dcadb17))
- CI: Update dependabot.yml ([`7ea0e53a`](https://github.com/simple-robot/simpler-robot/commit/7ea0e53a))
- test: 补充部分resource的相关JVM测试 ([`bc4b91c8`](https://github.com/simple-robot/simpler-robot/commit/bc4b91c8))
- pref: 增加/优化部分 Collectable(s) 相关的API、说明等 ([`78d5503f`](https://github.com/simple-robot/simpler-robot/commit/78d5503f))
- pref: 增加/优化部分 Message 相关的API、说明等 ([`7909633e`](https://github.com/simple-robot/simpler-robot/commit/7909633e))

# v4.0.0-dev16
> [!warning]
> 这是一个尚在开发中的**预览版**，它可能不稳定，可能会频繁变更，且没有可用性保证。


> Release & Pull Notes: [v4.0.0-dev16](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0-dev16) 

- build: 暂时关闭 K2 编译，等待 Kt2.0正式版 ([`b1731939`](https://github.com/simple-robot/simpler-robot/commit/b1731939))
- fix: BotManagerFactory 没有实现 PluginFactory 的问题 ([`e4699a30`](https://github.com/simple-robot/simpler-robot/commit/e4699a30))

# v4.0.0-dev15
> [!warning]
> 这是一个尚在开发中的**预览版**，它可能不稳定，可能会频繁变更，且没有可用性保证。


> Release & Pull Notes: [v4.0.0-dev15](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0-dev15) 


# v4.0.0-dev14
> [!warning]
> 这是一个尚在开发中的**预览版**，它可能不稳定，可能会频繁变更，且没有可用性保证。


> Release & Pull Notes: [v4.0.0-dev14](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0-dev14) 

- fix(api): SerializableBotConfiguration 在 JSON下的多态序列化仅外层使用 `"component"` ([`d696921c`](https://github.com/simple-robot/simpler-robot/commit/d696921c))

# v4.0.0-dev13
> [!warning]
> 这是一个尚在开发中的**预览版**，它可能不稳定，可能会频繁变更，且没有可用性保证。


> Release & Pull Notes: [v4.0.0-dev13](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0-dev13) 


# v4.0.0-dev12
> [!warning]
> 这是一个尚在开发中的**预览版**，它可能不稳定，可能会频繁变更，且没有可用性保证。


> Release & Pull Notes: [v4.0.0-dev12](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0-dev12) 


# v4.0.0-dev11
> [!warning]
> 这是一个尚在开发中的**预览版**，它可能不稳定，可能会频繁变更，且没有可用性保证。


> Release & Pull Notes: [v4.0.0-dev11](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0-dev11) 


# v4.0.0-dev10
> [!warning]
> 这是一个尚在开发中的**预览版**，它可能不稳定，可能会频繁变更，且没有可用性保证。


> Release & Pull Notes: [v4.0.0-dev10](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0-dev10) 


# v4.0.0-dev9
> [!warning]
> 这是一个尚在开发中的**预览版**，它可能不稳定，可能会频繁变更，且没有可用性保证。


> Release & Pull Notes: [v4.0.0-dev9](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0-dev9) 


# v4.0.0-dev8
> [!warning]
> 这是一个尚在开发中的**预览版**，它可能不稳定，可能会频繁变更，且没有可用性保证。


> Release & Pull Notes: [v4.0.0-dev8](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0-dev8) 


# v4.0.0-dev7
> [!warning]
> 这是一个尚在开发中的**预览版**，它可能不稳定，可能会频繁变更，且没有可用性保证。


> Release & Pull Notes: [v4.0.0-dev7](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0-dev7) 


> [!warning]
> 这是一个尚在开发中的**预览版**，它可能不稳定，可能会频繁变更，且没有可用性保证。


> Release & Pull Notes: [v4.0.0-dev6](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0-dev6) 


# v4.0.0-dev5
> [!warning]
> 这是一个尚在开发中的**预览版**，它可能不稳定，可能会频繁变更，且没有可用性保证。


> Release & Pull Notes: [v4.0.0-dev5](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0-dev5) 


# v4.0.0-dev4
> [!warning]
> 这是一个尚在开发中的**预览版**，它可能不稳定，可能会频繁变更，且没有可用性保证。


> Release & Pull Notes: [v4.0.0-dev4](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0-dev4) 


# v4.0.0-dev3
> [!warning]
> 这是一个尚在开发中的**预览版**，它可能不稳定，可能会频繁变更，且没有可用性保证。


> Release & Pull Notes: [v4.0.0-dev3](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0-dev3) 


# v4.0.0-dev2
> [!warning]
> 这是一个尚在开发中的**预览版**，它可能不稳定，可能会频繁变更，且没有可用性保证。


> Release & Pull Notes: [v4.0.0-dev2](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0-dev2) 


> [!warning]
> 这是一个尚在开发中的**预览版**，它可能不稳定，可能会频繁变更，且没有可用性保证。


> Release & Pull Notes: [v4.0.0-dev1](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0-dev1) 

- feat: Collectable ([`5d145237`](https://github.com/simple-robot/simpler-robot/commit/5d145237))
- feat: ID、Timestamp and Job link ([`60f8634f`](https://github.com/simple-robot/simpler-robot/commit/60f8634f))
- feat: Timestamp ([`3db7aa8f`](https://github.com/simple-robot/simpler-robot/commit/3db7aa8f))
- feat: (WIP) multiplatform ID ([`5eafba6e..1727015e`](https://github.com/simple-robot/simpler-robot/compare/5eafba6e..v3.3.0))

    <details><summary><code>5eafba6e..1727015e</code></summary>

    - [`5eafba6e`](https://github.com/simple-robot/simpler-robot/commit/5eafba6e)
    - [`3e0919a2`](https://github.com/simple-robot/simpler-robot/commit/3e0919a2)
    - [`1727015e`](https://github.com/simple-robot/simpler-robot/commit/1727015e)

    </details>


# v3.3.0

> Release & Pull Notes: [v3.3.0](https://github.com/simple-robot/simpler-robot/releases/tag/v3.3.0) 


# v3.3.0-beta1

> Release & Pull Notes: [v3.3.0-beta1](https://github.com/simple-robot/simpler-robot/releases/tag/v3.3.0-beta1) 

- CI: snapshot API Doc ([`c70b2541`](https://github.com/simple-robot/simpler-robot/commit/c70b2541))
- build(deps): bump com.github.gmazzo.buildconfig from 4.0.4 to 4.1.2 ([`e4d82d86`](https://github.com/simple-robot/simpler-robot/commit/e4d82d86))
- fix: 尝试修复无法发布快照的问题 ([`d2c79ec9..a0b7e11d`](https://github.com/simple-robot/simpler-robot/compare/d2c79ec9..8b14b21a))

    <details><summary><code>d2c79ec9..a0b7e11d</code></summary>

    - [`d2c79ec9`](https://github.com/simple-robot/simpler-robot/commit/d2c79ec9)
    - [`a0b7e11d`](https://github.com/simple-robot/simpler-robot/commit/a0b7e11d)

    </details>

- fix: 更新dokka到 1.9.0 并修复与 kapt 冲突的问题（[#3153](https://github.com/Kotlin/dokka/issues/3153)） ([`f4cbbc0d..8b14b21a`](https://github.com/simple-robot/simpler-robot/compare/f4cbbc0d..a9725fbc))

    <details><summary><code>f4cbbc0d..8b14b21a</code></summary>

    - [`f4cbbc0d`](https://github.com/simple-robot/simpler-robot/commit/f4cbbc0d)
    - [`8b14b21a`](https://github.com/simple-robot/simpler-robot/commit/8b14b21a)

    </details>

- pref: BlockingRunner 增加对虚拟线程的配置支持 ([`a9725fbc`](https://github.com/simple-robot/simpler-robot/commit/a9725fbc))
- fix: 尝试修复无法发布快照的问题 ([`ab9270a2..648918f7`](https://github.com/simple-robot/simpler-robot/compare/ab9270a2..f22b074e))

    <details><summary><code>ab9270a2..648918f7</code></summary>

    - [`ab9270a2`](https://github.com/simple-robot/simpler-robot/commit/ab9270a2)
    - [`8b1f3cb2`](https://github.com/simple-robot/simpler-robot/commit/8b1f3cb2)
    - [`648918f7`](https://github.com/simple-robot/simpler-robot/commit/648918f7)

    </details>

- fix: hide warn ([`f22b074e`](https://github.com/simple-robot/simpler-robot/commit/f22b074e))
- pref: suspend blocking runner 调整实现，增加对虚拟线程的部分支持 ([`f0802f3b..d983c05f`](https://github.com/simple-robot/simpler-robot/compare/f0802f3b..69a818da))

    <details><summary><code>f0802f3b..d983c05f</code></summary>

    - [`f0802f3b`](https://github.com/simple-robot/simpler-robot/commit/f0802f3b)
    - [`d983c05f`](https://github.com/simple-robot/simpler-robot/commit/d983c05f)

    </details>

- fix: 改善部分代码到kt1.9 ([`69a818da`](https://github.com/simple-robot/simpler-robot/commit/69a818da))
- pref: suspend blocking runner 调整实现，移除对 `synchronized` 的使用并更换为 CompletableFuture 的内部实现 ([`57c29746`](https://github.com/simple-robot/simpler-robot/commit/57c29746))
- build: 更新 Kotlinx Coroutines 到 v1.7.3 ([`968ecbb9`](https://github.com/simple-robot/simpler-robot/commit/968ecbb9))
- build: 更新 Kotlinx Serialization 到 v1.6.0 ([`83ae4a60`](https://github.com/simple-robot/simpler-robot/commit/83ae4a60))
- fix: 更新部分过时代码 ([`fa224fd1`](https://github.com/simple-robot/simpler-robot/commit/fa224fd1))
- build: 更新CI中gradle版本到 8.3; upgrade yarn.lock ([`7d806665`](https://github.com/simple-robot/simpler-robot/commit/7d806665))
- build: 更新Kotlin到 1.9.10; 优化针对Java的异步桥接函数的内部实现 ([`a8298f8b`](https://github.com/simple-robot/simpler-robot/commit/a8298f8b))
- WIP: Upgrade kotlin to 1.9.0 ([`bfccf89f..33a95929`](https://github.com/simple-robot/simpler-robot/compare/bfccf89f..7bc425f5))

    <details><summary><code>bfccf89f..33a95929</code></summary>

    - [`bfccf89f`](https://github.com/simple-robot/simpler-robot/commit/bfccf89f)
    - [`33a95929`](https://github.com/simple-robot/simpler-robot/commit/33a95929)

    </details>

- build: README and Qodana CI config ([`7bc425f5`](https://github.com/simple-robot/simpler-robot/commit/7bc425f5))
- fix: const name ([`62684f76`](https://github.com/simple-robot/simpler-robot/commit/62684f76))
- fix: 一些不应该是警告的警告 ([`edf3ed00..e53ab095`](https://github.com/simple-robot/simpler-robot/compare/edf3ed00..3b295b64))

    <details><summary><code>edf3ed00..e53ab095</code></summary>

    - [`edf3ed00`](https://github.com/simple-robot/simpler-robot/commit/edf3ed00)
    - [`e53ab095`](https://github.com/simple-robot/simpler-robot/commit/e53ab095)

    </details>

- fix: Condition 'cause != null' is always true ([`3b295b64`](https://github.com/simple-robot/simpler-robot/commit/3b295b64))
- fix: Logger more arguments provided ([`58446e97`](https://github.com/simple-robot/simpler-robot/commit/58446e97))
- fix: Cannot resolve symbol 'Survivable' ([`c55d05aa`](https://github.com/simple-robot/simpler-robot/commit/c55d05aa))
- fix: Value of 'instance' os always null ([`e637775e`](https://github.com/simple-robot/simpler-robot/commit/e637775e))
- fix: Redundant empty initializer block ([`2053b967`](https://github.com/simple-robot/simpler-robot/commit/2053b967))
- fix: Declaration has type inferred a platform call ([`faca1c5b`](https://github.com/simple-robot/simpler-robot/commit/faca1c5b))
- fix: Cannot resolve symbol 'Bot' ([`67f6f3cf`](https://github.com/simple-robot/simpler-robot/commit/67f6f3cf))
- fix: Cannot resolve symbol 'Survivable' ([`a6cc0e90`](https://github.com/simple-robot/simpler-robot/commit/a6cc0e90))
- fix: Cannot resolve symbol 'Preparator' ([`19099189`](https://github.com/simple-robot/simpler-robot/commit/19099189))
- fix: Cannot resolve symbol 'asKeywordMatcher' ([`95e0704e`](https://github.com/simple-robot/simpler-robot/commit/95e0704e))
- fix: Recursive property accessor ([`927a187e`](https://github.com/simple-robot/simpler-robot/commit/927a187e))
- fix: String template as argument to 'debug()' logging call ([`8518b105`](https://github.com/simple-robot/simpler-robot/commit/8518b105))
- fix: Could not autowire bean 'ApplicationArguments' critical ([`722c2faf`](https://github.com/simple-robot/simpler-robot/commit/722c2faf))
- build(deps): bump love.forte.simbot.component:simbot-component-mirai-core ([`0e69f5c9`](https://github.com/simple-robot/simpler-robot/commit/0e69f5c9))


> Release & Pull Notes: [v3.2.0](https://github.com/simple-robot/simpler-robot/releases/tag/v3.2.0) 

- Release: v3.2.0 ([`875eff03`](https://github.com/simple-robot/simpler-robot/commit/875eff03))
- fix(util): 修复异步调度器的Job会因异常而被关闭的问题 ([`d6784de8`](https://github.com/simple-robot/simpler-robot/commit/d6784de8))
- feat(boot): 支持在配置文件读取的时候使用 SerializersModule ([`c7e0208c`](https://github.com/simple-robot/simpler-robot/commit/c7e0208c))
- build: version to 3.2.0 ([`bedc962e`](https://github.com/simple-robot/simpler-robot/commit/bedc962e))
- build(deps): bump org.jetbrains.kotlinx:lincheck from 2.17 to 2.19 ([`54f66a13`](https://github.com/simple-robot/simpler-robot/commit/54f66a13))
- build(deps): bump dokkaPluginVersion from 1.8.10 to 1.8.20 ([`c63ad2e0`](https://github.com/simple-robot/simpler-robot/commit/c63ad2e0))
- build(deps): bump ktor from 2.3.0 to 2.3.1 ([`fd818cf3`](https://github.com/simple-robot/simpler-robot/commit/fd818cf3))
- fix(spring-boot): 在SpringBoot中支持 BotAutoRegistrationFailurePolicy ([`5316f1bf..c8b3769b`](https://github.com/simple-robot/simpler-robot/compare/5316f1bf..v3.1.0))

    <details><summary><code>5316f1bf..c8b3769b</code></summary>

    - [`5316f1bf`](https://github.com/simple-robot/simpler-robot/commit/5316f1bf)
    - [`c8b3769b`](https://github.com/simple-robot/simpler-robot/commit/c8b3769b)

    </details>

# v3.1.0

> Release & Pull Notes: [v3.1.0](https://github.com/simple-robot/simpler-robot/releases/tag/v3.1.0) 

- build(deps): bump com.charleskorn.kaml:kaml from 0.53.0 to 0.54.0 ([`035b7812`](https://github.com/simple-robot/simpler-robot/commit/035b7812))
- fix(boot): BotRegistrationFailurePolicy 支持 Spring Boot 配置 ([`4ab38750`](https://github.com/simple-robot/simpler-robot/commit/4ab38750))
- feat(boot): 支持对自动加载bot过程中出现的异常进行策略配置 ([`66e7f392`](https://github.com/simple-robot/simpler-robot/commit/66e7f392))
- build: upgrade version ([`568537c7`](https://github.com/simple-robot/simpler-robot/commit/568537c7))
- feat(api): 提供 ID 和 Timestamp 的属性委托API并完善文档 ([`80852ac8`](https://github.com/simple-robot/simpler-robot/commit/80852ac8))
- feat(api): 实现有关 Timestamp 的委托API 和部分 ID 的委托API ([`2f306ac8`](https://github.com/simple-robot/simpler-robot/commit/2f306ac8))
- feat(api): Timestamp 新增 Delegate API ([`cd52a211`](https://github.com/simple-robot/simpler-robot/commit/cd52a211))
- pref(api): Application在使用 `joinBlocking` 时不再输出 timeout debug ([`9c48bf2a`](https://github.com/simple-robot/simpler-robot/commit/9c48bf2a))
- feat(api): 为 BotManagers 增加部分Java友好的API: getFirst(Class), getFirstOrNull(Class) ([`4e1f3b28`](https://github.com/simple-robot/simpler-robot/commit/4e1f3b28))
- pref(api): 增加对迷惑的ID类型的警告注解与部分说明 ([`ec0c9739`](https://github.com/simple-robot/simpler-robot/commit/ec0c9739))
- feat(api): 支持两个无符号整型的ID类型 `UIntID` 和 `ULongID` ([`41f67929`](https://github.com/simple-robot/simpler-robot/commit/41f67929))
- feat(api): Timestamp 新增 Delegate API ([`52e1eb54`](https://github.com/simple-robot/simpler-robot/commit/52e1eb54))
- pref(api): Application在使用 `joinBlocking` 时不再输出 timeout debug ([`6d6493d3`](https://github.com/simple-robot/simpler-robot/commit/6d6493d3))
- feat(api): 为 BotManagers 增加部分Java友好的API: getFirst(Class), getFirstOrNull(Class) ([`3526cb29`](https://github.com/simple-robot/simpler-robot/commit/3526cb29))
- build(deps): bump spring-boot from 2.7.11 to 2.7.12 ([`fd4febeb`](https://github.com/simple-robot/simpler-robot/commit/fd4febeb))
- pref(api): 增加对迷惑的ID类型的警告注解与部分说明 ([`e5566eff`](https://github.com/simple-robot/simpler-robot/commit/e5566eff))
- feat(api): 支持两个无符号整型的ID类型 `UIntID` 和 `ULongID` ([`ce822898`](https://github.com/simple-robot/simpler-robot/commit/ce822898))
- fix: 使生成的 xxxAsync 函数会正确的使用当前类作为 CoroutineScope (如果可以的话) ([`47ea2ac1`](https://github.com/simple-robot/simpler-robot/commit/47ea2ac1))
- build(deps): bump kotlinx-serialization from 1.5.0 to 1.5.1 ([`0591ede3`](https://github.com/simple-robot/simpler-robot/commit/0591ede3))
- fix: 使生成的 xxxAsync 函数会正确的使用当前类作为 CoroutineScope (如果可以的话) ([`060ad2b5`](https://github.com/simple-robot/simpler-robot/commit/060ad2b5))
- build(deps): bump kotlinx-coroutines from 1.7.0 to 1.7.1 ([`e22b4d7b`](https://github.com/simple-robot/simpler-robot/commit/e22b4d7b))
- build(deps): bump gradleCommon from 0.1.0 to 0.1.1 ([`6b5830a4`](https://github.com/simple-robot/simpler-robot/commit/6b5830a4))
- build(deps): bump gradleCommon from 0.0.11 to 0.1.0 ([`c628b2ee`](https://github.com/simple-robot/simpler-robot/commit/c628b2ee))
- feat(api): 支持两个无符号整型的ID类型 `UIntID` 和 `ULongID` ([`a792859e`](https://github.com/simple-robot/simpler-robot/commit/a792859e))

# v3.0.0

> Release & Pull Notes: [v3.0.0](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0) 

- feat(logger): 更新 slf4j-api 的版本并改进 simbot-logger-slf4j-impl 内的实现 ([`225911ac`](https://github.com/simple-robot/simpler-robot/commit/225911ac))
- build(deps): 尝试更新 slf4j-api 到 v2.0.7 ([`0a2d7fb0..f7b60924`](https://github.com/simple-robot/simpler-robot/compare/0a2d7fb0..4eda2618))

    <details><summary><code>0a2d7fb0..f7b60924</code></summary>

    - [`0a2d7fb0`](https://github.com/simple-robot/simpler-robot/commit/0a2d7fb0)
    - [`f7b60924`](https://github.com/simple-robot/simpler-robot/commit/f7b60924)

    </details>

- build(deps): bump spring-boot from 2.7.10 to 2.7.11 ([`4eda2618`](https://github.com/simple-robot/simpler-robot/commit/4eda2618))
- build(dept): 更新kotlinx.coroutines到v1.7.0 ([`8d40add7`](https://github.com/simple-robot/simpler-robot/commit/8d40add7))
- build(dept): 更新Kotlin到v1.8.21 ([`4e7954d6..71bd6abc`](https://github.com/simple-robot/simpler-robot/compare/4e7954d6..eb7050f3))

    <details><summary><code>4e7954d6..71bd6abc</code></summary>

    - [`4e7954d6`](https://github.com/simple-robot/simpler-robot/commit/4e7954d6)
    - [`71bd6abc`](https://github.com/simple-robot/simpler-robot/commit/71bd6abc)

    </details>

- build(deps): bump com.github.gmazzo.buildconfig from 3.1.0 to 4.0.4 ([`eb7050f3`](https://github.com/simple-robot/simpler-robot/commit/eb7050f3))
- fix: Module readme ([`30b44eb7`](https://github.com/simple-robot/simpler-robot/commit/30b44eb7))
- fix: 项目配置 ([`76170e46`](https://github.com/simple-robot/simpler-robot/commit/76170e46))
- build: 调整项目结构，独立部分注解和suspend转化函数为独立模块 ([`613c21ba`](https://github.com/simple-robot/simpler-robot/commit/613c21ba))
- build: 调整 changelog 生成 ([`d4796b53`](https://github.com/simple-robot/simpler-robot/commit/d4796b53))
- build(deps): bump ktor from 2.2.4 to 2.3.0 ([`16274c80`](https://github.com/simple-robot/simpler-robot/commit/16274c80))
- build(deps): bump org.jetbrains.kotlinx:lincheck from 2.16 to 2.17 ([`9e94c361`](https://github.com/simple-robot/simpler-robot/commit/9e94c361))
- pref: SocialRelationsContainer 子类型的新语义 ([`38cbf931`](https://github.com/simple-robot/simpler-robot/commit/38cbf931))
- feat(stage-loop): 新的简单状态机实现 ([`a544d080`](https://github.com/simple-robot/simpler-robot/commit/a544d080))
- build(deps): bump spring-boot from 2.7.6 to 2.7.10 ([`3856b7dc`](https://github.com/simple-robot/simpler-robot/commit/3856b7dc))
- build(deps): bump org.slf4j:slf4j-nop from 1.7.36 to 2.0.7 ([`752c0d07`](https://github.com/simple-robot/simpler-robot/commit/752c0d07))
- build(deps): bump com.charleskorn.kaml:kaml from 0.49.0 to 0.53.0 ([`59828bc9`](https://github.com/simple-robot/simpler-robot/commit/59828bc9))
- build(deps): bump openjdk-jmh from 1.35 to 1.36 ([`12d85397`](https://github.com/simple-robot/simpler-robot/commit/12d85397))
- build(deps): bump love.forte.plugin.suspend-transform:suspend-transform-plugin-gradle ([`3373e573`](https://github.com/simple-robot/simpler-robot/commit/3373e573))
- build(deps): bump ktor from 2.1.1 to 2.2.4 ([`7c3bafb0`](https://github.com/simple-robot/simpler-robot/commit/7c3bafb0))
- build: 更新Kotlin版本到 v1.8.10 ([`12273af7`](https://github.com/simple-robot/simpler-robot/commit/12273af7))
- build(deps): bump org.jetbrains:annotations from 23.0.0 to 24.0.1 ([`0cb88528`](https://github.com/simple-robot/simpler-robot/commit/0cb88528))
- build(deps): bump kotlinx-serialization from 1.5.0-RC to 1.5.0 ([`92b7e305`](https://github.com/simple-robot/simpler-robot/commit/92b7e305))
- build(deps): bump org.jetbrains.kotlinx:lincheck from 2.15 to 2.16 ([`8b887b0a`](https://github.com/simple-robot/simpler-robot/commit/8b887b0a))
- build(deps): bump io.github.gradle-nexus:publish-plugin ([`09d85d18`](https://github.com/simple-robot/simpler-robot/commit/09d85d18))
- build(deps): bump dokkaPluginVersion from 1.7.20 to 1.8.10 ([`916b53aa`](https://github.com/simple-robot/simpler-robot/commit/916b53aa))
- build: 修复版本 ([`374d3348`](https://github.com/simple-robot/simpler-robot/commit/374d3348))
- CONTRIBUTING: CONTRIBUTING.md ([`35b22632`](https://github.com/simple-robot/simpler-robot/commit/35b22632))
- copyright: 更新版权信息 ([`f8ebb3db..360786e4`](https://github.com/simple-robot/simpler-robot/compare/f8ebb3db..315ceb3c))

    <details><summary><code>f8ebb3db..360786e4</code></summary>

    - [`f8ebb3db`](https://github.com/simple-robot/simpler-robot/commit/f8ebb3db)
    - [`19e3cc23`](https://github.com/simple-robot/simpler-robot/commit/19e3cc23)
    - [`c6531286`](https://github.com/simple-robot/simpler-robot/commit/c6531286)
    - [`360786e4`](https://github.com/simple-robot/simpler-robot/commit/360786e4)

    </details>

- build(deps): bump io.github.gradle-nexus:publish-plugin ([`315ceb3c`](https://github.com/simple-robot/simpler-robot/commit/315ceb3c))
- build: website以submodule的形式引用 ([`86e87ef2`](https://github.com/simple-robot/simpler-robot/commit/86e87ef2))
- build: 暂时移除 website 目录 ([`72fdd4c8`](https://github.com/simple-robot/simpler-robot/commit/72fdd4c8))
- build(deps): bump org.springframework:spring-context ([`a80f3eef`](https://github.com/simple-robot/simpler-robot/commit/a80f3eef))
- build(deps): bump org.springframework:spring-core from 5.3.13 to 6.0.5 ([`8f3c7b68`](https://github.com/simple-robot/simpler-robot/commit/8f3c7b68))
- test: stage loop ([`138e6c22..1fd29bab`](https://github.com/simple-robot/simpler-robot/compare/138e6c22..f7b0ff11))

    <details><summary><code>138e6c22..1fd29bab</code></summary>

    - [`138e6c22`](https://github.com/simple-robot/simpler-robot/commit/138e6c22)
    - [`1fd29bab`](https://github.com/simple-robot/simpler-robot/commit/1fd29bab)

    </details>

- build(deps): bump org.springframework:spring-context ([`f7b0ff11`](https://github.com/simple-robot/simpler-robot/commit/f7b0ff11))
- build(deps): bump org.springframework:spring-core from 5.3.13 to 6.0.5 ([`f082c093`](https://github.com/simple-robot/simpler-robot/commit/f082c093))
- test: stage loop ([`be112284..c598c40c`](https://github.com/simple-robot/simpler-robot/compare/be112284..v3.0.0-RC.3))

    <details><summary><code>be112284..c598c40c</code></summary>

    - [`be112284`](https://github.com/simple-robot/simpler-robot/commit/be112284)
    - [`c598c40c`](https://github.com/simple-robot/simpler-robot/commit/c598c40c)

    </details>


# v3.0.0-RC.3

> Release & Pull Notes: [v3.0.0-RC.3](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0-RC.3) 

- fix: build config ([`4cd0bb48..a6b8048d`](https://github.com/simple-robot/simpler-robot/compare/4cd0bb48..27055f7f))

    <details><summary><code>4cd0bb48..a6b8048d</code></summary>

    - [`4cd0bb48`](https://github.com/simple-robot/simpler-robot/commit/4cd0bb48)
    - [`249209ce`](https://github.com/simple-robot/simpler-robot/commit/249209ce)
    - [`5bc57233`](https://github.com/simple-robot/simpler-robot/commit/5bc57233)
    - [`a6b8048d`](https://github.com/simple-robot/simpler-robot/commit/a6b8048d)

    </details>

- build: 迁移 forte-di 和 annotation-tool 到当前仓库 ([`27055f7f`](https://github.com/simple-robot/simpler-robot/commit/27055f7f))
- build: 将 [annotationTool](https://github.com/ForteScarlet/annotation-tool) 迁移到当前仓库内 ([`d8325c7e`](https://github.com/simple-robot/simpler-robot/commit/d8325c7e))
- pref: kdoc deploy ([`a3924b79..efcbbd06`](https://github.com/simple-robot/simpler-robot/compare/a3924b79..b56b6528))

    <details><summary><code>a3924b79..efcbbd06</code></summary>

    - [`a3924b79`](https://github.com/simple-robot/simpler-robot/commit/a3924b79)
    - [`674f8ee7`](https://github.com/simple-robot/simpler-robot/commit/674f8ee7)
    - [`da53248e`](https://github.com/simple-robot/simpler-robot/commit/da53248e)
    - [`de208d0b`](https://github.com/simple-robot/simpler-robot/commit/de208d0b)
    - [`efcbbd06`](https://github.com/simple-robot/simpler-robot/commit/efcbbd06)

    </details>

- build: 调整项目整体目录结构以优化生成的文档效果 ([`b56b6528`](https://github.com/simple-robot/simpler-robot/commit/b56b6528))
- build: 优化部分内容 ([`3c0516dd`](https://github.com/simple-robot/simpler-robot/commit/3c0516dd))

