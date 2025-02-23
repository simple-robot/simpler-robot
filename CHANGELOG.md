# CHANGELOG
> 由自动任务基于Git提交记录生成，详细更新内容请参考对应版本的 release 。

## v4.11.0

> Release & Pull Notes: [v4.11.0](https://github.com/simple-robot/simpler-robot/releases/tag/v4.11.0)
>
> Commit compare: [v4.10.0..v4.11.0](https://github.com/simple-robot/simpler-robot/compare/v4.10.0..v4.11.0)

- [`5f616b5bf`](https://github.com/simple-robot/simpler-robot/commit/5f616b5bf): 改善changelog相关task实现，移除不再使用的 .changelog 目录，更新CHANGELOG
- [`57013f53d`](https://github.com/simple-robot/simpler-robot/commit/57013f53d): Add FuzzyEventTypeImplementation opt-in annotation for event type clarity
- [`4ce3a069e`](https://github.com/simple-robot/simpler-robot/commit/4ce3a069e): Update README.md
- [`907bc6ccf`](https://github.com/simple-robot/simpler-robot/commit/907bc6ccf): 补充部分注释
- [`45e895402`](https://github.com/simple-robot/simpler-robot/commit/45e895402): 简单调整贡献指南和README
- [`fd0c7c0cd`](https://github.com/simple-robot/simpler-robot/commit/fd0c7c0cd): Add ReplySupportInteractionEvent and related events
- [`e4a881378`](https://github.com/simple-robot/simpler-robot/commit/e4a881378): build(deps): bump io.gitlab.arturbosch.detekt from 1.23.7 to 1.23.8
- [`9bb51ea12`](https://github.com/simple-robot/simpler-robot/commit/9bb51ea12): InternalEvent: SendSupportInteractionEvent
- [`7992dee23`](https://github.com/simple-robot/simpler-robot/commit/7992dee23): 为OfflineImage增加 subclass opt-in 标记，并为几个未来弃用的 resolver 类型增加 opt 标记
- [`b22a8ab52`](https://github.com/simple-robot/simpler-robot/commit/b22a8ab52): Improve some DSL marker annotations
- [`1c2f4195d`](https://github.com/simple-robot/simpler-robot/commit/1c2f4195d): 简单调整 simbot-common-annotation 的部分依赖引用方式
- [`b2199eb02`](https://github.com/simple-robot/simpler-robot/commit/b2199eb02): refactor(resource): add subclass opt-in annotation for Resource interfaces
- [`7b7f0242a`](https://github.com/simple-robot/simpler-robot/commit/7b7f0242a): Class builder processor with Java11
- [`310c6fd80`](https://github.com/simple-robot/simpler-robot/commit/310c6fd80): build(deps): bump org.jetbrains.kotlinx:lincheck from 2.35 to 2.36
- [`ec83d1a4b`](https://github.com/simple-robot/simpler-robot/commit/ec83d1a4b): Builder generator class builder
- [`9ab54c579`](https://github.com/simple-robot/simpler-robot/commit/9ab54c579): Builder generator
- [`29d779a93`](https://github.com/simple-robot/simpler-robot/commit/29d779a93): 基于构造参数的parameters解析
- [`1851d6397`](https://github.com/simple-robot/simpler-robot/commit/1851d6397): Builder Generator
- [`29547b540`](https://github.com/simple-robot/simpler-robot/commit/29547b540): class builder processor
- [`322e5bea7`](https://github.com/simple-robot/simpler-robot/commit/322e5bea7): publish API Doc use ubuntu OS
- [`c5505af04..b1360d99e`](https://github.com/simple-robot/simpler-robot/compare/c5505af04..322e5bea7): Fix configs and CI
- [`ae35ae4ac`](https://github.com/simple-robot/simpler-robot/commit/ae35ae4ac): Update Dokka to 2.0.0
- [`560aa377f`](https://github.com/simple-robot/simpler-robot/commit/560aa377f): build(deps): bump org.jetbrains.kotlinx:lincheck from 2.34 to 2.35
- [`bf5fb92a3`](https://github.com/simple-robot/simpler-robot/commit/bf5fb92a3): build(deps): bump io.projectreactor:reactor-core from 3.7.1 to 3.7.3
- [`d0561ae67`](https://github.com/simple-robot/simpler-robot/commit/d0561ae67): build(deps): bump io.projectreactor:reactor-test from 3.7.1 to 3.7.3
- [`7ed6ee5fa`](https://github.com/simple-robot/simpler-robot/commit/7ed6ee5fa): 更新编译器插件和部分Gradle插件版本，修复 BlockingRunner 中用于编译器插件合成的函数的参数错误
- [`fc593bde3`](https://github.com/simple-robot/simpler-robot/commit/fc593bde3): 更新API dump
- [`6d79a428b`](https://github.com/simple-robot/simpler-robot/commit/6d79a428b): build(deps): bump org.jetbrains:annotations from 26.0.1 to 26.0.2
- [`bcbb1e27b`](https://github.com/simple-robot/simpler-robot/commit/bcbb1e27b): 尝试更新编译器插件到 *-0.10.1
- [`400f647a8`](https://github.com/simple-robot/simpler-robot/commit/400f647a8): 尝试更新编译器插件到 *-0.10.0 但是 MessageReceipt.deleteAll 编译不过-没有BODY，且只有它编译不过？
- [`e161ecb34`](https://github.com/simple-robot/simpler-robot/commit/e161ecb34): build(deps): bump io.mockk:mockk from 1.13.14 to 1.13.16
- [`10ab850cd`](https://github.com/simple-robot/simpler-robot/commit/10ab850cd): build(deps): bump org.jetbrains.kotlinx.kover from 0.9.0 to 0.9.1

## v4.10.0

> Release & Pull Notes: [v4.10.0](https://github.com/simple-robot/simpler-robot/releases/tag/v4.10.0)
>
> Commit compare: [v4.9.0..v4.10.0](https://github.com/simple-robot/simpler-robot/compare/v4.9.0..v4.10.0)

- [`66d34de5b`](https://github.com/simple-robot/simpler-robot/commit/66d34de5b): feat: 简化ContentTrimEventInterceptorFactory.create实现
- [`0d9e28759`](https://github.com/simple-robot/simpler-robot/commit/0d9e28759): Fix tests (remove some tests)
- [`de36fc64b`](https://github.com/simple-robot/simpler-robot/commit/de36fc64b): 调整部分Resource API
- [`3be741342`](https://github.com/simple-robot/simpler-robot/commit/3be741342): build(deps): bump io.mockk:mockk from 1.13.13 to 1.13.14
- [`e56f51ef1`](https://github.com/simple-robot/simpler-robot/commit/e56f51ef1): build(deps): bump io.spring.dependency-management from 1.1.5 to 1.1.7
- [`9b281b5c7`](https://github.com/simple-robot/simpler-robot/commit/9b281b5c7): build(deps): bump org.jetbrains.kotlinx.kover from 0.8.3 to 0.9.0
- [`fed5311fa..1e697fe6d`](https://github.com/simple-robot/simpler-robot/compare/fed5311fa..9b281b5c7): 基于KotlinxIO改善Resource[部分]
- [`fb701496a`](https://github.com/simple-robot/simpler-robot/commit/fb701496a): Next version: 4.10.0
- [`6309e9a82`](https://github.com/simple-robot/simpler-robot/commit/6309e9a82): 基于KotlinxIO改善Resource[部分]
- [`ae5193028`](https://github.com/simple-robot/simpler-robot/commit/ae5193028): build(deps): bump io.projectreactor:reactor-core from 3.7.0 to 3.7.1
- [`1849abdbe`](https://github.com/simple-robot/simpler-robot/commit/1849abdbe): build(deps): bump io.projectreactor:reactor-test from 3.7.0 to 3.7.1
- [`32a0e3263`](https://github.com/simple-robot/simpler-robot/commit/32a0e3263): 基于KotlinxIO改善Resource[部分]
- [`66e42a099`](https://github.com/simple-robot/simpler-robot/commit/66e42a099): Next version: 4.10.0
- [`22728471d`](https://github.com/simple-robot/simpler-robot/commit/22728471d): checkout for v4
- [`e4e447fba..d7b6f73c2`](https://github.com/simple-robot/simpler-robot/compare/e4e447fba..22728471d): Qodana CI?
- [`10e15c4ca..c2f9f1487`](https://github.com/simple-robot/simpler-robot/compare/10e15c4ca..d7b6f73c2): setup gradle?
- [`f70c90bbd`](https://github.com/simple-robot/simpler-robot/commit/f70c90bbd): checkout@v3?
- [`da49775eb..4a8b8e302`](https://github.com/simple-robot/simpler-robot/compare/da49775eb..f70c90bbd): Edit qodana ci config

## v4.9.0

> Release & Pull Notes: [v4.9.0](https://github.com/simple-robot/simpler-robot/releases/tag/v4.9.0)
>
> Commit compare: [v4.8.0..v4.9.0](https://github.com/simple-robot/simpler-robot/compare/v4.8.0..v4.9.0)

- [`2fe778c86`](https://github.com/simple-robot/simpler-robot/commit/2fe778c86): Update yarn
- [`0263e0dbf..9af38086d`](https://github.com/simple-robot/simpler-robot/compare/0263e0dbf..2fe778c86): Update Kotlin to 2.1.0; Update Gradle to 8.6; Update kcp: KSP, SuspendTransform;
- [`b859a4209`](https://github.com/simple-robot/simpler-robot/commit/b859a4209): build(deps): bump org.gradle.toolchains.foojay-resolver-convention

## v4.8.0

> Release & Pull Notes: [v4.8.0](https://github.com/simple-robot/simpler-robot/releases/tag/v4.8.0)
>
> Commit compare: [v4.7.0..v4.8.0](https://github.com/simple-robot/simpler-robot/compare/v4.7.0..v4.8.0)

- [`5818b3b48`](https://github.com/simple-robot/simpler-robot/commit/5818b3b48): fix module-info.java
- [`4e25ff0a9`](https://github.com/simple-robot/simpler-robot/commit/4e25ff0a9): deps: Update kotlinx-io to 0.6.0
- [`e0509acf9`](https://github.com/simple-robot/simpler-robot/commit/e0509acf9): build(deps): bump com.squareup:kotlinpoet-ksp from 1.18.1 to 2.0.0
- [`0659caac4`](https://github.com/simple-robot/simpler-robot/commit/0659caac4): build(deps): bump io.projectreactor:reactor-core from 3.6.11 to 3.7.0
- [`fbfe0fb81`](https://github.com/simple-robot/simpler-robot/commit/fbfe0fb81): build(deps): bump io.projectreactor:reactor-test from 3.6.11 to 3.7.0
- [`32f7d5e68`](https://github.com/simple-robot/simpler-robot/commit/32f7d5e68): build(deps): bump love.forte.plugin.suspend-transform:suspend-transform-plugin-gradle
- [`8e64fea97`](https://github.com/simple-robot/simpler-robot/commit/8e64fea97): build(deps): bump com.github.gmazzo.buildconfig from 5.5.0 to 5.5.1
- [`e85d61f0f`](https://github.com/simple-robot/simpler-robot/commit/e85d61f0f): Dump API

## v4.7.0

> Release & Pull Notes: [v4.7.0](https://github.com/simple-robot/simpler-robot/releases/tag/v4.7.0)
>
> Commit compare: [v4.7.0-beta1..v4.7.0](https://github.com/simple-robot/simpler-robot/compare/v4.7.0-beta1..v4.7.0)

- [`596bd6b4e`](https://github.com/simple-robot/simpler-robot/commit/596bd6b4e): optimize: 优化改善与 SourceResource 相关的API
- [`d00da05bc`](https://github.com/simple-robot/simpler-robot/commit/d00da05bc): build(deps): bump io.projectreactor:reactor-core from 3.6.10 to 3.6.11
- [`8585ca9cc`](https://github.com/simple-robot/simpler-robot/commit/8585ca9cc): build(deps): bump io.projectreactor:reactor-test from 3.6.10 to 3.6.11

## v4.7.0-beta1

> Release & Pull Notes: [v4.7.0-beta1](https://github.com/simple-robot/simpler-robot/releases/tag/v4.7.0-beta1)
>
> Commit compare: [v4.6.1..v4.7.0-beta1](https://github.com/simple-robot/simpler-robot/compare/v4.6.1..v4.7.0-beta1)

- [`9dbb7efee`](https://github.com/simple-robot/simpler-robot/commit/9dbb7efee): optimize: 优化改善与 SourceResource 相关的API
- [`dc811e0f3`](https://github.com/simple-robot/simpler-robot/commit/dc811e0f3): 修改CI，不在需要每次都手动 createChangelog
- [`3ac0dd517`](https://github.com/simple-robot/simpler-robot/commit/3ac0dd517): build(deps): bump org.jetbrains:annotations from 26.0.0 to 26.0.1
- [`b94fb31e6`](https://github.com/simple-robot/simpler-robot/commit/b94fb31e6): Qodana with an actual version v2024.2
- [`dc96be2a5`](https://github.com/simple-robot/simpler-robot/commit/dc96be2a5): feat(api): 增加部分基于文件系统的 Resource、Image API支持
- [`3048ad140`](https://github.com/simple-robot/simpler-robot/commit/3048ad140): build(deps): Apply kotlinx-io for simbot-api

## v4.6.1

> Release & Pull Notes: [v4.6.1](https://github.com/simple-robot/simpler-robot/releases/tag/v4.6.1)
>
> Commit compare: [v4.6.0..v4.6.1](https://github.com/simple-robot/simpler-robot/compare/v4.6.0..v4.6.1)

- [`7f859de66`](https://github.com/simple-robot/simpler-robot/commit/7f859de66): feat(common-core): 增加 UUID 对 `kotlin.uuid.Uuid` 的兼容API
- [`56f5856a5`](https://github.com/simple-robot/simpler-robot/commit/56f5856a5): apply detekt for a test file
- [`a4b9f6a2a`](https://github.com/simple-robot/simpler-robot/commit/a4b9f6a2a): build(deps): bump kotlinx-serialization from 1.7.1 to 1.7.3
- [`6f1fb1b1a`](https://github.com/simple-robot/simpler-robot/commit/6f1fb1b1a): build(deps): bump io.mockk:mockk from 1.13.12 to 1.13.13
- [`389ed9fc2`](https://github.com/simple-robot/simpler-robot/commit/389ed9fc2): build(deps): bump org.jetbrains:annotations from 25.0.0 to 26.0.0
- [`50b57d674`](https://github.com/simple-robot/simpler-robot/commit/50b57d674): bump(deps): Jetbrains annotation from 24.1.0 to 25.0.0
- [`af72957f0..3d0773e94`](https://github.com/simple-robot/simpler-robot/compare/af72957f0..50b57d674): bump(deps): Kotlin from 2.0.10 to 2.0.20
- [`99ef8eb36`](https://github.com/simple-robot/simpler-robot/commit/99ef8eb36): 感谢Jetbrains的授权！并更新它们的Logo到[新的](https://www.jetbrains.com/company/brand/#logos-and-icons)
- [`c40efb8e0`](https://github.com/simple-robot/simpler-robot/commit/c40efb8e0): build(deps): bump com.github.gmazzo.buildconfig from 5.4.0 to 5.5.0
- [`55e62299d`](https://github.com/simple-robot/simpler-robot/commit/55e62299d): build(deps): bump kotlinx-coroutines from 1.8.1 to 1.9.0
- [`e592fb05f`](https://github.com/simple-robot/simpler-robot/commit/e592fb05f): Update website
- [`0835dafe7`](https://github.com/simple-robot/simpler-robot/commit/0835dafe7): build(deps): bump io.projectreactor:reactor-core from 3.6.9 to 3.6.10
- [`130f3cefc`](https://github.com/simple-robot/simpler-robot/commit/130f3cefc): build(deps): bump io.projectreactor:reactor-test from 3.6.9 to 3.6.10
- [`d0a714465`](https://github.com/simple-robot/simpler-robot/commit/d0a714465): build(deps): bump io.gitlab.arturbosch.detekt from 1.23.6 to 1.23.7
- [`97bfb334a`](https://github.com/simple-robot/simpler-robot/commit/97bfb334a): build(deps): bump org.jetbrains.kotlinx:lincheck from 2.33 to 2.34
- [`0c0c49f24`](https://github.com/simple-robot/simpler-robot/commit/0c0c49f24): fix build
- [`8c59aa213`](https://github.com/simple-robot/simpler-robot/commit/8c59aa213): test for SerializableBotConfiguration
- [`428b43bb0`](https://github.com/simple-robot/simpler-robot/commit/428b43bb0): Update qodana_code_quality.yml
- [`a1d2e6e24`](https://github.com/simple-robot/simpler-robot/commit/a1d2e6e24): Add Date class for WasmJSMain: Represents epoch time in milliseconds for JS env. Provides `getTime` method for UTC timestamp since 1970-01-01. Instantiate via platform-specific methods.
- [`a68db142c`](https://github.com/simple-robot/simpler-robot/commit/a68db142c): 简单调整JS平台的配置，仅保留 nodeJs 的测试，并改变 Timestamp.wasmJs 中的 `Date` 的使用方式

## v4.6.0

> Release & Pull Notes: [v4.6.0](https://github.com/simple-robot/simpler-robot/releases/tag/v4.6.0)
>
> Commit compare: [v4.5.0..v4.6.0](https://github.com/simple-robot/simpler-robot/compare/v4.5.0..v4.6.0)

- [`99fd7c6f2`](https://github.com/simple-robot/simpler-robot/commit/99fd7c6f2): build(deps): bump io.projectreactor:reactor-core from 3.6.2 to 3.6.9
- [`615689775`](https://github.com/simple-robot/simpler-robot/commit/615689775): build(deps): bump io.projectreactor:reactor-test from 3.6.2 to 3.6.9
- [`38d1c1639`](https://github.com/simple-robot/simpler-robot/commit/38d1c1639): Apply apiDump
- [`4adc9412a`](https://github.com/simple-robot/simpler-robot/commit/4adc9412a): feat: 新增一个新的组 love.forte.simbot.processor, 以及其中一个新的用于组件开发的ksp处理器 simbot-processor-message-element-polymorphic-include
- [`1d478c4eb`](https://github.com/simple-robot/simpler-robot/commit/1d478c4eb): build(deps): bump slf4j from 2.0.15 to 2.0.16
- [`a2a6108b0`](https://github.com/simple-robot/simpler-robot/commit/a2a6108b0): Apply apiDump
- [`25ba84656`](https://github.com/simple-robot/simpler-robot/commit/25ba84656): feat(api): 为 `Bot` 增加用于根据ID获取源消息的API
- [`9ff7e9473`](https://github.com/simple-robot/simpler-robot/commit/9ff7e9473): feat(api): 为 `MessageContent` 和 `Bot` 增加用于根据引用 `MessageReference` 获取源消息的API
- [`f58c080b2`](https://github.com/simple-robot/simpler-robot/commit/f58c080b2): build(api): 在 simbot-api 中生成携带部分常量信息的 builtin
- [`825e09ab5`](https://github.com/simple-robot/simpler-robot/commit/825e09ab5): Update Kotlin from 2.0.0 to 2.0.10
- [`40dcf0ecf`](https://github.com/simple-robot/simpler-robot/commit/40dcf0ecf): build(deps): bump slf4j from 2.0.13 to 2.0.15
- [`385be898f`](https://github.com/simple-robot/simpler-robot/commit/385be898f): build(deps): bump plugin.spring from 2.0.0 to 2.0.10
- [`e9c81da85`](https://github.com/simple-robot/simpler-robot/commit/e9c81da85): build(deps): bump ksp from 2.0.0-1.0.24 to 2.0.10-1.0.24
- [`d3708123b`](https://github.com/simple-robot/simpler-robot/commit/d3708123b): build(deps): bump org.jetbrains.kotlin.plugin.allopen
- [`25c7b4923`](https://github.com/simple-robot/simpler-robot/commit/25c7b4923): build(deps): bump com.squareup:kotlinpoet-ksp from 1.17.0 to 1.18.1
- [`f218cd18c`](https://github.com/simple-robot/simpler-robot/commit/f218cd18c): build(deps): bump org.jetbrains.kotlinx:lincheck from 2.32 to 2.33
- [`dc8672440`](https://github.com/simple-robot/simpler-robot/commit/dc8672440): build(deps): bump org.jetbrains.kotlinx.binary-compatibility-validator
- [`89a13b76c..44b7dffe6`](https://github.com/simple-robot/simpler-robot/compare/89a13b76c..dc8672440): Update Qodana CI
- [`19d658b83`](https://github.com/simple-robot/simpler-robot/commit/19d658b83): Update dependabot.yml
- [`d7482901a`](https://github.com/simple-robot/simpler-robot/commit/d7482901a): build(deps): bump ksp from 2.0.0-1.0.23 to 2.0.0-1.0.24
- [`32ec83010`](https://github.com/simple-robot/simpler-robot/commit/32ec83010): build(deps): bump com.github.gmazzo.buildconfig from 5.3.5 to 5.4.0
- [`f48cec115`](https://github.com/simple-robot/simpler-robot/commit/f48cec115): Update qodana CI config
- [`bce28e3fd`](https://github.com/simple-robot/simpler-robot/commit/bce28e3fd): build(deps): bump ksp from 2.0.0-1.0.22 to 2.0.0-1.0.23

## v4.5.0

> Release & Pull Notes: [v4.5.0](https://github.com/simple-robot/simpler-robot/releases/tag/v4.5.0)
>
> Commit compare: [v4.4.0..v4.5.0](https://github.com/simple-robot/simpler-robot/compare/v4.4.0..v4.5.0)

- [`23050cd1d`](https://github.com/simple-robot/simpler-robot/commit/23050cd1d): feat(api): ApplicationBuilder.serializersModule 作为后备而不是基底
- [`34cfe9e16`](https://github.com/simple-robot/simpler-robot/commit/34cfe9e16): feat(api): ApplicationBuilder中新增可配置项 `serializersModule` 以允许配置一个自定义的序列化模块'基底'
- [`eb6b98f92`](https://github.com/simple-robot/simpler-robot/commit/eb6b98f92): apiDump
- [`f40018faa`](https://github.com/simple-robot/simpler-robot/commit/f40018faa): feat(api): 增加一个标准消息元素类型 MessageReference; 增加一个API MessageContent.reference()
- [`b05bcb75b`](https://github.com/simple-robot/simpler-robot/commit/b05bcb75b): build(deps): bump org.jetbrains.kotlinx.kover from 0.8.2 to 0.8.3
- [`737d87505`](https://github.com/simple-robot/simpler-robot/commit/737d87505): build(deps): bump io.mockk:mockk from 1.13.11 to 1.13.12

## v4.4.0

> Release & Pull Notes: [v4.4.0](https://github.com/simple-robot/simpler-robot/releases/tag/v4.4.0)
>
> Commit compare: [v4.3.1..v4.4.0](https://github.com/simple-robot/simpler-robot/compare/v4.3.1..v4.4.0)

- [`dd5c1c5ea`](https://github.com/simple-robot/simpler-robot/commit/dd5c1c5ea): 修正单元测试
- [`d741b8617`](https://github.com/simple-robot/simpler-robot/commit/d741b8617): fix(quantcat): 优化统一MergedBinder对null结果、失败结果的处理
- [`5d60f63cb`](https://github.com/simple-robot/simpler-robot/commit/5d60f63cb): update website
- [`2819fc40f..17f799880`](https://github.com/simple-robot/simpler-robot/compare/2819fc40f..5d60f63cb): fix test in CI
- [`1ba898c57`](https://github.com/simple-robot/simpler-robot/commit/1ba898c57): feat(common-streamable): 增加模块与新的公共类型 Streamable, 用来简化部分针对 Sequence 类型的转化操作，例如在JVM中转为 Stream 和在 JS 中转为数组。
- [`9c5777847`](https://github.com/simple-robot/simpler-robot/commit/9c5777847): fix(quantcat): 优化统一MergedBinder对null结果、失败结果的处理
- [`a04aa4871`](https://github.com/simple-robot/simpler-robot/commit/a04aa4871): Update website
- [`282f37c88..e370c3df4`](https://github.com/simple-robot/simpler-robot/compare/282f37c88..a04aa4871): fix test in CI
- [`ca3eaa02b`](https://github.com/simple-robot/simpler-robot/commit/ca3eaa02b): feat(common-streamable): 增加模块与新的公共类型 Streamable, 用来简化部分针对 Sequence 类型的转化操作，例如在JVM中转为 Stream 和在 JS 中转为数组。

## v4.3.1

> Release & Pull Notes: [v4.3.1](https://github.com/simple-robot/simpler-robot/releases/tag/v4.3.1)
>
> Commit compare: [v4.3.0..v4.3.1](https://github.com/simple-robot/simpler-robot/compare/v4.3.0..v4.3.1)

- [`ef31604c7`](https://github.com/simple-robot/simpler-robot/commit/ef31604c7): fix(quantcat): 修复使用KeywordBinder时无法正确处理参数类型的问题
- [`628c1fe0c`](https://github.com/simple-robot/simpler-robot/commit/628c1fe0c): optimize(api): 优化、调整MessagesBuilder实现结构
- [`28ed531e1`](https://github.com/simple-robot/simpler-robot/commit/28ed531e1): build(deps): bump ktor from 2.3.11 to 2.3.12
- [`be15aaef5`](https://github.com/simple-robot/simpler-robot/commit/be15aaef5): build(deps): bump kotlinx-serialization from 1.6.3 to 1.7.1
- [`ad2f21260..21bd244df`](https://github.com/simple-robot/simpler-robot/compare/ad2f21260..be15aaef5): Kdoc: homepage
- [`7fe46a0ba`](https://github.com/simple-robot/simpler-robot/commit/7fe46a0ba): 优化 KDoc; 增加gtag;

## v4.3.0

> Release & Pull Notes: [v4.3.0](https://github.com/simple-robot/simpler-robot/releases/tag/v4.3.0)
>
> Commit compare: [v4.2.0..v4.3.0](https://github.com/simple-robot/simpler-robot/compare/v4.2.0..v4.3.0)

- [`fcd2fd670..eceb41528`](https://github.com/simple-robot/simpler-robot/compare/fcd2fd670..HEAD): feat(api): 增加接口 RichMediaMessage 用以描述一个富媒体消息元素
- [`a032aa138`](https://github.com/simple-robot/simpler-robot/commit/a032aa138): fix test and build config
- [`6ec2a1a53..c38c80270`](https://github.com/simple-robot/simpler-robot/compare/6ec2a1a53..a032aa138): feat(api): 增加接口 RichMediaMessage 用以描述一个富媒体消息元素
- [`2f121a8b3`](https://github.com/simple-robot/simpler-robot/commit/2f121a8b3): Dump API
- [`7e52e17bc`](https://github.com/simple-robot/simpler-robot/commit/7e52e17bc): feat(api): 增加接口 BinaryDataAwareMessage 用以描述能够获取到二进制数据的消息元素
- [`933095e9d`](https://github.com/simple-robot/simpler-robot/commit/933095e9d): optimize(api): 增加接口 UrlAwareMessage 用以描述能够获取到 URL 信息的消息元素
- [`968e5afed`](https://github.com/simple-robot/simpler-robot/commit/968e5afed): build(deps): bump org.jetbrains.kotlinx.binary-compatibility-validator
- [`c756b57ba`](https://github.com/simple-robot/simpler-robot/commit/c756b57ba): build(deps): bump org.jetbrains.kotlinx.kover from 0.8.1 to 0.8.2

## v4.2.0

> Release & Pull Notes: [v4.2.0](https://github.com/simple-robot/simpler-robot/releases/tag/v4.2.0)
>
> Commit compare: [v4.1.0..v4.2.0](https://github.com/simple-robot/simpler-robot/compare/v4.1.0..v4.2.0)

- [`7e1cd2747`](https://github.com/simple-robot/simpler-robot/commit/7e1cd2747): test: try to fix test timeout
- [`f5802fa9f`](https://github.com/simple-robot/simpler-robot/commit/f5802fa9f): optimize(api): 提供更多Application和Bot的辅助扩展API
- [`84bfef6f8`](https://github.com/simple-robot/simpler-robot/commit/84bfef6f8): optimize(api): 为BotManager和BotManagers提供更多辅助的获取API
- [`dad1db51d`](https://github.com/simple-robot/simpler-robot/commit/dad1db51d): build(deps): bump org.jetbrains.kotlinx.binary-compatibility-validator
- [`a60168238`](https://github.com/simple-robot/simpler-robot/commit/a60168238): Website
- [`24d818f86`](https://github.com/simple-robot/simpler-robot/commit/24d818f86): CI
- [`333f5dd91..e59a4ae22`](https://github.com/simple-robot/simpler-robot/compare/333f5dd91..24d818f86): 尝试更新发布和CI配置
- [`392d3be70`](https://github.com/simple-robot/simpler-robot/commit/392d3be70): build(deps): bump org.springframework.boot from 3.3.0 to 3.3.1

## v4.1.0

> Release & Pull Notes: [v4.1.0](https://github.com/simple-robot/simpler-robot/releases/tag/v4.1.0)
>
> Commit compare: [v4.0.1..v4.1.0](https://github.com/simple-robot/simpler-robot/compare/v4.0.1..v4.1.0)

- [`668ef4493`](https://github.com/simple-robot/simpler-robot/commit/668ef4493): fix(spring): 修复使用多个Filter时会失效的问题
- [`931baecfb`](https://github.com/simple-robot/simpler-robot/commit/931baecfb): version to 4.1.0
- [`a1c54b593`](https://github.com/simple-robot/simpler-robot/commit/a1c54b593): build(deps): bump org.jetbrains.kotlinx.kover from 0.8.0 to 0.8.1
- [`da81814c3`](https://github.com/simple-robot/simpler-robot/commit/da81814c3): update CI config
- [`19aa56bc3`](https://github.com/simple-robot/simpler-robot/commit/19aa56bc3): feat(api): JVM中的EventListener兼容类型增加一个 nonBlock 类型用于简化响应式结果的使用
- [`a7b5307ed`](https://github.com/simple-robot/simpler-robot/commit/a7b5307ed): build(deps): bump io.mockk:mockk from 1.13.10 to 1.13.11
- [`beb3c7b3c`](https://github.com/simple-robot/simpler-robot/commit/beb3c7b3c): feat(api): JVM中的EventListener兼容类型增加一个 nonBlock 类型用于简化响应式结果的使用

## v4.0.1

> Release & Pull Notes: [v4.0.1](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.1)
>
> Commit compare: [v4.0.0..v4.0.1](https://github.com/simple-robot/simpler-robot/compare/v4.0.0..v4.0.1)

- [`44d73931d`](https://github.com/simple-robot/simpler-robot/commit/44d73931d): fix qodana JDK version
- [`3eada7b73`](https://github.com/simple-robot/simpler-robot/commit/3eada7b73): fix spring test
- [`3895cce3e`](https://github.com/simple-robot/simpler-robot/commit/3895cce3e): pref(spring): 简单优化spring中加载resources时资源不存在的错误提示
- [`7d1e505a8`](https://github.com/simple-robot/simpler-robot/commit/7d1e505a8): build(deps): bump ktor from 2.3.8 to 2.3.11
- [`1057130f0`](https://github.com/simple-robot/simpler-robot/commit/1057130f0): Api Dump
- [`461b95c12`](https://github.com/simple-robot/simpler-robot/commit/461b95c12): pref(gradle): 优化Gradle的挂起函数辅助插件的配置内容

## v4.0.0

> Release & Pull Notes: [v4.0.0](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0)
>
> Commit compare: [v4.0.0-RC3..v4.0.0](https://github.com/simple-robot/simpler-robot/compare/v4.0.0-RC3..v4.0.0)

- [`70e86fe0c`](https://github.com/simple-robot/simpler-robot/commit/70e86fe0c): fix: 修复部分面向Java未被正确隐藏/转化的挂起函数
- [`2d5afd5de`](https://github.com/simple-robot/simpler-robot/commit/2d5afd5de): deps: 更新 suspend-transform 编译器插件版本并借此修复 #849
- [`d09696324`](https://github.com/simple-robot/simpler-robot/commit/d09696324): 更新nexus-publish插件版本

## v4.0.0-RC3

> Release & Pull Notes: [v4.0.0-RC3](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0-RC3)
>
> Commit compare: [v4.0.0-RC2..v4.0.0-RC3](https://github.com/simple-robot/simpler-robot/compare/v4.0.0-RC2..v4.0.0-RC3)

- [`b40210240`](https://github.com/simple-robot/simpler-robot/commit/b40210240): 拆分 RequestEvent 中的 accept 与 reject 操作为单独的接口，并使它们支持options风格API
- [`c01fadb37`](https://github.com/simple-robot/simpler-robot/commit/c01fadb37): build(deps): bump ksp from 2.0.0-1.0.21 to 2.0.0-1.0.22
- [`b7f4db139`](https://github.com/simple-robot/simpler-robot/commit/b7f4db139): build(deps): bump kotlinxBenchmark from 0.4.10 to 0.4.11
- [`239d5dccf`](https://github.com/simple-robot/simpler-robot/commit/239d5dccf): build(deps): bump com.squareup:kotlinpoet-ksp from 1.16.0 to 1.17.0
- [`cc77e33b5`](https://github.com/simple-robot/simpler-robot/commit/cc77e33b5): build(deps): bump slf4j from 2.0.12 to 2.0.13
- [`5d128bccd`](https://github.com/simple-robot/simpler-robot/commit/5d128bccd): spring-boot-v2 增加maven发布配置
- [`299cc2e77`](https://github.com/simple-robot/simpler-robot/commit/299cc2e77): website
- [`b0a595d54`](https://github.com/simple-robot/simpler-robot/commit/b0a595d54): 尝试调整优化项目的构建配置与依赖关系配置
- [`48588934d`](https://github.com/simple-robot/simpler-robot/commit/48588934d): build(deps): bump io.gitlab.arturbosch.detekt from 1.23.3 to 1.23.6
- [`bc8814a3e`](https://github.com/simple-robot/simpler-robot/commit/bc8814a3e): 更多模块支持wasmJs平台
- [`539774e0b`](https://github.com/simple-robot/simpler-robot/commit/539774e0b): fix: Application的coroutineContext应当始终有一个Job

## v4.0.0-RC2

> Release & Pull Notes: [v4.0.0-RC2](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0-RC2)
>
> Commit compare: [v4.0.0-RC1..v4.0.0-RC2](https://github.com/simple-robot/simpler-robot/compare/v4.0.0-RC1..v4.0.0-RC2)

- [`ba555b222`](https://github.com/simple-robot/simpler-robot/commit/ba555b222): fix: Services.addProviderExceptJvm 的jvm判断条件与实际情况相反
- [`c1cba91b7`](https://github.com/simple-robot/simpler-robot/commit/c1cba91b7): 优化 BlockingRunner 内部实现

## v4.0.0-RC1

> Release & Pull Notes: [v4.0.0-RC1](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0-RC1)
>
> Commit compare: [v4.0.0-beta3..v4.0.0-RC1](https://github.com/simple-robot/simpler-robot/compare/v4.0.0-beta3..v4.0.0-RC1)

- [`32bc43a5d`](https://github.com/simple-robot/simpler-robot/commit/32bc43a5d): fix kdoc Module
- [`b265a61b7`](https://github.com/simple-robot/simpler-robot/commit/b265a61b7): IDTest detekt format
- [`d311563c0`](https://github.com/simple-robot/simpler-robot/commit/d311563c0): Qodana CI config
- [`ebc6faccb`](https://github.com/simple-robot/simpler-robot/commit/ebc6faccb): Some tests
- [`276a0376e`](https://github.com/simple-robot/simpler-robot/commit/276a0376e): 应用 binary-compatibility-validator 来增加代码二进制兼容性检测
- [`1dcd80f8e`](https://github.com/simple-robot/simpler-robot/commit/1dcd80f8e): Update Kotlin to 2.0.0, and update the version of some dependencies: - kotlinx-coroutines to v1.8.1 - ksp to v2.0.0-1.0.21 - suspendTransform to v0.8.0-beta1
- [`910e76f6c`](https://github.com/simple-robot/simpler-robot/commit/910e76f6c): Update jetbrains-annotations to 24.1.0
- [`87ece2a51`](https://github.com/simple-robot/simpler-robot/commit/87ece2a51): Update buildconfig version to 5.3.5
- [`7e0da5238`](https://github.com/simple-robot/simpler-robot/commit/7e0da5238): website
- [`dc9cd48d8`](https://github.com/simple-robot/simpler-robot/commit/dc9cd48d8): The .gitignore
- [`3f43de910`](https://github.com/simple-robot/simpler-robot/commit/3f43de910): Some project config files
- [`8463acf04`](https://github.com/simple-robot/simpler-robot/commit/8463acf04): Fleet config and gitignore
- [`d531a002b`](https://github.com/simple-robot/simpler-robot/commit/d531a002b): 增加一个基于 Spring Boot v2.7.x 的兼容版本 starter 实现模块
- [`c5264d231`](https://github.com/simple-robot/simpler-robot/commit/c5264d231): detekt 配置，不允许格式化问题出现
- [`b72179cbc`](https://github.com/simple-robot/simpler-robot/commit/b72179cbc): 增加BotStageEvent事件定义，以及其两个子类型BotRegisteredEvent、BotStartedEvent的定义
- [`e3b846ec5`](https://github.com/simple-robot/simpler-robot/commit/e3b846ec5): build(deps): bump io.mockk:mockk from 1.13.9 to 1.13.10
- [`443e1f0b6`](https://github.com/simple-robot/simpler-robot/commit/443e1f0b6): build(deps): bump org.jetbrains.kotlinx:lincheck from 2.26 to 2.30
- [`17f1533cc`](https://github.com/simple-robot/simpler-robot/commit/17f1533cc): StringResource 拆分为 StringReadableResource；增加 Resource 和 OfflineImage 的 Resolver 来允许组件等第三方更快速的分流它们的可能内容物
- [`180a69d75`](https://github.com/simple-robot/simpler-robot/commit/180a69d75): fix some detekt warning
- [`171466199`](https://github.com/simple-robot/simpler-robot/commit/171466199): CI concurrency config
- [`28c54303f`](https://github.com/simple-robot/simpler-robot/commit/28c54303f): fix check
- [`9c53354d2`](https://github.com/simple-robot/simpler-robot/commit/9c53354d2): remove kaml
- [`9d8b6bd0e`](https://github.com/simple-robot/simpler-robot/commit/9d8b6bd0e): detekt CI config
- [`8377e8788`](https://github.com/simple-robot/simpler-robot/commit/8377e8788): 根据detekt的部分问题调整
- [`10d21c703`](https://github.com/simple-robot/simpler-robot/commit/10d21c703): 配置detekt
- [`8f41a9864`](https://github.com/simple-robot/simpler-robot/commit/8f41a9864): update issue templates

## v4.0.0-beta3

> Release & Pull Notes: [v4.0.0-beta3](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0-beta3)
>
> Commit compare: [v4.0.0-beta2..v4.0.0-beta3](https://github.com/simple-robot/simpler-robot/compare/v4.0.0-beta2..v4.0.0-beta3)


## v4.0.0-beta2

> Release & Pull Notes: [v4.0.0-beta2](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0-beta2)
>
> Commit compare: [v4.0.0-beta1..v4.0.0-beta2](https://github.com/simple-robot/simpler-robot/compare/v4.0.0-beta1..v4.0.0-beta2)

- [`8c1f96c0d`](https://github.com/simple-robot/simpler-robot/commit/8c1f96c0d): NumericalID 增加两个toUxx
- [`727192648`](https://github.com/simple-robot/simpler-robot/commit/727192648): feat(ID): 提供更多与 ID 相关的API
- [`3dd75162a`](https://github.com/simple-robot/simpler-robot/commit/3dd75162a): fix: 修复ContinuousSession测试问题
- [`6086471d5`](https://github.com/simple-robot/simpler-robot/commit/6086471d5): fix: 修复ContinuousSession测试超时问题
- [`a93c8d0fa`](https://github.com/simple-robot/simpler-robot/commit/a93c8d0fa): deps: 更新Kotlin到1.9.23
- [`1d75b6a3e`](https://github.com/simple-robot/simpler-robot/commit/1d75b6a3e): 更新注释
- [`f1488c0c3`](https://github.com/simple-robot/simpler-robot/commit/f1488c0c3): website submodule
- [`734fddf1e..5c79836c5`](https://github.com/simple-robot/simpler-robot/compare/734fddf1e..f1488c0c3): README.md
- [`d1ce3ef7f`](https://github.com/simple-robot/simpler-robot/commit/d1ce3ef7f): NEW LOGO!
- [`2c895b1b9`](https://github.com/simple-robot/simpler-robot/commit/2c895b1b9): Remove old logo
- [`110dafd69`](https://github.com/simple-robot/simpler-robot/commit/110dafd69): LOGO!
- [`c757dcd13`](https://github.com/simple-robot/simpler-robot/commit/c757dcd13): 新LOGO
- [`37f7e92e6`](https://github.com/simple-robot/simpler-robot/commit/37f7e92e6): 简单调整 Session Continuous
- [`8888e4d6e`](https://github.com/simple-robot/simpler-robot/commit/8888e4d6e): Kover, 但是Qodana用不了
- [`b78ddac62`](https://github.com/simple-robot/simpler-robot/commit/b78ddac62): qodana to v4
- [`f65b33f76`](https://github.com/simple-robot/simpler-robot/commit/f65b33f76): snapshot test report uploaded
- [`33cff308b`](https://github.com/simple-robot/simpler-robot/commit/33cff308b): 准备一些benchmark，然后放弃
- [`fa1fc3705`](https://github.com/simple-robot/simpler-robot/commit/fa1fc3705): branch test reports upload
- [`a5e96fbea`](https://github.com/simple-robot/simpler-robot/commit/a5e96fbea): fix README for module common-ktor-inputfile
- [`7df76e509`](https://github.com/simple-robot/simpler-robot/commit/7df76e509): module: 增加一个模块 common-ktor-inputfile 用来提供更简单的向 Ktor 提供表单文件信息的类型 `InputFile`
- [`126486201`](https://github.com/simple-robot/simpler-robot/commit/126486201): build(deps): bump dokka from 1.9.10 to 1.9.20
- [`bcf4ff4a4..3b7e91ec9`](https://github.com/simple-robot/simpler-robot/compare/bcf4ff4a4..126486201): KDoc publish CI config
- [`2eb54ea94`](https://github.com/simple-robot/simpler-robot/commit/2eb54ea94): fix README

## v4.0.0-beta1

> Release & Pull Notes: [v4.0.0-beta1](https://github.com/simple-robot/simpler-robot/releases/tag/v4.0.0-beta1)
>
> Commit compare: [v3.3.0..v4.0.0-beta1](https://github.com/simple-robot/simpler-robot/compare/v3.3.0..v4.0.0-beta1)

- [`7d61c17a3`](https://github.com/simple-robot/simpler-robot/commit/7d61c17a3): ISSUE Template config
- [`b5d560ee0`](https://github.com/simple-robot/simpler-robot/commit/b5d560ee0): README_en.md
- [`143dd695e`](https://github.com/simple-robot/simpler-robot/commit/143dd695e): build(deps): bump org.jetbrains.kotlinx:lincheck from 2.25 to 2.26
- [`ddbc64944`](https://github.com/simple-robot/simpler-robot/commit/ddbc64944): refactor: 优化 **持续会话** 模块内部分API、Java友好API和注释,并配置其发布; refactor: 在 common-core 模块中增加与虚拟线程相关的辅助API
- [`6b67bf988`](https://github.com/simple-robot/simpler-robot/commit/6b67bf988): refactor: 优化/改变 EventProcessor.push 默认实现中的行为：现在会直接使用 flowOn 来指定事件处理器所处的协程上下文
- [`282a822fb`](https://github.com/simple-robot/simpler-robot/commit/282a822fb): Bump org.jetbrains.kotlinx:lincheck from 2.24 to 2.25
- [`ed6c0ca63`](https://github.com/simple-robot/simpler-robot/commit/ed6c0ca63): Bump slf4j from 2.0.7 to 2.0.12
- [`dde23bd50`](https://github.com/simple-robot/simpler-robot/commit/dde23bd50): build(deps): bump kotlinx-serialization from 1.6.2 to 1.6.3
- [`1517823ee`](https://github.com/simple-robot/simpler-robot/commit/1517823ee): build(deps): bump kotlinx-coroutines from 1.8.0-RC2 to 1.8.0
- [`613b688cd..0d806e3d8`](https://github.com/simple-robot/simpler-robot/compare/613b688cd..1517823ee): refactor: 改善 session 相关API，支持“延后恢复”
- [`cc2921841`](https://github.com/simple-robot/simpler-robot/commit/cc2921841): refactor: (WIP) 调整 session 相关API
- [`0c053dc9b`](https://github.com/simple-robot/simpler-robot/commit/0c053dc9b): refactor: 改善 session 相关API，支持“延后恢复”
- [`b6717346e`](https://github.com/simple-robot/simpler-robot/commit/b6717346e): fix: 修复 `flowCollectable` 错误的返回值类型问题
- [`0dcbd707f`](https://github.com/simple-robot/simpler-robot/commit/0dcbd707f): fix: suspend test timeout
- [`d819f1dcf`](https://github.com/simple-robot/simpler-robot/commit/d819f1dcf): refactor: (WIP) 调整 session 相关API
- [`3d12243f1`](https://github.com/simple-robot/simpler-robot/commit/3d12243f1): fix: 修复 `flowCollectable` 错误的返回值类型问题
- [`da5c4b9bb..57be7f216`](https://github.com/simple-robot/simpler-robot/compare/da5c4b9bb..3d12243f1): fix: Unit test timeout
- [`eaa1bd7ed`](https://github.com/simple-robot/simpler-robot/commit/eaa1bd7ed): refactor: 将 suspend-transformer 模块的异步相关API和 `Collectable` 的异步相关API内所有的 `CoroutineScope` 参数默认值调整为 `GlobalScope` 并增加与之相关的部分警告或说明
- [`4390ca65a`](https://github.com/simple-robot/simpler-robot/commit/4390ca65a): fix: 修复 `flowCollectable` 错误的返回值类型问题
- [`014d6564e`](https://github.com/simple-robot/simpler-robot/commit/014d6564e): refactor: 将 suspend-transformer 模块的异步相关API和 `Collectable` 的异步相关API内所有的 `CoroutineScope` 参数默认值调整为 `GlobalScope` 并增加与之相关的部分警告或说明
- [`c36b9c476`](https://github.com/simple-robot/simpler-robot/commit/c36b9c476): feat: 实现持续会话的基本内容
- [`7a79552e9`](https://github.com/simple-robot/simpler-robot/commit/7a79552e9): fix: concurrentMap在native上改为使用可重入同步锁实现
- [`4f1fb4268`](https://github.com/simple-robot/simpler-robot/commit/4f1fb4268): session, map
- [`2ed0524af`](https://github.com/simple-robot/simpler-robot/commit/2ed0524af): pref: 优化针对v4.0.0-dev16及以下版本的JVM二进制兼容性
- [`b83ab3c09`](https://github.com/simple-robot/simpler-robot/commit/b83ab3c09): test: 调整JVMConfig的test相关配置
- [`b52dd18f3`](https://github.com/simple-robot/simpler-robot/commit/b52dd18f3): test: 增加几个使用 lincheck 针对并发相关实现的测试
- [`28e915e9f`](https://github.com/simple-robot/simpler-robot/commit/28e915e9f): Bump com.squareup:kotlinpoet-ksp from 1.15.3 to 1.16.0
- [`b4dda0191`](https://github.com/simple-robot/simpler-robot/commit/b4dda0191): Bump org.gradle.toolchains.foojay-resolver-convention
- [`56d5c1278`](https://github.com/simple-robot/simpler-robot/commit/56d5c1278): Bump org.jetbrains.kotlinx:lincheck from 2.19 to 2.24
- [`f514d84b2`](https://github.com/simple-robot/simpler-robot/commit/f514d84b2): 文档更新; version to dev18
- [`70ccda3d4`](https://github.com/simple-robot/simpler-robot/commit/70ccda3d4): pref: 为 Services 增加一个可用来区分JVM的扩展
- [`bae344bd6`](https://github.com/simple-robot/simpler-robot/commit/bae344bd6): Update copyright config
- [`cec18a17a`](https://github.com/simple-robot/simpler-robot/commit/cec18a17a): fix: 优化/修复 ConcurrentMutableMap 在 Js、WasmJs 下会出现 ConcurrentModificationException 的问题，并为 MutableMap 增加一个扩展 API removeValue(key, value)
- [`7c7835b15`](https://github.com/simple-robot/simpler-robot/commit/7c7835b15): version to dev18
- [`dfb505141`](https://github.com/simple-robot/simpler-robot/commit/dfb505141): pref: 为 Image 增加更多可扩展的子类型
- [`12510b490`](https://github.com/simple-robot/simpler-robot/commit/12510b490): Release: v4.0.0-dev17
- [`012663003`](https://github.com/simple-robot/simpler-robot/commit/012663003): Bump ktor from 2.3.7 to 2.3.8
- [`f93df852a`](https://github.com/simple-robot/simpler-robot/commit/f93df852a): CI: Upgrade gradle/gradle-build-action@v2 to gradle/gradle-build-action@v3
- [`6dcadb177`](https://github.com/simple-robot/simpler-robot/commit/6dcadb177): CI: Upgrade actions/checkout@v3 to actions/checkout@v4、actions/setup-java@v3 to actions/setup-java@v4
- [`a3a59e890`](https://github.com/simple-robot/simpler-robot/commit/a3a59e890): 合并拉取请求 #779
- [`dbcdc7606`](https://github.com/simple-robot/simpler-robot/commit/dbcdc7606): Bump io.gitlab.arturbosch.detekt:detekt-gradle-plugin
- [`7ea0e53a5`](https://github.com/simple-robot/simpler-robot/commit/7ea0e53a5): CI: Update dependabot.yml
- [`bc4b91c8a`](https://github.com/simple-robot/simpler-robot/commit/bc4b91c8a): test: 补充部分resource的相关JVM测试
- [`59a22de58`](https://github.com/simple-robot/simpler-robot/commit/59a22de58): fix test
- [`78d5503f7`](https://github.com/simple-robot/simpler-robot/commit/78d5503f7): pref: 增加/优化部分 Collectable(s) 相关的API、说明等
- [`7909633e1`](https://github.com/simple-robot/simpler-robot/commit/7909633e1): pref: 增加/优化部分 Message 相关的API、说明等
- [`e0d4c90f6`](https://github.com/simple-robot/simpler-robot/commit/e0d4c90f6): fix tests
- [`6d81244bb`](https://github.com/simple-robot/simpler-robot/commit/6d81244bb): 简化 StandardDeleteOption：移除一个元素。
- [`eb9134fda`](https://github.com/simple-robot/simpler-robot/commit/eb9134fda): 为两个多平台Queue类型增加 isEmpty
- [`f218d58ff`](https://github.com/simple-robot/simpler-robot/commit/f218d58ff): website
- [`2b55d9e1a`](https://github.com/simple-robot/simpler-robot/commit/2b55d9e1a): to dev17
- [`b17319397`](https://github.com/simple-robot/simpler-robot/commit/b17319397): build: 暂时关闭 K2 编译，等待 Kt2.0正式版
- [`e4699a302`](https://github.com/simple-robot/simpler-robot/commit/e4699a302): fix: BotManagerFactory 没有实现 PluginFactory 的问题
- [`9b3b44cb7`](https://github.com/simple-robot/simpler-robot/commit/9b3b44cb7): 一些随手记录
- [`d555fc59a`](https://github.com/simple-robot/simpler-robot/commit/d555fc59a): 调整一些测试
- [`e8ca7973e`](https://github.com/simple-robot/simpler-robot/commit/e8ca7973e): 开启K2编译器
- [`d696921cb`](https://github.com/simple-robot/simpler-robot/commit/d696921cb): fix(api): SerializableBotConfiguration 在 JSON下的多态序列化仅外层使用 `"component"`
- [`c0923a5b1`](https://github.com/simple-robot/simpler-robot/commit/c0923a5b1): 笑死，机翻
- [`1f0dae398`](https://github.com/simple-robot/simpler-robot/commit/1f0dae398): v4 升级论
- [`208ac4d65`](https://github.com/simple-robot/simpler-robot/commit/208ac4d65): website
- [`72508467e`](https://github.com/simple-robot/simpler-robot/commit/72508467e): 移除 v4.md
- [`86a4d07f4`](https://github.com/simple-robot/simpler-robot/commit/86a4d07f4): README buildSrc/settings
- [`5353ad09f`](https://github.com/simple-robot/simpler-robot/commit/5353ad09f): 文档更新，并准备更换域名
- [`bb3db1a6b..6f1925d60`](https://github.com/simple-robot/simpler-robot/compare/bb3db1a6b..5353ad09f): v4.0.0-dev13
- [`306a977a2`](https://github.com/simple-robot/simpler-robot/commit/306a977a2): v4.0.0-dev12
- [`ac2bcf319..9cf335e86`](https://github.com/simple-robot/simpler-robot/compare/ac2bcf319..306a977a2): v4.0.0-dev11
- [`fe4576d90`](https://github.com/simple-robot/simpler-robot/commit/fe4576d90): v4.0.0-dev10
- [`da33ec6fa`](https://github.com/simple-robot/simpler-robot/commit/da33ec6fa): v4.0.0-dev9 listen 和 process 也增加返回值
- [`aa59daf83..ccd3daec9`](https://github.com/simple-robot/simpler-robot/compare/aa59daf83..da33ec6fa): v4.0.0-dev8 拦截器函数的参数移到接收器位置
- [`50337d707`](https://github.com/simple-robot/simpler-robot/commit/50337d707): v4.0.0-dev7 增加 process 事件注册扩展
- [`9cd254c45`](https://github.com/simple-robot/simpler-robot/commit/9cd254c45): v4.0.0-dev6 调整事件，以 SourceEvent 为主； KFunctionListener返回值默认使用 empty； website文档调整；
- [`d35a39d34`](https://github.com/simple-robot/simpler-robot/commit/d35a39d34): v4.0.0-dev5 优化部分内容、增加部分扩展；调整 EventListener 实现与定义
- [`e2a08bc97`](https://github.com/simple-robot/simpler-robot/commit/e2a08bc97): v4.0.0-dev4
- [`1ce427d3e`](https://github.com/simple-robot/simpler-robot/commit/1ce427d3e): 优化ID、Timestamp
- [`160b8376a`](https://github.com/simple-robot/simpler-robot/commit/160b8376a): v4.0.0-dev3
- [`735281426`](https://github.com/simple-robot/simpler-robot/commit/735281426): 类型更名、增加 EventListeners 扩展、增加事件类型定义、调整部分注释与描述、增加要发布的 gradle buildSrc 辅助模块
- [`45981e83e`](https://github.com/simple-robot/simpler-robot/commit/45981e83e): v4.0.0-dev2
- [`a3ace941d`](https://github.com/simple-robot/simpler-robot/commit/a3ace941d): 清理部分注释；
- [`ede1566c8`](https://github.com/simple-robot/simpler-robot/commit/ede1566c8): CHANGELOG
- [`79f88f870`](https://github.com/simple-robot/simpler-robot/commit/79f88f870): 一些单元测试，一些CoroutineContext合并功能
- [`62d420fa0`](https://github.com/simple-robot/simpler-robot/commit/62d420fa0): 修复spring配置默认值
- [`835de51e8`](https://github.com/simple-robot/simpler-robot/commit/835de51e8): 一些配置调整
- [`c7dc369e3`](https://github.com/simple-robot/simpler-robot/commit/c7dc369e3): 变更配置枚举名称
- [`ad83c192e`](https://github.com/simple-robot/simpler-robot/commit/ad83c192e): 清理遗留的测试代码
- [`6a030ffd8`](https://github.com/simple-robot/simpler-robot/commit/6a030ffd8): dev2
- [`a8f1df8b0`](https://github.com/simple-robot/simpler-robot/commit/a8f1df8b0): v4.0.0-dev1
- [`9a472fcf7`](https://github.com/simple-robot/simpler-robot/commit/9a472fcf7): 版本发布的一些准备
- [`7861bb12e`](https://github.com/simple-robot/simpler-robot/commit/7861bb12e): 一些调整
- [`5b511c331`](https://github.com/simple-robot/simpler-robot/commit/5b511c331): 一些调整，比如事件类型、单元测试、DeleteOptions 等
- [`c45f9d46e`](https://github.com/simple-robot/simpler-robot/commit/c45f9d46e): add `flowCollectable`
- [`680330eca`](https://github.com/simple-robot/simpler-robot/commit/680330eca): DeleteSupport 描述更新
- [`0b0cd3ce1`](https://github.com/simple-robot/simpler-robot/commit/0b0cd3ce1): StandardDeleteOption.StandardAnalysis 增加扩展属性
- [`e52ee1ecf`](https://github.com/simple-robot/simpler-robot/commit/e52ee1ecf): 增加扩展函数 initExceptionCause
- [`32536f848`](https://github.com/simple-robot/simpler-robot/commit/32536f848): ConcurrentQueue
- [`686ced27f`](https://github.com/simple-robot/simpler-robot/commit/686ced27f): 增加 @FragileSimbotAPI 注解
- [`5a891fe23`](https://github.com/simple-robot/simpler-robot/commit/5a891fe23): 行为对象中增加 `User` 定义
- [`f4a8b4787`](https://github.com/simple-robot/simpler-robot/commit/f4a8b4787): MessageReceipt
- [`485bd3e7d`](https://github.com/simple-robot/simpler-robot/commit/485bd3e7d): MessageReceipt 的两个标准类型更改为接口类型
- [`574b5bf4f`](https://github.com/simple-robot/simpler-robot/commit/574b5bf4f): Provider大部分内容迁移到common；Reserve标记过时；
- [`7fed0d1dd`](https://github.com/simple-robot/simpler-robot/commit/7fed0d1dd): BlockingRunner 内阻塞调度器默认调整为 IO 调度器
- [`b63b9d676`](https://github.com/simple-robot/simpler-robot/commit/b63b9d676): annotation exclude
- [`69404834c`](https://github.com/simple-robot/simpler-robot/commit/69404834c): atomic updateAndGet
- [`5059ebe67`](https://github.com/simple-robot/simpler-robot/commit/5059ebe67): JS 和 Native 平台依赖中所有 compileOnly 依赖调整为 api
- [`d222ee46d`](https://github.com/simple-robot/simpler-robot/commit/d222ee46d): 更新 JsConfig.kt
- [`e4ca58984`](https://github.com/simple-robot/simpler-robot/commit/e4ca58984): fix compile
- [`03259f4cc`](https://github.com/simple-robot/simpler-robot/commit/03259f4cc): fix JS compile
- [`630b95e43`](https://github.com/simple-robot/simpler-robot/commit/630b95e43): common 模块统一使用 SimbotCommon
- [`4f288452b`](https://github.com/simple-robot/simpler-robot/commit/4f288452b): Common with wasm targets and some tests
- [`35556f7d7`](https://github.com/simple-robot/simpler-robot/commit/35556f7d7): Collections OptIn Annotation and priority Constants
- [`d6d6dc089`](https://github.com/simple-robot/simpler-robot/commit/d6d6dc089): ConcurrentQueue OptIn Annotation [skip CI]
- [`ab1f02ada`](https://github.com/simple-robot/simpler-robot/commit/ab1f02ada): KDoc CI Test
- [`61d7819b4`](https://github.com/simple-robot/simpler-robot/commit/61d7819b4): Test
- [`b54a7970d`](https://github.com/simple-robot/simpler-robot/commit/b54a7970d): .:see_no_evil: Adding or updating a .gitignore file.
- [`2c1e8896d`](https://github.com/simple-robot/simpler-robot/commit/2c1e8896d): Test
- [`b50333521`](https://github.com/simple-robot/simpler-robot/commit/b50333521): mark TODO
- [`914fc39b4`](https://github.com/simple-robot/simpler-robot/commit/914fc39b4): TestPub
- [`68f4388a8`](https://github.com/simple-robot/simpler-robot/commit/68f4388a8): dokka pub CI [skip ci]
- [`7811aaff2`](https://github.com/simple-robot/simpler-robot/commit/7811aaff2): README.md [skip ci]
- [`ef6ee0c5e`](https://github.com/simple-robot/simpler-robot/commit/ef6ee0c5e): 尝试使用 mac 系统发布 API 文档
- [`0b37e9721..236211f30`](https://github.com/simple-robot/simpler-robot/compare/0b37e9721..ef6ee0c5e): :see_no_evil: 添加或更新 .gitignore 文件
- [`426e7d12c`](https://github.com/simple-robot/simpler-robot/commit/426e7d12c): Timestamp Apple [skip CI]
- [`f60693f68`](https://github.com/simple-robot/simpler-robot/commit/f60693f68): fleet config [skip ci]
- [`ffb821d46`](https://github.com/simple-robot/simpler-robot/commit/ffb821d46): Gradle wrapper [skip ci]
- [`1c78e5e6e`](https://github.com/simple-robot/simpler-robot/commit/1c78e5e6e): module...
- [`0429c62ce`](https://github.com/simple-robot/simpler-robot/commit/0429c62ce): atomic, dokka ..?
- [`09a7ef86c`](https://github.com/simple-robot/simpler-robot/commit/09a7ef86c): atomic ..??
- [`f8cdc02d6`](https://github.com/simple-robot/simpler-robot/commit/f8cdc02d6): dokka...!
- [`d9ca5929e`](https://github.com/simple-robot/simpler-robot/commit/d9ca5929e): emm, dokka!
- [`c9b09adec..03b63bdcc`](https://github.com/simple-robot/simpler-robot/compare/c9b09adec..d9ca5929e): URIResource
- [`e2e36c27a..672a85efa`](https://github.com/simple-robot/simpler-robot/compare/e2e36c27a..03b63bdcc): dokka configs
- [`d50e15a95..c777a9273`](https://github.com/simple-robot/simpler-robot/compare/d50e15a95..672a85efa): publish configs
- [`6748291ec`](https://github.com/simple-robot/simpler-robot/commit/6748291ec): atomic tests ..?
- [`4cd8273a6`](https://github.com/simple-robot/simpler-robot/commit/4cd8273a6): build config..?
- [`cdc71204c..14af30611`](https://github.com/simple-robot/simpler-robot/compare/cdc71204c..4cd8273a6): config..?
- [`7dbdf91f2`](https://github.com/simple-robot/simpler-robot/commit/7dbdf91f2): atomics
- [`efd4b1cf2..2931f8cf5`](https://github.com/simple-robot/simpler-robot/compare/efd4b1cf2..7dbdf91f2): nexus plugin applied
- [`76c50ae8d..8e8bdc4e7`](https://github.com/simple-robot/simpler-robot/compare/76c50ae8d..2931f8cf5): CI config
- [`1c51e3690..cf7adc152`](https://github.com/simple-robot/simpler-robot/compare/1c51e3690..8e8bdc4e7): fix config for spring modules
- [`59ac958cc`](https://github.com/simple-robot/simpler-robot/commit/59ac958cc): 与 Flow 相关的兼容API，以及部分注释
- [`1656f8064`](https://github.com/simple-robot/simpler-robot/commit/1656f8064): Snapshot CI
- [`38367bd27`](https://github.com/simple-robot/simpler-robot/commit/38367bd27): Tests and some fix, and spring starter module-info.java
- [`e95c753a4`](https://github.com/simple-robot/simpler-robot/commit/e95c753a4): Tests and bug fix
- [`e1aab798b`](https://github.com/simple-robot/simpler-robot/commit/e1aab798b): 内容迁移
- [`6ce4656ab`](https://github.com/simple-robot/simpler-robot/commit/6ce4656ab): Remove All modules, and change submodule to simbot4-website
- [`bf4b1e14a`](https://github.com/simple-robot/simpler-robot/commit/bf4b1e14a): Update README.md
- [`d908d6432`](https://github.com/simple-robot/simpler-robot/commit/d908d6432): 优化无符号ID类型的 toString
- [`4743817c4`](https://github.com/simple-robot/simpler-robot/commit/4743817c4): update website
- [`d5dd2a3d6`](https://github.com/simple-robot/simpler-robot/commit/d5dd2a3d6): Create FUNDING.yml
- [`df832c279`](https://github.com/simple-robot/simpler-robot/commit/df832c279): Update qodana.yaml
- [`a955f661c`](https://github.com/simple-robot/simpler-robot/commit/a955f661c): Update question.yml
- [`cd87541cb`](https://github.com/simple-robot/simpler-robot/commit/cd87541cb): Update bug-report.yml
- [`e03d12b14`](https://github.com/simple-robot/simpler-robot/commit/e03d12b14): update..?
- [`5d1452372`](https://github.com/simple-robot/simpler-robot/commit/5d1452372): feat: Collectable
- [`60f8634f5`](https://github.com/simple-robot/simpler-robot/commit/60f8634f5): feat: ID、Timestamp and Job link
- [`3db7aa8fc`](https://github.com/simple-robot/simpler-robot/commit/3db7aa8fc): feat: Timestamp
- [`63670e479`](https://github.com/simple-robot/simpler-robot/commit/63670e479): The Timestamp
- [`d81eeebc1..6d5a46883`](https://github.com/simple-robot/simpler-robot/compare/d81eeebc1..63670e479): the ID
- [`e1715ba32`](https://github.com/simple-robot/simpler-robot/commit/e1715ba32): ID
- [`5eafba6ed`](https://github.com/simple-robot/simpler-robot/commit/5eafba6ed): feat: (WIP) multiplatform ID
- [`4c3d243b9`](https://github.com/simple-robot/simpler-robot/commit/4c3d243b9): The Timestamp
- [`4c89aed61..b71218a55`](https://github.com/simple-robot/simpler-robot/compare/4c89aed61..4c3d243b9): the ID
- [`1684d25b6`](https://github.com/simple-robot/simpler-robot/commit/1684d25b6): ID
- [`1727015ee..3e0919a25`](https://github.com/simple-robot/simpler-robot/compare/1727015ee..1684d25b6): feat: (WIP) multiplatform ID

## v3.3.0

> Release & Pull Notes: [v3.3.0](https://github.com/simple-robot/simpler-robot/releases/tag/v3.3.0)
>
> Commit compare: [v3.3.0-beta1..v3.3.0](https://github.com/simple-robot/simpler-robot/compare/v3.3.0-beta1..v3.3.0)

- [`688160c77`](https://github.com/simple-robot/simpler-robot/commit/688160c77): website

## v3.3.0-beta1

> Release & Pull Notes: [v3.3.0-beta1](https://github.com/simple-robot/simpler-robot/releases/tag/v3.3.0-beta1)
>
> Commit compare: [v3.2.0..v3.3.0-beta1](https://github.com/simple-robot/simpler-robot/compare/v3.2.0..v3.3.0-beta1)

- [`c70b2541f`](https://github.com/simple-robot/simpler-robot/commit/c70b2541f): CI: snapshot API Doc
- [`e4d82d86d`](https://github.com/simple-robot/simpler-robot/commit/e4d82d86d): build(deps): bump com.github.gmazzo.buildconfig from 4.0.4 to 4.1.2
- [`a0b7e11dc..d2c79ec96`](https://github.com/simple-robot/simpler-robot/compare/a0b7e11dc..e4d82d86d): fix: 尝试修复无法发布快照的问题
- [`8b14b21aa..f4cbbc0db`](https://github.com/simple-robot/simpler-robot/compare/8b14b21aa..d2c79ec96): fix: 更新dokka到 1.9.0 并修复与 kapt 冲突的问题（[#3153](https://github.com/Kotlin/dokka/issues/3153)）
- [`a9725fbca`](https://github.com/simple-robot/simpler-robot/commit/a9725fbca): pref: BlockingRunner 增加对虚拟线程的配置支持
- [`8b1f3cb26..ab9270a25`](https://github.com/simple-robot/simpler-robot/compare/8b1f3cb26..a9725fbca): fix: 尝试修复无法发布快照的问题
- [`1b3beb3c2`](https://github.com/simple-robot/simpler-robot/commit/1b3beb3c2): Update BotManagersTests.kt
- [`648918f72`](https://github.com/simple-robot/simpler-robot/commit/648918f72): fix: 尝试修复无法发布快照的问题
- [`f22b074e9`](https://github.com/simple-robot/simpler-robot/commit/f22b074e9): fix: hide warn
- [`d983c05f8..f0802f3b6`](https://github.com/simple-robot/simpler-robot/compare/d983c05f8..f22b074e9): pref: suspend blocking runner 调整实现，增加对虚拟线程的部分支持
- [`69a818da9`](https://github.com/simple-robot/simpler-robot/commit/69a818da9): fix: 改善部分代码到kt1.9
- [`57c29746f`](https://github.com/simple-robot/simpler-robot/commit/57c29746f): pref: suspend blocking runner 调整实现，移除对 `synchronized` 的使用并更换为 CompletableFuture 的内部实现
- [`8b39c3578..895071b47`](https://github.com/simple-robot/simpler-robot/compare/8b39c3578..57c29746f): Update README.md
- [`968ecbb95`](https://github.com/simple-robot/simpler-robot/commit/968ecbb95): build: 更新 Kotlinx Coroutines 到 v1.7.3
- [`83ae4a603`](https://github.com/simple-robot/simpler-robot/commit/83ae4a603): build: 更新 Kotlinx Serialization 到 v1.6.0
- [`fa224fd1a`](https://github.com/simple-robot/simpler-robot/commit/fa224fd1a): fix: 更新部分过时代码
- [`7d806665c`](https://github.com/simple-robot/simpler-robot/commit/7d806665c): build: 更新CI中gradle版本到 8.3; upgrade yarn.lock
- [`a8298f8be`](https://github.com/simple-robot/simpler-robot/commit/a8298f8be): build: 更新Kotlin到 1.9.10; 优化针对Java的异步桥接函数的内部实现
- [`33a95929f..bfccf89f1`](https://github.com/simple-robot/simpler-robot/compare/33a95929f..a8298f8be): WIP: Upgrade kotlin to 1.9.0
- [`7bc425f56`](https://github.com/simple-robot/simpler-robot/commit/7bc425f56): build: README and Qodana CI config
- [`62684f76c`](https://github.com/simple-robot/simpler-robot/commit/62684f76c): fix: const name
- [`e53ab0950..edf3ed00e`](https://github.com/simple-robot/simpler-robot/compare/e53ab0950..62684f76c): fix: 一些不应该是警告的警告
- [`3b295b640`](https://github.com/simple-robot/simpler-robot/commit/3b295b640): fix: Condition 'cause != null' is always true
- [`58446e97d`](https://github.com/simple-robot/simpler-robot/commit/58446e97d): fix: Logger more arguments provided
- [`c55d05aa1`](https://github.com/simple-robot/simpler-robot/commit/c55d05aa1): fix: Cannot resolve symbol 'Survivable'
- [`e637775ee`](https://github.com/simple-robot/simpler-robot/commit/e637775ee): fix: Value of 'instance' os always null
- [`2053b9674`](https://github.com/simple-robot/simpler-robot/commit/2053b9674): fix: Redundant empty initializer block
- [`faca1c5b8`](https://github.com/simple-robot/simpler-robot/commit/faca1c5b8): fix: Declaration has type inferred a platform call
- [`67f6f3cf5`](https://github.com/simple-robot/simpler-robot/commit/67f6f3cf5): fix: Cannot resolve symbol 'Bot'
- [`a6cc0e90b`](https://github.com/simple-robot/simpler-robot/commit/a6cc0e90b): fix: Cannot resolve symbol 'Survivable'
- [`190991899`](https://github.com/simple-robot/simpler-robot/commit/190991899): fix: Cannot resolve symbol 'Preparator'
- [`95e0704e9`](https://github.com/simple-robot/simpler-robot/commit/95e0704e9): fix: Cannot resolve symbol 'asKeywordMatcher'
- [`927a187e5`](https://github.com/simple-robot/simpler-robot/commit/927a187e5): fix: Recursive property accessor
- [`8518b105c`](https://github.com/simple-robot/simpler-robot/commit/8518b105c): fix: String template as argument to 'debug()' logging call
- [`722c2faff`](https://github.com/simple-robot/simpler-robot/commit/722c2faff): fix: Could not autowire bean 'ApplicationArguments' critical
- [`7f817b3bf..65a5aee02`](https://github.com/simple-robot/simpler-robot/compare/7f817b3bf..722c2faff): build&CI: Qodana config
- [`0e69f5c97`](https://github.com/simple-robot/simpler-robot/commit/0e69f5c97): build(deps): bump love.forte.simbot.component:simbot-component-mirai-core

## v3.2.0

> Release & Pull Notes: [v3.2.0](https://github.com/simple-robot/simpler-robot/releases/tag/v3.2.0)
>
> Commit compare: [v3.1.0..v3.2.0](https://github.com/simple-robot/simpler-robot/compare/v3.1.0..v3.2.0)

- [`7c3b990b1..d7f20a64b`](https://github.com/simple-robot/simpler-robot/compare/7c3b990b1..HEAD): Release: v3.2.0
- [`a924cb81e`](https://github.com/simple-robot/simpler-robot/commit/a924cb81e): 清理警告
- [`d6784de8b`](https://github.com/simple-robot/simpler-robot/commit/d6784de8b): fix(util): 修复异步调度器的Job会因异常而被关闭的问题
- [`c7e0208cf`](https://github.com/simple-robot/simpler-robot/commit/c7e0208cf): feat(boot): 支持在配置文件读取的时候使用 SerializersModule
- [`bedc962e2`](https://github.com/simple-robot/simpler-robot/commit/bedc962e2): build: version to 3.2.0
- [`54f66a134`](https://github.com/simple-robot/simpler-robot/commit/54f66a134): build(deps): bump org.jetbrains.kotlinx:lincheck from 2.17 to 2.19
- [`c63ad2e0c`](https://github.com/simple-robot/simpler-robot/commit/c63ad2e0c): build(deps): bump dokkaPluginVersion from 1.8.10 to 1.8.20
- [`fd818cf32`](https://github.com/simple-robot/simpler-robot/commit/fd818cf32): build(deps): bump ktor from 2.3.0 to 2.3.1
- [`f60952dc8`](https://github.com/simple-robot/simpler-robot/commit/f60952dc8): Upgrade version to v3.1.1
- [`c8b3769b6..5316f1bfa`](https://github.com/simple-robot/simpler-robot/compare/c8b3769b6..f60952dc8): fix(spring-boot): 在SpringBoot中支持 BotAutoRegistrationFailurePolicy

## v3.1.0

> Release & Pull Notes: [v3.1.0](https://github.com/simple-robot/simpler-robot/releases/tag/v3.1.0)
>
> Commit compare: [v3.0.0..v3.1.0](https://github.com/simple-robot/simpler-robot/compare/v3.0.0..v3.1.0)

- [`035b7812f`](https://github.com/simple-robot/simpler-robot/commit/035b7812f): build(deps): bump com.charleskorn.kaml:kaml from 0.53.0 to 0.54.0
- [`4ab387507`](https://github.com/simple-robot/simpler-robot/commit/4ab387507): fix(boot): BotRegistrationFailurePolicy 支持 Spring Boot 配置
- [`66e7f392f`](https://github.com/simple-robot/simpler-robot/commit/66e7f392f): feat(boot): 支持对自动加载bot过程中出现的异常进行策略配置
- [`568537c7f`](https://github.com/simple-robot/simpler-robot/commit/568537c7f): build: upgrade version
- [`80852ac83`](https://github.com/simple-robot/simpler-robot/commit/80852ac83): feat(api): 提供 ID 和 Timestamp 的属性委托API并完善文档
- [`2f306ac84`](https://github.com/simple-robot/simpler-robot/commit/2f306ac84): feat(api): 实现有关 Timestamp 的委托API 和部分 ID 的委托API
- [`8da19c67d`](https://github.com/simple-robot/simpler-robot/commit/8da19c67d): Upgrade website version
- [`cd52a211c`](https://github.com/simple-robot/simpler-robot/commit/cd52a211c): feat(api): Timestamp 新增 Delegate API
- [`9c48bf2a1`](https://github.com/simple-robot/simpler-robot/commit/9c48bf2a1): pref(api): Application在使用 `joinBlocking` 时不再输出 timeout debug
- [`4e1f3b283`](https://github.com/simple-robot/simpler-robot/commit/4e1f3b283): feat(api): 为 BotManagers 增加部分Java友好的API: getFirst(Class), getFirstOrNull(Class)
- [`8d40bf80d`](https://github.com/simple-robot/simpler-robot/commit/8d40bf80d): BotManagers 增加新的获取API
- [`ec0c9739e`](https://github.com/simple-robot/simpler-robot/commit/ec0c9739e): pref(api): 增加对迷惑的ID类型的警告注解与部分说明
- [`3aed35a46`](https://github.com/simple-robot/simpler-robot/commit/3aed35a46): 碎碎念
- [`41f67929a`](https://github.com/simple-robot/simpler-robot/commit/41f67929a): feat(api): 支持两个无符号整型的ID类型 `UIntID` 和 `ULongID`
- [`eda8cb598`](https://github.com/simple-robot/simpler-robot/commit/eda8cb598): Upgrade website version
- [`52e1eb549`](https://github.com/simple-robot/simpler-robot/commit/52e1eb549): feat(api): Timestamp 新增 Delegate API
- [`6d6493d3f`](https://github.com/simple-robot/simpler-robot/commit/6d6493d3f): pref(api): Application在使用 `joinBlocking` 时不再输出 timeout debug
- [`3526cb297`](https://github.com/simple-robot/simpler-robot/commit/3526cb297): feat(api): 为 BotManagers 增加部分Java友好的API: getFirst(Class), getFirstOrNull(Class)
- [`ad5913b54`](https://github.com/simple-robot/simpler-robot/commit/ad5913b54): BotManagers 增加新的获取API
- [`fd4febeb9`](https://github.com/simple-robot/simpler-robot/commit/fd4febeb9): build(deps): bump spring-boot from 2.7.11 to 2.7.12
- [`e5566efff`](https://github.com/simple-robot/simpler-robot/commit/e5566efff): pref(api): 增加对迷惑的ID类型的警告注解与部分说明
- [`844092d06`](https://github.com/simple-robot/simpler-robot/commit/844092d06): 碎碎念
- [`ce8228988`](https://github.com/simple-robot/simpler-robot/commit/ce8228988): feat(api): 支持两个无符号整型的ID类型 `UIntID` 和 `ULongID`
- [`47ea2ac1f`](https://github.com/simple-robot/simpler-robot/commit/47ea2ac1f): fix: 使生成的 xxxAsync 函数会正确的使用当前类作为 CoroutineScope (如果可以的话)
- [`0591ede3f`](https://github.com/simple-robot/simpler-robot/commit/0591ede3f): build(deps): bump kotlinx-serialization from 1.5.0 to 1.5.1
- [`060ad2b52`](https://github.com/simple-robot/simpler-robot/commit/060ad2b52): fix: 使生成的 xxxAsync 函数会正确的使用当前类作为 CoroutineScope (如果可以的话)
- [`e22b4d7b6`](https://github.com/simple-robot/simpler-robot/commit/e22b4d7b6): build(deps): bump kotlinx-coroutines from 1.7.0 to 1.7.1
- [`6b5830a46`](https://github.com/simple-robot/simpler-robot/commit/6b5830a46): build(deps): bump gradleCommon from 0.1.0 to 0.1.1
- [`c628b2ee0`](https://github.com/simple-robot/simpler-robot/commit/c628b2ee0): build(deps): bump gradleCommon from 0.0.11 to 0.1.0
- [`24a6c8384`](https://github.com/simple-robot/simpler-robot/commit/24a6c8384): Upgrade website
- [`198ca1be0`](https://github.com/simple-robot/simpler-robot/commit/198ca1be0): 碎碎念
- [`a792859e5`](https://github.com/simple-robot/simpler-robot/commit/a792859e5): feat(api): 支持两个无符号整型的ID类型 `UIntID` 和 `ULongID`

## v3.0.0

> Release & Pull Notes: [v3.0.0](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0)
>
> Commit compare: [v3.0.0-RC.3..v3.0.0](https://github.com/simple-robot/simpler-robot/compare/v3.0.0-RC.3..v3.0.0)

- [`630f42683..67ba8b918`](https://github.com/simple-robot/simpler-robot/compare/630f42683..HEAD): Upgrade submodule
- [`8244d401f`](https://github.com/simple-robot/simpler-robot/commit/8244d401f): 调整部分配置
- [`70e7deb3c`](https://github.com/simple-robot/simpler-robot/commit/70e7deb3c): 准备发布 v3.0.0
- [`3b23c6edb`](https://github.com/simple-robot/simpler-robot/commit/3b23c6edb): 调整README
- [`225911aca`](https://github.com/simple-robot/simpler-robot/commit/225911aca): feat(logger): 更新 slf4j-api 的版本并改进 simbot-logger-slf4j-impl 内的实现
- [`0a2d7fb03`](https://github.com/simple-robot/simpler-robot/commit/0a2d7fb03): build(deps): 尝试更新 slf4j-api 到 v2.0.7
- [`288c1a960`](https://github.com/simple-robot/simpler-robot/commit/288c1a960): Update submodule
- [`f7b609249`](https://github.com/simple-robot/simpler-robot/commit/f7b609249): build(deps): 尝试更新 slf4j-api 到 v2.0.7
- [`4eda26183`](https://github.com/simple-robot/simpler-robot/commit/4eda26183): build(deps): bump spring-boot from 2.7.10 to 2.7.11
- [`8d40add76`](https://github.com/simple-robot/simpler-robot/commit/8d40add76): build(dept): 更新kotlinx.coroutines到v1.7.0
- [`71bd6abcf..4e7954d60`](https://github.com/simple-robot/simpler-robot/compare/71bd6abcf..8d40add76): build(dept): 更新Kotlin到v1.8.21
- [`eb7050f3e`](https://github.com/simple-robot/simpler-robot/commit/eb7050f3e): build(deps): bump com.github.gmazzo.buildconfig from 3.1.0 to 4.0.4
- [`5bec2f4b0`](https://github.com/simple-robot/simpler-robot/commit/5bec2f4b0): Create 'updateWebsiteVersionJson' task
- [`cf69b7e73`](https://github.com/simple-robot/simpler-robot/commit/cf69b7e73): Update release.yml
- [`30b44eb74`](https://github.com/simple-robot/simpler-robot/commit/30b44eb74): fix: Module readme
- [`76170e46b`](https://github.com/simple-robot/simpler-robot/commit/76170e46b): fix: 项目配置
- [`613c21bab`](https://github.com/simple-robot/simpler-robot/commit/613c21bab): build: 调整项目结构，独立部分注解和suspend转化函数为独立模块
- [`d4796b534`](https://github.com/simple-robot/simpler-robot/commit/d4796b534): build: 调整 changelog 生成
- [`16274c808`](https://github.com/simple-robot/simpler-robot/commit/16274c808): build(deps): bump ktor from 2.2.4 to 2.3.0
- [`9e94c3617`](https://github.com/simple-robot/simpler-robot/commit/9e94c3617): build(deps): bump org.jetbrains.kotlinx:lincheck from 2.16 to 2.17
- [`38cbf931c`](https://github.com/simple-robot/simpler-robot/commit/38cbf931c): pref: SocialRelationsContainer 子类型的新语义
- [`94dea96ce`](https://github.com/simple-robot/simpler-robot/commit/94dea96ce): Delete faq-request.yml
- [`a544d080b`](https://github.com/simple-robot/simpler-robot/commit/a544d080b): feat(stage-loop): 新的简单状态机实现
- [`3856b7dc5`](https://github.com/simple-robot/simpler-robot/commit/3856b7dc5): build(deps): bump spring-boot from 2.7.6 to 2.7.10
- [`752c0d07f`](https://github.com/simple-robot/simpler-robot/commit/752c0d07f): build(deps): bump org.slf4j:slf4j-nop from 1.7.36 to 2.0.7
- [`59828bc91`](https://github.com/simple-robot/simpler-robot/commit/59828bc91): build(deps): bump com.charleskorn.kaml:kaml from 0.49.0 to 0.53.0
- [`12d853972`](https://github.com/simple-robot/simpler-robot/commit/12d853972): build(deps): bump openjdk-jmh from 1.35 to 1.36
- [`3373e5733`](https://github.com/simple-robot/simpler-robot/commit/3373e5733): build(deps): bump love.forte.plugin.suspend-transform:suspend-transform-plugin-gradle
- [`4c82fa73e`](https://github.com/simple-robot/simpler-robot/commit/4c82fa73e): Update test-branch.yml
- [`7c3bafb09`](https://github.com/simple-robot/simpler-robot/commit/7c3bafb09): build(deps): bump ktor from 2.1.1 to 2.2.4
- [`d6e5af91e`](https://github.com/simple-robot/simpler-robot/commit/d6e5af91e): Update test-branch.yml
- [`a26a13fba`](https://github.com/simple-robot/simpler-robot/commit/a26a13fba): Create test-branch.yml
- [`12273af78`](https://github.com/simple-robot/simpler-robot/commit/12273af78): build: 更新Kotlin版本到 v1.8.10
- [`0cb88528c`](https://github.com/simple-robot/simpler-robot/commit/0cb88528c): build(deps): bump org.jetbrains:annotations from 23.0.0 to 24.0.1
- [`92b7e3058`](https://github.com/simple-robot/simpler-robot/commit/92b7e3058): build(deps): bump kotlinx-serialization from 1.5.0-RC to 1.5.0
- [`8b887b0aa`](https://github.com/simple-robot/simpler-robot/commit/8b887b0aa): build(deps): bump org.jetbrains.kotlinx:lincheck from 2.15 to 2.16
- [`09d85d186`](https://github.com/simple-robot/simpler-robot/commit/09d85d186): build(deps): bump io.github.gradle-nexus:publish-plugin
- [`916b53aad`](https://github.com/simple-robot/simpler-robot/commit/916b53aad): build(deps): bump dokkaPluginVersion from 1.7.20 to 1.8.10
- [`374d33484`](https://github.com/simple-robot/simpler-robot/commit/374d33484): build: 修复版本
- [`35b226327`](https://github.com/simple-robot/simpler-robot/commit/35b226327): CONTRIBUTING: CONTRIBUTING.md
- [`360786e46..f8ebb3db1`](https://github.com/simple-robot/simpler-robot/compare/360786e46..35b226327): copyright: 更新版权信息
- [`44ae5d2cb`](https://github.com/simple-robot/simpler-robot/commit/44ae5d2cb): 更新website
- [`315ceb3c9`](https://github.com/simple-robot/simpler-robot/commit/315ceb3c9): build(deps): bump io.github.gradle-nexus:publish-plugin
- [`86e87ef20`](https://github.com/simple-robot/simpler-robot/commit/86e87ef20): build: website以submodule的形式引用
- [`72fdd4c82`](https://github.com/simple-robot/simpler-robot/commit/72fdd4c82): build: 暂时移除 website 目录
- [`a80f3eef1`](https://github.com/simple-robot/simpler-robot/commit/a80f3eef1): build(deps): bump org.springframework:spring-context
- [`8f3c7b687`](https://github.com/simple-robot/simpler-robot/commit/8f3c7b687): build(deps): bump org.springframework:spring-core from 5.3.13 to 6.0.5
- [`1fd29babc..138e6c224`](https://github.com/simple-robot/simpler-robot/compare/1fd29babc..8f3c7b687): test: stage loop
- [`9b96cace6..64e2e7fc4`](https://github.com/simple-robot/simpler-robot/compare/9b96cace6..138e6c224): Update bug-report.yml
- [`f7b0ff11a`](https://github.com/simple-robot/simpler-robot/commit/f7b0ff11a): build(deps): bump org.springframework:spring-context
- [`f082c093d`](https://github.com/simple-robot/simpler-robot/commit/f082c093d): build(deps): bump org.springframework:spring-core from 5.3.13 to 6.0.5
- [`c598c40c9..be112284e`](https://github.com/simple-robot/simpler-robot/compare/c598c40c9..f082c093d): test: stage loop

## v3.0.0-RC.3

> Release & Pull Notes: [v3.0.0-RC.3](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0-RC.3)
>
> Commit compare: [v3.0.0-RC.2..v3.0.0-RC.3](https://github.com/simple-robot/simpler-robot/compare/v3.0.0-RC.2..v3.0.0-RC.3)

- [`a6b8048de..4cd0bb48d`](https://github.com/simple-robot/simpler-robot/compare/a6b8048de..HEAD): fix: build config
- [`27055f7ff`](https://github.com/simple-robot/simpler-robot/commit/27055f7ff): build: 迁移 forte-di 和 annotation-tool 到当前仓库
- [`d8325c7e8`](https://github.com/simple-robot/simpler-robot/commit/d8325c7e8): build: 将 [annotationTool](https://github.com/ForteScarlet/annotation-tool) 迁移到当前仓库内
- [`efcbbd068..a3924b79a`](https://github.com/simple-robot/simpler-robot/compare/efcbbd068..d8325c7e8): pref: kdoc deploy
- [`b56b6528a`](https://github.com/simple-robot/simpler-robot/commit/b56b6528a): build: 调整项目整体目录结构以优化生成的文档效果
- [`3c0516dd5`](https://github.com/simple-robot/simpler-robot/commit/3c0516dd5): build: 优化部分内容

## v3.0.0-RC.2

> Release & Pull Notes: [v3.0.0-RC.2](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0-RC.2)
>
> Commit compare: [v3.0.0-RC..v3.0.0-RC.2](https://github.com/simple-robot/simpler-robot/compare/v3.0.0-RC..v3.0.0-RC.2)

- [`79d5c0f25`](https://github.com/simple-robot/simpler-robot/commit/79d5c0f25): pref(api): 尝试使用新版本的编译器插件简化原本的编译标注注解
- [`364f00bf3..e977ad246`](https://github.com/simple-robot/simpler-robot/compare/364f00bf3..79d5c0f25): build: transform plugin update to 0.2.x
- [`0dcc9186d`](https://github.com/simple-robot/simpler-robot/commit/0dcc9186d): feat(api): 为 SocialRelationsContainer 中的类型增加获取对应序列的总数量的API
- [`df66b0238`](https://github.com/simple-robot/simpler-robot/commit/df66b0238): fix: 尝试修复CI Error
- [`d21068049`](https://github.com/simple-robot/simpler-robot/commit/d21068049): build: 为api模块增加 buildConfig
- [`5cf47862d..4f82bceed`](https://github.com/simple-robot/simpler-robot/compare/5cf47862d..d21068049): Update issue-handle.yml

## v3.0.0-RC

> Release & Pull Notes: [v3.0.0-RC](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0-RC)
>
> Commit compare: [v3.0.0-M6..v3.0.0-RC](https://github.com/simple-robot/simpler-robot/compare/v3.0.0-M6..v3.0.0-RC)

- [`87d90d052`](https://github.com/simple-robot/simpler-robot/commit/87d90d052): pref: 移除绝大多数被标记为过时（@Deprecated）的内容
- [`29878e1b0`](https://github.com/simple-robot/simpler-robot/commit/29878e1b0): build: version to RC
- [`a6550e35d`](https://github.com/simple-robot/simpler-robot/commit/a6550e35d): pref(api): 不痛不痒小更新
- [`c9feb35fd`](https://github.com/simple-robot/simpler-robot/commit/c9feb35fd): feat(api): 为Messages增加新的API
- [`0ae7bdb49`](https://github.com/simple-robot/simpler-robot/commit/0ae7bdb49): pref(api-requestor): 补充注释
- [`04d2838f3`](https://github.com/simple-robot/simpler-robot/commit/04d2838f3): feat(api-requestor): 调整模块名称
- [`b2707fc3a`](https://github.com/simple-robot/simpler-robot/commit/b2707fc3a): pref(boot): 增加 bot auto-start 的提示日志
- [`60f4a6a06`](https://github.com/simple-robot/simpler-robot/commit/60f4a6a06): pref: 简单调整
- [`cd1349e71`](https://github.com/simple-robot/simpler-robot/commit/cd1349e71): feat: 增加工具模块util_api_requestor
- [`a54b00fc4`](https://github.com/simple-robot/simpler-robot/commit/a54b00fc4): build: 调整配置
- [`295207a08`](https://github.com/simple-robot/simpler-robot/commit/295207a08): build: 调整部分配置

## v3.0.0-M6

> Release & Pull Notes: [v3.0.0-M6](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0-M6)
>
> Commit compare: [v3.0.0-M5..v3.0.0-M6](https://github.com/simple-robot/simpler-robot/compare/v3.0.0-M5..v3.0.0-M6)

- [`62f813408..1cf55acc5`](https://github.com/simple-robot/simpler-robot/compare/62f813408..HEAD): Release: v3.0.0-M6
- [`1d13a8754`](https://github.com/simple-robot/simpler-robot/commit/1d13a8754): pref(spring-boot-starter): Messages.serializersModule 标记 JvmStatic
- [`7bd2460c6`](https://github.com/simple-robot/simpler-robot/commit/7bd2460c6): pref(spring-boot-starter): 部分内部流程调整
- [`83e6490d0`](https://github.com/simple-robot/simpler-robot/commit/83e6490d0): fix(spring-boot-starter): 部分调整；修复bean重复注册的问题
- [`612a80789`](https://github.com/simple-robot/simpler-robot/commit/612a80789): fix(spring-boot-starter): 修复bean重复注册的问题
- [`b2d3543fa`](https://github.com/simple-robot/simpler-robot/commit/b2d3543fa): fix(spring-boot-starter): 调整SpringBootStarter模块中的部分内部机制，以尝试修复#543
- [`ac7699962`](https://github.com/simple-robot/simpler-robot/commit/ac7699962): feat(simbot-logger-slf4j): 支持配置文件和细化日志等级
- [`54dbd4d8e`](https://github.com/simple-robot/simpler-robot/commit/54dbd4d8e): test(spring-boot-starter): unit test
- [`c243ee5af`](https://github.com/simple-robot/simpler-robot/commit/c243ee5af): fix(spring-boot-starter): 修复可重复注解无法读取的问题；以及其他小调整
- [`898ed7f84`](https://github.com/simple-robot/simpler-robot/commit/898ed7f84): pref(boot): 优化Filter的正则解析
- [`ea8bc63d8`](https://github.com/simple-robot/simpler-robot/commit/ea8bc63d8): build: version to M6

## v3.0.0-M5

> Release & Pull Notes: [v3.0.0-M5](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0-M5)
>
> Commit compare: [v3.0.0-M4..v3.0.0-M5](https://github.com/simple-robot/simpler-robot/compare/v3.0.0-M4..v3.0.0-M5)

- [`6f42e1a18`](https://github.com/simple-robot/simpler-robot/commit/6f42e1a18): test: delete test file
- [`319ad0056`](https://github.com/simple-robot/simpler-robot/commit/319ad0056): test: test file
- [`aa31576c8`](https://github.com/simple-robot/simpler-robot/commit/aa31576c8): feat(api): Applications 增加部分Java友好API
- [`b1af23e13`](https://github.com/simple-robot/simpler-robot/commit/b1af23e13): build: settings.gradle.kts
- [`ccb8aca34`](https://github.com/simple-robot/simpler-robot/commit/ccb8aca34): build: gradle.properties
- [`ebc9deeba..0081b1f26`](https://github.com/simple-robot/simpler-robot/compare/ebc9deeba..ccb8aca34): CI: workflow test
- [`5bb48043d`](https://github.com/simple-robot/simpler-robot/commit/5bb48043d): CI: gradle -> 7.6
- [`dbe2ca2ac..58fd7bb55`](https://github.com/simple-robot/simpler-robot/compare/dbe2ca2ac..5bb48043d): CI: workflow test
- [`90270ae98`](https://github.com/simple-robot/simpler-robot/commit/90270ae98): CI: GitHub Workflows config
- [`972f9bd5a`](https://github.com/simple-robot/simpler-robot/commit/972f9bd5a): fix(spring-boot-starter): 修复 #531
- [`88dff870d`](https://github.com/simple-robot/simpler-robot/commit/88dff870d): pref(api): Application.botManagers
- [`75facbacb`](https://github.com/simple-robot/simpler-robot/commit/75facbacb): pref(api): BlockingRunner warn log for long time blocking
- [`0c59cd26b`](https://github.com/simple-robot/simpler-robot/commit/0c59cd26b): build: gradle.properties
- [`df2faa7f5`](https://github.com/simple-robot/simpler-robot/commit/df2faa7f5): fix: build script
- [`3b0fb585c`](https://github.com/simple-robot/simpler-robot/commit/3b0fb585c): build: 移除旧的 simbot-logger 模块
- [`e64bdca9c`](https://github.com/simple-robot/simpler-robot/commit/e64bdca9c): build: version to v3.0.0-M5
- [`5510f1dc8`](https://github.com/simple-robot/simpler-robot/commit/5510f1dc8): refactor(logger)
- [`ee57b122a`](https://github.com/simple-robot/simpler-robot/commit/ee57b122a): feat(util): stage loop
- [`5d8a77494`](https://github.com/simple-robot/simpler-robot/commit/5d8a77494): Revert "feat(utils_stage_loop): module for stage loop"
- [`dd2bc5bb8`](https://github.com/simple-robot/simpler-robot/commit/dd2bc5bb8): pref: 优化调整项目结构
- [`71b8b2910..ab9416d93`](https://github.com/simple-robot/simpler-robot/compare/71b8b2910..dd2bc5bb8): FUCK GRADLE
- [`2369fdbc0`](https://github.com/simple-robot/simpler-robot/commit/2369fdbc0): feat(utils_stage_loop): module for stage loop
- [`5b110c792`](https://github.com/simple-robot/simpler-robot/commit/5b110c792): pref(api): AggregatedMessageReceipt.get 增加 operator
- [`6897f1c04`](https://github.com/simple-robot/simpler-robot/commit/6897f1c04): Update README.md

## v3.0.0-M4

> Release & Pull Notes: [v3.0.0-M4](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0-M4)
>
> Commit compare: [v3.0.0-M3..v3.0.0-M4](https://github.com/simple-robot/simpler-robot/compare/v3.0.0-M3..v3.0.0-M4)

- [`916ec7b20..5f1572193`](https://github.com/simple-robot/simpler-robot/compare/916ec7b20..HEAD): pref(api): MessageReceipt 结构调整

## v3.0.0-M3

> Release & Pull Notes: [v3.0.0-M3](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0-M3)
>
> Commit compare: [v3.0.0-M2..v3.0.0-M3](https://github.com/simple-robot/simpler-robot/compare/v3.0.0-M2..v3.0.0-M3)

- [`756aed38c`](https://github.com/simple-robot/simpler-robot/commit/756aed38c): pref(core): 为 `EventListenerRegistrar` 增加注册监听函数的扩展API
- [`2a1180058`](https://github.com/simple-robot/simpler-robot/commit/2a1180058): pref(api): 优化调整 `MessagesBuilder` 的实现
- [`35f292c19`](https://github.com/simple-robot/simpler-robot/commit/35f292c19): fix(api): 修复 `ResourceImage` 序列化
- [`7a20fc415`](https://github.com/simple-robot/simpler-robot/commit/7a20fc415): pref(api): 调整 Messages 部分实现内容，删改部分API
- [`64243fc32`](https://github.com/simple-robot/simpler-robot/commit/64243fc32): pref(api): 清理Messages中标记过时内容
- [`f78c06d57`](https://github.com/simple-robot/simpler-robot/commit/f78c06d57): feat(api): `Messages` 实现 `View` 代替`List`
- [`b9717c127`](https://github.com/simple-robot/simpler-robot/commit/b9717c127): perf(api): 弃用部分 `xxxIfSupport` 相关扩展API
- [`581c04b3b`](https://github.com/simple-robot/simpler-robot/commit/581c04b3b): perf(api): 移除（删除）标记过时的 `ReplyMessageReceipt` 和 `ReactReceipt`
- [`0b6586bd9`](https://github.com/simple-robot/simpler-robot/commit/0b6586bd9): perf(api): MessageReceipt实现变更，支持聚合回执
- [`a2c49c3c5`](https://github.com/simple-robot/simpler-robot/commit/a2c49c3c5): pref(api): MessageReceipt api增加 synthetic 标记
- [`b5abbd097`](https://github.com/simple-robot/simpler-robot/commit/b5abbd097): pref(api): 清理（删除） `MessageReceipt` 中的过时API
- [`e943197c6`](https://github.com/simple-robot/simpler-robot/commit/e943197c6): refactor(spring-boot-starter): 内部调整
- [`3171b4bee`](https://github.com/simple-robot/simpler-robot/commit/3171b4bee): pref(spring-boot-starter): 优化Spring Boot Starter中监听函数与bot的注册，现在可以在监听函数配置类中直接使用 `Application` 了。
- [`df03c490e`](https://github.com/simple-robot/simpler-robot/commit/df03c490e): pref(api): 增加警告
- [`c3bc5b807`](https://github.com/simple-robot/simpler-robot/commit/c3bc5b807): pref(api): 简单调整 BlockingRunner 内容
- [`7bb18bcf5`](https://github.com/simple-robot/simpler-robot/commit/7bb18bcf5): feat(api): GuildsContainer 增加 `isGuildsSupported` 属性
- [`caba10265`](https://github.com/simple-robot/simpler-robot/commit/caba10265): feat(api): GroupsContainer 增加 `isGroupsSupported` 属性
- [`184f531d2`](https://github.com/simple-robot/simpler-robot/commit/184f531d2): feat(api): ContactsContainer 增加 `isContactsSupported` 属性
- [`e21744e78`](https://github.com/simple-robot/simpler-robot/commit/e21744e78): build(deps): bump suspend-transform-plugin-gradle from 0.0.5 to 0.1.0
- [`9c989b95a`](https://github.com/simple-robot/simpler-robot/commit/9c989b95a): feat(api): EventProcessor.resultsView 、 EventProcessingContext.resultsView
- [`49e6ec332`](https://github.com/simple-robot/simpler-robot/commit/49e6ec332): feat(api): Views
- [`e1fb3a482`](https://github.com/simple-robot/simpler-robot/commit/e1fb3a482): feat(api): View
- [`9acb1ede5`](https://github.com/simple-robot/simpler-robot/commit/9acb1ede5): fix(core): 修复 `ResourceImage` 无法序列化的问题

## v3.0.0-M2

> Release & Pull Notes: [v3.0.0-M2](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0-M2)
>
> Commit compare: [v2.5.0..v3.0.0-M2](https://github.com/simple-robot/simpler-robot/compare/v2.5.0..v3.0.0-M2)

- [`f2d311cab`](https://github.com/simple-robot/simpler-robot/commit/f2d311cab): feat(core): SimpleEventListenerManagerImpl 内部的默认事件调度器可关闭
- [`c0794bad8`](https://github.com/simple-robot/simpler-robot/commit/c0794bad8): feat(core): SimpleEventListenerManagerImpl 内部的默认事件调度器
- [`eda775c5f`](https://github.com/simple-robot/simpler-robot/commit/eda775c5f): refactor: 调整runInBlocking的使用
- [`3853e00bc`](https://github.com/simple-robot/simpler-robot/commit/3853e00bc): refactor: 调整部分LoggerFactory使用方式
- [`7ed492559`](https://github.com/simple-robot/simpler-robot/commit/7ed492559): refactor: runInAsync 函数receiver
- [`1d7d7fe5a`](https://github.com/simple-robot/simpler-robot/commit/1d7d7fe5a): fix(core): 事件的默认返回值
- [`b8fc23d03`](https://github.com/simple-robot/simpler-robot/commit/b8fc23d03): fix(boot): 事件返回值的解析策略
- [`d64dc7cb3`](https://github.com/simple-robot/simpler-robot/commit/d64dc7cb3): refactor(core): blocking runner 内容调整，追加可配置的 asyncDispatcher
- [`98e6d6aed`](https://github.com/simple-robot/simpler-robot/commit/98e6d6aed): refactor(core): blocking runner优化
- [`5101e6100`](https://github.com/simple-robot/simpler-robot/commit/5101e6100): feat: 阻塞API执行方式调整
- [`bd773ad72`](https://github.com/simple-robot/simpler-robot/commit/bd773ad72): feat(api): 为 Items 增加部分异步API
- [`6fedac755`](https://github.com/simple-robot/simpler-robot/commit/6fedac755): build(logger): 调整slf4j-impl模块名称
- [`89de1e859`](https://github.com/simple-robot/simpler-robot/commit/89de1e859): build: gradle-common upgrade
- [`77aaf6ad2`](https://github.com/simple-robot/simpler-robot/commit/77aaf6ad2): feat(api): 调整基础的阻塞API与兼容异步API的调度方式, 默认将不提供额外的调度器
- [`ad31f9868`](https://github.com/simple-robot/simpler-robot/commit/ad31f9868): feat(logger): 提供simbot-logger的实现模块
- [`b07e3f12b`](https://github.com/simple-robot/simpler-robot/commit/b07e3f12b): feat(logger): 提供slf4j默认实现
- [`6f5ddce21`](https://github.com/simple-robot/simpler-robot/commit/6f5ddce21): feat(api): 优化 EventResult 异步相关api; 调整所有 `Future` 相关内容为直接的 `CompletableFuture`
- [`b3b707db9`](https://github.com/simple-robot/simpler-robot/commit/b3b707db9): fix(api): EventListenerRegistrationDescription.Companion.of 调整为JVM static
- [`da996c9d5`](https://github.com/simple-robot/simpler-robot/commit/da996c9d5): feat(api): 优化 EventResult 异步相关api; 调整所有 `Future` 相关内容为直接的 `CompletableFuture`
- [`30a476205`](https://github.com/simple-robot/simpler-robot/commit/30a476205): fix(api): EventListenerRegistrationDescription.Companion.of 调整为JVM static

## v2.5.0

> Release & Pull Notes: [v2.5.0](https://github.com/simple-robot/simpler-robot/releases/tag/v2.5.0)
>
> Commit compare: [v3.0.0-M1..v2.5.0](https://github.com/simple-robot/simpler-robot/compare/v3.0.0-M1..v2.5.0)


## v3.0.0-M1

> Release & Pull Notes: [v3.0.0-M1](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0-M1)
>
> Commit compare: [v3.0.0-beta.3..v3.0.0-M1](https://github.com/simple-robot/simpler-robot/compare/v3.0.0-beta.3..v3.0.0-M1)

- [`14d40bd65`](https://github.com/simple-robot/simpler-robot/commit/14d40bd65): refactor: 暂时恢复 `love.forte.simbot.LoggerFactory` 并标记过时
- [`e0315ea0e`](https://github.com/simple-robot/simpler-robot/commit/e0315ea0e): build: update version to v3.0.0-beta.4
- [`5b367ab51`](https://github.com/simple-robot/simpler-robot/commit/5b367ab51): Update README.md
- [`e24f564d4`](https://github.com/simple-robot/simpler-robot/commit/e24f564d4): refactor: 暂时恢复 `love.forte.simbot.LoggerFactory` 并标记过时
- [`8cbdd2e3c`](https://github.com/simple-robot/simpler-robot/commit/8cbdd2e3c): build: update version to v3.0.0-beta.4

## v3.0.0-beta.3

> Release & Pull Notes: [v3.0.0-beta.3](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0-beta.3)
>
> Commit compare: [v3.0.0-beta.2..v3.0.0-beta.3](https://github.com/simple-robot/simpler-robot/compare/v3.0.0-beta.2..v3.0.0-beta.3)

- [`37a1e3730`](https://github.com/simple-robot/simpler-robot/commit/37a1e3730): build(workflows): 只有非dev才创建GitHub Release
- [`65f80807e`](https://github.com/simple-robot/simpler-robot/commit/65f80807e): dev-release: v3.0.0-beta.3-dev.10
- [`53433c74e`](https://github.com/simple-robot/simpler-robot/commit/53433c74e): feat(logger): js/native console logger rename
- [`305c26cf6`](https://github.com/simple-robot/simpler-robot/commit/305c26cf6): feat(logger): js console logger impl
- [`717d7a395`](https://github.com/simple-robot/simpler-robot/commit/717d7a395): feat(logger): natice console logger impl
- [`13803565b`](https://github.com/simple-robot/simpler-robot/commit/13803565b): publish: v3.0.0-beta.3-dev.2
- [`bd89b03f4..7eb35d6af`](https://github.com/simple-robot/simpler-robot/compare/bd89b03f4..13803565b): build: try publish version: v3.0.0-beta.3-dev.1
- [`abbcd70ff`](https://github.com/simple-robot/simpler-robot/commit/abbcd70ff): build: project config
- [`4dff4fe07..b9199537a`](https://github.com/simple-robot/simpler-robot/compare/4dff4fe07..abbcd70ff): build: gradle config
- [`88cd0866d`](https://github.com/simple-robot/simpler-robot/commit/88cd0866d): build: change logger module to multiplatform-logger
- [`618a19a79`](https://github.com/simple-robot/simpler-robot/commit/618a19a79): test: log level compare
- [`4078aaa39`](https://github.com/simple-robot/simpler-robot/commit/4078aaa39): test: log formatter
- [`1217b80ef`](https://github.com/simple-robot/simpler-robot/commit/1217b80ef): feat: multiplatform logger module
- [`c68470343`](https://github.com/simple-robot/simpler-robot/commit/c68470343): refactor: 简单调整文档和部分内容
- [`1111224c0..20cdc9279`](https://github.com/simple-robot/simpler-robot/compare/1111224c0..c68470343): fix: suspend transform plugin: include annotations
- [`4ac3610d0`](https://github.com/simple-robot/simpler-robot/commit/4ac3610d0): refactor: 恢复一个 EventListenersGenerator 接口类型
- [`83b9efbd9`](https://github.com/simple-robot/simpler-robot/commit/83b9efbd9): build: version to v3.0.0-beta.3
- [`feee5cc84`](https://github.com/simple-robot/simpler-robot/commit/feee5cc84): test(api): EventResult reactive test
- [`57d3f803b`](https://github.com/simple-robot/simpler-robot/commit/57d3f803b): feat(api): EventResult support CompletionStage and Deferred
- [`e36e7a687`](https://github.com/simple-robot/simpler-robot/commit/e36e7a687): refactor(api): Event in isSub
- [`b5f3183d9`](https://github.com/simple-robot/simpler-robot/commit/b5f3183d9): feat: 新的监听函数注册, 管理等内容
- [`2e5a4cf3f`](https://github.com/simple-robot/simpler-robot/commit/2e5a4cf3f): feat: 监听函数注册, 管理等内容
- [`67e30e8a3`](https://github.com/simple-robot/simpler-robot/commit/67e30e8a3): feat(api): SimpleEventListenerManager
- [`21d55100a`](https://github.com/simple-robot/simpler-robot/commit/21d55100a): fix(api): 修复 `DelayableCompletableFuture.get`
- [`88a18b0e8`](https://github.com/simple-robot/simpler-robot/commit/88a18b0e8): feat(api): 调整优化 `DefaultBlockingDispatcher` 实现, 增加使用 `ForkJoinPool` 的可能性
- [`9772d7da8`](https://github.com/simple-robot/simpler-robot/commit/9772d7da8): feat(api): DelayableCompletableFutureImpl使用的默认调度器调整为DefaultBlockingDispatcher
- [`0d8698bd7`](https://github.com/simple-robot/simpler-robot/commit/0d8698bd7): refactor(api): rename DelayCompletionFutureStage to DelayCompletionStage
- [`f66e790e2`](https://github.com/simple-robot/simpler-robot/commit/f66e790e2): feat(core): DelayableCompletableFuture
- [`0716b7be9`](https://github.com/simple-robot/simpler-robot/commit/0716b7be9): test(core): delayable future test
- [`d67fb40ed..d64bdf256`](https://github.com/simple-robot/simpler-robot/compare/d67fb40ed..0716b7be9): feat(core): delayable future api
- [`a85c8ff7b`](https://github.com/simple-robot/simpler-robot/commit/a85c8ff7b): feature: 监听函数句柄
- [`7f19d6931`](https://github.com/simple-robot/simpler-robot/commit/7f19d6931): refactor: 调整命名与过时相关
- [`bb1000d39`](https://github.com/simple-robot/simpler-robot/commit/bb1000d39): build: project config
- [`bfcf876bc..6627f5f10`](https://github.com/simple-robot/simpler-robot/compare/bfcf876bc..bb1000d39): build: gradle config
- [`56000b099`](https://github.com/simple-robot/simpler-robot/commit/56000b099): build: change logger module to multiplatform-logger
- [`de6de2efb..3d34dbc62`](https://github.com/simple-robot/simpler-robot/compare/de6de2efb..56000b099): Update README.md
- [`4967f5e6b`](https://github.com/simple-robot/simpler-robot/commit/4967f5e6b): test: log level compare
- [`f7bae84ad`](https://github.com/simple-robot/simpler-robot/commit/f7bae84ad): test: log formatter
- [`87708f5ae`](https://github.com/simple-robot/simpler-robot/commit/87708f5ae): feat: multiplatform logger module
- [`38a98c0e7`](https://github.com/simple-robot/simpler-robot/commit/38a98c0e7): refactor: 简单调整文档和部分内容
- [`bfb09e21a..1c0d218ea`](https://github.com/simple-robot/simpler-robot/compare/bfb09e21a..38a98c0e7): fix: suspend transform plugin: include annotations
- [`49ebdc108`](https://github.com/simple-robot/simpler-robot/commit/49ebdc108): refactor: 恢复一个 EventListenersGenerator 接口类型
- [`f04d97da5`](https://github.com/simple-robot/simpler-robot/commit/f04d97da5): build: version to v3.0.0-beta.3
- [`663c9ed41`](https://github.com/simple-robot/simpler-robot/commit/663c9ed41): test(api): EventResult reactive test
- [`f60f2b512`](https://github.com/simple-robot/simpler-robot/commit/f60f2b512): feat(api): EventResult support CompletionStage and Deferred
- [`81a9a16d9`](https://github.com/simple-robot/simpler-robot/commit/81a9a16d9): refactor(api): Event in isSub
- [`fde0f2ea0`](https://github.com/simple-robot/simpler-robot/commit/fde0f2ea0): feat: 新的监听函数注册, 管理等内容
- [`3e9642da3`](https://github.com/simple-robot/simpler-robot/commit/3e9642da3): feat: 监听函数注册, 管理等内容
- [`e5a4ece1e`](https://github.com/simple-robot/simpler-robot/commit/e5a4ece1e): feat(api): SimpleEventListenerManager
- [`e06665915`](https://github.com/simple-robot/simpler-robot/commit/e06665915): fix(api): 修复 `DelayableCompletableFuture.get`
- [`9bbc35f9b`](https://github.com/simple-robot/simpler-robot/commit/9bbc35f9b): feat(api): 调整优化 `DefaultBlockingDispatcher` 实现, 增加使用 `ForkJoinPool` 的可能性
- [`14f4e2e46`](https://github.com/simple-robot/simpler-robot/commit/14f4e2e46): feat(api): DelayableCompletableFutureImpl使用的默认调度器调整为DefaultBlockingDispatcher
- [`d0e94436a`](https://github.com/simple-robot/simpler-robot/commit/d0e94436a): refactor(api): rename DelayCompletionFutureStage to DelayCompletionStage
- [`016ed43b5`](https://github.com/simple-robot/simpler-robot/commit/016ed43b5): feat(core): DelayableCompletableFuture
- [`e750b02a6`](https://github.com/simple-robot/simpler-robot/commit/e750b02a6): test(core): delayable future test
- [`5930613b7..35164d402`](https://github.com/simple-robot/simpler-robot/compare/5930613b7..e750b02a6): feat(core): delayable future api
- [`8d2f974f6`](https://github.com/simple-robot/simpler-robot/commit/8d2f974f6): feature: 监听函数句柄
- [`50316c735`](https://github.com/simple-robot/simpler-robot/commit/50316c735): refactor: 调整命名与过时相关

## v3.0.0-beta.2

> Release & Pull Notes: [v3.0.0-beta.2](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0-beta.2)
>
> Commit compare: [v3.0.0-beta..v3.0.0-beta.2](https://github.com/simple-robot/simpler-robot/compare/v3.0.0-beta..v3.0.0-beta.2)

- [`b31c2c588..c89cb6521`](https://github.com/simple-robot/simpler-robot/compare/b31c2c588..HEAD): refactor: 调整默认调度器内部分配置
- [`8ddd1f576`](https://github.com/simple-robot/simpler-robot/commit/8ddd1f576): build: suspend transform version to 0.0.4
- [`19dc218fc..416544fa8`](https://github.com/simple-robot/simpler-robot/compare/19dc218fc..8ddd1f576): build: Gradle环境调整为JDK11
- [`df0357f05`](https://github.com/simple-robot/simpler-robot/commit/df0357f05): feat: 准备有关HttpServerBot的相关内容
- [`c6bf65fde`](https://github.com/simple-robot/simpler-robot/commit/c6bf65fde): build: support suspend transform plugin
- [`0ea4bfdec`](https://github.com/simple-robot/simpler-robot/commit/0ea4bfdec): feat: 简单优化美化输出日志色彩
- [`fc269d8cd..9fb77c3b4`](https://github.com/simple-robot/simpler-robot/compare/fc269d8cd..0ea4bfdec): build: 调整与本地测试
- [`bc009f793`](https://github.com/simple-robot/simpler-robot/commit/bc009f793): feature: suspend兼容
- [`e775626e3..67124c540`](https://github.com/simple-robot/simpler-robot/compare/e775626e3..bc009f793): feature(core): suspend兼容
- [`efa4d59a8`](https://github.com/simple-robot/simpler-robot/commit/efa4d59a8): feature(api): suspend兼容
- [`5aec4deb2`](https://github.com/simple-robot/simpler-robot/commit/5aec4deb2): build(core): 配置suspend兼容插件
- [`e749069f3..7d3b9af20`](https://github.com/simple-robot/simpler-robot/compare/e749069f3..5aec4deb2): feature(api): 改造blocking与async桥接
- [`95a0910ef`](https://github.com/simple-robot/simpler-robot/commit/95a0910ef): feature: 改造blocking与async桥接
- [`13f5e2d82`](https://github.com/simple-robot/simpler-robot/commit/13f5e2d82): refactor: 大成功⭐
- [`ae9e7e493`](https://github.com/simple-robot/simpler-robot/commit/ae9e7e493): refactor: 尝试使用 [suspend-transform-plugin](https://github.com/ForteScarlet/kotlin-suspend-transform-compiler-plugin) 替换部分内容
- [`d7e5775a8`](https://github.com/simple-robot/simpler-robot/commit/d7e5775a8): build: update kotlin version
- [`7f98885e7`](https://github.com/simple-robot/simpler-robot/commit/7f98885e7): build: update gradle version
- [`9bac41b92`](https://github.com/simple-robot/simpler-robot/commit/9bac41b92): build: version调整为 `v3.0.0-beta.2-dev.1`
- [`0ca17db55`](https://github.com/simple-robot/simpler-robot/commit/0ca17db55): perf(api): 调整 `Lambdas` 中函数参数顺序
- [`5bafac3db`](https://github.com/simple-robot/simpler-robot/commit/5bafac3db): feature(api): 面向Java的Lambda工具
- [`cde7a73db..ac6258a3e`](https://github.com/simple-robot/simpler-robot/compare/cde7a73db..5bafac3db): feature(api): 提供为Java提供 lambda 兼容转化的工具
- [`183ddfd16`](https://github.com/simple-robot/simpler-robot/commit/183ddfd16): build: update version to 3.0.0-beta.2
- [`ec3fcc255`](https://github.com/simple-robot/simpler-robot/commit/ec3fcc255): fix: 调整各监听函数相关实现，所有默认的 `EventResult` 调整为 `EventResult.invalid()`。
- [`e37752fcc`](https://github.com/simple-robot/simpler-robot/commit/e37752fcc): fix(api): 将 `EventResult.Default` 访问级别恢复为私有。
- [`eecf25659`](https://github.com/simple-robot/simpler-robot/commit/eecf25659): feature(api): 将 `EventResult.Default` 访问级别调整为公开
- [`10ff9acf1`](https://github.com/simple-robot/simpler-robot/commit/10ff9acf1): build: update version to 3.0.0-beta.2
- [`0565c010f`](https://github.com/simple-robot/simpler-robot/commit/0565c010f): fix: 调整各监听函数相关实现，所有默认的 `EventResult` 调整为 `EventResult.invalid()`。
- [`4a8bf5971`](https://github.com/simple-robot/simpler-robot/commit/4a8bf5971): fix(api): 将 `EventResult.Default` 访问级别恢复为私有。
- [`fd8deaf73..82f8cd489`](https://github.com/simple-robot/simpler-robot/compare/fd8deaf73..4a8bf5971): feature(api): 将 `EventResult.Default` 访问级别调整为公开

## v3.0.0-beta

> Release & Pull Notes: [v3.0.0-beta](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0-beta)
>
> Commit compare: [v3.0.0-beta-RC.2..v3.0.0-beta](https://github.com/simple-robot/simpler-robot/compare/v3.0.0-beta-RC.2..v3.0.0-beta)

- [`8f95fb7c1`](https://github.com/simple-robot/simpler-robot/commit/8f95fb7c1): feature(core): 为 `SimpleListenerBuilder` 提供部分匹配扩展
- [`1d7ecf524`](https://github.com/simple-robot/simpler-robot/commit/1d7ecf524): refactor(api): 调整 `BlockingEventInterceptor`
- [`ef9fcb2b3`](https://github.com/simple-robot/simpler-robot/commit/ef9fcb2b3): fix(api): 为 `BlockingFilter` 及其衍生补充缺失的参数
- [`d84ba564b`](https://github.com/simple-robot/simpler-robot/commit/d84ba564b): fix(api): 处理logger最后的Throwable参数
- [`2ca4a97d5`](https://github.com/simple-robot/simpler-robot/commit/2ca4a97d5): feat(api): 为Limiter提供解构扩展
- [`d7572119d`](https://github.com/simple-robot/simpler-robot/commit/d7572119d): feat(api): 为MemberInfo提供解构扩展
- [`13056c15e`](https://github.com/simple-robot/simpler-robot/commit/13056c15e): feat(api): 为FriendInfo提供解构扩展
- [`090de245e`](https://github.com/simple-robot/simpler-robot/commit/090de245e): feat(api): 为UserInfo提供解构扩展
- [`3cf2b5eb3`](https://github.com/simple-robot/simpler-robot/commit/3cf2b5eb3): feat(api): 为Category提供解构扩展
- [`218805fb0`](https://github.com/simple-robot/simpler-robot/commit/218805fb0): fix(api): 修复Bot未实现BotInfo的问题
- [`889196228`](https://github.com/simple-robot/simpler-robot/commit/889196228): feat(api): 提供OrganizationInfo的解构扩展
- [`15d694419..a0261abfb`](https://github.com/simple-robot/simpler-robot/compare/15d694419..889196228): feat(api): 提供BotInfo的解构扩展
- [`f3889c84a`](https://github.com/simple-robot/simpler-robot/commit/f3889c84a): build: release.yml
- [`cf17d11fd`](https://github.com/simple-robot/simpler-robot/commit/cf17d11fd): fix(api): 调整 `Image.of` JVM上为静态方法
- [`fa199cbe6`](https://github.com/simple-robot/simpler-robot/commit/fa199cbe6): refactor(api): Digest增加方法
- [`c8bca5c05`](https://github.com/simple-robot/simpler-robot/commit/c8bca5c05): refactor(api): 简单调整RandomIDUtil
- [`c3aa4ff85`](https://github.com/simple-robot/simpler-robot/commit/c3aa4ff85): build: spring boot 依赖版本更新
- [`a2b363f7a`](https://github.com/simple-robot/simpler-robot/commit/a2b363f7a): build: 版本到RC.3

## v3.0.0-beta-RC.2

> Release & Pull Notes: [v3.0.0-beta-RC.2](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0-beta-RC.2)
>
> Commit compare: [v3.0.0-beta-RC.1..v3.0.0-beta-RC.2](https://github.com/simple-robot/simpler-robot/compare/v3.0.0-beta-RC.1..v3.0.0-beta-RC.2)

- [`5e04c0e5c`](https://github.com/simple-robot/simpler-robot/commit/5e04c0e5c): test(spring-boot-starter): spring boot starter监听函数扫描注册测试
- [`98ca98af1`](https://github.com/simple-robot/simpler-robot/commit/98ca98af1): feat(spring-boot-starter): Spring Boot Starter监听函数扫描
- [`6695c64ed..f8f23d208`](https://github.com/simple-robot/simpler-robot/compare/6695c64ed..98ca98af1): feat(spring-boot-starter): 重整监听函数注册流程
- [`e20c35215`](https://github.com/simple-robot/simpler-robot/commit/e20c35215): refactor(spring-boot-starter): Just update something
- [`2a8d97fbe`](https://github.com/simple-robot/simpler-robot/commit/2a8d97fbe): refactor(spring-boot-starter): 重整监听函数注册流程
- [`9da74b240`](https://github.com/simple-robot/simpler-robot/commit/9da74b240): fix(boot-api): 修复匹配失效的问题
- [`7641d2df9`](https://github.com/simple-robot/simpler-robot/commit/7641d2df9): build: 调整版本为 `3.0.0-beta-RC.2`
- [`8610729b9..33e314db0`](https://github.com/simple-robot/simpler-robot/compare/8610729b9..7641d2df9): feat(spring-boot-starter): 重整监听函数注册流程
- [`7cf1059cb`](https://github.com/simple-robot/simpler-robot/commit/7cf1059cb): refactor(spring-boot-starter): Just update something
- [`35a3314ab`](https://github.com/simple-robot/simpler-robot/commit/35a3314ab): refactor(spring-boot-starter): 重整监听函数注册流程
- [`c4a2af8ac`](https://github.com/simple-robot/simpler-robot/commit/c4a2af8ac): fix(boot-api): 修复匹配失效的问题
- [`68ddb2246`](https://github.com/simple-robot/simpler-robot/commit/68ddb2246): build: 调整版本为 `3.0.0-beta-RC.2`
- [`ded141afb`](https://github.com/simple-robot/simpler-robot/commit/ded141afb): feat(api): 为 DelayableCompletableFuture 提供更多参数并优化

## v3.0.0-beta-RC.1

> Release & Pull Notes: [v3.0.0-beta-RC.1](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0-beta-RC.1)
>
> Commit compare: [v3.0.0-beta-M3..v3.0.0-beta-RC.1](https://github.com/simple-robot/simpler-robot/compare/v3.0.0-beta-M3..v3.0.0-beta-RC.1)

- [`b950009de`](https://github.com/simple-robot/simpler-robot/commit/b950009de): publish: 发布 `v3.0.0-beta-RC.1`
- [`75b739818`](https://github.com/simple-robot/simpler-robot/commit/75b739818): fix: 修复快照发布失败问题
- [`c61c481a0`](https://github.com/simple-robot/simpler-robot/commit/c61c481a0): version: 更新版本到 `v3.0.0-beta-RC.1` 并合并‘提升过时等级’内容
- [`019c7734b`](https://github.com/simple-robot/simpler-robot/commit/019c7734b): refactor: 过时标记过时等级提升
- [`227001875`](https://github.com/simple-robot/simpler-robot/commit/227001875): feat(api): 清理注释
- [`a285d59b8`](https://github.com/simple-robot/simpler-robot/commit/a285d59b8): feat(boot): 当启动自动启动bot时，将会在启动流程中顺序启动bot
- [`3c506ce45`](https://github.com/simple-robot/simpler-robot/commit/3c506ce45): feat(api): 为成员变动事件实现计划中的4个子事件类型
- [`606adc690`](https://github.com/simple-robot/simpler-robot/commit/606adc690): refactor: 标记部分过时内容
- [`9fa6d36fe`](https://github.com/simple-robot/simpler-robot/commit/9fa6d36fe): refactor: 版本至beta-RC

## v3.0.0-beta-M3

> Release & Pull Notes: [v3.0.0-beta-M3](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0-beta-M3)
>
> Commit compare: [v3.0.0-beta-M2..v3.0.0-beta-M3](https://github.com/simple-robot/simpler-robot/compare/v3.0.0-beta-M2..v3.0.0-beta-M3)

- [`3aff4cf0a`](https://github.com/simple-robot/simpler-robot/commit/3aff4cf0a): publish: :bookmark: v3.0.0-beta-M3
- [`e57ab1f8d`](https://github.com/simple-robot/simpler-robot/commit/e57ab1f8d): refactor: 简单调整gradle配置
- [`1e7a7be46`](https://github.com/simple-robot/simpler-robot/commit/1e7a7be46): feat(api): 优化/简化监听函数构建DSL
- [`64354a587`](https://github.com/simple-robot/simpler-robot/commit/64354a587): refactor(api): Group子集api提供默认值
- [`6ff04b379`](https://github.com/simple-robot/simpler-robot/commit/6ff04b379): feat(api): 为 `Organization`  提供对children的精准获取api
- [`6cd330c8e`](https://github.com/simple-robot/simpler-robot/commit/6cd330c8e): fix(api): Friend编译不通过
- [`793904f93`](https://github.com/simple-robot/simpler-robot/commit/793904f93): feat(api): 为部分容器类型提供更多扩展函数
- [`78c5cc7f6`](https://github.com/simple-robot/simpler-robot/commit/78c5cc7f6): feat(api): 提供 `Category` 定义及简单实现
- [`ccfc7cfb6`](https://github.com/simple-robot/simpler-robot/commit/ccfc7cfb6): refactor(api): 调整 `SocialRelationsContainer` 及其子接口的包路径
- [`6175debc3`](https://github.com/simple-robot/simpler-robot/commit/6175debc3): fix(spring-boot-starter-test): fix build failure
- [`8d5cb309f`](https://github.com/simple-robot/simpler-robot/commit/8d5cb309f): version: M3
- [`3564b8cb0`](https://github.com/simple-robot/simpler-robot/commit/3564b8cb0): refactor(api): Bot相关内容包路径调整
- [`fd7e22dcd`](https://github.com/simple-robot/simpler-robot/commit/fd7e22dcd): fix(api): 消除警告

## v3.0.0-beta-M2

> Release & Pull Notes: [v3.0.0-beta-M2](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0-beta-M2)
>
> Commit compare: [v2.4.0..v3.0.0-beta-M2](https://github.com/simple-robot/simpler-robot/compare/v2.4.0..v3.0.0-beta-M2)

- [`e23688062`](https://github.com/simple-robot/simpler-robot/commit/e23688062): feat(api): ContinuousSessionContext 提供 Duration Support
- [`df2ce99f7`](https://github.com/simple-robot/simpler-robot/commit/df2ce99f7): refactor(api): 调整内部代码
- [`1d096d8ba`](https://github.com/simple-robot/simpler-robot/commit/1d096d8ba): feat(api): 为 MuteSupport 提供 Duration support
- [`9a9d1a31f`](https://github.com/simple-robot/simpler-robot/commit/9a9d1a31f): feat(api): 为 DelayableCoroutineScope 提供Duration support
- [`1194157f5`](https://github.com/simple-robot/simpler-robot/commit/1194157f5): feat(api): 为各持续时间参数提供Duration support
- [`088ecb7a4`](https://github.com/simple-robot/simpler-robot/commit/088ecb7a4): test: DurationConvert 基准测试
- [`4497ba243..24ffde7b0`](https://github.com/simple-robot/simpler-robot/compare/4497ba243..088ecb7a4): feat(api): JavaDuration与KotlinDuration的转化
- [`f8769f51a`](https://github.com/simple-robot/simpler-robot/commit/f8769f51a): feat(api): MuteSupport中各函数的时间参数调整为 java.time.Duration
- [`ea21569c6`](https://github.com/simple-robot/simpler-robot/commit/ea21569c6): feat(api): DelayableCoroutineScope中各函数的时间参数调整为 java.time.Duration
- [`e8ac46e4e`](https://github.com/simple-robot/simpler-robot/commit/e8ac46e4e): version: 版本递增
- [`6110cc9aa`](https://github.com/simple-robot/simpler-robot/commit/6110cc9aa): build: 调整构建配置
- [`6b2ddb25a`](https://github.com/simple-robot/simpler-robot/commit/6b2ddb25a): 版本配置调整
- [`a562b1161`](https://github.com/simple-robot/simpler-robot/commit/a562b1161): changelog调整
- [`d2eb9b0ee`](https://github.com/simple-robot/simpler-robot/commit/d2eb9b0ee): 清理配置
- [`6133e9bfb`](https://github.com/simple-robot/simpler-robot/commit/6133e9bfb): :white_check_mark: 添加测试代码
- [`82dc8550f..549727417`](https://github.com/simple-robot/simpler-robot/compare/82dc8550f..6133e9bfb): feat(spring): 内部注解处理改为优先使用Spring的AnnotationUtils
- [`14393250b`](https://github.com/simple-robot/simpler-robot/commit/14393250b): update: 版本更新到pre.18.1
- [`25e148ac4`](https://github.com/simple-robot/simpler-robot/commit/25e148ac4): fix(ci): 修复配置
- [`1dfc4094d`](https://github.com/simple-robot/simpler-robot/commit/1dfc4094d): fix(boot): 修复boot模块类扫描
- [`d598d125c`](https://github.com/simple-robot/simpler-robot/commit/d598d125c): update: 为监听函数的产生量计数
- [`c3d486cd8`](https://github.com/simple-robot/simpler-robot/commit/c3d486cd8): test: 提供服务测试相关内容的测试模块
- [`bbec85352`](https://github.com/simple-robot/simpler-robot/commit/bbec85352): refactor: 规范化配置
- [`edc3e7d35`](https://github.com/simple-robot/simpler-robot/commit/edc3e7d35): update: README.md
- [`2f1a3f305`](https://github.com/simple-robot/simpler-robot/commit/2f1a3f305): 项目配置结构调整
- [`2c61e170b`](https://github.com/simple-robot/simpler-robot/commit/2c61e170b): update: release 配置
- [`e31ba2964`](https://github.com/simple-robot/simpler-robot/commit/e31ba2964): update: 版本更新至 v3.0.0-beta-M1
- [`2ce1562bf..f28df497d`](https://github.com/simple-robot/simpler-robot/compare/2ce1562bf..e31ba2964): update(buildSrc): 更新调整buildSrc内容
- [`714e348a1`](https://github.com/simple-robot/simpler-robot/commit/714e348a1): update(buildSrc): 更新调整buildSrc文件结构
- [`ad8e7e421`](https://github.com/simple-robot/simpler-robot/commit/ad8e7e421): update: gradle config
- [`39b950025`](https://github.com/simple-robot/simpler-robot/commit/39b950025): 消除/修复警告
- [`62b67a702`](https://github.com/simple-robot/simpler-robot/commit/62b67a702): gradle 配置
- [`7e565b9ea..a80891923`](https://github.com/simple-robot/simpler-robot/compare/7e565b9ea..62b67a702): 整理 gradle build src
- [`b6b36131a`](https://github.com/simple-robot/simpler-robot/commit/b6b36131a): fix(simbot-api): 修复获取PlainText导致堆栈溢出的问题
- [`cb88b3b7e`](https://github.com/simple-robot/simpler-robot/commit/cb88b3b7e): 清理代码
- [`9fec6a56a`](https://github.com/simple-robot/simpler-robot/commit/9fec6a56a): simbot-boots 模块下相关依赖改造
- [`bfe802dc1`](https://github.com/simple-robot/simpler-robot/commit/bfe802dc1): 依赖调整、更新
- [`6b6e984e5`](https://github.com/simple-robot/simpler-robot/commit/6b6e984e5): simbot-core 模块下依赖管理改造
- [`cf9a6843b`](https://github.com/simple-robot/simpler-robot/commit/cf9a6843b): simbot-apis 模块下依赖管理改造
- [`2a5e86e69`](https://github.com/simple-robot/simpler-robot/commit/2a5e86e69): 弃用 `@Filter(target = TargetFilter(...))`, 以 `@Filter(targets = Filter.Targets(...))` 取代之
- [`35385ebab`](https://github.com/simple-robot/simpler-robot/commit/35385ebab): 版本调整至 `v3.v3.0.0.preview.18.0`
- [`f24c8c889..c3f69148f`](https://github.com/simple-robot/simpler-robot/compare/f24c8c889..35385ebab): 调整注释
- [`ed19e7b59`](https://github.com/simple-robot/simpler-robot/commit/ed19e7b59): fix: 清理遗留代码
- [`4350ef4e3`](https://github.com/simple-robot/simpler-robot/commit/4350ef4e3): update release config
- [`596a52c02`](https://github.com/simple-robot/simpler-robot/commit/596a52c02): 更新changelog
- [`3db8de77f..f1e26f977`](https://github.com/simple-robot/simpler-robot/compare/3db8de77f..596a52c02): Update release.yml
- [`9a19c8452`](https://github.com/simple-robot/simpler-robot/commit/9a19c8452): Release: v3.0.0.preview.17.1
- [`4c2d99f03`](https://github.com/simple-robot/simpler-robot/commit/4c2d99f03): GitHub release config
- [`527574e90`](https://github.com/simple-robot/simpler-robot/commit/527574e90): github release config
- [`ac8fe567d`](https://github.com/simple-robot/simpler-robot/commit/ac8fe567d): 改善SpringBoot下对于直接注册监听函数的处理
- [`d56f468dd`](https://github.com/simple-robot/simpler-robot/commit/d56f468dd): fix(boot): 修复过滤器注解处理器无法获取监听函数id的问题
- [`4a1dd2964`](https://github.com/simple-robot/simpler-robot/commit/4a1dd2964): build(project): 调整项目目录结构
- [`46ffe38d7`](https://github.com/simple-robot/simpler-robot/commit/46ffe38d7): perf(project): version to v3.0.0.preview.17.1
- [`8ebb95dfb`](https://github.com/simple-robot/simpler-robot/commit/8ebb95dfb): test依赖调整
- [`0c090c913`](https://github.com/simple-robot/simpler-robot/commit/0c090c913): 持续会话机制变更
- [`fd0f65c5d`](https://github.com/simple-robot/simpler-robot/commit/fd0f65c5d): Resource to Image
- [`1fc130a3f`](https://github.com/simple-robot/simpler-robot/commit/1fc130a3f): README.md
- [`cfc656b75`](https://github.com/simple-robot/simpler-robot/commit/cfc656b75): feat(component): `Component.id` 调整为String类型； feat(nullable): 细化部分函数的可空与不可空
- [`092d2b8c5`](https://github.com/simple-robot/simpler-robot/commit/092d2b8c5): fix(application): 修复Application关闭无效问题
- [`7befb6c19`](https://github.com/simple-robot/simpler-robot/commit/7befb6c19): 调整优化日志输出
- [`3bc5ede5d`](https://github.com/simple-robot/simpler-robot/commit/3bc5ede5d): ApplicationFactory会在未配置Job时补充Job
- [`41fc6ad24`](https://github.com/simple-robot/simpler-robot/commit/41fc6ad24): binder增加作用域和序列化模块
- [`403474e16`](https://github.com/simple-robot/simpler-robot/commit/403474e16): 为各种预设属性/作用域提供扩展
- [`a61504d41`](https://github.com/simple-robot/simpler-robot/commit/a61504d41): 为各种预设属性提供扩展
- [`4af518a0a`](https://github.com/simple-robot/simpler-robot/commit/4af518a0a): 提供并实现 `ApplicationAttributes` 约定属性
- [`3c158af32`](https://github.com/simple-robot/simpler-robot/commit/3c158af32): 为 Application 提供 botManager 属性
- [`c9c85ec1a`](https://github.com/simple-robot/simpler-robot/commit/c9c85ec1a): 使@Listener支持EventListenerBuilder和EventListener解析
- [`946c3f758`](https://github.com/simple-robot/simpler-robot/commit/946c3f758): @Listener支持EventListenerBuilder和EventListener解析
- [`ff8ffd6eb`](https://github.com/simple-robot/simpler-robot/commit/ff8ffd6eb): boot支持EventListenerBuilder
- [`8b3a7d700`](https://github.com/simple-robot/simpler-robot/commit/8b3a7d700): 提供 `EventListenerBuilder`
- [`ece52d2b5`](https://github.com/simple-robot/simpler-robot/commit/ece52d2b5): 调整ApplicationBuilder中的流程，bot的注册将会在Application完成后执行；
- [`4fec72717`](https://github.com/simple-robot/simpler-robot/commit/4fec72717): 清理注释
- [`a635f85ee`](https://github.com/simple-robot/simpler-robot/commit/a635f85ee): 为 `Switchable` 中的 `*Async` 函数增加 `Future` 返回值
- [`469187275`](https://github.com/simple-robot/simpler-robot/commit/469187275): 清理import
- [`8be095809`](https://github.com/simple-robot/simpler-robot/commit/8be095809): 移除测试
- [`c02e1cef3`](https://github.com/simple-robot/simpler-robot/commit/c02e1cef3): for DelayableCoroutineScope
- [`dfedeaf73..989db3f5e`](https://github.com/simple-robot/simpler-robot/compare/dfedeaf73..c02e1cef3): 实现新特性 DelayableCoroutineScope
- [`f6958422e`](https://github.com/simple-robot/simpler-robot/commit/f6958422e): 实现 SimpleListenerBuilder 并替换 EventListenersGenerator
- [`d849e57de`](https://github.com/simple-robot/simpler-robot/commit/d849e57de): 版本更至 v3.0.0.preview.17.0
- [`474384138`](https://github.com/simple-robot/simpler-robot/commit/474384138): 实现 SimpleListenerBuilder 并替换 EventListenersGenerator
- [`54b097d0b`](https://github.com/simple-robot/simpler-robot/commit/54b097d0b): 准备发布版本 v3.0.0.preview.16.0
- [`0c756149f`](https://github.com/simple-robot/simpler-robot/commit/0c756149f): 调整changelog生成描述
- [`a5ce5245b`](https://github.com/simple-robot/simpler-robot/commit/a5ce5245b): 调整事件处理的日志
- [`f24bb991c`](https://github.com/simple-robot/simpler-robot/commit/f24bb991c): 标记 `EventListener.logger` 为过时并计划删除
- [`3e701ef1d`](https://github.com/simple-robot/simpler-robot/commit/3e701ef1d): 移除 `EventListener` 的 `IDContainer` 实现，并变更 `EventListener.id` 类型为 `String`
- [`e6f9f61a1`](https://github.com/simple-robot/simpler-robot/commit/e6f9f61a1): 清理或调整部分TODO
- [`450517f61`](https://github.com/simple-robot/simpler-robot/commit/450517f61): 函数接口重命名
- [`0541cca09`](https://github.com/simple-robot/simpler-robot/commit/0541cca09): 补充持续会话注释
- [`fdc4ac30c`](https://github.com/simple-robot/simpler-robot/commit/fdc4ac30c): 调整c继续会话api
- [`2c410e50a`](https://github.com/simple-robot/simpler-robot/commit/2c410e50a): Scope移动为Core模块特性
- [`ae9511fa7..87d22dcc4`](https://github.com/simple-robot/simpler-robot/compare/ae9511fa7..2c410e50a): 将所有Core相关的内容重命名为Simple
- [`6a73d81aa..fdd4b94a4`](https://github.com/simple-robot/simpler-robot/compare/6a73d81aa..87d22dcc4): 持续会话思考
- [`ce1c25704`](https://github.com/simple-robot/simpler-robot/commit/ce1c25704): 版本更新到 v3.0.0.preview.16.x
- [`029857389`](https://github.com/simple-robot/simpler-robot/commit/029857389): 注解过滤器工厂
- [`4ffd44fb1`](https://github.com/simple-robot/simpler-robot/commit/4ffd44fb1): 准备changelog
- [`28d956c98..6379535bf`](https://github.com/simple-robot/simpler-robot/compare/28d956c98..4ffd44fb1): 性能测试
- [`9e14848aa`](https://github.com/simple-robot/simpler-robot/commit/9e14848aa): 清理测试文件
- [`31b6440a1..1b3c36c39`](https://github.com/simple-robot/simpler-robot/compare/31b6440a1..9e14848aa): 调整测试报告
- [`d74152c70`](https://github.com/simple-robot/simpler-robot/commit/d74152c70): 弃用Filter.or 和 Filter.and；提供新的注解过滤器工厂
- [`a9d023b3a`](https://github.com/simple-robot/simpler-robot/commit/a9d023b3a): 依赖版本更新: Kotlinx Serialization `v1.3.1` -> `v1.3.3`
- [`3dd314527`](https://github.com/simple-robot/simpler-robot/commit/3dd314527): 依赖版本更新: Kotlinx Coroutines `v1.6.1` -> `v1.6.2`
- [`9f5b38788`](https://github.com/simple-robot/simpler-robot/commit/9f5b38788): 使用testng
- [`60cc04c45`](https://github.com/simple-robot/simpler-robot/commit/60cc04c45): ID相关更新；增加JMH性能测试报告；
- [`6001342f8`](https://github.com/simple-robot/simpler-robot/commit/6001342f8): 依赖版本更新: Kotlin `v1.6.10` -> `v1.6.21`
- [`1bb6a371e`](https://github.com/simple-robot/simpler-robot/commit/1bb6a371e): ResourceImage消除警告
- [`c6068ee67`](https://github.com/simple-robot/simpler-robot/commit/c6068ee67): 清理代码
- [`2731e059b`](https://github.com/simple-robot/simpler-robot/commit/2731e059b): `Image.asImage` -> `Image.toImage`
- [`ac15f4fc7`](https://github.com/simple-robot/simpler-robot/commit/ac15f4fc7): 消除警告
- [`58e841585`](https://github.com/simple-robot/simpler-robot/commit/58e841585): 调整 MessageBuilder 内Image相关内容
- [`bb03e96f8`](https://github.com/simple-robot/simpler-robot/commit/bb03e96f8): 调整Image相关API
- [`3509da1ac`](https://github.com/simple-robot/simpler-robot/commit/3509da1ac): 调整优化ID相关API
- [`ab038e599`](https://github.com/simple-robot/simpler-robot/commit/ab038e599): 移除UserStatus及相关内容
- [`400fe2d76`](https://github.com/simple-robot/simpler-robot/commit/400fe2d76): Role API
- [`255b06e55`](https://github.com/simple-robot/simpler-robot/commit/255b06e55): 核心版本更新到 preview.15.0
- [`5715f3998`](https://github.com/simple-robot/simpler-robot/commit/5715f3998): 更新README
- [`01d85b87e`](https://github.com/simple-robot/simpler-robot/commit/01d85b87e): 准备发布 v3.0.0.preview.14.0
- [`6eeb18d55`](https://github.com/simple-robot/simpler-robot/commit/6eeb18d55): 优化描述
- [`21a50cced`](https://github.com/simple-robot/simpler-robot/commit/21a50cced): 更新优化ID相关内容
- [`336be3db2`](https://github.com/simple-robot/simpler-robot/commit/336be3db2): README.md 更新
- [`1db756b13`](https://github.com/simple-robot/simpler-robot/commit/1db756b13): 补充注释
- [`cc9c47bc6`](https://github.com/simple-robot/simpler-robot/commit/cc9c47bc6): 为Items实现Stream相关API
- [`2a4357e17`](https://github.com/simple-robot/simpler-robot/commit/2a4357e17): 为Items准备Stream相关API
- [`798f1b409`](https://github.com/simple-robot/simpler-robot/commit/798f1b409): 为Items提供Sequence相关API
- [`43cd88f88`](https://github.com/simple-robot/simpler-robot/commit/43cd88f88): 将 UserStatus 标记为 '实验性' 并待议。
- [`2ad234da5`](https://github.com/simple-robot/simpler-robot/commit/2ad234da5): 简单调整
- [`9eea7093e`](https://github.com/simple-robot/simpler-robot/commit/9eea7093e): 优化补充注释信息
- [`d73ad4c4f`](https://github.com/simple-robot/simpler-robot/commit/d73ad4c4f): 改善优化持续会话相关API、提供部分扩展
- [`6723f07b0`](https://github.com/simple-robot/simpler-robot/commit/6723f07b0): 调整优化 `SimbootApp` 部分API
- [`8fa9d80cc`](https://github.com/simple-robot/simpler-robot/commit/8fa9d80cc): `Member` 实现 `Contact`
- [`ebd1af3bf`](https://github.com/simple-robot/simpler-robot/commit/ebd1af3bf): 补充注释
- [`3923d0f38`](https://github.com/simple-robot/simpler-robot/commit/3923d0f38): 调整 `DeleteSupport` 的实现
- [`09224096f`](https://github.com/simple-robot/simpler-robot/commit/09224096f): 重构 `OrganizationBot` 及其子类
- [`2f8e0af14`](https://github.com/simple-robot/simpler-robot/commit/2f8e0af14): update readme
- [`2fc3e6571`](https://github.com/simple-robot/simpler-robot/commit/2fc3e6571): Create CONTRIBUTING.md
- [`405aa9e86`](https://github.com/simple-robot/simpler-robot/commit/405aa9e86): Create CODE_OF_CONDUCT.md
- [`ba714b791`](https://github.com/simple-robot/simpler-robot/commit/ba714b791): `Objectives` 重命名为 `Objective`
- [`0258bdcdf`](https://github.com/simple-robot/simpler-robot/commit/0258bdcdf): 调整Bot社交关系容器的实现；补充注释；
- [`5e744ee81..2b15434c1`](https://github.com/simple-robot/simpler-robot/compare/5e744ee81..0258bdcdf): Update question.yml
- [`98e178bc8`](https://github.com/simple-robot/simpler-robot/commit/98e178bc8): Update bug-report.yml
- [`909402ee3`](https://github.com/simple-robot/simpler-robot/commit/909402ee3): 对 Timestamp 的API进行调整改造
- [`c8a83dad7`](https://github.com/simple-robot/simpler-robot/commit/c8a83dad7): 对 Timestamp 的调整
- [`1e939d47c`](https://github.com/simple-robot/simpler-robot/commit/1e939d47c): preview.14.x: Bot.contact api
- [`07e4639c9`](https://github.com/simple-robot/simpler-robot/commit/07e4639c9): 调整部署任务流程
- [`aa1f210fd..43ee27aef`](https://github.com/simple-robot/simpler-robot/compare/aa1f210fd..07e4639c9): 更新 v3.0.0.preview.13.0
- [`14b0e1831..a976859cb`](https://github.com/simple-robot/simpler-robot/compare/14b0e1831..43ee27aef): 调整 UserStatus 相关API
- [`d107608b7`](https://github.com/simple-robot/simpler-robot/commit/d107608b7): 算了，没有必要
- [`df88e3ba5`](https://github.com/simple-robot/simpler-robot/commit/df88e3ba5): ListenerGenerator 扩展
- [`88997d6f4`](https://github.com/simple-robot/simpler-robot/commit/88997d6f4): 更新优化 Items API
- [`3dc3e510b`](https://github.com/simple-robot/simpler-robot/commit/3dc3e510b): 更新到 preview.13.0
- [`15ce30ae1..3091b0277`](https://github.com/simple-robot/simpler-robot/compare/15ce30ae1..3dc3e510b): Update timeout-issue.yml
- [`ac9d3270e`](https://github.com/simple-robot/simpler-robot/commit/ac9d3270e): 更新到 preview.12.1
- [`1a73e730c`](https://github.com/simple-robot/simpler-robot/commit/1a73e730c): 准备发布版本
- [`126f7c29a`](https://github.com/simple-robot/simpler-robot/commit/126f7c29a): 构建函数
- [`2be3bc581`](https://github.com/simple-robot/simpler-robot/commit/2be3bc581): 转化函数
- [`db423be6b..927a00597`](https://github.com/simple-robot/simpler-robot/compare/db423be6b..2be3bc581): 增加构建Items的扩展函数
- [`74a63d58b..8462aeffc`](https://github.com/simple-robot/simpler-robot/compare/74a63d58b..927a00597): 重构实现Items取代Flow或其他序列API
- [`d606b5279`](https://github.com/simple-robot/simpler-robot/commit/d606b5279): preview.12
- [`83a9d1036`](https://github.com/simple-robot/simpler-robot/commit/83a9d1036): 更新快照规则
- [`54517dd06..804fe8b0b`](https://github.com/simple-robot/simpler-robot/compare/54517dd06..83a9d1036): 发布 v3.0.0.preview.11.1
- [`ab0c5b032`](https://github.com/simple-robot/simpler-robot/commit/ab0c5b032): 修复配置
- [`a119fe6dd`](https://github.com/simple-robot/simpler-robot/commit/a119fe6dd): 补充注释
- [`53477c2e7`](https://github.com/simple-robot/simpler-robot/commit/53477c2e7): 修复CI配置
- [`cb9d30c13`](https://github.com/simple-robot/simpler-robot/commit/cb9d30c13): 尝试修复 #310; 补充注释
- [`425392b0a`](https://github.com/simple-robot/simpler-robot/commit/425392b0a): Update timeout-issue.yml
- [`8915f09c6`](https://github.com/simple-robot/simpler-robot/commit/8915f09c6): 调整/补充描述
- [`2b73413e6..3850fda36`](https://github.com/simple-robot/simpler-robot/compare/2b73413e6..8915f09c6): Update timeout-issue.yml
- [`196ef9418`](https://github.com/simple-robot/simpler-robot/commit/196ef9418): 预更新版本到pre.11.1
- [`a441bff8d`](https://github.com/simple-robot/simpler-robot/commit/a441bff8d): 更新 v3.0.0.preview.11.0
- [`00a25bbca`](https://github.com/simple-robot/simpler-robot/commit/00a25bbca): 补充、调整注释
- [`a67664ad4`](https://github.com/simple-robot/simpler-robot/commit/a67664ad4): Update issue-waiting-report.yml
- [`31112ab69`](https://github.com/simple-robot/simpler-robot/commit/31112ab69): 统一 send、reply、react 相关api的返回值，使他们都为 MessageReceipt 类型。
- [`bb70ba08e`](https://github.com/simple-robot/simpler-robot/commit/bb70ba08e): 补充调整注释描述
- [`5b5bfdac1`](https://github.com/simple-robot/simpler-robot/commit/5b5bfdac1): 补充调整注释
- [`6a446f13f`](https://github.com/simple-robot/simpler-robot/commit/6a446f13f): 补充注释
- [`a2cdddf92..34126f260`](https://github.com/simple-robot/simpler-robot/compare/a2cdddf92..6a446f13f): 使 MessageEvent 默认实现 ReplySupport
- [`6c6f01f7f`](https://github.com/simple-robot/simpler-robot/commit/6c6f01f7f): Deprecated 'sendIfSupportBlocking' in contact
- [`ae47e8177`](https://github.com/simple-robot/simpler-robot/commit/ae47e8177): 调整 EventListenerProcessingContext.eventResult 内联函数位置到 EventListenersGenerator 处
- [`39705c8f3`](https://github.com/simple-robot/simpler-robot/commit/39705c8f3): feat(api): 事件构建: onMatch / async
- [`2c21f1394`](https://github.com/simple-robot/simpler-robot/commit/2c21f1394): 其他api？
- [`8823ad2cd`](https://github.com/simple-robot/simpler-robot/commit/8823ad2cd): 补充注释
- [`9f5a223ef`](https://github.com/simple-robot/simpler-robot/commit/9f5a223ef): 为 EventListenersGenerator 及其衍生提供更多实用api
- [`1ad06d162`](https://github.com/simple-robot/simpler-robot/commit/1ad06d162): fix(api): 修复EventListenersGenerator中match函数合并逻辑错误问题
- [`ea27aec5e..1d52d679d`](https://github.com/simple-robot/simpler-robot/compare/ea27aec5e..1ad06d162): fix(api): 调整 `Preparator` 相关名称为 `Preparer`
- [`b31294c1b`](https://github.com/simple-robot/simpler-robot/commit/b31294c1b): Update issue-waiting-report.yml
- [`ecfb5d1aa`](https://github.com/simple-robot/simpler-robot/commit/ecfb5d1aa): Update timeout-issue.yml
- [`78e671eef`](https://github.com/simple-robot/simpler-robot/commit/78e671eef): Create issue-handle.yml
- [`c2623c0dd`](https://github.com/simple-robot/simpler-robot/commit/c2623c0dd): 其他api？
- [`d3224ec01`](https://github.com/simple-robot/simpler-robot/commit/d3224ec01): 补充注释
- [`030ede8e2`](https://github.com/simple-robot/simpler-robot/commit/030ede8e2): 为 EventListenersGenerator 及其衍生提供更多实用api
- [`3dd158348`](https://github.com/simple-robot/simpler-robot/commit/3dd158348): 调整文件结构
- [`b8c481f17`](https://github.com/simple-robot/simpler-robot/commit/b8c481f17): 调整 BotMember 名称为 MemberBot。
- [`d493ab306`](https://github.com/simple-robot/simpler-robot/commit/d493ab306): 调整botMember相关属性值
- [`d0b1dbe32`](https://github.com/simple-robot/simpler-robot/commit/d0b1dbe32): 版本修改至pre.11.0
- [`7e38f1d74..d26bffeec`](https://github.com/simple-robot/simpler-robot/compare/7e38f1d74..d0b1dbe32): Update timeout-issue.yml
- [`ba5c36993`](https://github.com/simple-robot/simpler-robot/commit/ba5c36993): Create timeout-issue.yml
- [`3af0ac78a..a2e8c3a02`](https://github.com/simple-robot/simpler-robot/compare/3af0ac78a..ba5c36993): Update issue-waiting-report.yml
- [`3b0fa2cf3`](https://github.com/simple-robot/simpler-robot/commit/3b0fa2cf3): Create issue-waiting-report.yml
- [`76bc60909`](https://github.com/simple-robot/simpler-robot/commit/76bc60909): 提供 BotMember 类型实现
- [`550cf3e7e..9d6efdc78`](https://github.com/simple-robot/simpler-robot/compare/550cf3e7e..76bc60909): 完善mute api的描述
- [`8a8f0cad6`](https://github.com/simple-robot/simpler-robot/commit/8a8f0cad6): 调整禁言api的描述与约束
- [`ee79a0316`](https://github.com/simple-robot/simpler-robot/commit/ee79a0316): 版本修改至pre.11.0
- [`c9f9e7f32..22361c4ab`](https://github.com/simple-robot/simpler-robot/compare/c9f9e7f32..ee79a0316): Update bug-report.yml
- [`538dcc65a`](https://github.com/simple-robot/simpler-robot/commit/538dcc65a): Update question.yml
- [`c72596b89`](https://github.com/simple-robot/simpler-robot/commit/c72596b89): Create labeler.yml
- [`a33c4a967`](https://github.com/simple-robot/simpler-robot/commit/a33c4a967): Update advice.yml
- [`a097936e1`](https://github.com/simple-robot/simpler-robot/commit/a097936e1): Update show.yml
- [`37bd3c8c7`](https://github.com/simple-robot/simpler-robot/commit/37bd3c8c7): Update question.yml
- [`1a5e556c4..f67f33d94`](https://github.com/simple-robot/simpler-robot/compare/1a5e556c4..37bd3c8c7): Update bug-report.yml
- [`175c70eea`](https://github.com/simple-robot/simpler-robot/commit/175c70eea): Update question.yml
- [`e0829bec9`](https://github.com/simple-robot/simpler-robot/commit/e0829bec9): Update advice.yml
- [`8c0ea8e52..532174eeb`](https://github.com/simple-robot/simpler-robot/compare/8c0ea8e52..e0829bec9): Update question.yml
- [`d764c8fc3..1b0d8a0e1`](https://github.com/simple-robot/simpler-robot/compare/d764c8fc3..532174eeb): Update bug-report.yml
- [`0b0c404e2`](https://github.com/simple-robot/simpler-robot/commit/0b0c404e2): update README.md
- [`fb550e36f`](https://github.com/simple-robot/simpler-robot/commit/fb550e36f): 更新版本 v3.0.0.preview.10.2
- [`20890003c..453bd7918`](https://github.com/simple-robot/simpler-robot/compare/20890003c..fb550e36f): 补充注释
- [`78dfb4907`](https://github.com/simple-robot/simpler-robot/commit/78dfb4907): 为 ListenerPreparator 提供阻塞兼容；补充注释
- [`691800198`](https://github.com/simple-robot/simpler-robot/commit/691800198): 移除无用目录
- [`26b18e23a`](https://github.com/simple-robot/simpler-robot/commit/26b18e23a): 下一个版本
- [`568ae9a7a`](https://github.com/simple-robot/simpler-robot/commit/568ae9a7a): 更新 v3.0.0.preview.10.1
- [`617a73ec2`](https://github.com/simple-robot/simpler-robot/commit/617a73ec2): 尝试修复@FilterValue得不到对应attributes的问题
- [`e6c6dc5a2`](https://github.com/simple-robot/simpler-robot/commit/e6c6dc5a2): 阻塞函数增加 runWithInterruptible
- [`30c54a139`](https://github.com/simple-robot/simpler-robot/commit/30c54a139): CodeListener过时标记
- [`1543f2884`](https://github.com/simple-robot/simpler-robot/commit/1543f2884): SimpleListeners
- [`5a44a1995`](https://github.com/simple-robot/simpler-robot/commit/5a44a1995): 版本预先调整到 pre10.1
- [`9f052ceec`](https://github.com/simple-robot/simpler-robot/commit/9f052ceec): 更新版本 v3.0.0.preview.10.0
- [`e2483dc79`](https://github.com/simple-robot/simpler-robot/commit/e2483dc79): nextMessages -> nextMessage
- [`d89922adc`](https://github.com/simple-robot/simpler-robot/commit/d89922adc): session.nextMessages -> nextMessage
- [`b2b31555e`](https://github.com/simple-robot/simpler-robot/commit/b2b31555e): 优化ParameterBinder.Context部分API
- [`ae8b28f33`](https://github.com/simple-robot/simpler-robot/commit/ae8b28f33): SimpleListenerBuilder
- [`47e11355b`](https://github.com/simple-robot/simpler-robot/commit/47e11355b): 持续会话API优化
- [`1e3f9d0a1`](https://github.com/simple-robot/simpler-robot/commit/1e3f9d0a1): 拦截器扫描加载
- [`fb5f4d851`](https://github.com/simple-robot/simpler-robot/commit/fb5f4d851): 拦截器重建: point
- [`251ee2bb5`](https://github.com/simple-robot/simpler-robot/commit/251ee2bb5): interceptor for spring boot starter
- [`ef1714eca`](https://github.com/simple-robot/simpler-robot/commit/ef1714eca): 隐藏bug
- [`dc28a7b15`](https://github.com/simple-robot/simpler-robot/commit/dc28a7b15): 调整接口内抽象
- [`943c1ea1e`](https://github.com/simple-robot/simpler-robot/commit/943c1ea1e): 监听函数重构/监听准备器
- [`26e2f9899`](https://github.com/simple-robot/simpler-robot/commit/26e2f9899): Interceptor s
- [`8f1e877e4`](https://github.com/simple-robot/simpler-robot/commit/8f1e877e4): EventListenersGenerator
- [`8a62ea84d`](https://github.com/simple-robot/simpler-robot/commit/8a62ea84d): 监听函数构建器
- [`5b3d6db48`](https://github.com/simple-robot/simpler-robot/commit/5b3d6db48): 新的默认监听函数实现/matchable
- [`bd12ba30c`](https://github.com/simple-robot/simpler-robot/commit/bd12ba30c): 监听函数重构
- [`8115d3785`](https://github.com/simple-robot/simpler-robot/commit/8115d3785): 版本 to 10.0
- [`94ef89263`](https://github.com/simple-robot/simpler-robot/commit/94ef89263): matcher..?
- [`0b70e34a2`](https://github.com/simple-robot/simpler-robot/commit/0b70e34a2): 更新 v3.0.0.preview.9.1
- [`7f051dfac`](https://github.com/simple-robot/simpler-robot/commit/7f051dfac): 监听函数与拦截器调整：matchable
- [`2c8074f4a`](https://github.com/simple-robot/simpler-robot/commit/2c8074f4a): 拦截器: before Filter
- [`d6c2200c8`](https://github.com/simple-robot/simpler-robot/commit/d6c2200c8): fix(spring-boot-starter): 尝试修复动态代理目标类获取问题 #281
- [`ef71cbfed`](https://github.com/simple-robot/simpler-robot/commit/ef71cbfed): fix(spring-boot-starter): 尝试修复动态代理目标类获取问题
- [`6600aeabd`](https://github.com/simple-robot/simpler-robot/commit/6600aeabd): fix(spring-boot-starter): 尝试修复动态代理目标类获取问题&配置快照发布策略
- [`a866d7fe2`](https://github.com/simple-robot/simpler-robot/commit/a866d7fe2): fix(spring-boot-starter): 尝试修复动态代理目标类获取问题 #281
- [`39c04878d`](https://github.com/simple-robot/simpler-robot/commit/39c04878d): 删除无用文件
- [`33c19990a`](https://github.com/simple-robot/simpler-robot/commit/33c19990a): readme更新
- [`ffd0eadba`](https://github.com/simple-robot/simpler-robot/commit/ffd0eadba): feat(api): BotSocialRelationsContainer
- [`6e6d91af0`](https://github.com/simple-robot/simpler-robot/commit/6e6d91af0): refactor(api): 移除 BlockingClearTargetResumeListener
- [`54cacff86`](https://github.com/simple-robot/simpler-robot/commit/54cacff86): feat(api): 持续会话API
- [`1526fbb00`](https://github.com/simple-robot/simpler-robot/commit/1526fbb00): refactor(spring-boot-starter): 简单调整
- [`4e48cb099`](https://github.com/simple-robot/simpler-robot/commit/4e48cb099): runInBlocking相关
- [`7700c8463..0f7db353a`](https://github.com/simple-robot/simpler-robot/compare/7700c8463..4e48cb099): 持续会话API #238
- [`22972503a..61e67b746`](https://github.com/simple-robot/simpler-robot/compare/22972503a..0f7db353a): BlockingRunner 调整
- [`108244d1a`](https://github.com/simple-robot/simpler-robot/commit/108244d1a): 标记待办
- [`215241a37`](https://github.com/simple-robot/simpler-robot/commit/215241a37): fix delete if support
- [`05d08d2fb`](https://github.com/simple-robot/simpler-robot/commit/05d08d2fb): 优化日志
- [`e38e53442`](https://github.com/simple-robot/simpler-robot/commit/e38e53442): fix filter
- [`502d26ac9`](https://github.com/simple-robot/simpler-robot/commit/502d26ac9): session context 4j return type
- [`b8fb6cc17`](https://github.com/simple-robot/simpler-robot/commit/b8fb6cc17): session context 4j
- [`e4cc28ec4`](https://github.com/simple-robot/simpler-robot/commit/e4cc28ec4): class loader.
- [`b573ca006`](https://github.com/simple-robot/simpler-robot/commit/b573ca006): The Boot classloader configuration
- [`a3256a818`](https://github.com/simple-robot/simpler-robot/commit/a3256a818): Boot模块的自动扫描
- [`03eef71d6`](https://github.com/simple-robot/simpler-robot/commit/03eef71d6): The build src
- [`875b29805`](https://github.com/simple-robot/simpler-robot/commit/875b29805): Remove a submodule.
- [`73387f222`](https://github.com/simple-robot/simpler-robot/commit/73387f222): The build src
- [`26dde0880`](https://github.com/simple-robot/simpler-robot/commit/26dde0880): Remove a submodule
- [`19350a64c`](https://github.com/simple-robot/simpler-robot/commit/19350a64c): Session Context snap
- [`c575e4e7b`](https://github.com/simple-robot/simpler-robot/commit/c575e4e7b): snapshot for other branches
- [`785009adc`](https://github.com/simple-robot/simpler-robot/commit/785009adc): preview.10.0
- [`6553865b8`](https://github.com/simple-robot/simpler-robot/commit/6553865b8): 修改readme和changelog
- [`70a37e6cf`](https://github.com/simple-robot/simpler-robot/commit/70a37e6cf): 兼容性过时标记
- [`1975c129f`](https://github.com/simple-robot/simpler-robot/commit/1975c129f): 修复auto register bot重复启动的问题
- [`28e5badb9`](https://github.com/simple-robot/simpler-robot/commit/28e5badb9): Boot about
- [`39ef6740b`](https://github.com/simple-robot/simpler-robot/commit/39ef6740b): changelog
- [`732cfa7ed..4723686be`](https://github.com/simple-robot/simpler-robot/compare/732cfa7ed..39ef6740b): 调整部分内部实现
- [`c6e279078`](https://github.com/simple-robot/simpler-robot/commit/c6e279078): 调整注释、调整部分实现
- [`2a206178d`](https://github.com/simple-robot/simpler-robot/commit/2a206178d): 简单调整
- [`743f3fce8`](https://github.com/simple-robot/simpler-robot/commit/743f3fce8): 调整构建listener时的 handle 函数，将 context 作为接收者；修复部分内容
- [`e493da9a7`](https://github.com/simple-robot/simpler-robot/commit/e493da9a7): 简单更新
- [`7ed19476e`](https://github.com/simple-robot/simpler-robot/commit/7ed19476e): 简单优化事件注册器
- [`8f321c54d`](https://github.com/simple-robot/simpler-robot/commit/8f321c54d): fix install bug
- [`106a782b6`](https://github.com/simple-robot/simpler-robot/commit/106a782b6): application内异步改造、简单调整、补充注释
- [`b65bcb54d`](https://github.com/simple-robot/simpler-robot/commit/b65bcb54d): 应用程序内部启动全异步改造
- [`b43cf2ab8`](https://github.com/simple-robot/simpler-robot/commit/b43cf2ab8): Application Launcher
- [`f6b75df0d`](https://github.com/simple-robot/simpler-robot/commit/f6b75df0d): Fix SpreadOperator
- [`0ce7427d5`](https://github.com/simple-robot/simpler-robot/commit/0ce7427d5): the version to pre.9.0
- [`09fff5280`](https://github.com/simple-robot/simpler-robot/commit/09fff5280): ItemFlow
- [`4c3480fc5..f7a3ceb9f`](https://github.com/simple-robot/simpler-robot/compare/4c3480fc5..09fff5280): annotation event filter
- [`1f63453f4`](https://github.com/simple-robot/simpler-robot/commit/1f63453f4): Item Flow
- [`cab1a6111`](https://github.com/simple-robot/simpler-robot/commit/cab1a6111): BaseSequence
- [`7cf30077f`](https://github.com/simple-robot/simpler-robot/commit/7cf30077f): Item Sequence and Item Flow
- [`3a436efa0`](https://github.com/simple-robot/simpler-robot/commit/3a436efa0): The Spring Boot App
- [`f6f41aec6`](https://github.com/simple-robot/simpler-robot/commit/f6f41aec6): Boot相关模块下为listener提供部分原始属性支持
- [`08be90751`](https://github.com/simple-robot/simpler-robot/commit/08be90751): SpringBoot app
- [`2a20fb2f2`](https://github.com/simple-robot/simpler-robot/commit/2a20fb2f2): Binder For MessageValue
- [`8bf909bf3`](https://github.com/simple-robot/simpler-robot/commit/8bf909bf3): 挖坑
- [`0e0d537bb`](https://github.com/simple-robot/simpler-robot/commit/0e0d537bb): The Binder
- [`4f6496513`](https://github.com/simple-robot/simpler-robot/commit/4f6496513): The Boot application.
- [`b14fff70c`](https://github.com/simple-robot/simpler-robot/commit/b14fff70c): The Boot
- [`977ada715..1dd04bcdb`](https://github.com/simple-robot/simpler-robot/compare/977ada715..b14fff70c): 阶段性更新
- [`8fd0366a1`](https://github.com/simple-robot/simpler-robot/commit/8fd0366a1): Message extra
- [`e46147a9c`](https://github.com/simple-robot/simpler-robot/commit/e46147a9c): The Binder
- [`97173f2ab`](https://github.com/simple-robot/simpler-robot/commit/97173f2ab): 消息序列化相关
- [`b234042dc`](https://github.com/simple-robot/simpler-robot/commit/b234042dc): Boot Application
- [`c8858a879`](https://github.com/simple-robot/simpler-robot/commit/c8858a879): Create config.yml
- [`d0a1d1a79`](https://github.com/simple-robot/simpler-robot/commit/d0a1d1a79): Boot, and Spring Boot
- [`bf28cac14`](https://github.com/simple-robot/simpler-robot/commit/bf28cac14): For Spring boot and ..
- [`b5b5026e2`](https://github.com/simple-robot/simpler-robot/commit/b5b5026e2): build listener manager
- [`0e2c15742`](https://github.com/simple-robot/simpler-robot/commit/0e2c15742): 调整 `EventListenerManager` 的定义，包括Scope、Context等。
- [`c91b1507c..8ba3b6792`](https://github.com/simple-robot/simpler-robot/compare/c91b1507c..0e2c15742): 为 `Boot` 提供 Application 实现。
- [`fe7f24059`](https://github.com/simple-robot/simpler-robot/commit/fe7f24059): 文件内简单调整
- [`f23a1e402`](https://github.com/simple-robot/simpler-robot/commit/f23a1e402): Builder and interface
- [`33b9a8336`](https://github.com/simple-robot/simpler-robot/commit/33b9a8336): 与Job相关的简单调整
- [`2db193c9a`](https://github.com/simple-robot/simpler-robot/commit/2db193c9a): 增加 Completable 接口并使 `ApplicationBuilder` 实现以提供完成回调；  `ApplicationBuilder.install(...)` 中的 `configurator` 函数增加一个函数参数 `perceivable: CompletionPerceivable<A>` 来允许注册配置的时候额外注册回调函数。
- [`54e9e4f04`](https://github.com/simple-robot/simpler-robot/commit/54e9e4f04): Application.Environment中提供序列化模块相关api
- [`5b505f4d5..69bcb4511`](https://github.com/simple-robot/simpler-robot/compare/5b505f4d5..54e9e4f04): 从 `simboot-api` 中排除 `simbot-logger`
- [`a84666ffd..b965604b7`](https://github.com/simple-robot/simpler-robot/compare/a84666ffd..69bcb4511): 只在 `simbot-core`、`simboot-core` 中传递使用 `simbot-logger`
- [`e172f029e`](https://github.com/simple-robot/simpler-robot/commit/e172f029e): 在 spring boot starter 中默认使用 spring-boot-starter-logging
- [`aafa248ae`](https://github.com/simple-robot/simpler-robot/commit/aafa248ae): 更新kotlin到1.6.10
- [`8f6a4c9dc`](https://github.com/simple-robot/simpler-robot/commit/8f6a4c9dc): 清理未使用的版本信息; 更新部署脚本配置
- [`5140e40e0`](https://github.com/simple-robot/simpler-robot/commit/5140e40e0): 更新ktx.coroutines版本到1.6.1
- [`2f649df8b`](https://github.com/simple-robot/simpler-robot/commit/2f649df8b): 清理test
- [`bc69e457b`](https://github.com/simple-robot/simpler-robot/commit/bc69e457b): 版本发布 v3.0.0.preview.8.0
- [`bccce90c0`](https://github.com/simple-robot/simpler-robot/commit/bccce90c0): 变更调用
- [`6de353ad5..0395af0fb`](https://github.com/simple-robot/simpler-robot/compare/6de353ad5..bccce90c0): 提供 `Resource` 的子类型 `DeserializableResource` 并将其与 `BotVerifyInfo` 结合。
- [`f5cc86950`](https://github.com/simple-robot/simpler-robot/commit/f5cc86950): 调整 Resource 的部分API
- [`51e7fd052`](https://github.com/simple-robot/simpler-robot/commit/51e7fd052): Update advice.yml
- [`7fa6fbe58`](https://github.com/simple-robot/simpler-robot/commit/7fa6fbe58): Update show.yml
- [`498c7eae9`](https://github.com/simple-robot/simpler-robot/commit/498c7eae9): Update question.yml
- [`10b8d1c8a..20ab5e523`](https://github.com/simple-robot/simpler-robot/compare/10b8d1c8a..498c7eae9): Update bug-report.yml
- [`740bd1951`](https://github.com/simple-robot/simpler-robot/commit/740bd1951): 重写 BotVerifyInfo
- [`da9cd54a5`](https://github.com/simple-robot/simpler-robot/commit/da9cd54a5): 调整 `BotManager` 和 `OriginBotManager` 相关内容。
- [`f297846a9`](https://github.com/simple-robot/simpler-robot/commit/f297846a9): 变更 BotManager api: all() 返回值变更为 List<Bot>
- [`6d35c77bc`](https://github.com/simple-robot/simpler-robot/commit/6d35c77bc): 更新.ignore
- [`73ba592c7`](https://github.com/simple-robot/simpler-robot/commit/73ba592c7): SupportedBotVerificationType
- [`d5d6df95c`](https://github.com/simple-robot/simpler-robot/commit/d5d6df95c): update changelog
- [`5e83d6e68`](https://github.com/simple-robot/simpler-robot/commit/5e83d6e68): feat(implement SendSupport): 为Member、Friend等实现 SendSupport 接口
- [`22be7b9bf`](https://github.com/simple-robot/simpler-robot/commit/22be7b9bf): fix(Image.resource提供blocking api)
- [`b6a0dff4e`](https://github.com/simple-robot/simpler-robot/commit/b6a0dff4e): Request相关事件定义调整
- [`867234da5..6a6d5deb5`](https://github.com/simple-robot/simpler-robot/compare/867234da5..b6a0dff4e): Changelog
- [`dd46ba6c8`](https://github.com/simple-robot/simpler-robot/commit/dd46ba6c8): Event、MessageEvent部分内容定义调整，去除默认实现
- [`c841a499d`](https://github.com/simple-robot/simpler-robot/commit/c841a499d): 变更事件的重新定义
- [`480bafec8`](https://github.com/simple-robot/simpler-robot/commit/480bafec8): 准备更新日志
- [`ddaf3eacc`](https://github.com/simple-robot/simpler-robot/commit/ddaf3eacc): 更新版本到pre.8.0
- [`da1d99601`](https://github.com/simple-robot/simpler-robot/commit/da1d99601): Simple Application dsl api
- [`59e857395`](https://github.com/simple-robot/simpler-robot/commit/59e857395): Update advice.yml
- [`5e876c85d..d4c8a2df0`](https://github.com/simple-robot/simpler-robot/compare/5e876c85d..59e857395): Application Factory实现; 核心 Simple 工厂; fix some
- [`02620d709`](https://github.com/simple-robot/simpler-robot/commit/02620d709): Application Factory实现; 核心 Simple 工厂;
- [`0db62b995`](https://github.com/simple-robot/simpler-robot/commit/0db62b995): Application Factory
- [`e1690957e`](https://github.com/simple-robot/simpler-robot/commit/e1690957e): Application...?
- [`b1e956d2d`](https://github.com/simple-robot/simpler-robot/commit/b1e956d2d): 调整注释
- [`a37c019cc`](https://github.com/simple-robot/simpler-robot/commit/a37c019cc): clean import
- [`f74689ea1..298944e1d`](https://github.com/simple-robot/simpler-robot/compare/f74689ea1..a37c019cc): 调整API
- [`c68f9b517`](https://github.com/simple-robot/simpler-robot/commit/c68f9b517): 调整注释
- [`f3ace62c6`](https://github.com/simple-robot/simpler-robot/commit/f3ace62c6): Application steps
- [`0e4930b87`](https://github.com/simple-robot/simpler-robot/commit/0e4930b87): update ci config
- [`112a44ff8`](https://github.com/simple-robot/simpler-robot/commit/112a44ff8): 为 SingleOnlyMessage 提供toString函数实现约束
- [`1aead8265`](https://github.com/simple-robot/simpler-robot/commit/1aead8265): 更新CI配置
- [`604b6c7af`](https://github.com/simple-robot/simpler-robot/commit/604b6c7af): :rewind: 恢复版本号
- [`65fbecd28`](https://github.com/simple-robot/simpler-robot/commit/65fbecd28): 更新配置
- [`ba74a4411`](https://github.com/simple-robot/simpler-robot/commit/ba74a4411): logger common
- [`30a8ae919`](https://github.com/simple-robot/simpler-robot/commit/30a8ae919): simple test
- [`660f167cc..06a6bee96`](https://github.com/simple-robot/simpler-robot/compare/660f167cc..30a8ae919): 临时回退版本以发布快照
- [`220885234`](https://github.com/simple-robot/simpler-robot/commit/220885234): 准备BaseEvent
- [`f0504881f`](https://github.com/simple-robot/simpler-robot/commit/f0504881f): 调整配置
- [`9af614963`](https://github.com/simple-robot/simpler-robot/commit/9af614963): 调整 changelog 生成规则
- [`22861752a`](https://github.com/simple-robot/simpler-robot/commit/22861752a): 新的 changelog 生成规则
- [`ffc675e02`](https://github.com/simple-robot/simpler-robot/commit/ffc675e02): update copyright and fix
- [`65ca3986d`](https://github.com/simple-robot/simpler-robot/commit/65ca3986d): update copyright;
- [`c716688e3`](https://github.com/simple-robot/simpler-robot/commit/c716688e3): NEXT VERSION
- [`cb396e9e0`](https://github.com/simple-robot/simpler-robot/commit/cb396e9e0): Spring boot version update
- [`25dc23499`](https://github.com/simple-robot/simpler-robot/commit/25dc23499): remove some test file
- [`f46d84a59`](https://github.com/simple-robot/simpler-robot/commit/f46d84a59): 版本发布
- [`95589cc7f`](https://github.com/simple-robot/simpler-robot/commit/95589cc7f): 更新版本 pre.7.0
- [`d8baf2ece`](https://github.com/simple-robot/simpler-robot/commit/d8baf2ece): boot api提供 ComponentRegistryConfigure 抽象配置类以实现自定义组件注册
- [`e40dedc04`](https://github.com/simple-robot/simpler-robot/commit/e40dedc04): 优化Messages下相关内容效果，例如toString等。
- [`ccd470388`](https://github.com/simple-robot/simpler-robot/commit/ccd470388): ID and test
- [`f5e8e9b44..f11a2fb00`](https://github.com/simple-robot/simpler-robot/compare/f5e8e9b44..ccd470388): ID
- [`329de6e1f`](https://github.com/simple-robot/simpler-robot/commit/329de6e1f): Bonus!
- [`b2559c244`](https://github.com/simple-robot/simpler-robot/commit/b2559c244): 更新版本
- [`ba45623b9`](https://github.com/simple-robot/simpler-robot/commit/ba45623b9): 更新changelog模板
- [`61198a0dc`](https://github.com/simple-robot/simpler-robot/commit/61198a0dc): :bookmark: 发布版本 `v3.0.0.preview.6.0`
- [`e3e82d554..72c63e29b`](https://github.com/simple-robot/simpler-robot/compare/e3e82d554..61198a0dc): 新的版本定义方式
- [`205299bf9`](https://github.com/simple-robot/simpler-robot/commit/205299bf9): 范型调整
- [`f7a7c660c`](https://github.com/simple-robot/simpler-robot/commit/f7a7c660c): 范型fix
- [`961742ef7`](https://github.com/simple-robot/simpler-robot/commit/961742ef7): 版本to6.0
- [`72e41dcce`](https://github.com/simple-robot/simpler-robot/commit/72e41dcce): 调整event范型定义
- [`ce46e38ce..605c24c26`](https://github.com/simple-robot/simpler-robot/compare/ce46e38ce..72e41dcce): Update README.md
- [`7d6814b4a`](https://github.com/simple-robot/simpler-robot/commit/7d6814b4a): 标识待办
- [`c95be246c`](https://github.com/simple-robot/simpler-robot/commit/c95be246c): Update to v3.0.0.preview.5.0
- [`7679f2182`](https://github.com/simple-robot/simpler-robot/commit/7679f2182): 优化日志
- [`1b8de500f`](https://github.com/simple-robot/simpler-robot/commit/1b8de500f): boot-core自动加载所有可加载组件（`installAll`）
- [`9595de83f`](https://github.com/simple-robot/simpler-robot/commit/9595de83f): 补充注释
- [`f83aeea7c`](https://github.com/simple-robot/simpler-robot/commit/f83aeea7c): 为 MuteSupport.mute 的参数提供默认值
- [`8e804e038..635d81a46`](https://github.com/simple-robot/simpler-robot/compare/8e804e038..f83aeea7c): Update README.md
- [`d3bd38336`](https://github.com/simple-robot/simpler-robot/commit/d3bd38336): some test
- [`09c2afd3a`](https://github.com/simple-robot/simpler-robot/commit/09c2afd3a): component
- [`98ce57073..17c4bde06`](https://github.com/simple-robot/simpler-robot/compare/98ce57073..09c2afd3a): bugfix
- [`dbfc6d2a0..5be8c9557`](https://github.com/simple-robot/simpler-robot/compare/dbfc6d2a0..17c4bde06): 组件中的序列化器
- [`e62ad09fc`](https://github.com/simple-robot/simpler-robot/commit/e62ad09fc): 文件名调整
- [`2fc5d93e3`](https://github.com/simple-robot/simpler-robot/commit/2fc5d93e3): 调整组件机制
- [`89b4d64af`](https://github.com/simple-robot/simpler-robot/commit/89b4d64af): 移除Message的ComponentContainer实现，并调整 Message.Key 的定义
- [`06bbecb85`](https://github.com/simple-robot/simpler-robot/commit/06bbecb85): Component Registrar and listener manager install()
- [`333752e2b`](https://github.com/simple-robot/simpler-robot/commit/333752e2b): update readme
- [`94659b921`](https://github.com/simple-robot/simpler-robot/commit/94659b921): Bot.isMe api
- [`2310eab64`](https://github.com/simple-robot/simpler-robot/commit/2310eab64): 补充部分注释
- [`380c1ca64`](https://github.com/simple-robot/simpler-robot/commit/380c1ca64): version to pre.5.0
- [`b65e58c1c`](https://github.com/simple-robot/simpler-robot/commit/b65e58c1c): 简单调整部分shared operato和
- [`c042503a8`](https://github.com/simple-robot/simpler-robot/commit/c042503a8): 补充注释与单元测试
- [`031079119`](https://github.com/simple-robot/simpler-robot/commit/031079119): configuration中的监听函数构建器
- [`181e10c7f..a51afcd92`](https://github.com/simple-robot/simpler-robot/compare/181e10c7f..031079119): 监听函数构建器，在configuration中
- [`a9c0f3079`](https://github.com/simple-robot/simpler-robot/commit/a9c0f3079): 补充大量注释; 调整核心事件管理器的配置类为可链式的
- [`4f334963e`](https://github.com/simple-robot/simpler-robot/commit/4f334963e): EventListenerRegistrar 说明
- [`65c467d6b`](https://github.com/simple-robot/simpler-robot/commit/65c467d6b): fix submodule head
- [`3236f6405`](https://github.com/simple-robot/simpler-robot/commit/3236f6405): update submodule config
- [`13e23e395`](https://github.com/simple-robot/simpler-robot/commit/13e23e395): just update
- [`bbf75d8c1`](https://github.com/simple-robot/simpler-robot/commit/bbf75d8c1): 清理部分遗留输出斌修复部分错误
- [`fa796b8e7`](https://github.com/simple-robot/simpler-robot/commit/fa796b8e7): 优化reactive相关api无法被处理的警告信息
- [`3318f8fbb`](https://github.com/simple-robot/simpler-robot/commit/3318f8fbb): 调整actions以支持submodule
- [`04c8395db`](https://github.com/simple-robot/simpler-robot/commit/04c8395db): remove buildSrc2
- [`2c6e419c8`](https://github.com/simple-robot/simpler-robot/commit/2c6e419c8): config git submodules
- [`b078bcc5d`](https://github.com/simple-robot/simpler-robot/commit/b078bcc5d): buildSrc submodule try fix
- [`1b18b12a8`](https://github.com/simple-robot/simpler-robot/commit/1b18b12a8): delete submodule
- [`3339baa3b`](https://github.com/simple-robot/simpler-robot/commit/3339baa3b): rename buildSrc for fix git submodule
- [`dbf123dd8`](https://github.com/simple-robot/simpler-robot/commit/dbf123dd8): remove错误提交的文件夹
- [`f1a2396bd`](https://github.com/simple-robot/simpler-robot/commit/f1a2396bd): buildSrc 子模块
- [`d94369bc0`](https://github.com/simple-robot/simpler-robot/commit/d94369bc0): About buildSrc
- [`844e30c63`](https://github.com/simple-robot/simpler-robot/commit/844e30c63): 简单优化对于核心boot模块下的类扫描提示
- [`229fce815`](https://github.com/simple-robot/simpler-robot/commit/229fce815): Version util
- [`46a50f38e..80c0b63f1`](https://github.com/simple-robot/simpler-robot/compare/46a50f38e..229fce815): issue模板
- [`abf3eaac0`](https://github.com/simple-robot/simpler-robot/commit/abf3eaac0): 恢复配置
- [`98d4b8552`](https://github.com/simple-robot/simpler-robot/commit/98d4b8552): kdoc
- [`2d6a582cf`](https://github.com/simple-robot/simpler-robot/commit/2d6a582cf): kdoc publish
- [`3202305ef`](https://github.com/simple-robot/simpler-robot/commit/3202305ef): :wrench: 修改配置文件
- [`766e8ff67..bc07df22b`](https://github.com/simple-robot/simpler-robot/compare/766e8ff67..3202305ef): 补充注释与说明
- [`d572c555a`](https://github.com/simple-robot/simpler-robot/commit/d572c555a): 清理代码
- [`3a6959754`](https://github.com/simple-robot/simpler-robot/commit/3a6959754): 优化展示
- [`49a1f6aea`](https://github.com/simple-robot/simpler-robot/commit/49a1f6aea): default logger use
- [`3fa4c5581`](https://github.com/simple-robot/simpler-robot/commit/3fa4c5581): Simbot Logger
- [`84c687c53`](https://github.com/simple-robot/simpler-robot/commit/84c687c53): Logger
- [`c62fdff7f`](https://github.com/simple-robot/simpler-robot/commit/c62fdff7f): 优化警告日志
- [`e26e7eaf3`](https://github.com/simple-robot/simpler-robot/commit/e26e7eaf3): 调整警告日志
- [`62ac24d6a`](https://github.com/simple-robot/simpler-robot/commit/62ac24d6a): reactive api support
- [`16ee681b8..d120f29cd`](https://github.com/simple-robot/simpler-robot/compare/16ee681b8..62ac24d6a): reactive api
- [`bc56a4658`](https://github.com/simple-robot/simpler-robot/commit/bc56a4658): Logger
- [`d32ad7200..83d8e16a3`](https://github.com/simple-robot/simpler-robot/compare/d32ad7200..bc56a4658): 更新配置
- [`26c967629`](https://github.com/simple-robot/simpler-robot/commit/26c967629): 简单细化spring-boot-starter部分配置类
- [`75b61a0e8`](https://github.com/simple-robot/simpler-robot/commit/75b61a0e8): CoreListenerManager中的事件处理不在使用bot的context
- [`d6522d63d`](https://github.com/simple-robot/simpler-robot/commit/d6522d63d): 增加文档配置
- [`4eeb558f6..6a0b4f0dd`](https://github.com/simple-robot/simpler-robot/compare/4eeb558f6..d6522d63d): 更新readme
- [`fb2dd4f27`](https://github.com/simple-robot/simpler-robot/commit/fb2dd4f27): update config
- [`f73bcf6ae`](https://github.com/simple-robot/simpler-robot/commit/f73bcf6ae): update to pre.3.1
- [`d6db3901c`](https://github.com/simple-robot/simpler-robot/commit/d6db3901c): update README
- [`2f38df142`](https://github.com/simple-robot/simpler-robot/commit/2f38df142): 更新changelog配置
- [`a4fa0355c`](https://github.com/simple-robot/simpler-robot/commit/a4fa0355c): 更新配置
- [`2a76c1e44`](https://github.com/simple-robot/simpler-robot/commit/2a76c1e44): Release for v3.0.0.preview.3.0
- [`3a7f834dc`](https://github.com/simple-robot/simpler-robot/commit/3a7f834dc): 补充注释
- [`7ff0674a0`](https://github.com/simple-robot/simpler-robot/commit/7ff0674a0): MessagesBuilder
- [`07a58f0b4`](https://github.com/simple-robot/simpler-robot/commit/07a58f0b4): 简单调整publish配置
- [`05bafd19c`](https://github.com/simple-robot/simpler-robot/commit/05bafd19c): 序列化&Test
- [`37226935e`](https://github.com/simple-robot/simpler-robot/commit/37226935e): 为 Messages 提供java平台的序列化api
- [`8d5372964`](https://github.com/simple-robot/simpler-robot/commit/8d5372964): 为 Messages 的实现提供 toString 和 equals
- [`8144e3faf`](https://github.com/simple-robot/simpler-robot/commit/8144e3faf): 补充 Limiter 注释
- [`1d604f2c0`](https://github.com/simple-robot/simpler-robot/commit/1d604f2c0): Messages 注释补充，toString 补充
- [`88d769681..823114932`](https://github.com/simple-robot/simpler-robot/compare/88d769681..1d604f2c0): 更新配置
- [`35879a688`](https://github.com/simple-robot/simpler-robot/commit/35879a688): 更新快照更新配置
- [`0033c456e`](https://github.com/simple-robot/simpler-robot/commit/0033c456e): 调整规则 - 修改了源码才发布
- [`42c60ef9c`](https://github.com/simple-robot/simpler-robot/commit/42c60ef9c): 自动更新快照脚本
- [`c5167a9b9`](https://github.com/simple-robot/simpler-robot/commit/c5167a9b9): Logger 模块?
- [`b92475576`](https://github.com/simple-robot/simpler-robot/commit/b92475576): 补充注释
- [`a8739fe72`](https://github.com/simple-robot/simpler-robot/commit/a8739fe72): 补充/调整注释
- [`a2b89bd18..1b99ef2b0`](https://github.com/simple-robot/simpler-robot/compare/a2b89bd18..a8739fe72): 补充注释
- [`997a0631f`](https://github.com/simple-robot/simpler-robot/commit/997a0631f): 调整注释
- [`d7589e568`](https://github.com/simple-robot/simpler-robot/commit/d7589e568): update doc config
- [`d3da88d6e`](https://github.com/simple-robot/simpler-robot/commit/d3da88d6e): Doc publish config
- [`7a43ad7f1`](https://github.com/simple-robot/simpler-robot/commit/7a43ad7f1): config
- [`7d64c972e`](https://github.com/simple-robot/simpler-robot/commit/7d64c972e): 补充 OriginBotManager 的相关注释, 增加一个 `getAny` 函数
- [`3c3b65f43`](https://github.com/simple-robot/simpler-robot/commit/3c3b65f43): 调整错别字
- [`4abf3857e`](https://github.com/simple-robot/simpler-robot/commit/4abf3857e): 更新文档部署配置文件
- [`8c853f8db`](https://github.com/simple-robot/simpler-robot/commit/8c853f8db): Dokka doc config and published to <https://simple-robot-library.github.io/simbot3-main-apiDoc>
- [`364dc4e88..a5b620aa4`](https://github.com/simple-robot/simpler-robot/compare/364dc4e88..8c853f8db): update workflow settings, and rename
- [`44238be75..de1e252f3`](https://github.com/simple-robot/simpler-robot/compare/44238be75..a5b620aa4): update workflow settings.
- [`a176882f8..2b64e33bb`](https://github.com/simple-robot/simpler-robot/compare/a176882f8..de1e252f3): update build settings.
- [`847de58aa`](https://github.com/simple-robot/simpler-robot/commit/847de58aa): test file
- [`1f72d8d48`](https://github.com/simple-robot/simpler-robot/commit/1f72d8d48): :camera_flash: Adding or updating snapshots.
- [`3cd68af28`](https://github.com/simple-robot/simpler-robot/commit/3cd68af28): config snapshot
- [`8d9bb1915`](https://github.com/simple-robot/simpler-robot/commit/8d9bb1915): new Logo!
- [`9fb5828ae`](https://github.com/simple-robot/simpler-robot/commit/9fb5828ae): new spring config
- [`d850b706b`](https://github.com/simple-robot/simpler-robot/commit/d850b706b): 重命名spring-boot-starter模块名称 `simboot-core-springboot-starter` -> `simboot-core-spring-boot-starter`
- [`bacae85f0..f53040e25`](https://github.com/simple-robot/simpler-robot/compare/bacae85f0..d850b706b): 调整注释
- [`1fd6bb355`](https://github.com/simple-robot/simpler-robot/commit/1fd6bb355): 恢复@Filter和@Filters的递归性。参考 https://github.com/spring-projects/spring-boot/issues/29662
- [`90e20c1f1`](https://github.com/simple-robot/simpler-robot/commit/90e20c1f1): 重新调整Role相关Api
- [`ebf4e8f34`](https://github.com/simple-robot/simpler-robot/commit/ebf4e8f34): 标记修改点
- [`8cfa52fb2..18b67a096`](https://github.com/simple-robot/simpler-robot/compare/8cfa52fb2..ebf4e8f34): update readme
- [`d1af4263c`](https://github.com/simple-robot/simpler-robot/commit/d1af4263c): build.kts配置修改
- [`cf18217e0`](https://github.com/simple-robot/simpler-robot/commit/cf18217e0): 测试补充
- [`6a48fa704`](https://github.com/simple-robot/simpler-robot/commit/6a48fa704): 增加 Timestamp 相关API
- [`d24ac0a7d`](https://github.com/simple-robot/simpler-robot/commit/d24ac0a7d): 调整 Timestamp 相关API
- [`a60570c25`](https://github.com/simple-robot/simpler-robot/commit/a60570c25): 消除 Message.Metadata 并调整注释
- [`dd5770ee9`](https://github.com/simple-robot/simpler-robot/commit/dd5770ee9): 消除Event.Metadata并调整注释
- [`aa1743b80`](https://github.com/simple-robot/simpler-robot/commit/aa1743b80): 移除Event中的Metadata
- [`00efc9b24`](https://github.com/simple-robot/simpler-robot/commit/00efc9b24): 补充注释
- [`a6e4b322b`](https://github.com/simple-robot/simpler-robot/commit/a6e4b322b): 增加部分中断异常
- [`f6df97e4e`](https://github.com/simple-robot/simpler-robot/commit/f6df97e4e): 移除 SimbootApplication 无用配置
- [`81bfe8e53`](https://github.com/simple-robot/simpler-robot/commit/81bfe8e53): 发布preview.2.0
- [`bcde2d41c..75f9bd98a`](https://github.com/simple-robot/simpler-robot/compare/bcde2d41c..81bfe8e53): 补充注释
- [`036547dfb`](https://github.com/simple-robot/simpler-robot/commit/036547dfb): push async
- [`de620740b`](https://github.com/simple-robot/simpler-robot/commit/de620740b): async listener
- [`0025a0b63..f66ab27f9`](https://github.com/simple-robot/simpler-robot/compare/0025a0b63..de620740b): keyword match
- [`08bd3ecda`](https://github.com/simple-robot/simpler-robot/commit/08bd3ecda): TODO mark
- [`c1a23b7e3`](https://github.com/simple-robot/simpler-robot/commit/c1a23b7e3): fix keyword match; update annotationTool to 0.6.3
- [`2d5d4b0db`](https://github.com/simple-robot/simpler-robot/commit/2d5d4b0db): update annotation tool
- [`a84d5cb77`](https://github.com/simple-robot/simpler-robot/commit/a84d5cb77): Filters fix
- [`db6810acc`](https://github.com/simple-robot/simpler-robot/commit/db6810acc): di -> 0.0.3
- [`3cf422e41`](https://github.com/simple-robot/simpler-robot/commit/3cf422e41): 修改注释
- [`0dd3cf8e4`](https://github.com/simple-robot/simpler-robot/commit/0dd3cf8e4): Filters 默认匹配方式
- [`a0997dfd4`](https://github.com/simple-robot/simpler-robot/commit/a0997dfd4): @TargetFilter.atBot: Boolean
- [`2ff9b1fa4`](https://github.com/simple-robot/simpler-robot/commit/2ff9b1fa4): Message.Element.equals() & hashCode()
- [`5a0ca59df..2364e44d9`](https://github.com/simple-robot/simpler-robot/compare/5a0ca59df..2ff9b1fa4): 为部分事件增加 `inXxx` 和 `useXxx` 扩展函数
- [`36d97d521`](https://github.com/simple-robot/simpler-robot/commit/36d97d521): 为部分事件增加 `inXxx` 扩展函数
- [`a55c35b33`](https://github.com/simple-robot/simpler-robot/commit/a55c35b33): 为部分事件增加 `useXxx` 扩展函数
- [`bec6cfe50`](https://github.com/simple-robot/simpler-robot/commit/bec6cfe50): version to pre.2.0
- [`7f04abe2c`](https://github.com/simple-robot/simpler-robot/commit/7f04abe2c): Resource and bot image api
- [`687ce0eb6..8ccbc1476`](https://github.com/simple-robot/simpler-robot/compare/687ce0eb6..7f04abe2c): StandardStreamableResource
- [`92f6d24ac`](https://github.com/simple-robot/simpler-robot/commit/92f6d24ac): remove serializable on Resource
- [`837ef071b..2b8c8f676`](https://github.com/simple-robot/simpler-robot/compare/837ef071b..92f6d24ac): 暂时移除IDResource及其相关内容
- [`dadfc572f..c9dcaa244`](https://github.com/simple-robot/simpler-robot/compare/dadfc572f..2b8c8f676): StandardStreamableResource
- [`8b2a99c51`](https://github.com/simple-robot/simpler-robot/commit/8b2a99c51): remove serializable on Resource
- [`2ca93a85b..bc4a49bb4`](https://github.com/simple-robot/simpler-robot/compare/2ca93a85b..8b2a99c51): 暂时移除IDResource及其相关内容
- [`9892b5602..28c13643e`](https://github.com/simple-robot/simpler-robot/compare/9892b5602..bc4a49bb4): ID.literal
- [`5ca4e66e1`](https://github.com/simple-robot/simpler-robot/commit/5ca4e66e1): 暂时移除IDResource及其相关内容
- [`1a40b1fca`](https://github.com/simple-robot/simpler-robot/commit/1a40b1fca): FriendInfo提供用户名相关辅助函数
- [`585ff502e`](https://github.com/simple-robot/simpler-robot/commit/585ff502e): MemberInfo提供用户名相关辅助函数
- [`164a69a1d`](https://github.com/simple-robot/simpler-robot/commit/164a69a1d): 增加待实现事件
- [`35132fab0`](https://github.com/simple-robot/simpler-robot/commit/35132fab0): 补充注释
- [`65d31436c`](https://github.com/simple-robot/simpler-robot/commit/65d31436c): 组织ID
- [`be13542f1..fef8f2b4a`](https://github.com/simple-robot/simpler-robot/compare/be13542f1..65d31436c): Readme
- [`c57f1aaa7`](https://github.com/simple-robot/simpler-robot/commit/c57f1aaa7): Readmy & copying
- [`d0465dfc6..9bed33f0b`](https://github.com/simple-robot/simpler-robot/compare/d0465dfc6..c57f1aaa7): Readme
- [`6ac811da3`](https://github.com/simple-robot/simpler-robot/commit/6ac811da3): logo
- [`638dfe0fe..66c9ed19c`](https://github.com/simple-robot/simpler-robot/compare/638dfe0fe..6ac811da3): 补充注释。
- [`66fbfa689..4e7218259`](https://github.com/simple-robot/simpler-robot/compare/66fbfa689..66c9ed19c): internal event keys
- [`91d01b271..39562b438`](https://github.com/simple-robot/simpler-robot/compare/91d01b271..4e7218259): 补充注释
- [`bbaeca91c`](https://github.com/simple-robot/simpler-robot/commit/bbaeca91c): 内部bot相关事件
- [`9e426d92e`](https://github.com/simple-robot/simpler-robot/commit/9e426d92e): ID for Resource
- [`fe71d1af6`](https://github.com/simple-robot/simpler-robot/commit/fe71d1af6): ID容器的实现
- [`d49e54b86`](https://github.com/simple-robot/simpler-robot/commit/d49e54b86): ID Test
- [`ad9bc8907`](https://github.com/simple-robot/simpler-robot/commit/ad9bc8907): 调整注释
- [`f072a333c`](https://github.com/simple-robot/simpler-robot/commit/f072a333c): ID容器的实现
- [`41aa0800a`](https://github.com/simple-robot/simpler-robot/commit/41aa0800a): ID 容器定义
- [`b011d1302`](https://github.com/simple-robot/simpler-robot/commit/b011d1302): 为@Listen所有衍生注解标记过时
- [`bacdbe24b`](https://github.com/simple-robot/simpler-robot/commit/bacdbe24b): 补充注释
- [`350148237`](https://github.com/simple-robot/simpler-robot/commit/350148237): 暂时异常`OnXxx`监听注解
- [`cf4b740a2`](https://github.com/simple-robot/simpler-robot/commit/cf4b740a2): 持续会话相关更新
- [`68f30d665`](https://github.com/simple-robot/simpler-robot/commit/68f30d665): 隐藏部分suspend api
- [`20a6ac82c`](https://github.com/simple-robot/simpler-robot/commit/20a6ac82c): 增加内部Bot事件
- [`c82abe3ed..27b70b16f`](https://github.com/simple-robot/simpler-robot/compare/c82abe3ed..20a6ac82c): 隐藏部分suspend api
- [`90ed097f2`](https://github.com/simple-robot/simpler-robot/commit/90ed097f2): 更新开源协议
- [`7a000f624..1d9698c46`](https://github.com/simple-robot/simpler-robot/compare/7a000f624..90ed097f2): 更新、调整、完善开源协议
- [`d3d7f4395`](https://github.com/simple-robot/simpler-robot/commit/d3d7f4395): ID Util
- [`db7baaaa9`](https://github.com/simple-robot/simpler-robot/commit/db7baaaa9): ID UUID random
- [`52cd193c4..698bdee9c`](https://github.com/simple-robot/simpler-robot/compare/52cd193c4..db7baaaa9): 调整toAsync返回值类型。
- [`4b6608bad`](https://github.com/simple-robot/simpler-robot/commit/4b6608bad): Core manager intercept config
- [`400a45b82`](https://github.com/simple-robot/simpler-robot/commit/400a45b82): 补充注释
- [`db3d48142`](https://github.com/simple-robot/simpler-robot/commit/db3d48142): 全局获取作用域与持续会话作用域
- [`d3897f6e1`](https://github.com/simple-robot/simpler-robot/commit/d3897f6e1): 补充注释
- [`c223df9b8`](https://github.com/simple-robot/simpler-robot/commit/c223df9b8): Survivable.waiting() 抛出中断异常
- [`eb1d94aa7`](https://github.com/simple-robot/simpler-robot/commit/eb1d94aa7): Organization / group / guild / channel 相关直接获取API, 调整返回值类型
- [`f4977d2fc`](https://github.com/simple-robot/simpler-robot/commit/f4977d2fc): runBlocking -> runInBlocking
- [`37acb252a`](https://github.com/simple-robot/simpler-robot/commit/37acb252a): 调整注释
- [`ecb516315`](https://github.com/simple-robot/simpler-robot/commit/ecb516315): 增加 xxIfSupport相关inline API
- [`1665ba556`](https://github.com/simple-robot/simpler-robot/commit/1665ba556): Bot增加独立获取相关内容的api
- [`105948fcf`](https://github.com/simple-robot/simpler-robot/commit/105948fcf): 事件注释修改，test
- [`706099094`](https://github.com/simple-robot/simpler-robot/commit/706099094): 恢复监听，onMessage with Duration
- [`f458720e2`](https://github.com/simple-robot/simpler-robot/commit/f458720e2): waiting on message
- [`540b570cc..b54a8ef05`](https://github.com/simple-robot/simpler-robot/compare/540b570cc..f458720e2): 调整BotManager api
- [`661b4663f`](https://github.com/simple-robot/simpler-robot/commit/661b4663f): session waitingOnMessage
- [`a70e45cf6`](https://github.com/simple-robot/simpler-robot/commit/a70e45cf6): session waitingForOnMessage
- [`395bc8917`](https://github.com/simple-robot/simpler-robot/commit/395bc8917): 超时处理
- [`f4b682891`](https://github.com/simple-robot/simpler-robot/commit/f4b682891): 超时清理
- [`89faa018e`](https://github.com/simple-robot/simpler-robot/commit/89faa018e): 调整OriginBotManager部分API
- [`4f7c6c69a`](https://github.com/simple-robot/simpler-robot/commit/4f7c6c69a): Member.roles
- [`ec38399af`](https://github.com/simple-robot/simpler-robot/commit/ec38399af): 补充注释，隐藏部分函数
- [`8b27b3165`](https://github.com/simple-robot/simpler-robot/commit/8b27b3165): 调整Member/Organization/Role相关API，更新版本到pre.1.1
- [`c3d0ce810`](https://github.com/simple-robot/simpler-robot/commit/c3d0ce810): version to 1.0
- [`8806b1eda`](https://github.com/simple-robot/simpler-robot/commit/8806b1eda): coroutine scopes
- [`43a418ac3`](https://github.com/simple-robot/simpler-robot/commit/43a418ac3): Add some@JvmSynthetic
- [`3dde53a42`](https://github.com/simple-robot/simpler-robot/commit/3dde53a42): test comment
- [`c457ef220..84400f2a3`](https://github.com/simple-robot/simpler-robot/compare/c457ef220..3dde53a42): build config
- [`d892053c7`](https://github.com/simple-robot/simpler-robot/commit/d892053c7): ID update
- [`02bfcb88e`](https://github.com/simple-robot/simpler-robot/commit/02bfcb88e): Test and get members
- [`1db64a889`](https://github.com/simple-robot/simpler-robot/commit/1db64a889): Event annotations
- [`344b31f41`](https://github.com/simple-robot/simpler-robot/commit/344b31f41): accept
- [`b00a00ac7`](https://github.com/simple-robot/simpler-robot/commit/b00a00ac7): GroupJoinRequestEvent
- [`3ee736561`](https://github.com/simple-robot/simpler-robot/commit/3ee736561): message
- [`934836960`](https://github.com/simple-robot/simpler-robot/commit/934836960): Events
- [`5b5d2324d`](https://github.com/simple-robot/simpler-robot/commit/5b5d2324d): manager
- [`a85fe27c0`](https://github.com/simple-robot/simpler-robot/commit/a85fe27c0): 注释于注解
- [`24c9d019d`](https://github.com/simple-robot/simpler-robot/commit/24c9d019d): annotations
- [`6aad064a0`](https://github.com/simple-robot/simpler-robot/commit/6aad064a0): friends
- [`f6567691f`](https://github.com/simple-robot/simpler-robot/commit/f6567691f): requires opt annotation
- [`b510fd172`](https://github.com/simple-robot/simpler-robot/commit/b510fd172): request events, changed events
- [`d8fe1f5c9`](https://github.com/simple-robot/simpler-robot/commit/d8fe1f5c9): 补充注释
- [`91d1d0db4`](https://github.com/simple-robot/simpler-robot/commit/91d1d0db4): Limiter
- [`2e6f8bc95`](https://github.com/simple-robot/simpler-robot/commit/2e6f8bc95): Resources
- [`66cba4f9e..a32569cfe`](https://github.com/simple-robot/simpler-robot/compare/66cba4f9e..2e6f8bc95): Limiter.kt
- [`7fe5b3d92`](https://github.com/simple-robot/simpler-robot/commit/7fe5b3d92): Bot apis
- [`d25755511`](https://github.com/simple-robot/simpler-robot/commit/d25755511): pushIfProcessable
- [`b8b4e0f92`](https://github.com/simple-robot/simpler-robot/commit/b8b4e0f92): remove ComplexID
- [`df4db5504..9e1fe320b`](https://github.com/simple-robot/simpler-robot/compare/df4db5504..b8b4e0f92): Lazy value util
- [`9c3ab7db5`](https://github.com/simple-robot/simpler-robot/commit/9c3ab7db5): friend message event
- [`7a7f6b571`](https://github.com/simple-robot/simpler-robot/commit/7a7f6b571): base event
- [`c94f4ce60`](https://github.com/simple-robot/simpler-robot/commit/c94f4ce60): new api
- [`a1addc1b5`](https://github.com/simple-robot/simpler-robot/commit/a1addc1b5): remove session old
- [`b153b7c72`](https://github.com/simple-robot/simpler-robot/commit/b153b7c72): events api
- [`76efacfc2`](https://github.com/simple-robot/simpler-robot/commit/76efacfc2): blocking with interruptible
- [`626ace278`](https://github.com/simple-robot/simpler-robot/commit/626ace278): Resource serializer
- [`5cdfe049b`](https://github.com/simple-robot/simpler-robot/commit/5cdfe049b): Image<E>
- [`038302b9e..87346870c`](https://github.com/simple-robot/simpler-robot/compare/038302b9e..5cdfe049b): resource
- [`1440e50b6`](https://github.com/simple-robot/simpler-robot/commit/1440e50b6): send message content
- [`42f2b7071`](https://github.com/simple-robot/simpler-robot/commit/42f2b7071): send string
- [`3d5ef0014..6bb0f9e42`](https://github.com/simple-robot/simpler-robot/compare/3d5ef0014..42f2b7071): Resources
- [`0b44157d5`](https://github.com/simple-robot/simpler-robot/commit/0b44157d5): Resource
- [`57b2734bb`](https://github.com/simple-robot/simpler-robot/commit/57b2734bb): SendSupport.kt
- [`ed7f32526`](https://github.com/simple-robot/simpler-robot/commit/ed7f32526): DeleteAction -> DeleteSupport
- [`37cee1be9`](https://github.com/simple-robot/simpler-robot/commit/37cee1be9): bot manager config
- [`47988e331`](https://github.com/simple-robot/simpler-robot/commit/47988e331): MuteAction.kt
- [`d3510906a`](https://github.com/simple-robot/simpler-robot/commit/d3510906a): V, and session context
- [`18a026aff`](https://github.com/simple-robot/simpler-robot/commit/18a026aff): Messages 优化
- [`3c7e91abb`](https://github.com/simple-robot/simpler-robot/commit/3c7e91abb): Messages 不再验证 component
- [`0b36545d3`](https://github.com/simple-robot/simpler-robot/commit/0b36545d3): add log
- [`ab9937af9..5ee2266bb`](https://github.com/simple-robot/simpler-robot/compare/ab9937af9..0b36545d3): preview-0.6
- [`d8e88f45e`](https://github.com/simple-robot/simpler-robot/commit/d8e88f45e): update README.md
- [`b5e96fceb`](https://github.com/simple-robot/simpler-robot/commit/b5e96fceb): check event type
- [`b3d793888`](https://github.com/simple-robot/simpler-robot/commit/b3d793888): EventResult.kt
- [`549a1f5c3`](https://github.com/simple-robot/simpler-robot/commit/549a1f5c3): 事件会话
- [`30f27b093`](https://github.com/simple-robot/simpler-robot/commit/30f27b093): 持续会话；Event.Key
- [`ac5d5c081`](https://github.com/simple-robot/simpler-robot/commit/ac5d5c081): textContent前置处理器标准注解
- [`de1ca69ea`](https://github.com/simple-robot/simpler-robot/commit/de1ca69ea): 专属拦截器、textContent前置处理器
- [`f52455c05..114453e5c`](https://github.com/simple-robot/simpler-robot/compare/f52455c05..de1ca69ea): 拦截器接口本身不携带ID; @Interceptor;
- [`95ff9196c`](https://github.com/simple-robot/simpler-robot/commit/95ff9196c): session context
- [`e4ca747fa`](https://github.com/simple-robot/simpler-robot/commit/e4ca747fa): Session
- [`34f517f4a..b9aa97cca`](https://github.com/simple-robot/simpler-robot/compare/34f517f4a..e4ca747fa): ID Map
- [`e5de54c80`](https://github.com/simple-robot/simpler-robot/commit/e5de54c80): session context 2
- [`2b98cbd67`](https://github.com/simple-robot/simpler-robot/commit/2b98cbd67): scope binder
- [`dc8ee83d3..363ff8261`](https://github.com/simple-robot/simpler-robot/compare/dc8ee83d3..2b98cbd67): continuous session
- [`f358bcb0b`](https://github.com/simple-robot/simpler-robot/commit/f358bcb0b): text processor
- [`d4fc957f8`](https://github.com/simple-robot/simpler-robot/commit/d4fc957f8): @Filter.ifNullPass
- [`490fdad0b..47629d2c9`](https://github.com/simple-robot/simpler-robot/compare/490fdad0b..d4fc957f8): loggers
- [`050c1ab7d`](https://github.com/simple-robot/simpler-robot/commit/050c1ab7d): event
- [`a1bbb3c05`](https://github.com/simple-robot/simpler-robot/commit/a1bbb3c05): @Listener
- [`95953cc87`](https://github.com/simple-robot/simpler-robot/commit/95953cc87): @Interceptor
- [`61a0a469c`](https://github.com/simple-robot/simpler-robot/commit/61a0a469c): if null pass
- [`f5198b26a`](https://github.com/simple-robot/simpler-robot/commit/f5198b26a): text content processor
- [`d93bbd447`](https://github.com/simple-robot/simpler-robot/commit/d93bbd447): Top listener scan
- [`cfaf93386`](https://github.com/simple-robot/simpler-robot/commit/cfaf93386): test
- [`214cd1fb0`](https://github.com/simple-robot/simpler-robot/commit/214cd1fb0): session
- [`3f4a49903`](https://github.com/simple-robot/simpler-robot/commit/3f4a49903): session scope, filter for session
- [`a37bbc631`](https://github.com/simple-robot/simpler-robot/commit/a37bbc631): Blocking api
- [`e6701eff1`](https://github.com/simple-robot/simpler-robot/commit/e6701eff1): coroutine test
- [`ad7894663`](https://github.com/simple-robot/simpler-robot/commit/ad7894663): Continuous session scope
- [`fa45198e8`](https://github.com/simple-robot/simpler-robot/commit/fa45198e8): rename module
- [`f83348814`](https://github.com/simple-robot/simpler-robot/commit/f83348814): autowired annotation
- [`d385741fa..8abfe1c69`](https://github.com/simple-robot/simpler-robot/compare/d385741fa..f83348814): springboot starter test
- [`cc9459c8f..8589152b9`](https://github.com/simple-robot/simpler-robot/compare/cc9459c8f..8abfe1c69): Springboot starter
- [`6fd7f7801`](https://github.com/simple-robot/simpler-robot/commit/6fd7f7801): springboot starter module
- [`0129a2cbd`](https://github.com/simple-robot/simpler-robot/commit/0129a2cbd): 监听事件类型检测优化
- [`6b46ebeb4`](https://github.com/simple-robot/simpler-robot/commit/6b46ebeb4): 各种默认binder
- [`7369f8e2d`](https://github.com/simple-robot/simpler-robot/commit/7369f8e2d): test and all bots info
- [`1b24a0fcf`](https://github.com/simple-robot/simpler-robot/commit/1b24a0fcf): Scanner and runner
- [`7ace0c1c7`](https://github.com/simple-robot/simpler-robot/commit/7ace0c1c7): Pom setup and group
- [`38f231c87..36aa4d649`](https://github.com/simple-robot/simpler-robot/compare/38f231c87..7ace0c1c7): Core entrance
- [`98c8e6549..205b1f0a1`](https://github.com/simple-robot/simpler-robot/compare/98c8e6549..36aa4d649): Scanner and runner
- [`cb938f332`](https://github.com/simple-robot/simpler-robot/commit/cb938f332): annotation processor
- [`d57ea35a3`](https://github.com/simple-robot/simpler-robot/commit/d57ea35a3): interceptor
- [`849af204b`](https://github.com/simple-robot/simpler-robot/commit/849af204b): new listener for java
- [`4f116e1dc..baf13fcac`](https://github.com/simple-robot/simpler-robot/compare/4f116e1dc..849af204b): Event.Key.getKey
- [`5beea6248`](https://github.com/simple-robot/simpler-robot/commit/5beea6248): event listener
- [`338b1fb5c`](https://github.com/simple-robot/simpler-robot/commit/338b1fb5c): components
- [`ebaa826e1..beb4236b8`](https://github.com/simple-robot/simpler-robot/compare/ebaa826e1..338b1fb5c): binder
- [`39df79396`](https://github.com/simple-robot/simpler-robot/commit/39df79396): BotVerifyInfo
- [`158d4050b..4cd11a8e6`](https://github.com/simple-robot/simpler-robot/compare/158d4050b..39df79396): Update V
- [`1eed33361`](https://github.com/simple-robot/simpler-robot/commit/1eed33361): project names and module names
- [`9c7f7a932`](https://github.com/simple-robot/simpler-robot/commit/9c7f7a932): Resource scanner
- [`9e202d7c9`](https://github.com/simple-robot/simpler-robot/commit/9e202d7c9): update icon
- [`09531043a`](https://github.com/simple-robot/simpler-robot/commit/09531043a): Annotation listener processor
- [`6fea3ebfc`](https://github.com/simple-robot/simpler-robot/commit/6fea3ebfc): Listener and Attributes
- [`d4240f646`](https://github.com/simple-robot/simpler-robot/commit/d4240f646): Scope
- [`3483fd31f`](https://github.com/simple-robot/simpler-robot/commit/3483fd31f): Scanner
- [`5864a4d51`](https://github.com/simple-robot/simpler-robot/commit/5864a4d51): ReactEvents
- [`510893b6d`](https://github.com/simple-robot/simpler-robot/commit/510893b6d): caller
- [`38612d311`](https://github.com/simple-robot/simpler-robot/commit/38612d311): GenericListener
- [`001b7bcc0`](https://github.com/simple-robot/simpler-robot/commit/001b7bcc0): update slf4j version
- [`a4bb09fb8`](https://github.com/simple-robot/simpler-robot/commit/a4bb09fb8): Event isSubFrom
- [`93da2d935`](https://github.com/simple-robot/simpler-robot/commit/93da2d935): SimbotComponent
- [`3574a23b7..056ddcb8f`](https://github.com/simple-robot/simpler-robot/compare/3574a23b7..93da2d935): Components.all
- [`a1cb5758b`](https://github.com/simple-robot/simpler-robot/commit/a1cb5758b): Components.getAll
- [`f2b8c26ec`](https://github.com/simple-robot/simpler-robot/commit/f2b8c26ec): boots, and remove submodule
- [`81e1002f0`](https://github.com/simple-robot/simpler-robot/commit/81e1002f0): managers and listeners
- [`f91d7374d`](https://github.com/simple-robot/simpler-robot/commit/f91d7374d): core boot filter
- [`9368bf86b`](https://github.com/simple-robot/simpler-robot/commit/9368bf86b): core intercept
- [`847ccd97e`](https://github.com/simple-robot/simpler-robot/commit/847ccd97e): Filter processor
- [`71fa7e67d`](https://github.com/simple-robot/simpler-robot/commit/71fa7e67d): Survivable, bot processor
- [`aa941c7d7..483dad759`](https://github.com/simple-robot/simpler-robot/compare/aa941c7d7..71fa7e67d): Test show for doc
- [`9cf929d03`](https://github.com/simple-robot/simpler-robot/commit/9cf929d03): rename for CoreListenerManagerConfiguration
- [`9e06d006d`](https://github.com/simple-robot/simpler-robot/commit/9e06d006d): version to 3.p.0.5
- [`cf162fa25`](https://github.com/simple-robot/simpler-robot/commit/cf162fa25): 3.0.0-preview.0.4
- [`08b78de1d`](https://github.com/simple-robot/simpler-robot/commit/08b78de1d): core event manager -> core listener manager
- [`fb279d470..068456403`](https://github.com/simple-robot/simpler-robot/compare/fb279d470..08b78de1d): Objective event
- [`bb32466f7`](https://github.com/simple-robot/simpler-robot/commit/bb32466f7): Comment
- [`e2c8a0d90`](https://github.com/simple-robot/simpler-robot/commit/e2c8a0d90): Filters and listeners func 4j
- [`f998f0db0`](https://github.com/simple-robot/simpler-robot/commit/f998f0db0): 更多的@Api4J
- [`b572c4fb9`](https://github.com/simple-robot/simpler-robot/commit/b572c4fb9): 更多的标准事件
- [`cbb08e9e5`](https://github.com/simple-robot/simpler-robot/commit/cbb08e9e5): Future, and to 3.0.0-preview.0.4
- [`1f7468e18`](https://github.com/simple-robot/simpler-robot/commit/1f7468e18): for 3.0.0-preview.0.3
- [`e713dc490`](https://github.com/simple-robot/simpler-robot/commit/e713dc490): CoreEventManager
- [`c07a4a29f`](https://github.com/simple-robot/simpler-robot/commit/c07a4a29f): OriginBotManager.kt
- [`f3f8f3a6f`](https://github.com/simple-robot/simpler-robot/commit/f3f8f3a6f): bot
- [`ce514d8d5`](https://github.com/simple-robot/simpler-robot/commit/ce514d8d5): override
- [`07b752578`](https://github.com/simple-robot/simpler-robot/commit/07b752578): Events
- [`8ffe085ee`](https://github.com/simple-robot/simpler-robot/commit/8ffe085ee): messages
- [`ff46f4125`](https://github.com/simple-robot/simpler-robot/commit/ff46f4125): Boots - boot-core module
- [`63f7ad6ff`](https://github.com/simple-robot/simpler-robot/commit/63f7ad6ff): attributes
- [`e87cbec75..d1bb5f016`](https://github.com/simple-robot/simpler-robot/compare/e87cbec75..63f7ad6ff): OriginBotManager
- [`96037c58e`](https://github.com/simple-robot/simpler-robot/commit/96037c58e): CoreEventProcessingContextResolver
- [`62f66f4e1`](https://github.com/simple-robot/simpler-robot/commit/62f66f4e1): 拦截器
- [`9b73b44fc`](https://github.com/simple-robot/simpler-robot/commit/9b73b44fc): Core manager
- [`02630c7f5..71a039ce5`](https://github.com/simple-robot/simpler-robot/compare/02630c7f5..9b73b44fc): Messages.kt
- [`46a8125b7`](https://github.com/simple-robot/simpler-robot/commit/46a8125b7): clean test code
- [`0bd4e0ced`](https://github.com/simple-robot/simpler-robot/commit/0bd4e0ced): Components
- [`a3dcd7240`](https://github.com/simple-robot/simpler-robot/commit/a3dcd7240): 不管子模块了
- [`7c1dc6fd1`](https://github.com/simple-robot/simpler-robot/commit/7c1dc6fd1): add submodule tencent-guild again
- [`3d476152b`](https://github.com/simple-robot/simpler-robot/commit/3d476152b): update\
- [`8b447d939`](https://github.com/simple-robot/simpler-robot/commit/8b447d939): Idea copyright
- [`51000ef70`](https://github.com/simple-robot/simpler-robot/commit/51000ef70): publish util
- [`72658080e`](https://github.com/simple-robot/simpler-robot/commit/72658080e): Message serializers
- [`a74a7536e`](https://github.com/simple-robot/simpler-robot/commit/a74a7536e): Fix some
- [`42f555eab`](https://github.com/simple-robot/simpler-robot/commit/42f555eab): modules and move component to tencent-guild
- [`061eb9b5d`](https://github.com/simple-robot/simpler-robot/commit/061eb9b5d): update module info
- [`4a09523b9`](https://github.com/simple-robot/simpler-robot/commit/4a09523b9): upload to preview dev-v3.0.0.preview.0.2
- [`f21f261d4`](https://github.com/simple-robot/simpler-robot/commit/f21f261d4): Publish
- [`11cb4fc3a`](https://github.com/simple-robot/simpler-robot/commit/11cb4fc3a): Bot with processor
- [`0ba58ca2c`](https://github.com/simple-robot/simpler-robot/commit/0ba58ca2c): component-guild for core
- [`1b9cce9da`](https://github.com/simple-robot/simpler-robot/commit/1b9cce9da): Permissions
- [`ed8b3dd3c`](https://github.com/simple-robot/simpler-robot/commit/ed8b3dd3c): limiter offset
- [`33afcdec6`](https://github.com/simple-robot/simpler-robot/commit/33afcdec6): mute, limiter actions
- [`62bd355d5`](https://github.com/simple-robot/simpler-robot/commit/62bd355d5): README, and something for event, listens, limiters
- [`16d8b1d37`](https://github.com/simple-robot/simpler-robot/commit/16d8b1d37): events
- [`b9896f8d8`](https://github.com/simple-robot/simpler-robot/commit/b9896f8d8): View
- [`a7d2a2b6d`](https://github.com/simple-robot/simpler-robot/commit/a7d2a2b6d): timestamp serializer
- [`42f08c7ce`](https://github.com/simple-robot/simpler-robot/commit/42f08c7ce): Flow to Stream
- [`712455305..bb0efcfff`](https://github.com/simple-robot/simpler-robot/compare/712455305..42f08c7ce): Manager
- [`df517be07`](https://github.com/simple-robot/simpler-robot/commit/df517be07): Infos
- [`329f693c1`](https://github.com/simple-robot/simpler-robot/commit/329f693c1): EventManager
- [`2ca6ee4e8`](https://github.com/simple-robot/simpler-robot/commit/2ca6ee4e8): component
- [`1009a1bb5`](https://github.com/simple-robot/simpler-robot/commit/1009a1bb5): some blocking api
- [`e47387b9f`](https://github.com/simple-robot/simpler-robot/commit/e47387b9f): 调整结构
- [`d622d946f`](https://github.com/simple-robot/simpler-robot/commit/d622d946f): tencent component
- [`a8f715858`](https://github.com/simple-robot/simpler-robot/commit/a8f715858): modules
- [`2dab3c951`](https://github.com/simple-robot/simpler-robot/commit/2dab3c951): Message action
- [`c7798a81b`](https://github.com/simple-robot/simpler-robot/commit/c7798a81b): 移除部分action
- [`2ae043172`](https://github.com/simple-robot/simpler-robot/commit/2ae043172): 组织，分组
- [`f8ae5eec1`](https://github.com/simple-robot/simpler-robot/commit/f8ae5eec1): Messages
- [`ef1141bad`](https://github.com/simple-robot/simpler-robot/commit/ef1141bad): Authors
- [`df2cfd3d6`](https://github.com/simple-robot/simpler-robot/commit/df2cfd3d6): Event
- [`a89825365`](https://github.com/simple-robot/simpler-robot/commit/a89825365): MessageContent
- [`5d7ed3ea2`](https://github.com/simple-robot/simpler-robot/commit/5d7ed3ea2): action, reply message
- [`dd909f235`](https://github.com/simple-robot/simpler-robot/commit/dd909f235): Attribute, component
- [`0bbc20396`](https://github.com/simple-robot/simpler-robot/commit/0bbc20396): Component SPI
- [`476c0b45e`](https://github.com/simple-robot/simpler-robot/commit/476c0b45e): Components get
- [`4b748c063`](https://github.com/simple-robot/simpler-robot/commit/4b748c063): BotManager
- [`f78fd4ad9`](https://github.com/simple-robot/simpler-robot/commit/f78fd4ad9): Attr
- [`66383dcf8`](https://github.com/simple-robot/simpler-robot/commit/66383dcf8): 组织信息
- [`91e3cf99f`](https://github.com/simple-robot/simpler-robot/commit/91e3cf99f): publish to local config
- [`dfcfada75`](https://github.com/simple-robot/simpler-robot/commit/dfcfada75): move api to apis, and add submodule for tencent-guild
- [`d5e275f48`](https://github.com/simple-robot/simpler-robot/commit/d5e275f48): 变更事件 时间戳
- [`43a6f1716`](https://github.com/simple-robot/simpler-robot/commit/43a6f1716): Events
- [`2a48b8f86`](https://github.com/simple-robot/simpler-robot/commit/2a48b8f86): Component
- [`47a122b82`](https://github.com/simple-robot/simpler-robot/commit/47a122b82): 事件，行为，ID，请求
- [`577e02c1f`](https://github.com/simple-robot/simpler-robot/commit/577e02c1f): request event
- [`680b988ae`](https://github.com/simple-robot/simpler-robot/commit/680b988ae): Request event
- [`ddda26be4`](https://github.com/simple-robot/simpler-robot/commit/ddda26be4): 组织，限流器
- [`5e132564f`](https://github.com/simple-robot/simpler-robot/commit/5e132564f): 事件处理
- [`af66814e4`](https://github.com/simple-robot/simpler-robot/commit/af66814e4): Event Manager
- [`b3c66a853`](https://github.com/simple-robot/simpler-robot/commit/b3c66a853): 核心 - 事件管理
- [`bd1e080f7`](https://github.com/simple-robot/simpler-robot/commit/bd1e080f7): listeners with filter
- [`efb023a2f`](https://github.com/simple-robot/simpler-robot/commit/efb023a2f): 拦截器，过滤器
- [`7e8c8c7d7`](https://github.com/simple-robot/simpler-robot/commit/7e8c8c7d7): ID, listener
- [`632dd99ec`](https://github.com/simple-robot/simpler-robot/commit/632dd99ec): 定义，消息
- [`9fcb4ae8f`](https://github.com/simple-robot/simpler-robot/commit/9fcb4ae8f): Result
- [`4ef719157`](https://github.com/simple-robot/simpler-robot/commit/4ef719157): 事件, 定义, 行为, 提供者
- [`ba9b24337`](https://github.com/simple-robot/simpler-robot/commit/ba9b24337): 事件相关
- [`c1dba6d80`](https://github.com/simple-robot/simpler-robot/commit/c1dba6d80): 放弃多平台；annotation和api定义
- [`7b837c4ea`](https://github.com/simple-robot/simpler-robot/commit/7b837c4ea): 处理器
- [`8e3d917c3`](https://github.com/simple-robot/simpler-robot/commit/8e3d917c3): 事件流程上下文
- [`4e6c9038c`](https://github.com/simple-robot/simpler-robot/commit/4e6c9038c): 各种基础接口定义
- [`62272fc64`](https://github.com/simple-robot/simpler-robot/commit/62272fc64): 移动异常类
- [`bf2dcd60f`](https://github.com/simple-robot/simpler-robot/commit/bf2dcd60f): Gradlew
- [`7e5ef7682`](https://github.com/simple-robot/simpler-robot/commit/7e5ef7682): ID
- [`5475f8582`](https://github.com/simple-robot/simpler-robot/commit/5475f8582): 事件，拦截
- [`1c80e8e67`](https://github.com/simple-robot/simpler-robot/commit/1c80e8e67): 拦截器，处理器
- [`baae7f670..b88db64ee`](https://github.com/simple-robot/simpler-robot/compare/baae7f670..1c80e8e67): ID
- [`f1b1a6e23`](https://github.com/simple-robot/simpler-robot/commit/f1b1a6e23): Project conf
- [`0a73dacaa`](https://github.com/simple-robot/simpler-robot/commit/0a73dacaa): conf
- [`266281b06`](https://github.com/simple-robot/simpler-robot/commit/266281b06): Dokka config
- [`494aec776`](https://github.com/simple-robot/simpler-robot/commit/494aec776): update copyright and license
- [`a8deca991..071b89d13`](https://github.com/simple-robot/simpler-robot/compare/a8deca991..494aec776): ID
- [`92f728fc2`](https://github.com/simple-robot/simpler-robot/commit/92f728fc2): ID and buildSrc
- [`ae28e3374..d902e7bff`](https://github.com/simple-robot/simpler-robot/compare/ae28e3374..92f728fc2): update
- [`2bf7b4d40`](https://github.com/simple-robot/simpler-robot/commit/2bf7b4d40): update amend
- [`66ddcd6c1`](https://github.com/simple-robot/simpler-robot/commit/66ddcd6c1): ID
- [`86ad08daa`](https://github.com/simple-robot/simpler-robot/commit/86ad08daa): Logger
- [`b452fb816`](https://github.com/simple-robot/simpler-robot/commit/b452fb816): Logger & i18n
- [`d139c7b3a`](https://github.com/simple-robot/simpler-robot/commit/d139c7b3a): annotation module
- [`1ea57655b`](https://github.com/simple-robot/simpler-robot/commit/1ea57655b): okio
- [`7c0812266`](https://github.com/simple-robot/simpler-robot/commit/7c0812266): The ID
- [`980257b39`](https://github.com/simple-robot/simpler-robot/commit/980257b39): Messages & Events
- [`6ce9d184a`](https://github.com/simple-robot/simpler-robot/commit/6ce9d184a): Messages
- [`3885f038e`](https://github.com/simple-robot/simpler-robot/commit/3885f038e): Messages.
- [`d260686e1`](https://github.com/simple-robot/simpler-robot/commit/d260686e1): Internal
- [`33db3fcbd`](https://github.com/simple-robot/simpler-robot/commit/33db3fcbd): Messages
- [`d20ce149b`](https://github.com/simple-robot/simpler-robot/commit/d20ce149b): Message, and test
- [`ba29f30dc`](https://github.com/simple-robot/simpler-robot/commit/ba29f30dc): Messages
- [`baf45c786`](https://github.com/simple-robot/simpler-robot/commit/baf45c786): Message, and test
- [`a81c64ca6`](https://github.com/simple-robot/simpler-robot/commit/a81c64ca6): Message
- [`86cb673f8`](https://github.com/simple-robot/simpler-robot/commit/86cb673f8): List
- [`e169c10b5`](https://github.com/simple-robot/simpler-robot/commit/e169c10b5): attribute
- [`c5ef4ff1d..9c6629283`](https://github.com/simple-robot/simpler-robot/compare/c5ef4ff1d..e169c10b5): Component
- [`a1656624c`](https://github.com/simple-robot/simpler-robot/commit/a1656624c): Message
- [`f77f17772`](https://github.com/simple-robot/simpler-robot/commit/f77f17772): Bot Manager
- [`bfd2232c6`](https://github.com/simple-robot/simpler-robot/commit/bfd2232c6): Result serializer
- [`ca616e871`](https://github.com/simple-robot/simpler-robot/commit/ca616e871): 部分接口定义: Result : FutureResult
- [`b9ce69126`](https://github.com/simple-robot/simpler-robot/commit/b9ce69126): 部分接口定义: Result
- [`2f3948d51`](https://github.com/simple-robot/simpler-robot/commit/2f3948d51): 部分接口定义。
- [`980ad0583`](https://github.com/simple-robot/simpler-robot/commit/980ad0583): Api
- [`14852d543..189161d51`](https://github.com/simple-robot/simpler-robot/compare/14852d543..980ad0583): Update gradle scripts.
- [`70bb72d8e`](https://github.com/simple-robot/simpler-robot/commit/70bb72d8e): :bulb: 添加注释说明
- [`00500c404`](https://github.com/simple-robot/simpler-robot/commit/00500c404): Add icon file
- [`4d65550cc`](https://github.com/simple-robot/simpler-robot/commit/4d65550cc): :see_no_evil: Adding or updating a .gitignore file.
- [`f282146e2`](https://github.com/simple-robot/simpler-robot/commit/f282146e2): update .idea files
- [`1db51ada1`](https://github.com/simple-robot/simpler-robot/commit/1db51ada1): Gradle buildSrc
- [`d38a05056`](https://github.com/simple-robot/simpler-robot/commit/d38a05056): Api module
- [`7c846f98e..ff3c71cd3`](https://github.com/simple-robot/simpler-robot/compare/7c846f98e..d38a05056): :tada: Initial project.

## v2.4.0

> Release & Pull Notes: [v2.4.0](https://github.com/simple-robot/simpler-robot/releases/tag/v2.4.0)
>
> Commit compare: [v3.0.0-beta-M1..v2.4.0](https://github.com/simple-robot/simpler-robot/compare/v3.0.0-beta-M1..v2.4.0)

- [`8111e54ad`](https://github.com/simple-robot/simpler-robot/commit/8111e54ad): 优化 import
- [`796eae5dd`](https://github.com/simple-robot/simpler-robot/commit/796eae5dd): 准备发布 v2.4.0
- [`87fc8f27f`](https://github.com/simple-robot/simpler-robot/commit/87fc8f27f): 支持直接使用ListenerFunction实例
- [`e7920fccd`](https://github.com/simple-robot/simpler-robot/commit/e7920fccd): 移除群宣传
- [`ba323e615`](https://github.com/simple-robot/simpler-robot/commit/ba323e615): 清理部分内容
- [`fe29cb5a8`](https://github.com/simple-robot/simpler-robot/commit/fe29cb5a8): 依赖版本更新
- [`75445a020`](https://github.com/simple-robot/simpler-robot/commit/75445a020): 调整CatCode解析判断顺序
- [`7d26b10e3`](https://github.com/simple-robot/simpler-robot/commit/7d26b10e3): 更新hutool标记版本
- [`44f1eb5c9`](https://github.com/simple-robot/simpler-robot/commit/44f1eb5c9): test
- [`2055ceec0`](https://github.com/simple-robot/simpler-robot/commit/2055ceec0): update to v2.3.8
- [`98024f69b`](https://github.com/simple-robot/simpler-robot/commit/98024f69b): 移除github配置
- [`7aa7c7e8d`](https://github.com/simple-robot/simpler-robot/commit/7aa7c7e8d): test
- [`0179e7efb`](https://github.com/simple-robot/simpler-robot/commit/0179e7efb): 临时针对[mirai issue#1852](https://github.com/mamoe/mirai/issues/1852) 进行特殊处理
- [`917653d30`](https://github.com/simple-robot/simpler-robot/commit/917653d30): update readme
- [`ad8efda79`](https://github.com/simple-robot/simpler-robot/commit/ad8efda79): update version
- [`5502c0428`](https://github.com/simple-robot/simpler-robot/commit/5502c0428): update README.md
- [`d0293ea5c`](https://github.com/simple-robot/simpler-robot/commit/d0293ea5c): update to v2.3.5 - 尝试使用另一种办法解决mirai 下login异常时 logback 堆栈溢出问题。 - 修复mirai下GroupMsg的groupMsgType无法区分匿名用户的问题
- [`6e0ceba72`](https://github.com/simple-robot/simpler-robot/commit/6e0ceba72): test
- [`2d752df55`](https://github.com/simple-robot/simpler-robot/commit/2d752df55): update to v2.3.5 尝试使用另一种办法解决mirai 下login异常时 logback 堆栈溢出问题。
- [`eec05b038..5de6faa50`](https://github.com/simple-robot/simpler-robot/compare/eec05b038..2d752df55): WOW! new logo!
- [`5ec9e92b9..7d93e1d4e`](https://github.com/simple-robot/simpler-robot/compare/5ec9e92b9..5de6faa50): logger test
- [`a9efc2123`](https://github.com/simple-robot/simpler-robot/commit/a9efc2123): Update to v2.3.4
- [`86ab31cd2`](https://github.com/simple-robot/simpler-robot/commit/86ab31cd2): fix #143
- [`a17fe3793`](https://github.com/simple-robot/simpler-robot/commit/a17fe3793): README for 3.0.0
- [`7e2965395`](https://github.com/simple-robot/simpler-robot/commit/7e2965395): update to v2.3.3
- [`97b814b19`](https://github.com/simple-robot/simpler-robot/commit/97b814b19): Update to v2.3.2
- [`50a4d3d34`](https://github.com/simple-robot/simpler-robot/commit/50a4d3d34): fix #182
- [`68e494548`](https://github.com/simple-robot/simpler-robot/commit/68e494548): Update to v2.3.1
- [`6e23dd218`](https://github.com/simple-robot/simpler-robot/commit/6e23dd218): Update workflow
- [`310c0c16f`](https://github.com/simple-robot/simpler-robot/commit/310c0c16f): Update to v2.3.0-DEV.1
- [`afea1c755`](https://github.com/simple-robot/simpler-robot/commit/afea1c755): 更新工作流
- [`1b4843c05`](https://github.com/simple-robot/simpler-robot/commit/1b4843c05): - mirai组件: 更新新的群文件相关API
- [`3dc49a42e`](https://github.com/simple-robot/simpler-robot/commit/3dc49a42e): - mirai组件: 更新新的群文件API
- [`595f93c16..714e838e7`](https://github.com/simple-robot/simpler-robot/compare/595f93c16..3dc49a42e): - mirai组件: 移除弃用配置项
- [`fc4cea12b`](https://github.com/simple-robot/simpler-robot/commit/fc4cea12b): - mirai组件支持 群解散消息事件 (BotLeaveEvent.Disband)
- [`2ef8c6eb0`](https://github.com/simple-robot/simpler-robot/commit/2ef8c6eb0): Mirai版本更新; 追加Image属性
- [`e3c9dd4f0..c85cdcd49`](https://github.com/simple-robot/simpler-robot/compare/e3c9dd4f0..2ef8c6eb0): 开黑啦 README
- [`48aec73ca`](https://github.com/simple-robot/simpler-robot/commit/48aec73ca): Update version to v2.3.0
- [`2c95254f5`](https://github.com/simple-robot/simpler-robot/commit/2c95254f5): Update version to v2.3.0-BETA.6
- [`07b3f8100`](https://github.com/simple-robot/simpler-robot/commit/07b3f8100): Support for #179 Close #179
- [`eb58f59ce`](https://github.com/simple-robot/simpler-robot/commit/eb58f59ce): Update version to v2.3.0-BETA.5
- [`c81579cb8`](https://github.com/simple-robot/simpler-robot/commit/c81579cb8): Fix #177 在 v2.3.0-BETA.4 中出现的新问题
- [`fe8344937..b4f168cdd`](https://github.com/simple-robot/simpler-robot/compare/fe8344937..c81579cb8): Update version to v.2.3.0-BETA.4
- [`715aab16e..9de03c771`](https://github.com/simple-robot/simpler-robot/compare/715aab16e..b4f168cdd): Fix #176. Close #176
- [`7ec8f5d94`](https://github.com/simple-robot/simpler-robot/commit/7ec8f5d94): Fix #177 Close #177
- [`9e4c23ab4`](https://github.com/simple-robot/simpler-robot/commit/9e4c23ab4): Update version v2.3.0-BETA.3
- [`eecb8c06c`](https://github.com/simple-robot/simpler-robot/commit/eecb8c06c): Fix #175 close #175
- [`7ad999495`](https://github.com/simple-robot/simpler-robot/commit/7ad999495): test
- [`925d7dcc4`](https://github.com/simple-robot/simpler-robot/commit/925d7dcc4): Async func for sender and setter.
- [`e492600f9`](https://github.com/simple-robot/simpler-robot/commit/e492600f9): Mirai sender getter setter update and update version to v2.3.0-BETA.2
- [`1a5b61be1..b50ab5439`](https://github.com/simple-robot/simpler-robot/compare/1a5b61be1..e492600f9): README.md
- [`4b59caf9c`](https://github.com/simple-robot/simpler-robot/commit/4b59caf9c): for v2.3.0-BETA.1 and kaiheila component v0.0.1-PREVIEW
- [`3ea9cfb03`](https://github.com/simple-robot/simpler-robot/commit/3ea9cfb03): Khl for simbot v0.0.1-PREVIEW
- [`6ed01cd4d`](https://github.com/simple-robot/simpler-robot/commit/6ed01cd4d): Update khl README.md
- [`09668c30a`](https://github.com/simple-robot/simpler-robot/commit/09668c30a): Edit some
- [`258640550`](https://github.com/simple-robot/simpler-robot/commit/258640550): Khl v3 api component
- [`d77022084`](https://github.com/simple-robot/simpler-robot/commit/d77022084): Rename for kaiheila module package
- [`941ab2a2e`](https://github.com/simple-robot/simpler-robot/commit/941ab2a2e): Normal event and test
- [`8b9c15826`](https://github.com/simple-robot/simpler-robot/commit/8b9c15826): Bot Listener
- [`932877b6f`](https://github.com/simple-robot/simpler-robot/commit/932877b6f): Text event
- [`61c42cb1a`](https://github.com/simple-robot/simpler-robot/commit/61c42cb1a): Getters
- [`df5bdeb95`](https://github.com/simple-robot/simpler-robot/commit/df5bdeb95): Khl Senders and getters
- [`72ebc992e..0c9b95fc4`](https://github.com/simple-robot/simpler-robot/compare/72ebc992e..df5bdeb95): For getter suspend function
- [`dba0c0859`](https://github.com/simple-robot/simpler-robot/commit/dba0c0859): README.md
- [`4d05b1aec`](https://github.com/simple-robot/simpler-robot/commit/4d05b1aec): Setter for suspend fun
- [`564ed7420`](https://github.com/simple-robot/simpler-robot/commit/564ed7420): Delete some test files.
- [`461f59e1b..699cabd80`](https://github.com/simple-robot/simpler-robot/compare/461f59e1b..564ed7420): Khl setters
- [`cb4f7e1cf`](https://github.com/simple-robot/simpler-robot/commit/cb4f7e1cf): Bot
- [`7f3e41617..eee8c7fef`](https://github.com/simple-robot/simpler-robot/compare/7f3e41617..cb4f7e1cf): Khl component.
- [`e58025af4`](https://github.com/simple-robot/simpler-robot/commit/e58025af4): Message Events.
- [`8ce8fac24`](https://github.com/simple-robot/simpler-robot/commit/8ce8fac24): Video events and image events
- [`db19cb52a`](https://github.com/simple-robot/simpler-robot/commit/db19cb52a): Message Events
- [`3838dabfc`](https://github.com/simple-robot/simpler-robot/commit/3838dabfc): Kaiheila
- [`4fa2f6629..b735d8965`](https://github.com/simple-robot/simpler-robot/compare/4fa2f6629..3838dabfc): Tips.
- [`ebf149ea1`](https://github.com/simple-robot/simpler-robot/commit/ebf149ea1): Update tips.
- [`cbb528511..09903e7fb`](https://github.com/simple-robot/simpler-robot/compare/cbb528511..ebf149ea1): Update .ignore file and remove some .idea file.
- [`0087a84e8`](https://github.com/simple-robot/simpler-robot/commit/0087a84e8): Remove some .idea files
- [`f062bf013`](https://github.com/simple-robot/simpler-robot/commit/f062bf013): :see_no_evil: Adding or updating a .gitignore file.
- [`dda0bb667..61d776825`](https://github.com/simple-robot/simpler-robot/compare/dda0bb667..f062bf013): For .idea file
- [`6128145d6`](https://github.com/simple-robot/simpler-robot/commit/6128145d6): For some .idea file
- [`525b1df9b`](https://github.com/simple-robot/simpler-robot/commit/525b1df9b): Session's Waiting, 优化超时 For some .idea file
- [`4e757020c..1da317f13`](https://github.com/simple-robot/simpler-robot/compare/4e757020c..525b1df9b): For some .idea file
- [`901ab2e00`](https://github.com/simple-robot/simpler-robot/commit/901ab2e00): update .idea files
- [`624f539e0..962b297a2`](https://github.com/simple-robot/simpler-robot/compare/624f539e0..901ab2e00): Guild role events and guild member events.
- [`d212e0191`](https://github.com/simple-robot/simpler-robot/commit/d212e0191): Private message events
- [`4b12dc2b1`](https://github.com/simple-robot/simpler-robot/commit/4b12dc2b1): Message events
- [`65cfbbf2a..e20506c48`](https://github.com/simple-robot/simpler-robot/compare/65cfbbf2a..4b12dc2b1): Guild event extra bodys.
- [`b51dfd007`](https://github.com/simple-robot/simpler-robot/commit/b51dfd007): mirai合并转发消息
- [`952ddc203`](https://github.com/simple-robot/simpler-robot/commit/952ddc203): Mirai forward message in v2.3.0-ALPHA.6 for #169 close #169
- [`a10ac0f61`](https://github.com/simple-robot/simpler-robot/commit/a10ac0f61): For #169
- [`b015ad9c4`](https://github.com/simple-robot/simpler-robot/commit/b015ad9c4): New branch for support mirai forward message; for #169
- [`2f64fb37e`](https://github.com/simple-robot/simpler-robot/commit/2f64fb37e): 清理pom
- [`81b3f2027..84e1ea830`](https://github.com/simple-robot/simpler-robot/compare/81b3f2027..2f64fb37e): 增加注释
- [`9527fcdfd`](https://github.com/simple-robot/simpler-robot/commit/9527fcdfd): 优化会话等待与回调；增加阻塞等待； v2.3.0-ALPHA.5 for #142
- [`3505e1587`](https://github.com/simple-robot/simpler-robot/commit/3505e1587): :zap: 优化持续会话部分内容与日志，清除遗留代码
- [`4349a190a`](https://github.com/simple-robot/simpler-robot/commit/4349a190a): Test file rename
- [`851b1e74c`](https://github.com/simple-robot/simpler-robot/commit/851b1e74c): :art: Improving structure / format of the code.
- [`e16943e1c`](https://github.com/simple-robot/simpler-robot/commit/e16943e1c): BotVerifyInfo提示优化
- [`c4cea6a63`](https://github.com/simple-robot/simpler-robot/commit/c4cea6a63): test
- [`50b785383`](https://github.com/simple-robot/simpler-robot/commit/50b785383): #142 基础实现完成 in v.2.3.0-ALPHA.4 close #142
- [`4963e9a62`](https://github.com/simple-robot/simpler-robot/commit/4963e9a62): ContinuousSessionScopeContext internal for double map #142
- [`30e55fa6f`](https://github.com/simple-robot/simpler-robot/commit/30e55fa6f): Matcher
- [`fa92a9fa5`](https://github.com/simple-robot/simpler-robot/commit/fa92a9fa5): FileUtil for Session MatchType #142
- [`44741280c`](https://github.com/simple-robot/simpler-robot/commit/44741280c): CoreListenerContextFactory coroutineScope with CoroutineName for #142
- [`8fcdd11c7`](https://github.com/simple-robot/simpler-robot/commit/8fcdd11c7): 作用域: 持续会话 ContinuousSession for #142
- [`b16f454dc`](https://github.com/simple-robot/simpler-robot/commit/b16f454dc): Event Locator
- [`7280008bc`](https://github.com/simple-robot/simpler-robot/commit/7280008bc): Events for user event
- [`379f24092`](https://github.com/simple-robot/simpler-robot/commit/379f24092): 优化 BotVerifyInfo 的相关内容
- [`1f59ee433`](https://github.com/simple-robot/simpler-robot/commit/1f59ee433): Update pom
- [`44ea6ab16`](https://github.com/simple-robot/simpler-robot/commit/44ea6ab16): Update Mirai version to v2.7.1 to fix #159 close #159
- [`64122b44a`](https://github.com/simple-robot/simpler-robot/commit/64122b44a): Update Mirai version to v2.7.1
- [`5c0824fc9`](https://github.com/simple-robot/simpler-robot/commit/5c0824fc9): Mirai message Content
- [`ed01315d4`](https://github.com/simple-robot/simpler-robot/commit/ed01315d4): listener manager
- [`aa507bce7`](https://github.com/simple-robot/simpler-robot/commit/aa507bce7): Sender 重构为 suspend 为主的函数 and for version to v2.3.0-ALPHA.2 for #166
- [`1ac0b2883`](https://github.com/simple-robot/simpler-robot/commit/1ac0b2883): pom
- [`de0a2cc53`](https://github.com/simple-robot/simpler-robot/commit/de0a2cc53): update logo show
- [`e1a09ddcd`](https://github.com/simple-robot/simpler-robot/commit/e1a09ddcd): Guild event for user reaction event.
- [`32a0be2a8`](https://github.com/simple-robot/simpler-robot/commit/32a0be2a8): khl serializer todo
- [`0509e419a`](https://github.com/simple-robot/simpler-robot/commit/0509e419a): Card object
- [`30fdf3962`](https://github.com/simple-robot/simpler-robot/commit/30fdf3962): KMarkdown
- [`c1ba7b828`](https://github.com/simple-robot/simpler-robot/commit/c1ba7b828): object -> objects
- [`133aadbb5`](https://github.com/simple-robot/simpler-robot/commit/133aadbb5): khl event for text
- [`0c2fa8cb3`](https://github.com/simple-robot/simpler-robot/commit/0c2fa8cb3): New banner show with version info
- [`35b9bde2b`](https://github.com/simple-robot/simpler-robot/commit/35b9bde2b): new Logo
- [`446e3406c`](https://github.com/simple-robot/simpler-robot/commit/446e3406c): fix #163 in v2.2.2 close #163
- [`fa72e0ed3`](https://github.com/simple-robot/simpler-robot/commit/fa72e0ed3): remove some test file
- [`6b65f94b5`](https://github.com/simple-robot/simpler-robot/commit/6b65f94b5): @Async 优先级更高
- [`ce5f9beda..f8d9bac36`](https://github.com/simple-robot/simpler-robot/compare/ce5f9beda..6b65f94b5): For v2.3.0-ALPHA.1
- [`6cc02ec59`](https://github.com/simple-robot/simpler-robot/commit/6cc02ec59): spare -> isSpare
- [`871a394df`](https://github.com/simple-robot/simpler-robot/commit/871a394df): Async func with spare for #161 plugin warn for #154
- [`37ba38081`](https://github.com/simple-robot/simpler-robot/commit/37ba38081): test pom
- [`4f8956d41..64fd70a79`](https://github.com/simple-robot/simpler-robot/compare/4f8956d41..37ba38081): fix test error
- [`a407592c0`](https://github.com/simple-robot/simpler-robot/commit/a407592c0): CoreListenerManager for #161 close #161
- [`65a78a336`](https://github.com/simple-robot/simpler-robot/commit/65a78a336): definition @Async and modify listenerFunction for #161
- [`ab5150c0b`](https://github.com/simple-robot/simpler-robot/commit/ab5150c0b): fix khl pom
- [`3c9267467..5788e3a6b`](https://github.com/simple-robot/simpler-robot/compare/3c9267467..ab5150c0b): 监听函数构建器 for #154
- [`c14190b27`](https://github.com/simple-robot/simpler-robot/commit/c14190b27): 实现动态更新、删除 for #154
- [`7d1d83bd4`](https://github.com/simple-robot/simpler-robot/commit/7d1d83bd4): listener manager lock
- [`6658d8dbf`](https://github.com/simple-robot/simpler-robot/commit/6658d8dbf): kaiheila event
- [`f4b34c123`](https://github.com/simple-robot/simpler-robot/commit/f4b34c123): intimacy's api
- [`e38a9c418`](https://github.com/simple-robot/simpler-robot/commit/e38a9c418): invite data
- [`a0cafc640`](https://github.com/simple-robot/simpler-robot/commit/a0cafc640): invite api
- [`50a41e1e6`](https://github.com/simple-robot/simpler-robot/commit/50a41e1e6): merge dev-#154-plugins branch
- [`3ba8c7a18`](https://github.com/simple-robot/simpler-robot/commit/3ba8c7a18): URL's resource fix
- [`5af985013`](https://github.com/simple-robot/simpler-robot/commit/5af985013): Url cache false
- [`5a3184da7`](https://github.com/simple-robot/simpler-robot/commit/5a3184da7): cache
- [`f8d909f45`](https://github.com/simple-robot/simpler-robot/commit/f8d909f45): PluginManager File load fix for #154
- [`88dedf1c3`](https://github.com/simple-robot/simpler-robot/commit/88dedf1c3): PluginManager and test for #154
- [`589d19017`](https://github.com/simple-robot/simpler-robot/commit/589d19017): PluginManager for #154
- [`d97520348`](https://github.com/simple-robot/simpler-robot/commit/d97520348): 动态插件管理
- [`344f9edcb`](https://github.com/simple-robot/simpler-robot/commit/344f9edcb): 监听函数管理器与分组管理器
- [`f08ab2b84`](https://github.com/simple-robot/simpler-robot/commit/f08ab2b84): 监听函数管理器调整以及插件读取
- [`5ce0c27ac`](https://github.com/simple-robot/simpler-robot/commit/5ce0c27ac): 动态类加载器
- [`2d5a4ba1a`](https://github.com/simple-robot/simpler-robot/commit/2d5a4ba1a): plugin loader?
- [`9e5597296`](https://github.com/simple-robot/simpler-robot/commit/9e5597296): remove some
- [`803a661b9`](https://github.com/simple-robot/simpler-robot/commit/803a661b9): File Sync
- [`70d09eb2b`](https://github.com/simple-robot/simpler-robot/commit/70d09eb2b): 文件监听器
- [`eddabf672..476fb5426`](https://github.com/simple-robot/simpler-robot/compare/eddabf672..70d09eb2b): Plugin system for #154
- [`dc0f583e4`](https://github.com/simple-robot/simpler-robot/commit/dc0f583e4): New banner show with version info
- [`15d7e8cb0`](https://github.com/simple-robot/simpler-robot/commit/15d7e8cb0): For invite api
- [`f8beb5be2`](https://github.com/simple-robot/simpler-robot/commit/f8beb5be2): For GuildRole
- [`7c8f9a77b`](https://github.com/simple-robot/simpler-robot/commit/7c8f9a77b): new Logo
- [`73edba6de`](https://github.com/simple-robot/simpler-robot/commit/73edba6de): Update ParametersAppender
- [`846c10fac`](https://github.com/simple-robot/simpler-robot/commit/846c10fac): update ApiData
- [`c7a3bcce7`](https://github.com/simple-robot/simpler-robot/commit/c7a3bcce7): update some opt
- [`57f7564c1`](https://github.com/simple-robot/simpler-robot/commit/57f7564c1): test
- [`1cdb59001`](https://github.com/simple-robot/simpler-robot/commit/1cdb59001): for v2.2.1 and update mirai to v2.7.0
- [`9cc0545f2`](https://github.com/simple-robot/simpler-robot/commit/9cc0545f2): mirai 2.7.0 and fix for MiraiMessageParser.kt
- [`ed270c6d5`](https://github.com/simple-robot/simpler-robot/commit/ed270c6d5): for dependence
- [`3ffdf3658`](https://github.com/simple-robot/simpler-robot/commit/3ffdf3658): For guild role
- [`d5e261064`](https://github.com/simple-robot/simpler-robot/commit/d5e261064): Update README.md
- [`2a0396a4c`](https://github.com/simple-robot/simpler-robot/commit/2a0396a4c): For v2.2.0 releases
- [`b90177eaa`](https://github.com/simple-robot/simpler-robot/commit/b90177eaa): Asset
- [`7f7b70e43`](https://github.com/simple-robot/simpler-robot/commit/7f7b70e43): Me、Asset、Direct Message、Guild Role
- [`508ca29f8`](https://github.com/simple-robot/simpler-robot/commit/508ca29f8): Me、Asset、Direct Message
- [`3a092f02a`](https://github.com/simple-robot/simpler-robot/commit/3a092f02a): Update message and direct message
- [`db8614dfb`](https://github.com/simple-robot/simpler-robot/commit/db8614dfb): 用户私聊相关
- [`1dd211af4`](https://github.com/simple-robot/simpler-robot/commit/1dd211af4): 私聊会话相关
- [`661f64831..66ab01138`](https://github.com/simple-robot/simpler-robot/compare/661f64831..1dd211af4): 优化ApiData.Req, 频道消息相关
- [`bc5e88923`](https://github.com/simple-robot/simpler-robot/commit/bc5e88923): update tests and rebase
- [`627e68d07`](https://github.com/simple-robot/simpler-robot/commit/627e68d07): mute
- [`f221c69a2..9fe439c83`](https://github.com/simple-robot/simpler-robot/compare/f221c69a2..627e68d07): for v3 bot
- [`a833f2842`](https://github.com/simple-robot/simpler-robot/commit/a833f2842): v3 bot
- [`4dd1fd611`](https://github.com/simple-robot/simpler-robot/commit/4dd1fd611): test
- [`1af6cb6e0`](https://github.com/simple-robot/simpler-robot/commit/1af6cb6e0): gateway test
- [`940c953d8`](https://github.com/simple-robot/simpler-robot/commit/940c953d8): channel view、create、delete
- [`fa12cc2a6`](https://github.com/simple-robot/simpler-robot/commit/fa12cc2a6): channel list
- [`513d6b7bd`](https://github.com/simple-robot/simpler-robot/commit/513d6b7bd): 频道相关
- [`b4e503bd2`](https://github.com/simple-robot/simpler-robot/commit/b4e503bd2): mute create
- [`9de5dc7d6`](https://github.com/simple-robot/simpler-robot/commit/9de5dc7d6): kaiheila api : - muteList - leave - kickout
- [`84c6815b5`](https://github.com/simple-robot/simpler-robot/commit/84c6815b5): :bulb: 更新注释
- [`59b325cbb..75245c21e`](https://github.com/simple-robot/simpler-robot/compare/59b325cbb..84c6815b5): api message create and test
- [`b919d3e5c`](https://github.com/simple-robot/simpler-robot/commit/b919d3e5c): update module name
- [`ddb08c9ac`](https://github.com/simple-robot/simpler-robot/commit/ddb08c9ac): kaiheila api : message create.
- [`d6b1310dc`](https://github.com/simple-robot/simpler-robot/commit/d6b1310dc): kaiheila api : guild view and test.
- [`2756efbaf`](https://github.com/simple-robot/simpler-robot/commit/2756efbaf): kaiheila api : guild list for test.
- [`333823c3e..a2e878707`](https://github.com/simple-robot/simpler-robot/compare/333823c3e..2756efbaf): ws test
- [`a9047cf26..ff36944b9`](https://github.com/simple-robot/simpler-robot/compare/a9047cf26..a2e878707): api some
- [`46749b400`](https://github.com/simple-robot/simpler-robot/commit/46749b400): gateway api test
- [`955f9ac29`](https://github.com/simple-robot/simpler-robot/commit/955f9ac29): khl gateway api test
- [`b2edaa214`](https://github.com/simple-robot/simpler-robot/commit/b2edaa214): khl api test and rename
- [`c01e04721`](https://github.com/simple-robot/simpler-robot/commit/c01e04721): unit test
- [`a72db88f8..e76bd8205`](https://github.com/simple-robot/simpler-robot/compare/a72db88f8..c01e04721): serializer for guild list api resp.
- [`90626003a..18eaef13c`](https://github.com/simple-robot/simpler-robot/compare/90626003a..e76bd8205): bot info
- [`8270a89b4`](https://github.com/simple-robot/simpler-robot/commit/8270a89b4): api data req base
- [`143996fc5`](https://github.com/simple-robot/simpler-robot/commit/143996fc5): api req builder
- [`f12d8da61`](https://github.com/simple-robot/simpler-robot/commit/f12d8da61): kaiheila api conf
- [`7f6c2d5ad`](https://github.com/simple-robot/simpler-robot/commit/7f6c2d5ad): api data for v3
- [`6ec6d6f20`](https://github.com/simple-robot/simpler-robot/commit/6ec6d6f20): server api for v3
- [`1223bfea3`](https://github.com/simple-robot/simpler-robot/commit/1223bfea3): api实现模块
- [`16f3a972b..fba8c61d1`](https://github.com/simple-robot/simpler-robot/compare/16f3a972b..1223bfea3): update readme
- [`441f2663a`](https://github.com/simple-robot/simpler-robot/commit/441f2663a): update info. move module
- [`79a9cf0c3..52a063b36`](https://github.com/simple-robot/simpler-robot/compare/79a9cf0c3..441f2663a): Text event extra
- [`354abc6f7`](https://github.com/simple-robot/simpler-robot/commit/354abc6f7): kmarkdown
- [`d85316e20`](https://github.com/simple-robot/simpler-robot/commit/d85316e20): guild 序列化
- [`1fea4917b`](https://github.com/simple-robot/simpler-robot/commit/1fea4917b): 信令测试
- [`7d81cec84`](https://github.com/simple-robot/simpler-robot/commit/7d81cec84): 信令定义
- [`6b92462cc`](https://github.com/simple-robot/simpler-robot/commit/6b92462cc): 开黑啦 信令
- [`df62f8b8e`](https://github.com/simple-robot/simpler-robot/commit/df62f8b8e): 开黑啦 objects定义 link #91
- [`63b9f7296`](https://github.com/simple-robot/simpler-robot/commit/63b9f7296): 开黑啦bot组件 objects
- [`e68b9892f`](https://github.com/simple-robot/simpler-robot/commit/e68b9892f): 开黑啦bot组件 ktx json
- [`af3edd3a8`](https://github.com/simple-robot/simpler-robot/commit/af3edd3a8): 开黑啦bot组件分支init
- [`e6825dd92`](https://github.com/simple-robot/simpler-robot/commit/e6825dd92): for v2.2.0-BETA.4
- [`edf2eba40`](https://github.com/simple-robot/simpler-robot/commit/edf2eba40): mirai custom event solver for #150 close #150
- [`a6d480da3`](https://github.com/simple-robot/simpler-robot/commit/a6d480da3): for 2.2.0-dev.15
- [`896d8654b`](https://github.com/simple-robot/simpler-robot/commit/896d8654b): Things and Auths for #149; close #149
- [`4b5ee19d7`](https://github.com/simple-robot/simpler-robot/commit/4b5ee19d7): Thing and auth
- [`1881dfdd2`](https://github.com/simple-robot/simpler-robot/commit/1881dfdd2): update GitHub issue template config
- [`dd6ea349e`](https://github.com/simple-robot/simpler-robot/commit/dd6ea349e): Update issue templates
- [`1e137fa3d`](https://github.com/simple-robot/simpler-robot/commit/1e137fa3d): Rename show-my-work to show-my-work.md
- [`7d483f9fb`](https://github.com/simple-robot/simpler-robot/commit/7d483f9fb): Create show-my-work
- [`51b625c17..c291be091`](https://github.com/simple-robot/simpler-robot/compare/51b625c17..7d483f9fb): for v2.2.0-BETA.3
- [`fe9e33bad`](https://github.com/simple-robot/simpler-robot/commit/fe9e33bad): event launch
- [`5659ad812`](https://github.com/simple-robot/simpler-robot/commit/5659ad812): channel flow
- [`856e73a23`](https://github.com/simple-robot/simpler-robot/commit/856e73a23): try-catch for Image(id)
- [`882ba6fbd`](https://github.com/simple-robot/simpler-robot/commit/882ba6fbd): for v2.2.0-BETA.2
- [`d11092034`](https://github.com/simple-robot/simpler-robot/commit/d11092034): fix #145 in v2.2.0-DEV.14 close #145
- [`e2264e530`](https://github.com/simple-robot/simpler-robot/commit/e2264e530): fix #145 in v2.2.0-DEV.13 close #145
- [`543d5c940`](https://github.com/simple-robot/simpler-robot/commit/543d5c940): t
- [`8259a7f9f..ace0cae84`](https://github.com/simple-robot/simpler-robot/compare/8259a7f9f..543d5c940): for v2.2.0-DEV.12
- [`2cd706ec7`](https://github.com/simple-robot/simpler-robot/commit/2cd706ec7): fix annotation getter
- [`703a59808`](https://github.com/simple-robot/simpler-robot/commit/703a59808): for v2.2.0-BETA.1
- [`ba266e8f8`](https://github.com/simple-robot/simpler-robot/commit/ba266e8f8): for #144 close #144
- [`266f4f89e`](https://github.com/simple-robot/simpler-robot/commit/266f4f89e): switch test and for v2.2.0-DEV.11
- [`dec67ef17`](https://github.com/simple-robot/simpler-robot/commit/dec67ef17): switch test
- [`0c042e9cc`](https://github.com/simple-robot/simpler-robot/commit/0c042e9cc): switch 注释
- [`48870f9e3`](https://github.com/simple-robot/simpler-robot/commit/48870f9e3): fix 非严格模式下的动态参数注入 & for v2.2.0-DEV.10
- [`95fb3534f`](https://github.com/simple-robot/simpler-robot/commit/95fb3534f): for suspend test
- [`af75295c9`](https://github.com/simple-robot/simpler-robot/commit/af75295c9): test
- [`03214f2f3`](https://github.com/simple-robot/simpler-robot/commit/03214f2f3): listener function switch for #119
- [`9b6fd2ccf`](https://github.com/simple-robot/simpler-robot/commit/9b6fd2ccf): try fix java.io.EOFException and for v2.2.0-DEV.9
- [`d49351336`](https://github.com/simple-robot/simpler-robot/commit/d49351336): for v2.2.0-DEV.8
- [`897cbfc97`](https://github.com/simple-robot/simpler-robot/commit/897cbfc97): annotated filter processor test for #112
- [`9b113c373..87ab8e5d5`](https://github.com/simple-robot/simpler-robot/compare/9b113c373..897cbfc97): annotated filter processor
- [`1e5180d37`](https://github.com/simple-robot/simpler-robot/commit/1e5180d37): for #69
- [`59accb4d6`](https://github.com/simple-robot/simpler-robot/commit/59accb4d6): for v2.2.0-DEV.7
- [`9721930f4`](https://github.com/simple-robot/simpler-robot/commit/9721930f4): 增加注释
- [`8a4104163`](https://github.com/simple-robot/simpler-robot/commit/8a4104163): Implementation via strict mode #139
- [`4c9fbbade`](https://github.com/simple-robot/simpler-robot/commit/4c9fbbade): strict mode for #139
- [`f7e000085`](https://github.com/simple-robot/simpler-robot/commit/f7e000085): for v2.2.0-DEV.6
- [`e4070a994..dc959c2bc`](https://github.com/simple-robot/simpler-robot/compare/e4070a994..f7e000085): suspend listener test
- [`9929586be`](https://github.com/simple-robot/simpler-robot/commit/9929586be): event logger and for v2.2.0-DEV.5
- [`e2aa2a55f`](https://github.com/simple-robot/simpler-robot/commit/e2aa2a55f): fix annotationUtil's bug for v2.2.0-DEV.4
- [`3de2986bb`](https://github.com/simple-robot/simpler-robot/commit/3de2986bb): for v2.2.0-DEV.3
- [`3569468ff`](https://github.com/simple-robot/simpler-robot/commit/3569468ff): for v2.2.0-DEV.2
- [`3f7792e2f`](https://github.com/simple-robot/simpler-robot/commit/3f7792e2f): for v2.2.0-DEV.1
- [`fbb509e2d`](https://github.com/simple-robot/simpler-robot/commit/fbb509e2d): for v2.1.2-DEV.1
- [`8d5b784c7`](https://github.com/simple-robot/simpler-robot/commit/8d5b784c7): move pkg
- [`fa349cd10`](https://github.com/simple-robot/simpler-robot/commit/fa349cd10): build instance for filter with dsl or lambda
- [`cce71f63f`](https://github.com/simple-robot/simpler-robot/commit/cce71f63f): Update test.yml
- [`1fef96305`](https://github.com/simple-robot/simpler-robot/commit/1fef96305): fix lovely cat
- [`15d55d299`](https://github.com/simple-robot/simpler-robot/commit/15d55d299): new listenerFunction and new ListenerFilter for #129 #131 #132 #130 #113 107
- [`a8a24ee82`](https://github.com/simple-robot/simpler-robot/commit/a8a24ee82): lovely cat sb starter
- [`690e71f30`](https://github.com/simple-robot/simpler-robot/commit/690e71f30): Rename .java to .kt
- [`b8df4e3de`](https://github.com/simple-robot/simpler-robot/commit/b8df4e3de): fix for lovely cat
- [`ccd7e6421`](https://github.com/simple-robot/simpler-robot/commit/ccd7e6421): Rename .java to .kt
- [`74eec597a`](https://github.com/simple-robot/simpler-robot/commit/74eec597a): inline
- [`e521d4d12`](https://github.com/simple-robot/simpler-robot/commit/e521d4d12): for #132 #131 #129 #107
- [`c087eec9a`](https://github.com/simple-robot/simpler-robot/commit/c087eec9a): for #128
- [`0cf769992`](https://github.com/simple-robot/simpler-robot/commit/0cf769992): mute
- [`c6c531003..bfa50eb66`](https://github.com/simple-robot/simpler-robot/compare/c6c531003..0cf769992): for v3 bot
- [`70a9525c5`](https://github.com/simple-robot/simpler-robot/commit/70a9525c5): v3 bot
- [`3ece5df37`](https://github.com/simple-robot/simpler-robot/commit/3ece5df37): for springboot annotation processor
- [`38f349cb5`](https://github.com/simple-robot/simpler-robot/commit/38f349cb5): tips
- [`c069cf2a4`](https://github.com/simple-robot/simpler-robot/commit/c069cf2a4): for 2.1.1
- [`523385d5c`](https://github.com/simple-robot/simpler-robot/commit/523385d5c): fix mvn err
- [`a94d9364e`](https://github.com/simple-robot/simpler-robot/commit/a94d9364e): add parent for GroupMsg
- [`eecbe8922`](https://github.com/simple-robot/simpler-robot/commit/eecbe8922): 临时移除模块
- [`a7933543e`](https://github.com/simple-robot/simpler-robot/commit/a7933543e): test
- [`5a915d935`](https://github.com/simple-robot/simpler-robot/commit/5a915d935): gateway test
- [`d1cb8bb15`](https://github.com/simple-robot/simpler-robot/commit/d1cb8bb15): channel view、create、delete
- [`1592eedb1`](https://github.com/simple-robot/simpler-robot/commit/1592eedb1): channel list
- [`39d762906`](https://github.com/simple-robot/simpler-robot/commit/39d762906): 频道相关
- [`7a9bcef7f..79c15573e`](https://github.com/simple-robot/simpler-robot/compare/7a9bcef7f..39d762906): add
- [`65ecfae21`](https://github.com/simple-robot/simpler-robot/commit/65ecfae21): mute create
- [`09a531702`](https://github.com/simple-robot/simpler-robot/commit/09a531702): kaiheila api : - muteList - leave - kickout
- [`7d93e6cbe`](https://github.com/simple-robot/simpler-robot/commit/7d93e6cbe): kaiheila api : nickname and test
- [`0b25be321`](https://github.com/simple-robot/simpler-robot/commit/0b25be321): kaiheila api : nickname.
- [`420112dcc`](https://github.com/simple-robot/simpler-robot/commit/420112dcc): :bulb: 更新注释
- [`10d97c836..d20b7cb63`](https://github.com/simple-robot/simpler-robot/compare/10d97c836..420112dcc): api message create and test
- [`260a5b8d6`](https://github.com/simple-robot/simpler-robot/commit/260a5b8d6): update module name
- [`175c62bc9`](https://github.com/simple-robot/simpler-robot/commit/175c62bc9): kaiheila api : message create.
- [`5433e19b8`](https://github.com/simple-robot/simpler-robot/commit/5433e19b8): kaiheila api : guild view and test.
- [`f647e17ff..40aa3069e`](https://github.com/simple-robot/simpler-robot/compare/f647e17ff..5433e19b8): Update README.md
- [`36cb04d31`](https://github.com/simple-robot/simpler-robot/commit/36cb04d31): kaiheila api : guild list for test.
- [`bb58b2f20`](https://github.com/simple-robot/simpler-robot/commit/bb58b2f20): for v2.1.0 release.
- [`43642d116`](https://github.com/simple-robot/simpler-robot/commit/43642d116): for v2.1.0-DEV.10 update ktx-core-jvm version mark todo for MsgProcessor some test
- [`b4417122b..4d0191023`](https://github.com/simple-robot/simpler-robot/compare/b4417122b..43642d116): for v2.1.0-RC.5 and mvn test
- [`c8071a039`](https://github.com/simple-robot/simpler-robot/commit/c8071a039): for v2.1.0-RC.5
- [`22aa57687`](https://github.com/simple-robot/simpler-robot/commit/22aa57687): for v2.1.0-DEV.9
- [`f8fcbb984..83f99bd71`](https://github.com/simple-robot/simpler-robot/compare/f8fcbb984..22aa57687): for v2.1.0-DEV.8
- [`d95955cdc..769b681a2`](https://github.com/simple-robot/simpler-robot/compare/d95955cdc..83f99bd71): ws test
- [`4c38ce2a8`](https://github.com/simple-robot/simpler-robot/commit/4c38ce2a8): for v2.1.0-RC.4
- [`df0e89f8f`](https://github.com/simple-robot/simpler-robot/commit/df0e89f8f): fix warn
- [`309b58a44..6eea3b301`](https://github.com/simple-robot/simpler-robot/compare/309b58a44..df0e89f8f): version to v2.1.0-DEV.7 mirai to 2.6.7
- [`5a733092e`](https://github.com/simple-robot/simpler-robot/commit/5a733092e): Update test.yml
- [`47525d49d..28b8c84d6`](https://github.com/simple-robot/simpler-robot/compare/47525d49d..5a733092e): api some
- [`8854b3bf4`](https://github.com/simple-robot/simpler-robot/commit/8854b3bf4): gateway api test
- [`039ee2dc5`](https://github.com/simple-robot/simpler-robot/commit/039ee2dc5): khl gateway api test
- [`85891b60c`](https://github.com/simple-robot/simpler-robot/commit/85891b60c): for v2.1.0-DEV.6
- [`59236a9fa`](https://github.com/simple-robot/simpler-robot/commit/59236a9fa): Update snapshot.yml
- [`a8ab50319`](https://github.com/simple-robot/simpler-robot/commit/a8ab50319): Update test.yml
- [`627e0778b`](https://github.com/simple-robot/simpler-robot/commit/627e0778b): Update dev-tag-deploy.yml
- [`8584786ae`](https://github.com/simple-robot/simpler-robot/commit/8584786ae): for v2.1.0-DEV.5 尝试修改部分项目目录结构
- [`ec7b66344`](https://github.com/simple-robot/simpler-robot/commit/ec7b66344): change dir tree
- [`80a1e3453`](https://github.com/simple-robot/simpler-robot/commit/80a1e3453): new module for `api-qq`
- [`52fe575c6`](https://github.com/simple-robot/simpler-robot/commit/52fe575c6): try change tree mode
- [`406fd54f2`](https://github.com/simple-robot/simpler-robot/commit/406fd54f2): msg parser
- [`989636733`](https://github.com/simple-robot/simpler-robot/commit/989636733): mirai special event interface.
- [`14159c7f2`](https://github.com/simple-robot/simpler-robot/commit/14159c7f2): more debug info and test
- [`cee623434`](https://github.com/simple-robot/simpler-robot/commit/cee623434): update pom
- [`55719a8ad`](https://github.com/simple-robot/simpler-robot/commit/55719a8ad): rc.3
- [`8d7c1225b`](https://github.com/simple-robot/simpler-robot/commit/8d7c1225b): listener group update
- [`8feb23fed`](https://github.com/simple-robot/simpler-robot/commit/8feb23fed): for v2.1.0-DEV.4
- [`92aec21b2`](https://github.com/simple-robot/simpler-robot/commit/92aec21b2): more info and more containers for account and bot.
- [`1ced7ffae..14c5de415`](https://github.com/simple-robot/simpler-robot/compare/1ced7ffae..92aec21b2): more info and more containers for account and bot. for #126
- [`02d12ca42`](https://github.com/simple-robot/simpler-robot/commit/02d12ca42): update pom
- [`2101007e9`](https://github.com/simple-robot/simpler-robot/commit/2101007e9): remove some pom
- [`a9e90cffb`](https://github.com/simple-robot/simpler-robot/commit/a9e90cffb): version to rc.2
- [`13396c45d`](https://github.com/simple-robot/simpler-robot/commit/13396c45d): fix #127
- [`2fb719b93..28dd3f234`](https://github.com/simple-robot/simpler-robot/compare/2fb719b93..13396c45d): update readme
- [`3e64b0c38`](https://github.com/simple-robot/simpler-robot/commit/3e64b0c38): for v2.1.0-M1
- [`6f458a1e9`](https://github.com/simple-robot/simpler-robot/commit/6f458a1e9): close #124
- [`1e525d81d`](https://github.com/simple-robot/simpler-robot/commit/1e525d81d): for v2.1.0-DEV.3.2
- [`e10616f1c`](https://github.com/simple-robot/simpler-robot/commit/e10616f1c): Update dev-tag-deploy.yml
- [`fba106614`](https://github.com/simple-robot/simpler-robot/commit/fba106614): for v2.1.0-DEV.3.1
- [`fd6e6b22b..efc94313b`](https://github.com/simple-robot/simpler-robot/compare/fd6e6b22b..fba106614): for v2.1.0-DEV.3
- [`58ebe1a4c`](https://github.com/simple-robot/simpler-robot/commit/58ebe1a4c): Update dev-tag-deploy.yml
- [`d4ddf5663`](https://github.com/simple-robot/simpler-robot/commit/d4ddf5663): for v2.1.0-DEV.2
- [`f1a0cc2d5`](https://github.com/simple-robot/simpler-robot/commit/f1a0cc2d5): Create dev-tag-deploy.yml
- [`62642e402`](https://github.com/simple-robot/simpler-robot/commit/62642e402): comment 实现多bot扫描与注册。准备逐步弃用 `simbot.core.bots`
- [`6ae6ae3dd`](https://github.com/simple-robot/simpler-robot/commit/6ae6ae3dd): resources test
- [`e20d66519..632c58a78`](https://github.com/simple-robot/simpler-robot/compare/e20d66519..6ae6ae3dd): Update snapshot.yml
- [`160d4603c`](https://github.com/simple-robot/simpler-robot/commit/160d4603c): Create snapshot.yml
- [`ba22a587f`](https://github.com/simple-robot/simpler-robot/commit/ba22a587f): Update test.yml
- [`51c97f3c2`](https://github.com/simple-robot/simpler-robot/commit/51c97f3c2): test..?
- [`bfcdb6395..070d3e6b6`](https://github.com/simple-robot/simpler-robot/compare/bfcdb6395..51c97f3c2): Update test.yml
- [`9decfd77a..49acc27e7`](https://github.com/simple-robot/simpler-robot/compare/9decfd77a..070d3e6b6): update workflows
- [`cc1f98e87..579ff857d`](https://github.com/simple-robot/simpler-robot/compare/cc1f98e87..49acc27e7): test for projects
- [`d9e63e5e9`](https://github.com/simple-robot/simpler-robot/commit/d9e63e5e9): Update test.yml
- [`9b22aa369`](https://github.com/simple-robot/simpler-robot/commit/9b22aa369): Create test.yml
- [`ef354066e..426b7b440`](https://github.com/simple-robot/simpler-robot/compare/ef354066e..9b22aa369): icon
- [`0479fc10e`](https://github.com/simple-robot/simpler-robot/commit/0479fc10e): resource path expression
- [`337771882`](https://github.com/simple-robot/simpler-robot/commit/337771882): resource path
- [`6d698765c..d86559099`](https://github.com/simple-robot/simpler-robot/compare/6d698765c..337771882): expression
- [`49747b6a2`](https://github.com/simple-robot/simpler-robot/commit/49747b6a2): resource expression test
- [`292f34e5b`](https://github.com/simple-robot/simpler-robot/commit/292f34e5b): resource expression 测试完成 for #68
- [`10610abee..3eda9ce9e`](https://github.com/simple-robot/simpler-robot/compare/10610abee..292f34e5b): resource
- [`bae402fa1`](https://github.com/simple-robot/simpler-robot/commit/bae402fa1): resources path expression
- [`d737cee29`](https://github.com/simple-robot/simpler-robot/commit/d737cee29): reset conf
- [`ae71719ff`](https://github.com/simple-robot/simpler-robot/commit/ae71719ff): 2.1.0-dev.1
- [`9d315769e`](https://github.com/simple-robot/simpler-robot/commit/9d315769e): #125 - simbot.component.mirai.dispatcher.corePoolSize - simbot.component.mirai.dispatcher.maximumPoolSize - simbot.component.mirai.dispatcher.keepAliveTime close #125
- [`332d3275c`](https://github.com/simple-robot/simpler-robot/commit/332d3275c): loggers
- [`07850a3bd`](https://github.com/simple-robot/simpler-robot/commit/07850a3bd): try fix #123 #124
- [`0c43fe87b`](https://github.com/simple-robot/simpler-robot/commit/0c43fe87b): for 2.1.0-dev.1
- [`a67430e37`](https://github.com/simple-robot/simpler-robot/commit/a67430e37): Test
- [`835b60e25..102c4984d`](https://github.com/simple-robot/simpler-robot/compare/835b60e25..a67430e37): Resource path expression
- [`6528f58b3`](https://github.com/simple-robot/simpler-robot/commit/6528f58b3): Resource path expression for #68
- [`ea1e4d533`](https://github.com/simple-robot/simpler-robot/commit/ea1e4d533): kt test junit5
- [`16504a805`](https://github.com/simple-robot/simpler-robot/commit/16504a805): test
- [`cae8cf910`](https://github.com/simple-robot/simpler-robot/commit/cae8cf910): forte-common to 1-b.2
- [`19787f942`](https://github.com/simple-robot/simpler-robot/commit/19787f942): for v2.1.0-beta.1
- [`599b77513`](https://github.com/simple-robot/simpler-robot/commit/599b77513): 实现 #122
- [`62d352161`](https://github.com/simple-robot/simpler-robot/commit/62d352161): file rename
- [`a01d72e30`](https://github.com/simple-robot/simpler-robot/commit/a01d72e30): verifyInfo config
- [`b9cf780e5..48b1150b5`](https://github.com/simple-robot/simpler-robot/compare/b9cf780e5..a01d72e30): readme
- [`4231b0586`](https://github.com/simple-robot/simpler-robot/commit/4231b0586): snapshot for v2.0.9
- [`abc99de7c`](https://github.com/simple-robot/simpler-robot/commit/abc99de7c): fix kt opt warn
- [`f48f2709e`](https://github.com/simple-robot/simpler-robot/commit/f48f2709e): for 2.0.9-dev.2
- [`83631c5c1`](https://github.com/simple-robot/simpler-robot/commit/83631c5c1): fix kt opt warn
- [`1cec73443`](https://github.com/simple-robot/simpler-robot/commit/1cec73443): for 2.0.9-dev.2
- [`0c8a8d66a`](https://github.com/simple-robot/simpler-robot/commit/0c8a8d66a): bot register log
- [`2761c80e9`](https://github.com/simple-robot/simpler-robot/commit/2761c80e9): delete unused object
- [`dd4612f60`](https://github.com/simple-robot/simpler-robot/commit/dd4612f60): 监听函数的分组与分组拦截器
- [`270b0d3bd`](https://github.com/simple-robot/simpler-robot/commit/270b0d3bd): 监听函数分组
- [`657ec854a`](https://github.com/simple-robot/simpler-robot/commit/657ec854a): 监听函数分组功能
- [`7c3b8409b`](https://github.com/simple-robot/simpler-robot/commit/7c3b8409b): 重新设计ListenContext并支持对后续监听函数的注入功能。
- [`5b1397683..de1b5b424`](https://github.com/simple-robot/simpler-robot/compare/5b1397683..7c3b8409b): listener context
- [`f444cedfc`](https://github.com/simple-robot/simpler-robot/commit/f444cedfc): tag version for 2.0.9-dev.1
- [`e3c58e227`](https://github.com/simple-robot/simpler-robot/commit/e3c58e227): 清理过时
- [`2a76e4d22`](https://github.com/simple-robot/simpler-robot/commit/2a76e4d22): 尝试解决#118
- [`ac9f73ceb`](https://github.com/simple-robot/simpler-robot/commit/ac9f73ceb): setter update
- [`096ab8044`](https://github.com/simple-robot/simpler-robot/commit/096ab8044): update
- [`0a929d6bd`](https://github.com/simple-robot/simpler-robot/commit/0a929d6bd): 追加注释
- [`0b8705503`](https://github.com/simple-robot/simpler-robot/commit/0b8705503): 移除掉部分多余注解
- [`e76acda66`](https://github.com/simple-robot/simpler-robot/commit/e76acda66): 为操作者、被操作者实现 AccountInfo特性。
- [`295bd69a5`](https://github.com/simple-robot/simpler-robot/commit/295bd69a5): 更新注释
- [`31b336d31..90dff6079`](https://github.com/simple-robot/simpler-robot/compare/31b336d31..295bd69a5): ListenerContext 重构 link #116
- [`c392cb2de`](https://github.com/simple-robot/simpler-robot/commit/c392cb2de): Listener事件流程？
- [`accfc4140`](https://github.com/simple-robot/simpler-robot/commit/accfc4140): update sample doc
- [`4d55ec7a2`](https://github.com/simple-robot/simpler-robot/commit/4d55ec7a2): khl api test and rename
- [`b495f0a8e`](https://github.com/simple-robot/simpler-robot/commit/b495f0a8e): springboot configuration metadata fix.
- [`19c8c3669`](https://github.com/simple-robot/simpler-robot/commit/19c8c3669): to v2.0.9 and async to block
- [`f20f13962`](https://github.com/simple-robot/simpler-robot/commit/f20f13962): unit test
- [`d630a5110`](https://github.com/simple-robot/simpler-robot/commit/d630a5110): fix warn
- [`d7511f5d5..882c11196`](https://github.com/simple-robot/simpler-robot/compare/d7511f5d5..d630a5110): try fix #106
- [`092d86ed4`](https://github.com/simple-robot/simpler-robot/commit/092d86ed4): try fix 106
- [`fb4986ef4..1e7536dd2`](https://github.com/simple-robot/simpler-robot/compare/fb4986ef4..092d86ed4): serializer for guild list api resp.
- [`c96add206..1a9f0a911`](https://github.com/simple-robot/simpler-robot/compare/c96add206..1e7536dd2): bot info
- [`26d0f3b2c`](https://github.com/simple-robot/simpler-robot/commit/26d0f3b2c): hide @JvmDefault
- [`b900c3afb`](https://github.com/simple-robot/simpler-robot/commit/b900c3afb): update kt ktx ktor kotlinx-serialization
- [`01c42709e`](https://github.com/simple-robot/simpler-robot/commit/01c42709e): api data req base
- [`250873f03`](https://github.com/simple-robot/simpler-robot/commit/250873f03): api req builder
- [`ab8e3d4e4`](https://github.com/simple-robot/simpler-robot/commit/ab8e3d4e4): kaiheila api conf
- [`6d6ca7b0a`](https://github.com/simple-robot/simpler-robot/commit/6d6ca7b0a): api data for v3
- [`a15956674`](https://github.com/simple-robot/simpler-robot/commit/a15956674): server api for v3
- [`f8e5bba87`](https://github.com/simple-robot/simpler-robot/commit/f8e5bba87): api实现模块
- [`110894ee7..a2ffb8867`](https://github.com/simple-robot/simpler-robot/compare/110894ee7..f8e5bba87): update readme
- [`a9dd259f1`](https://github.com/simple-robot/simpler-robot/commit/a9dd259f1): update readme info
- [`a88b601a7`](https://github.com/simple-robot/simpler-robot/commit/a88b601a7): 转发消息
- [`8275b7085`](https://github.com/simple-robot/simpler-robot/commit/8275b7085): forward message
- [`55d2f7f4d`](https://github.com/simple-robot/simpler-robot/commit/55d2f7f4d): try update send private msg.
- [`ad0ea3efe`](https://github.com/simple-robot/simpler-robot/commit/ad0ea3efe): try update send group msg.
- [`d3df5aa61`](https://github.com/simple-robot/simpler-robot/commit/d3df5aa61): 注释update
- [`47fae65c0`](https://github.com/simple-robot/simpler-robot/commit/47fae65c0): for BotVerifyInfo
- [`f45b44523`](https://github.com/simple-robot/simpler-robot/commit/f45b44523): BotVerifyInfo 定义、基础实现 link #68
- [`95ccb2154`](https://github.com/simple-robot/simpler-robot/commit/95ccb2154): BotVerifyInfo 定义 link #68
- [`2c8e860bb`](https://github.com/simple-robot/simpler-robot/commit/2c8e860bb): fix #102 for xml cat.
- [`be716dab3`](https://github.com/simple-robot/simpler-robot/commit/be716dab3): update readme
- [`2031b96b8`](https://github.com/simple-robot/simpler-robot/commit/2031b96b8): feat: fix #99
- [`f633fe412`](https://github.com/simple-robot/simpler-robot/commit/f633fe412): update version to 2.0.8?
- [`8042fcab1`](https://github.com/simple-robot/simpler-robot/commit/8042fcab1): implements for #100
- [`3167e1ee2`](https://github.com/simple-robot/simpler-robot/commit/3167e1ee2): fix factory
- [`27fb921b7`](https://github.com/simple-robot/simpler-robot/commit/27fb921b7): quartz comment.
- [`db5973fa9`](https://github.com/simple-robot/simpler-robot/commit/db5973fa9): 警告日志默认送信器； mirai更新到2.6.4
- [`5ea46502b`](https://github.com/simple-robot/simpler-robot/commit/5ea46502b): try fix #98
- [`9059d1245`](https://github.com/simple-robot/simpler-robot/commit/9059d1245): for msg builder
- [`0af35381a`](https://github.com/simple-robot/simpler-robot/commit/0af35381a): update to 2.0.7
- [`485845ad3`](https://github.com/simple-robot/simpler-robot/commit/485845ad3): fix #97. close #97
- [`d1fb6e9d8..0adaf88c1`](https://github.com/simple-robot/simpler-robot/compare/d1fb6e9d8..485845ad3): mirai message builder reimplement
- [`dcf12e441`](https://github.com/simple-robot/simpler-robot/commit/dcf12e441): messageContentBuilder流相关接口方法定义
- [`83092b3b7`](https://github.com/simple-robot/simpler-robot/commit/83092b3b7): #95 相关接口方法定义
- [`a9cc4f6fa`](https://github.com/simple-robot/simpler-robot/commit/a9cc4f6fa): mark annotations
- [`9217154e1`](https://github.com/simple-robot/simpler-robot/commit/9217154e1): 调整RequestGet.flag
- [`bd1b9af2b`](https://github.com/simple-robot/simpler-robot/commit/bd1b9af2b): 调整MessageGet.flag
- [`55ff4a752`](https://github.com/simple-robot/simpler-robot/commit/55ff4a752): test
- [`4db3b3bd9..d9d9e8efa`](https://github.com/simple-robot/simpler-robot/compare/4db3b3bd9..55ff4a752): set Setter's async api to blocking
- [`09b2b4bd5`](https://github.com/simple-robot/simpler-robot/commit/09b2b4bd5): clean code
- [`ca1edd06d..e1168c09d`](https://github.com/simple-robot/simpler-robot/compare/ca1edd06d..09b2b4bd5): update pom
- [`1a7d14988`](https://github.com/simple-robot/simpler-robot/commit/1a7d14988): update log
- [`111b67a9f`](https://github.com/simple-robot/simpler-robot/commit/111b67a9f): 增加资源获取处理器 for #93
- [`a57224a2e..940eb67ed`](https://github.com/simple-robot/simpler-robot/compare/a57224a2e..111b67a9f): update readme
- [`edb4b9e93`](https://github.com/simple-robot/simpler-robot/commit/edb4b9e93): readme
- [`9028839f9`](https://github.com/simple-robot/simpler-robot/commit/9028839f9): update info. move module
- [`a2ab1bbb3..c654dffcb`](https://github.com/simple-robot/simpler-robot/compare/a2ab1bbb3..9028839f9): Text event extra
- [`aa795d276`](https://github.com/simple-robot/simpler-robot/commit/aa795d276): kmarkdown
- [`7956351b4..109fe5ae7`](https://github.com/simple-robot/simpler-robot/compare/7956351b4..aa795d276): 尝试优化 AtDetection 逻辑
- [`74d76abfb..25e6954ec`](https://github.com/simple-robot/simpler-robot/compare/74d76abfb..109fe5ae7): mirai组件环境下, 消息构建器增加部分方法
- [`60d951ea5..ad6e762e0`](https://github.com/simple-robot/simpler-robot/compare/60d951ea5..25e6954ec): mirai下, 消息撤回的序列化
- [`69f2402b9`](https://github.com/simple-robot/simpler-robot/commit/69f2402b9): v to beta.4 拦截器优化
- [`468c19d65`](https://github.com/simple-robot/simpler-robot/commit/468c19d65): remove unused func
- [`bca13dab2`](https://github.com/simple-robot/simpler-robot/commit/bca13dab2): try fix #92
- [`c493f7b68`](https://github.com/simple-robot/simpler-robot/commit/c493f7b68): mirai message content builder for mirai native
- [`559adb942`](https://github.com/simple-robot/simpler-robot/commit/559adb942): guild 序列化
- [`5d5ac52de`](https://github.com/simple-robot/simpler-robot/commit/5d5ac52de): 信令测试
- [`a615542cd`](https://github.com/simple-robot/simpler-robot/commit/a615542cd): 信令定义
- [`2e12c59ce`](https://github.com/simple-robot/simpler-robot/commit/2e12c59ce): 开黑啦 信令
- [`5e54b9d83`](https://github.com/simple-robot/simpler-robot/commit/5e54b9d83): 开黑啦 objects定义 link #91
- [`eb92e4c3c`](https://github.com/simple-robot/simpler-robot/commit/eb92e4c3c): SNAPSHOT
- [`48fcd7ba8`](https://github.com/simple-robot/simpler-robot/commit/48fcd7ba8): v beta.3
- [`d908d77a5`](https://github.com/simple-robot/simpler-robot/commit/d908d77a5): 修复注释错误
- [`a5cde6dbd`](https://github.com/simple-robot/simpler-robot/commit/a5cde6dbd): 该死的泛型问题
- [`e4e63dc45`](https://github.com/simple-robot/simpler-robot/commit/e4e63dc45): mirai bot info优化 close #90
- [`a7c1fa688`](https://github.com/simple-robot/simpler-robot/commit/a7c1fa688): mirai bot info优化 link #90
- [`009a0fc49`](https://github.com/simple-robot/simpler-robot/commit/009a0fc49): update readme
- [`29c15ba64`](https://github.com/simple-robot/simpler-robot/commit/29c15ba64): 开黑啦bot组件 objects
- [`acbb13a56`](https://github.com/simple-robot/simpler-robot/commit/acbb13a56): 开黑啦bot组件 ktx json
- [`6d0ef60a8`](https://github.com/simple-robot/simpler-robot/commit/6d0ef60a8): 开黑啦bot组件分支init
- [`6ce6c0bfa`](https://github.com/simple-robot/simpler-robot/commit/6ce6c0bfa): 消息重构器 readme
- [`dd5901385`](https://github.com/simple-robot/simpler-robot/commit/dd5901385): 消息重构器 注释
- [`d70183ff9`](https://github.com/simple-robot/simpler-robot/commit/d70183ff9): 消息重构器接口调整  link #89
- [`955774228`](https://github.com/simple-robot/simpler-robot/commit/955774228): mirai组件 消息重构器 for test  link #89
- [`fd6e579a2`](https://github.com/simple-robot/simpler-robot/commit/fd6e579a2): mirai组件 消息重构器 link #89
- [`14ba93cf9`](https://github.com/simple-robot/simpler-robot/commit/14ba93cf9): 消息重构器参数
- [`6e66ba150..e1f12f035`](https://github.com/simple-robot/simpler-robot/compare/6e66ba150..14ba93cf9): 消息重构器
- [`2a0889499..7823d1a09`](https://github.com/simple-robot/simpler-robot/compare/2a0889499..e1f12f035): v to 2.0.6-beta.2
- [`bf3752294`](https://github.com/simple-robot/simpler-robot/commit/bf3752294): additional api test
- [`5582cb182`](https://github.com/simple-robot/simpler-robot/commit/5582cb182): 好友信息获取支持
- [`1593995e2`](https://github.com/simple-robot/simpler-robot/commit/1593995e2): sender setter additionalApi.
- [`5a5e2d1c0`](https://github.com/simple-robot/simpler-robot/commit/5a5e2d1c0): 增加注释
- [`f2ac37324`](https://github.com/simple-robot/simpler-robot/commit/f2ac37324): 额外API与mirai文件相关
- [`18209cccb..e159dc48c`](https://github.com/simple-robot/simpler-robot/compare/18209cccb..f2ac37324): for additional api - remote file
- [`81ee7e5c3..2249de148`](https://github.com/simple-robot/simpler-robot/compare/81ee7e5c3..e159dc48c): for additional api
- [`4a8f95462..bbfe07862`](https://github.com/simple-robot/simpler-robot/compare/4a8f95462..2249de148): additional api interface
- [`1d718d488`](https://github.com/simple-robot/simpler-robot/commit/1d718d488): file upload auto mkdir
- [`69d8ab1b7`](https://github.com/simple-robot/simpler-robot/commit/69d8ab1b7): 引用回复相关
- [`2729f1ab0`](https://github.com/simple-robot/simpler-robot/commit/2729f1ab0): 移除重复参数
- [`71c50b032`](https://github.com/simple-robot/simpler-robot/commit/71c50b032): 取消一个不必要的LazyNeko
- [`6ceaf4c42`](https://github.com/simple-robot/simpler-robot/commit/6ceaf4c42): 整理处理消息转化器
- [`be2aa2215`](https://github.com/simple-robot/simpler-robot/commit/be2aa2215): for auto scan test and close issue; close #88
- [`64f5cdc88`](https://github.com/simple-robot/simpler-robot/commit/64f5cdc88): update version and auto scan config
- [`f7ac8ca30`](https://github.com/simple-robot/simpler-robot/commit/f7ac8ca30): update readme
- [`77177ce3c`](https://github.com/simple-robot/simpler-robot/commit/77177ce3c): remove some module
- [`3847edefa`](https://github.com/simple-robot/simpler-robot/commit/3847edefa): auto scan packages
- [`71fa04d34`](https://github.com/simple-robot/simpler-robot/commit/71fa04d34): simbot app
- [`a675f377c`](https://github.com/simple-robot/simpler-robot/commit/a675f377c): 优化cat file解析
- [`b553a0a30`](https://github.com/simple-robot/simpler-robot/commit/b553a0a30): remove a test file
- [`aff9cf7b2`](https://github.com/simple-robot/simpler-robot/commit/aff9cf7b2): 动态参数提取器改动
- [`20af02bd6`](https://github.com/simple-robot/simpler-robot/commit/20af02bd6): 动态参数提取 link #85
- [`4ebe04523`](https://github.com/simple-robot/simpler-robot/commit/4ebe04523): 转发消息
- [`246dec16e..3907c5699`](https://github.com/simple-robot/simpler-robot/compare/246dec16e..4ebe04523): 骰子
- [`348597505`](https://github.com/simple-robot/simpler-robot/commit/348597505): 文件上传
- [`02fa9328a..0300c4ed2`](https://github.com/simple-robot/simpler-robot/compare/02fa9328a..348597505): code解析相关
- [`acf955ed8`](https://github.com/simple-robot/simpler-robot/commit/acf955ed8): 音乐分享相关
- [`acc477635`](https://github.com/simple-robot/simpler-robot/commit/acc477635): flow to stream
- [`7e295fb95`](https://github.com/simple-robot/simpler-robot/commit/7e295fb95): mirai 文件相关
- [`43793ae46..494e241a4`](https://github.com/simple-robot/simpler-robot/compare/43793ae46..7e295fb95): 更新注释
- [`48e0c91e3..94bfcb18c`](https://github.com/simple-robot/simpler-robot/compare/48e0c91e3..494e241a4): upload file for cat?
- [`01859fd0a`](https://github.com/simple-robot/simpler-robot/commit/01859fd0a): upload file for cat
- [`a4dacd8ee`](https://github.com/simple-robot/simpler-robot/commit/a4dacd8ee): upload file
- [`0ee9deb2e`](https://github.com/simple-robot/simpler-robot/commit/0ee9deb2e): Remote file.
- [`23e118301`](https://github.com/simple-robot/simpler-robot/commit/23e118301): 消息解析器
- [`b4daa7fd9`](https://github.com/simple-robot/simpler-robot/commit/b4daa7fd9): update mirai version to 2.5.0
- [`fa9cd7992`](https://github.com/simple-robot/simpler-robot/commit/fa9cd7992): pre update version to 2.0.5
- [`279a8d8e2`](https://github.com/simple-robot/simpler-robot/commit/279a8d8e2): update mirai to 2.5.0
- [`fda3267fe`](https://github.com/simple-robot/simpler-robot/commit/fda3267fe): Fix mirai config.
- [`d9b0014c8`](https://github.com/simple-robot/simpler-robot/commit/d9b0014c8): fix #82;
- [`15363a0e1`](https://github.com/simple-robot/simpler-robot/commit/15363a0e1): Update github issue template.
- [`a1536bf78`](https://github.com/simple-robot/simpler-robot/commit/a1536bf78): 允许Filter与Filters进行注解继承; close #83
- [`718042b07`](https://github.com/simple-robot/simpler-robot/commit/718042b07): Mirai message source;
- [`b64b341c5`](https://github.com/simple-robot/simpler-robot/commit/b64b341c5): 恢复不应过时的api
- [`69562bb20`](https://github.com/simple-robot/simpler-robot/commit/69562bb20): 调整注释
- [`9b117cc94`](https://github.com/simple-robot/simpler-robot/commit/9b117cc94): verify ex
- [`fa273b47f`](https://github.com/simple-robot/simpler-robot/commit/fa273b47f): close #78
- [`48a19c52d`](https://github.com/simple-robot/simpler-robot/commit/48a19c52d): update to 2.0.3
- [`0b51e48b6`](https://github.com/simple-robot/simpler-robot/commit/0b51e48b6): fix #77 close #77
- [`bb46b2df2`](https://github.com/simple-robot/simpler-robot/commit/bb46b2df2): readme
- [`872676993`](https://github.com/simple-robot/simpler-robot/commit/872676993): 修复@FilterValue没有前置处理 fix #76
- [`a7d150da6`](https://github.com/simple-robot/simpler-robot/commit/a7d150da6): 消息解析 - 音乐 账号验证 - 日志
- [`a774e270b`](https://github.com/simple-robot/simpler-robot/commit/a774e270b): update ktx and fastjson version.
- [`9c9f21d35`](https://github.com/simple-robot/simpler-robot/commit/9c9f21d35): 调整注释
- [`a1f7f5e55..60ae24a76`](https://github.com/simple-robot/simpler-robot/compare/a1f7f5e55..9c9f21d35): setter相关修改;
- [`483ea6b90`](https://github.com/simple-robot/simpler-robot/commit/483ea6b90): getter相关修改; BanInfo -> MuteInfo BanList -> MuteList
- [`cce360a33`](https://github.com/simple-robot/simpler-robot/commit/cce360a33): 处理过时函数
- [`1040daabd`](https://github.com/simple-robot/simpler-robot/commit/1040daabd): 优先级注解 close #72
- [`689cacd12`](https://github.com/simple-robot/simpler-robot/commit/689cacd12): update version to 2.0.2 close #74
- [`f3f30610c`](https://github.com/simple-robot/simpler-robot/commit/f3f30610c): for #74
- [`a6bbf4593`](https://github.com/simple-robot/simpler-robot/commit/a6bbf4593): 接口增加属性
- [`73e542804`](https://github.com/simple-robot/simpler-robot/commit/73e542804): test
- [`36d8feb62..61846321b`](https://github.com/simple-robot/simpler-robot/compare/36d8feb62..73e542804): update version 2.0.1
- [`f56e11c73`](https://github.com/simple-robot/simpler-robot/commit/f56e11c73): test
- [`cc6eafbe2`](https://github.com/simple-robot/simpler-robot/commit/cc6eafbe2): fix message builder
- [`629db2df4`](https://github.com/simple-robot/simpler-robot/commit/629db2df4): 配置项
- [`f015a7e97`](https://github.com/simple-robot/simpler-robot/commit/f015a7e97): 移除无用配置项
- [`3c496566e`](https://github.com/simple-robot/simpler-robot/commit/3c496566e): 2.0.1-SNAPSHOT
- [`4ce05ac05`](https://github.com/simple-robot/simpler-robot/commit/4ce05ac05): 尝试优化keyword动态参数提取
- [`2d6983843`](https://github.com/simple-robot/simpler-robot/commit/2d6983843): update kt version
- [`886f70e8a..3b1bee4ec`](https://github.com/simple-robot/simpler-robot/compare/886f70e8a..2d6983843): pom update version
- [`2edf79698`](https://github.com/simple-robot/simpler-robot/commit/2edf79698): update log
- [`9e2f4a17d`](https://github.com/simple-robot/simpler-robot/commit/9e2f4a17d): update parent pom and deploy
- [`a3a931f78`](https://github.com/simple-robot/simpler-robot/commit/a3a931f78): clean import
- [`76f3b176a`](https://github.com/simple-robot/simpler-robot/commit/76f3b176a): update version to 2.0.0
- [`b1a6c5f60`](https://github.com/simple-robot/simpler-robot/commit/b1a6c5f60): update mirai to 2.3.2
- [`ed0e06449`](https://github.com/simple-robot/simpler-robot/commit/ed0e06449): update log
- [`23ee8bc5d`](https://github.com/simple-robot/simpler-robot/commit/23ee8bc5d): 优化过滤器目标处理器
- [`e21d7ac22`](https://github.com/simple-robot/simpler-robot/commit/e21d7ac22): update catcode version
- [`937995083`](https://github.com/simple-robot/simpler-robot/commit/937995083): test
- [`66f398d64`](https://github.com/simple-robot/simpler-robot/commit/66f398d64): 自定义过滤目标匹配器注释
- [`dcbdcee17`](https://github.com/simple-robot/simpler-robot/commit/dcbdcee17): 自定义过滤目标匹配器。
- [`00e177a0b`](https://github.com/simple-robot/simpler-robot/commit/00e177a0b): fix lovelycat starter
- [`54d21fa08..958b211c0`](https://github.com/simple-robot/simpler-robot/compare/54d21fa08..00e177a0b): 2.0.0.1-SNAP to 2.0.0-SNAP
- [`60051aa93`](https://github.com/simple-robot/simpler-robot/commit/60051aa93): maybe 2.0.0 ?
- [`31a66da58..66788ccde`](https://github.com/simple-robot/simpler-robot/compare/31a66da58..60051aa93): readme
- [`d4d264eab`](https://github.com/simple-robot/simpler-robot/commit/d4d264eab): update README.md
- [`363aa0319`](https://github.com/simple-robot/simpler-robot/commit/363aa0319): mirai更新
- [`423512ce7`](https://github.com/simple-robot/simpler-robot/commit/423512ce7): deploy info
- [`24777e599`](https://github.com/simple-robot/simpler-robot/commit/24777e599): 调整项目整体结构，去除`parent`中可能会出现的多余属性
- [`216ff3d63`](https://github.com/simple-robot/simpler-robot/commit/216ff3d63): clean import
- [`c9f9bb00c`](https://github.com/simple-robot/simpler-robot/commit/c9f9bb00c): 修改优化match逻辑
- [`83f4494a4`](https://github.com/simple-robot/simpler-robot/commit/83f4494a4): 删除尚未进行开发的模块
- [`5b3de5adf`](https://github.com/simple-robot/simpler-robot/commit/5b3de5adf): server
- [`8423f9236`](https://github.com/simple-robot/simpler-robot/commit/8423f9236): 移除多余shutdown hook
- [`0bbe325b2`](https://github.com/simple-robot/simpler-robot/commit/0bbe325b2): shutdown hook
- [`f84d73159..0f9475496`](https://github.com/simple-robot/simpler-robot/compare/f84d73159..0bbe325b2): server index
- [`6bbdd3828`](https://github.com/simple-robot/simpler-robot/commit/6bbdd3828): shutdown file
- [`b9920de1c`](https://github.com/simple-robot/simpler-robot/commit/b9920de1c): 优化可爱猫默认页; 为监听服务增加shutdown hook
- [`cf36d1370`](https://github.com/simple-robot/simpler-robot/commit/cf36d1370): 优化日志
- [`5cb793c22`](https://github.com/simple-robot/simpler-robot/commit/5cb793c22): 清理注释
- [`d1516db9b`](https://github.com/simple-robot/simpler-robot/commit/d1516db9b): ktor respond fix
- [`a76c76251`](https://github.com/simple-robot/simpler-robot/commit/a76c76251): pom executions
- [`5b55b29af`](https://github.com/simple-robot/simpler-robot/commit/5b55b29af): 邀请入群事件
- [`75481a4c5`](https://github.com/simple-robot/simpler-robot/commit/75481a4c5): 群成员最后发言时间
- [`3a7fb0e8d`](https://github.com/simple-robot/simpler-robot/commit/3a7fb0e8d): 群成员入群时间
- [`0708de0e9`](https://github.com/simple-robot/simpler-robot/commit/0708de0e9): update mirai to 2.1.1 支持音乐解析
- [`cd14d37a3`](https://github.com/simple-robot/simpler-robot/commit/cd14d37a3): 可爱猫 自动登录配置; 快速回复at
- [`b6723b5ba`](https://github.com/simple-robot/simpler-robot/commit/b6723b5ba): 可爱猫 群号支持获取数字
- [`d17ce2a00`](https://github.com/simple-robot/simpler-robot/commit/d17ce2a00): fix result processor
- [`efc26df44`](https://github.com/simple-robot/simpler-robot/commit/efc26df44): version.
- [`ad35f6c7e`](https://github.com/simple-robot/simpler-robot/commit/ad35f6c7e): remove empty body
- [`9982b98a4`](https://github.com/simple-robot/simpler-robot/commit/9982b98a4): toString
- [`3a7e0f3c7`](https://github.com/simple-robot/simpler-robot/commit/3a7e0f3c7): 获取拥有管理权限的人的列表
- [`a678915d1`](https://github.com/simple-robot/simpler-robot/commit/a678915d1): 群人数应该+bot
- [`45d90fcfd`](https://github.com/simple-robot/simpler-robot/commit/45d90fcfd): 部分字母开头大写
- [`752a843d7`](https://github.com/simple-robot/simpler-robot/commit/752a843d7): pom version
- [`70de580bb`](https://github.com/simple-robot/simpler-robot/commit/70de580bb): 修复可爱猫事件监听 close #67
- [`81be82dc4`](https://github.com/simple-robot/simpler-robot/commit/81be82dc4): 网络路径配置文件 close #66
- [`953bd2054`](https://github.com/simple-robot/simpler-robot/commit/953bd2054): 配置资源加载
- [`c2cffbf8f`](https://github.com/simple-robot/simpler-robot/commit/c2cffbf8f): 动态代理的问题
- [`bb002139a`](https://github.com/simple-robot/simpler-robot/commit/bb002139a): 修复springboot下动态代理的问题。 close #65
- [`e5be9f07a`](https://github.com/simple-robot/simpler-robot/commit/e5be9f07a): tips
- [`97b1698f8`](https://github.com/simple-robot/simpler-robot/commit/97b1698f8): resource load
- [`6e01e9e84`](https://github.com/simple-robot/simpler-robot/commit/6e01e9e84): 好多图标！
- [`d60c8cbb0`](https://github.com/simple-robot/simpler-robot/commit/d60c8cbb0): readme
- [`ed19775d1`](https://github.com/simple-robot/simpler-robot/commit/ed19775d1): 2.0.0-快照
- [`e914fe9a8..760ea900b`](https://github.com/simple-robot/simpler-robot/compare/e914fe9a8..ed19775d1): mark annotation
- [`4e7846451`](https://github.com/simple-robot/simpler-robot/commit/4e7846451): fix #63, #64
- [`a890b6a83`](https://github.com/simple-robot/simpler-robot/commit/a890b6a83): 快照版本不太行啊，先RC.4吧
- [`897287987`](https://github.com/simple-robot/simpler-robot/commit/897287987): for #62 and deploy 2.0.0-SNAPSHOT
- [`56d700428`](https://github.com/simple-robot/simpler-robot/commit/56d700428): readme
- [`8100e2114`](https://github.com/simple-robot/simpler-robot/commit/8100e2114): shutdown hook
- [`f22087f7a`](https://github.com/simple-robot/simpler-robot/commit/f22087f7a): snapshot
- [`3544c9ec8`](https://github.com/simple-robot/simpler-robot/commit/3544c9ec8): test
- [`cfc6af014`](https://github.com/simple-robot/simpler-robot/commit/cfc6af014): update log
- [`d478d70b9`](https://github.com/simple-robot/simpler-robot/commit/d478d70b9): fix #55; update log; close #55
- [`27dafab7d`](https://github.com/simple-robot/simpler-robot/commit/27dafab7d): 暂停钉钉模块的部署更新
- [`a9f058016`](https://github.com/simple-robot/simpler-robot/commit/a9f058016): bot level
- [`d46b94344`](https://github.com/simple-robot/simpler-robot/commit/d46b94344): bot level util
- [`d4ee97ad0`](https://github.com/simple-robot/simpler-robot/commit/d4ee97ad0): 监听响应处理器 #49 快速回复 #54
- [`5c0aee4b3`](https://github.com/simple-robot/simpler-robot/commit/5c0aee4b3): up log
- [`a59988885..b44f6e2cd`](https://github.com/simple-robot/simpler-robot/compare/a59988885..5c0aee4b3): for quick reply
- [`3cdf179d6`](https://github.com/simple-robot/simpler-robot/commit/3cdf179d6): reply
- [`52c145788`](https://github.com/simple-robot/simpler-robot/commit/52c145788): processor and quick reply
- [`76244bf6f`](https://github.com/simple-robot/simpler-robot/commit/76244bf6f): processor
- [`0ad73cc36`](https://github.com/simple-robot/simpler-robot/commit/0ad73cc36): #51: 监听参数警告信息以及类型处理优化 #52: 兼容kotlin的可空类型 #53: 兼容kotlin扩展函数写法
- [`a28e4702c`](https://github.com/simple-robot/simpler-robot/commit/a28e4702c): 清理过时代码
- [`f143ec076`](https://github.com/simple-robot/simpler-robot/commit/f143ec076): quick reply
- [`c37e07f01`](https://github.com/simple-robot/simpler-robot/commit/c37e07f01): update to next ver 2.0.0-rc.2
- [`920e95339`](https://github.com/simple-robot/simpler-robot/commit/920e95339): listener manager
- [`30bb69099`](https://github.com/simple-robot/simpler-robot/commit/30bb69099): update log and for deploy
- [`f3de075ee`](https://github.com/simple-robot/simpler-robot/commit/f3de075ee): fix: 修复可爱猫相关问题
- [`5b64e017c..634b99b65`](https://github.com/simple-robot/simpler-robot/compare/5b64e017c..f3de075ee): 决策
- [`ff03db773..92228b7ef`](https://github.com/simple-robot/simpler-robot/compare/ff03db773..634b99b65): test
- [`ea0880e50`](https://github.com/simple-robot/simpler-robot/commit/ea0880e50): 图片
- [`96c1ee630`](https://github.com/simple-robot/simpler-robot/commit/96c1ee630): test
- [`f1eec568e`](https://github.com/simple-robot/simpler-robot/commit/f1eec568e): 能力接口
- [`39bdf2a3c`](https://github.com/simple-robot/simpler-robot/commit/39bdf2a3c): package info
- [`1a1f9d92c`](https://github.com/simple-robot/simpler-robot/commit/1a1f9d92c): mirai catcode
- [`0df161dd7`](https://github.com/simple-robot/simpler-robot/commit/0df161dd7): tips
- [`3ed0bd52a`](https://github.com/simple-robot/simpler-robot/commit/3ed0bd52a): pom
- [`8112eecd6`](https://github.com/simple-robot/simpler-robot/commit/8112eecd6): kill warn
- [`cf09091a6`](https://github.com/simple-robot/simpler-robot/commit/cf09091a6): log and deploy
- [`350b02446`](https://github.com/simple-robot/simpler-robot/commit/350b02446): to 2.0.0-rc.1
- [`00e78ace6`](https://github.com/simple-robot/simpler-robot/commit/00e78ace6): mirai update, cookies, ktor
- [`2c8fd10a3..5b0947415`](https://github.com/simple-robot/simpler-robot/compare/2c8fd10a3..00e78ace6): log
- [`3efb36152`](https://github.com/simple-robot/simpler-robot/commit/3efb36152): pom and tips
- [`a97fd5003`](https://github.com/simple-robot/simpler-robot/commit/a97fd5003): operate todo
- [`38a1fe61a`](https://github.com/simple-robot/simpler-robot/commit/38a1fe61a): fix @Ignore fail
- [`a5d7e0c30`](https://github.com/simple-robot/simpler-robot/commit/a5d7e0c30): test
- [`eb43561ba..58b8c805d`](https://github.com/simple-robot/simpler-robot/compare/eb43561ba..a5d7e0c30): readme。
- [`cdc27c9a5`](https://github.com/simple-robot/simpler-robot/commit/cdc27c9a5): readme and logo.
- [`3e53a3960`](https://github.com/simple-robot/simpler-robot/commit/3e53a3960): log
- [`c97f4fbf2`](https://github.com/simple-robot/simpler-robot/commit/c97f4fbf2): message content
- [`cef05c0db`](https://github.com/simple-robot/simpler-robot/commit/cef05c0db): messageContent equals
- [`f36ba2848..22eb47ad6`](https://github.com/simple-robot/simpler-robot/compare/f36ba2848..cef05c0db): fix mirai cookies.
- [`a2c7a2654`](https://github.com/simple-robot/simpler-robot/commit/a2c7a2654): pom version
- [`884265e9f`](https://github.com/simple-robot/simpler-robot/commit/884265e9f): warn sender
- [`3b0c78ce6..ad2d19862`](https://github.com/simple-robot/simpler-robot/compare/3b0c78ce6..884265e9f): MultipleResults.getSize() -> MultipleResults.size()
- [`5cfc36841`](https://github.com/simple-robot/simpler-robot/commit/5cfc36841): bot as account
- [`e4b2b5c71`](https://github.com/simple-robot/simpler-robot/commit/e4b2b5c71): tips
- [`9919f1f4a`](https://github.com/simple-robot/simpler-robot/commit/9919f1f4a): pom.url
- [`04210dfd2`](https://github.com/simple-robot/simpler-robot/commit/04210dfd2): logger sender
- [`af978ed0e`](https://github.com/simple-robot/simpler-robot/commit/af978ed0e): update version to b.9
- [`73745d016`](https://github.com/simple-robot/simpler-robot/commit/73745d016): fix: 可爱猫组件update
- [`7a93cc519`](https://github.com/simple-robot/simpler-robot/commit/7a93cc519): feat: #32 备用函数
- [`2ce370960`](https://github.com/simple-robot/simpler-robot/commit/2ce370960): feat: #18 不支持的API提供默认送信器配置
- [`abe300065`](https://github.com/simple-robot/simpler-robot/commit/abe300065): delete: 无用代码
- [`77baf3890`](https://github.com/simple-robot/simpler-robot/commit/77baf3890): feat: #40 细化各个类型
- [`050087833`](https://github.com/simple-robot/simpler-robot/commit/050087833): feat: 优化AccountInfo与AccountContainer
- [`e94845d6f`](https://github.com/simple-robot/simpler-robot/commit/e94845d6f): feat: #40 群成员accountInfo
- [`5a1db3e1b`](https://github.com/simple-robot/simpler-robot/commit/5a1db3e1b): for #18
- [`836c09560`](https://github.com/simple-robot/simpler-robot/commit/836c09560): for #40
- [`70a8635ab`](https://github.com/simple-robot/simpler-robot/commit/70a8635ab): lovely cat starter
- [`0b88c0986`](https://github.com/simple-robot/simpler-robot/commit/0b88c0986): feat: 优化事件触发机制 #36 finish
- [`e951170c8`](https://github.com/simple-robot/simpler-robot/commit/e951170c8): update version to b.8
- [`69ed99ca9`](https://github.com/simple-robot/simpler-robot/commit/69ed99ca9): fix: fix #39
- [`8e8514df4`](https://github.com/simple-robot/simpler-robot/commit/8e8514df4): Mirai on msg
- [`aea73f1b4`](https://github.com/simple-robot/simpler-robot/commit/aea73f1b4): for #36
- [`8d3f23d6b`](https://github.com/simple-robot/simpler-robot/commit/8d3f23d6b): pom
- [`c004abef6`](https://github.com/simple-robot/simpler-robot/commit/c004abef6): version
- [`cd74754ea`](https://github.com/simple-robot/simpler-robot/commit/cd74754ea): clean import
- [`fe8820db1`](https://github.com/simple-robot/simpler-robot/commit/fe8820db1): version fix
- [`51b0ccec7`](https://github.com/simple-robot/simpler-robot/commit/51b0ccec7): fix ding
- [`ee5065df5`](https://github.com/simple-robot/simpler-robot/commit/ee5065df5): update log
- [`96e6df531`](https://github.com/simple-robot/simpler-robot/commit/96e6df531): 可爱猫springboot-starter; 移除部分多余输出; 可爱猫服务器响应修复; 追加spare并作准备;
- [`d630401ec`](https://github.com/simple-robot/simpler-robot/commit/d630401ec): ListenerContext 追加两个方法
- [`e1119f8d2`](https://github.com/simple-robot/simpler-robot/commit/e1119f8d2): 遗漏的 @JvmDefault注解
- [`b15836153`](https://github.com/simple-robot/simpler-robot/commit/b15836153): Achieve and close #29
- [`42f9cd7a3`](https://github.com/simple-robot/simpler-robot/commit/42f9cd7a3): Achieve and close #33
- [`2261dcb30`](https://github.com/simple-robot/simpler-robot/commit/2261dcb30): fix and close #34
- [`48c0f3c16`](https://github.com/simple-robot/simpler-robot/commit/48c0f3c16): fix and close #35
- [`0dd5423a1`](https://github.com/simple-robot/simpler-robot/commit/0dd5423a1): filter
- [`bb61751e4..7d215e1d4`](https://github.com/simple-robot/simpler-robot/compare/bb61751e4..0dd5423a1): tips
- [`f925fa847`](https://github.com/simple-robot/simpler-robot/commit/f925fa847): clean import
- [`629fd42e5`](https://github.com/simple-robot/simpler-robot/commit/629fd42e5): fix lovelycat api post
- [`e18fdcc85`](https://github.com/simple-robot/simpler-robot/commit/e18fdcc85): close #30
- [`191f76c85`](https://github.com/simple-robot/simpler-robot/commit/191f76c85): rest template client; pom;
- [`ac32c1d59`](https://github.com/simple-robot/simpler-robot/commit/ac32c1d59): dispatch
- [`f62810e16`](https://github.com/simple-robot/simpler-robot/commit/f62810e16): Coroutine test
- [`0f6416aba..cf50310f6`](https://github.com/simple-robot/simpler-robot/compare/0f6416aba..f62810e16): test
- [`91ed7e6a8`](https://github.com/simple-robot/simpler-robot/commit/91ed7e6a8): rest http
- [`bafe950cf`](https://github.com/simple-robot/simpler-robot/commit/bafe950cf): http client restTemplate
- [`b3a0e1eda`](https://github.com/simple-robot/simpler-robot/commit/b3a0e1eda): http-restTemplate
- [`b882be49c`](https://github.com/simple-robot/simpler-robot/commit/b882be49c): 可爱猫springboot-starter
- [`c9a973b96`](https://github.com/simple-robot/simpler-robot/commit/c9a973b96): mirai 纯text cat
- [`eac9b4976..d6314ad6e`](https://github.com/simple-robot/simpler-robot/compare/eac9b4976..c9a973b96): client template
- [`7f2cd1e44`](https://github.com/simple-robot/simpler-robot/commit/7f2cd1e44): tips; readme
- [`21be74b1d`](https://github.com/simple-robot/simpler-robot/commit/21be74b1d): 重命名 component-parent 模块为 component
- [`985d7c733`](https://github.com/simple-robot/simpler-robot/commit/985d7c733): logger
- [`a03be57f4`](https://github.com/simple-robot/simpler-robot/commit/a03be57f4): tips & logo conf
- [`5df40ec14`](https://github.com/simple-robot/simpler-robot/commit/5df40ec14): tips
- [`5880ff225`](https://github.com/simple-robot/simpler-robot/commit/5880ff225): thread { ... }
- [`b58f0186e`](https://github.com/simple-robot/simpler-robot/commit/b58f0186e): update version
- [`ee8df10f2`](https://github.com/simple-robot/simpler-robot/commit/ee8df10f2): readme
- [`de9d4e079`](https://github.com/simple-robot/simpler-robot/commit/de9d4e079): group sync
- [`43d0dab7e`](https://github.com/simple-robot/simpler-robot/commit/43d0dab7e): lovelycat setter
- [`557b34e31`](https://github.com/simple-robot/simpler-robot/commit/557b34e31): mirai update
- [`d4eb8d7d4`](https://github.com/simple-robot/simpler-robot/commit/d4eb8d7d4): recall
- [`c6a07ee93`](https://github.com/simple-robot/simpler-robot/commit/c6a07ee93): event
- [`9fb14c25b`](https://github.com/simple-robot/simpler-robot/commit/9fb14c25b): mirai 荣耀
- [`78f3d2fff`](https://github.com/simple-robot/simpler-robot/commit/78f3d2fff): delete friend
- [`d5c9a4560`](https://github.com/simple-robot/simpler-robot/commit/d5c9a4560): mirai update
- [`94ce8a917..eb9c54275`](https://github.com/simple-robot/simpler-robot/compare/94ce8a917..d5c9a4560): update readme
- [`25dda82e7`](https://github.com/simple-robot/simpler-robot/commit/25dda82e7): update log
- [`17d901bb6..534b478fa`](https://github.com/simple-robot/simpler-robot/compare/17d901bb6..25dda82e7): time task Backward compatible to b.5
- [`6b062d7e3..87cfb8b8f`](https://github.com/simple-robot/simpler-robot/compare/6b062d7e3..534b478fa): time task log
- [`e6f13ce7f`](https://github.com/simple-robot/simpler-robot/commit/e6f13ce7f): time task for quartz; close #24
- [`be6f5249a`](https://github.com/simple-robot/simpler-robot/commit/be6f5249a): 注释等
- [`e515c12a8`](https://github.com/simple-robot/simpler-robot/commit/e515c12a8): time method task
- [`9824701ef..a43c909d8`](https://github.com/simple-robot/simpler-robot/compare/9824701ef..e515c12a8): time task
- [`ece091a8c`](https://github.com/simple-robot/simpler-robot/commit/ece091a8c): fix close #28;
- [`52fd7b6c9`](https://github.com/simple-robot/simpler-robot/commit/52fd7b6c9): timetask
- [`02c62ce13`](https://github.com/simple-robot/simpler-robot/commit/02c62ce13): time task
- [`081158df3`](https://github.com/simple-robot/simpler-robot/commit/081158df3): time task core
- [`e239e056b`](https://github.com/simple-robot/simpler-robot/commit/e239e056b): update to b.6 version
- [`5e1b19a9d`](https://github.com/simple-robot/simpler-robot/commit/5e1b19a9d): time-task and readme
- [`ba210cf48`](https://github.com/simple-robot/simpler-robot/commit/ba210cf48): pom
- [`fbf522b0c`](https://github.com/simple-robot/simpler-robot/commit/fbf522b0c): readme
- [`53d53bfa4`](https://github.com/simple-robot/simpler-robot/commit/53d53bfa4): dokka plugin update
- [`3d174827d`](https://github.com/simple-robot/simpler-robot/commit/3d174827d): test
- [`35eb1c90c`](https://github.com/simple-robot/simpler-robot/commit/35eb1c90c): kill warns
- [`4e947ad5e`](https://github.com/simple-robot/simpler-robot/commit/4e947ad5e): no Unsafe lazy
- [`571f031a0`](https://github.com/simple-robot/simpler-robot/commit/571f031a0): time-task module
- [`9ff2a2e80`](https://github.com/simple-robot/simpler-robot/commit/9ff2a2e80): mirai url img/voice 优化
- [`585155649`](https://github.com/simple-robot/simpler-robot/commit/585155649): ktor update
- [`c96bee814`](https://github.com/simple-robot/simpler-robot/commit/c96bee814): update to b.5
- [`510a45ee5`](https://github.com/simple-robot/simpler-robot/commit/510a45ee5): mirai-存活线程 守护线程
- [`a9ba4f466..941fdca7d`](https://github.com/simple-robot/simpler-robot/compare/a9ba4f466..510a45ee5): :loud_sound: 添加日志记录
- [`63138efea`](https://github.com/simple-robot/simpler-robot/commit/63138efea): 捕获部分可能的异常
- [`64348377b`](https://github.com/simple-robot/simpler-robot/commit/64348377b): remove *.iml
- [`0037e611e`](https://github.com/simple-robot/simpler-robot/commit/0037e611e): log
- [`ee46483cc`](https://github.com/simple-robot/simpler-robot/commit/ee46483cc): #27
- [`62cde4245`](https://github.com/simple-robot/simpler-robot/commit/62cde4245): sb starter conf
- [`38e8f69e7`](https://github.com/simple-robot/simpler-robot/commit/38e8f69e7): 独立serialization模块
- [`49a096929`](https://github.com/simple-robot/simpler-robot/commit/49a096929): serialization readme
- [`3f8938df7`](https://github.com/simple-robot/simpler-robot/commit/3f8938df7): 独立serialization模块
- [`9748e0340`](https://github.com/simple-robot/simpler-robot/commit/9748e0340): sb conf
- [`4fc592bd2`](https://github.com/simple-robot/simpler-robot/commit/4fc592bd2): update to v b.4
- [`1c8b5f157`](https://github.com/simple-robot/simpler-robot/commit/1c8b5f157): update log
- [`8847bd314..25dcd7875`](https://github.com/simple-robot/simpler-robot/compare/8847bd314..1c8b5f157): update version to b.3
- [`2c5f1f5ea`](https://github.com/simple-robot/simpler-robot/commit/2c5f1f5ea): fix close #26;
- [`71227d7dc`](https://github.com/simple-robot/simpler-robot/commit/71227d7dc): fix close #25;
- [`8aa7e5cf6`](https://github.com/simple-robot/simpler-robot/commit/8aa7e5cf6): readme
- [`fae311504..bd4faf0ee`](https://github.com/simple-robot/simpler-robot/compare/fae311504..8aa7e5cf6): 适配mirai-2.0-M1
- [`69418b211`](https://github.com/simple-robot/simpler-robot/commit/69418b211): new version
- [`c357bce21`](https://github.com/simple-robot/simpler-robot/commit/c357bce21): :bulb: 添加源码注释
- [`eb51dbdcb`](https://github.com/simple-robot/simpler-robot/commit/eb51dbdcb): log name fix
- [`afcdc86b1`](https://github.com/simple-robot/simpler-robot/commit/afcdc86b1): readme
- [`513102e39`](https://github.com/simple-robot/simpler-robot/commit/513102e39): readme and deploy
- [`863b70f6f`](https://github.com/simple-robot/simpler-robot/commit/863b70f6f): to BETA.1
- [`17dc57465`](https://github.com/simple-robot/simpler-robot/commit/17dc57465): sb metadata
- [`754a38129`](https://github.com/simple-robot/simpler-robot/commit/754a38129): jackson serializer
- [`d3bdeeaf9`](https://github.com/simple-robot/simpler-robot/commit/d3bdeeaf9): 临时移除onebot组件
- [`97fb69ac4`](https://github.com/simple-robot/simpler-robot/commit/97fb69ac4): fix #21
- [`fceeda361`](https://github.com/simple-robot/simpler-robot/commit/fceeda361): finish #20
- [`30be2cde5`](https://github.com/simple-robot/simpler-robot/commit/30be2cde5): component onebot
- [`dc4e406e7`](https://github.com/simple-robot/simpler-robot/commit/dc4e406e7): delete module
- [`cfb7eb8ef..24a37ab91`](https://github.com/simple-robot/simpler-robot/compare/cfb7eb8ef..dc4e406e7): update and deploy
- [`3ccd50960..5bd9cd479`](https://github.com/simple-robot/simpler-robot/compare/3ccd50960..24a37ab91): result 无效化
- [`4af953fec`](https://github.com/simple-robot/simpler-robot/commit/4af953fec): lovely cat setter
- [`31f2dd9f8`](https://github.com/simple-robot/simpler-robot/commit/31f2dd9f8): test
- [`646d00099`](https://github.com/simple-robot/simpler-robot/commit/646d00099): lovely cat setter
- [`306302cde`](https://github.com/simple-robot/simpler-robot/commit/306302cde): update log
- [`b92be1362`](https://github.com/simple-robot/simpler-robot/commit/b92be1362): achieve #17
- [`cc35525e1..69b02b27e`](https://github.com/simple-robot/simpler-robot/compare/cc35525e1..b92be1362): ex handle
- [`f7a4dc923`](https://github.com/simple-robot/simpler-robot/commit/f7a4dc923): listen result rename 'throwable' to 'cause'
- [`aa4c5dc51`](https://github.com/simple-robot/simpler-robot/commit/aa4c5dc51): listen result impl
- [`565ca999b`](https://github.com/simple-robot/simpler-robot/commit/565ca999b): handle failed
- [`7d1e5c0c4`](https://github.com/simple-robot/simpler-robot/commit/7d1e5c0c4): ex log
- [`b279dd225`](https://github.com/simple-robot/simpler-robot/commit/b279dd225): LogAble
- [`3f045c48d`](https://github.com/simple-robot/simpler-robot/commit/3f045c48d): ex handle
- [`0619d755c`](https://github.com/simple-robot/simpler-robot/commit/0619d755c): update readme
- [`1ae0858e8`](https://github.com/simple-robot/simpler-robot/commit/1ae0858e8): test and deploy a.11
- [`b0b717e94`](https://github.com/simple-robot/simpler-robot/commit/b0b717e94): log
- [`14e6abfb6..f717e6466`](https://github.com/simple-robot/simpler-robot/compare/14e6abfb6..b0b717e94): lovely cat getter.
- [`a330a771d`](https://github.com/simple-robot/simpler-robot/commit/a330a771d): fix #14 #15
- [`554fee40c`](https://github.com/simple-robot/simpler-robot/commit/554fee40c): lovelycat
- [`4675797ab`](https://github.com/simple-robot/simpler-robot/commit/4675797ab): api exception
- [`ec5b63b6a`](https://github.com/simple-robot/simpler-robot/commit/ec5b63b6a): exception
- [`5f512310b`](https://github.com/simple-robot/simpler-robot/commit/5f512310b): remove todo
- [`de72b0a6d`](https://github.com/simple-robot/simpler-robot/commit/de72b0a6d): lovelycat lazy cache
- [`ce3528e19..919cb703b`](https://github.com/simple-robot/simpler-robot/compare/ce3528e19..de72b0a6d): lock test
- [`787b216b4`](https://github.com/simple-robot/simpler-robot/commit/787b216b4): lock?
- [`fd066e783`](https://github.com/simple-robot/simpler-robot/commit/fd066e783): lovely cat api cache
- [`68ea0ada1`](https://github.com/simple-robot/simpler-robot/commit/68ea0ada1): listener cache compute
- [`487bc90c3..09f1be7d3`](https://github.com/simple-robot/simpler-robot/compare/487bc90c3..68ea0ada1): update ListenerContext
- [`f0f7a9abf`](https://github.com/simple-robot/simpler-robot/commit/f0f7a9abf): test
- [`fc23ef9d4`](https://github.com/simple-robot/simpler-robot/commit/fc23ef9d4): group
- [`792660315`](https://github.com/simple-robot/simpler-robot/commit/792660315): update to next v(a.11)
- [`0cc2ff0c5..91befa18b`](https://github.com/simple-robot/simpler-robot/compare/0cc2ff0c5..792660315): update log
- [`b7066fd9a`](https://github.com/simple-robot/simpler-robot/commit/b7066fd9a): 为 GroupAddRequest 追加群容器。 fix #12
- [`6fec34e3e..2ea207c91`](https://github.com/simple-robot/simpler-robot/compare/6fec34e3e..b7066fd9a): update doc
- [`4fca185c1`](https://github.com/simple-robot/simpler-robot/commit/4fca185c1): update readme
- [`d169a21b2`](https://github.com/simple-robot/simpler-robot/commit/d169a21b2): update log
- [`4cccfa922`](https://github.com/simple-robot/simpler-robot/commit/4cccfa922): fix #11
- [`59f75f65b`](https://github.com/simple-robot/simpler-robot/commit/59f75f65b): cache
- [`92eeb9b7f`](https://github.com/simple-robot/simpler-robot/commit/92eeb9b7f): lovely cat getter;
- [`dc7f982e6`](https://github.com/simple-robot/simpler-robot/commit/dc7f982e6): lovelycat
- [`0dc3f333a`](https://github.com/simple-robot/simpler-robot/commit/0dc3f333a): update to next alpha version(a.10)
- [`d5f00de0a`](https://github.com/simple-robot/simpler-robot/commit/d5f00de0a): lovely cat component;
- [`f58f6b5b5`](https://github.com/simple-robot/simpler-robot/commit/f58f6b5b5): Lovely cat sender;
- [`c31525e0b`](https://github.com/simple-robot/simpler-robot/commit/c31525e0b): at检测器更新,默认检测器变更为使用neko进行检测。
- [`3c6b189e1`](https://github.com/simple-robot/simpler-robot/commit/3c6b189e1): lovely cat sender;
- [`a58c2cab1`](https://github.com/simple-robot/simpler-robot/commit/a58c2cab1): core configs
- [`67e249035`](https://github.com/simple-robot/simpler-robot/commit/67e249035): ding configs
- [`e884e5b8b`](https://github.com/simple-robot/simpler-robot/commit/e884e5b8b): mirai configs
- [`4bfa955ce`](https://github.com/simple-robot/simpler-robot/commit/4bfa955ce): lovely cat events & configs
- [`f6eeafc39`](https://github.com/simple-robot/simpler-robot/commit/f6eeafc39): 全员增加减少事件
- [`d5616748c`](https://github.com/simple-robot/simpler-robot/commit/d5616748c): contacts change event
- [`430a412ed`](https://github.com/simple-robot/simpler-robot/commit/430a412ed): lovely cat friend verify event;
- [`55b9a3dce`](https://github.com/simple-robot/simpler-robot/commit/55b9a3dce): interface Requestable
- [`86d0e2163`](https://github.com/simple-robot/simpler-robot/commit/86d0e2163): update log
- [`0a3668cae`](https://github.com/simple-robot/simpler-robot/commit/0a3668cae): 扫描支付事件。
- [`3b2193c1a`](https://github.com/simple-robot/simpler-robot/commit/3b2193c1a): update log
- [`d3638d128`](https://github.com/simple-robot/simpler-robot/commit/d3638d128): lovely cat 转账事件
- [`b89cef061`](https://github.com/simple-robot/simpler-robot/commit/b89cef061): simbot app logs
- [`d7fb5b7de`](https://github.com/simple-robot/simpler-robot/commit/d7fb5b7de): update tips
- [`2693c0e94`](https://github.com/simple-robot/simpler-robot/commit/2693c0e94): update comment
- [`0f491aad6..5dc0893d6`](https://github.com/simple-robot/simpler-robot/compare/0f491aad6..2693c0e94): update log
- [`457746217`](https://github.com/simple-robot/simpler-robot/commit/457746217): fix #10 ;
- [`2adc001ac`](https://github.com/simple-robot/simpler-robot/commit/2adc001ac): ready to deploy
- [`3eda0e812..636db82fc`](https://github.com/simple-robot/simpler-robot/compare/3eda0e812..2adc001ac): lovely cat;
- [`48ed3c535`](https://github.com/simple-robot/simpler-robot/commit/48ed3c535): clear import
- [`37cb7e4c9`](https://github.com/simple-robot/simpler-robot/commit/37cb7e4c9): Revert "实现 ListenBreak解析 与 ListenResult解析";
- [`d48eaca15`](https://github.com/simple-robot/simpler-robot/commit/d48eaca15): clear import
- [`dd2f3871f`](https://github.com/simple-robot/simpler-robot/commit/dd2f3871f): 实现 ListenBreak解析 与 ListenResult解析
- [`42461f435`](https://github.com/simple-robot/simpler-robot/commit/42461f435): lovely cat private msg
- [`012d7cb4a`](https://github.com/simple-robot/simpler-robot/commit/012d7cb4a): private msg type
- [`2848bf7f9`](https://github.com/simple-robot/simpler-robot/commit/2848bf7f9): login event
- [`6e08e4c9a`](https://github.com/simple-robot/simpler-robot/commit/6e08e4c9a): update log
- [`900d50504..c3f60aff2`](https://github.com/simple-robot/simpler-robot/compare/900d50504..6e08e4c9a): lovelycat at code;
- [`d19dff02d`](https://github.com/simple-robot/simpler-robot/commit/d19dff02d): clear imports;
- [`bf8d65679`](https://github.com/simple-robot/simpler-robot/commit/bf8d65679): lovely cat;
- [`d4e289c13`](https://github.com/simple-robot/simpler-robot/commit/d4e289c13): RequestGets
- [`26cbcb9df`](https://github.com/simple-robot/simpler-robot/commit/26cbcb9df): mirai messages;
- [`238ef7bda`](https://github.com/simple-robot/simpler-robot/commit/238ef7bda): update comment
- [`f61c0919e`](https://github.com/simple-robot/simpler-robot/commit/f61c0919e): update to next (a.9
- [`cde3a2e23`](https://github.com/simple-robot/simpler-robot/commit/cde3a2e23): deploy a.8
- [`1d4fdd8a7..41c2a3a49`](https://github.com/simple-robot/simpler-robot/compare/1d4fdd8a7..cde3a2e23): ready to deploy;
- [`4dbdacdfe`](https://github.com/simple-robot/simpler-robot/commit/4dbdacdfe): fix #9 ;
- [`f6e28f669..c405ece9d`](https://github.com/simple-robot/simpler-robot/compare/f6e28f669..4dbdacdfe): lovely cat ;
- [`21b2fae30`](https://github.com/simple-robot/simpler-robot/commit/21b2fae30): update to next version(a.8)
- [`6dd0ba7af`](https://github.com/simple-robot/simpler-robot/commit/6dd0ba7af): update dokka to v1.4.10.2;
- [`d95a41418..4154945dc`](https://github.com/simple-robot/simpler-robot/compare/d95a41418..6dd0ba7af): update log;
- [`ef34fed03..52bd3edbc`](https://github.com/simple-robot/simpler-robot/compare/ef34fed03..4154945dc): update and ready to deploy;
- [`fac84edce`](https://github.com/simple-robot/simpler-robot/commit/fac84edce): event registrar
- [`618901edb`](https://github.com/simple-robot/simpler-robot/commit/618901edb): test
- [`1f1ca4d84`](https://github.com/simple-robot/simpler-robot/commit/1f1ca4d84): 变更toString信息
- [`4cc1aaa92`](https://github.com/simple-robot/simpler-robot/commit/4cc1aaa92): 移除多余输出
- [`7eb64ed79`](https://github.com/simple-robot/simpler-robot/commit/7eb64ed79): fix #8;
- [`aa4792d63`](https://github.com/simple-robot/simpler-robot/commit/aa4792d63): update log;
- [`c2724d604`](https://github.com/simple-robot/simpler-robot/commit/c2724d604): update hutool to v5.5.1;
- [`77a666150`](https://github.com/simple-robot/simpler-robot/commit/77a666150): mirai messages;
- [`de9e249f8`](https://github.com/simple-robot/simpler-robot/commit/de9e249f8): lovelycat api templates;
- [`6a9555efe`](https://github.com/simple-robot/simpler-robot/commit/6a9555efe): clear import;
- [`00c9c0d87`](https://github.com/simple-robot/simpler-robot/commit/00c9c0d87): mirai组件大部分位置的BotInfo支持获取等级信息
- [`0bf7c0367`](https://github.com/simple-robot/simpler-robot/commit/0bf7c0367): 修复日志国际格式化错误
- [`ffd7aa77e`](https://github.com/simple-robot/simpler-robot/commit/ffd7aa77e): 追加http-client模块的cookie携带; fix #7;
- [`c85419e58`](https://github.com/simple-robot/simpler-robot/commit/c85419e58): update to next version(a.7)
- [`698abb0b9`](https://github.com/simple-robot/simpler-robot/commit/698abb0b9): 优化过滤器匹配规则
- [`2fad654f4`](https://github.com/simple-robot/simpler-robot/commit/2fad654f4): update log;
- [`844b1e2f9`](https://github.com/simple-robot/simpler-robot/commit/844b1e2f9): update readme and comment;
- [`380a476db`](https://github.com/simple-robot/simpler-robot/commit/380a476db): update and deploy a.6;
- [`8b5cae1d2`](https://github.com/simple-robot/simpler-robot/commit/8b5cae1d2): update test;
- [`625ed6736`](https://github.com/simple-robot/simpler-robot/commit/625ed6736): fix https://github.com/ForteScarlet/simpler-robot/issues/6
- [`29f075f8b`](https://github.com/simple-robot/simpler-robot/commit/29f075f8b): Update issue templates
- [`258d67723`](https://github.com/simple-robot/simpler-robot/commit/258d67723): update test;
- [`072844b54`](https://github.com/simple-robot/simpler-robot/commit/072844b54): fix #5
- [`ef23a259e`](https://github.com/simple-robot/simpler-robot/commit/ef23a259e): update to next version(a.6)
- [`dcd67c962`](https://github.com/simple-robot/simpler-robot/commit/dcd67c962): issue templates.
- [`b90872984..9ee232d17`](https://github.com/simple-robot/simpler-robot/compare/b90872984..dcd67c962): move MessageContent package and deploy;
- [`91b1e5cbb`](https://github.com/simple-robot/simpler-robot/commit/91b1e5cbb): interceptor
- [`e9eb85f71`](https://github.com/simple-robot/simpler-robot/commit/e9eb85f71): http template cookies;
- [`f5522ab8a`](https://github.com/simple-robot/simpler-robot/commit/f5522ab8a): mirai level;
- [`ea0469a0e`](https://github.com/simple-robot/simpler-robot/commit/ea0469a0e): lovelycat .
- [`d9ab9f775..5f87d2cad`](https://github.com/simple-robot/simpler-robot/compare/d9ab9f775..ea0469a0e): lovely cat component;
- [`e9df9d6f0`](https://github.com/simple-robot/simpler-robot/commit/e9df9d6f0): update to next alpha version
- [`97dc5d410`](https://github.com/simple-robot/simpler-robot/commit/97dc5d410): deploy a.4 and update log.
- [`dedebb076`](https://github.com/simple-robot/simpler-robot/commit/dedebb076): update tips
- [`0a3a257ec`](https://github.com/simple-robot/simpler-robot/commit/0a3a257ec): 优化mirai日志展示
- [`c073d06d5`](https://github.com/simple-robot/simpler-robot/commit/c073d06d5): 暂时移除未完成的可爱猫组件module;
- [`8fb3fd51d`](https://github.com/simple-robot/simpler-robot/commit/8fb3fd51d): try fix #3
- [`2d7b66bbd`](https://github.com/simple-robot/simpler-robot/commit/2d7b66bbd): 用不着，以前写过了..
- [`41d105c4a`](https://github.com/simple-robot/simpler-robot/commit/41d105c4a): mirai组件增加bot的shutdown hook
- [`181a5f6b5`](https://github.com/simple-robot/simpler-robot/commit/181a5f6b5): move configuration
- [`5b40182a8`](https://github.com/simple-robot/simpler-robot/commit/5b40182a8): update some version.
- [`615e65449`](https://github.com/simple-robot/simpler-robot/commit/615e65449): mirai text and base text.
- [`286fc8110`](https://github.com/simple-robot/simpler-robot/commit/286fc8110): fix #2
- [`946787d29`](https://github.com/simple-robot/simpler-robot/commit/946787d29): fix #1
- [`681977f4c`](https://github.com/simple-robot/simpler-robot/commit/681977f4c): lovely cat readme;
- [`5e5537850`](https://github.com/simple-robot/simpler-robot/commit/5e5537850): update kt version.
- [`76369e8da`](https://github.com/simple-robot/simpler-robot/commit/76369e8da): test
- [`1a12a9265`](https://github.com/simple-robot/simpler-robot/commit/1a12a9265): http client template.
- [`cdb651916..edc5ff816`](https://github.com/simple-robot/simpler-robot/compare/cdb651916..1a12a9265): lovely cat component.
- [`3971f84ed`](https://github.com/simple-robot/simpler-robot/commit/3971f84ed): fast json auto configure
- [`9ebd00c5d`](https://github.com/simple-robot/simpler-robot/commit/9ebd00c5d): move ComponentBeans
- [`f8430ce7d`](https://github.com/simple-robot/simpler-robot/commit/f8430ce7d): api bot as accessInfo.
- [`d4dc14d98`](https://github.com/simple-robot/simpler-robot/commit/d4dc14d98): logger.
- [`cddce3f0d`](https://github.com/simple-robot/simpler-robot/commit/cddce3f0d): import clean.
- [`ba27bf0f8`](https://github.com/simple-robot/simpler-robot/commit/ba27bf0f8): test.
- [`92b19e898`](https://github.com/simple-robot/simpler-robot/commit/92b19e898): move message content builder.
- [`f9014bdc0..6ca3498c6`](https://github.com/simple-robot/simpler-robot/compare/f9014bdc0..92b19e898): mirai-bot logger.
- [`c77874453`](https://github.com/simple-robot/simpler-robot/commit/c77874453): ver a.4
- [`4f4ac772f..cce9e5127`](https://github.com/simple-robot/simpler-robot/compare/4f4ac772f..c77874453): component-ding
- [`81f163615`](https://github.com/simple-robot/simpler-robot/commit/81f163615): 白忙活了
- [`54f5b88b0`](https://github.com/simple-robot/simpler-robot/commit/54f5b88b0): moshi json.
- [`5f9fbce0e`](https://github.com/simple-robot/simpler-robot/commit/5f9fbce0e): tips
- [`061423cee`](https://github.com/simple-robot/simpler-robot/commit/061423cee): update logo
- [`bc1add809`](https://github.com/simple-robot/simpler-robot/commit/bc1add809): update copyright; clear import;
- [`80018e2ff..e9bdfdeff`](https://github.com/simple-robot/simpler-robot/compare/80018e2ff..bc1add809): update log.
- [`e7e3273af`](https://github.com/simple-robot/simpler-robot/commit/e7e3273af): pom
- [`6806be369`](https://github.com/simple-robot/simpler-robot/commit/6806be369): flag; content;
- [`ca6b687e8`](https://github.com/simple-robot/simpler-robot/commit/ca6b687e8): readme;
- [`5a3f84a86`](https://github.com/simple-robot/simpler-robot/commit/5a3f84a86): img builder; content; filter text test;
- [`846a45bd0`](https://github.com/simple-robot/simpler-robot/commit/846a45bd0): test getText and getMsg;
- [`e6b2f9e03`](https://github.com/simple-robot/simpler-robot/commit/e6b2f9e03): update version; 重新实现 messageContent;
- [`17dde8569`](https://github.com/simple-robot/simpler-robot/commit/17dde8569): core-starter移除部分多余控制台输出
- [`fbe649e0f`](https://github.com/simple-robot/simpler-robot/commit/fbe649e0f): 调整为新的 messageContent 实现。
- [`13d845059`](https://github.com/simple-robot/simpler-robot/commit/13d845059): builder
- [`66a285a4d`](https://github.com/simple-robot/simpler-robot/commit/66a285a4d): msg content.
- [`3cb9e968a`](https://github.com/simple-robot/simpler-robot/commit/3cb9e968a): 注释
- [`8aeabe774`](https://github.com/simple-robot/simpler-robot/commit/8aeabe774): content
- [`abb015f2f`](https://github.com/simple-robot/simpler-robot/commit/abb015f2f): catcode version.
- [`2e48a32b2`](https://github.com/simple-robot/simpler-robot/commit/2e48a32b2): MessageContent-cats
- [`89210ccff`](https://github.com/simple-robot/simpler-robot/commit/89210ccff): :bulb: 添加源码注释
- [`621db829a`](https://github.com/simple-robot/simpler-robot/commit/621db829a): msg content.
- [`7531889ae`](https://github.com/simple-robot/simpler-robot/commit/7531889ae): msg parser.
- [`7179e6628..9c6c00fb8`](https://github.com/simple-robot/simpler-robot/compare/7179e6628..7531889ae): readme.
- [`9211e3c1c..e246878a5`](https://github.com/simple-robot/simpler-robot/compare/9211e3c1c..9c6c00fb8): readmes.
- [`201459e4e..d883a5d90`](https://github.com/simple-robot/simpler-robot/compare/201459e4e..e246878a5): update to alpha.2
- [`530bac111`](https://github.com/simple-robot/simpler-robot/commit/530bac111): readme
- [`6ed3bd24c`](https://github.com/simple-robot/simpler-robot/commit/6ed3bd24c): mirai-starter
- [`77dc6c828`](https://github.com/simple-robot/simpler-robot/commit/77dc6c828): spring metadata
- [`4ecbce643`](https://github.com/simple-robot/simpler-robot/commit/4ecbce643): update springboot-starter; 追加springboot部分配置文件提示。
- [`f0f7837be`](https://github.com/simple-robot/simpler-robot/commit/f0f7837be): 任务执行器; test
- [`fbe5dfeb4`](https://github.com/simple-robot/simpler-robot/commit/fbe5dfeb4): 任务执行器
- [`d1b121b29..81759ca0a`](https://github.com/simple-robot/simpler-robot/compare/d1b121b29..fbe5dfeb4): readme
- [`2a3e943a9`](https://github.com/simple-robot/simpler-robot/commit/2a3e943a9): logger;
- [`7c31644b2`](https://github.com/simple-robot/simpler-robot/commit/7c31644b2): logger; clean import; listener registers;
- [`9021aecb6`](https://github.com/simple-robot/simpler-robot/commit/9021aecb6): Core springboot starter module
- [`2e7e88682`](https://github.com/simple-robot/simpler-robot/commit/2e7e88682): Springboot starter module
- [`496ce2297`](https://github.com/simple-robot/simpler-robot/commit/496ce2297): api-Task runner
- [`4bb9a2eb1`](https://github.com/simple-robot/simpler-robot/commit/4bb9a2eb1): json-fastjson
- [`ec0ec94ca`](https://github.com/simple-robot/simpler-robot/commit/ec0ec94ca): http template.
- [`d1353003c`](https://github.com/simple-robot/simpler-robot/commit/d1353003c): remove some comment
- [`b7412161d..8b226e862`](https://github.com/simple-robot/simpler-robot/compare/b7412161d..d1353003c): http template.
- [`1245d35d4`](https://github.com/simple-robot/simpler-robot/commit/1245d35d4): http-template-ktor
- [`95901e6cc`](https://github.com/simple-robot/simpler-robot/commit/95901e6cc): http-template-core
- [`3a414fc1e`](https://github.com/simple-robot/simpler-robot/commit/3a414fc1e): json-moshi
- [`4c0d22b3f`](https://github.com/simple-robot/simpler-robot/commit/4c0d22b3f): json-core
- [`37c7ca80c`](https://github.com/simple-robot/simpler-robot/commit/37c7ca80c): json-moshi
- [`29ad48f9a`](https://github.com/simple-robot/simpler-robot/commit/29ad48f9a): http client; json core;
- [`5e6e9443f`](https://github.com/simple-robot/simpler-robot/commit/5e6e9443f): http client;
- [`a641d884e..f6512edc4`](https://github.com/simple-robot/simpler-robot/compare/a641d884e..5e6e9443f): ktor.
- [`54d43c079`](https://github.com/simple-robot/simpler-robot/commit/54d43c079): http template core
- [`6b5690c21`](https://github.com/simple-robot/simpler-robot/commit/6b5690c21): http template
- [`1480b6aa1`](https://github.com/simple-robot/simpler-robot/commit/1480b6aa1): 修改部分顺序
- [`39f1ed203`](https://github.com/simple-robot/simpler-robot/commit/39f1ed203): update logger.
- [`83fc0e30c..962fee2f0`](https://github.com/simple-robot/simpler-robot/compare/83fc0e30c..39f1ed203): tips
- [`160afa875`](https://github.com/simple-robot/simpler-robot/commit/160afa875): MessageEventGet更名为MessageGet
- [`034e32129..91a34056e`](https://github.com/simple-robot/simpler-robot/compare/034e32129..160afa875): tips.
- [`2fb0f7c9e`](https://github.com/simple-robot/simpler-robot/commit/2fb0f7c9e): remove test main.
- [`09acaf45b`](https://github.com/simple-robot/simpler-robot/commit/09acaf45b): 夹点儿私货
- [`7b8d549b4`](https://github.com/simple-robot/simpler-robot/commit/7b8d549b4): remove test main.
- [`a9278518f`](https://github.com/simple-robot/simpler-robot/commit/a9278518f): 夹点儿私货
- [`141562a5b`](https://github.com/simple-robot/simpler-robot/commit/141562a5b): readme.
- [`13940ea37`](https://github.com/simple-robot/simpler-robot/commit/13940ea37): version. sb-maven plugin.
- [`338440be8`](https://github.com/simple-robot/simpler-robot/commit/338440be8): simbot v2.0.0-ALPHA.1
- [`ce852db6a`](https://github.com/simple-robot/simpler-robot/commit/ce852db6a): log
- [`beb79377b`](https://github.com/simple-robot/simpler-robot/commit/beb79377b): at, filter, msg&text
- [`d883bf8d9`](https://github.com/simple-robot/simpler-robot/commit/d883bf8d9): 隐藏/修改文件编译名称
- [`a2c256fea`](https://github.com/simple-robot/simpler-robot/commit/a2c256fea): update, conf.
- [`312a45d8e`](https://github.com/simple-robot/simpler-robot/commit/312a45d8e): update.
- [`a719c40b8`](https://github.com/simple-robot/simpler-robot/commit/a719c40b8): events.
- [`e790bdd89`](https://github.com/simple-robot/simpler-robot/commit/e790bdd89): mute; time;
- [`2cb571dfa`](https://github.com/simple-robot/simpler-robot/commit/2cb571dfa): events
- [`965a33de0`](https://github.com/simple-robot/simpler-robot/commit/965a33de0): 调整包结构
- [`34382bc46`](https://github.com/simple-robot/simpler-robot/commit/34382bc46): nudge msg; request msg; flags; setters; content;
- [`da3de4c25`](https://github.com/simple-robot/simpler-robot/commit/da3de4c25): requests; result;
- [`b49a747a1`](https://github.com/simple-robot/simpler-robot/commit/b49a747a1): 調整包结构，api下包路径不再作为core的子包
- [`8da0de1f1`](https://github.com/simple-robot/simpler-robot/commit/8da0de1f1): sender.
- [`9221a0570`](https://github.com/simple-robot/simpler-robot/commit/9221a0570): readme.
- [`660a50974`](https://github.com/simple-robot/simpler-robot/commit/660a50974): 调整结构。
- [`bee814e14`](https://github.com/simple-robot/simpler-robot/commit/bee814e14): 调整结构，调整编译文件名。
- [`59f5d3dd2`](https://github.com/simple-robot/simpler-robot/commit/59f5d3dd2): filter
- [`c18309dff`](https://github.com/simple-robot/simpler-robot/commit/c18309dff): in
- [`9d7d7c7f5`](https://github.com/simple-robot/simpler-robot/commit/9d7d7c7f5): readme, comment
- [`165aef8ff`](https://github.com/simple-robot/simpler-robot/commit/165aef8ff): 文档地址
- [`360197231`](https://github.com/simple-robot/simpler-robot/commit/360197231): 测试群消息发送
- [`293a10ae4`](https://github.com/simple-robot/simpler-robot/commit/293a10ae4): 测试私聊与回复、消息builder、复读。
- [`87215f01a`](https://github.com/simple-robot/simpler-robot/commit/87215f01a): mirai.
- [`eb21771c7`](https://github.com/simple-robot/simpler-robot/commit/eb21771c7): logger conf.
- [`9577c64b1`](https://github.com/simple-robot/simpler-robot/commit/9577c64b1): mirai private msg.
- [`61b495aa3`](https://github.com/simple-robot/simpler-robot/commit/61b495aa3): mirai sender.
- [`4f251809a`](https://github.com/simple-robot/simpler-robot/commit/4f251809a): mirai setter
- [`8c91f514f`](https://github.com/simple-robot/simpler-robot/commit/8c91f514f): getter, setter.
- [`39f4d1320`](https://github.com/simple-robot/simpler-robot/commit/39f4d1320): log, config, core, mirai
- [`2dbd56f7d`](https://github.com/simple-robot/simpler-robot/commit/2dbd56f7d): core, api, component.
- [`6cb396e51`](https://github.com/simple-robot/simpler-robot/commit/6cb396e51): parent pom
- [`756f9f9e0`](https://github.com/simple-robot/simpler-robot/commit/756f9f9e0): simbot.
- [`371778eb1`](https://github.com/simple-robot/simpler-robot/commit/371778eb1): bot registers.
- [`a26abf616`](https://github.com/simple-robot/simpler-robot/commit/a26abf616): listen test.
- [`82c13f5fe`](https://github.com/simple-robot/simpler-robot/commit/82c13f5fe): logger;sender;
- [`09fb23176`](https://github.com/simple-robot/simpler-robot/commit/09fb23176): app;depend;mirai test;test;conf;
- [`dc5523b10`](https://github.com/simple-robot/simpler-robot/commit/dc5523b10): mirai try.
- [`aab34f54c`](https://github.com/simple-robot/simpler-robot/commit/aab34f54c): pom url.
- [`715c2af5c`](https://github.com/simple-robot/simpler-robot/commit/715c2af5c): mirai component
- [`c89475a9e`](https://github.com/simple-robot/simpler-robot/commit/c89475a9e): listener?
- [`aac7ea30d`](https://github.com/simple-robot/simpler-robot/commit/aac7ea30d): component
- [`0ba7b1a5c`](https://github.com/simple-robot/simpler-robot/commit/0ba7b1a5c): oh!
- [`4b7852483`](https://github.com/simple-robot/simpler-robot/commit/4b7852483): config; listener; ioc;
- [`86b3dc1ce`](https://github.com/simple-robot/simpler-robot/commit/86b3dc1ce): listener.
- [`82aaec79c`](https://github.com/simple-robot/simpler-robot/commit/82aaec79c): listener manager.
- [`9d1e39336`](https://github.com/simple-robot/simpler-robot/commit/9d1e39336): listener intercept chain.
- [`51a067162`](https://github.com/simple-robot/simpler-robot/commit/51a067162): listener manager.
- [`6033536c5`](https://github.com/simple-robot/simpler-robot/commit/6033536c5): sort queue.
- [`2ff8ff892..210bac84f`](https://github.com/simple-robot/simpler-robot/compare/2ff8ff892..6033536c5): ListenerManagerBuilder.
- [`2ec596b7a`](https://github.com/simple-robot/simpler-robot/commit/2ec596b7a): filters.
- [`83f304df0`](https://github.com/simple-robot/simpler-robot/commit/83f304df0): import clear.
- [`7c63248f0`](https://github.com/simple-robot/simpler-robot/commit/7c63248f0): some.
- [`420f0c34a`](https://github.com/simple-robot/simpler-robot/commit/420f0c34a): msgs.
- [`23841f824`](https://github.com/simple-robot/simpler-robot/commit/23841f824): wildcat code template
- [`0916fb84f`](https://github.com/simple-robot/simpler-robot/commit/0916fb84f): neko some.
- [`22433d7d6`](https://github.com/simple-robot/simpler-robot/commit/22433d7d6): remove some.
- [`0cb7f8424`](https://github.com/simple-robot/simpler-robot/commit/0cb7f8424): msg interceptor chain.
- [`1a6150d7a`](https://github.com/simple-robot/simpler-robot/commit/1a6150d7a): chained interceptor, listener intercept, listener chain factory.
- [`fa8c97fd6`](https://github.com/simple-robot/simpler-robot/commit/fa8c97fd6): listener manager.
- [`cdea0c24f`](https://github.com/simple-robot/simpler-robot/commit/cdea0c24f): pre,post init
- [`506b0e20b`](https://github.com/simple-robot/simpler-robot/commit/506b0e20b): pass
- [`9257d756d`](https://github.com/simple-robot/simpler-robot/commit/9257d756d): ex, api, listen func, filter.
- [`deff592e1`](https://github.com/simple-robot/simpler-robot/commit/deff592e1): listen result.
- [`0e1a27863`](https://github.com/simple-robot/simpler-robot/commit/0e1a27863): infix funcs.
- [`58355444f`](https://github.com/simple-robot/simpler-robot/commit/58355444f): :page_facing_up: Adding or updating license.
- [`23663429e`](https://github.com/simple-robot/simpler-robot/commit/23663429e): common's version update.
- [`18c67fabe..05b60dcdd`](https://github.com/simple-robot/simpler-robot/compare/18c67fabe..23663429e): update readme.
- [`f2cb43917`](https://github.com/simple-robot/simpler-robot/commit/f2cb43917): 独立common模块
- [`39a164ce6..9f16c8eb5`](https://github.com/simple-robot/simpler-robot/compare/39a164ce6..f2cb43917): neko!
- [`6d385d805`](https://github.com/simple-robot/simpler-robot/commit/6d385d805): core; api; code;
- [`c938f3d20..5748e7827`](https://github.com/simple-robot/simpler-robot/compare/c938f3d20..6d385d805): core; api;
- [`01a642d13..f9985c2c5`](https://github.com/simple-robot/simpler-robot/compare/01a642d13..5748e7827): ioc
- [`ee12b7c9c`](https://github.com/simple-robot/simpler-robot/commit/ee12b7c9c): annotationUtil
- [`223a05183`](https://github.com/simple-robot/simpler-robot/commit/223a05183): ioc
- [`270951296..d8c75fb3b`](https://github.com/simple-robot/simpler-robot/compare/270951296..223a05183): AnnotationUtil
- [`186ec9265`](https://github.com/simple-robot/simpler-robot/commit/186ec9265): AnnotationUtil ioc
- [`299fadef4..6a136aad2`](https://github.com/simple-robot/simpler-robot/compare/299fadef4..186ec9265): logger
- [`dfb65b5b1`](https://github.com/simple-robot/simpler-robot/commit/dfb65b5b1): color
- [`f8433b46a..dca74d621`](https://github.com/simple-robot/simpler-robot/compare/f8433b46a..dfb65b5b1): logger
- [`9a0d24d51`](https://github.com/simple-robot/simpler-robot/commit/9a0d24d51): 配置读取相关 test;
- [`d1e0a46bf..250f2febe`](https://github.com/simple-robot/simpler-robot/compare/d1e0a46bf..9a0d24d51): 配置读取相关;
- [`ab1e33e42`](https://github.com/simple-robot/simpler-robot/commit/ab1e33e42): 配置读取相关; 常量相关;
- [`ac4e3a8c7`](https://github.com/simple-robot/simpler-robot/commit/ac4e3a8c7): configuration相关
- [`96dbb8a07`](https://github.com/simple-robot/simpler-robot/commit/96dbb8a07): update copyright
- [`107fef983`](https://github.com/simple-robot/simpler-robot/commit/107fef983): annotation utils
- [`42b2985b1`](https://github.com/simple-robot/simpler-robot/commit/42b2985b1): utils
- [`f12967a74`](https://github.com/simple-robot/simpler-robot/commit/f12967a74): 包结构; 新建模块;
- [`f364b3c83..d3b5a7eea`](https://github.com/simple-robot/simpler-robot/compare/f364b3c83..f12967a74): 类型转化器;
- [`eaee7b09c`](https://github.com/simple-robot/simpler-robot/commit/eaee7b09c): 部分接口定义; ioc模块
- [`48524a9ff`](https://github.com/simple-robot/simpler-robot/commit/48524a9ff): 定义送信器接口及其附属
- [`67787926c`](https://github.com/simple-robot/simpler-robot/commit/67787926c): 调整结构, 增加模块
- [`70a0aaa90..70b6ca6b4`](https://github.com/simple-robot/simpler-robot/compare/70a0aaa90..67787926c): 调整结构
- [`3f0addb5b`](https://github.com/simple-robot/simpler-robot/commit/3f0addb5b): setters
- [`34d9edbef`](https://github.com/simple-robot/simpler-robot/commit/34d9edbef): result and other
- [`b3fecc178`](https://github.com/simple-robot/simpler-robot/commit/b3fecc178): 调整注释
- [`8b0290c22`](https://github.com/simple-robot/simpler-robot/commit/8b0290c22): 不断地尝试调整kdoc样式并最终放弃
- [`fd7d7be38`](https://github.com/simple-robot/simpler-robot/commit/fd7d7be38): sender and carrier
- [`579e39f5e`](https://github.com/simple-robot/simpler-robot/commit/579e39f5e): 变更包名
- [`2c951097a`](https://github.com/simple-robot/simpler-robot/commit/2c951097a): containers 结构
- [`6b802155a`](https://github.com/simple-robot/simpler-robot/commit/6b802155a): changed events
- [`d99c18c9f`](https://github.com/simple-robot/simpler-robot/commit/d99c18c9f): msg content
- [`8d4a01621`](https://github.com/simple-robot/simpler-robot/commit/8d4a01621): operator and beOperator
- [`523b1728b`](https://github.com/simple-robot/simpler-robot/commit/523b1728b): 请求 - invitor
- [`68b49c15d`](https://github.com/simple-robot/simpler-robot/commit/68b49c15d): 描述标识
- [`fd3f8d039`](https://github.com/simple-robot/simpler-robot/commit/fd3f8d039): 调整annotation包的位置
- [`4e11500b0`](https://github.com/simple-robot/simpler-robot/commit/4e11500b0): flag; 请求父接口;
- [`e4a609e31`](https://github.com/simple-robot/simpler-robot/commit/e4a609e31): flag; assists;
- [`67b9aa30d`](https://github.com/simple-robot/simpler-robot/commit/67b9aa30d): 添加描述性接口 减少事件
- [`07f615d75`](https://github.com/simple-robot/simpler-robot/commit/07f615d75): 定义接口
- [`5d806b246`](https://github.com/simple-robot/simpler-robot/commit/5d806b246): update dokka plugin.
- [`75e8d74df`](https://github.com/simple-robot/simpler-robot/commit/75e8d74df): master doc delete and ignored
- [`471f075d1..747e78e26`](https://github.com/simple-robot/simpler-robot/compare/471f075d1..75e8d74df): index.html
- [`f8bf2ce56`](https://github.com/simple-robot/simpler-robot/commit/f8bf2ce56): 增加doc路径
- [`dfde4a20d..35ffbf9a9`](https://github.com/simple-robot/simpler-robot/compare/dfde4a20d..f8bf2ce56): dokka plugin
- [`886b265df..65d87ae93`](https://github.com/simple-robot/simpler-robot/compare/886b265df..35ffbf9a9): 添加上测试相关的模块
- [`05a00a02b`](https://github.com/simple-robot/simpler-robot/commit/05a00a02b): 定义api模块内相关接口
- [`98adf530e..2e326bff1`](https://github.com/simple-robot/simpler-robot/compare/98adf530e..05a00a02b): init

## v3.0.0-beta-M1

> Release & Pull Notes: [v3.0.0-beta-M1](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0-beta-M1)
>
> Commit compare: [v2.3.9..v3.0.0-beta-M1](https://github.com/simple-robot/simpler-robot/compare/v2.3.9..v3.0.0-beta-M1)

- [`e31ba2964`](https://github.com/simple-robot/simpler-robot/commit/e31ba2964): update: 版本更新至 v3.0.0-beta-M1
- [`2ce1562bf..f28df497d`](https://github.com/simple-robot/simpler-robot/compare/2ce1562bf..e31ba2964): update(buildSrc): 更新调整buildSrc内容
- [`714e348a1`](https://github.com/simple-robot/simpler-robot/commit/714e348a1): update(buildSrc): 更新调整buildSrc文件结构
- [`39b950025`](https://github.com/simple-robot/simpler-robot/commit/39b950025): 消除/修复警告
- [`62b67a702`](https://github.com/simple-robot/simpler-robot/commit/62b67a702): gradle 配置
- [`7e565b9ea..a80891923`](https://github.com/simple-robot/simpler-robot/compare/7e565b9ea..62b67a702): 整理 gradle build src
- [`b6b36131a`](https://github.com/simple-robot/simpler-robot/commit/b6b36131a): fix(simbot-api): 修复获取PlainText导致堆栈溢出的问题
- [`cb88b3b7e`](https://github.com/simple-robot/simpler-robot/commit/cb88b3b7e): 清理代码
- [`9fec6a56a`](https://github.com/simple-robot/simpler-robot/commit/9fec6a56a): simbot-boots 模块下相关依赖改造
- [`bfe802dc1`](https://github.com/simple-robot/simpler-robot/commit/bfe802dc1): 依赖调整、更新
- [`6b6e984e5`](https://github.com/simple-robot/simpler-robot/commit/6b6e984e5): simbot-core 模块下依赖管理改造
- [`cf9a6843b`](https://github.com/simple-robot/simpler-robot/commit/cf9a6843b): simbot-apis 模块下依赖管理改造
- [`2a5e86e69`](https://github.com/simple-robot/simpler-robot/commit/2a5e86e69): 弃用 `@Filter(target = TargetFilter(...))`, 以 `@Filter(targets = Filter.Targets(...))` 取代之
- [`35385ebab`](https://github.com/simple-robot/simpler-robot/commit/35385ebab): 版本调整至 `v3.v3.0.0.preview.18.0`
- [`f24c8c889..c3f69148f`](https://github.com/simple-robot/simpler-robot/compare/f24c8c889..35385ebab): 调整注释
- [`ed19e7b59`](https://github.com/simple-robot/simpler-robot/commit/ed19e7b59): fix: 清理遗留代码
- [`4350ef4e3`](https://github.com/simple-robot/simpler-robot/commit/4350ef4e3): update release config
- [`596a52c02`](https://github.com/simple-robot/simpler-robot/commit/596a52c02): 更新changelog
- [`f1e26f977`](https://github.com/simple-robot/simpler-robot/commit/f1e26f977): Update release.yml
- [`9a19c8452`](https://github.com/simple-robot/simpler-robot/commit/9a19c8452): Release: v3.0.0.preview.17.1
- [`527574e90`](https://github.com/simple-robot/simpler-robot/commit/527574e90): github release config
- [`ac8fe567d`](https://github.com/simple-robot/simpler-robot/commit/ac8fe567d): 改善SpringBoot下对于直接注册监听函数的处理
- [`d56f468dd`](https://github.com/simple-robot/simpler-robot/commit/d56f468dd): fix(boot): 修复过滤器注解处理器无法获取监听函数id的问题
- [`4a1dd2964`](https://github.com/simple-robot/simpler-robot/commit/4a1dd2964): build(project): 调整项目目录结构
- [`46ffe38d7`](https://github.com/simple-robot/simpler-robot/commit/46ffe38d7): perf(project): version to v3.0.0.preview.17.1
- [`8ebb95dfb`](https://github.com/simple-robot/simpler-robot/commit/8ebb95dfb): test依赖调整
- [`0c090c913`](https://github.com/simple-robot/simpler-robot/commit/0c090c913): 持续会话机制变更
- [`fd0f65c5d`](https://github.com/simple-robot/simpler-robot/commit/fd0f65c5d): Resource to Image
- [`1fc130a3f`](https://github.com/simple-robot/simpler-robot/commit/1fc130a3f): README.md
- [`cfc656b75`](https://github.com/simple-robot/simpler-robot/commit/cfc656b75): feat(component): `Component.id` 调整为String类型； feat(nullable): 细化部分函数的可空与不可空
- [`092d2b8c5`](https://github.com/simple-robot/simpler-robot/commit/092d2b8c5): fix(application): 修复Application关闭无效问题
- [`7befb6c19`](https://github.com/simple-robot/simpler-robot/commit/7befb6c19): 调整优化日志输出
- [`3bc5ede5d`](https://github.com/simple-robot/simpler-robot/commit/3bc5ede5d): ApplicationFactory会在未配置Job时补充Job
- [`41fc6ad24`](https://github.com/simple-robot/simpler-robot/commit/41fc6ad24): binder增加作用域和序列化模块
- [`403474e16`](https://github.com/simple-robot/simpler-robot/commit/403474e16): 为各种预设属性/作用域提供扩展
- [`a61504d41`](https://github.com/simple-robot/simpler-robot/commit/a61504d41): 为各种预设属性提供扩展
- [`4af518a0a`](https://github.com/simple-robot/simpler-robot/commit/4af518a0a): 提供并实现 `ApplicationAttributes` 约定属性
- [`3c158af32`](https://github.com/simple-robot/simpler-robot/commit/3c158af32): 为 Application 提供 botManager 属性
- [`c9c85ec1a`](https://github.com/simple-robot/simpler-robot/commit/c9c85ec1a): 使@Listener支持EventListenerBuilder和EventListener解析
- [`946c3f758`](https://github.com/simple-robot/simpler-robot/commit/946c3f758): @Listener支持EventListenerBuilder和EventListener解析
- [`ff8ffd6eb`](https://github.com/simple-robot/simpler-robot/commit/ff8ffd6eb): boot支持EventListenerBuilder
- [`8b3a7d700`](https://github.com/simple-robot/simpler-robot/commit/8b3a7d700): 提供 `EventListenerBuilder`
- [`ece52d2b5`](https://github.com/simple-robot/simpler-robot/commit/ece52d2b5): 调整ApplicationBuilder中的流程，bot的注册将会在Application完成后执行；
- [`4fec72717`](https://github.com/simple-robot/simpler-robot/commit/4fec72717): 清理注释
- [`a635f85ee`](https://github.com/simple-robot/simpler-robot/commit/a635f85ee): 为 `Switchable` 中的 `*Async` 函数增加 `Future` 返回值
- [`469187275`](https://github.com/simple-robot/simpler-robot/commit/469187275): 清理import
- [`8be095809`](https://github.com/simple-robot/simpler-robot/commit/8be095809): 移除测试
- [`c02e1cef3`](https://github.com/simple-robot/simpler-robot/commit/c02e1cef3): for DelayableCoroutineScope
- [`dfedeaf73..989db3f5e`](https://github.com/simple-robot/simpler-robot/compare/dfedeaf73..c02e1cef3): 实现新特性 DelayableCoroutineScope
- [`f6958422e`](https://github.com/simple-robot/simpler-robot/commit/f6958422e): 实现 SimpleListenerBuilder 并替换 EventListenersGenerator
- [`d849e57de`](https://github.com/simple-robot/simpler-robot/commit/d849e57de): 版本更至 v3.0.0.preview.17.0
- [`474384138`](https://github.com/simple-robot/simpler-robot/commit/474384138): 实现 SimpleListenerBuilder 并替换 EventListenersGenerator
- [`54b097d0b`](https://github.com/simple-robot/simpler-robot/commit/54b097d0b): 准备发布版本 v3.0.0.preview.16.0
- [`0c756149f`](https://github.com/simple-robot/simpler-robot/commit/0c756149f): 调整changelog生成描述
- [`a5ce5245b`](https://github.com/simple-robot/simpler-robot/commit/a5ce5245b): 调整事件处理的日志
- [`f24bb991c`](https://github.com/simple-robot/simpler-robot/commit/f24bb991c): 标记 `EventListener.logger` 为过时并计划删除
- [`3e701ef1d`](https://github.com/simple-robot/simpler-robot/commit/3e701ef1d): 移除 `EventListener` 的 `IDContainer` 实现，并变更 `EventListener.id` 类型为 `String`
- [`e6f9f61a1`](https://github.com/simple-robot/simpler-robot/commit/e6f9f61a1): 清理或调整部分TODO
- [`450517f61`](https://github.com/simple-robot/simpler-robot/commit/450517f61): 函数接口重命名
- [`0541cca09`](https://github.com/simple-robot/simpler-robot/commit/0541cca09): 补充持续会话注释
- [`fdc4ac30c`](https://github.com/simple-robot/simpler-robot/commit/fdc4ac30c): 调整c继续会话api
- [`2c410e50a`](https://github.com/simple-robot/simpler-robot/commit/2c410e50a): Scope移动为Core模块特性
- [`ae9511fa7..87d22dcc4`](https://github.com/simple-robot/simpler-robot/compare/ae9511fa7..2c410e50a): 将所有Core相关的内容重命名为Simple
- [`6a73d81aa..fdd4b94a4`](https://github.com/simple-robot/simpler-robot/compare/6a73d81aa..87d22dcc4): 持续会话思考
- [`ce1c25704`](https://github.com/simple-robot/simpler-robot/commit/ce1c25704): 版本更新到 v3.0.0.preview.16.x
- [`029857389`](https://github.com/simple-robot/simpler-robot/commit/029857389): 注解过滤器工厂
- [`4ffd44fb1`](https://github.com/simple-robot/simpler-robot/commit/4ffd44fb1): 准备changelog
- [`28d956c98..6379535bf`](https://github.com/simple-robot/simpler-robot/compare/28d956c98..4ffd44fb1): 性能测试
- [`9e14848aa`](https://github.com/simple-robot/simpler-robot/commit/9e14848aa): 清理测试文件
- [`31b6440a1..1b3c36c39`](https://github.com/simple-robot/simpler-robot/compare/31b6440a1..9e14848aa): 调整测试报告
- [`d74152c70`](https://github.com/simple-robot/simpler-robot/commit/d74152c70): 弃用Filter.or 和 Filter.and；提供新的注解过滤器工厂
- [`a9d023b3a`](https://github.com/simple-robot/simpler-robot/commit/a9d023b3a): 依赖版本更新: Kotlinx Serialization `v1.3.1` -> `v1.3.3`
- [`3dd314527`](https://github.com/simple-robot/simpler-robot/commit/3dd314527): 依赖版本更新: Kotlinx Coroutines `v1.6.1` -> `v1.6.2`
- [`9f5b38788`](https://github.com/simple-robot/simpler-robot/commit/9f5b38788): 使用testng
- [`60cc04c45`](https://github.com/simple-robot/simpler-robot/commit/60cc04c45): ID相关更新；增加JMH性能测试报告；
- [`6001342f8`](https://github.com/simple-robot/simpler-robot/commit/6001342f8): 依赖版本更新: Kotlin `v1.6.10` -> `v1.6.21`
- [`1bb6a371e`](https://github.com/simple-robot/simpler-robot/commit/1bb6a371e): ResourceImage消除警告
- [`c6068ee67`](https://github.com/simple-robot/simpler-robot/commit/c6068ee67): 清理代码
- [`2731e059b`](https://github.com/simple-robot/simpler-robot/commit/2731e059b): `Image.asImage` -> `Image.toImage`
- [`ac15f4fc7`](https://github.com/simple-robot/simpler-robot/commit/ac15f4fc7): 消除警告
- [`58e841585`](https://github.com/simple-robot/simpler-robot/commit/58e841585): 调整 MessageBuilder 内Image相关内容
- [`bb03e96f8`](https://github.com/simple-robot/simpler-robot/commit/bb03e96f8): 调整Image相关API
- [`3509da1ac`](https://github.com/simple-robot/simpler-robot/commit/3509da1ac): 调整优化ID相关API
- [`ab038e599`](https://github.com/simple-robot/simpler-robot/commit/ab038e599): 移除UserStatus及相关内容
- [`400fe2d76`](https://github.com/simple-robot/simpler-robot/commit/400fe2d76): Role API
- [`255b06e55`](https://github.com/simple-robot/simpler-robot/commit/255b06e55): 核心版本更新到 preview.15.0
- [`5715f3998`](https://github.com/simple-robot/simpler-robot/commit/5715f3998): 更新README
- [`01d85b87e`](https://github.com/simple-robot/simpler-robot/commit/01d85b87e): 准备发布 v3.0.0.preview.14.0
- [`6eeb18d55`](https://github.com/simple-robot/simpler-robot/commit/6eeb18d55): 优化描述
- [`21a50cced`](https://github.com/simple-robot/simpler-robot/commit/21a50cced): 更新优化ID相关内容
- [`336be3db2`](https://github.com/simple-robot/simpler-robot/commit/336be3db2): README.md 更新
- [`1db756b13`](https://github.com/simple-robot/simpler-robot/commit/1db756b13): 补充注释
- [`cc9c47bc6`](https://github.com/simple-robot/simpler-robot/commit/cc9c47bc6): 为Items实现Stream相关API
- [`2a4357e17`](https://github.com/simple-robot/simpler-robot/commit/2a4357e17): 为Items准备Stream相关API
- [`798f1b409`](https://github.com/simple-robot/simpler-robot/commit/798f1b409): 为Items提供Sequence相关API
- [`43cd88f88`](https://github.com/simple-robot/simpler-robot/commit/43cd88f88): 将 UserStatus 标记为 '实验性' 并待议。
- [`2ad234da5`](https://github.com/simple-robot/simpler-robot/commit/2ad234da5): 简单调整
- [`9eea7093e`](https://github.com/simple-robot/simpler-robot/commit/9eea7093e): 优化补充注释信息
- [`d73ad4c4f`](https://github.com/simple-robot/simpler-robot/commit/d73ad4c4f): 改善优化持续会话相关API、提供部分扩展
- [`6723f07b0`](https://github.com/simple-robot/simpler-robot/commit/6723f07b0): 调整优化 `SimbootApp` 部分API
- [`8fa9d80cc`](https://github.com/simple-robot/simpler-robot/commit/8fa9d80cc): `Member` 实现 `Contact`
- [`ebd1af3bf`](https://github.com/simple-robot/simpler-robot/commit/ebd1af3bf): 补充注释
- [`3923d0f38`](https://github.com/simple-robot/simpler-robot/commit/3923d0f38): 调整 `DeleteSupport` 的实现
- [`09224096f`](https://github.com/simple-robot/simpler-robot/commit/09224096f): 重构 `OrganizationBot` 及其子类
- [`2f8e0af14`](https://github.com/simple-robot/simpler-robot/commit/2f8e0af14): update readme
- [`2fc3e6571`](https://github.com/simple-robot/simpler-robot/commit/2fc3e6571): Create CONTRIBUTING.md
- [`405aa9e86`](https://github.com/simple-robot/simpler-robot/commit/405aa9e86): Create CODE_OF_CONDUCT.md
- [`ba714b791`](https://github.com/simple-robot/simpler-robot/commit/ba714b791): `Objectives` 重命名为 `Objective`
- [`0258bdcdf`](https://github.com/simple-robot/simpler-robot/commit/0258bdcdf): 调整Bot社交关系容器的实现；补充注释；
- [`5e744ee81..2b15434c1`](https://github.com/simple-robot/simpler-robot/compare/5e744ee81..0258bdcdf): Update question.yml
- [`98e178bc8`](https://github.com/simple-robot/simpler-robot/commit/98e178bc8): Update bug-report.yml
- [`909402ee3`](https://github.com/simple-robot/simpler-robot/commit/909402ee3): 对 Timestamp 的API进行调整改造
- [`c8a83dad7`](https://github.com/simple-robot/simpler-robot/commit/c8a83dad7): 对 Timestamp 的调整
- [`1e939d47c`](https://github.com/simple-robot/simpler-robot/commit/1e939d47c): preview.14.x: Bot.contact api
- [`07e4639c9`](https://github.com/simple-robot/simpler-robot/commit/07e4639c9): 调整部署任务流程
- [`aa1f210fd..43ee27aef`](https://github.com/simple-robot/simpler-robot/compare/aa1f210fd..07e4639c9): 更新 v3.0.0.preview.13.0
- [`14b0e1831..a976859cb`](https://github.com/simple-robot/simpler-robot/compare/14b0e1831..43ee27aef): 调整 UserStatus 相关API
- [`d107608b7`](https://github.com/simple-robot/simpler-robot/commit/d107608b7): 算了，没有必要
- [`df88e3ba5`](https://github.com/simple-robot/simpler-robot/commit/df88e3ba5): ListenerGenerator 扩展
- [`88997d6f4`](https://github.com/simple-robot/simpler-robot/commit/88997d6f4): 更新优化 Items API
- [`3dc3e510b`](https://github.com/simple-robot/simpler-robot/commit/3dc3e510b): 更新到 preview.13.0
- [`15ce30ae1..3091b0277`](https://github.com/simple-robot/simpler-robot/compare/15ce30ae1..3dc3e510b): Update timeout-issue.yml
- [`ac9d3270e`](https://github.com/simple-robot/simpler-robot/commit/ac9d3270e): 更新到 preview.12.1
- [`1a73e730c`](https://github.com/simple-robot/simpler-robot/commit/1a73e730c): 准备发布版本
- [`126f7c29a`](https://github.com/simple-robot/simpler-robot/commit/126f7c29a): 构建函数
- [`2be3bc581`](https://github.com/simple-robot/simpler-robot/commit/2be3bc581): 转化函数
- [`db423be6b..927a00597`](https://github.com/simple-robot/simpler-robot/compare/db423be6b..2be3bc581): 增加构建Items的扩展函数
- [`74a63d58b..8462aeffc`](https://github.com/simple-robot/simpler-robot/compare/74a63d58b..927a00597): 重构实现Items取代Flow或其他序列API
- [`d606b5279`](https://github.com/simple-robot/simpler-robot/commit/d606b5279): preview.12
- [`83a9d1036`](https://github.com/simple-robot/simpler-robot/commit/83a9d1036): 更新快照规则
- [`54517dd06..804fe8b0b`](https://github.com/simple-robot/simpler-robot/compare/54517dd06..83a9d1036): 发布 v3.0.0.preview.11.1
- [`ab0c5b032`](https://github.com/simple-robot/simpler-robot/commit/ab0c5b032): 修复配置
- [`a119fe6dd`](https://github.com/simple-robot/simpler-robot/commit/a119fe6dd): 补充注释
- [`53477c2e7`](https://github.com/simple-robot/simpler-robot/commit/53477c2e7): 修复CI配置
- [`cb9d30c13`](https://github.com/simple-robot/simpler-robot/commit/cb9d30c13): 尝试修复 #310; 补充注释
- [`425392b0a`](https://github.com/simple-robot/simpler-robot/commit/425392b0a): Update timeout-issue.yml
- [`8915f09c6`](https://github.com/simple-robot/simpler-robot/commit/8915f09c6): 调整/补充描述
- [`2b73413e6..3850fda36`](https://github.com/simple-robot/simpler-robot/compare/2b73413e6..8915f09c6): Update timeout-issue.yml
- [`196ef9418`](https://github.com/simple-robot/simpler-robot/commit/196ef9418): 预更新版本到pre.11.1
- [`a441bff8d`](https://github.com/simple-robot/simpler-robot/commit/a441bff8d): 更新 v3.0.0.preview.11.0
- [`00a25bbca`](https://github.com/simple-robot/simpler-robot/commit/00a25bbca): 补充、调整注释
- [`a67664ad4`](https://github.com/simple-robot/simpler-robot/commit/a67664ad4): Update issue-waiting-report.yml
- [`31112ab69`](https://github.com/simple-robot/simpler-robot/commit/31112ab69): 统一 send、reply、react 相关api的返回值，使他们都为 MessageReceipt 类型。
- [`bb70ba08e`](https://github.com/simple-robot/simpler-robot/commit/bb70ba08e): 补充调整注释描述
- [`5b5bfdac1`](https://github.com/simple-robot/simpler-robot/commit/5b5bfdac1): 补充调整注释
- [`6a446f13f`](https://github.com/simple-robot/simpler-robot/commit/6a446f13f): 补充注释
- [`a2cdddf92..34126f260`](https://github.com/simple-robot/simpler-robot/compare/a2cdddf92..6a446f13f): 使 MessageEvent 默认实现 ReplySupport
- [`6c6f01f7f`](https://github.com/simple-robot/simpler-robot/commit/6c6f01f7f): Deprecated 'sendIfSupportBlocking' in contact
- [`ae47e8177`](https://github.com/simple-robot/simpler-robot/commit/ae47e8177): 调整 EventListenerProcessingContext.eventResult 内联函数位置到 EventListenersGenerator 处
- [`39705c8f3`](https://github.com/simple-robot/simpler-robot/commit/39705c8f3): feat(api): 事件构建: onMatch / async
- [`2c21f1394`](https://github.com/simple-robot/simpler-robot/commit/2c21f1394): 其他api？
- [`8823ad2cd`](https://github.com/simple-robot/simpler-robot/commit/8823ad2cd): 补充注释
- [`9f5a223ef`](https://github.com/simple-robot/simpler-robot/commit/9f5a223ef): 为 EventListenersGenerator 及其衍生提供更多实用api
- [`1ad06d162`](https://github.com/simple-robot/simpler-robot/commit/1ad06d162): fix(api): 修复EventListenersGenerator中match函数合并逻辑错误问题
- [`ea27aec5e..1d52d679d`](https://github.com/simple-robot/simpler-robot/compare/ea27aec5e..1ad06d162): fix(api): 调整 `Preparator` 相关名称为 `Preparer`
- [`b31294c1b`](https://github.com/simple-robot/simpler-robot/commit/b31294c1b): Update issue-waiting-report.yml
- [`ecfb5d1aa`](https://github.com/simple-robot/simpler-robot/commit/ecfb5d1aa): Update timeout-issue.yml
- [`78e671eef`](https://github.com/simple-robot/simpler-robot/commit/78e671eef): Create issue-handle.yml
- [`c2623c0dd`](https://github.com/simple-robot/simpler-robot/commit/c2623c0dd): 其他api？
- [`d3224ec01`](https://github.com/simple-robot/simpler-robot/commit/d3224ec01): 补充注释
- [`030ede8e2`](https://github.com/simple-robot/simpler-robot/commit/030ede8e2): 为 EventListenersGenerator 及其衍生提供更多实用api
- [`3dd158348`](https://github.com/simple-robot/simpler-robot/commit/3dd158348): 调整文件结构
- [`b8c481f17`](https://github.com/simple-robot/simpler-robot/commit/b8c481f17): 调整 BotMember 名称为 MemberBot。
- [`d493ab306`](https://github.com/simple-robot/simpler-robot/commit/d493ab306): 调整botMember相关属性值
- [`d0b1dbe32`](https://github.com/simple-robot/simpler-robot/commit/d0b1dbe32): 版本修改至pre.11.0
- [`7e38f1d74..d26bffeec`](https://github.com/simple-robot/simpler-robot/compare/7e38f1d74..d0b1dbe32): Update timeout-issue.yml
- [`ba5c36993`](https://github.com/simple-robot/simpler-robot/commit/ba5c36993): Create timeout-issue.yml
- [`3af0ac78a..a2e8c3a02`](https://github.com/simple-robot/simpler-robot/compare/3af0ac78a..ba5c36993): Update issue-waiting-report.yml
- [`3b0fa2cf3`](https://github.com/simple-robot/simpler-robot/commit/3b0fa2cf3): Create issue-waiting-report.yml
- [`76bc60909`](https://github.com/simple-robot/simpler-robot/commit/76bc60909): 提供 BotMember 类型实现
- [`550cf3e7e..9d6efdc78`](https://github.com/simple-robot/simpler-robot/compare/550cf3e7e..76bc60909): 完善mute api的描述
- [`8a8f0cad6`](https://github.com/simple-robot/simpler-robot/commit/8a8f0cad6): 调整禁言api的描述与约束
- [`ee79a0316`](https://github.com/simple-robot/simpler-robot/commit/ee79a0316): 版本修改至pre.11.0
- [`c9f9e7f32..22361c4ab`](https://github.com/simple-robot/simpler-robot/compare/c9f9e7f32..ee79a0316): Update bug-report.yml
- [`538dcc65a`](https://github.com/simple-robot/simpler-robot/commit/538dcc65a): Update question.yml
- [`c72596b89`](https://github.com/simple-robot/simpler-robot/commit/c72596b89): Create labeler.yml
- [`a33c4a967`](https://github.com/simple-robot/simpler-robot/commit/a33c4a967): Update advice.yml
- [`a097936e1`](https://github.com/simple-robot/simpler-robot/commit/a097936e1): Update show.yml
- [`37bd3c8c7`](https://github.com/simple-robot/simpler-robot/commit/37bd3c8c7): Update question.yml
- [`1a5e556c4..f67f33d94`](https://github.com/simple-robot/simpler-robot/compare/1a5e556c4..37bd3c8c7): Update bug-report.yml
- [`175c70eea`](https://github.com/simple-robot/simpler-robot/commit/175c70eea): Update question.yml
- [`e0829bec9`](https://github.com/simple-robot/simpler-robot/commit/e0829bec9): Update advice.yml
- [`8c0ea8e52..532174eeb`](https://github.com/simple-robot/simpler-robot/compare/8c0ea8e52..e0829bec9): Update question.yml
- [`d764c8fc3..1b0d8a0e1`](https://github.com/simple-robot/simpler-robot/compare/d764c8fc3..532174eeb): Update bug-report.yml
- [`0b0c404e2`](https://github.com/simple-robot/simpler-robot/commit/0b0c404e2): update README.md
- [`fb550e36f`](https://github.com/simple-robot/simpler-robot/commit/fb550e36f): 更新版本 v3.0.0.preview.10.2
- [`20890003c..453bd7918`](https://github.com/simple-robot/simpler-robot/compare/20890003c..fb550e36f): 补充注释
- [`78dfb4907`](https://github.com/simple-robot/simpler-robot/commit/78dfb4907): 为 ListenerPreparator 提供阻塞兼容；补充注释
- [`691800198`](https://github.com/simple-robot/simpler-robot/commit/691800198): 移除无用目录
- [`26b18e23a`](https://github.com/simple-robot/simpler-robot/commit/26b18e23a): 下一个版本
- [`568ae9a7a`](https://github.com/simple-robot/simpler-robot/commit/568ae9a7a): 更新 v3.0.0.preview.10.1
- [`617a73ec2`](https://github.com/simple-robot/simpler-robot/commit/617a73ec2): 尝试修复@FilterValue得不到对应attributes的问题
- [`e6c6dc5a2`](https://github.com/simple-robot/simpler-robot/commit/e6c6dc5a2): 阻塞函数增加 runWithInterruptible
- [`30c54a139`](https://github.com/simple-robot/simpler-robot/commit/30c54a139): CodeListener过时标记
- [`1543f2884`](https://github.com/simple-robot/simpler-robot/commit/1543f2884): SimpleListeners
- [`5a44a1995`](https://github.com/simple-robot/simpler-robot/commit/5a44a1995): 版本预先调整到 pre10.1
- [`9f052ceec`](https://github.com/simple-robot/simpler-robot/commit/9f052ceec): 更新版本 v3.0.0.preview.10.0
- [`e2483dc79`](https://github.com/simple-robot/simpler-robot/commit/e2483dc79): nextMessages -> nextMessage
- [`d89922adc`](https://github.com/simple-robot/simpler-robot/commit/d89922adc): session.nextMessages -> nextMessage
- [`b2b31555e`](https://github.com/simple-robot/simpler-robot/commit/b2b31555e): 优化ParameterBinder.Context部分API
- [`ae8b28f33`](https://github.com/simple-robot/simpler-robot/commit/ae8b28f33): SimpleListenerBuilder
- [`47e11355b`](https://github.com/simple-robot/simpler-robot/commit/47e11355b): 持续会话API优化
- [`1e3f9d0a1`](https://github.com/simple-robot/simpler-robot/commit/1e3f9d0a1): 拦截器扫描加载
- [`fb5f4d851`](https://github.com/simple-robot/simpler-robot/commit/fb5f4d851): 拦截器重建: point
- [`251ee2bb5`](https://github.com/simple-robot/simpler-robot/commit/251ee2bb5): interceptor for spring boot starter
- [`ef1714eca`](https://github.com/simple-robot/simpler-robot/commit/ef1714eca): 隐藏bug
- [`dc28a7b15`](https://github.com/simple-robot/simpler-robot/commit/dc28a7b15): 调整接口内抽象
- [`943c1ea1e`](https://github.com/simple-robot/simpler-robot/commit/943c1ea1e): 监听函数重构/监听准备器
- [`26e2f9899`](https://github.com/simple-robot/simpler-robot/commit/26e2f9899): Interceptor s
- [`8f1e877e4`](https://github.com/simple-robot/simpler-robot/commit/8f1e877e4): EventListenersGenerator
- [`8a62ea84d`](https://github.com/simple-robot/simpler-robot/commit/8a62ea84d): 监听函数构建器
- [`5b3d6db48`](https://github.com/simple-robot/simpler-robot/commit/5b3d6db48): 新的默认监听函数实现/matchable
- [`bd12ba30c`](https://github.com/simple-robot/simpler-robot/commit/bd12ba30c): 监听函数重构
- [`8115d3785`](https://github.com/simple-robot/simpler-robot/commit/8115d3785): 版本 to 10.0
- [`94ef89263`](https://github.com/simple-robot/simpler-robot/commit/94ef89263): matcher..?
- [`0b70e34a2`](https://github.com/simple-robot/simpler-robot/commit/0b70e34a2): 更新 v3.0.0.preview.9.1
- [`7f051dfac`](https://github.com/simple-robot/simpler-robot/commit/7f051dfac): 监听函数与拦截器调整：matchable
- [`2c8074f4a`](https://github.com/simple-robot/simpler-robot/commit/2c8074f4a): 拦截器: before Filter
- [`d6c2200c8`](https://github.com/simple-robot/simpler-robot/commit/d6c2200c8): fix(spring-boot-starter): 尝试修复动态代理目标类获取问题 #281
- [`ef71cbfed`](https://github.com/simple-robot/simpler-robot/commit/ef71cbfed): fix(spring-boot-starter): 尝试修复动态代理目标类获取问题
- [`6600aeabd`](https://github.com/simple-robot/simpler-robot/commit/6600aeabd): fix(spring-boot-starter): 尝试修复动态代理目标类获取问题&配置快照发布策略
- [`a866d7fe2`](https://github.com/simple-robot/simpler-robot/commit/a866d7fe2): fix(spring-boot-starter): 尝试修复动态代理目标类获取问题 #281
- [`39c04878d`](https://github.com/simple-robot/simpler-robot/commit/39c04878d): 删除无用文件
- [`33c19990a`](https://github.com/simple-robot/simpler-robot/commit/33c19990a): readme更新
- [`ffd0eadba`](https://github.com/simple-robot/simpler-robot/commit/ffd0eadba): feat(api): BotSocialRelationsContainer
- [`6e6d91af0`](https://github.com/simple-robot/simpler-robot/commit/6e6d91af0): refactor(api): 移除 BlockingClearTargetResumeListener
- [`54cacff86`](https://github.com/simple-robot/simpler-robot/commit/54cacff86): feat(api): 持续会话API
- [`1526fbb00`](https://github.com/simple-robot/simpler-robot/commit/1526fbb00): refactor(spring-boot-starter): 简单调整
- [`4e48cb099`](https://github.com/simple-robot/simpler-robot/commit/4e48cb099): runInBlocking相关
- [`7700c8463..0f7db353a`](https://github.com/simple-robot/simpler-robot/compare/7700c8463..4e48cb099): 持续会话API #238
- [`22972503a..61e67b746`](https://github.com/simple-robot/simpler-robot/compare/22972503a..0f7db353a): BlockingRunner 调整
- [`108244d1a`](https://github.com/simple-robot/simpler-robot/commit/108244d1a): 标记待办
- [`215241a37`](https://github.com/simple-robot/simpler-robot/commit/215241a37): fix delete if support
- [`05d08d2fb`](https://github.com/simple-robot/simpler-robot/commit/05d08d2fb): 优化日志
- [`e38e53442`](https://github.com/simple-robot/simpler-robot/commit/e38e53442): fix filter
- [`502d26ac9`](https://github.com/simple-robot/simpler-robot/commit/502d26ac9): session context 4j return type
- [`b8fb6cc17`](https://github.com/simple-robot/simpler-robot/commit/b8fb6cc17): session context 4j
- [`e4cc28ec4`](https://github.com/simple-robot/simpler-robot/commit/e4cc28ec4): class loader.
- [`b573ca006`](https://github.com/simple-robot/simpler-robot/commit/b573ca006): The Boot classloader configuration
- [`a3256a818`](https://github.com/simple-robot/simpler-robot/commit/a3256a818): Boot模块的自动扫描
- [`03eef71d6`](https://github.com/simple-robot/simpler-robot/commit/03eef71d6): The build src
- [`875b29805`](https://github.com/simple-robot/simpler-robot/commit/875b29805): Remove a submodule.
- [`73387f222`](https://github.com/simple-robot/simpler-robot/commit/73387f222): The build src
- [`26dde0880`](https://github.com/simple-robot/simpler-robot/commit/26dde0880): Remove a submodule
- [`19350a64c`](https://github.com/simple-robot/simpler-robot/commit/19350a64c): Session Context snap
- [`c575e4e7b`](https://github.com/simple-robot/simpler-robot/commit/c575e4e7b): snapshot for other branches
- [`785009adc`](https://github.com/simple-robot/simpler-robot/commit/785009adc): preview.10.0
- [`6553865b8`](https://github.com/simple-robot/simpler-robot/commit/6553865b8): 修改readme和changelog
- [`70a37e6cf`](https://github.com/simple-robot/simpler-robot/commit/70a37e6cf): 兼容性过时标记
- [`1975c129f`](https://github.com/simple-robot/simpler-robot/commit/1975c129f): 修复auto register bot重复启动的问题
- [`28e5badb9`](https://github.com/simple-robot/simpler-robot/commit/28e5badb9): Boot about
- [`39ef6740b`](https://github.com/simple-robot/simpler-robot/commit/39ef6740b): changelog
- [`732cfa7ed..4723686be`](https://github.com/simple-robot/simpler-robot/compare/732cfa7ed..39ef6740b): 调整部分内部实现
- [`c6e279078`](https://github.com/simple-robot/simpler-robot/commit/c6e279078): 调整注释、调整部分实现
- [`2a206178d`](https://github.com/simple-robot/simpler-robot/commit/2a206178d): 简单调整
- [`743f3fce8`](https://github.com/simple-robot/simpler-robot/commit/743f3fce8): 调整构建listener时的 handle 函数，将 context 作为接收者；修复部分内容
- [`e493da9a7`](https://github.com/simple-robot/simpler-robot/commit/e493da9a7): 简单更新
- [`7ed19476e`](https://github.com/simple-robot/simpler-robot/commit/7ed19476e): 简单优化事件注册器
- [`8f321c54d`](https://github.com/simple-robot/simpler-robot/commit/8f321c54d): fix install bug
- [`106a782b6`](https://github.com/simple-robot/simpler-robot/commit/106a782b6): application内异步改造、简单调整、补充注释
- [`b65bcb54d`](https://github.com/simple-robot/simpler-robot/commit/b65bcb54d): 应用程序内部启动全异步改造
- [`b43cf2ab8`](https://github.com/simple-robot/simpler-robot/commit/b43cf2ab8): Application Launcher
- [`f6b75df0d`](https://github.com/simple-robot/simpler-robot/commit/f6b75df0d): Fix SpreadOperator
- [`0ce7427d5`](https://github.com/simple-robot/simpler-robot/commit/0ce7427d5): the version to pre.9.0
- [`09fff5280`](https://github.com/simple-robot/simpler-robot/commit/09fff5280): ItemFlow
- [`4c3480fc5..f7a3ceb9f`](https://github.com/simple-robot/simpler-robot/compare/4c3480fc5..09fff5280): annotation event filter
- [`1f63453f4`](https://github.com/simple-robot/simpler-robot/commit/1f63453f4): Item Flow
- [`cab1a6111`](https://github.com/simple-robot/simpler-robot/commit/cab1a6111): BaseSequence
- [`7cf30077f`](https://github.com/simple-robot/simpler-robot/commit/7cf30077f): Item Sequence and Item Flow
- [`3a436efa0`](https://github.com/simple-robot/simpler-robot/commit/3a436efa0): The Spring Boot App
- [`f6f41aec6`](https://github.com/simple-robot/simpler-robot/commit/f6f41aec6): Boot相关模块下为listener提供部分原始属性支持
- [`08be90751`](https://github.com/simple-robot/simpler-robot/commit/08be90751): SpringBoot app
- [`2a20fb2f2`](https://github.com/simple-robot/simpler-robot/commit/2a20fb2f2): Binder For MessageValue
- [`8bf909bf3`](https://github.com/simple-robot/simpler-robot/commit/8bf909bf3): 挖坑
- [`0e0d537bb`](https://github.com/simple-robot/simpler-robot/commit/0e0d537bb): The Binder
- [`4f6496513`](https://github.com/simple-robot/simpler-robot/commit/4f6496513): The Boot application.
- [`b14fff70c`](https://github.com/simple-robot/simpler-robot/commit/b14fff70c): The Boot
- [`977ada715..1dd04bcdb`](https://github.com/simple-robot/simpler-robot/compare/977ada715..b14fff70c): 阶段性更新
- [`8fd0366a1`](https://github.com/simple-robot/simpler-robot/commit/8fd0366a1): Message extra
- [`e46147a9c`](https://github.com/simple-robot/simpler-robot/commit/e46147a9c): The Binder
- [`97173f2ab`](https://github.com/simple-robot/simpler-robot/commit/97173f2ab): 消息序列化相关
- [`b234042dc`](https://github.com/simple-robot/simpler-robot/commit/b234042dc): Boot Application
- [`c8858a879`](https://github.com/simple-robot/simpler-robot/commit/c8858a879): Create config.yml
- [`d0a1d1a79`](https://github.com/simple-robot/simpler-robot/commit/d0a1d1a79): Boot, and Spring Boot
- [`bf28cac14`](https://github.com/simple-robot/simpler-robot/commit/bf28cac14): For Spring boot and ..
- [`b5b5026e2`](https://github.com/simple-robot/simpler-robot/commit/b5b5026e2): build listener manager
- [`0e2c15742`](https://github.com/simple-robot/simpler-robot/commit/0e2c15742): 调整 `EventListenerManager` 的定义，包括Scope、Context等。
- [`c91b1507c..8ba3b6792`](https://github.com/simple-robot/simpler-robot/compare/c91b1507c..0e2c15742): 为 `Boot` 提供 Application 实现。
- [`fe7f24059`](https://github.com/simple-robot/simpler-robot/commit/fe7f24059): 文件内简单调整
- [`f23a1e402`](https://github.com/simple-robot/simpler-robot/commit/f23a1e402): Builder and interface
- [`33b9a8336`](https://github.com/simple-robot/simpler-robot/commit/33b9a8336): 与Job相关的简单调整
- [`2db193c9a`](https://github.com/simple-robot/simpler-robot/commit/2db193c9a): 增加 Completable 接口并使 `ApplicationBuilder` 实现以提供完成回调；  `ApplicationBuilder.install(...)` 中的 `configurator` 函数增加一个函数参数 `perceivable: CompletionPerceivable<A>` 来允许注册配置的时候额外注册回调函数。
- [`54e9e4f04`](https://github.com/simple-robot/simpler-robot/commit/54e9e4f04): Application.Environment中提供序列化模块相关api
- [`5b505f4d5..69bcb4511`](https://github.com/simple-robot/simpler-robot/compare/5b505f4d5..54e9e4f04): 从 `simboot-api` 中排除 `simbot-logger`
- [`a84666ffd..b965604b7`](https://github.com/simple-robot/simpler-robot/compare/a84666ffd..69bcb4511): 只在 `simbot-core`、`simboot-core` 中传递使用 `simbot-logger`
- [`e172f029e`](https://github.com/simple-robot/simpler-robot/commit/e172f029e): 在 spring boot starter 中默认使用 spring-boot-starter-logging
- [`aafa248ae`](https://github.com/simple-robot/simpler-robot/commit/aafa248ae): 更新kotlin到1.6.10
- [`8f6a4c9dc`](https://github.com/simple-robot/simpler-robot/commit/8f6a4c9dc): 清理未使用的版本信息; 更新部署脚本配置
- [`5140e40e0`](https://github.com/simple-robot/simpler-robot/commit/5140e40e0): 更新ktx.coroutines版本到1.6.1
- [`2f649df8b`](https://github.com/simple-robot/simpler-robot/commit/2f649df8b): 清理test
- [`bc69e457b`](https://github.com/simple-robot/simpler-robot/commit/bc69e457b): 版本发布 v3.0.0.preview.8.0
- [`bccce90c0`](https://github.com/simple-robot/simpler-robot/commit/bccce90c0): 变更调用
- [`6de353ad5..0395af0fb`](https://github.com/simple-robot/simpler-robot/compare/6de353ad5..bccce90c0): 提供 `Resource` 的子类型 `DeserializableResource` 并将其与 `BotVerifyInfo` 结合。
- [`f5cc86950`](https://github.com/simple-robot/simpler-robot/commit/f5cc86950): 调整 Resource 的部分API
- [`51e7fd052`](https://github.com/simple-robot/simpler-robot/commit/51e7fd052): Update advice.yml
- [`7fa6fbe58`](https://github.com/simple-robot/simpler-robot/commit/7fa6fbe58): Update show.yml
- [`498c7eae9`](https://github.com/simple-robot/simpler-robot/commit/498c7eae9): Update question.yml
- [`10b8d1c8a..20ab5e523`](https://github.com/simple-robot/simpler-robot/compare/10b8d1c8a..498c7eae9): Update bug-report.yml
- [`740bd1951`](https://github.com/simple-robot/simpler-robot/commit/740bd1951): 重写 BotVerifyInfo
- [`da9cd54a5`](https://github.com/simple-robot/simpler-robot/commit/da9cd54a5): 调整 `BotManager` 和 `OriginBotManager` 相关内容。
- [`f297846a9`](https://github.com/simple-robot/simpler-robot/commit/f297846a9): 变更 BotManager api: all() 返回值变更为 List<Bot>
- [`6d35c77bc`](https://github.com/simple-robot/simpler-robot/commit/6d35c77bc): 更新.ignore
- [`73ba592c7`](https://github.com/simple-robot/simpler-robot/commit/73ba592c7): SupportedBotVerificationType
- [`d5d6df95c`](https://github.com/simple-robot/simpler-robot/commit/d5d6df95c): update changelog
- [`5e83d6e68`](https://github.com/simple-robot/simpler-robot/commit/5e83d6e68): feat(implement SendSupport): 为Member、Friend等实现 SendSupport 接口
- [`22be7b9bf`](https://github.com/simple-robot/simpler-robot/commit/22be7b9bf): fix(Image.resource提供blocking api)
- [`b6a0dff4e`](https://github.com/simple-robot/simpler-robot/commit/b6a0dff4e): Request相关事件定义调整
- [`867234da5..6a6d5deb5`](https://github.com/simple-robot/simpler-robot/compare/867234da5..b6a0dff4e): Changelog
- [`dd46ba6c8`](https://github.com/simple-robot/simpler-robot/commit/dd46ba6c8): Event、MessageEvent部分内容定义调整，去除默认实现
- [`c841a499d`](https://github.com/simple-robot/simpler-robot/commit/c841a499d): 变更事件的重新定义
- [`480bafec8`](https://github.com/simple-robot/simpler-robot/commit/480bafec8): 准备更新日志
- [`ddaf3eacc`](https://github.com/simple-robot/simpler-robot/commit/ddaf3eacc): 更新版本到pre.8.0
- [`da1d99601`](https://github.com/simple-robot/simpler-robot/commit/da1d99601): Simple Application dsl api
- [`59e857395`](https://github.com/simple-robot/simpler-robot/commit/59e857395): Update advice.yml
- [`5e876c85d..d4c8a2df0`](https://github.com/simple-robot/simpler-robot/compare/5e876c85d..59e857395): Application Factory实现; 核心 Simple 工厂; fix some
- [`02620d709`](https://github.com/simple-robot/simpler-robot/commit/02620d709): Application Factory实现; 核心 Simple 工厂;
- [`0db62b995`](https://github.com/simple-robot/simpler-robot/commit/0db62b995): Application Factory
- [`e1690957e`](https://github.com/simple-robot/simpler-robot/commit/e1690957e): Application...?
- [`b1e956d2d`](https://github.com/simple-robot/simpler-robot/commit/b1e956d2d): 调整注释
- [`a37c019cc`](https://github.com/simple-robot/simpler-robot/commit/a37c019cc): clean import
- [`f74689ea1..298944e1d`](https://github.com/simple-robot/simpler-robot/compare/f74689ea1..a37c019cc): 调整API
- [`c68f9b517`](https://github.com/simple-robot/simpler-robot/commit/c68f9b517): 调整注释
- [`f3ace62c6`](https://github.com/simple-robot/simpler-robot/commit/f3ace62c6): Application steps
- [`0e4930b87`](https://github.com/simple-robot/simpler-robot/commit/0e4930b87): update ci config
- [`112a44ff8`](https://github.com/simple-robot/simpler-robot/commit/112a44ff8): 为 SingleOnlyMessage 提供toString函数实现约束
- [`1aead8265`](https://github.com/simple-robot/simpler-robot/commit/1aead8265): 更新CI配置
- [`604b6c7af`](https://github.com/simple-robot/simpler-robot/commit/604b6c7af): :rewind: 恢复版本号
- [`65fbecd28`](https://github.com/simple-robot/simpler-robot/commit/65fbecd28): 更新配置
- [`ba74a4411`](https://github.com/simple-robot/simpler-robot/commit/ba74a4411): logger common
- [`30a8ae919`](https://github.com/simple-robot/simpler-robot/commit/30a8ae919): simple test
- [`660f167cc..06a6bee96`](https://github.com/simple-robot/simpler-robot/compare/660f167cc..30a8ae919): 临时回退版本以发布快照
- [`220885234`](https://github.com/simple-robot/simpler-robot/commit/220885234): 准备BaseEvent
- [`f0504881f`](https://github.com/simple-robot/simpler-robot/commit/f0504881f): 调整配置
- [`9af614963`](https://github.com/simple-robot/simpler-robot/commit/9af614963): 调整 changelog 生成规则
- [`22861752a`](https://github.com/simple-robot/simpler-robot/commit/22861752a): 新的 changelog 生成规则
- [`ffc675e02`](https://github.com/simple-robot/simpler-robot/commit/ffc675e02): update copyright and fix
- [`65ca3986d`](https://github.com/simple-robot/simpler-robot/commit/65ca3986d): update copyright;
- [`c716688e3`](https://github.com/simple-robot/simpler-robot/commit/c716688e3): NEXT VERSION
- [`cb396e9e0`](https://github.com/simple-robot/simpler-robot/commit/cb396e9e0): Spring boot version update
- [`25dc23499`](https://github.com/simple-robot/simpler-robot/commit/25dc23499): remove some test file
- [`f46d84a59`](https://github.com/simple-robot/simpler-robot/commit/f46d84a59): 版本发布
- [`95589cc7f`](https://github.com/simple-robot/simpler-robot/commit/95589cc7f): 更新版本 pre.7.0
- [`d8baf2ece`](https://github.com/simple-robot/simpler-robot/commit/d8baf2ece): boot api提供 ComponentRegistryConfigure 抽象配置类以实现自定义组件注册
- [`e40dedc04`](https://github.com/simple-robot/simpler-robot/commit/e40dedc04): 优化Messages下相关内容效果，例如toString等。
- [`ccd470388`](https://github.com/simple-robot/simpler-robot/commit/ccd470388): ID and test
- [`f5e8e9b44..f11a2fb00`](https://github.com/simple-robot/simpler-robot/compare/f5e8e9b44..ccd470388): ID
- [`329de6e1f`](https://github.com/simple-robot/simpler-robot/commit/329de6e1f): Bonus!
- [`b2559c244`](https://github.com/simple-robot/simpler-robot/commit/b2559c244): 更新版本
- [`ba45623b9`](https://github.com/simple-robot/simpler-robot/commit/ba45623b9): 更新changelog模板
- [`61198a0dc`](https://github.com/simple-robot/simpler-robot/commit/61198a0dc): :bookmark: 发布版本 `v3.0.0.preview.6.0`
- [`e3e82d554..72c63e29b`](https://github.com/simple-robot/simpler-robot/compare/e3e82d554..61198a0dc): 新的版本定义方式
- [`205299bf9`](https://github.com/simple-robot/simpler-robot/commit/205299bf9): 范型调整
- [`f7a7c660c`](https://github.com/simple-robot/simpler-robot/commit/f7a7c660c): 范型fix
- [`961742ef7`](https://github.com/simple-robot/simpler-robot/commit/961742ef7): 版本to6.0
- [`72e41dcce`](https://github.com/simple-robot/simpler-robot/commit/72e41dcce): 调整event范型定义
- [`ce46e38ce..605c24c26`](https://github.com/simple-robot/simpler-robot/compare/ce46e38ce..72e41dcce): Update README.md
- [`7d6814b4a`](https://github.com/simple-robot/simpler-robot/commit/7d6814b4a): 标识待办
- [`c95be246c`](https://github.com/simple-robot/simpler-robot/commit/c95be246c): Update to v3.0.0.preview.5.0
- [`7679f2182`](https://github.com/simple-robot/simpler-robot/commit/7679f2182): 优化日志
- [`1b8de500f`](https://github.com/simple-robot/simpler-robot/commit/1b8de500f): boot-core自动加载所有可加载组件（`installAll`）
- [`9595de83f`](https://github.com/simple-robot/simpler-robot/commit/9595de83f): 补充注释
- [`f83aeea7c`](https://github.com/simple-robot/simpler-robot/commit/f83aeea7c): 为 MuteSupport.mute 的参数提供默认值
- [`8e804e038..635d81a46`](https://github.com/simple-robot/simpler-robot/compare/8e804e038..f83aeea7c): Update README.md
- [`d3bd38336`](https://github.com/simple-robot/simpler-robot/commit/d3bd38336): some test
- [`09c2afd3a`](https://github.com/simple-robot/simpler-robot/commit/09c2afd3a): component
- [`98ce57073..17c4bde06`](https://github.com/simple-robot/simpler-robot/compare/98ce57073..09c2afd3a): bugfix
- [`dbfc6d2a0..5be8c9557`](https://github.com/simple-robot/simpler-robot/compare/dbfc6d2a0..17c4bde06): 组件中的序列化器
- [`e62ad09fc`](https://github.com/simple-robot/simpler-robot/commit/e62ad09fc): 文件名调整
- [`2fc5d93e3`](https://github.com/simple-robot/simpler-robot/commit/2fc5d93e3): 调整组件机制
- [`89b4d64af`](https://github.com/simple-robot/simpler-robot/commit/89b4d64af): 移除Message的ComponentContainer实现，并调整 Message.Key 的定义
- [`06bbecb85`](https://github.com/simple-robot/simpler-robot/commit/06bbecb85): Component Registrar and listener manager install()
- [`333752e2b`](https://github.com/simple-robot/simpler-robot/commit/333752e2b): update readme
- [`94659b921`](https://github.com/simple-robot/simpler-robot/commit/94659b921): Bot.isMe api
- [`2310eab64`](https://github.com/simple-robot/simpler-robot/commit/2310eab64): 补充部分注释
- [`380c1ca64`](https://github.com/simple-robot/simpler-robot/commit/380c1ca64): version to pre.5.0
- [`b65e58c1c`](https://github.com/simple-robot/simpler-robot/commit/b65e58c1c): 简单调整部分shared operato和
- [`c042503a8`](https://github.com/simple-robot/simpler-robot/commit/c042503a8): 补充注释与单元测试
- [`031079119`](https://github.com/simple-robot/simpler-robot/commit/031079119): configuration中的监听函数构建器
- [`181e10c7f..a51afcd92`](https://github.com/simple-robot/simpler-robot/compare/181e10c7f..031079119): 监听函数构建器，在configuration中
- [`a9c0f3079`](https://github.com/simple-robot/simpler-robot/commit/a9c0f3079): 补充大量注释; 调整核心事件管理器的配置类为可链式的
- [`4f334963e`](https://github.com/simple-robot/simpler-robot/commit/4f334963e): EventListenerRegistrar 说明
- [`65c467d6b`](https://github.com/simple-robot/simpler-robot/commit/65c467d6b): fix submodule head
- [`3236f6405`](https://github.com/simple-robot/simpler-robot/commit/3236f6405): update submodule config
- [`13e23e395`](https://github.com/simple-robot/simpler-robot/commit/13e23e395): just update
- [`bbf75d8c1`](https://github.com/simple-robot/simpler-robot/commit/bbf75d8c1): 清理部分遗留输出斌修复部分错误
- [`fa796b8e7`](https://github.com/simple-robot/simpler-robot/commit/fa796b8e7): 优化reactive相关api无法被处理的警告信息
- [`3318f8fbb`](https://github.com/simple-robot/simpler-robot/commit/3318f8fbb): 调整actions以支持submodule
- [`04c8395db`](https://github.com/simple-robot/simpler-robot/commit/04c8395db): remove buildSrc2
- [`2c6e419c8`](https://github.com/simple-robot/simpler-robot/commit/2c6e419c8): config git submodules
- [`b078bcc5d`](https://github.com/simple-robot/simpler-robot/commit/b078bcc5d): buildSrc submodule try fix
- [`1b18b12a8`](https://github.com/simple-robot/simpler-robot/commit/1b18b12a8): delete submodule
- [`3339baa3b`](https://github.com/simple-robot/simpler-robot/commit/3339baa3b): rename buildSrc for fix git submodule
- [`dbf123dd8`](https://github.com/simple-robot/simpler-robot/commit/dbf123dd8): remove错误提交的文件夹
- [`f1a2396bd`](https://github.com/simple-robot/simpler-robot/commit/f1a2396bd): buildSrc 子模块
- [`d94369bc0`](https://github.com/simple-robot/simpler-robot/commit/d94369bc0): About buildSrc
- [`844e30c63`](https://github.com/simple-robot/simpler-robot/commit/844e30c63): 简单优化对于核心boot模块下的类扫描提示
- [`229fce815`](https://github.com/simple-robot/simpler-robot/commit/229fce815): Version util
- [`46a50f38e..80c0b63f1`](https://github.com/simple-robot/simpler-robot/compare/46a50f38e..229fce815): issue模板
- [`abf3eaac0`](https://github.com/simple-robot/simpler-robot/commit/abf3eaac0): 恢复配置
- [`98d4b8552`](https://github.com/simple-robot/simpler-robot/commit/98d4b8552): kdoc
- [`2d6a582cf`](https://github.com/simple-robot/simpler-robot/commit/2d6a582cf): kdoc publish
- [`3202305ef`](https://github.com/simple-robot/simpler-robot/commit/3202305ef): :wrench: 修改配置文件
- [`766e8ff67..bc07df22b`](https://github.com/simple-robot/simpler-robot/compare/766e8ff67..3202305ef): 补充注释与说明
- [`d572c555a`](https://github.com/simple-robot/simpler-robot/commit/d572c555a): 清理代码
- [`3a6959754`](https://github.com/simple-robot/simpler-robot/commit/3a6959754): 优化展示
- [`49a1f6aea`](https://github.com/simple-robot/simpler-robot/commit/49a1f6aea): default logger use
- [`3fa4c5581`](https://github.com/simple-robot/simpler-robot/commit/3fa4c5581): Simbot Logger
- [`84c687c53`](https://github.com/simple-robot/simpler-robot/commit/84c687c53): Logger
- [`c62fdff7f`](https://github.com/simple-robot/simpler-robot/commit/c62fdff7f): 优化警告日志
- [`e26e7eaf3`](https://github.com/simple-robot/simpler-robot/commit/e26e7eaf3): 调整警告日志
- [`62ac24d6a`](https://github.com/simple-robot/simpler-robot/commit/62ac24d6a): reactive api support
- [`16ee681b8..d120f29cd`](https://github.com/simple-robot/simpler-robot/compare/16ee681b8..62ac24d6a): reactive api
- [`bc56a4658`](https://github.com/simple-robot/simpler-robot/commit/bc56a4658): Logger
- [`d32ad7200..83d8e16a3`](https://github.com/simple-robot/simpler-robot/compare/d32ad7200..bc56a4658): 更新配置
- [`26c967629`](https://github.com/simple-robot/simpler-robot/commit/26c967629): 简单细化spring-boot-starter部分配置类
- [`75b61a0e8`](https://github.com/simple-robot/simpler-robot/commit/75b61a0e8): CoreListenerManager中的事件处理不在使用bot的context
- [`d6522d63d`](https://github.com/simple-robot/simpler-robot/commit/d6522d63d): 增加文档配置
- [`4eeb558f6..6a0b4f0dd`](https://github.com/simple-robot/simpler-robot/compare/4eeb558f6..d6522d63d): 更新readme
- [`fb2dd4f27`](https://github.com/simple-robot/simpler-robot/commit/fb2dd4f27): update config
- [`f73bcf6ae`](https://github.com/simple-robot/simpler-robot/commit/f73bcf6ae): update to pre.3.1
- [`d6db3901c`](https://github.com/simple-robot/simpler-robot/commit/d6db3901c): update README
- [`2f38df142`](https://github.com/simple-robot/simpler-robot/commit/2f38df142): 更新changelog配置
- [`a4fa0355c`](https://github.com/simple-robot/simpler-robot/commit/a4fa0355c): 更新配置
- [`2a76c1e44`](https://github.com/simple-robot/simpler-robot/commit/2a76c1e44): Release for v3.0.0.preview.3.0
- [`3a7f834dc`](https://github.com/simple-robot/simpler-robot/commit/3a7f834dc): 补充注释
- [`7ff0674a0`](https://github.com/simple-robot/simpler-robot/commit/7ff0674a0): MessagesBuilder
- [`07a58f0b4`](https://github.com/simple-robot/simpler-robot/commit/07a58f0b4): 简单调整publish配置
- [`05bafd19c`](https://github.com/simple-robot/simpler-robot/commit/05bafd19c): 序列化&Test
- [`37226935e`](https://github.com/simple-robot/simpler-robot/commit/37226935e): 为 Messages 提供java平台的序列化api
- [`8d5372964`](https://github.com/simple-robot/simpler-robot/commit/8d5372964): 为 Messages 的实现提供 toString 和 equals
- [`8144e3faf`](https://github.com/simple-robot/simpler-robot/commit/8144e3faf): 补充 Limiter 注释
- [`1d604f2c0`](https://github.com/simple-robot/simpler-robot/commit/1d604f2c0): Messages 注释补充，toString 补充
- [`88d769681..823114932`](https://github.com/simple-robot/simpler-robot/compare/88d769681..1d604f2c0): 更新配置
- [`35879a688`](https://github.com/simple-robot/simpler-robot/commit/35879a688): 更新快照更新配置
- [`0033c456e`](https://github.com/simple-robot/simpler-robot/commit/0033c456e): 调整规则 - 修改了源码才发布
- [`42c60ef9c`](https://github.com/simple-robot/simpler-robot/commit/42c60ef9c): 自动更新快照脚本
- [`c5167a9b9`](https://github.com/simple-robot/simpler-robot/commit/c5167a9b9): Logger 模块?
- [`b92475576`](https://github.com/simple-robot/simpler-robot/commit/b92475576): 补充注释
- [`a8739fe72`](https://github.com/simple-robot/simpler-robot/commit/a8739fe72): 补充/调整注释
- [`a2b89bd18..1b99ef2b0`](https://github.com/simple-robot/simpler-robot/compare/a2b89bd18..a8739fe72): 补充注释
- [`997a0631f`](https://github.com/simple-robot/simpler-robot/commit/997a0631f): 调整注释
- [`d7589e568`](https://github.com/simple-robot/simpler-robot/commit/d7589e568): update doc config
- [`d3da88d6e`](https://github.com/simple-robot/simpler-robot/commit/d3da88d6e): Doc publish config
- [`7a43ad7f1`](https://github.com/simple-robot/simpler-robot/commit/7a43ad7f1): config
- [`7d64c972e`](https://github.com/simple-robot/simpler-robot/commit/7d64c972e): 补充 OriginBotManager 的相关注释, 增加一个 `getAny` 函数
- [`3c3b65f43`](https://github.com/simple-robot/simpler-robot/commit/3c3b65f43): 调整错别字
- [`4abf3857e`](https://github.com/simple-robot/simpler-robot/commit/4abf3857e): 更新文档部署配置文件
- [`8c853f8db`](https://github.com/simple-robot/simpler-robot/commit/8c853f8db): Dokka doc config and published to <https://simple-robot-library.github.io/simbot3-main-apiDoc>
- [`364dc4e88..a5b620aa4`](https://github.com/simple-robot/simpler-robot/compare/364dc4e88..8c853f8db): update workflow settings, and rename
- [`44238be75..de1e252f3`](https://github.com/simple-robot/simpler-robot/compare/44238be75..a5b620aa4): update workflow settings.
- [`a176882f8..2b64e33bb`](https://github.com/simple-robot/simpler-robot/compare/a176882f8..de1e252f3): update build settings.
- [`847de58aa`](https://github.com/simple-robot/simpler-robot/commit/847de58aa): test file
- [`1f72d8d48`](https://github.com/simple-robot/simpler-robot/commit/1f72d8d48): :camera_flash: Adding or updating snapshots.
- [`3cd68af28`](https://github.com/simple-robot/simpler-robot/commit/3cd68af28): config snapshot
- [`8d9bb1915`](https://github.com/simple-robot/simpler-robot/commit/8d9bb1915): new Logo!
- [`9fb5828ae`](https://github.com/simple-robot/simpler-robot/commit/9fb5828ae): new spring config
- [`d850b706b`](https://github.com/simple-robot/simpler-robot/commit/d850b706b): 重命名spring-boot-starter模块名称 `simboot-core-springboot-starter` -> `simboot-core-spring-boot-starter`
- [`bacae85f0..f53040e25`](https://github.com/simple-robot/simpler-robot/compare/bacae85f0..d850b706b): 调整注释
- [`1fd6bb355`](https://github.com/simple-robot/simpler-robot/commit/1fd6bb355): 恢复@Filter和@Filters的递归性。参考 https://github.com/spring-projects/spring-boot/issues/29662
- [`90e20c1f1`](https://github.com/simple-robot/simpler-robot/commit/90e20c1f1): 重新调整Role相关Api
- [`ebf4e8f34`](https://github.com/simple-robot/simpler-robot/commit/ebf4e8f34): 标记修改点
- [`8cfa52fb2..18b67a096`](https://github.com/simple-robot/simpler-robot/compare/8cfa52fb2..ebf4e8f34): update readme
- [`d1af4263c`](https://github.com/simple-robot/simpler-robot/commit/d1af4263c): build.kts配置修改
- [`cf18217e0`](https://github.com/simple-robot/simpler-robot/commit/cf18217e0): 测试补充
- [`6a48fa704`](https://github.com/simple-robot/simpler-robot/commit/6a48fa704): 增加 Timestamp 相关API
- [`d24ac0a7d`](https://github.com/simple-robot/simpler-robot/commit/d24ac0a7d): 调整 Timestamp 相关API
- [`a60570c25`](https://github.com/simple-robot/simpler-robot/commit/a60570c25): 消除 Message.Metadata 并调整注释
- [`dd5770ee9`](https://github.com/simple-robot/simpler-robot/commit/dd5770ee9): 消除Event.Metadata并调整注释
- [`aa1743b80`](https://github.com/simple-robot/simpler-robot/commit/aa1743b80): 移除Event中的Metadata
- [`00efc9b24`](https://github.com/simple-robot/simpler-robot/commit/00efc9b24): 补充注释
- [`a6e4b322b`](https://github.com/simple-robot/simpler-robot/commit/a6e4b322b): 增加部分中断异常
- [`f6df97e4e`](https://github.com/simple-robot/simpler-robot/commit/f6df97e4e): 移除 SimbootApplication 无用配置
- [`81bfe8e53`](https://github.com/simple-robot/simpler-robot/commit/81bfe8e53): 发布preview.2.0
- [`bcde2d41c..75f9bd98a`](https://github.com/simple-robot/simpler-robot/compare/bcde2d41c..81bfe8e53): 补充注释
- [`036547dfb`](https://github.com/simple-robot/simpler-robot/commit/036547dfb): push async
- [`de620740b`](https://github.com/simple-robot/simpler-robot/commit/de620740b): async listener
- [`0025a0b63..f66ab27f9`](https://github.com/simple-robot/simpler-robot/compare/0025a0b63..de620740b): keyword match
- [`08bd3ecda`](https://github.com/simple-robot/simpler-robot/commit/08bd3ecda): TODO mark
- [`c1a23b7e3`](https://github.com/simple-robot/simpler-robot/commit/c1a23b7e3): fix keyword match; update annotationTool to 0.6.3
- [`2d5d4b0db`](https://github.com/simple-robot/simpler-robot/commit/2d5d4b0db): update annotation tool
- [`a84d5cb77`](https://github.com/simple-robot/simpler-robot/commit/a84d5cb77): Filters fix
- [`db6810acc`](https://github.com/simple-robot/simpler-robot/commit/db6810acc): di -> 0.0.3
- [`3cf422e41`](https://github.com/simple-robot/simpler-robot/commit/3cf422e41): 修改注释
- [`0dd3cf8e4`](https://github.com/simple-robot/simpler-robot/commit/0dd3cf8e4): Filters 默认匹配方式
- [`a0997dfd4`](https://github.com/simple-robot/simpler-robot/commit/a0997dfd4): @TargetFilter.atBot: Boolean
- [`2ff9b1fa4`](https://github.com/simple-robot/simpler-robot/commit/2ff9b1fa4): Message.Element.equals() & hashCode()
- [`5a0ca59df..2364e44d9`](https://github.com/simple-robot/simpler-robot/compare/5a0ca59df..2ff9b1fa4): 为部分事件增加 `inXxx` 和 `useXxx` 扩展函数
- [`36d97d521`](https://github.com/simple-robot/simpler-robot/commit/36d97d521): 为部分事件增加 `inXxx` 扩展函数
- [`a55c35b33`](https://github.com/simple-robot/simpler-robot/commit/a55c35b33): 为部分事件增加 `useXxx` 扩展函数
- [`bec6cfe50`](https://github.com/simple-robot/simpler-robot/commit/bec6cfe50): version to pre.2.0
- [`7f04abe2c`](https://github.com/simple-robot/simpler-robot/commit/7f04abe2c): Resource and bot image api
- [`687ce0eb6..8ccbc1476`](https://github.com/simple-robot/simpler-robot/compare/687ce0eb6..7f04abe2c): StandardStreamableResource
- [`92f6d24ac`](https://github.com/simple-robot/simpler-robot/commit/92f6d24ac): remove serializable on Resource
- [`837ef071b..2b8c8f676`](https://github.com/simple-robot/simpler-robot/compare/837ef071b..92f6d24ac): 暂时移除IDResource及其相关内容
- [`dadfc572f..c9dcaa244`](https://github.com/simple-robot/simpler-robot/compare/dadfc572f..2b8c8f676): StandardStreamableResource
- [`8b2a99c51`](https://github.com/simple-robot/simpler-robot/commit/8b2a99c51): remove serializable on Resource
- [`2ca93a85b..bc4a49bb4`](https://github.com/simple-robot/simpler-robot/compare/2ca93a85b..8b2a99c51): 暂时移除IDResource及其相关内容
- [`9892b5602..28c13643e`](https://github.com/simple-robot/simpler-robot/compare/9892b5602..bc4a49bb4): ID.literal
- [`5ca4e66e1`](https://github.com/simple-robot/simpler-robot/commit/5ca4e66e1): 暂时移除IDResource及其相关内容
- [`1a40b1fca`](https://github.com/simple-robot/simpler-robot/commit/1a40b1fca): FriendInfo提供用户名相关辅助函数
- [`585ff502e`](https://github.com/simple-robot/simpler-robot/commit/585ff502e): MemberInfo提供用户名相关辅助函数
- [`164a69a1d`](https://github.com/simple-robot/simpler-robot/commit/164a69a1d): 增加待实现事件
- [`35132fab0`](https://github.com/simple-robot/simpler-robot/commit/35132fab0): 补充注释
- [`65d31436c`](https://github.com/simple-robot/simpler-robot/commit/65d31436c): 组织ID
- [`be13542f1..fef8f2b4a`](https://github.com/simple-robot/simpler-robot/compare/be13542f1..65d31436c): Readme
- [`c57f1aaa7`](https://github.com/simple-robot/simpler-robot/commit/c57f1aaa7): Readmy & copying
- [`d0465dfc6..9bed33f0b`](https://github.com/simple-robot/simpler-robot/compare/d0465dfc6..c57f1aaa7): Readme
- [`6ac811da3`](https://github.com/simple-robot/simpler-robot/commit/6ac811da3): logo
- [`638dfe0fe..66c9ed19c`](https://github.com/simple-robot/simpler-robot/compare/638dfe0fe..6ac811da3): 补充注释。
- [`66fbfa689..4e7218259`](https://github.com/simple-robot/simpler-robot/compare/66fbfa689..66c9ed19c): internal event keys
- [`91d01b271..39562b438`](https://github.com/simple-robot/simpler-robot/compare/91d01b271..4e7218259): 补充注释
- [`bbaeca91c`](https://github.com/simple-robot/simpler-robot/commit/bbaeca91c): 内部bot相关事件
- [`9e426d92e`](https://github.com/simple-robot/simpler-robot/commit/9e426d92e): ID for Resource
- [`fe71d1af6`](https://github.com/simple-robot/simpler-robot/commit/fe71d1af6): ID容器的实现
- [`d49e54b86`](https://github.com/simple-robot/simpler-robot/commit/d49e54b86): ID Test
- [`ad9bc8907`](https://github.com/simple-robot/simpler-robot/commit/ad9bc8907): 调整注释
- [`f072a333c`](https://github.com/simple-robot/simpler-robot/commit/f072a333c): ID容器的实现
- [`41aa0800a`](https://github.com/simple-robot/simpler-robot/commit/41aa0800a): ID 容器定义
- [`b011d1302`](https://github.com/simple-robot/simpler-robot/commit/b011d1302): 为@Listen所有衍生注解标记过时
- [`bacdbe24b`](https://github.com/simple-robot/simpler-robot/commit/bacdbe24b): 补充注释
- [`350148237`](https://github.com/simple-robot/simpler-robot/commit/350148237): 暂时异常`OnXxx`监听注解
- [`cf4b740a2`](https://github.com/simple-robot/simpler-robot/commit/cf4b740a2): 持续会话相关更新
- [`68f30d665`](https://github.com/simple-robot/simpler-robot/commit/68f30d665): 隐藏部分suspend api
- [`20a6ac82c`](https://github.com/simple-robot/simpler-robot/commit/20a6ac82c): 增加内部Bot事件
- [`c82abe3ed..27b70b16f`](https://github.com/simple-robot/simpler-robot/compare/c82abe3ed..20a6ac82c): 隐藏部分suspend api
- [`90ed097f2`](https://github.com/simple-robot/simpler-robot/commit/90ed097f2): 更新开源协议
- [`7a000f624..1d9698c46`](https://github.com/simple-robot/simpler-robot/compare/7a000f624..90ed097f2): 更新、调整、完善开源协议
- [`d3d7f4395`](https://github.com/simple-robot/simpler-robot/commit/d3d7f4395): ID Util
- [`db7baaaa9`](https://github.com/simple-robot/simpler-robot/commit/db7baaaa9): ID UUID random
- [`52cd193c4..698bdee9c`](https://github.com/simple-robot/simpler-robot/compare/52cd193c4..db7baaaa9): 调整toAsync返回值类型。
- [`4b6608bad`](https://github.com/simple-robot/simpler-robot/commit/4b6608bad): Core manager intercept config
- [`400a45b82`](https://github.com/simple-robot/simpler-robot/commit/400a45b82): 补充注释
- [`db3d48142`](https://github.com/simple-robot/simpler-robot/commit/db3d48142): 全局获取作用域与持续会话作用域
- [`d3897f6e1`](https://github.com/simple-robot/simpler-robot/commit/d3897f6e1): 补充注释
- [`c223df9b8`](https://github.com/simple-robot/simpler-robot/commit/c223df9b8): Survivable.waiting() 抛出中断异常
- [`eb1d94aa7`](https://github.com/simple-robot/simpler-robot/commit/eb1d94aa7): Organization / group / guild / channel 相关直接获取API, 调整返回值类型
- [`f4977d2fc`](https://github.com/simple-robot/simpler-robot/commit/f4977d2fc): runBlocking -> runInBlocking
- [`37acb252a`](https://github.com/simple-robot/simpler-robot/commit/37acb252a): 调整注释
- [`ecb516315`](https://github.com/simple-robot/simpler-robot/commit/ecb516315): 增加 xxIfSupport相关inline API
- [`1665ba556`](https://github.com/simple-robot/simpler-robot/commit/1665ba556): Bot增加独立获取相关内容的api
- [`105948fcf`](https://github.com/simple-robot/simpler-robot/commit/105948fcf): 事件注释修改，test
- [`706099094`](https://github.com/simple-robot/simpler-robot/commit/706099094): 恢复监听，onMessage with Duration
- [`f458720e2`](https://github.com/simple-robot/simpler-robot/commit/f458720e2): waiting on message
- [`540b570cc..b54a8ef05`](https://github.com/simple-robot/simpler-robot/compare/540b570cc..f458720e2): 调整BotManager api
- [`661b4663f`](https://github.com/simple-robot/simpler-robot/commit/661b4663f): session waitingOnMessage
- [`a70e45cf6`](https://github.com/simple-robot/simpler-robot/commit/a70e45cf6): session waitingForOnMessage
- [`395bc8917`](https://github.com/simple-robot/simpler-robot/commit/395bc8917): 超时处理
- [`f4b682891`](https://github.com/simple-robot/simpler-robot/commit/f4b682891): 超时清理
- [`89faa018e`](https://github.com/simple-robot/simpler-robot/commit/89faa018e): 调整OriginBotManager部分API
- [`4f7c6c69a`](https://github.com/simple-robot/simpler-robot/commit/4f7c6c69a): Member.roles
- [`ec38399af`](https://github.com/simple-robot/simpler-robot/commit/ec38399af): 补充注释，隐藏部分函数
- [`8b27b3165`](https://github.com/simple-robot/simpler-robot/commit/8b27b3165): 调整Member/Organization/Role相关API，更新版本到pre.1.1
- [`c3d0ce810`](https://github.com/simple-robot/simpler-robot/commit/c3d0ce810): version to 1.0
- [`8806b1eda`](https://github.com/simple-robot/simpler-robot/commit/8806b1eda): coroutine scopes
- [`43a418ac3`](https://github.com/simple-robot/simpler-robot/commit/43a418ac3): Add some@JvmSynthetic
- [`3dde53a42`](https://github.com/simple-robot/simpler-robot/commit/3dde53a42): test comment
- [`c457ef220..84400f2a3`](https://github.com/simple-robot/simpler-robot/compare/c457ef220..3dde53a42): build config
- [`d892053c7`](https://github.com/simple-robot/simpler-robot/commit/d892053c7): ID update
- [`02bfcb88e`](https://github.com/simple-robot/simpler-robot/commit/02bfcb88e): Test and get members
- [`1db64a889`](https://github.com/simple-robot/simpler-robot/commit/1db64a889): Event annotations
- [`344b31f41`](https://github.com/simple-robot/simpler-robot/commit/344b31f41): accept
- [`b00a00ac7`](https://github.com/simple-robot/simpler-robot/commit/b00a00ac7): GroupJoinRequestEvent
- [`3ee736561`](https://github.com/simple-robot/simpler-robot/commit/3ee736561): message
- [`934836960`](https://github.com/simple-robot/simpler-robot/commit/934836960): Events
- [`5b5d2324d`](https://github.com/simple-robot/simpler-robot/commit/5b5d2324d): manager
- [`a85fe27c0`](https://github.com/simple-robot/simpler-robot/commit/a85fe27c0): 注释于注解
- [`24c9d019d`](https://github.com/simple-robot/simpler-robot/commit/24c9d019d): annotations
- [`6aad064a0`](https://github.com/simple-robot/simpler-robot/commit/6aad064a0): friends
- [`f6567691f`](https://github.com/simple-robot/simpler-robot/commit/f6567691f): requires opt annotation
- [`b510fd172`](https://github.com/simple-robot/simpler-robot/commit/b510fd172): request events, changed events
- [`d8fe1f5c9`](https://github.com/simple-robot/simpler-robot/commit/d8fe1f5c9): 补充注释
- [`91d1d0db4`](https://github.com/simple-robot/simpler-robot/commit/91d1d0db4): Limiter
- [`2e6f8bc95`](https://github.com/simple-robot/simpler-robot/commit/2e6f8bc95): Resources
- [`66cba4f9e..a32569cfe`](https://github.com/simple-robot/simpler-robot/compare/66cba4f9e..2e6f8bc95): Limiter.kt
- [`7fe5b3d92`](https://github.com/simple-robot/simpler-robot/commit/7fe5b3d92): Bot apis
- [`d25755511`](https://github.com/simple-robot/simpler-robot/commit/d25755511): pushIfProcessable
- [`b8b4e0f92`](https://github.com/simple-robot/simpler-robot/commit/b8b4e0f92): remove ComplexID
- [`df4db5504..9e1fe320b`](https://github.com/simple-robot/simpler-robot/compare/df4db5504..b8b4e0f92): Lazy value util
- [`9c3ab7db5`](https://github.com/simple-robot/simpler-robot/commit/9c3ab7db5): friend message event
- [`7a7f6b571`](https://github.com/simple-robot/simpler-robot/commit/7a7f6b571): base event
- [`c94f4ce60`](https://github.com/simple-robot/simpler-robot/commit/c94f4ce60): new api
- [`a1addc1b5`](https://github.com/simple-robot/simpler-robot/commit/a1addc1b5): remove session old
- [`b153b7c72`](https://github.com/simple-robot/simpler-robot/commit/b153b7c72): events api
- [`76efacfc2`](https://github.com/simple-robot/simpler-robot/commit/76efacfc2): blocking with interruptible
- [`626ace278`](https://github.com/simple-robot/simpler-robot/commit/626ace278): Resource serializer
- [`5cdfe049b`](https://github.com/simple-robot/simpler-robot/commit/5cdfe049b): Image<E>
- [`038302b9e..87346870c`](https://github.com/simple-robot/simpler-robot/compare/038302b9e..5cdfe049b): resource
- [`1440e50b6`](https://github.com/simple-robot/simpler-robot/commit/1440e50b6): send message content
- [`42f2b7071`](https://github.com/simple-robot/simpler-robot/commit/42f2b7071): send string
- [`3d5ef0014..6bb0f9e42`](https://github.com/simple-robot/simpler-robot/compare/3d5ef0014..42f2b7071): Resources
- [`0b44157d5`](https://github.com/simple-robot/simpler-robot/commit/0b44157d5): Resource
- [`57b2734bb`](https://github.com/simple-robot/simpler-robot/commit/57b2734bb): SendSupport.kt
- [`ed7f32526`](https://github.com/simple-robot/simpler-robot/commit/ed7f32526): DeleteAction -> DeleteSupport
- [`37cee1be9`](https://github.com/simple-robot/simpler-robot/commit/37cee1be9): bot manager config
- [`47988e331`](https://github.com/simple-robot/simpler-robot/commit/47988e331): MuteAction.kt
- [`d3510906a`](https://github.com/simple-robot/simpler-robot/commit/d3510906a): V, and session context
- [`18a026aff`](https://github.com/simple-robot/simpler-robot/commit/18a026aff): Messages 优化
- [`3c7e91abb`](https://github.com/simple-robot/simpler-robot/commit/3c7e91abb): Messages 不再验证 component
- [`0b36545d3`](https://github.com/simple-robot/simpler-robot/commit/0b36545d3): add log
- [`ab9937af9..5ee2266bb`](https://github.com/simple-robot/simpler-robot/compare/ab9937af9..0b36545d3): preview-0.6
- [`d8e88f45e`](https://github.com/simple-robot/simpler-robot/commit/d8e88f45e): update README.md
- [`b5e96fceb`](https://github.com/simple-robot/simpler-robot/commit/b5e96fceb): check event type
- [`b3d793888`](https://github.com/simple-robot/simpler-robot/commit/b3d793888): EventResult.kt
- [`549a1f5c3`](https://github.com/simple-robot/simpler-robot/commit/549a1f5c3): 事件会话
- [`30f27b093`](https://github.com/simple-robot/simpler-robot/commit/30f27b093): 持续会话；Event.Key
- [`ac5d5c081`](https://github.com/simple-robot/simpler-robot/commit/ac5d5c081): textContent前置处理器标准注解
- [`de1ca69ea`](https://github.com/simple-robot/simpler-robot/commit/de1ca69ea): 专属拦截器、textContent前置处理器
- [`f52455c05..114453e5c`](https://github.com/simple-robot/simpler-robot/compare/f52455c05..de1ca69ea): 拦截器接口本身不携带ID; @Interceptor;
- [`95ff9196c`](https://github.com/simple-robot/simpler-robot/commit/95ff9196c): session context
- [`e4ca747fa`](https://github.com/simple-robot/simpler-robot/commit/e4ca747fa): Session
- [`34f517f4a..b9aa97cca`](https://github.com/simple-robot/simpler-robot/compare/34f517f4a..e4ca747fa): ID Map
- [`e5de54c80`](https://github.com/simple-robot/simpler-robot/commit/e5de54c80): session context 2
- [`2b98cbd67`](https://github.com/simple-robot/simpler-robot/commit/2b98cbd67): scope binder
- [`dc8ee83d3..363ff8261`](https://github.com/simple-robot/simpler-robot/compare/dc8ee83d3..2b98cbd67): continuous session
- [`f358bcb0b`](https://github.com/simple-robot/simpler-robot/commit/f358bcb0b): text processor
- [`d4fc957f8`](https://github.com/simple-robot/simpler-robot/commit/d4fc957f8): @Filter.ifNullPass
- [`490fdad0b..47629d2c9`](https://github.com/simple-robot/simpler-robot/compare/490fdad0b..d4fc957f8): loggers
- [`050c1ab7d`](https://github.com/simple-robot/simpler-robot/commit/050c1ab7d): event
- [`a1bbb3c05`](https://github.com/simple-robot/simpler-robot/commit/a1bbb3c05): @Listener
- [`95953cc87`](https://github.com/simple-robot/simpler-robot/commit/95953cc87): @Interceptor
- [`61a0a469c`](https://github.com/simple-robot/simpler-robot/commit/61a0a469c): if null pass
- [`f5198b26a`](https://github.com/simple-robot/simpler-robot/commit/f5198b26a): text content processor
- [`d93bbd447`](https://github.com/simple-robot/simpler-robot/commit/d93bbd447): Top listener scan
- [`cfaf93386`](https://github.com/simple-robot/simpler-robot/commit/cfaf93386): test
- [`214cd1fb0`](https://github.com/simple-robot/simpler-robot/commit/214cd1fb0): session
- [`3f4a49903`](https://github.com/simple-robot/simpler-robot/commit/3f4a49903): session scope, filter for session
- [`a37bbc631`](https://github.com/simple-robot/simpler-robot/commit/a37bbc631): Blocking api
- [`e6701eff1`](https://github.com/simple-robot/simpler-robot/commit/e6701eff1): coroutine test
- [`ad7894663`](https://github.com/simple-robot/simpler-robot/commit/ad7894663): Continuous session scope
- [`fa45198e8`](https://github.com/simple-robot/simpler-robot/commit/fa45198e8): rename module
- [`f83348814`](https://github.com/simple-robot/simpler-robot/commit/f83348814): autowired annotation
- [`d385741fa..8abfe1c69`](https://github.com/simple-robot/simpler-robot/compare/d385741fa..f83348814): springboot starter test
- [`cc9459c8f..8589152b9`](https://github.com/simple-robot/simpler-robot/compare/cc9459c8f..8abfe1c69): Springboot starter
- [`6fd7f7801`](https://github.com/simple-robot/simpler-robot/commit/6fd7f7801): springboot starter module
- [`0129a2cbd`](https://github.com/simple-robot/simpler-robot/commit/0129a2cbd): 监听事件类型检测优化
- [`6b46ebeb4`](https://github.com/simple-robot/simpler-robot/commit/6b46ebeb4): 各种默认binder
- [`7369f8e2d`](https://github.com/simple-robot/simpler-robot/commit/7369f8e2d): test and all bots info
- [`1b24a0fcf`](https://github.com/simple-robot/simpler-robot/commit/1b24a0fcf): Scanner and runner
- [`7ace0c1c7`](https://github.com/simple-robot/simpler-robot/commit/7ace0c1c7): Pom setup and group
- [`38f231c87..36aa4d649`](https://github.com/simple-robot/simpler-robot/compare/38f231c87..7ace0c1c7): Core entrance
- [`98c8e6549..205b1f0a1`](https://github.com/simple-robot/simpler-robot/compare/98c8e6549..36aa4d649): Scanner and runner
- [`cb938f332`](https://github.com/simple-robot/simpler-robot/commit/cb938f332): annotation processor
- [`d57ea35a3`](https://github.com/simple-robot/simpler-robot/commit/d57ea35a3): interceptor
- [`849af204b`](https://github.com/simple-robot/simpler-robot/commit/849af204b): new listener for java
- [`4f116e1dc..baf13fcac`](https://github.com/simple-robot/simpler-robot/compare/4f116e1dc..849af204b): Event.Key.getKey
- [`5beea6248`](https://github.com/simple-robot/simpler-robot/commit/5beea6248): event listener
- [`338b1fb5c`](https://github.com/simple-robot/simpler-robot/commit/338b1fb5c): components
- [`ebaa826e1..beb4236b8`](https://github.com/simple-robot/simpler-robot/compare/ebaa826e1..338b1fb5c): binder
- [`39df79396`](https://github.com/simple-robot/simpler-robot/commit/39df79396): BotVerifyInfo
- [`158d4050b..4cd11a8e6`](https://github.com/simple-robot/simpler-robot/compare/158d4050b..39df79396): Update V
- [`1eed33361`](https://github.com/simple-robot/simpler-robot/commit/1eed33361): project names and module names
- [`9c7f7a932`](https://github.com/simple-robot/simpler-robot/commit/9c7f7a932): Resource scanner
- [`9e202d7c9`](https://github.com/simple-robot/simpler-robot/commit/9e202d7c9): update icon
- [`09531043a`](https://github.com/simple-robot/simpler-robot/commit/09531043a): Annotation listener processor
- [`6fea3ebfc`](https://github.com/simple-robot/simpler-robot/commit/6fea3ebfc): Listener and Attributes
- [`d4240f646`](https://github.com/simple-robot/simpler-robot/commit/d4240f646): Scope
- [`3483fd31f`](https://github.com/simple-robot/simpler-robot/commit/3483fd31f): Scanner
- [`5864a4d51`](https://github.com/simple-robot/simpler-robot/commit/5864a4d51): ReactEvents
- [`510893b6d`](https://github.com/simple-robot/simpler-robot/commit/510893b6d): caller
- [`38612d311`](https://github.com/simple-robot/simpler-robot/commit/38612d311): GenericListener
- [`001b7bcc0`](https://github.com/simple-robot/simpler-robot/commit/001b7bcc0): update slf4j version
- [`a4bb09fb8`](https://github.com/simple-robot/simpler-robot/commit/a4bb09fb8): Event isSubFrom
- [`93da2d935`](https://github.com/simple-robot/simpler-robot/commit/93da2d935): SimbotComponent
- [`3574a23b7..056ddcb8f`](https://github.com/simple-robot/simpler-robot/compare/3574a23b7..93da2d935): Components.all
- [`a1cb5758b`](https://github.com/simple-robot/simpler-robot/commit/a1cb5758b): Components.getAll
- [`f2b8c26ec`](https://github.com/simple-robot/simpler-robot/commit/f2b8c26ec): boots, and remove submodule
- [`81e1002f0`](https://github.com/simple-robot/simpler-robot/commit/81e1002f0): managers and listeners
- [`f91d7374d`](https://github.com/simple-robot/simpler-robot/commit/f91d7374d): core boot filter
- [`9368bf86b`](https://github.com/simple-robot/simpler-robot/commit/9368bf86b): core intercept
- [`847ccd97e`](https://github.com/simple-robot/simpler-robot/commit/847ccd97e): Filter processor
- [`71fa7e67d`](https://github.com/simple-robot/simpler-robot/commit/71fa7e67d): Survivable, bot processor
- [`aa941c7d7..483dad759`](https://github.com/simple-robot/simpler-robot/compare/aa941c7d7..71fa7e67d): Test show for doc
- [`9cf929d03`](https://github.com/simple-robot/simpler-robot/commit/9cf929d03): rename for CoreListenerManagerConfiguration
- [`9e06d006d`](https://github.com/simple-robot/simpler-robot/commit/9e06d006d): version to 3.p.0.5
- [`cf162fa25`](https://github.com/simple-robot/simpler-robot/commit/cf162fa25): 3.0.0-preview.0.4
- [`08b78de1d`](https://github.com/simple-robot/simpler-robot/commit/08b78de1d): core event manager -> core listener manager
- [`fb279d470..068456403`](https://github.com/simple-robot/simpler-robot/compare/fb279d470..08b78de1d): Objective event
- [`bb32466f7`](https://github.com/simple-robot/simpler-robot/commit/bb32466f7): Comment
- [`e2c8a0d90`](https://github.com/simple-robot/simpler-robot/commit/e2c8a0d90): Filters and listeners func 4j
- [`f998f0db0`](https://github.com/simple-robot/simpler-robot/commit/f998f0db0): 更多的@Api4J
- [`b572c4fb9`](https://github.com/simple-robot/simpler-robot/commit/b572c4fb9): 更多的标准事件
- [`cbb08e9e5`](https://github.com/simple-robot/simpler-robot/commit/cbb08e9e5): Future, and to 3.0.0-preview.0.4
- [`1f7468e18`](https://github.com/simple-robot/simpler-robot/commit/1f7468e18): for 3.0.0-preview.0.3
- [`e713dc490`](https://github.com/simple-robot/simpler-robot/commit/e713dc490): CoreEventManager
- [`c07a4a29f`](https://github.com/simple-robot/simpler-robot/commit/c07a4a29f): OriginBotManager.kt
- [`f3f8f3a6f`](https://github.com/simple-robot/simpler-robot/commit/f3f8f3a6f): bot
- [`ce514d8d5`](https://github.com/simple-robot/simpler-robot/commit/ce514d8d5): override
- [`07b752578`](https://github.com/simple-robot/simpler-robot/commit/07b752578): Events
- [`8ffe085ee`](https://github.com/simple-robot/simpler-robot/commit/8ffe085ee): messages
- [`ff46f4125`](https://github.com/simple-robot/simpler-robot/commit/ff46f4125): Boots - boot-core module
- [`63f7ad6ff`](https://github.com/simple-robot/simpler-robot/commit/63f7ad6ff): attributes
- [`e87cbec75..d1bb5f016`](https://github.com/simple-robot/simpler-robot/compare/e87cbec75..63f7ad6ff): OriginBotManager
- [`96037c58e`](https://github.com/simple-robot/simpler-robot/commit/96037c58e): CoreEventProcessingContextResolver
- [`62f66f4e1`](https://github.com/simple-robot/simpler-robot/commit/62f66f4e1): 拦截器
- [`9b73b44fc`](https://github.com/simple-robot/simpler-robot/commit/9b73b44fc): Core manager
- [`02630c7f5..71a039ce5`](https://github.com/simple-robot/simpler-robot/compare/02630c7f5..9b73b44fc): Messages.kt
- [`46a8125b7`](https://github.com/simple-robot/simpler-robot/commit/46a8125b7): clean test code
- [`0bd4e0ced`](https://github.com/simple-robot/simpler-robot/commit/0bd4e0ced): Components
- [`a3dcd7240`](https://github.com/simple-robot/simpler-robot/commit/a3dcd7240): 不管子模块了
- [`7c1dc6fd1`](https://github.com/simple-robot/simpler-robot/commit/7c1dc6fd1): add submodule tencent-guild again
- [`3d476152b`](https://github.com/simple-robot/simpler-robot/commit/3d476152b): update\
- [`8b447d939`](https://github.com/simple-robot/simpler-robot/commit/8b447d939): Idea copyright
- [`51000ef70`](https://github.com/simple-robot/simpler-robot/commit/51000ef70): publish util
- [`72658080e`](https://github.com/simple-robot/simpler-robot/commit/72658080e): Message serializers
- [`a74a7536e`](https://github.com/simple-robot/simpler-robot/commit/a74a7536e): Fix some
- [`42f555eab`](https://github.com/simple-robot/simpler-robot/commit/42f555eab): modules and move component to tencent-guild
- [`061eb9b5d`](https://github.com/simple-robot/simpler-robot/commit/061eb9b5d): update module info
- [`4a09523b9`](https://github.com/simple-robot/simpler-robot/commit/4a09523b9): upload to preview dev-v3.0.0.preview.0.2
- [`f21f261d4`](https://github.com/simple-robot/simpler-robot/commit/f21f261d4): Publish
- [`11cb4fc3a`](https://github.com/simple-robot/simpler-robot/commit/11cb4fc3a): Bot with processor
- [`0ba58ca2c`](https://github.com/simple-robot/simpler-robot/commit/0ba58ca2c): component-guild for core
- [`1b9cce9da`](https://github.com/simple-robot/simpler-robot/commit/1b9cce9da): Permissions
- [`ed8b3dd3c`](https://github.com/simple-robot/simpler-robot/commit/ed8b3dd3c): limiter offset
- [`33afcdec6`](https://github.com/simple-robot/simpler-robot/commit/33afcdec6): mute, limiter actions
- [`62bd355d5`](https://github.com/simple-robot/simpler-robot/commit/62bd355d5): README, and something for event, listens, limiters
- [`16d8b1d37`](https://github.com/simple-robot/simpler-robot/commit/16d8b1d37): events
- [`b9896f8d8`](https://github.com/simple-robot/simpler-robot/commit/b9896f8d8): View
- [`a7d2a2b6d`](https://github.com/simple-robot/simpler-robot/commit/a7d2a2b6d): timestamp serializer
- [`42f08c7ce`](https://github.com/simple-robot/simpler-robot/commit/42f08c7ce): Flow to Stream
- [`712455305..bb0efcfff`](https://github.com/simple-robot/simpler-robot/compare/712455305..42f08c7ce): Manager
- [`df517be07`](https://github.com/simple-robot/simpler-robot/commit/df517be07): Infos
- [`329f693c1`](https://github.com/simple-robot/simpler-robot/commit/329f693c1): EventManager
- [`2ca6ee4e8`](https://github.com/simple-robot/simpler-robot/commit/2ca6ee4e8): component
- [`1009a1bb5`](https://github.com/simple-robot/simpler-robot/commit/1009a1bb5): some blocking api
- [`e47387b9f`](https://github.com/simple-robot/simpler-robot/commit/e47387b9f): 调整结构
- [`d622d946f`](https://github.com/simple-robot/simpler-robot/commit/d622d946f): tencent component
- [`a8f715858`](https://github.com/simple-robot/simpler-robot/commit/a8f715858): modules
- [`2dab3c951`](https://github.com/simple-robot/simpler-robot/commit/2dab3c951): Message action
- [`c7798a81b`](https://github.com/simple-robot/simpler-robot/commit/c7798a81b): 移除部分action
- [`2ae043172`](https://github.com/simple-robot/simpler-robot/commit/2ae043172): 组织，分组
- [`f8ae5eec1`](https://github.com/simple-robot/simpler-robot/commit/f8ae5eec1): Messages
- [`ef1141bad`](https://github.com/simple-robot/simpler-robot/commit/ef1141bad): Authors
- [`df2cfd3d6`](https://github.com/simple-robot/simpler-robot/commit/df2cfd3d6): Event
- [`a89825365`](https://github.com/simple-robot/simpler-robot/commit/a89825365): MessageContent
- [`5d7ed3ea2`](https://github.com/simple-robot/simpler-robot/commit/5d7ed3ea2): action, reply message
- [`dd909f235`](https://github.com/simple-robot/simpler-robot/commit/dd909f235): Attribute, component
- [`0bbc20396`](https://github.com/simple-robot/simpler-robot/commit/0bbc20396): Component SPI
- [`476c0b45e`](https://github.com/simple-robot/simpler-robot/commit/476c0b45e): Components get
- [`4b748c063`](https://github.com/simple-robot/simpler-robot/commit/4b748c063): BotManager
- [`f78fd4ad9`](https://github.com/simple-robot/simpler-robot/commit/f78fd4ad9): Attr
- [`66383dcf8`](https://github.com/simple-robot/simpler-robot/commit/66383dcf8): 组织信息
- [`91e3cf99f`](https://github.com/simple-robot/simpler-robot/commit/91e3cf99f): publish to local config
- [`dfcfada75`](https://github.com/simple-robot/simpler-robot/commit/dfcfada75): move api to apis, and add submodule for tencent-guild
- [`d5e275f48`](https://github.com/simple-robot/simpler-robot/commit/d5e275f48): 变更事件 时间戳
- [`43a6f1716`](https://github.com/simple-robot/simpler-robot/commit/43a6f1716): Events
- [`2a48b8f86`](https://github.com/simple-robot/simpler-robot/commit/2a48b8f86): Component
- [`47a122b82`](https://github.com/simple-robot/simpler-robot/commit/47a122b82): 事件，行为，ID，请求
- [`577e02c1f`](https://github.com/simple-robot/simpler-robot/commit/577e02c1f): request event
- [`680b988ae`](https://github.com/simple-robot/simpler-robot/commit/680b988ae): Request event
- [`ddda26be4`](https://github.com/simple-robot/simpler-robot/commit/ddda26be4): 组织，限流器
- [`5e132564f`](https://github.com/simple-robot/simpler-robot/commit/5e132564f): 事件处理
- [`af66814e4`](https://github.com/simple-robot/simpler-robot/commit/af66814e4): Event Manager
- [`b3c66a853`](https://github.com/simple-robot/simpler-robot/commit/b3c66a853): 核心 - 事件管理
- [`bd1e080f7`](https://github.com/simple-robot/simpler-robot/commit/bd1e080f7): listeners with filter
- [`efb023a2f`](https://github.com/simple-robot/simpler-robot/commit/efb023a2f): 拦截器，过滤器
- [`7e8c8c7d7`](https://github.com/simple-robot/simpler-robot/commit/7e8c8c7d7): ID, listener
- [`632dd99ec`](https://github.com/simple-robot/simpler-robot/commit/632dd99ec): 定义，消息
- [`9fcb4ae8f`](https://github.com/simple-robot/simpler-robot/commit/9fcb4ae8f): Result
- [`4ef719157`](https://github.com/simple-robot/simpler-robot/commit/4ef719157): 事件, 定义, 行为, 提供者
- [`ba9b24337`](https://github.com/simple-robot/simpler-robot/commit/ba9b24337): 事件相关
- [`c1dba6d80`](https://github.com/simple-robot/simpler-robot/commit/c1dba6d80): 放弃多平台；annotation和api定义
- [`7b837c4ea`](https://github.com/simple-robot/simpler-robot/commit/7b837c4ea): 处理器
- [`8e3d917c3`](https://github.com/simple-robot/simpler-robot/commit/8e3d917c3): 事件流程上下文
- [`4e6c9038c`](https://github.com/simple-robot/simpler-robot/commit/4e6c9038c): 各种基础接口定义
- [`62272fc64`](https://github.com/simple-robot/simpler-robot/commit/62272fc64): 移动异常类
- [`bf2dcd60f`](https://github.com/simple-robot/simpler-robot/commit/bf2dcd60f): Gradlew
- [`7e5ef7682`](https://github.com/simple-robot/simpler-robot/commit/7e5ef7682): ID
- [`5475f8582`](https://github.com/simple-robot/simpler-robot/commit/5475f8582): 事件，拦截
- [`1c80e8e67`](https://github.com/simple-robot/simpler-robot/commit/1c80e8e67): 拦截器，处理器
- [`baae7f670..b88db64ee`](https://github.com/simple-robot/simpler-robot/compare/baae7f670..1c80e8e67): ID
- [`f1b1a6e23`](https://github.com/simple-robot/simpler-robot/commit/f1b1a6e23): Project conf
- [`0a73dacaa`](https://github.com/simple-robot/simpler-robot/commit/0a73dacaa): conf
- [`266281b06`](https://github.com/simple-robot/simpler-robot/commit/266281b06): Dokka config
- [`494aec776`](https://github.com/simple-robot/simpler-robot/commit/494aec776): update copyright and license
- [`a8deca991..071b89d13`](https://github.com/simple-robot/simpler-robot/compare/a8deca991..494aec776): ID
- [`92f728fc2`](https://github.com/simple-robot/simpler-robot/commit/92f728fc2): ID and buildSrc
- [`ae28e3374..d902e7bff`](https://github.com/simple-robot/simpler-robot/compare/ae28e3374..92f728fc2): update
- [`2bf7b4d40`](https://github.com/simple-robot/simpler-robot/commit/2bf7b4d40): update amend
- [`66ddcd6c1`](https://github.com/simple-robot/simpler-robot/commit/66ddcd6c1): ID
- [`86ad08daa`](https://github.com/simple-robot/simpler-robot/commit/86ad08daa): Logger
- [`b452fb816`](https://github.com/simple-robot/simpler-robot/commit/b452fb816): Logger & i18n
- [`d139c7b3a`](https://github.com/simple-robot/simpler-robot/commit/d139c7b3a): annotation module
- [`1ea57655b`](https://github.com/simple-robot/simpler-robot/commit/1ea57655b): okio
- [`7c0812266`](https://github.com/simple-robot/simpler-robot/commit/7c0812266): The ID
- [`980257b39`](https://github.com/simple-robot/simpler-robot/commit/980257b39): Messages & Events
- [`6ce9d184a`](https://github.com/simple-robot/simpler-robot/commit/6ce9d184a): Messages
- [`3885f038e`](https://github.com/simple-robot/simpler-robot/commit/3885f038e): Messages.
- [`d260686e1`](https://github.com/simple-robot/simpler-robot/commit/d260686e1): Internal
- [`33db3fcbd`](https://github.com/simple-robot/simpler-robot/commit/33db3fcbd): Messages
- [`d20ce149b`](https://github.com/simple-robot/simpler-robot/commit/d20ce149b): Message, and test
- [`ba29f30dc`](https://github.com/simple-robot/simpler-robot/commit/ba29f30dc): Messages
- [`baf45c786`](https://github.com/simple-robot/simpler-robot/commit/baf45c786): Message, and test
- [`a81c64ca6`](https://github.com/simple-robot/simpler-robot/commit/a81c64ca6): Message
- [`86cb673f8`](https://github.com/simple-robot/simpler-robot/commit/86cb673f8): List
- [`e169c10b5`](https://github.com/simple-robot/simpler-robot/commit/e169c10b5): attribute
- [`c5ef4ff1d..9c6629283`](https://github.com/simple-robot/simpler-robot/compare/c5ef4ff1d..e169c10b5): Component
- [`a1656624c`](https://github.com/simple-robot/simpler-robot/commit/a1656624c): Message
- [`f77f17772`](https://github.com/simple-robot/simpler-robot/commit/f77f17772): Bot Manager
- [`bfd2232c6`](https://github.com/simple-robot/simpler-robot/commit/bfd2232c6): Result serializer
- [`ca616e871`](https://github.com/simple-robot/simpler-robot/commit/ca616e871): 部分接口定义: Result : FutureResult
- [`b9ce69126`](https://github.com/simple-robot/simpler-robot/commit/b9ce69126): 部分接口定义: Result
- [`2f3948d51`](https://github.com/simple-robot/simpler-robot/commit/2f3948d51): 部分接口定义。
- [`980ad0583`](https://github.com/simple-robot/simpler-robot/commit/980ad0583): Api
- [`14852d543..189161d51`](https://github.com/simple-robot/simpler-robot/compare/14852d543..980ad0583): Update gradle scripts.
- [`70bb72d8e`](https://github.com/simple-robot/simpler-robot/commit/70bb72d8e): :bulb: 添加注释说明
- [`00500c404`](https://github.com/simple-robot/simpler-robot/commit/00500c404): Add icon file
- [`4d65550cc`](https://github.com/simple-robot/simpler-robot/commit/4d65550cc): :see_no_evil: Adding or updating a .gitignore file.
- [`f282146e2`](https://github.com/simple-robot/simpler-robot/commit/f282146e2): update .idea files
- [`1db51ada1`](https://github.com/simple-robot/simpler-robot/commit/1db51ada1): Gradle buildSrc
- [`d38a05056`](https://github.com/simple-robot/simpler-robot/commit/d38a05056): Api module
- [`7c846f98e..ff3c71cd3`](https://github.com/simple-robot/simpler-robot/compare/7c846f98e..d38a05056): :tada: Initial project.

## v2.3.9

> Release & Pull Notes: [v2.3.9](https://github.com/simple-robot/simpler-robot/releases/tag/v2.3.9)
>
> Commit compare: [v2.3.8..v2.3.9](https://github.com/simple-robot/simpler-robot/compare/v2.3.8..v2.3.9)

- [`ba323e615`](https://github.com/simple-robot/simpler-robot/commit/ba323e615): 清理部分内容
- [`fe29cb5a8`](https://github.com/simple-robot/simpler-robot/commit/fe29cb5a8): 依赖版本更新
- [`75445a020`](https://github.com/simple-robot/simpler-robot/commit/75445a020): 调整CatCode解析判断顺序
- [`7d26b10e3`](https://github.com/simple-robot/simpler-robot/commit/7d26b10e3): 更新hutool标记版本
- [`44f1eb5c9`](https://github.com/simple-robot/simpler-robot/commit/44f1eb5c9): test

## v2.3.8

> Release & Pull Notes: [v2.3.8](https://github.com/simple-robot/simpler-robot/releases/tag/v2.3.8)
>
> Commit compare: [v2.3.7..v2.3.8](https://github.com/simple-robot/simpler-robot/compare/v2.3.7..v2.3.8)

- [`2055ceec0`](https://github.com/simple-robot/simpler-robot/commit/2055ceec0): update to v2.3.8
- [`98024f69b`](https://github.com/simple-robot/simpler-robot/commit/98024f69b): 移除github配置
- [`7aa7c7e8d`](https://github.com/simple-robot/simpler-robot/commit/7aa7c7e8d): test

## v2.3.7

> Release & Pull Notes: [v2.3.7](https://github.com/simple-robot/simpler-robot/releases/tag/v2.3.7)
>
> Commit compare: [v2.3.5..v2.3.7](https://github.com/simple-robot/simpler-robot/compare/v2.3.5..v2.3.7)

- [`0179e7efb`](https://github.com/simple-robot/simpler-robot/commit/0179e7efb): 临时针对[mirai issue#1852](https://github.com/mamoe/mirai/issues/1852) 进行特殊处理
- [`917653d30`](https://github.com/simple-robot/simpler-robot/commit/917653d30): update readme
- [`ad8efda79`](https://github.com/simple-robot/simpler-robot/commit/ad8efda79): update version
- [`5502c0428`](https://github.com/simple-robot/simpler-robot/commit/5502c0428): update README.md
- [`d0293ea5c`](https://github.com/simple-robot/simpler-robot/commit/d0293ea5c): update to v2.3.5 - 尝试使用另一种办法解决mirai 下login异常时 logback 堆栈溢出问题。 - 修复mirai下GroupMsg的groupMsgType无法区分匿名用户的问题
- [`6e0ceba72`](https://github.com/simple-robot/simpler-robot/commit/6e0ceba72): test

## v2.3.5

> Release & Pull Notes: [v2.3.5](https://github.com/simple-robot/simpler-robot/releases/tag/v2.3.5)
>
> Commit compare: [v2.3.4..v2.3.5](https://github.com/simple-robot/simpler-robot/compare/v2.3.4..v2.3.5)

- [`2d752df55`](https://github.com/simple-robot/simpler-robot/commit/2d752df55): update to v2.3.5 尝试使用另一种办法解决mirai 下login异常时 logback 堆栈溢出问题。
- [`eec05b038..5de6faa50`](https://github.com/simple-robot/simpler-robot/compare/eec05b038..2d752df55): WOW! new logo!
- [`5ec9e92b9..7d93e1d4e`](https://github.com/simple-robot/simpler-robot/compare/5ec9e92b9..5de6faa50): logger test

## v2.3.4

> Release & Pull Notes: [v2.3.4](https://github.com/simple-robot/simpler-robot/releases/tag/v2.3.4)
>
> Commit compare: [v2.3.3..v2.3.4](https://github.com/simple-robot/simpler-robot/compare/v2.3.3..v2.3.4)

- [`a9efc2123`](https://github.com/simple-robot/simpler-robot/commit/a9efc2123): Update to v2.3.4
- [`86ab31cd2`](https://github.com/simple-robot/simpler-robot/commit/86ab31cd2): fix #143
- [`a17fe3793`](https://github.com/simple-robot/simpler-robot/commit/a17fe3793): README for 3.0.0

## v2.3.3

> Release & Pull Notes: [v2.3.3](https://github.com/simple-robot/simpler-robot/releases/tag/v2.3.3)
>
> Commit compare: [v2.3.2..v2.3.3](https://github.com/simple-robot/simpler-robot/compare/v2.3.2..v2.3.3)

- [`7e2965395`](https://github.com/simple-robot/simpler-robot/commit/7e2965395): update to v2.3.3

## v2.3.2

> Release & Pull Notes: [v2.3.2](https://github.com/simple-robot/simpler-robot/releases/tag/v2.3.2)
>
> Commit compare: [v2.3.1..v2.3.2](https://github.com/simple-robot/simpler-robot/compare/v2.3.1..v2.3.2)

- [`97b814b19`](https://github.com/simple-robot/simpler-robot/commit/97b814b19): Update to v2.3.2
- [`50a4d3d34`](https://github.com/simple-robot/simpler-robot/commit/50a4d3d34): fix #182

## v2.3.1

> Release & Pull Notes: [v2.3.1](https://github.com/simple-robot/simpler-robot/releases/tag/v2.3.1)
>
> Commit compare: [v2.3.0..v2.3.1](https://github.com/simple-robot/simpler-robot/compare/v2.3.0..v2.3.1)

- [`68e494548`](https://github.com/simple-robot/simpler-robot/commit/68e494548): Update to v2.3.1
- [`6e23dd218`](https://github.com/simple-robot/simpler-robot/commit/6e23dd218): Update workflow
- [`310c0c16f`](https://github.com/simple-robot/simpler-robot/commit/310c0c16f): Update to v2.3.0-DEV.1
- [`afea1c755`](https://github.com/simple-robot/simpler-robot/commit/afea1c755): 更新工作流
- [`1b4843c05`](https://github.com/simple-robot/simpler-robot/commit/1b4843c05): - mirai组件: 更新新的群文件相关API
- [`3dc49a42e`](https://github.com/simple-robot/simpler-robot/commit/3dc49a42e): - mirai组件: 更新新的群文件API
- [`595f93c16..714e838e7`](https://github.com/simple-robot/simpler-robot/compare/595f93c16..3dc49a42e): - mirai组件: 移除弃用配置项
- [`fc4cea12b`](https://github.com/simple-robot/simpler-robot/commit/fc4cea12b): - mirai组件支持 群解散消息事件 (BotLeaveEvent.Disband)
- [`2ef8c6eb0`](https://github.com/simple-robot/simpler-robot/commit/2ef8c6eb0): Mirai版本更新; 追加Image属性
- [`e3c9dd4f0..c85cdcd49`](https://github.com/simple-robot/simpler-robot/compare/e3c9dd4f0..2ef8c6eb0): 开黑啦 README

## v2.3.0

> Release & Pull Notes: [v2.3.0](https://github.com/simple-robot/simpler-robot/releases/tag/v2.3.0)
>
> Commit compare: [v2.3.0-BETA.6..v2.3.0](https://github.com/simple-robot/simpler-robot/compare/v2.3.0-BETA.6..v2.3.0)

- [`48aec73ca`](https://github.com/simple-robot/simpler-robot/commit/48aec73ca): Update version to v2.3.0

## v2.3.0-BETA.6

> Release & Pull Notes: [v2.3.0-BETA.6](https://github.com/simple-robot/simpler-robot/releases/tag/v2.3.0-BETA.6)
>
> Commit compare: [v2.3.0-BETA.5..v2.3.0-BETA.6](https://github.com/simple-robot/simpler-robot/compare/v2.3.0-BETA.5..v2.3.0-BETA.6)

- [`2c95254f5`](https://github.com/simple-robot/simpler-robot/commit/2c95254f5): Update version to v2.3.0-BETA.6
- [`07b3f8100`](https://github.com/simple-robot/simpler-robot/commit/07b3f8100): Support for #179 Close #179

## v2.3.0-BETA.5

> Release & Pull Notes: [v2.3.0-BETA.5](https://github.com/simple-robot/simpler-robot/releases/tag/v2.3.0-BETA.5)
>
> Commit compare: [v.2.3.0-BETA.4..v2.3.0-BETA.5](https://github.com/simple-robot/simpler-robot/compare/v.2.3.0-BETA.4..v2.3.0-BETA.5)

- [`eb58f59ce`](https://github.com/simple-robot/simpler-robot/commit/eb58f59ce): Update version to v2.3.0-BETA.5
- [`c81579cb8`](https://github.com/simple-robot/simpler-robot/commit/c81579cb8): Fix #177 在 v2.3.0-BETA.4 中出现的新问题
- [`b4f168cdd`](https://github.com/simple-robot/simpler-robot/commit/b4f168cdd): Update version to v.2.3.0-BETA.4
- [`b50ab5439`](https://github.com/simple-robot/simpler-robot/commit/b50ab5439): README.md
- [`4fa2f6629..723ba4ebb`](https://github.com/simple-robot/simpler-robot/compare/4fa2f6629..b50ab5439): Tips.
- [`ebf149ea1`](https://github.com/simple-robot/simpler-robot/commit/ebf149ea1): Update tips.
- [`0087a84e8`](https://github.com/simple-robot/simpler-robot/commit/0087a84e8): Remove some .idea files
- [`f062bf013`](https://github.com/simple-robot/simpler-robot/commit/f062bf013): :see_no_evil: Adding or updating a .gitignore file.
- [`dda0bb667..61d776825`](https://github.com/simple-robot/simpler-robot/compare/dda0bb667..f062bf013): For .idea file

## v.2.3.0-BETA.4

> Release & Pull Notes: [v.2.3.0-BETA.4](https://github.com/simple-robot/simpler-robot/releases/tag/v.2.3.0-BETA.4)
>
> Commit compare: [v2.3.0-BETA.3..v.2.3.0-BETA.4](https://github.com/simple-robot/simpler-robot/compare/v2.3.0-BETA.3..v.2.3.0-BETA.4)

- [`fe8344937`](https://github.com/simple-robot/simpler-robot/commit/fe8344937): Update version to v.2.3.0-BETA.4
- [`715aab16e..9de03c771`](https://github.com/simple-robot/simpler-robot/compare/715aab16e..fe8344937): Fix #176. Close #176
- [`7ec8f5d94`](https://github.com/simple-robot/simpler-robot/commit/7ec8f5d94): Fix #177 Close #177

## v2.3.0-BETA.3

> Release & Pull Notes: [v2.3.0-BETA.3](https://github.com/simple-robot/simpler-robot/releases/tag/v2.3.0-BETA.3)
>
> Commit compare: [v2.3.0-BETA.2..v2.3.0-BETA.3](https://github.com/simple-robot/simpler-robot/compare/v2.3.0-BETA.2..v2.3.0-BETA.3)

- [`9e4c23ab4`](https://github.com/simple-robot/simpler-robot/commit/9e4c23ab4): Update version v2.3.0-BETA.3
- [`eecb8c06c`](https://github.com/simple-robot/simpler-robot/commit/eecb8c06c): Fix #175 close #175
- [`7ad999495`](https://github.com/simple-robot/simpler-robot/commit/7ad999495): test

## v2.3.0-BETA.2

> Release & Pull Notes: [v2.3.0-BETA.2](https://github.com/simple-robot/simpler-robot/releases/tag/v2.3.0-BETA.2)
>
> Commit compare: [v2.3.0-BETA.1..v2.3.0-BETA.2](https://github.com/simple-robot/simpler-robot/compare/v2.3.0-BETA.1..v2.3.0-BETA.2)

- [`925d7dcc4`](https://github.com/simple-robot/simpler-robot/commit/925d7dcc4): Async func for sender and setter.
- [`e492600f9`](https://github.com/simple-robot/simpler-robot/commit/e492600f9): Mirai sender getter setter update and update version to v2.3.0-BETA.2
- [`1a5b61be1`](https://github.com/simple-robot/simpler-robot/commit/1a5b61be1): README.md

## v2.3.0-BETA.1

> Release & Pull Notes: [v2.3.0-BETA.1](https://github.com/simple-robot/simpler-robot/releases/tag/v2.3.0-BETA.1)
>
> Commit compare: [v2.3.0-ALPHA.6..v2.3.0-BETA.1](https://github.com/simple-robot/simpler-robot/compare/v2.3.0-ALPHA.6..v2.3.0-BETA.1)

- [`4b59caf9c`](https://github.com/simple-robot/simpler-robot/commit/4b59caf9c): for v2.3.0-BETA.1 and kaiheila component v0.0.1-PREVIEW
- [`3ea9cfb03`](https://github.com/simple-robot/simpler-robot/commit/3ea9cfb03): Khl for simbot v0.0.1-PREVIEW
- [`6ed01cd4d`](https://github.com/simple-robot/simpler-robot/commit/6ed01cd4d): Update khl README.md
- [`09668c30a`](https://github.com/simple-robot/simpler-robot/commit/09668c30a): Edit some
- [`258640550`](https://github.com/simple-robot/simpler-robot/commit/258640550): Khl v3 api component
- [`d77022084`](https://github.com/simple-robot/simpler-robot/commit/d77022084): Rename for kaiheila module package
- [`941ab2a2e`](https://github.com/simple-robot/simpler-robot/commit/941ab2a2e): Normal event and test
- [`8b9c15826`](https://github.com/simple-robot/simpler-robot/commit/8b9c15826): Bot Listener
- [`932877b6f`](https://github.com/simple-robot/simpler-robot/commit/932877b6f): Text event
- [`61c42cb1a`](https://github.com/simple-robot/simpler-robot/commit/61c42cb1a): Getters
- [`df5bdeb95`](https://github.com/simple-robot/simpler-robot/commit/df5bdeb95): Khl Senders and getters
- [`72ebc992e..0c9b95fc4`](https://github.com/simple-robot/simpler-robot/compare/72ebc992e..df5bdeb95): For getter suspend function
- [`dba0c0859`](https://github.com/simple-robot/simpler-robot/commit/dba0c0859): README.md
- [`4d05b1aec`](https://github.com/simple-robot/simpler-robot/commit/4d05b1aec): Setter for suspend fun
- [`564ed7420`](https://github.com/simple-robot/simpler-robot/commit/564ed7420): Delete some test files.
- [`461f59e1b..699cabd80`](https://github.com/simple-robot/simpler-robot/compare/461f59e1b..564ed7420): Khl setters
- [`cb4f7e1cf`](https://github.com/simple-robot/simpler-robot/commit/cb4f7e1cf): Bot
- [`7f3e41617..eee8c7fef`](https://github.com/simple-robot/simpler-robot/compare/7f3e41617..cb4f7e1cf): Khl component.
- [`e58025af4`](https://github.com/simple-robot/simpler-robot/commit/e58025af4): Message Events.
- [`8ce8fac24`](https://github.com/simple-robot/simpler-robot/commit/8ce8fac24): Video events and image events
- [`db19cb52a`](https://github.com/simple-robot/simpler-robot/commit/db19cb52a): Message Events
- [`3838dabfc`](https://github.com/simple-robot/simpler-robot/commit/3838dabfc): Kaiheila
- [`b735d8965`](https://github.com/simple-robot/simpler-robot/commit/b735d8965): Tips.
- [`cbb528511..09903e7fb`](https://github.com/simple-robot/simpler-robot/compare/cbb528511..b735d8965): Update .ignore file and remove some .idea file.
- [`6128145d6`](https://github.com/simple-robot/simpler-robot/commit/6128145d6): For some .idea file
- [`525b1df9b`](https://github.com/simple-robot/simpler-robot/commit/525b1df9b): Session's Waiting, 优化超时 For some .idea file
- [`4e757020c..1da317f13`](https://github.com/simple-robot/simpler-robot/compare/4e757020c..525b1df9b): For some .idea file
- [`901ab2e00`](https://github.com/simple-robot/simpler-robot/commit/901ab2e00): update .idea files
- [`624f539e0..962b297a2`](https://github.com/simple-robot/simpler-robot/compare/624f539e0..901ab2e00): Guild role events and guild member events.
- [`d212e0191`](https://github.com/simple-robot/simpler-robot/commit/d212e0191): Private message events
- [`4b12dc2b1`](https://github.com/simple-robot/simpler-robot/commit/4b12dc2b1): Message events
- [`65cfbbf2a..e20506c48`](https://github.com/simple-robot/simpler-robot/compare/65cfbbf2a..4b12dc2b1): Guild event extra bodys.
- [`b51dfd007`](https://github.com/simple-robot/simpler-robot/commit/b51dfd007): mirai合并转发消息
- [`b16f454dc`](https://github.com/simple-robot/simpler-robot/commit/b16f454dc): Event Locator
- [`7280008bc`](https://github.com/simple-robot/simpler-robot/commit/7280008bc): Events for user event
- [`1f59ee433`](https://github.com/simple-robot/simpler-robot/commit/1f59ee433): Update pom
- [`44ea6ab16`](https://github.com/simple-robot/simpler-robot/commit/44ea6ab16): Update Mirai version to v2.7.1 to fix #159 close #159
- [`1ac0b2883`](https://github.com/simple-robot/simpler-robot/commit/1ac0b2883): pom
- [`e1a09ddcd`](https://github.com/simple-robot/simpler-robot/commit/e1a09ddcd): Guild event for user reaction event.
- [`32a0be2a8`](https://github.com/simple-robot/simpler-robot/commit/32a0be2a8): khl serializer todo
- [`0509e419a`](https://github.com/simple-robot/simpler-robot/commit/0509e419a): Card object
- [`30fdf3962`](https://github.com/simple-robot/simpler-robot/commit/30fdf3962): KMarkdown
- [`c1ba7b828`](https://github.com/simple-robot/simpler-robot/commit/c1ba7b828): object -> objects
- [`133aadbb5`](https://github.com/simple-robot/simpler-robot/commit/133aadbb5): khl event for text
- [`6658d8dbf`](https://github.com/simple-robot/simpler-robot/commit/6658d8dbf): kaiheila event
- [`f4b34c123`](https://github.com/simple-robot/simpler-robot/commit/f4b34c123): intimacy's api
- [`e38a9c418`](https://github.com/simple-robot/simpler-robot/commit/e38a9c418): invite data
- [`a0cafc640`](https://github.com/simple-robot/simpler-robot/commit/a0cafc640): invite api
- [`50a41e1e6`](https://github.com/simple-robot/simpler-robot/commit/50a41e1e6): merge dev-#154-plugins branch

## v2.3.0-ALPHA.6

> Release & Pull Notes: [v2.3.0-ALPHA.6](https://github.com/simple-robot/simpler-robot/releases/tag/v2.3.0-ALPHA.6)
>
> Commit compare: [v2.3.0-ALPHA.5..v2.3.0-ALPHA.6](https://github.com/simple-robot/simpler-robot/compare/v2.3.0-ALPHA.5..v2.3.0-ALPHA.6)

- [`952ddc203`](https://github.com/simple-robot/simpler-robot/commit/952ddc203): Mirai forward message in v2.3.0-ALPHA.6 for #169 close #169
- [`a10ac0f61`](https://github.com/simple-robot/simpler-robot/commit/a10ac0f61): For #169
- [`b015ad9c4`](https://github.com/simple-robot/simpler-robot/commit/b015ad9c4): New branch for support mirai forward message; for #169
- [`2f64fb37e`](https://github.com/simple-robot/simpler-robot/commit/2f64fb37e): 清理pom
- [`81b3f2027..84e1ea830`](https://github.com/simple-robot/simpler-robot/compare/81b3f2027..2f64fb37e): 增加注释

## v2.3.0-ALPHA.5

> Release & Pull Notes: [v2.3.0-ALPHA.5](https://github.com/simple-robot/simpler-robot/releases/tag/v2.3.0-ALPHA.5)
>
> Commit compare: [v.2.3.0-ALPHA.4..v2.3.0-ALPHA.5](https://github.com/simple-robot/simpler-robot/compare/v.2.3.0-ALPHA.4..v2.3.0-ALPHA.5)

- [`9527fcdfd`](https://github.com/simple-robot/simpler-robot/commit/9527fcdfd): 优化会话等待与回调；增加阻塞等待； v2.3.0-ALPHA.5 for #142
- [`3505e1587`](https://github.com/simple-robot/simpler-robot/commit/3505e1587): :zap: 优化持续会话部分内容与日志，清除遗留代码
- [`4349a190a`](https://github.com/simple-robot/simpler-robot/commit/4349a190a): Test file rename
- [`851b1e74c`](https://github.com/simple-robot/simpler-robot/commit/851b1e74c): :art: Improving structure / format of the code.
- [`e16943e1c`](https://github.com/simple-robot/simpler-robot/commit/e16943e1c): BotVerifyInfo提示优化
- [`c4cea6a63`](https://github.com/simple-robot/simpler-robot/commit/c4cea6a63): test

## v.2.3.0-ALPHA.4

> Release & Pull Notes: [v.2.3.0-ALPHA.4](https://github.com/simple-robot/simpler-robot/releases/tag/v.2.3.0-ALPHA.4)
>
> Commit compare: [v2.2.3..v.2.3.0-ALPHA.4](https://github.com/simple-robot/simpler-robot/compare/v2.2.3..v.2.3.0-ALPHA.4)

- [`50b785383`](https://github.com/simple-robot/simpler-robot/commit/50b785383): #142 基础实现完成 in v.2.3.0-ALPHA.4 close #142
- [`4963e9a62`](https://github.com/simple-robot/simpler-robot/commit/4963e9a62): ContinuousSessionScopeContext internal for double map #142
- [`30e55fa6f`](https://github.com/simple-robot/simpler-robot/commit/30e55fa6f): Matcher
- [`fa92a9fa5`](https://github.com/simple-robot/simpler-robot/commit/fa92a9fa5): FileUtil for Session MatchType #142
- [`44741280c`](https://github.com/simple-robot/simpler-robot/commit/44741280c): CoreListenerContextFactory coroutineScope with CoroutineName for #142
- [`8fcdd11c7`](https://github.com/simple-robot/simpler-robot/commit/8fcdd11c7): 作用域: 持续会话 ContinuousSession for #142
- [`379f24092`](https://github.com/simple-robot/simpler-robot/commit/379f24092): 优化 BotVerifyInfo 的相关内容
- [`64122b44a`](https://github.com/simple-robot/simpler-robot/commit/64122b44a): Update Mirai version to v2.7.1
- [`5c0824fc9`](https://github.com/simple-robot/simpler-robot/commit/5c0824fc9): Mirai message Content
- [`ed01315d4`](https://github.com/simple-robot/simpler-robot/commit/ed01315d4): listener manager
- [`aa507bce7`](https://github.com/simple-robot/simpler-robot/commit/aa507bce7): Sender 重构为 suspend 为主的函数 and for version to v2.3.0-ALPHA.2 for #166
- [`fa72e0ed3`](https://github.com/simple-robot/simpler-robot/commit/fa72e0ed3): remove some test file
- [`6b65f94b5`](https://github.com/simple-robot/simpler-robot/commit/6b65f94b5): @Async 优先级更高
- [`ce5f9beda..f8d9bac36`](https://github.com/simple-robot/simpler-robot/compare/ce5f9beda..6b65f94b5): For v2.3.0-ALPHA.1
- [`6cc02ec59`](https://github.com/simple-robot/simpler-robot/commit/6cc02ec59): spare -> isSpare
- [`871a394df`](https://github.com/simple-robot/simpler-robot/commit/871a394df): Async func with spare for #161 plugin warn for #154
- [`37ba38081`](https://github.com/simple-robot/simpler-robot/commit/37ba38081): test pom
- [`4f8956d41..64fd70a79`](https://github.com/simple-robot/simpler-robot/compare/4f8956d41..37ba38081): fix test error
- [`a407592c0`](https://github.com/simple-robot/simpler-robot/commit/a407592c0): CoreListenerManager for #161 close #161
- [`65a78a336`](https://github.com/simple-robot/simpler-robot/commit/65a78a336): definition @Async and modify listenerFunction for #161
- [`ab5150c0b`](https://github.com/simple-robot/simpler-robot/commit/ab5150c0b): fix khl pom
- [`3c9267467..5788e3a6b`](https://github.com/simple-robot/simpler-robot/compare/3c9267467..ab5150c0b): 监听函数构建器 for #154
- [`c14190b27`](https://github.com/simple-robot/simpler-robot/commit/c14190b27): 实现动态更新、删除 for #154
- [`7d1d83bd4`](https://github.com/simple-robot/simpler-robot/commit/7d1d83bd4): listener manager lock
- [`3ba8c7a18`](https://github.com/simple-robot/simpler-robot/commit/3ba8c7a18): URL's resource fix
- [`5af985013`](https://github.com/simple-robot/simpler-robot/commit/5af985013): Url cache false
- [`5a3184da7`](https://github.com/simple-robot/simpler-robot/commit/5a3184da7): cache
- [`f8d909f45`](https://github.com/simple-robot/simpler-robot/commit/f8d909f45): PluginManager File load fix for #154
- [`88dedf1c3`](https://github.com/simple-robot/simpler-robot/commit/88dedf1c3): PluginManager and test for #154
- [`589d19017`](https://github.com/simple-robot/simpler-robot/commit/589d19017): PluginManager for #154
- [`d97520348`](https://github.com/simple-robot/simpler-robot/commit/d97520348): 动态插件管理
- [`344f9edcb`](https://github.com/simple-robot/simpler-robot/commit/344f9edcb): 监听函数管理器与分组管理器
- [`f08ab2b84`](https://github.com/simple-robot/simpler-robot/commit/f08ab2b84): 监听函数管理器调整以及插件读取
- [`5ce0c27ac`](https://github.com/simple-robot/simpler-robot/commit/5ce0c27ac): 动态类加载器
- [`2d5a4ba1a`](https://github.com/simple-robot/simpler-robot/commit/2d5a4ba1a): plugin loader?
- [`9e5597296`](https://github.com/simple-robot/simpler-robot/commit/9e5597296): remove some
- [`803a661b9`](https://github.com/simple-robot/simpler-robot/commit/803a661b9): File Sync
- [`70d09eb2b`](https://github.com/simple-robot/simpler-robot/commit/70d09eb2b): 文件监听器
- [`eddabf672..476fb5426`](https://github.com/simple-robot/simpler-robot/compare/eddabf672..70d09eb2b): Plugin system for #154
- [`15d7e8cb0`](https://github.com/simple-robot/simpler-robot/commit/15d7e8cb0): For invite api
- [`f8beb5be2`](https://github.com/simple-robot/simpler-robot/commit/f8beb5be2): For GuildRole
- [`73edba6de`](https://github.com/simple-robot/simpler-robot/commit/73edba6de): Update ParametersAppender
- [`846c10fac`](https://github.com/simple-robot/simpler-robot/commit/846c10fac): update ApiData
- [`ed270c6d5`](https://github.com/simple-robot/simpler-robot/commit/ed270c6d5): for dependence
- [`3ffdf3658`](https://github.com/simple-robot/simpler-robot/commit/3ffdf3658): For guild role
- [`b90177eaa`](https://github.com/simple-robot/simpler-robot/commit/b90177eaa): Asset
- [`7f7b70e43`](https://github.com/simple-robot/simpler-robot/commit/7f7b70e43): Me、Asset、Direct Message、Guild Role
- [`508ca29f8`](https://github.com/simple-robot/simpler-robot/commit/508ca29f8): Me、Asset、Direct Message
- [`3a092f02a`](https://github.com/simple-robot/simpler-robot/commit/3a092f02a): Update message and direct message
- [`db8614dfb`](https://github.com/simple-robot/simpler-robot/commit/db8614dfb): 用户私聊相关
- [`1dd211af4`](https://github.com/simple-robot/simpler-robot/commit/1dd211af4): 私聊会话相关
- [`661f64831..66ab01138`](https://github.com/simple-robot/simpler-robot/compare/661f64831..1dd211af4): 优化ApiData.Req, 频道消息相关
- [`bc5e88923`](https://github.com/simple-robot/simpler-robot/commit/bc5e88923): update tests and rebase
- [`627e68d07`](https://github.com/simple-robot/simpler-robot/commit/627e68d07): mute
- [`f221c69a2..9fe439c83`](https://github.com/simple-robot/simpler-robot/compare/f221c69a2..627e68d07): for v3 bot
- [`a833f2842`](https://github.com/simple-robot/simpler-robot/commit/a833f2842): v3 bot
- [`4dd1fd611`](https://github.com/simple-robot/simpler-robot/commit/4dd1fd611): test
- [`1af6cb6e0`](https://github.com/simple-robot/simpler-robot/commit/1af6cb6e0): gateway test
- [`940c953d8`](https://github.com/simple-robot/simpler-robot/commit/940c953d8): channel view、create、delete
- [`fa12cc2a6`](https://github.com/simple-robot/simpler-robot/commit/fa12cc2a6): channel list
- [`513d6b7bd`](https://github.com/simple-robot/simpler-robot/commit/513d6b7bd): 频道相关
- [`b4e503bd2`](https://github.com/simple-robot/simpler-robot/commit/b4e503bd2): mute create
- [`9de5dc7d6`](https://github.com/simple-robot/simpler-robot/commit/9de5dc7d6): kaiheila api : - muteList - leave - kickout
- [`84c6815b5`](https://github.com/simple-robot/simpler-robot/commit/84c6815b5): :bulb: 更新注释
- [`59b325cbb..75245c21e`](https://github.com/simple-robot/simpler-robot/compare/59b325cbb..84c6815b5): api message create and test
- [`b919d3e5c`](https://github.com/simple-robot/simpler-robot/commit/b919d3e5c): update module name
- [`ddb08c9ac`](https://github.com/simple-robot/simpler-robot/commit/ddb08c9ac): kaiheila api : message create.
- [`d6b1310dc`](https://github.com/simple-robot/simpler-robot/commit/d6b1310dc): kaiheila api : guild view and test.
- [`2756efbaf`](https://github.com/simple-robot/simpler-robot/commit/2756efbaf): kaiheila api : guild list for test.
- [`333823c3e..a2e878707`](https://github.com/simple-robot/simpler-robot/compare/333823c3e..2756efbaf): ws test
- [`a9047cf26..ff36944b9`](https://github.com/simple-robot/simpler-robot/compare/a9047cf26..a2e878707): api some
- [`46749b400`](https://github.com/simple-robot/simpler-robot/commit/46749b400): gateway api test
- [`955f9ac29`](https://github.com/simple-robot/simpler-robot/commit/955f9ac29): khl gateway api test
- [`b2edaa214`](https://github.com/simple-robot/simpler-robot/commit/b2edaa214): khl api test and rename
- [`c01e04721`](https://github.com/simple-robot/simpler-robot/commit/c01e04721): unit test
- [`a72db88f8..e76bd8205`](https://github.com/simple-robot/simpler-robot/compare/a72db88f8..c01e04721): serializer for guild list api resp.
- [`90626003a..18eaef13c`](https://github.com/simple-robot/simpler-robot/compare/90626003a..e76bd8205): bot info
- [`8270a89b4`](https://github.com/simple-robot/simpler-robot/commit/8270a89b4): api data req base
- [`143996fc5`](https://github.com/simple-robot/simpler-robot/commit/143996fc5): api req builder
- [`f12d8da61`](https://github.com/simple-robot/simpler-robot/commit/f12d8da61): kaiheila api conf
- [`7f6c2d5ad`](https://github.com/simple-robot/simpler-robot/commit/7f6c2d5ad): api data for v3
- [`6ec6d6f20`](https://github.com/simple-robot/simpler-robot/commit/6ec6d6f20): server api for v3
- [`1223bfea3`](https://github.com/simple-robot/simpler-robot/commit/1223bfea3): api实现模块
- [`16f3a972b..fba8c61d1`](https://github.com/simple-robot/simpler-robot/compare/16f3a972b..1223bfea3): update readme
- [`441f2663a`](https://github.com/simple-robot/simpler-robot/commit/441f2663a): update info. move module
- [`79a9cf0c3..52a063b36`](https://github.com/simple-robot/simpler-robot/compare/79a9cf0c3..441f2663a): Text event extra
- [`354abc6f7`](https://github.com/simple-robot/simpler-robot/commit/354abc6f7): kmarkdown
- [`d85316e20`](https://github.com/simple-robot/simpler-robot/commit/d85316e20): guild 序列化
- [`1fea4917b`](https://github.com/simple-robot/simpler-robot/commit/1fea4917b): 信令测试
- [`7d81cec84`](https://github.com/simple-robot/simpler-robot/commit/7d81cec84): 信令定义
- [`6b92462cc`](https://github.com/simple-robot/simpler-robot/commit/6b92462cc): 开黑啦 信令
- [`df62f8b8e`](https://github.com/simple-robot/simpler-robot/commit/df62f8b8e): 开黑啦 objects定义 link #91
- [`63b9f7296`](https://github.com/simple-robot/simpler-robot/commit/63b9f7296): 开黑啦bot组件 objects
- [`e68b9892f`](https://github.com/simple-robot/simpler-robot/commit/e68b9892f): 开黑啦bot组件 ktx json
- [`af3edd3a8`](https://github.com/simple-robot/simpler-robot/commit/af3edd3a8): 开黑啦bot组件分支init
- [`0cf769992`](https://github.com/simple-robot/simpler-robot/commit/0cf769992): mute
- [`c6c531003..bfa50eb66`](https://github.com/simple-robot/simpler-robot/compare/c6c531003..0cf769992): for v3 bot
- [`70a9525c5`](https://github.com/simple-robot/simpler-robot/commit/70a9525c5): v3 bot
- [`a7933543e`](https://github.com/simple-robot/simpler-robot/commit/a7933543e): test
- [`5a915d935`](https://github.com/simple-robot/simpler-robot/commit/5a915d935): gateway test
- [`d1cb8bb15`](https://github.com/simple-robot/simpler-robot/commit/d1cb8bb15): channel view、create、delete
- [`1592eedb1`](https://github.com/simple-robot/simpler-robot/commit/1592eedb1): channel list
- [`39d762906`](https://github.com/simple-robot/simpler-robot/commit/39d762906): 频道相关
- [`7a9bcef7f..79c15573e`](https://github.com/simple-robot/simpler-robot/compare/7a9bcef7f..39d762906): add
- [`65ecfae21`](https://github.com/simple-robot/simpler-robot/commit/65ecfae21): mute create
- [`09a531702`](https://github.com/simple-robot/simpler-robot/commit/09a531702): kaiheila api : - muteList - leave - kickout
- [`7d93e6cbe`](https://github.com/simple-robot/simpler-robot/commit/7d93e6cbe): kaiheila api : nickname and test
- [`0b25be321`](https://github.com/simple-robot/simpler-robot/commit/0b25be321): kaiheila api : nickname.
- [`420112dcc`](https://github.com/simple-robot/simpler-robot/commit/420112dcc): :bulb: 更新注释
- [`10d97c836..d20b7cb63`](https://github.com/simple-robot/simpler-robot/compare/10d97c836..420112dcc): api message create and test
- [`260a5b8d6`](https://github.com/simple-robot/simpler-robot/commit/260a5b8d6): update module name
- [`175c62bc9`](https://github.com/simple-robot/simpler-robot/commit/175c62bc9): kaiheila api : message create.
- [`5433e19b8`](https://github.com/simple-robot/simpler-robot/commit/5433e19b8): kaiheila api : guild view and test.
- [`36cb04d31`](https://github.com/simple-robot/simpler-robot/commit/36cb04d31): kaiheila api : guild list for test.
- [`d95955cdc..769b681a2`](https://github.com/simple-robot/simpler-robot/compare/d95955cdc..36cb04d31): ws test
- [`47525d49d..28b8c84d6`](https://github.com/simple-robot/simpler-robot/compare/47525d49d..769b681a2): api some
- [`8854b3bf4`](https://github.com/simple-robot/simpler-robot/commit/8854b3bf4): gateway api test
- [`039ee2dc5`](https://github.com/simple-robot/simpler-robot/commit/039ee2dc5): khl gateway api test
- [`4d55ec7a2`](https://github.com/simple-robot/simpler-robot/commit/4d55ec7a2): khl api test and rename
- [`f20f13962`](https://github.com/simple-robot/simpler-robot/commit/f20f13962): unit test
- [`fb4986ef4..1e7536dd2`](https://github.com/simple-robot/simpler-robot/compare/fb4986ef4..f20f13962): serializer for guild list api resp.
- [`c96add206..1a9f0a911`](https://github.com/simple-robot/simpler-robot/compare/c96add206..1e7536dd2): bot info
- [`01c42709e`](https://github.com/simple-robot/simpler-robot/commit/01c42709e): api data req base
- [`250873f03`](https://github.com/simple-robot/simpler-robot/commit/250873f03): api req builder
- [`ab8e3d4e4`](https://github.com/simple-robot/simpler-robot/commit/ab8e3d4e4): kaiheila api conf
- [`6d6ca7b0a`](https://github.com/simple-robot/simpler-robot/commit/6d6ca7b0a): api data for v3
- [`a15956674`](https://github.com/simple-robot/simpler-robot/commit/a15956674): server api for v3
- [`f8e5bba87`](https://github.com/simple-robot/simpler-robot/commit/f8e5bba87): api实现模块
- [`ec0174061..940eb67ed`](https://github.com/simple-robot/simpler-robot/compare/ec0174061..f8e5bba87): update readme
- [`edb4b9e93`](https://github.com/simple-robot/simpler-robot/commit/edb4b9e93): readme
- [`9028839f9`](https://github.com/simple-robot/simpler-robot/commit/9028839f9): update info. move module
- [`a2ab1bbb3..c654dffcb`](https://github.com/simple-robot/simpler-robot/compare/a2ab1bbb3..9028839f9): Text event extra
- [`aa795d276`](https://github.com/simple-robot/simpler-robot/commit/aa795d276): kmarkdown
- [`559adb942`](https://github.com/simple-robot/simpler-robot/commit/559adb942): guild 序列化
- [`5d5ac52de`](https://github.com/simple-robot/simpler-robot/commit/5d5ac52de): 信令测试
- [`a615542cd`](https://github.com/simple-robot/simpler-robot/commit/a615542cd): 信令定义
- [`2e12c59ce`](https://github.com/simple-robot/simpler-robot/commit/2e12c59ce): 开黑啦 信令
- [`5e54b9d83`](https://github.com/simple-robot/simpler-robot/commit/5e54b9d83): 开黑啦 objects定义 link #91
- [`29c15ba64`](https://github.com/simple-robot/simpler-robot/commit/29c15ba64): 开黑啦bot组件 objects
- [`acbb13a56`](https://github.com/simple-robot/simpler-robot/commit/acbb13a56): 开黑啦bot组件 ktx json
- [`6d0ef60a8`](https://github.com/simple-robot/simpler-robot/commit/6d0ef60a8): 开黑啦bot组件分支init

## v2.2.3

> Release & Pull Notes: [v2.2.3](https://github.com/simple-robot/simpler-robot/releases/tag/v2.2.3)
>
> Commit compare: [v2.3.0-ALPHA.3..v2.2.3](https://github.com/simple-robot/simpler-robot/compare/v2.3.0-ALPHA.3..v2.2.3)

- [`44ea6ab16`](https://github.com/simple-robot/simpler-robot/commit/44ea6ab16): Update Mirai version to v2.7.1 to fix #159 close #159

## v2.3.0-ALPHA.3

> Release & Pull Notes: [v2.3.0-ALPHA.3](https://github.com/simple-robot/simpler-robot/releases/tag/v2.3.0-ALPHA.3)
>
> Commit compare: [v2.3.0-ALPHA.2..v2.3.0-ALPHA.3](https://github.com/simple-robot/simpler-robot/compare/v2.3.0-ALPHA.2..v2.3.0-ALPHA.3)

- [`64122b44a`](https://github.com/simple-robot/simpler-robot/commit/64122b44a): Update Mirai version to v2.7.1
- [`5c0824fc9`](https://github.com/simple-robot/simpler-robot/commit/5c0824fc9): Mirai message Content
- [`ed01315d4`](https://github.com/simple-robot/simpler-robot/commit/ed01315d4): listener manager
- [`de0a2cc53`](https://github.com/simple-robot/simpler-robot/commit/de0a2cc53): update logo show
- [`0c2fa8cb3`](https://github.com/simple-robot/simpler-robot/commit/0c2fa8cb3): New banner show with version info
- [`35b9bde2b`](https://github.com/simple-robot/simpler-robot/commit/35b9bde2b): new Logo
- [`446e3406c`](https://github.com/simple-robot/simpler-robot/commit/446e3406c): fix #163 in v2.2.2 close #163

## v2.3.0-ALPHA.2

> Release & Pull Notes: [v2.3.0-ALPHA.2](https://github.com/simple-robot/simpler-robot/releases/tag/v2.3.0-ALPHA.2)
>
> Commit compare: [v2.2.2..v2.3.0-ALPHA.2](https://github.com/simple-robot/simpler-robot/compare/v2.2.2..v2.3.0-ALPHA.2)

- [`aa507bce7`](https://github.com/simple-robot/simpler-robot/commit/aa507bce7): Sender 重构为 suspend 为主的函数 and for version to v2.3.0-ALPHA.2 for #166
- [`fa72e0ed3`](https://github.com/simple-robot/simpler-robot/commit/fa72e0ed3): remove some test file
- [`6b65f94b5`](https://github.com/simple-robot/simpler-robot/commit/6b65f94b5): @Async 优先级更高
- [`ce5f9beda..f8d9bac36`](https://github.com/simple-robot/simpler-robot/compare/ce5f9beda..6b65f94b5): For v2.3.0-ALPHA.1
- [`6cc02ec59`](https://github.com/simple-robot/simpler-robot/commit/6cc02ec59): spare -> isSpare
- [`871a394df`](https://github.com/simple-robot/simpler-robot/commit/871a394df): Async func with spare for #161 plugin warn for #154
- [`37ba38081`](https://github.com/simple-robot/simpler-robot/commit/37ba38081): test pom
- [`4f8956d41..64fd70a79`](https://github.com/simple-robot/simpler-robot/compare/4f8956d41..37ba38081): fix test error
- [`a407592c0`](https://github.com/simple-robot/simpler-robot/commit/a407592c0): CoreListenerManager for #161 close #161
- [`65a78a336`](https://github.com/simple-robot/simpler-robot/commit/65a78a336): definition @Async and modify listenerFunction for #161
- [`ab5150c0b`](https://github.com/simple-robot/simpler-robot/commit/ab5150c0b): fix khl pom
- [`3c9267467..5788e3a6b`](https://github.com/simple-robot/simpler-robot/compare/3c9267467..ab5150c0b): 监听函数构建器 for #154
- [`c14190b27`](https://github.com/simple-robot/simpler-robot/commit/c14190b27): 实现动态更新、删除 for #154
- [`7d1d83bd4`](https://github.com/simple-robot/simpler-robot/commit/7d1d83bd4): listener manager lock
- [`3ba8c7a18`](https://github.com/simple-robot/simpler-robot/commit/3ba8c7a18): URL's resource fix
- [`5af985013`](https://github.com/simple-robot/simpler-robot/commit/5af985013): Url cache false
- [`5a3184da7`](https://github.com/simple-robot/simpler-robot/commit/5a3184da7): cache
- [`f8d909f45`](https://github.com/simple-robot/simpler-robot/commit/f8d909f45): PluginManager File load fix for #154
- [`88dedf1c3`](https://github.com/simple-robot/simpler-robot/commit/88dedf1c3): PluginManager and test for #154
- [`589d19017`](https://github.com/simple-robot/simpler-robot/commit/589d19017): PluginManager for #154
- [`d97520348`](https://github.com/simple-robot/simpler-robot/commit/d97520348): 动态插件管理
- [`344f9edcb`](https://github.com/simple-robot/simpler-robot/commit/344f9edcb): 监听函数管理器与分组管理器
- [`f08ab2b84`](https://github.com/simple-robot/simpler-robot/commit/f08ab2b84): 监听函数管理器调整以及插件读取
- [`5ce0c27ac`](https://github.com/simple-robot/simpler-robot/commit/5ce0c27ac): 动态类加载器
- [`2d5a4ba1a`](https://github.com/simple-robot/simpler-robot/commit/2d5a4ba1a): plugin loader?
- [`9e5597296`](https://github.com/simple-robot/simpler-robot/commit/9e5597296): remove some
- [`803a661b9`](https://github.com/simple-robot/simpler-robot/commit/803a661b9): File Sync
- [`70d09eb2b`](https://github.com/simple-robot/simpler-robot/commit/70d09eb2b): 文件监听器
- [`eddabf672..476fb5426`](https://github.com/simple-robot/simpler-robot/compare/eddabf672..70d09eb2b): Plugin system for #154
- [`dc0f583e4`](https://github.com/simple-robot/simpler-robot/commit/dc0f583e4): New banner show with version info
- [`15d7e8cb0`](https://github.com/simple-robot/simpler-robot/commit/15d7e8cb0): For invite api
- [`f8beb5be2`](https://github.com/simple-robot/simpler-robot/commit/f8beb5be2): For GuildRole
- [`7c8f9a77b`](https://github.com/simple-robot/simpler-robot/commit/7c8f9a77b): new Logo
- [`73edba6de`](https://github.com/simple-robot/simpler-robot/commit/73edba6de): Update ParametersAppender
- [`846c10fac`](https://github.com/simple-robot/simpler-robot/commit/846c10fac): update ApiData
- [`ed270c6d5`](https://github.com/simple-robot/simpler-robot/commit/ed270c6d5): for dependence
- [`3ffdf3658`](https://github.com/simple-robot/simpler-robot/commit/3ffdf3658): For guild role
- [`b90177eaa`](https://github.com/simple-robot/simpler-robot/commit/b90177eaa): Asset
- [`7f7b70e43`](https://github.com/simple-robot/simpler-robot/commit/7f7b70e43): Me、Asset、Direct Message、Guild Role
- [`508ca29f8`](https://github.com/simple-robot/simpler-robot/commit/508ca29f8): Me、Asset、Direct Message
- [`3a092f02a`](https://github.com/simple-robot/simpler-robot/commit/3a092f02a): Update message and direct message
- [`db8614dfb`](https://github.com/simple-robot/simpler-robot/commit/db8614dfb): 用户私聊相关
- [`1dd211af4`](https://github.com/simple-robot/simpler-robot/commit/1dd211af4): 私聊会话相关
- [`661f64831..66ab01138`](https://github.com/simple-robot/simpler-robot/compare/661f64831..1dd211af4): 优化ApiData.Req, 频道消息相关
- [`bc5e88923`](https://github.com/simple-robot/simpler-robot/commit/bc5e88923): update tests and rebase
- [`627e68d07`](https://github.com/simple-robot/simpler-robot/commit/627e68d07): mute
- [`f221c69a2..9fe439c83`](https://github.com/simple-robot/simpler-robot/compare/f221c69a2..627e68d07): for v3 bot
- [`a833f2842`](https://github.com/simple-robot/simpler-robot/commit/a833f2842): v3 bot
- [`4dd1fd611`](https://github.com/simple-robot/simpler-robot/commit/4dd1fd611): test
- [`1af6cb6e0`](https://github.com/simple-robot/simpler-robot/commit/1af6cb6e0): gateway test
- [`940c953d8`](https://github.com/simple-robot/simpler-robot/commit/940c953d8): channel view、create、delete
- [`fa12cc2a6`](https://github.com/simple-robot/simpler-robot/commit/fa12cc2a6): channel list
- [`513d6b7bd`](https://github.com/simple-robot/simpler-robot/commit/513d6b7bd): 频道相关
- [`b4e503bd2`](https://github.com/simple-robot/simpler-robot/commit/b4e503bd2): mute create
- [`9de5dc7d6`](https://github.com/simple-robot/simpler-robot/commit/9de5dc7d6): kaiheila api : - muteList - leave - kickout
- [`84c6815b5`](https://github.com/simple-robot/simpler-robot/commit/84c6815b5): :bulb: 更新注释
- [`59b325cbb..75245c21e`](https://github.com/simple-robot/simpler-robot/compare/59b325cbb..84c6815b5): api message create and test
- [`b919d3e5c`](https://github.com/simple-robot/simpler-robot/commit/b919d3e5c): update module name
- [`ddb08c9ac`](https://github.com/simple-robot/simpler-robot/commit/ddb08c9ac): kaiheila api : message create.
- [`d6b1310dc`](https://github.com/simple-robot/simpler-robot/commit/d6b1310dc): kaiheila api : guild view and test.
- [`2756efbaf`](https://github.com/simple-robot/simpler-robot/commit/2756efbaf): kaiheila api : guild list for test.
- [`333823c3e..a2e878707`](https://github.com/simple-robot/simpler-robot/compare/333823c3e..2756efbaf): ws test
- [`a9047cf26..ff36944b9`](https://github.com/simple-robot/simpler-robot/compare/a9047cf26..a2e878707): api some
- [`46749b400`](https://github.com/simple-robot/simpler-robot/commit/46749b400): gateway api test
- [`955f9ac29`](https://github.com/simple-robot/simpler-robot/commit/955f9ac29): khl gateway api test
- [`b2edaa214`](https://github.com/simple-robot/simpler-robot/commit/b2edaa214): khl api test and rename
- [`c01e04721`](https://github.com/simple-robot/simpler-robot/commit/c01e04721): unit test
- [`a72db88f8..e76bd8205`](https://github.com/simple-robot/simpler-robot/compare/a72db88f8..c01e04721): serializer for guild list api resp.
- [`90626003a..18eaef13c`](https://github.com/simple-robot/simpler-robot/compare/90626003a..e76bd8205): bot info
- [`8270a89b4`](https://github.com/simple-robot/simpler-robot/commit/8270a89b4): api data req base
- [`143996fc5`](https://github.com/simple-robot/simpler-robot/commit/143996fc5): api req builder
- [`f12d8da61`](https://github.com/simple-robot/simpler-robot/commit/f12d8da61): kaiheila api conf
- [`7f6c2d5ad`](https://github.com/simple-robot/simpler-robot/commit/7f6c2d5ad): api data for v3
- [`6ec6d6f20`](https://github.com/simple-robot/simpler-robot/commit/6ec6d6f20): server api for v3
- [`1223bfea3`](https://github.com/simple-robot/simpler-robot/commit/1223bfea3): api实现模块
- [`16f3a972b..fba8c61d1`](https://github.com/simple-robot/simpler-robot/compare/16f3a972b..1223bfea3): update readme
- [`441f2663a`](https://github.com/simple-robot/simpler-robot/commit/441f2663a): update info. move module
- [`79a9cf0c3..52a063b36`](https://github.com/simple-robot/simpler-robot/compare/79a9cf0c3..441f2663a): Text event extra
- [`354abc6f7`](https://github.com/simple-robot/simpler-robot/commit/354abc6f7): kmarkdown
- [`d85316e20`](https://github.com/simple-robot/simpler-robot/commit/d85316e20): guild 序列化
- [`1fea4917b`](https://github.com/simple-robot/simpler-robot/commit/1fea4917b): 信令测试
- [`7d81cec84`](https://github.com/simple-robot/simpler-robot/commit/7d81cec84): 信令定义
- [`6b92462cc`](https://github.com/simple-robot/simpler-robot/commit/6b92462cc): 开黑啦 信令
- [`df62f8b8e`](https://github.com/simple-robot/simpler-robot/commit/df62f8b8e): 开黑啦 objects定义 link #91
- [`63b9f7296`](https://github.com/simple-robot/simpler-robot/commit/63b9f7296): 开黑啦bot组件 objects
- [`e68b9892f`](https://github.com/simple-robot/simpler-robot/commit/e68b9892f): 开黑啦bot组件 ktx json
- [`af3edd3a8`](https://github.com/simple-robot/simpler-robot/commit/af3edd3a8): 开黑啦bot组件分支init
- [`0cf769992`](https://github.com/simple-robot/simpler-robot/commit/0cf769992): mute
- [`c6c531003..bfa50eb66`](https://github.com/simple-robot/simpler-robot/compare/c6c531003..0cf769992): for v3 bot
- [`70a9525c5`](https://github.com/simple-robot/simpler-robot/commit/70a9525c5): v3 bot
- [`a7933543e`](https://github.com/simple-robot/simpler-robot/commit/a7933543e): test
- [`5a915d935`](https://github.com/simple-robot/simpler-robot/commit/5a915d935): gateway test
- [`d1cb8bb15`](https://github.com/simple-robot/simpler-robot/commit/d1cb8bb15): channel view、create、delete
- [`1592eedb1`](https://github.com/simple-robot/simpler-robot/commit/1592eedb1): channel list
- [`39d762906`](https://github.com/simple-robot/simpler-robot/commit/39d762906): 频道相关
- [`7a9bcef7f..79c15573e`](https://github.com/simple-robot/simpler-robot/compare/7a9bcef7f..39d762906): add
- [`65ecfae21`](https://github.com/simple-robot/simpler-robot/commit/65ecfae21): mute create
- [`09a531702`](https://github.com/simple-robot/simpler-robot/commit/09a531702): kaiheila api : - muteList - leave - kickout
- [`7d93e6cbe`](https://github.com/simple-robot/simpler-robot/commit/7d93e6cbe): kaiheila api : nickname and test
- [`0b25be321`](https://github.com/simple-robot/simpler-robot/commit/0b25be321): kaiheila api : nickname.
- [`420112dcc`](https://github.com/simple-robot/simpler-robot/commit/420112dcc): :bulb: 更新注释
- [`10d97c836..d20b7cb63`](https://github.com/simple-robot/simpler-robot/compare/10d97c836..420112dcc): api message create and test
- [`260a5b8d6`](https://github.com/simple-robot/simpler-robot/commit/260a5b8d6): update module name
- [`175c62bc9`](https://github.com/simple-robot/simpler-robot/commit/175c62bc9): kaiheila api : message create.
- [`5433e19b8`](https://github.com/simple-robot/simpler-robot/commit/5433e19b8): kaiheila api : guild view and test.
- [`36cb04d31`](https://github.com/simple-robot/simpler-robot/commit/36cb04d31): kaiheila api : guild list for test.
- [`d95955cdc..769b681a2`](https://github.com/simple-robot/simpler-robot/compare/d95955cdc..36cb04d31): ws test
- [`47525d49d..28b8c84d6`](https://github.com/simple-robot/simpler-robot/compare/47525d49d..769b681a2): api some
- [`8854b3bf4`](https://github.com/simple-robot/simpler-robot/commit/8854b3bf4): gateway api test
- [`039ee2dc5`](https://github.com/simple-robot/simpler-robot/commit/039ee2dc5): khl gateway api test
- [`4d55ec7a2`](https://github.com/simple-robot/simpler-robot/commit/4d55ec7a2): khl api test and rename
- [`f20f13962`](https://github.com/simple-robot/simpler-robot/commit/f20f13962): unit test
- [`fb4986ef4..1e7536dd2`](https://github.com/simple-robot/simpler-robot/compare/fb4986ef4..f20f13962): serializer for guild list api resp.
- [`c96add206..1a9f0a911`](https://github.com/simple-robot/simpler-robot/compare/c96add206..1e7536dd2): bot info
- [`01c42709e`](https://github.com/simple-robot/simpler-robot/commit/01c42709e): api data req base
- [`250873f03`](https://github.com/simple-robot/simpler-robot/commit/250873f03): api req builder
- [`ab8e3d4e4`](https://github.com/simple-robot/simpler-robot/commit/ab8e3d4e4): kaiheila api conf
- [`6d6ca7b0a`](https://github.com/simple-robot/simpler-robot/commit/6d6ca7b0a): api data for v3
- [`a15956674`](https://github.com/simple-robot/simpler-robot/commit/a15956674): server api for v3
- [`f8e5bba87`](https://github.com/simple-robot/simpler-robot/commit/f8e5bba87): api实现模块
- [`ec0174061..940eb67ed`](https://github.com/simple-robot/simpler-robot/compare/ec0174061..f8e5bba87): update readme
- [`edb4b9e93`](https://github.com/simple-robot/simpler-robot/commit/edb4b9e93): readme
- [`9028839f9`](https://github.com/simple-robot/simpler-robot/commit/9028839f9): update info. move module
- [`a2ab1bbb3..c654dffcb`](https://github.com/simple-robot/simpler-robot/compare/a2ab1bbb3..9028839f9): Text event extra
- [`aa795d276`](https://github.com/simple-robot/simpler-robot/commit/aa795d276): kmarkdown
- [`559adb942`](https://github.com/simple-robot/simpler-robot/commit/559adb942): guild 序列化
- [`5d5ac52de`](https://github.com/simple-robot/simpler-robot/commit/5d5ac52de): 信令测试
- [`a615542cd`](https://github.com/simple-robot/simpler-robot/commit/a615542cd): 信令定义
- [`2e12c59ce`](https://github.com/simple-robot/simpler-robot/commit/2e12c59ce): 开黑啦 信令
- [`5e54b9d83`](https://github.com/simple-robot/simpler-robot/commit/5e54b9d83): 开黑啦 objects定义 link #91
- [`29c15ba64`](https://github.com/simple-robot/simpler-robot/commit/29c15ba64): 开黑啦bot组件 objects
- [`acbb13a56`](https://github.com/simple-robot/simpler-robot/commit/acbb13a56): 开黑啦bot组件 ktx json
- [`6d0ef60a8`](https://github.com/simple-robot/simpler-robot/commit/6d0ef60a8): 开黑啦bot组件分支init

## v2.2.2

> Release & Pull Notes: [v2.2.2](https://github.com/simple-robot/simpler-robot/releases/tag/v2.2.2)
>
> Commit compare: [v2.3.0-ALPHA.1..v2.2.2](https://github.com/simple-robot/simpler-robot/compare/v2.3.0-ALPHA.1..v2.2.2)

- [`446e3406c`](https://github.com/simple-robot/simpler-robot/commit/446e3406c): fix #163 in v2.2.2 close #163

## v2.3.0-ALPHA.1

> Release & Pull Notes: [v2.3.0-ALPHA.1](https://github.com/simple-robot/simpler-robot/releases/tag/v2.3.0-ALPHA.1)
>
> Commit compare: [v2.2.1..v2.3.0-ALPHA.1](https://github.com/simple-robot/simpler-robot/compare/v2.2.1..v2.3.0-ALPHA.1)

- [`ce5f9beda`](https://github.com/simple-robot/simpler-robot/commit/ce5f9beda): For v2.3.0-ALPHA.1
- [`6cc02ec59`](https://github.com/simple-robot/simpler-robot/commit/6cc02ec59): spare -> isSpare
- [`871a394df`](https://github.com/simple-robot/simpler-robot/commit/871a394df): Async func with spare for #161 plugin warn for #154
- [`37ba38081`](https://github.com/simple-robot/simpler-robot/commit/37ba38081): test pom
- [`4f8956d41..64fd70a79`](https://github.com/simple-robot/simpler-robot/compare/4f8956d41..37ba38081): fix test error
- [`a407592c0`](https://github.com/simple-robot/simpler-robot/commit/a407592c0): CoreListenerManager for #161 close #161
- [`65a78a336`](https://github.com/simple-robot/simpler-robot/commit/65a78a336): definition @Async and modify listenerFunction for #161
- [`ab5150c0b`](https://github.com/simple-robot/simpler-robot/commit/ab5150c0b): fix khl pom
- [`3c9267467..5788e3a6b`](https://github.com/simple-robot/simpler-robot/compare/3c9267467..ab5150c0b): 监听函数构建器 for #154
- [`c14190b27`](https://github.com/simple-robot/simpler-robot/commit/c14190b27): 实现动态更新、删除 for #154
- [`7d1d83bd4`](https://github.com/simple-robot/simpler-robot/commit/7d1d83bd4): listener manager lock
- [`3ba8c7a18`](https://github.com/simple-robot/simpler-robot/commit/3ba8c7a18): URL's resource fix
- [`5af985013`](https://github.com/simple-robot/simpler-robot/commit/5af985013): Url cache false
- [`5a3184da7`](https://github.com/simple-robot/simpler-robot/commit/5a3184da7): cache
- [`f8d909f45`](https://github.com/simple-robot/simpler-robot/commit/f8d909f45): PluginManager File load fix for #154
- [`88dedf1c3`](https://github.com/simple-robot/simpler-robot/commit/88dedf1c3): PluginManager and test for #154
- [`589d19017`](https://github.com/simple-robot/simpler-robot/commit/589d19017): PluginManager for #154
- [`d97520348`](https://github.com/simple-robot/simpler-robot/commit/d97520348): 动态插件管理
- [`344f9edcb`](https://github.com/simple-robot/simpler-robot/commit/344f9edcb): 监听函数管理器与分组管理器
- [`f08ab2b84`](https://github.com/simple-robot/simpler-robot/commit/f08ab2b84): 监听函数管理器调整以及插件读取
- [`5ce0c27ac`](https://github.com/simple-robot/simpler-robot/commit/5ce0c27ac): 动态类加载器
- [`2d5a4ba1a`](https://github.com/simple-robot/simpler-robot/commit/2d5a4ba1a): plugin loader?
- [`9e5597296`](https://github.com/simple-robot/simpler-robot/commit/9e5597296): remove some
- [`803a661b9`](https://github.com/simple-robot/simpler-robot/commit/803a661b9): File Sync
- [`70d09eb2b`](https://github.com/simple-robot/simpler-robot/commit/70d09eb2b): 文件监听器
- [`eddabf672..476fb5426`](https://github.com/simple-robot/simpler-robot/compare/eddabf672..70d09eb2b): Plugin system for #154
- [`dc0f583e4`](https://github.com/simple-robot/simpler-robot/commit/dc0f583e4): New banner show with version info
- [`15d7e8cb0`](https://github.com/simple-robot/simpler-robot/commit/15d7e8cb0): For invite api
- [`f8beb5be2`](https://github.com/simple-robot/simpler-robot/commit/f8beb5be2): For GuildRole
- [`7c8f9a77b`](https://github.com/simple-robot/simpler-robot/commit/7c8f9a77b): new Logo
- [`73edba6de`](https://github.com/simple-robot/simpler-robot/commit/73edba6de): Update ParametersAppender
- [`846c10fac`](https://github.com/simple-robot/simpler-robot/commit/846c10fac): update ApiData
- [`c7a3bcce7`](https://github.com/simple-robot/simpler-robot/commit/c7a3bcce7): update some opt
- [`57f7564c1`](https://github.com/simple-robot/simpler-robot/commit/57f7564c1): test
- [`ed270c6d5`](https://github.com/simple-robot/simpler-robot/commit/ed270c6d5): for dependence
- [`3ffdf3658`](https://github.com/simple-robot/simpler-robot/commit/3ffdf3658): For guild role
- [`b90177eaa`](https://github.com/simple-robot/simpler-robot/commit/b90177eaa): Asset
- [`7f7b70e43`](https://github.com/simple-robot/simpler-robot/commit/7f7b70e43): Me、Asset、Direct Message、Guild Role
- [`508ca29f8`](https://github.com/simple-robot/simpler-robot/commit/508ca29f8): Me、Asset、Direct Message
- [`3a092f02a`](https://github.com/simple-robot/simpler-robot/commit/3a092f02a): Update message and direct message
- [`db8614dfb`](https://github.com/simple-robot/simpler-robot/commit/db8614dfb): 用户私聊相关
- [`1dd211af4`](https://github.com/simple-robot/simpler-robot/commit/1dd211af4): 私聊会话相关
- [`661f64831..66ab01138`](https://github.com/simple-robot/simpler-robot/compare/661f64831..1dd211af4): 优化ApiData.Req, 频道消息相关
- [`bc5e88923`](https://github.com/simple-robot/simpler-robot/commit/bc5e88923): update tests and rebase
- [`627e68d07`](https://github.com/simple-robot/simpler-robot/commit/627e68d07): mute
- [`f221c69a2..9fe439c83`](https://github.com/simple-robot/simpler-robot/compare/f221c69a2..627e68d07): for v3 bot
- [`a833f2842`](https://github.com/simple-robot/simpler-robot/commit/a833f2842): v3 bot
- [`4dd1fd611`](https://github.com/simple-robot/simpler-robot/commit/4dd1fd611): test
- [`1af6cb6e0`](https://github.com/simple-robot/simpler-robot/commit/1af6cb6e0): gateway test
- [`940c953d8`](https://github.com/simple-robot/simpler-robot/commit/940c953d8): channel view、create、delete
- [`fa12cc2a6`](https://github.com/simple-robot/simpler-robot/commit/fa12cc2a6): channel list
- [`513d6b7bd`](https://github.com/simple-robot/simpler-robot/commit/513d6b7bd): 频道相关
- [`b4e503bd2`](https://github.com/simple-robot/simpler-robot/commit/b4e503bd2): mute create
- [`9de5dc7d6`](https://github.com/simple-robot/simpler-robot/commit/9de5dc7d6): kaiheila api : - muteList - leave - kickout
- [`84c6815b5`](https://github.com/simple-robot/simpler-robot/commit/84c6815b5): :bulb: 更新注释
- [`59b325cbb..75245c21e`](https://github.com/simple-robot/simpler-robot/compare/59b325cbb..84c6815b5): api message create and test
- [`b919d3e5c`](https://github.com/simple-robot/simpler-robot/commit/b919d3e5c): update module name
- [`ddb08c9ac`](https://github.com/simple-robot/simpler-robot/commit/ddb08c9ac): kaiheila api : message create.
- [`d6b1310dc`](https://github.com/simple-robot/simpler-robot/commit/d6b1310dc): kaiheila api : guild view and test.
- [`2756efbaf`](https://github.com/simple-robot/simpler-robot/commit/2756efbaf): kaiheila api : guild list for test.
- [`333823c3e..a2e878707`](https://github.com/simple-robot/simpler-robot/compare/333823c3e..2756efbaf): ws test
- [`a9047cf26..ff36944b9`](https://github.com/simple-robot/simpler-robot/compare/a9047cf26..a2e878707): api some
- [`46749b400`](https://github.com/simple-robot/simpler-robot/commit/46749b400): gateway api test
- [`955f9ac29`](https://github.com/simple-robot/simpler-robot/commit/955f9ac29): khl gateway api test
- [`b2edaa214`](https://github.com/simple-robot/simpler-robot/commit/b2edaa214): khl api test and rename
- [`c01e04721`](https://github.com/simple-robot/simpler-robot/commit/c01e04721): unit test
- [`a72db88f8..e76bd8205`](https://github.com/simple-robot/simpler-robot/compare/a72db88f8..c01e04721): serializer for guild list api resp.
- [`90626003a..18eaef13c`](https://github.com/simple-robot/simpler-robot/compare/90626003a..e76bd8205): bot info
- [`8270a89b4`](https://github.com/simple-robot/simpler-robot/commit/8270a89b4): api data req base
- [`143996fc5`](https://github.com/simple-robot/simpler-robot/commit/143996fc5): api req builder
- [`f12d8da61`](https://github.com/simple-robot/simpler-robot/commit/f12d8da61): kaiheila api conf
- [`7f6c2d5ad`](https://github.com/simple-robot/simpler-robot/commit/7f6c2d5ad): api data for v3
- [`6ec6d6f20`](https://github.com/simple-robot/simpler-robot/commit/6ec6d6f20): server api for v3
- [`1223bfea3`](https://github.com/simple-robot/simpler-robot/commit/1223bfea3): api实现模块
- [`16f3a972b..fba8c61d1`](https://github.com/simple-robot/simpler-robot/compare/16f3a972b..1223bfea3): update readme
- [`441f2663a`](https://github.com/simple-robot/simpler-robot/commit/441f2663a): update info. move module
- [`79a9cf0c3..52a063b36`](https://github.com/simple-robot/simpler-robot/compare/79a9cf0c3..441f2663a): Text event extra
- [`354abc6f7`](https://github.com/simple-robot/simpler-robot/commit/354abc6f7): kmarkdown
- [`d85316e20`](https://github.com/simple-robot/simpler-robot/commit/d85316e20): guild 序列化
- [`1fea4917b`](https://github.com/simple-robot/simpler-robot/commit/1fea4917b): 信令测试
- [`7d81cec84`](https://github.com/simple-robot/simpler-robot/commit/7d81cec84): 信令定义
- [`6b92462cc`](https://github.com/simple-robot/simpler-robot/commit/6b92462cc): 开黑啦 信令
- [`df62f8b8e`](https://github.com/simple-robot/simpler-robot/commit/df62f8b8e): 开黑啦 objects定义 link #91
- [`63b9f7296`](https://github.com/simple-robot/simpler-robot/commit/63b9f7296): 开黑啦bot组件 objects
- [`e68b9892f`](https://github.com/simple-robot/simpler-robot/commit/e68b9892f): 开黑啦bot组件 ktx json
- [`af3edd3a8`](https://github.com/simple-robot/simpler-robot/commit/af3edd3a8): 开黑啦bot组件分支init
- [`0cf769992`](https://github.com/simple-robot/simpler-robot/commit/0cf769992): mute
- [`c6c531003..bfa50eb66`](https://github.com/simple-robot/simpler-robot/compare/c6c531003..0cf769992): for v3 bot
- [`70a9525c5`](https://github.com/simple-robot/simpler-robot/commit/70a9525c5): v3 bot
- [`a7933543e`](https://github.com/simple-robot/simpler-robot/commit/a7933543e): test
- [`5a915d935`](https://github.com/simple-robot/simpler-robot/commit/5a915d935): gateway test
- [`d1cb8bb15`](https://github.com/simple-robot/simpler-robot/commit/d1cb8bb15): channel view、create、delete
- [`1592eedb1`](https://github.com/simple-robot/simpler-robot/commit/1592eedb1): channel list
- [`39d762906`](https://github.com/simple-robot/simpler-robot/commit/39d762906): 频道相关
- [`7a9bcef7f..79c15573e`](https://github.com/simple-robot/simpler-robot/compare/7a9bcef7f..39d762906): add
- [`65ecfae21`](https://github.com/simple-robot/simpler-robot/commit/65ecfae21): mute create
- [`09a531702`](https://github.com/simple-robot/simpler-robot/commit/09a531702): kaiheila api : - muteList - leave - kickout
- [`7d93e6cbe`](https://github.com/simple-robot/simpler-robot/commit/7d93e6cbe): kaiheila api : nickname and test
- [`0b25be321`](https://github.com/simple-robot/simpler-robot/commit/0b25be321): kaiheila api : nickname.
- [`420112dcc`](https://github.com/simple-robot/simpler-robot/commit/420112dcc): :bulb: 更新注释
- [`10d97c836..d20b7cb63`](https://github.com/simple-robot/simpler-robot/compare/10d97c836..420112dcc): api message create and test
- [`260a5b8d6`](https://github.com/simple-robot/simpler-robot/commit/260a5b8d6): update module name
- [`175c62bc9`](https://github.com/simple-robot/simpler-robot/commit/175c62bc9): kaiheila api : message create.
- [`5433e19b8`](https://github.com/simple-robot/simpler-robot/commit/5433e19b8): kaiheila api : guild view and test.
- [`36cb04d31`](https://github.com/simple-robot/simpler-robot/commit/36cb04d31): kaiheila api : guild list for test.
- [`d95955cdc..769b681a2`](https://github.com/simple-robot/simpler-robot/compare/d95955cdc..36cb04d31): ws test
- [`47525d49d..28b8c84d6`](https://github.com/simple-robot/simpler-robot/compare/47525d49d..769b681a2): api some
- [`8854b3bf4`](https://github.com/simple-robot/simpler-robot/commit/8854b3bf4): gateway api test
- [`039ee2dc5`](https://github.com/simple-robot/simpler-robot/commit/039ee2dc5): khl gateway api test
- [`4d55ec7a2`](https://github.com/simple-robot/simpler-robot/commit/4d55ec7a2): khl api test and rename
- [`f20f13962`](https://github.com/simple-robot/simpler-robot/commit/f20f13962): unit test
- [`fb4986ef4..1e7536dd2`](https://github.com/simple-robot/simpler-robot/compare/fb4986ef4..f20f13962): serializer for guild list api resp.
- [`c96add206..1a9f0a911`](https://github.com/simple-robot/simpler-robot/compare/c96add206..1e7536dd2): bot info
- [`01c42709e`](https://github.com/simple-robot/simpler-robot/commit/01c42709e): api data req base
- [`250873f03`](https://github.com/simple-robot/simpler-robot/commit/250873f03): api req builder
- [`ab8e3d4e4`](https://github.com/simple-robot/simpler-robot/commit/ab8e3d4e4): kaiheila api conf
- [`6d6ca7b0a`](https://github.com/simple-robot/simpler-robot/commit/6d6ca7b0a): api data for v3
- [`a15956674`](https://github.com/simple-robot/simpler-robot/commit/a15956674): server api for v3
- [`f8e5bba87`](https://github.com/simple-robot/simpler-robot/commit/f8e5bba87): api实现模块
- [`ec0174061..940eb67ed`](https://github.com/simple-robot/simpler-robot/compare/ec0174061..f8e5bba87): update readme
- [`edb4b9e93`](https://github.com/simple-robot/simpler-robot/commit/edb4b9e93): readme
- [`9028839f9`](https://github.com/simple-robot/simpler-robot/commit/9028839f9): update info. move module
- [`a2ab1bbb3..c654dffcb`](https://github.com/simple-robot/simpler-robot/compare/a2ab1bbb3..9028839f9): Text event extra
- [`aa795d276`](https://github.com/simple-robot/simpler-robot/commit/aa795d276): kmarkdown
- [`559adb942`](https://github.com/simple-robot/simpler-robot/commit/559adb942): guild 序列化
- [`5d5ac52de`](https://github.com/simple-robot/simpler-robot/commit/5d5ac52de): 信令测试
- [`a615542cd`](https://github.com/simple-robot/simpler-robot/commit/a615542cd): 信令定义
- [`2e12c59ce`](https://github.com/simple-robot/simpler-robot/commit/2e12c59ce): 开黑啦 信令
- [`5e54b9d83`](https://github.com/simple-robot/simpler-robot/commit/5e54b9d83): 开黑啦 objects定义 link #91
- [`29c15ba64`](https://github.com/simple-robot/simpler-robot/commit/29c15ba64): 开黑啦bot组件 objects
- [`acbb13a56`](https://github.com/simple-robot/simpler-robot/commit/acbb13a56): 开黑啦bot组件 ktx json
- [`6d0ef60a8`](https://github.com/simple-robot/simpler-robot/commit/6d0ef60a8): 开黑啦bot组件分支init

## v2.2.1

> Release & Pull Notes: [v2.2.1](https://github.com/simple-robot/simpler-robot/releases/tag/v2.2.1)
>
> Commit compare: [v2.2.0..v2.2.1](https://github.com/simple-robot/simpler-robot/compare/v2.2.0..v2.2.1)

- [`1cdb59001`](https://github.com/simple-robot/simpler-robot/commit/1cdb59001): for v2.2.1 and update mirai to v2.7.0
- [`9cc0545f2`](https://github.com/simple-robot/simpler-robot/commit/9cc0545f2): mirai 2.7.0 and fix for MiraiMessageParser.kt
- [`d5e261064`](https://github.com/simple-robot/simpler-robot/commit/d5e261064): Update README.md
- [`2a0396a4c`](https://github.com/simple-robot/simpler-robot/commit/2a0396a4c): For v2.2.0 releases

## v2.2.0

> Release & Pull Notes: [v2.2.0](https://github.com/simple-robot/simpler-robot/releases/tag/v2.2.0)
>
> Commit compare: [v2.2.0-BETA.4..v2.2.0](https://github.com/simple-robot/simpler-robot/compare/v2.2.0-BETA.4..v2.2.0)

- [`e6825dd92`](https://github.com/simple-robot/simpler-robot/commit/e6825dd92): for v2.2.0-BETA.4

## v2.2.0-BETA.4

> Release & Pull Notes: [v2.2.0-BETA.4](https://github.com/simple-robot/simpler-robot/releases/tag/v2.2.0-BETA.4)
>
> Commit compare: [v2.2.0-BETA.3..v2.2.0-BETA.4](https://github.com/simple-robot/simpler-robot/compare/v2.2.0-BETA.3..v2.2.0-BETA.4)

- [`edf2eba40`](https://github.com/simple-robot/simpler-robot/commit/edf2eba40): mirai custom event solver for #150 close #150
- [`a6d480da3`](https://github.com/simple-robot/simpler-robot/commit/a6d480da3): for 2.2.0-dev.15
- [`896d8654b`](https://github.com/simple-robot/simpler-robot/commit/896d8654b): Things and Auths for #149; close #149
- [`4b5ee19d7`](https://github.com/simple-robot/simpler-robot/commit/4b5ee19d7): Thing and auth
- [`1881dfdd2`](https://github.com/simple-robot/simpler-robot/commit/1881dfdd2): update GitHub issue template config
- [`dd6ea349e`](https://github.com/simple-robot/simpler-robot/commit/dd6ea349e): Update issue templates
- [`1e137fa3d`](https://github.com/simple-robot/simpler-robot/commit/1e137fa3d): Rename show-my-work to show-my-work.md
- [`7d483f9fb`](https://github.com/simple-robot/simpler-robot/commit/7d483f9fb): Create show-my-work

## v2.2.0-BETA.3

> Release & Pull Notes: [v2.2.0-BETA.3](https://github.com/simple-robot/simpler-robot/releases/tag/v2.2.0-BETA.3)
>
> Commit compare: [v2.2.0-BETA.2..v2.2.0-BETA.3](https://github.com/simple-robot/simpler-robot/compare/v2.2.0-BETA.2..v2.2.0-BETA.3)

- [`51b625c17..c291be091`](https://github.com/simple-robot/simpler-robot/compare/51b625c17..HEAD): for v2.2.0-BETA.3
- [`fe9e33bad`](https://github.com/simple-robot/simpler-robot/commit/fe9e33bad): event launch
- [`5659ad812`](https://github.com/simple-robot/simpler-robot/commit/5659ad812): channel flow
- [`856e73a23`](https://github.com/simple-robot/simpler-robot/commit/856e73a23): try-catch for Image(id)

## v2.2.0-BETA.2

> Release & Pull Notes: [v2.2.0-BETA.2](https://github.com/simple-robot/simpler-robot/releases/tag/v2.2.0-BETA.2)
>
> Commit compare: [v2.2.0-BETA.1..v2.2.0-BETA.2](https://github.com/simple-robot/simpler-robot/compare/v2.2.0-BETA.1..v2.2.0-BETA.2)

- [`882ba6fbd`](https://github.com/simple-robot/simpler-robot/commit/882ba6fbd): for v2.2.0-BETA.2
- [`d11092034`](https://github.com/simple-robot/simpler-robot/commit/d11092034): fix #145 in v2.2.0-DEV.14 close #145
- [`e2264e530`](https://github.com/simple-robot/simpler-robot/commit/e2264e530): fix #145 in v2.2.0-DEV.13 close #145
- [`543d5c940`](https://github.com/simple-robot/simpler-robot/commit/543d5c940): t

## v2.2.0-BETA.1

> Release & Pull Notes: [v2.2.0-BETA.1](https://github.com/simple-robot/simpler-robot/releases/tag/v2.2.0-BETA.1)
>
> Commit compare: [v2.1.1..v2.2.0-BETA.1](https://github.com/simple-robot/simpler-robot/compare/v2.1.1..v2.2.0-BETA.1)

- [`8259a7f9f..ace0cae84`](https://github.com/simple-robot/simpler-robot/compare/8259a7f9f..HEAD): for v2.2.0-DEV.12
- [`2cd706ec7`](https://github.com/simple-robot/simpler-robot/commit/2cd706ec7): fix annotation getter
- [`703a59808`](https://github.com/simple-robot/simpler-robot/commit/703a59808): for v2.2.0-BETA.1
- [`ba266e8f8`](https://github.com/simple-robot/simpler-robot/commit/ba266e8f8): for #144 close #144
- [`266f4f89e`](https://github.com/simple-robot/simpler-robot/commit/266f4f89e): switch test and for v2.2.0-DEV.11
- [`dec67ef17`](https://github.com/simple-robot/simpler-robot/commit/dec67ef17): switch test
- [`0c042e9cc`](https://github.com/simple-robot/simpler-robot/commit/0c042e9cc): switch 注释
- [`48870f9e3`](https://github.com/simple-robot/simpler-robot/commit/48870f9e3): fix 非严格模式下的动态参数注入 & for v2.2.0-DEV.10
- [`95fb3534f`](https://github.com/simple-robot/simpler-robot/commit/95fb3534f): for suspend test
- [`af75295c9`](https://github.com/simple-robot/simpler-robot/commit/af75295c9): test
- [`03214f2f3`](https://github.com/simple-robot/simpler-robot/commit/03214f2f3): listener function switch for #119
- [`9b6fd2ccf`](https://github.com/simple-robot/simpler-robot/commit/9b6fd2ccf): try fix java.io.EOFException and for v2.2.0-DEV.9
- [`d49351336`](https://github.com/simple-robot/simpler-robot/commit/d49351336): for v2.2.0-DEV.8
- [`897cbfc97`](https://github.com/simple-robot/simpler-robot/commit/897cbfc97): annotated filter processor test for #112
- [`9b113c373..87ab8e5d5`](https://github.com/simple-robot/simpler-robot/compare/9b113c373..897cbfc97): annotated filter processor
- [`1e5180d37`](https://github.com/simple-robot/simpler-robot/commit/1e5180d37): for #69
- [`59accb4d6`](https://github.com/simple-robot/simpler-robot/commit/59accb4d6): for v2.2.0-DEV.7
- [`9721930f4`](https://github.com/simple-robot/simpler-robot/commit/9721930f4): 增加注释
- [`8a4104163`](https://github.com/simple-robot/simpler-robot/commit/8a4104163): Implementation via strict mode #139
- [`4c9fbbade`](https://github.com/simple-robot/simpler-robot/commit/4c9fbbade): strict mode for #139
- [`f7e000085`](https://github.com/simple-robot/simpler-robot/commit/f7e000085): for v2.2.0-DEV.6
- [`e4070a994..dc959c2bc`](https://github.com/simple-robot/simpler-robot/compare/e4070a994..f7e000085): suspend listener test
- [`9929586be`](https://github.com/simple-robot/simpler-robot/commit/9929586be): event logger and for v2.2.0-DEV.5
- [`e2aa2a55f`](https://github.com/simple-robot/simpler-robot/commit/e2aa2a55f): fix annotationUtil's bug for v2.2.0-DEV.4
- [`3de2986bb`](https://github.com/simple-robot/simpler-robot/commit/3de2986bb): for v2.2.0-DEV.3
- [`3569468ff`](https://github.com/simple-robot/simpler-robot/commit/3569468ff): for v2.2.0-DEV.2
- [`3f7792e2f`](https://github.com/simple-robot/simpler-robot/commit/3f7792e2f): for v2.2.0-DEV.1
- [`fbb509e2d`](https://github.com/simple-robot/simpler-robot/commit/fbb509e2d): for v2.1.2-DEV.1
- [`8d5b784c7`](https://github.com/simple-robot/simpler-robot/commit/8d5b784c7): move pkg
- [`fa349cd10`](https://github.com/simple-robot/simpler-robot/commit/fa349cd10): build instance for filter with dsl or lambda
- [`cce71f63f`](https://github.com/simple-robot/simpler-robot/commit/cce71f63f): Update test.yml
- [`1fef96305`](https://github.com/simple-robot/simpler-robot/commit/1fef96305): fix lovely cat
- [`15d55d299`](https://github.com/simple-robot/simpler-robot/commit/15d55d299): new listenerFunction and new ListenerFilter for #129 #131 #132 #130 #113 107
- [`a8a24ee82`](https://github.com/simple-robot/simpler-robot/commit/a8a24ee82): lovely cat sb starter
- [`690e71f30`](https://github.com/simple-robot/simpler-robot/commit/690e71f30): Rename .java to .kt
- [`b8df4e3de`](https://github.com/simple-robot/simpler-robot/commit/b8df4e3de): fix for lovely cat
- [`ccd7e6421`](https://github.com/simple-robot/simpler-robot/commit/ccd7e6421): Rename .java to .kt
- [`74eec597a`](https://github.com/simple-robot/simpler-robot/commit/74eec597a): inline
- [`e521d4d12`](https://github.com/simple-robot/simpler-robot/commit/e521d4d12): for #132 #131 #129 #107
- [`c087eec9a`](https://github.com/simple-robot/simpler-robot/commit/c087eec9a): for #128

## v2.1.1

> Release & Pull Notes: [v2.1.1](https://github.com/simple-robot/simpler-robot/releases/tag/v2.1.1)
>
> Commit compare: [v2.1.0..v2.1.1](https://github.com/simple-robot/simpler-robot/compare/v2.1.0..v2.1.1)

- [`3ece5df37`](https://github.com/simple-robot/simpler-robot/commit/3ece5df37): for springboot annotation processor
- [`38f349cb5`](https://github.com/simple-robot/simpler-robot/commit/38f349cb5): tips
- [`c069cf2a4`](https://github.com/simple-robot/simpler-robot/commit/c069cf2a4): for 2.1.1
- [`523385d5c`](https://github.com/simple-robot/simpler-robot/commit/523385d5c): fix mvn err
- [`a94d9364e`](https://github.com/simple-robot/simpler-robot/commit/a94d9364e): add parent for GroupMsg
- [`eecbe8922`](https://github.com/simple-robot/simpler-robot/commit/eecbe8922): 临时移除模块
- [`f647e17ff..40aa3069e`](https://github.com/simple-robot/simpler-robot/compare/f647e17ff..eecbe8922): Update README.md

## v2.1.0

> Release & Pull Notes: [v2.1.0](https://github.com/simple-robot/simpler-robot/releases/tag/v2.1.0)
>
> Commit compare: [v2.1.0-RC.5..v2.1.0](https://github.com/simple-robot/simpler-robot/compare/v2.1.0-RC.5..v2.1.0)

- [`bb58b2f20`](https://github.com/simple-robot/simpler-robot/commit/bb58b2f20): for v2.1.0 release.
- [`43642d116`](https://github.com/simple-robot/simpler-robot/commit/43642d116): for v2.1.0-DEV.10 update ktx-core-jvm version mark todo for MsgProcessor some test
- [`5a733092e`](https://github.com/simple-robot/simpler-robot/commit/5a733092e): Update test.yml

## v2.1.0-RC.5

> Release & Pull Notes: [v2.1.0-RC.5](https://github.com/simple-robot/simpler-robot/releases/tag/v2.1.0-RC.5)
>
> Commit compare: [v2.1.0-RC.4..v2.1.0-RC.5](https://github.com/simple-robot/simpler-robot/compare/v2.1.0-RC.4..v2.1.0-RC.5)

- [`b4417122b..4d0191023`](https://github.com/simple-robot/simpler-robot/compare/b4417122b..HEAD): for v2.1.0-RC.5 and mvn test
- [`c8071a039`](https://github.com/simple-robot/simpler-robot/commit/c8071a039): for v2.1.0-RC.5
- [`22aa57687`](https://github.com/simple-robot/simpler-robot/commit/22aa57687): for v2.1.0-DEV.9
- [`f8fcbb984..83f99bd71`](https://github.com/simple-robot/simpler-robot/compare/f8fcbb984..22aa57687): for v2.1.0-DEV.8

## v2.1.0-RC.4

> Release & Pull Notes: [v2.1.0-RC.4](https://github.com/simple-robot/simpler-robot/releases/tag/v2.1.0-RC.4)
>
> Commit compare: [v2.1.0-RC.3..v2.1.0-RC.4](https://github.com/simple-robot/simpler-robot/compare/v2.1.0-RC.3..v2.1.0-RC.4)

- [`4c38ce2a8`](https://github.com/simple-robot/simpler-robot/commit/4c38ce2a8): for v2.1.0-RC.4
- [`df0e89f8f`](https://github.com/simple-robot/simpler-robot/commit/df0e89f8f): fix warn
- [`309b58a44..6eea3b301`](https://github.com/simple-robot/simpler-robot/compare/309b58a44..df0e89f8f): version to v2.1.0-DEV.7 mirai to 2.6.7
- [`85891b60c`](https://github.com/simple-robot/simpler-robot/commit/85891b60c): for v2.1.0-DEV.6
- [`59236a9fa`](https://github.com/simple-robot/simpler-robot/commit/59236a9fa): Update snapshot.yml
- [`a8ab50319`](https://github.com/simple-robot/simpler-robot/commit/a8ab50319): Update test.yml
- [`627e0778b`](https://github.com/simple-robot/simpler-robot/commit/627e0778b): Update dev-tag-deploy.yml
- [`8584786ae`](https://github.com/simple-robot/simpler-robot/commit/8584786ae): for v2.1.0-DEV.5 尝试修改部分项目目录结构
- [`ec7b66344`](https://github.com/simple-robot/simpler-robot/commit/ec7b66344): change dir tree
- [`80a1e3453`](https://github.com/simple-robot/simpler-robot/commit/80a1e3453): new module for `api-qq`
- [`52fe575c6`](https://github.com/simple-robot/simpler-robot/commit/52fe575c6): try change tree mode
- [`406fd54f2`](https://github.com/simple-robot/simpler-robot/commit/406fd54f2): msg parser
- [`989636733`](https://github.com/simple-robot/simpler-robot/commit/989636733): mirai special event interface.
- [`14159c7f2`](https://github.com/simple-robot/simpler-robot/commit/14159c7f2): more debug info and test
- [`cee623434`](https://github.com/simple-robot/simpler-robot/commit/cee623434): update pom
- [`55719a8ad`](https://github.com/simple-robot/simpler-robot/commit/55719a8ad): rc.3
- [`8d7c1225b`](https://github.com/simple-robot/simpler-robot/commit/8d7c1225b): listener group update
- [`8feb23fed`](https://github.com/simple-robot/simpler-robot/commit/8feb23fed): for v2.1.0-DEV.4
- [`92aec21b2`](https://github.com/simple-robot/simpler-robot/commit/92aec21b2): more info and more containers for account and bot.
- [`1ced7ffae..14c5de415`](https://github.com/simple-robot/simpler-robot/compare/1ced7ffae..92aec21b2): more info and more containers for account and bot. for #126

## v2.1.0-RC.3

> Release & Pull Notes: [v2.1.0-RC.3](https://github.com/simple-robot/simpler-robot/releases/tag/v2.1.0-RC.3)
>
> Commit compare: [v2.1.0-M1..v2.1.0-RC.3](https://github.com/simple-robot/simpler-robot/compare/v2.1.0-M1..v2.1.0-RC.3)

- [`02d12ca42`](https://github.com/simple-robot/simpler-robot/commit/02d12ca42): update pom
- [`2101007e9`](https://github.com/simple-robot/simpler-robot/commit/2101007e9): remove some pom
- [`a9e90cffb`](https://github.com/simple-robot/simpler-robot/commit/a9e90cffb): version to rc.2
- [`13396c45d`](https://github.com/simple-robot/simpler-robot/commit/13396c45d): fix #127
- [`2fb719b93..28dd3f234`](https://github.com/simple-robot/simpler-robot/compare/2fb719b93..13396c45d): update readme

## v2.1.0-M1

> Release & Pull Notes: [v2.1.0-M1](https://github.com/simple-robot/simpler-robot/releases/tag/v2.1.0-M1)
>
> Commit compare: [v2.1.0-BETA.1..v2.1.0-M1](https://github.com/simple-robot/simpler-robot/compare/v2.1.0-BETA.1..v2.1.0-M1)

- [`3e64b0c38`](https://github.com/simple-robot/simpler-robot/commit/3e64b0c38): for v2.1.0-M1
- [`6f458a1e9`](https://github.com/simple-robot/simpler-robot/commit/6f458a1e9): close #124
- [`1e525d81d`](https://github.com/simple-robot/simpler-robot/commit/1e525d81d): for v2.1.0-DEV.3.2
- [`e10616f1c`](https://github.com/simple-robot/simpler-robot/commit/e10616f1c): Update dev-tag-deploy.yml
- [`fba106614`](https://github.com/simple-robot/simpler-robot/commit/fba106614): for v2.1.0-DEV.3.1
- [`fd6e6b22b..efc94313b`](https://github.com/simple-robot/simpler-robot/compare/fd6e6b22b..fba106614): for v2.1.0-DEV.3
- [`58ebe1a4c`](https://github.com/simple-robot/simpler-robot/commit/58ebe1a4c): Update dev-tag-deploy.yml
- [`d4ddf5663`](https://github.com/simple-robot/simpler-robot/commit/d4ddf5663): for v2.1.0-DEV.2
- [`f1a0cc2d5`](https://github.com/simple-robot/simpler-robot/commit/f1a0cc2d5): Create dev-tag-deploy.yml
- [`62642e402`](https://github.com/simple-robot/simpler-robot/commit/62642e402): comment 实现多bot扫描与注册。准备逐步弃用 `simbot.core.bots`
- [`6ae6ae3dd`](https://github.com/simple-robot/simpler-robot/commit/6ae6ae3dd): resources test
- [`e20d66519..632c58a78`](https://github.com/simple-robot/simpler-robot/compare/e20d66519..6ae6ae3dd): Update snapshot.yml
- [`160d4603c`](https://github.com/simple-robot/simpler-robot/commit/160d4603c): Create snapshot.yml
- [`ba22a587f`](https://github.com/simple-robot/simpler-robot/commit/ba22a587f): Update test.yml
- [`51c97f3c2`](https://github.com/simple-robot/simpler-robot/commit/51c97f3c2): test..?
- [`bfcdb6395..070d3e6b6`](https://github.com/simple-robot/simpler-robot/compare/bfcdb6395..51c97f3c2): Update test.yml
- [`9decfd77a..49acc27e7`](https://github.com/simple-robot/simpler-robot/compare/9decfd77a..070d3e6b6): update workflows
- [`cc1f98e87..579ff857d`](https://github.com/simple-robot/simpler-robot/compare/cc1f98e87..49acc27e7): test for projects
- [`d9e63e5e9`](https://github.com/simple-robot/simpler-robot/commit/d9e63e5e9): Update test.yml
- [`9b22aa369`](https://github.com/simple-robot/simpler-robot/commit/9b22aa369): Create test.yml
- [`ef354066e..426b7b440`](https://github.com/simple-robot/simpler-robot/compare/ef354066e..9b22aa369): icon
- [`0479fc10e`](https://github.com/simple-robot/simpler-robot/commit/0479fc10e): resource path expression
- [`337771882`](https://github.com/simple-robot/simpler-robot/commit/337771882): resource path
- [`6d698765c..d86559099`](https://github.com/simple-robot/simpler-robot/compare/6d698765c..337771882): expression
- [`49747b6a2`](https://github.com/simple-robot/simpler-robot/commit/49747b6a2): resource expression test
- [`292f34e5b`](https://github.com/simple-robot/simpler-robot/commit/292f34e5b): resource expression 测试完成 for #68
- [`10610abee..3eda9ce9e`](https://github.com/simple-robot/simpler-robot/compare/10610abee..292f34e5b): resource
- [`bae402fa1`](https://github.com/simple-robot/simpler-robot/commit/bae402fa1): resources path expression
- [`d737cee29`](https://github.com/simple-robot/simpler-robot/commit/d737cee29): reset conf
- [`ae71719ff`](https://github.com/simple-robot/simpler-robot/commit/ae71719ff): 2.1.0-dev.1
- [`9d315769e`](https://github.com/simple-robot/simpler-robot/commit/9d315769e): #125 - simbot.component.mirai.dispatcher.corePoolSize - simbot.component.mirai.dispatcher.maximumPoolSize - simbot.component.mirai.dispatcher.keepAliveTime close #125
- [`332d3275c`](https://github.com/simple-robot/simpler-robot/commit/332d3275c): loggers
- [`07850a3bd`](https://github.com/simple-robot/simpler-robot/commit/07850a3bd): try fix #123 #124
- [`0c43fe87b`](https://github.com/simple-robot/simpler-robot/commit/0c43fe87b): for 2.1.0-dev.1
- [`a67430e37`](https://github.com/simple-robot/simpler-robot/commit/a67430e37): Test
- [`835b60e25..102c4984d`](https://github.com/simple-robot/simpler-robot/compare/835b60e25..a67430e37): Resource path expression
- [`6528f58b3`](https://github.com/simple-robot/simpler-robot/commit/6528f58b3): Resource path expression for #68
- [`ea1e4d533`](https://github.com/simple-robot/simpler-robot/commit/ea1e4d533): kt test junit5
- [`16504a805`](https://github.com/simple-robot/simpler-robot/commit/16504a805): test
- [`62d352161`](https://github.com/simple-robot/simpler-robot/commit/62d352161): file rename
- [`a01d72e30`](https://github.com/simple-robot/simpler-robot/commit/a01d72e30): verifyInfo config

## v2.1.0-BETA.1

> Release & Pull Notes: [v2.1.0-BETA.1](https://github.com/simple-robot/simpler-robot/releases/tag/v2.1.0-BETA.1)
>
> Commit compare: [v2.0.8..v2.1.0-BETA.1](https://github.com/simple-robot/simpler-robot/compare/v2.0.8..v2.1.0-BETA.1)

- [`cae8cf910`](https://github.com/simple-robot/simpler-robot/commit/cae8cf910): forte-common to 1-b.2
- [`19787f942`](https://github.com/simple-robot/simpler-robot/commit/19787f942): for v2.1.0-beta.1
- [`599b77513`](https://github.com/simple-robot/simpler-robot/commit/599b77513): 实现 #122
- [`b9cf780e5..48b1150b5`](https://github.com/simple-robot/simpler-robot/compare/b9cf780e5..599b77513): readme
- [`4231b0586`](https://github.com/simple-robot/simpler-robot/commit/4231b0586): snapshot for v2.0.9
- [`abc99de7c`](https://github.com/simple-robot/simpler-robot/commit/abc99de7c): fix kt opt warn
- [`f48f2709e`](https://github.com/simple-robot/simpler-robot/commit/f48f2709e): for 2.0.9-dev.2
- [`83631c5c1`](https://github.com/simple-robot/simpler-robot/commit/83631c5c1): fix kt opt warn
- [`1cec73443`](https://github.com/simple-robot/simpler-robot/commit/1cec73443): for 2.0.9-dev.2
- [`0c8a8d66a`](https://github.com/simple-robot/simpler-robot/commit/0c8a8d66a): bot register log
- [`2761c80e9`](https://github.com/simple-robot/simpler-robot/commit/2761c80e9): delete unused object
- [`dd4612f60`](https://github.com/simple-robot/simpler-robot/commit/dd4612f60): 监听函数的分组与分组拦截器
- [`270b0d3bd`](https://github.com/simple-robot/simpler-robot/commit/270b0d3bd): 监听函数分组
- [`657ec854a`](https://github.com/simple-robot/simpler-robot/commit/657ec854a): 监听函数分组功能
- [`7c3b8409b`](https://github.com/simple-robot/simpler-robot/commit/7c3b8409b): 重新设计ListenContext并支持对后续监听函数的注入功能。
- [`5b1397683..de1b5b424`](https://github.com/simple-robot/simpler-robot/compare/5b1397683..7c3b8409b): listener context
- [`f444cedfc`](https://github.com/simple-robot/simpler-robot/commit/f444cedfc): tag version for 2.0.9-dev.1
- [`e3c58e227`](https://github.com/simple-robot/simpler-robot/commit/e3c58e227): 清理过时
- [`2a76e4d22`](https://github.com/simple-robot/simpler-robot/commit/2a76e4d22): 尝试解决#118
- [`ac9f73ceb`](https://github.com/simple-robot/simpler-robot/commit/ac9f73ceb): setter update
- [`096ab8044`](https://github.com/simple-robot/simpler-robot/commit/096ab8044): update
- [`0a929d6bd`](https://github.com/simple-robot/simpler-robot/commit/0a929d6bd): 追加注释
- [`0b8705503`](https://github.com/simple-robot/simpler-robot/commit/0b8705503): 移除掉部分多余注解
- [`e76acda66`](https://github.com/simple-robot/simpler-robot/commit/e76acda66): 为操作者、被操作者实现 AccountInfo特性。
- [`295bd69a5`](https://github.com/simple-robot/simpler-robot/commit/295bd69a5): 更新注释
- [`31b336d31..90dff6079`](https://github.com/simple-robot/simpler-robot/compare/31b336d31..295bd69a5): ListenerContext 重构 link #116
- [`c392cb2de`](https://github.com/simple-robot/simpler-robot/commit/c392cb2de): Listener事件流程？
- [`accfc4140`](https://github.com/simple-robot/simpler-robot/commit/accfc4140): update sample doc
- [`b495f0a8e`](https://github.com/simple-robot/simpler-robot/commit/b495f0a8e): springboot configuration metadata fix.
- [`19c8c3669`](https://github.com/simple-robot/simpler-robot/commit/19c8c3669): to v2.0.9 and async to block

## v2.0.8

> Release & Pull Notes: [v2.0.8](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.8)
>
> Commit compare: [v2.0.7..v2.0.8](https://github.com/simple-robot/simpler-robot/compare/v2.0.7..v2.0.8)

- [`d630a5110`](https://github.com/simple-robot/simpler-robot/commit/d630a5110): fix warn
- [`d7511f5d5..882c11196`](https://github.com/simple-robot/simpler-robot/compare/d7511f5d5..d630a5110): try fix #106
- [`092d86ed4`](https://github.com/simple-robot/simpler-robot/commit/092d86ed4): try fix 106
- [`26d0f3b2c`](https://github.com/simple-robot/simpler-robot/commit/26d0f3b2c): hide @JvmDefault
- [`b900c3afb`](https://github.com/simple-robot/simpler-robot/commit/b900c3afb): update kt ktx ktor kotlinx-serialization
- [`110894ee7..a2ffb8867`](https://github.com/simple-robot/simpler-robot/compare/110894ee7..b900c3afb): update readme
- [`a9dd259f1`](https://github.com/simple-robot/simpler-robot/commit/a9dd259f1): update readme info
- [`a88b601a7`](https://github.com/simple-robot/simpler-robot/commit/a88b601a7): 转发消息
- [`8275b7085`](https://github.com/simple-robot/simpler-robot/commit/8275b7085): forward message
- [`55d2f7f4d`](https://github.com/simple-robot/simpler-robot/commit/55d2f7f4d): try update send private msg.
- [`ad0ea3efe`](https://github.com/simple-robot/simpler-robot/commit/ad0ea3efe): try update send group msg.
- [`d3df5aa61`](https://github.com/simple-robot/simpler-robot/commit/d3df5aa61): 注释update
- [`47fae65c0`](https://github.com/simple-robot/simpler-robot/commit/47fae65c0): for BotVerifyInfo
- [`f45b44523`](https://github.com/simple-robot/simpler-robot/commit/f45b44523): BotVerifyInfo 定义、基础实现 link #68
- [`95ccb2154`](https://github.com/simple-robot/simpler-robot/commit/95ccb2154): BotVerifyInfo 定义 link #68
- [`2c8e860bb`](https://github.com/simple-robot/simpler-robot/commit/2c8e860bb): fix #102 for xml cat.
- [`be716dab3`](https://github.com/simple-robot/simpler-robot/commit/be716dab3): update readme
- [`2031b96b8`](https://github.com/simple-robot/simpler-robot/commit/2031b96b8): feat: fix #99
- [`f633fe412`](https://github.com/simple-robot/simpler-robot/commit/f633fe412): update version to 2.0.8?
- [`8042fcab1`](https://github.com/simple-robot/simpler-robot/commit/8042fcab1): implements for #100

## v2.0.7

> Release & Pull Notes: [v2.0.7](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.7)
>
> Commit compare: [v2.0.6..v2.0.7](https://github.com/simple-robot/simpler-robot/compare/v2.0.6..v2.0.7)

- [`3167e1ee2`](https://github.com/simple-robot/simpler-robot/commit/3167e1ee2): fix factory
- [`27fb921b7`](https://github.com/simple-robot/simpler-robot/commit/27fb921b7): quartz comment.
- [`db5973fa9`](https://github.com/simple-robot/simpler-robot/commit/db5973fa9): 警告日志默认送信器； mirai更新到2.6.4
- [`5ea46502b`](https://github.com/simple-robot/simpler-robot/commit/5ea46502b): try fix #98
- [`9059d1245`](https://github.com/simple-robot/simpler-robot/commit/9059d1245): for msg builder
- [`0af35381a`](https://github.com/simple-robot/simpler-robot/commit/0af35381a): update to 2.0.7
- [`485845ad3`](https://github.com/simple-robot/simpler-robot/commit/485845ad3): fix #97. close #97
- [`d1fb6e9d8..0adaf88c1`](https://github.com/simple-robot/simpler-robot/compare/d1fb6e9d8..485845ad3): mirai message builder reimplement
- [`dcf12e441`](https://github.com/simple-robot/simpler-robot/commit/dcf12e441): messageContentBuilder流相关接口方法定义
- [`83092b3b7`](https://github.com/simple-robot/simpler-robot/commit/83092b3b7): #95 相关接口方法定义
- [`a9cc4f6fa`](https://github.com/simple-robot/simpler-robot/commit/a9cc4f6fa): mark annotations

## v2.0.6

> Release & Pull Notes: [v2.0.6](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.6)
>
> Commit compare: [v2.0.6-BETA.5..v2.0.6](https://github.com/simple-robot/simpler-robot/compare/v2.0.6-BETA.5..v2.0.6)

- [`9217154e1`](https://github.com/simple-robot/simpler-robot/commit/9217154e1): 调整RequestGet.flag
- [`bd1b9af2b`](https://github.com/simple-robot/simpler-robot/commit/bd1b9af2b): 调整MessageGet.flag
- [`55ff4a752`](https://github.com/simple-robot/simpler-robot/commit/55ff4a752): test
- [`4db3b3bd9..d9d9e8efa`](https://github.com/simple-robot/simpler-robot/compare/4db3b3bd9..55ff4a752): set Setter's async api to blocking
- [`bbfe07862`](https://github.com/simple-robot/simpler-robot/commit/bbfe07862): additional api interface

## v2.0.6-BETA.5

> Release & Pull Notes: [v2.0.6-BETA.5](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.6-BETA.5)
>
> Commit compare: [v2.0.6-BETA.4..v2.0.6-BETA.5](https://github.com/simple-robot/simpler-robot/compare/v2.0.6-BETA.4..v2.0.6-BETA.5)

- [`09b2b4bd5`](https://github.com/simple-robot/simpler-robot/commit/09b2b4bd5): clean code
- [`ca1edd06d..e1168c09d`](https://github.com/simple-robot/simpler-robot/compare/ca1edd06d..09b2b4bd5): update pom
- [`1a7d14988`](https://github.com/simple-robot/simpler-robot/commit/1a7d14988): update log
- [`111b67a9f`](https://github.com/simple-robot/simpler-robot/commit/111b67a9f): 增加资源获取处理器 for #93
- [`a57224a2e..abf7a6a51`](https://github.com/simple-robot/simpler-robot/compare/a57224a2e..111b67a9f): update readme

## v2.0.6-BETA.4

> Release & Pull Notes: [v2.0.6-BETA.4](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.6-BETA.4)
>
> Commit compare: [v2.0.6-BETA.3..v2.0.6-BETA.4](https://github.com/simple-robot/simpler-robot/compare/v2.0.6-BETA.3..v2.0.6-BETA.4)

- [`7956351b4..109fe5ae7`](https://github.com/simple-robot/simpler-robot/compare/7956351b4..HEAD): 尝试优化 AtDetection 逻辑
- [`74d76abfb..25e6954ec`](https://github.com/simple-robot/simpler-robot/compare/74d76abfb..109fe5ae7): mirai组件环境下, 消息构建器增加部分方法
- [`60d951ea5..ad6e762e0`](https://github.com/simple-robot/simpler-robot/compare/60d951ea5..25e6954ec): mirai下, 消息撤回的序列化
- [`69f2402b9`](https://github.com/simple-robot/simpler-robot/commit/69f2402b9): v to beta.4 拦截器优化
- [`468c19d65`](https://github.com/simple-robot/simpler-robot/commit/468c19d65): remove unused func
- [`bca13dab2`](https://github.com/simple-robot/simpler-robot/commit/bca13dab2): try fix #92
- [`c493f7b68`](https://github.com/simple-robot/simpler-robot/commit/c493f7b68): mirai message content builder for mirai native
- [`eb92e4c3c`](https://github.com/simple-robot/simpler-robot/commit/eb92e4c3c): SNAPSHOT

## v2.0.6-BETA.3

> Release & Pull Notes: [v2.0.6-BETA.3](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.6-BETA.3)
>
> Commit compare: [v2.0.6-BETA.2..v2.0.6-BETA.3](https://github.com/simple-robot/simpler-robot/compare/v2.0.6-BETA.2..v2.0.6-BETA.3)

- [`48fcd7ba8`](https://github.com/simple-robot/simpler-robot/commit/48fcd7ba8): v beta.3
- [`d908d77a5`](https://github.com/simple-robot/simpler-robot/commit/d908d77a5): 修复注释错误
- [`a5cde6dbd`](https://github.com/simple-robot/simpler-robot/commit/a5cde6dbd): 该死的泛型问题
- [`e4e63dc45`](https://github.com/simple-robot/simpler-robot/commit/e4e63dc45): mirai bot info优化 close #90
- [`a7c1fa688`](https://github.com/simple-robot/simpler-robot/commit/a7c1fa688): mirai bot info优化 link #90
- [`009a0fc49`](https://github.com/simple-robot/simpler-robot/commit/009a0fc49): update readme
- [`6ce6c0bfa`](https://github.com/simple-robot/simpler-robot/commit/6ce6c0bfa): 消息重构器 readme
- [`dd5901385`](https://github.com/simple-robot/simpler-robot/commit/dd5901385): 消息重构器 注释
- [`d70183ff9`](https://github.com/simple-robot/simpler-robot/commit/d70183ff9): 消息重构器接口调整  link #89
- [`955774228`](https://github.com/simple-robot/simpler-robot/commit/955774228): mirai组件 消息重构器 for test  link #89
- [`fd6e579a2`](https://github.com/simple-robot/simpler-robot/commit/fd6e579a2): mirai组件 消息重构器 link #89
- [`14ba93cf9`](https://github.com/simple-robot/simpler-robot/commit/14ba93cf9): 消息重构器参数
- [`6e66ba150..e1f12f035`](https://github.com/simple-robot/simpler-robot/compare/6e66ba150..14ba93cf9): 消息重构器

## v2.0.6-BETA.2

> Release & Pull Notes: [v2.0.6-BETA.2](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.6-BETA.2)
>
> Commit compare: [v2.0.6-BETA.1..v2.0.6-BETA.2](https://github.com/simple-robot/simpler-robot/compare/v2.0.6-BETA.1..v2.0.6-BETA.2)

- [`2a0889499..7823d1a09`](https://github.com/simple-robot/simpler-robot/compare/2a0889499..HEAD): v to 2.0.6-beta.2
- [`bf3752294`](https://github.com/simple-robot/simpler-robot/commit/bf3752294): additional api test
- [`5582cb182`](https://github.com/simple-robot/simpler-robot/commit/5582cb182): 好友信息获取支持
- [`1593995e2`](https://github.com/simple-robot/simpler-robot/commit/1593995e2): sender setter additionalApi.
- [`5a5e2d1c0`](https://github.com/simple-robot/simpler-robot/commit/5a5e2d1c0): 增加注释
- [`f2ac37324`](https://github.com/simple-robot/simpler-robot/commit/f2ac37324): 额外API与mirai文件相关
- [`18209cccb..e159dc48c`](https://github.com/simple-robot/simpler-robot/compare/18209cccb..f2ac37324): for additional api - remote file
- [`81ee7e5c3..2249de148`](https://github.com/simple-robot/simpler-robot/compare/81ee7e5c3..e159dc48c): for additional api
- [`4a8f95462..0ce431371`](https://github.com/simple-robot/simpler-robot/compare/4a8f95462..2249de148): additional api interface

## v2.0.6-BETA.1

> Release & Pull Notes: [v2.0.6-BETA.1](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.6-BETA.1)
>
> Commit compare: [v2.0.5..v2.0.6-BETA.1](https://github.com/simple-robot/simpler-robot/compare/v2.0.5..v2.0.6-BETA.1)

- [`1d718d488`](https://github.com/simple-robot/simpler-robot/commit/1d718d488): file upload auto mkdir
- [`69d8ab1b7`](https://github.com/simple-robot/simpler-robot/commit/69d8ab1b7): 引用回复相关
- [`2729f1ab0`](https://github.com/simple-robot/simpler-robot/commit/2729f1ab0): 移除重复参数
- [`71c50b032`](https://github.com/simple-robot/simpler-robot/commit/71c50b032): 取消一个不必要的LazyNeko
- [`6ceaf4c42`](https://github.com/simple-robot/simpler-robot/commit/6ceaf4c42): 整理处理消息转化器
- [`be2aa2215`](https://github.com/simple-robot/simpler-robot/commit/be2aa2215): for auto scan test and close issue; close #88
- [`64f5cdc88`](https://github.com/simple-robot/simpler-robot/commit/64f5cdc88): update version and auto scan config
- [`f7ac8ca30`](https://github.com/simple-robot/simpler-robot/commit/f7ac8ca30): update readme
- [`77177ce3c`](https://github.com/simple-robot/simpler-robot/commit/77177ce3c): remove some module
- [`3847edefa`](https://github.com/simple-robot/simpler-robot/commit/3847edefa): auto scan packages
- [`71fa04d34`](https://github.com/simple-robot/simpler-robot/commit/71fa04d34): simbot app

## v2.0.5

> Release & Pull Notes: [v2.0.5](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.5)
>
> Commit compare: [v2.0.4..v2.0.5](https://github.com/simple-robot/simpler-robot/compare/v2.0.4..v2.0.5)

- [`a675f377c`](https://github.com/simple-robot/simpler-robot/commit/a675f377c): 优化cat file解析
- [`b553a0a30`](https://github.com/simple-robot/simpler-robot/commit/b553a0a30): remove a test file
- [`aff9cf7b2`](https://github.com/simple-robot/simpler-robot/commit/aff9cf7b2): 动态参数提取器改动
- [`20af02bd6`](https://github.com/simple-robot/simpler-robot/commit/20af02bd6): 动态参数提取 link #85
- [`4ebe04523`](https://github.com/simple-robot/simpler-robot/commit/4ebe04523): 转发消息
- [`246dec16e..3907c5699`](https://github.com/simple-robot/simpler-robot/compare/246dec16e..4ebe04523): 骰子
- [`348597505`](https://github.com/simple-robot/simpler-robot/commit/348597505): 文件上传
- [`02fa9328a..0300c4ed2`](https://github.com/simple-robot/simpler-robot/compare/02fa9328a..348597505): code解析相关
- [`acf955ed8`](https://github.com/simple-robot/simpler-robot/commit/acf955ed8): 音乐分享相关
- [`acc477635`](https://github.com/simple-robot/simpler-robot/commit/acc477635): flow to stream
- [`7e295fb95`](https://github.com/simple-robot/simpler-robot/commit/7e295fb95): mirai 文件相关
- [`43793ae46..494e241a4`](https://github.com/simple-robot/simpler-robot/compare/43793ae46..7e295fb95): 更新注释
- [`48e0c91e3..94bfcb18c`](https://github.com/simple-robot/simpler-robot/compare/48e0c91e3..494e241a4): upload file for cat?
- [`01859fd0a`](https://github.com/simple-robot/simpler-robot/commit/01859fd0a): upload file for cat
- [`a4dacd8ee`](https://github.com/simple-robot/simpler-robot/commit/a4dacd8ee): upload file
- [`0ee9deb2e`](https://github.com/simple-robot/simpler-robot/commit/0ee9deb2e): Remote file.
- [`23e118301`](https://github.com/simple-robot/simpler-robot/commit/23e118301): 消息解析器
- [`b4daa7fd9`](https://github.com/simple-robot/simpler-robot/commit/b4daa7fd9): update mirai version to 2.5.0
- [`fa9cd7992`](https://github.com/simple-robot/simpler-robot/commit/fa9cd7992): pre update version to 2.0.5
- [`279a8d8e2`](https://github.com/simple-robot/simpler-robot/commit/279a8d8e2): update mirai to 2.5.0

## v2.0.4

> Release & Pull Notes: [v2.0.4](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.4)
>
> Commit compare: [v2.0.3..v2.0.4](https://github.com/simple-robot/simpler-robot/compare/v2.0.3..v2.0.4)

- [`fda3267fe`](https://github.com/simple-robot/simpler-robot/commit/fda3267fe): Fix mirai config.
- [`d9b0014c8`](https://github.com/simple-robot/simpler-robot/commit/d9b0014c8): fix #82;
- [`15363a0e1`](https://github.com/simple-robot/simpler-robot/commit/15363a0e1): Update github issue template.
- [`a1536bf78`](https://github.com/simple-robot/simpler-robot/commit/a1536bf78): 允许Filter与Filters进行注解继承; close #83
- [`718042b07`](https://github.com/simple-robot/simpler-robot/commit/718042b07): Mirai message source;
- [`b64b341c5`](https://github.com/simple-robot/simpler-robot/commit/b64b341c5): 恢复不应过时的api
- [`69562bb20`](https://github.com/simple-robot/simpler-robot/commit/69562bb20): 调整注释
- [`9b117cc94`](https://github.com/simple-robot/simpler-robot/commit/9b117cc94): verify ex
- [`fa273b47f`](https://github.com/simple-robot/simpler-robot/commit/fa273b47f): close #78

## v2.0.3

> Release & Pull Notes: [v2.0.3](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.3)
>
> Commit compare: [v2.0.2..v2.0.3](https://github.com/simple-robot/simpler-robot/compare/v2.0.2..v2.0.3)

- [`48a19c52d`](https://github.com/simple-robot/simpler-robot/commit/48a19c52d): update to 2.0.3
- [`0b51e48b6`](https://github.com/simple-robot/simpler-robot/commit/0b51e48b6): fix #77 close #77
- [`bb46b2df2`](https://github.com/simple-robot/simpler-robot/commit/bb46b2df2): readme
- [`872676993`](https://github.com/simple-robot/simpler-robot/commit/872676993): 修复@FilterValue没有前置处理 fix #76

## v2.0.2

> Release & Pull Notes: [v2.0.2](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.2)
>
> Commit compare: [v2.0.1..v2.0.2](https://github.com/simple-robot/simpler-robot/compare/v2.0.1..v2.0.2)

- [`a7d150da6`](https://github.com/simple-robot/simpler-robot/commit/a7d150da6): 消息解析 - 音乐 账号验证 - 日志
- [`a774e270b`](https://github.com/simple-robot/simpler-robot/commit/a774e270b): update ktx and fastjson version.
- [`9c9f21d35`](https://github.com/simple-robot/simpler-robot/commit/9c9f21d35): 调整注释
- [`a1f7f5e55..60ae24a76`](https://github.com/simple-robot/simpler-robot/compare/a1f7f5e55..9c9f21d35): setter相关修改;
- [`483ea6b90`](https://github.com/simple-robot/simpler-robot/commit/483ea6b90): getter相关修改; BanInfo -> MuteInfo BanList -> MuteList
- [`cce360a33`](https://github.com/simple-robot/simpler-robot/commit/cce360a33): 处理过时函数
- [`1040daabd`](https://github.com/simple-robot/simpler-robot/commit/1040daabd): 优先级注解 close #72
- [`689cacd12`](https://github.com/simple-robot/simpler-robot/commit/689cacd12): update version to 2.0.2 close #74
- [`f3f30610c`](https://github.com/simple-robot/simpler-robot/commit/f3f30610c): for #74
- [`a6bbf4593`](https://github.com/simple-robot/simpler-robot/commit/a6bbf4593): 接口增加属性

## v2.0.1

> Release & Pull Notes: [v2.0.1](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.1)
>
> Commit compare: [v2.0.0..v2.0.1](https://github.com/simple-robot/simpler-robot/compare/v2.0.0..v2.0.1)

- [`73e542804`](https://github.com/simple-robot/simpler-robot/commit/73e542804): test
- [`36d8feb62..61846321b`](https://github.com/simple-robot/simpler-robot/compare/36d8feb62..73e542804): update version 2.0.1
- [`f56e11c73`](https://github.com/simple-robot/simpler-robot/commit/f56e11c73): test
- [`cc6eafbe2`](https://github.com/simple-robot/simpler-robot/commit/cc6eafbe2): fix message builder
- [`629db2df4`](https://github.com/simple-robot/simpler-robot/commit/629db2df4): 配置项
- [`f015a7e97`](https://github.com/simple-robot/simpler-robot/commit/f015a7e97): 移除无用配置项
- [`3c496566e`](https://github.com/simple-robot/simpler-robot/commit/3c496566e): 2.0.1-SNAPSHOT
- [`4ce05ac05`](https://github.com/simple-robot/simpler-robot/commit/4ce05ac05): 尝试优化keyword动态参数提取
- [`2d6983843`](https://github.com/simple-robot/simpler-robot/commit/2d6983843): update kt version
- [`886f70e8a..3b1bee4ec`](https://github.com/simple-robot/simpler-robot/compare/886f70e8a..2d6983843): pom update version
- [`2edf79698`](https://github.com/simple-robot/simpler-robot/commit/2edf79698): update log

## v2.0.0

> Release & Pull Notes: [v2.0.0](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.0)
>
> Commit compare: [v2.0.0-RC.4..v2.0.0](https://github.com/simple-robot/simpler-robot/compare/v2.0.0-RC.4..v2.0.0)

- [`9e2f4a17d`](https://github.com/simple-robot/simpler-robot/commit/9e2f4a17d): update parent pom and deploy
- [`a3a931f78`](https://github.com/simple-robot/simpler-robot/commit/a3a931f78): clean import
- [`76f3b176a`](https://github.com/simple-robot/simpler-robot/commit/76f3b176a): update version to 2.0.0
- [`b1a6c5f60`](https://github.com/simple-robot/simpler-robot/commit/b1a6c5f60): update mirai to 2.3.2
- [`ed0e06449`](https://github.com/simple-robot/simpler-robot/commit/ed0e06449): update log
- [`23ee8bc5d`](https://github.com/simple-robot/simpler-robot/commit/23ee8bc5d): 优化过滤器目标处理器
- [`e21d7ac22`](https://github.com/simple-robot/simpler-robot/commit/e21d7ac22): update catcode version
- [`937995083`](https://github.com/simple-robot/simpler-robot/commit/937995083): test
- [`66f398d64`](https://github.com/simple-robot/simpler-robot/commit/66f398d64): 自定义过滤目标匹配器注释
- [`dcbdcee17`](https://github.com/simple-robot/simpler-robot/commit/dcbdcee17): 自定义过滤目标匹配器。

## v2.0.0-RC.4

> Release & Pull Notes: [v2.0.0-RC.4](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.0-RC.4)
>
> Commit compare: [v2.0.0-SNAPSHOT..v2.0.0-RC.4](https://github.com/simple-robot/simpler-robot/compare/v2.0.0-SNAPSHOT..v2.0.0-RC.4)

- [`00e177a0b`](https://github.com/simple-robot/simpler-robot/commit/00e177a0b): fix lovelycat starter
- [`54d21fa08..958b211c0`](https://github.com/simple-robot/simpler-robot/compare/54d21fa08..00e177a0b): 2.0.0.1-SNAP to 2.0.0-SNAP
- [`60051aa93`](https://github.com/simple-robot/simpler-robot/commit/60051aa93): maybe 2.0.0 ?
- [`31a66da58..66788ccde`](https://github.com/simple-robot/simpler-robot/compare/31a66da58..60051aa93): readme
- [`d4d264eab`](https://github.com/simple-robot/simpler-robot/commit/d4d264eab): update README.md
- [`363aa0319`](https://github.com/simple-robot/simpler-robot/commit/363aa0319): mirai更新
- [`423512ce7`](https://github.com/simple-robot/simpler-robot/commit/423512ce7): deploy info
- [`24777e599`](https://github.com/simple-robot/simpler-robot/commit/24777e599): 调整项目整体结构，去除`parent`中可能会出现的多余属性
- [`216ff3d63`](https://github.com/simple-robot/simpler-robot/commit/216ff3d63): clean import
- [`c9f9bb00c`](https://github.com/simple-robot/simpler-robot/commit/c9f9bb00c): 修改优化match逻辑
- [`83f4494a4`](https://github.com/simple-robot/simpler-robot/commit/83f4494a4): 删除尚未进行开发的模块
- [`5b3de5adf`](https://github.com/simple-robot/simpler-robot/commit/5b3de5adf): server
- [`8423f9236`](https://github.com/simple-robot/simpler-robot/commit/8423f9236): 移除多余shutdown hook
- [`0bbe325b2`](https://github.com/simple-robot/simpler-robot/commit/0bbe325b2): shutdown hook
- [`f84d73159..0f9475496`](https://github.com/simple-robot/simpler-robot/compare/f84d73159..0bbe325b2): server index
- [`6bbdd3828`](https://github.com/simple-robot/simpler-robot/commit/6bbdd3828): shutdown file
- [`b9920de1c`](https://github.com/simple-robot/simpler-robot/commit/b9920de1c): 优化可爱猫默认页; 为监听服务增加shutdown hook
- [`cf36d1370`](https://github.com/simple-robot/simpler-robot/commit/cf36d1370): 优化日志
- [`5cb793c22`](https://github.com/simple-robot/simpler-robot/commit/5cb793c22): 清理注释
- [`d1516db9b`](https://github.com/simple-robot/simpler-robot/commit/d1516db9b): ktor respond fix
- [`a76c76251`](https://github.com/simple-robot/simpler-robot/commit/a76c76251): pom executions
- [`5b55b29af`](https://github.com/simple-robot/simpler-robot/commit/5b55b29af): 邀请入群事件
- [`75481a4c5`](https://github.com/simple-robot/simpler-robot/commit/75481a4c5): 群成员最后发言时间
- [`3a7fb0e8d`](https://github.com/simple-robot/simpler-robot/commit/3a7fb0e8d): 群成员入群时间
- [`0708de0e9`](https://github.com/simple-robot/simpler-robot/commit/0708de0e9): update mirai to 2.1.1 支持音乐解析
- [`cd14d37a3`](https://github.com/simple-robot/simpler-robot/commit/cd14d37a3): 可爱猫 自动登录配置; 快速回复at
- [`b6723b5ba`](https://github.com/simple-robot/simpler-robot/commit/b6723b5ba): 可爱猫 群号支持获取数字
- [`d17ce2a00`](https://github.com/simple-robot/simpler-robot/commit/d17ce2a00): fix result processor
- [`efc26df44`](https://github.com/simple-robot/simpler-robot/commit/efc26df44): version.
- [`ad35f6c7e`](https://github.com/simple-robot/simpler-robot/commit/ad35f6c7e): remove empty body
- [`9982b98a4`](https://github.com/simple-robot/simpler-robot/commit/9982b98a4): toString
- [`3a7e0f3c7`](https://github.com/simple-robot/simpler-robot/commit/3a7e0f3c7): 获取拥有管理权限的人的列表
- [`a678915d1`](https://github.com/simple-robot/simpler-robot/commit/a678915d1): 群人数应该+bot
- [`45d90fcfd`](https://github.com/simple-robot/simpler-robot/commit/45d90fcfd): 部分字母开头大写
- [`752a843d7`](https://github.com/simple-robot/simpler-robot/commit/752a843d7): pom version
- [`70de580bb`](https://github.com/simple-robot/simpler-robot/commit/70de580bb): 修复可爱猫事件监听 close #67

## v2.0.0-SNAPSHOT

> Release & Pull Notes: [v2.0.0-SNAPSHOT](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.0-SNAPSHOT)
>
> Commit compare: [v2.0.0-RC.3..v2.0.0-SNAPSHOT](https://github.com/simple-robot/simpler-robot/compare/v2.0.0-RC.3..v2.0.0-SNAPSHOT)

- [`81be82dc4`](https://github.com/simple-robot/simpler-robot/commit/81be82dc4): 网络路径配置文件 close #66
- [`953bd2054`](https://github.com/simple-robot/simpler-robot/commit/953bd2054): 配置资源加载
- [`c2cffbf8f`](https://github.com/simple-robot/simpler-robot/commit/c2cffbf8f): 动态代理的问题
- [`bb002139a`](https://github.com/simple-robot/simpler-robot/commit/bb002139a): 修复springboot下动态代理的问题。 close #65
- [`e5be9f07a`](https://github.com/simple-robot/simpler-robot/commit/e5be9f07a): tips
- [`97b1698f8`](https://github.com/simple-robot/simpler-robot/commit/97b1698f8): resource load
- [`6e01e9e84`](https://github.com/simple-robot/simpler-robot/commit/6e01e9e84): 好多图标！
- [`d60c8cbb0`](https://github.com/simple-robot/simpler-robot/commit/d60c8cbb0): readme
- [`ed19775d1`](https://github.com/simple-robot/simpler-robot/commit/ed19775d1): 2.0.0-快照
- [`e914fe9a8..760ea900b`](https://github.com/simple-robot/simpler-robot/compare/e914fe9a8..ed19775d1): mark annotation
- [`4e7846451`](https://github.com/simple-robot/simpler-robot/commit/4e7846451): fix #63, #64
- [`a890b6a83`](https://github.com/simple-robot/simpler-robot/commit/a890b6a83): 快照版本不太行啊，先RC.4吧
- [`897287987`](https://github.com/simple-robot/simpler-robot/commit/897287987): for #62 and deploy 2.0.0-SNAPSHOT
- [`56d700428`](https://github.com/simple-robot/simpler-robot/commit/56d700428): readme
- [`8100e2114`](https://github.com/simple-robot/simpler-robot/commit/8100e2114): shutdown hook
- [`f22087f7a`](https://github.com/simple-robot/simpler-robot/commit/f22087f7a): snapshot
- [`3544c9ec8`](https://github.com/simple-robot/simpler-robot/commit/3544c9ec8): test
- [`cfc6af014`](https://github.com/simple-robot/simpler-robot/commit/cfc6af014): update log
- [`d478d70b9`](https://github.com/simple-robot/simpler-robot/commit/d478d70b9): fix #55; update log; close #55

## v2.0.0-RC.3

> Release & Pull Notes: [v2.0.0-RC.3](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.0-RC.3)
>
> Commit compare: [v2.0.0-RC.2..v2.0.0-RC.3](https://github.com/simple-robot/simpler-robot/compare/v2.0.0-RC.2..v2.0.0-RC.3)

- [`27dafab7d`](https://github.com/simple-robot/simpler-robot/commit/27dafab7d): 暂停钉钉模块的部署更新
- [`a9f058016`](https://github.com/simple-robot/simpler-robot/commit/a9f058016): bot level
- [`d46b94344`](https://github.com/simple-robot/simpler-robot/commit/d46b94344): bot level util
- [`d4ee97ad0`](https://github.com/simple-robot/simpler-robot/commit/d4ee97ad0): 监听响应处理器 #49 快速回复 #54
- [`5c0aee4b3`](https://github.com/simple-robot/simpler-robot/commit/5c0aee4b3): up log
- [`a59988885..b44f6e2cd`](https://github.com/simple-robot/simpler-robot/compare/a59988885..5c0aee4b3): for quick reply
- [`3cdf179d6`](https://github.com/simple-robot/simpler-robot/commit/3cdf179d6): reply
- [`52c145788`](https://github.com/simple-robot/simpler-robot/commit/52c145788): processor and quick reply
- [`76244bf6f`](https://github.com/simple-robot/simpler-robot/commit/76244bf6f): processor
- [`0ad73cc36`](https://github.com/simple-robot/simpler-robot/commit/0ad73cc36): #51: 监听参数警告信息以及类型处理优化 #52: 兼容kotlin的可空类型 #53: 兼容kotlin扩展函数写法
- [`a28e4702c`](https://github.com/simple-robot/simpler-robot/commit/a28e4702c): 清理过时代码
- [`f143ec076`](https://github.com/simple-robot/simpler-robot/commit/f143ec076): quick reply
- [`c37e07f01`](https://github.com/simple-robot/simpler-robot/commit/c37e07f01): update to next ver 2.0.0-rc.2

## v2.0.0-RC.2

> Release & Pull Notes: [v2.0.0-RC.2](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.0-RC.2)
>
> Commit compare: [v2.0.0-RC.1..v2.0.0-RC.2](https://github.com/simple-robot/simpler-robot/compare/v2.0.0-RC.1..v2.0.0-RC.2)

- [`920e95339`](https://github.com/simple-robot/simpler-robot/commit/920e95339): listener manager
- [`30bb69099`](https://github.com/simple-robot/simpler-robot/commit/30bb69099): update log and for deploy
- [`f3de075ee`](https://github.com/simple-robot/simpler-robot/commit/f3de075ee): fix: 修复可爱猫相关问题
- [`5b64e017c..634b99b65`](https://github.com/simple-robot/simpler-robot/compare/5b64e017c..f3de075ee): 决策
- [`ff03db773..92228b7ef`](https://github.com/simple-robot/simpler-robot/compare/ff03db773..634b99b65): test
- [`ea0880e50`](https://github.com/simple-robot/simpler-robot/commit/ea0880e50): 图片
- [`96c1ee630`](https://github.com/simple-robot/simpler-robot/commit/96c1ee630): test
- [`f1eec568e`](https://github.com/simple-robot/simpler-robot/commit/f1eec568e): 能力接口
- [`39bdf2a3c`](https://github.com/simple-robot/simpler-robot/commit/39bdf2a3c): package info
- [`1a1f9d92c`](https://github.com/simple-robot/simpler-robot/commit/1a1f9d92c): mirai catcode
- [`0df161dd7`](https://github.com/simple-robot/simpler-robot/commit/0df161dd7): tips
- [`3ed0bd52a`](https://github.com/simple-robot/simpler-robot/commit/3ed0bd52a): pom
- [`8112eecd6`](https://github.com/simple-robot/simpler-robot/commit/8112eecd6): kill warn

## v2.0.0-RC.1

> Release & Pull Notes: [v2.0.0-RC.1](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.0-RC.1)
>
> Commit compare: [v2.0.0-BETA.8..v2.0.0-RC.1](https://github.com/simple-robot/simpler-robot/compare/v2.0.0-BETA.8..v2.0.0-RC.1)

- [`cf09091a6`](https://github.com/simple-robot/simpler-robot/commit/cf09091a6): log and deploy
- [`350b02446`](https://github.com/simple-robot/simpler-robot/commit/350b02446): to 2.0.0-rc.1
- [`00e78ace6`](https://github.com/simple-robot/simpler-robot/commit/00e78ace6): mirai update, cookies, ktor
- [`2c8fd10a3..5b0947415`](https://github.com/simple-robot/simpler-robot/compare/2c8fd10a3..00e78ace6): log
- [`3efb36152`](https://github.com/simple-robot/simpler-robot/commit/3efb36152): pom and tips
- [`a97fd5003`](https://github.com/simple-robot/simpler-robot/commit/a97fd5003): operate todo
- [`38a1fe61a`](https://github.com/simple-robot/simpler-robot/commit/38a1fe61a): fix @Ignore fail
- [`a5d7e0c30`](https://github.com/simple-robot/simpler-robot/commit/a5d7e0c30): test
- [`eb43561ba..58b8c805d`](https://github.com/simple-robot/simpler-robot/compare/eb43561ba..a5d7e0c30): readme。
- [`cdc27c9a5`](https://github.com/simple-robot/simpler-robot/commit/cdc27c9a5): readme and logo.
- [`3e53a3960`](https://github.com/simple-robot/simpler-robot/commit/3e53a3960): log
- [`c97f4fbf2`](https://github.com/simple-robot/simpler-robot/commit/c97f4fbf2): message content
- [`cef05c0db`](https://github.com/simple-robot/simpler-robot/commit/cef05c0db): messageContent equals
- [`f36ba2848..22eb47ad6`](https://github.com/simple-robot/simpler-robot/compare/f36ba2848..cef05c0db): fix mirai cookies.
- [`a2c7a2654`](https://github.com/simple-robot/simpler-robot/commit/a2c7a2654): pom version
- [`884265e9f`](https://github.com/simple-robot/simpler-robot/commit/884265e9f): warn sender
- [`3b0c78ce6..ad2d19862`](https://github.com/simple-robot/simpler-robot/compare/3b0c78ce6..884265e9f): MultipleResults.getSize() -> MultipleResults.size()
- [`5cfc36841`](https://github.com/simple-robot/simpler-robot/commit/5cfc36841): bot as account
- [`e4b2b5c71`](https://github.com/simple-robot/simpler-robot/commit/e4b2b5c71): tips
- [`9919f1f4a`](https://github.com/simple-robot/simpler-robot/commit/9919f1f4a): pom.url
- [`04210dfd2`](https://github.com/simple-robot/simpler-robot/commit/04210dfd2): logger sender
- [`af978ed0e`](https://github.com/simple-robot/simpler-robot/commit/af978ed0e): update version to b.9

## v2.0.0-BETA.8

> Release & Pull Notes: [v2.0.0-BETA.8](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.0-BETA.8)
>
> Commit compare: [v2.0.0-BETA.7..v2.0.0-BETA.8](https://github.com/simple-robot/simpler-robot/compare/v2.0.0-BETA.7..v2.0.0-BETA.8)

- [`73745d016`](https://github.com/simple-robot/simpler-robot/commit/73745d016): fix: 可爱猫组件update
- [`7a93cc519`](https://github.com/simple-robot/simpler-robot/commit/7a93cc519): feat: #32 备用函数
- [`2ce370960`](https://github.com/simple-robot/simpler-robot/commit/2ce370960): feat: #18 不支持的API提供默认送信器配置
- [`abe300065`](https://github.com/simple-robot/simpler-robot/commit/abe300065): delete: 无用代码
- [`77baf3890`](https://github.com/simple-robot/simpler-robot/commit/77baf3890): feat: #40 细化各个类型
- [`050087833`](https://github.com/simple-robot/simpler-robot/commit/050087833): feat: 优化AccountInfo与AccountContainer
- [`e94845d6f`](https://github.com/simple-robot/simpler-robot/commit/e94845d6f): feat: #40 群成员accountInfo
- [`5a1db3e1b`](https://github.com/simple-robot/simpler-robot/commit/5a1db3e1b): for #18
- [`836c09560`](https://github.com/simple-robot/simpler-robot/commit/836c09560): for #40
- [`70a8635ab`](https://github.com/simple-robot/simpler-robot/commit/70a8635ab): lovely cat starter
- [`0b88c0986`](https://github.com/simple-robot/simpler-robot/commit/0b88c0986): feat: 优化事件触发机制 #36 finish
- [`e951170c8`](https://github.com/simple-robot/simpler-robot/commit/e951170c8): update version to b.8
- [`69ed99ca9`](https://github.com/simple-robot/simpler-robot/commit/69ed99ca9): fix: fix #39
- [`8e8514df4`](https://github.com/simple-robot/simpler-robot/commit/8e8514df4): Mirai on msg
- [`aea73f1b4`](https://github.com/simple-robot/simpler-robot/commit/aea73f1b4): for #36

## v2.0.0-BETA.7

> Release & Pull Notes: [v2.0.0-BETA.7](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.0-BETA.7)
>
> Commit compare: [v2.0.0-BETA.6..v2.0.0-BETA.7](https://github.com/simple-robot/simpler-robot/compare/v2.0.0-BETA.6..v2.0.0-BETA.7)

- [`8d3f23d6b`](https://github.com/simple-robot/simpler-robot/commit/8d3f23d6b): pom
- [`c004abef6`](https://github.com/simple-robot/simpler-robot/commit/c004abef6): version
- [`cd74754ea`](https://github.com/simple-robot/simpler-robot/commit/cd74754ea): clean import
- [`fe8820db1`](https://github.com/simple-robot/simpler-robot/commit/fe8820db1): version fix
- [`51b0ccec7`](https://github.com/simple-robot/simpler-robot/commit/51b0ccec7): fix ding
- [`ee5065df5`](https://github.com/simple-robot/simpler-robot/commit/ee5065df5): update log
- [`96e6df531`](https://github.com/simple-robot/simpler-robot/commit/96e6df531): 可爱猫springboot-starter; 移除部分多余输出; 可爱猫服务器响应修复; 追加spare并作准备;
- [`d630401ec`](https://github.com/simple-robot/simpler-robot/commit/d630401ec): ListenerContext 追加两个方法
- [`e1119f8d2`](https://github.com/simple-robot/simpler-robot/commit/e1119f8d2): 遗漏的 @JvmDefault注解
- [`b15836153`](https://github.com/simple-robot/simpler-robot/commit/b15836153): Achieve and close #29
- [`42f9cd7a3`](https://github.com/simple-robot/simpler-robot/commit/42f9cd7a3): Achieve and close #33
- [`2261dcb30`](https://github.com/simple-robot/simpler-robot/commit/2261dcb30): fix and close #34
- [`48c0f3c16`](https://github.com/simple-robot/simpler-robot/commit/48c0f3c16): fix and close #35
- [`0dd5423a1`](https://github.com/simple-robot/simpler-robot/commit/0dd5423a1): filter
- [`bb61751e4..7d215e1d4`](https://github.com/simple-robot/simpler-robot/compare/bb61751e4..0dd5423a1): tips
- [`f925fa847`](https://github.com/simple-robot/simpler-robot/commit/f925fa847): clean import
- [`629fd42e5`](https://github.com/simple-robot/simpler-robot/commit/629fd42e5): fix lovelycat api post
- [`e18fdcc85`](https://github.com/simple-robot/simpler-robot/commit/e18fdcc85): close #30
- [`191f76c85`](https://github.com/simple-robot/simpler-robot/commit/191f76c85): rest template client; pom;
- [`ac32c1d59`](https://github.com/simple-robot/simpler-robot/commit/ac32c1d59): dispatch
- [`f62810e16`](https://github.com/simple-robot/simpler-robot/commit/f62810e16): Coroutine test
- [`0f6416aba..cf50310f6`](https://github.com/simple-robot/simpler-robot/compare/0f6416aba..f62810e16): test
- [`91ed7e6a8`](https://github.com/simple-robot/simpler-robot/commit/91ed7e6a8): rest http
- [`bafe950cf`](https://github.com/simple-robot/simpler-robot/commit/bafe950cf): http client restTemplate
- [`b3a0e1eda`](https://github.com/simple-robot/simpler-robot/commit/b3a0e1eda): http-restTemplate
- [`b882be49c`](https://github.com/simple-robot/simpler-robot/commit/b882be49c): 可爱猫springboot-starter
- [`c9a973b96`](https://github.com/simple-robot/simpler-robot/commit/c9a973b96): mirai 纯text cat
- [`eac9b4976..d6314ad6e`](https://github.com/simple-robot/simpler-robot/compare/eac9b4976..c9a973b96): client template
- [`7f2cd1e44`](https://github.com/simple-robot/simpler-robot/commit/7f2cd1e44): tips; readme
- [`21be74b1d`](https://github.com/simple-robot/simpler-robot/commit/21be74b1d): 重命名 component-parent 模块为 component
- [`985d7c733`](https://github.com/simple-robot/simpler-robot/commit/985d7c733): logger
- [`a03be57f4`](https://github.com/simple-robot/simpler-robot/commit/a03be57f4): tips & logo conf
- [`5df40ec14`](https://github.com/simple-robot/simpler-robot/commit/5df40ec14): tips
- [`5880ff225`](https://github.com/simple-robot/simpler-robot/commit/5880ff225): thread { ... }
- [`b58f0186e`](https://github.com/simple-robot/simpler-robot/commit/b58f0186e): update version
- [`ee8df10f2`](https://github.com/simple-robot/simpler-robot/commit/ee8df10f2): readme

## v2.0.0-BETA.6

> Release & Pull Notes: [v2.0.0-BETA.6](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.0-BETA.6)
>
> Commit compare: [v2.0.0-BETA.5-time-task..v2.0.0-BETA.6](https://github.com/simple-robot/simpler-robot/compare/v2.0.0-BETA.5-time-task..v2.0.0-BETA.6)

- [`de9d4e079`](https://github.com/simple-robot/simpler-robot/commit/de9d4e079): group sync
- [`43d0dab7e`](https://github.com/simple-robot/simpler-robot/commit/43d0dab7e): lovelycat setter
- [`557b34e31`](https://github.com/simple-robot/simpler-robot/commit/557b34e31): mirai update
- [`d4eb8d7d4`](https://github.com/simple-robot/simpler-robot/commit/d4eb8d7d4): recall
- [`c6a07ee93`](https://github.com/simple-robot/simpler-robot/commit/c6a07ee93): event
- [`9fb14c25b`](https://github.com/simple-robot/simpler-robot/commit/9fb14c25b): mirai 荣耀
- [`78f3d2fff`](https://github.com/simple-robot/simpler-robot/commit/78f3d2fff): delete friend
- [`d5c9a4560`](https://github.com/simple-robot/simpler-robot/commit/d5c9a4560): mirai update
- [`94ce8a917..eb9c54275`](https://github.com/simple-robot/simpler-robot/compare/94ce8a917..d5c9a4560): update readme

## v2.0.0-BETA.5-time-task

> Release & Pull Notes: [v2.0.0-BETA.5-time-task](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.0-BETA.5-time-task)
>
> Commit compare: [v2.0.0-BETA.5..v2.0.0-BETA.5-time-task](https://github.com/simple-robot/simpler-robot/compare/v2.0.0-BETA.5..v2.0.0-BETA.5-time-task)

- [`25dda82e7`](https://github.com/simple-robot/simpler-robot/commit/25dda82e7): update log
- [`17d901bb6..534b478fa`](https://github.com/simple-robot/simpler-robot/compare/17d901bb6..25dda82e7): time task Backward compatible to b.5
- [`6b062d7e3..87cfb8b8f`](https://github.com/simple-robot/simpler-robot/compare/6b062d7e3..534b478fa): time task log
- [`e6f13ce7f`](https://github.com/simple-robot/simpler-robot/commit/e6f13ce7f): time task for quartz; close #24
- [`be6f5249a`](https://github.com/simple-robot/simpler-robot/commit/be6f5249a): 注释等
- [`e515c12a8`](https://github.com/simple-robot/simpler-robot/commit/e515c12a8): time method task
- [`9824701ef..a43c909d8`](https://github.com/simple-robot/simpler-robot/compare/9824701ef..e515c12a8): time task
- [`ece091a8c`](https://github.com/simple-robot/simpler-robot/commit/ece091a8c): fix close #28;
- [`52fd7b6c9`](https://github.com/simple-robot/simpler-robot/commit/52fd7b6c9): timetask
- [`02c62ce13`](https://github.com/simple-robot/simpler-robot/commit/02c62ce13): time task
- [`081158df3`](https://github.com/simple-robot/simpler-robot/commit/081158df3): time task core
- [`e239e056b`](https://github.com/simple-robot/simpler-robot/commit/e239e056b): update to b.6 version
- [`5e1b19a9d`](https://github.com/simple-robot/simpler-robot/commit/5e1b19a9d): time-task and readme
- [`ba210cf48`](https://github.com/simple-robot/simpler-robot/commit/ba210cf48): pom

## v2.0.0-BETA.5

> Release & Pull Notes: [v2.0.0-BETA.5](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.0-BETA.5)
>
> Commit compare: [v2.0.0-BETA.4..v2.0.0-BETA.5](https://github.com/simple-robot/simpler-robot/compare/v2.0.0-BETA.4..v2.0.0-BETA.5)

- [`fbf522b0c`](https://github.com/simple-robot/simpler-robot/commit/fbf522b0c): readme
- [`53d53bfa4`](https://github.com/simple-robot/simpler-robot/commit/53d53bfa4): dokka plugin update
- [`3d174827d`](https://github.com/simple-robot/simpler-robot/commit/3d174827d): test
- [`35eb1c90c`](https://github.com/simple-robot/simpler-robot/commit/35eb1c90c): kill warns
- [`4e947ad5e`](https://github.com/simple-robot/simpler-robot/commit/4e947ad5e): no Unsafe lazy
- [`571f031a0`](https://github.com/simple-robot/simpler-robot/commit/571f031a0): time-task module
- [`9ff2a2e80`](https://github.com/simple-robot/simpler-robot/commit/9ff2a2e80): mirai url img/voice 优化
- [`585155649`](https://github.com/simple-robot/simpler-robot/commit/585155649): ktor update
- [`c96bee814`](https://github.com/simple-robot/simpler-robot/commit/c96bee814): update to b.5
- [`510a45ee5`](https://github.com/simple-robot/simpler-robot/commit/510a45ee5): mirai-存活线程 守护线程
- [`a9ba4f466..941fdca7d`](https://github.com/simple-robot/simpler-robot/compare/a9ba4f466..510a45ee5): :loud_sound: 添加日志记录
- [`63138efea`](https://github.com/simple-robot/simpler-robot/commit/63138efea): 捕获部分可能的异常

## v2.0.0-BETA.4

> Release & Pull Notes: [v2.0.0-BETA.4](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.0-BETA.4)
>
> Commit compare: [v2.0.0-BETA.3..v2.0.0-BETA.4](https://github.com/simple-robot/simpler-robot/compare/v2.0.0-BETA.3..v2.0.0-BETA.4)

- [`64348377b`](https://github.com/simple-robot/simpler-robot/commit/64348377b): remove *.iml
- [`0037e611e`](https://github.com/simple-robot/simpler-robot/commit/0037e611e): log
- [`ee46483cc`](https://github.com/simple-robot/simpler-robot/commit/ee46483cc): #27
- [`62cde4245`](https://github.com/simple-robot/simpler-robot/commit/62cde4245): sb starter conf
- [`38e8f69e7`](https://github.com/simple-robot/simpler-robot/commit/38e8f69e7): 独立serialization模块
- [`49a096929`](https://github.com/simple-robot/simpler-robot/commit/49a096929): serialization readme
- [`3f8938df7`](https://github.com/simple-robot/simpler-robot/commit/3f8938df7): 独立serialization模块
- [`9748e0340`](https://github.com/simple-robot/simpler-robot/commit/9748e0340): sb conf
- [`4fc592bd2`](https://github.com/simple-robot/simpler-robot/commit/4fc592bd2): update to v b.4

## v2.0.0-BETA.3

> Release & Pull Notes: [v2.0.0-BETA.3](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.0-BETA.3)
>
> Commit compare: [v2.0.0-BETA.2..v2.0.0-BETA.3](https://github.com/simple-robot/simpler-robot/compare/v2.0.0-BETA.2..v2.0.0-BETA.3)

- [`1c8b5f157`](https://github.com/simple-robot/simpler-robot/commit/1c8b5f157): update log
- [`8847bd314..25dcd7875`](https://github.com/simple-robot/simpler-robot/compare/8847bd314..1c8b5f157): update version to b.3
- [`2c5f1f5ea`](https://github.com/simple-robot/simpler-robot/commit/2c5f1f5ea): fix close #26;
- [`71227d7dc`](https://github.com/simple-robot/simpler-robot/commit/71227d7dc): fix close #25;

## v2.0.0-BETA.2

> Release & Pull Notes: [v2.0.0-BETA.2](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.0-BETA.2)
>
> Commit compare: [v2.0.0-BETA.1..v2.0.0-BETA.2](https://github.com/simple-robot/simpler-robot/compare/v2.0.0-BETA.1..v2.0.0-BETA.2)

- [`8aa7e5cf6`](https://github.com/simple-robot/simpler-robot/commit/8aa7e5cf6): readme
- [`fae311504..bd4faf0ee`](https://github.com/simple-robot/simpler-robot/compare/fae311504..8aa7e5cf6): 适配mirai-2.0-M1
- [`69418b211`](https://github.com/simple-robot/simpler-robot/commit/69418b211): new version
- [`c357bce21`](https://github.com/simple-robot/simpler-robot/commit/c357bce21): :bulb: 添加源码注释
- [`eb51dbdcb`](https://github.com/simple-robot/simpler-robot/commit/eb51dbdcb): log name fix
- [`afcdc86b1`](https://github.com/simple-robot/simpler-robot/commit/afcdc86b1): readme

## v2.0.0-BETA.1

> Release & Pull Notes: [v2.0.0-BETA.1](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.0-BETA.1)
>
> Commit compare: [v2.0.0-ALPHA.12..v2.0.0-BETA.1](https://github.com/simple-robot/simpler-robot/compare/v2.0.0-ALPHA.12..v2.0.0-BETA.1)

- [`513102e39`](https://github.com/simple-robot/simpler-robot/commit/513102e39): readme and deploy
- [`863b70f6f`](https://github.com/simple-robot/simpler-robot/commit/863b70f6f): to BETA.1
- [`17dc57465`](https://github.com/simple-robot/simpler-robot/commit/17dc57465): sb metadata
- [`754a38129`](https://github.com/simple-robot/simpler-robot/commit/754a38129): jackson serializer
- [`d3bdeeaf9`](https://github.com/simple-robot/simpler-robot/commit/d3bdeeaf9): 临时移除onebot组件
- [`97fb69ac4`](https://github.com/simple-robot/simpler-robot/commit/97fb69ac4): fix #21
- [`fceeda361`](https://github.com/simple-robot/simpler-robot/commit/fceeda361): finish #20
- [`30be2cde5`](https://github.com/simple-robot/simpler-robot/commit/30be2cde5): component onebot
- [`dc4e406e7`](https://github.com/simple-robot/simpler-robot/commit/dc4e406e7): delete module
- [`24a37ab91`](https://github.com/simple-robot/simpler-robot/commit/24a37ab91): update and deploy
- [`f7a4dc923`](https://github.com/simple-robot/simpler-robot/commit/f7a4dc923): listen result rename 'throwable' to 'cause'

## v2.0.0-ALPHA.12

> Release & Pull Notes: [v2.0.0-ALPHA.12](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.0-ALPHA.12)
>
> Commit compare: [v2.0.0-ALPHA.11..v2.0.0-ALPHA.12](https://github.com/simple-robot/simpler-robot/compare/v2.0.0-ALPHA.11..v2.0.0-ALPHA.12)

- [`cfb7eb8ef`](https://github.com/simple-robot/simpler-robot/commit/cfb7eb8ef): update and deploy
- [`3ccd50960..5bd9cd479`](https://github.com/simple-robot/simpler-robot/compare/3ccd50960..cfb7eb8ef): result 无效化
- [`4af953fec`](https://github.com/simple-robot/simpler-robot/commit/4af953fec): lovely cat setter
- [`31f2dd9f8`](https://github.com/simple-robot/simpler-robot/commit/31f2dd9f8): test
- [`646d00099`](https://github.com/simple-robot/simpler-robot/commit/646d00099): lovely cat setter
- [`306302cde`](https://github.com/simple-robot/simpler-robot/commit/306302cde): update log
- [`b92be1362`](https://github.com/simple-robot/simpler-robot/commit/b92be1362): achieve #17
- [`cc35525e1..69b02b27e`](https://github.com/simple-robot/simpler-robot/compare/cc35525e1..b92be1362): ex handle
- [`aa4c5dc51`](https://github.com/simple-robot/simpler-robot/commit/aa4c5dc51): listen result impl
- [`565ca999b`](https://github.com/simple-robot/simpler-robot/commit/565ca999b): handle failed
- [`7d1e5c0c4`](https://github.com/simple-robot/simpler-robot/commit/7d1e5c0c4): ex log
- [`b279dd225`](https://github.com/simple-robot/simpler-robot/commit/b279dd225): LogAble
- [`3f045c48d`](https://github.com/simple-robot/simpler-robot/commit/3f045c48d): ex handle
- [`0619d755c`](https://github.com/simple-robot/simpler-robot/commit/0619d755c): update readme
- [`1ae0858e8`](https://github.com/simple-robot/simpler-robot/commit/1ae0858e8): test and deploy a.11
- [`b0b717e94`](https://github.com/simple-robot/simpler-robot/commit/b0b717e94): log
- [`14e6abfb6..f717e6466`](https://github.com/simple-robot/simpler-robot/compare/14e6abfb6..b0b717e94): lovely cat getter.
- [`a330a771d`](https://github.com/simple-robot/simpler-robot/commit/a330a771d): fix #14 #15
- [`554fee40c`](https://github.com/simple-robot/simpler-robot/commit/554fee40c): lovelycat
- [`4675797ab`](https://github.com/simple-robot/simpler-robot/commit/4675797ab): api exception
- [`ec5b63b6a`](https://github.com/simple-robot/simpler-robot/commit/ec5b63b6a): exception
- [`5f512310b`](https://github.com/simple-robot/simpler-robot/commit/5f512310b): remove todo
- [`de72b0a6d`](https://github.com/simple-robot/simpler-robot/commit/de72b0a6d): lovelycat lazy cache
- [`ce3528e19..919cb703b`](https://github.com/simple-robot/simpler-robot/compare/ce3528e19..de72b0a6d): lock test
- [`787b216b4`](https://github.com/simple-robot/simpler-robot/commit/787b216b4): lock?
- [`fd066e783`](https://github.com/simple-robot/simpler-robot/commit/fd066e783): lovely cat api cache
- [`68ea0ada1`](https://github.com/simple-robot/simpler-robot/commit/68ea0ada1): listener cache compute
- [`487bc90c3..09f1be7d3`](https://github.com/simple-robot/simpler-robot/compare/487bc90c3..68ea0ada1): update ListenerContext
- [`f0f7a9abf`](https://github.com/simple-robot/simpler-robot/commit/f0f7a9abf): test
- [`fc23ef9d4`](https://github.com/simple-robot/simpler-robot/commit/fc23ef9d4): group
- [`792660315`](https://github.com/simple-robot/simpler-robot/commit/792660315): update to next v(a.11)
- [`29f075f8b`](https://github.com/simple-robot/simpler-robot/commit/29f075f8b): Update issue templates
- [`dcd67c962`](https://github.com/simple-robot/simpler-robot/commit/dcd67c962): issue templates.

## v2.0.0-ALPHA.11

> Release & Pull Notes: [v2.0.0-ALPHA.11](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.0-ALPHA.11)
>
> Commit compare: [v2.0.0-ALPHA.8..v2.0.0-ALPHA.11](https://github.com/simple-robot/simpler-robot/compare/v2.0.0-ALPHA.8..v2.0.0-ALPHA.11)

- [`0cc2ff0c5..91befa18b`](https://github.com/simple-robot/simpler-robot/compare/0cc2ff0c5..HEAD): update log
- [`b7066fd9a`](https://github.com/simple-robot/simpler-robot/commit/b7066fd9a): 为 GroupAddRequest 追加群容器。 fix #12
- [`6fec34e3e..2ea207c91`](https://github.com/simple-robot/simpler-robot/compare/6fec34e3e..b7066fd9a): update doc
- [`4fca185c1`](https://github.com/simple-robot/simpler-robot/commit/4fca185c1): update readme
- [`d169a21b2`](https://github.com/simple-robot/simpler-robot/commit/d169a21b2): update log
- [`4cccfa922`](https://github.com/simple-robot/simpler-robot/commit/4cccfa922): fix #11
- [`59f75f65b`](https://github.com/simple-robot/simpler-robot/commit/59f75f65b): cache
- [`92eeb9b7f`](https://github.com/simple-robot/simpler-robot/commit/92eeb9b7f): lovely cat getter;
- [`dc7f982e6`](https://github.com/simple-robot/simpler-robot/commit/dc7f982e6): lovelycat
- [`0dc3f333a`](https://github.com/simple-robot/simpler-robot/commit/0dc3f333a): update to next alpha version(a.10)
- [`d5f00de0a`](https://github.com/simple-robot/simpler-robot/commit/d5f00de0a): lovely cat component;
- [`f58f6b5b5`](https://github.com/simple-robot/simpler-robot/commit/f58f6b5b5): Lovely cat sender;
- [`c31525e0b`](https://github.com/simple-robot/simpler-robot/commit/c31525e0b): at检测器更新,默认检测器变更为使用neko进行检测。
- [`3c6b189e1`](https://github.com/simple-robot/simpler-robot/commit/3c6b189e1): lovely cat sender;
- [`a58c2cab1`](https://github.com/simple-robot/simpler-robot/commit/a58c2cab1): core configs
- [`67e249035`](https://github.com/simple-robot/simpler-robot/commit/67e249035): ding configs
- [`e884e5b8b`](https://github.com/simple-robot/simpler-robot/commit/e884e5b8b): mirai configs
- [`4bfa955ce`](https://github.com/simple-robot/simpler-robot/commit/4bfa955ce): lovely cat events & configs
- [`f6eeafc39`](https://github.com/simple-robot/simpler-robot/commit/f6eeafc39): 全员增加减少事件
- [`d5616748c`](https://github.com/simple-robot/simpler-robot/commit/d5616748c): contacts change event
- [`430a412ed`](https://github.com/simple-robot/simpler-robot/commit/430a412ed): lovely cat friend verify event;
- [`55b9a3dce`](https://github.com/simple-robot/simpler-robot/commit/55b9a3dce): interface Requestable
- [`86d0e2163`](https://github.com/simple-robot/simpler-robot/commit/86d0e2163): update log
- [`0a3668cae`](https://github.com/simple-robot/simpler-robot/commit/0a3668cae): 扫描支付事件。
- [`3b2193c1a`](https://github.com/simple-robot/simpler-robot/commit/3b2193c1a): update log
- [`d3638d128`](https://github.com/simple-robot/simpler-robot/commit/d3638d128): lovely cat 转账事件
- [`b89cef061`](https://github.com/simple-robot/simpler-robot/commit/b89cef061): simbot app logs
- [`d7fb5b7de`](https://github.com/simple-robot/simpler-robot/commit/d7fb5b7de): update tips
- [`2693c0e94`](https://github.com/simple-robot/simpler-robot/commit/2693c0e94): update comment
- [`0f491aad6..5dc0893d6`](https://github.com/simple-robot/simpler-robot/compare/0f491aad6..2693c0e94): update log
- [`457746217`](https://github.com/simple-robot/simpler-robot/commit/457746217): fix #10 ;
- [`2adc001ac`](https://github.com/simple-robot/simpler-robot/commit/2adc001ac): ready to deploy
- [`3eda0e812..636db82fc`](https://github.com/simple-robot/simpler-robot/compare/3eda0e812..2adc001ac): lovely cat;
- [`48ed3c535`](https://github.com/simple-robot/simpler-robot/commit/48ed3c535): clear import
- [`37cb7e4c9`](https://github.com/simple-robot/simpler-robot/commit/37cb7e4c9): Revert "实现 ListenBreak解析 与 ListenResult解析";
- [`d48eaca15`](https://github.com/simple-robot/simpler-robot/commit/d48eaca15): clear import
- [`dd2f3871f`](https://github.com/simple-robot/simpler-robot/commit/dd2f3871f): 实现 ListenBreak解析 与 ListenResult解析
- [`42461f435`](https://github.com/simple-robot/simpler-robot/commit/42461f435): lovely cat private msg
- [`012d7cb4a`](https://github.com/simple-robot/simpler-robot/commit/012d7cb4a): private msg type
- [`2848bf7f9`](https://github.com/simple-robot/simpler-robot/commit/2848bf7f9): login event
- [`6e08e4c9a`](https://github.com/simple-robot/simpler-robot/commit/6e08e4c9a): update log
- [`900d50504..c3f60aff2`](https://github.com/simple-robot/simpler-robot/compare/900d50504..6e08e4c9a): lovelycat at code;
- [`d19dff02d`](https://github.com/simple-robot/simpler-robot/commit/d19dff02d): clear imports;
- [`bf8d65679`](https://github.com/simple-robot/simpler-robot/commit/bf8d65679): lovely cat;
- [`d4e289c13`](https://github.com/simple-robot/simpler-robot/commit/d4e289c13): RequestGets
- [`26cbcb9df`](https://github.com/simple-robot/simpler-robot/commit/26cbcb9df): mirai messages;
- [`238ef7bda`](https://github.com/simple-robot/simpler-robot/commit/238ef7bda): update comment
- [`f61c0919e`](https://github.com/simple-robot/simpler-robot/commit/f61c0919e): update to next (a.9
- [`cde3a2e23`](https://github.com/simple-robot/simpler-robot/commit/cde3a2e23): deploy a.8

## v2.0.0-ALPHA.8

> Release & Pull Notes: [v2.0.0-ALPHA.8](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.0-ALPHA.8)
>
> Commit compare: [v2.0.0-ALPHA.7..v2.0.0-ALPHA.8](https://github.com/simple-robot/simpler-robot/compare/v2.0.0-ALPHA.7..v2.0.0-ALPHA.8)

- [`1d4fdd8a7..41c2a3a49`](https://github.com/simple-robot/simpler-robot/compare/1d4fdd8a7..HEAD): ready to deploy;
- [`4dbdacdfe`](https://github.com/simple-robot/simpler-robot/commit/4dbdacdfe): fix #9 ;
- [`f6e28f669..c405ece9d`](https://github.com/simple-robot/simpler-robot/compare/f6e28f669..4dbdacdfe): lovely cat ;
- [`21b2fae30`](https://github.com/simple-robot/simpler-robot/commit/21b2fae30): update to next version(a.8)
- [`6dd0ba7af`](https://github.com/simple-robot/simpler-robot/commit/6dd0ba7af): update dokka to v1.4.10.2;
- [`d95a41418..4154945dc`](https://github.com/simple-robot/simpler-robot/compare/d95a41418..6dd0ba7af): update log;
- [`ef34fed03..52bd3edbc`](https://github.com/simple-robot/simpler-robot/compare/ef34fed03..4154945dc): update and ready to deploy;
- [`fac84edce`](https://github.com/simple-robot/simpler-robot/commit/fac84edce): event registrar
- [`618901edb`](https://github.com/simple-robot/simpler-robot/commit/618901edb): test
- [`1f1ca4d84`](https://github.com/simple-robot/simpler-robot/commit/1f1ca4d84): 变更toString信息
- [`4cc1aaa92`](https://github.com/simple-robot/simpler-robot/commit/4cc1aaa92): 移除多余输出
- [`7eb64ed79`](https://github.com/simple-robot/simpler-robot/commit/7eb64ed79): fix #8;
- [`aa4792d63`](https://github.com/simple-robot/simpler-robot/commit/aa4792d63): update log;
- [`c2724d604`](https://github.com/simple-robot/simpler-robot/commit/c2724d604): update hutool to v5.5.1;
- [`77a666150`](https://github.com/simple-robot/simpler-robot/commit/77a666150): mirai messages;
- [`de9e249f8`](https://github.com/simple-robot/simpler-robot/commit/de9e249f8): lovelycat api templates;
- [`6a9555efe`](https://github.com/simple-robot/simpler-robot/commit/6a9555efe): clear import;
- [`00c9c0d87`](https://github.com/simple-robot/simpler-robot/commit/00c9c0d87): mirai组件大部分位置的BotInfo支持获取等级信息
- [`0bf7c0367`](https://github.com/simple-robot/simpler-robot/commit/0bf7c0367): 修复日志国际格式化错误
- [`ffd7aa77e`](https://github.com/simple-robot/simpler-robot/commit/ffd7aa77e): 追加http-client模块的cookie携带; fix #7;
- [`c85419e58`](https://github.com/simple-robot/simpler-robot/commit/c85419e58): update to next version(a.7)
- [`698abb0b9`](https://github.com/simple-robot/simpler-robot/commit/698abb0b9): 优化过滤器匹配规则
- [`2fad654f4`](https://github.com/simple-robot/simpler-robot/commit/2fad654f4): update log;
- [`844b1e2f9`](https://github.com/simple-robot/simpler-robot/commit/844b1e2f9): update readme and comment;

## v2.0.0-ALPHA.7

> Release & Pull Notes: [v2.0.0-ALPHA.7](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.0-ALPHA.7)
>
> Commit compare: [v2.0.0-ALPHA.10..v2.0.0-ALPHA.7](https://github.com/simple-robot/simpler-robot/compare/v2.0.0-ALPHA.10..v2.0.0-ALPHA.7)

- [`380a476db`](https://github.com/simple-robot/simpler-robot/commit/380a476db): update and deploy a.6;
- [`8b5cae1d2`](https://github.com/simple-robot/simpler-robot/commit/8b5cae1d2): update test;
- [`625ed6736`](https://github.com/simple-robot/simpler-robot/commit/625ed6736): fix https://github.com/ForteScarlet/simpler-robot/issues/6
- [`258d67723`](https://github.com/simple-robot/simpler-robot/commit/258d67723): update test;
- [`072844b54`](https://github.com/simple-robot/simpler-robot/commit/072844b54): fix #5
- [`ef23a259e`](https://github.com/simple-robot/simpler-robot/commit/ef23a259e): update to next version(a.6)
- [`b90872984..9ee232d17`](https://github.com/simple-robot/simpler-robot/compare/b90872984..ef23a259e): move MessageContent package and deploy;
- [`91b1e5cbb`](https://github.com/simple-robot/simpler-robot/commit/91b1e5cbb): interceptor
- [`e9eb85f71`](https://github.com/simple-robot/simpler-robot/commit/e9eb85f71): http template cookies;
- [`f5522ab8a`](https://github.com/simple-robot/simpler-robot/commit/f5522ab8a): mirai level;
- [`ea0469a0e`](https://github.com/simple-robot/simpler-robot/commit/ea0469a0e): lovelycat .
- [`d9ab9f775..5f87d2cad`](https://github.com/simple-robot/simpler-robot/compare/d9ab9f775..ea0469a0e): lovely cat component;
- [`e9df9d6f0`](https://github.com/simple-robot/simpler-robot/commit/e9df9d6f0): update to next alpha version
- [`97dc5d410`](https://github.com/simple-robot/simpler-robot/commit/97dc5d410): deploy a.4 and update log.
- [`dedebb076`](https://github.com/simple-robot/simpler-robot/commit/dedebb076): update tips
- [`0a3a257ec`](https://github.com/simple-robot/simpler-robot/commit/0a3a257ec): 优化mirai日志展示
- [`c073d06d5`](https://github.com/simple-robot/simpler-robot/commit/c073d06d5): 暂时移除未完成的可爱猫组件module;
- [`8fb3fd51d`](https://github.com/simple-robot/simpler-robot/commit/8fb3fd51d): try fix #3
- [`2d7b66bbd`](https://github.com/simple-robot/simpler-robot/commit/2d7b66bbd): 用不着，以前写过了..
- [`41d105c4a`](https://github.com/simple-robot/simpler-robot/commit/41d105c4a): mirai组件增加bot的shutdown hook
- [`181a5f6b5`](https://github.com/simple-robot/simpler-robot/commit/181a5f6b5): move configuration
- [`5b40182a8`](https://github.com/simple-robot/simpler-robot/commit/5b40182a8): update some version.
- [`615e65449`](https://github.com/simple-robot/simpler-robot/commit/615e65449): mirai text and base text.
- [`286fc8110`](https://github.com/simple-robot/simpler-robot/commit/286fc8110): fix #2
- [`946787d29`](https://github.com/simple-robot/simpler-robot/commit/946787d29): fix #1
- [`681977f4c`](https://github.com/simple-robot/simpler-robot/commit/681977f4c): lovely cat readme;
- [`5e5537850`](https://github.com/simple-robot/simpler-robot/commit/5e5537850): update kt version.
- [`76369e8da`](https://github.com/simple-robot/simpler-robot/commit/76369e8da): test
- [`1a12a9265`](https://github.com/simple-robot/simpler-robot/commit/1a12a9265): http client template.
- [`cdb651916..edc5ff816`](https://github.com/simple-robot/simpler-robot/compare/cdb651916..1a12a9265): lovely cat component.
- [`3971f84ed`](https://github.com/simple-robot/simpler-robot/commit/3971f84ed): fast json auto configure
- [`9ebd00c5d`](https://github.com/simple-robot/simpler-robot/commit/9ebd00c5d): move ComponentBeans
- [`f8430ce7d`](https://github.com/simple-robot/simpler-robot/commit/f8430ce7d): api bot as accessInfo.
- [`d4dc14d98`](https://github.com/simple-robot/simpler-robot/commit/d4dc14d98): logger.
- [`cddce3f0d`](https://github.com/simple-robot/simpler-robot/commit/cddce3f0d): import clean.
- [`ba27bf0f8`](https://github.com/simple-robot/simpler-robot/commit/ba27bf0f8): test.
- [`92b19e898`](https://github.com/simple-robot/simpler-robot/commit/92b19e898): move message content builder.
- [`f9014bdc0..6ca3498c6`](https://github.com/simple-robot/simpler-robot/compare/f9014bdc0..92b19e898): mirai-bot logger.
- [`c77874453`](https://github.com/simple-robot/simpler-robot/commit/c77874453): ver a.4
- [`4f4ac772f..cce9e5127`](https://github.com/simple-robot/simpler-robot/compare/4f4ac772f..c77874453): component-ding
- [`81f163615`](https://github.com/simple-robot/simpler-robot/commit/81f163615): 白忙活了
- [`54f5b88b0`](https://github.com/simple-robot/simpler-robot/commit/54f5b88b0): moshi json.
- [`5f9fbce0e`](https://github.com/simple-robot/simpler-robot/commit/5f9fbce0e): tips
- [`061423cee`](https://github.com/simple-robot/simpler-robot/commit/061423cee): update logo
- [`bc1add809`](https://github.com/simple-robot/simpler-robot/commit/bc1add809): update copyright; clear import;
- [`80018e2ff..e9bdfdeff`](https://github.com/simple-robot/simpler-robot/compare/80018e2ff..bc1add809): update log.
- [`e7e3273af`](https://github.com/simple-robot/simpler-robot/commit/e7e3273af): pom
- [`6806be369`](https://github.com/simple-robot/simpler-robot/commit/6806be369): flag; content;
- [`ca6b687e8`](https://github.com/simple-robot/simpler-robot/commit/ca6b687e8): readme;
- [`5a3f84a86`](https://github.com/simple-robot/simpler-robot/commit/5a3f84a86): img builder; content; filter text test;
- [`846a45bd0`](https://github.com/simple-robot/simpler-robot/commit/846a45bd0): test getText and getMsg;
- [`e6b2f9e03`](https://github.com/simple-robot/simpler-robot/commit/e6b2f9e03): update version; 重新实现 messageContent;
- [`17dde8569`](https://github.com/simple-robot/simpler-robot/commit/17dde8569): core-starter移除部分多余控制台输出
- [`fbe649e0f`](https://github.com/simple-robot/simpler-robot/commit/fbe649e0f): 调整为新的 messageContent 实现。
- [`13d845059`](https://github.com/simple-robot/simpler-robot/commit/13d845059): builder
- [`66a285a4d`](https://github.com/simple-robot/simpler-robot/commit/66a285a4d): msg content.
- [`3cb9e968a`](https://github.com/simple-robot/simpler-robot/commit/3cb9e968a): 注释
- [`8aeabe774`](https://github.com/simple-robot/simpler-robot/commit/8aeabe774): content
- [`abb015f2f`](https://github.com/simple-robot/simpler-robot/commit/abb015f2f): catcode version.
- [`2e48a32b2`](https://github.com/simple-robot/simpler-robot/commit/2e48a32b2): MessageContent-cats
- [`89210ccff`](https://github.com/simple-robot/simpler-robot/commit/89210ccff): :bulb: 添加源码注释
- [`621db829a`](https://github.com/simple-robot/simpler-robot/commit/621db829a): msg content.
- [`7531889ae`](https://github.com/simple-robot/simpler-robot/commit/7531889ae): msg parser.
- [`7179e6628..9c6c00fb8`](https://github.com/simple-robot/simpler-robot/compare/7179e6628..7531889ae): readme.
- [`9211e3c1c..e246878a5`](https://github.com/simple-robot/simpler-robot/compare/9211e3c1c..9c6c00fb8): readmes.
- [`201459e4e..d883a5d90`](https://github.com/simple-robot/simpler-robot/compare/201459e4e..e246878a5): update to alpha.2
- [`530bac111`](https://github.com/simple-robot/simpler-robot/commit/530bac111): readme
- [`6ed3bd24c`](https://github.com/simple-robot/simpler-robot/commit/6ed3bd24c): mirai-starter
- [`77dc6c828`](https://github.com/simple-robot/simpler-robot/commit/77dc6c828): spring metadata
- [`4ecbce643`](https://github.com/simple-robot/simpler-robot/commit/4ecbce643): update springboot-starter; 追加springboot部分配置文件提示。
- [`f0f7837be`](https://github.com/simple-robot/simpler-robot/commit/f0f7837be): 任务执行器; test
- [`fbe5dfeb4`](https://github.com/simple-robot/simpler-robot/commit/fbe5dfeb4): 任务执行器
- [`d1b121b29..81759ca0a`](https://github.com/simple-robot/simpler-robot/compare/d1b121b29..fbe5dfeb4): readme
- [`2a3e943a9`](https://github.com/simple-robot/simpler-robot/commit/2a3e943a9): logger;
- [`7c31644b2`](https://github.com/simple-robot/simpler-robot/commit/7c31644b2): logger; clean import; listener registers;
- [`9021aecb6`](https://github.com/simple-robot/simpler-robot/commit/9021aecb6): Core springboot starter module
- [`2e7e88682`](https://github.com/simple-robot/simpler-robot/commit/2e7e88682): Springboot starter module
- [`496ce2297`](https://github.com/simple-robot/simpler-robot/commit/496ce2297): api-Task runner
- [`4bb9a2eb1`](https://github.com/simple-robot/simpler-robot/commit/4bb9a2eb1): json-fastjson
- [`ec0ec94ca`](https://github.com/simple-robot/simpler-robot/commit/ec0ec94ca): http template.
- [`d1353003c`](https://github.com/simple-robot/simpler-robot/commit/d1353003c): remove some comment
- [`b7412161d..8b226e862`](https://github.com/simple-robot/simpler-robot/compare/b7412161d..d1353003c): http template.
- [`1245d35d4`](https://github.com/simple-robot/simpler-robot/commit/1245d35d4): http-template-ktor
- [`95901e6cc`](https://github.com/simple-robot/simpler-robot/commit/95901e6cc): http-template-core
- [`3a414fc1e`](https://github.com/simple-robot/simpler-robot/commit/3a414fc1e): json-moshi
- [`4c0d22b3f`](https://github.com/simple-robot/simpler-robot/commit/4c0d22b3f): json-core
- [`37c7ca80c`](https://github.com/simple-robot/simpler-robot/commit/37c7ca80c): json-moshi
- [`29ad48f9a`](https://github.com/simple-robot/simpler-robot/commit/29ad48f9a): http client; json core;
- [`5e6e9443f`](https://github.com/simple-robot/simpler-robot/commit/5e6e9443f): http client;
- [`a641d884e..f6512edc4`](https://github.com/simple-robot/simpler-robot/compare/a641d884e..5e6e9443f): ktor.
- [`54d43c079`](https://github.com/simple-robot/simpler-robot/commit/54d43c079): http template core
- [`6b5690c21`](https://github.com/simple-robot/simpler-robot/commit/6b5690c21): http template
- [`1480b6aa1`](https://github.com/simple-robot/simpler-robot/commit/1480b6aa1): 修改部分顺序
- [`39f1ed203`](https://github.com/simple-robot/simpler-robot/commit/39f1ed203): update logger.
- [`83fc0e30c..962fee2f0`](https://github.com/simple-robot/simpler-robot/compare/83fc0e30c..39f1ed203): tips
- [`160afa875`](https://github.com/simple-robot/simpler-robot/commit/160afa875): MessageEventGet更名为MessageGet
- [`034e32129..91a34056e`](https://github.com/simple-robot/simpler-robot/compare/034e32129..160afa875): tips.
- [`2fb0f7c9e`](https://github.com/simple-robot/simpler-robot/commit/2fb0f7c9e): remove test main.
- [`09acaf45b`](https://github.com/simple-robot/simpler-robot/commit/09acaf45b): 夹点儿私货
- [`7b8d549b4`](https://github.com/simple-robot/simpler-robot/commit/7b8d549b4): remove test main.
- [`a9278518f`](https://github.com/simple-robot/simpler-robot/commit/a9278518f): 夹点儿私货

## v2.0.0-ALPHA.10

> Release & Pull Notes: [v2.0.0-ALPHA.10](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.0-ALPHA.10)
>
> Commit compare: [v2.0.0-ALPHA.6..v2.0.0-ALPHA.10](https://github.com/simple-robot/simpler-robot/compare/v2.0.0-ALPHA.6..v2.0.0-ALPHA.10)


## v2.0.0-ALPHA.6

> Release & Pull Notes: [v2.0.0-ALPHA.6](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.0-ALPHA.6)
>
> Commit compare: [v2.0.0-ALPHA.9..v2.0.0-ALPHA.6](https://github.com/simple-robot/simpler-robot/compare/v2.0.0-ALPHA.9..v2.0.0-ALPHA.6)


## v2.0.0-ALPHA.9

> Release & Pull Notes: [v2.0.0-ALPHA.9](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.0-ALPHA.9)
>
> Commit compare: [v2.0.0-ALPHA.5..v2.0.0-ALPHA.9](https://github.com/simple-robot/simpler-robot/compare/v2.0.0-ALPHA.5..v2.0.0-ALPHA.9)

- [`29f075f8b`](https://github.com/simple-robot/simpler-robot/commit/29f075f8b): Update issue templates
- [`dcd67c962`](https://github.com/simple-robot/simpler-robot/commit/dcd67c962): issue templates.

## v2.0.0-ALPHA.5

> Release & Pull Notes: [v2.0.0-ALPHA.5](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.0-ALPHA.5)
>
> Commit compare: [v2.0.0-ALPHA.4..v2.0.0-ALPHA.5](https://github.com/simple-robot/simpler-robot/compare/v2.0.0-ALPHA.4..v2.0.0-ALPHA.5)

- [`b90872984..9ee232d17`](https://github.com/simple-robot/simpler-robot/compare/b90872984..HEAD): move MessageContent package and deploy;
- [`91b1e5cbb`](https://github.com/simple-robot/simpler-robot/commit/91b1e5cbb): interceptor
- [`e9eb85f71`](https://github.com/simple-robot/simpler-robot/commit/e9eb85f71): http template cookies;
- [`f5522ab8a`](https://github.com/simple-robot/simpler-robot/commit/f5522ab8a): mirai level;
- [`ea0469a0e`](https://github.com/simple-robot/simpler-robot/commit/ea0469a0e): lovelycat .
- [`d9ab9f775..5f87d2cad`](https://github.com/simple-robot/simpler-robot/compare/d9ab9f775..ea0469a0e): lovely cat component;
- [`e9df9d6f0`](https://github.com/simple-robot/simpler-robot/commit/e9df9d6f0): update to next alpha version
- [`97dc5d410`](https://github.com/simple-robot/simpler-robot/commit/97dc5d410): deploy a.4 and update log.

## v2.0.0-ALPHA.4

> Release & Pull Notes: [v2.0.0-ALPHA.4](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.0-ALPHA.4)
>
> Commit compare: [v2.0.0-ALPHA.3..v2.0.0-ALPHA.4](https://github.com/simple-robot/simpler-robot/compare/v2.0.0-ALPHA.3..v2.0.0-ALPHA.4)

- [`dedebb076`](https://github.com/simple-robot/simpler-robot/commit/dedebb076): update tips
- [`0a3a257ec`](https://github.com/simple-robot/simpler-robot/commit/0a3a257ec): 优化mirai日志展示
- [`c073d06d5`](https://github.com/simple-robot/simpler-robot/commit/c073d06d5): 暂时移除未完成的可爱猫组件module;
- [`8fb3fd51d`](https://github.com/simple-robot/simpler-robot/commit/8fb3fd51d): try fix #3
- [`2d7b66bbd`](https://github.com/simple-robot/simpler-robot/commit/2d7b66bbd): 用不着，以前写过了..
- [`41d105c4a`](https://github.com/simple-robot/simpler-robot/commit/41d105c4a): mirai组件增加bot的shutdown hook
- [`181a5f6b5`](https://github.com/simple-robot/simpler-robot/commit/181a5f6b5): move configuration
- [`5b40182a8`](https://github.com/simple-robot/simpler-robot/commit/5b40182a8): update some version.
- [`615e65449`](https://github.com/simple-robot/simpler-robot/commit/615e65449): mirai text and base text.
- [`286fc8110`](https://github.com/simple-robot/simpler-robot/commit/286fc8110): fix #2
- [`946787d29`](https://github.com/simple-robot/simpler-robot/commit/946787d29): fix #1
- [`681977f4c`](https://github.com/simple-robot/simpler-robot/commit/681977f4c): lovely cat readme;
- [`5e5537850`](https://github.com/simple-robot/simpler-robot/commit/5e5537850): update kt version.
- [`76369e8da`](https://github.com/simple-robot/simpler-robot/commit/76369e8da): test
- [`1a12a9265`](https://github.com/simple-robot/simpler-robot/commit/1a12a9265): http client template.
- [`cdb651916..edc5ff816`](https://github.com/simple-robot/simpler-robot/compare/cdb651916..1a12a9265): lovely cat component.
- [`3971f84ed`](https://github.com/simple-robot/simpler-robot/commit/3971f84ed): fast json auto configure
- [`9ebd00c5d`](https://github.com/simple-robot/simpler-robot/commit/9ebd00c5d): move ComponentBeans
- [`f8430ce7d`](https://github.com/simple-robot/simpler-robot/commit/f8430ce7d): api bot as accessInfo.
- [`d4dc14d98`](https://github.com/simple-robot/simpler-robot/commit/d4dc14d98): logger.
- [`cddce3f0d`](https://github.com/simple-robot/simpler-robot/commit/cddce3f0d): import clean.
- [`ba27bf0f8`](https://github.com/simple-robot/simpler-robot/commit/ba27bf0f8): test.
- [`92b19e898`](https://github.com/simple-robot/simpler-robot/commit/92b19e898): move message content builder.
- [`f9014bdc0..6ca3498c6`](https://github.com/simple-robot/simpler-robot/compare/f9014bdc0..92b19e898): mirai-bot logger.
- [`c77874453`](https://github.com/simple-robot/simpler-robot/commit/c77874453): ver a.4
- [`4f4ac772f..cce9e5127`](https://github.com/simple-robot/simpler-robot/compare/4f4ac772f..c77874453): component-ding
- [`81f163615`](https://github.com/simple-robot/simpler-robot/commit/81f163615): 白忙活了
- [`54f5b88b0`](https://github.com/simple-robot/simpler-robot/commit/54f5b88b0): moshi json.
- [`5f9fbce0e`](https://github.com/simple-robot/simpler-robot/commit/5f9fbce0e): tips
- [`061423cee`](https://github.com/simple-robot/simpler-robot/commit/061423cee): update logo
- [`bc1add809`](https://github.com/simple-robot/simpler-robot/commit/bc1add809): update copyright; clear import;
- [`80018e2ff..e9bdfdeff`](https://github.com/simple-robot/simpler-robot/compare/80018e2ff..bc1add809): update log.

## v2.0.0-ALPHA.3

> Release & Pull Notes: [v2.0.0-ALPHA.3](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.0-ALPHA.3)
>
> Commit compare: [v2.0.0-ALPHA.2..v2.0.0-ALPHA.3](https://github.com/simple-robot/simpler-robot/compare/v2.0.0-ALPHA.2..v2.0.0-ALPHA.3)

- [`e7e3273af`](https://github.com/simple-robot/simpler-robot/commit/e7e3273af): pom
- [`6806be369`](https://github.com/simple-robot/simpler-robot/commit/6806be369): flag; content;
- [`ca6b687e8`](https://github.com/simple-robot/simpler-robot/commit/ca6b687e8): readme;
- [`5a3f84a86`](https://github.com/simple-robot/simpler-robot/commit/5a3f84a86): img builder; content; filter text test;
- [`846a45bd0`](https://github.com/simple-robot/simpler-robot/commit/846a45bd0): test getText and getMsg;
- [`e6b2f9e03`](https://github.com/simple-robot/simpler-robot/commit/e6b2f9e03): update version; 重新实现 messageContent;
- [`17dde8569`](https://github.com/simple-robot/simpler-robot/commit/17dde8569): core-starter移除部分多余控制台输出
- [`fbe649e0f`](https://github.com/simple-robot/simpler-robot/commit/fbe649e0f): 调整为新的 messageContent 实现。
- [`13d845059`](https://github.com/simple-robot/simpler-robot/commit/13d845059): builder
- [`66a285a4d`](https://github.com/simple-robot/simpler-robot/commit/66a285a4d): msg content.
- [`3cb9e968a`](https://github.com/simple-robot/simpler-robot/commit/3cb9e968a): 注释
- [`8aeabe774`](https://github.com/simple-robot/simpler-robot/commit/8aeabe774): content
- [`abb015f2f`](https://github.com/simple-robot/simpler-robot/commit/abb015f2f): catcode version.
- [`2e48a32b2`](https://github.com/simple-robot/simpler-robot/commit/2e48a32b2): MessageContent-cats
- [`89210ccff`](https://github.com/simple-robot/simpler-robot/commit/89210ccff): :bulb: 添加源码注释
- [`621db829a`](https://github.com/simple-robot/simpler-robot/commit/621db829a): msg content.
- [`7531889ae`](https://github.com/simple-robot/simpler-robot/commit/7531889ae): msg parser.
- [`7179e6628..9c6c00fb8`](https://github.com/simple-robot/simpler-robot/compare/7179e6628..7531889ae): readme.
- [`9211e3c1c..e246878a5`](https://github.com/simple-robot/simpler-robot/compare/9211e3c1c..9c6c00fb8): readmes.
- [`201459e4e..d883a5d90`](https://github.com/simple-robot/simpler-robot/compare/201459e4e..e246878a5): update to alpha.2

## v2.0.0-ALPHA.2

> Release & Pull Notes: [v2.0.0-ALPHA.2](https://github.com/simple-robot/simpler-robot/releases/tag/v2.0.0-ALPHA.2)
>
> Commit compare: [v2.0.0-ALPHA.1..v2.0.0-ALPHA.2](https://github.com/simple-robot/simpler-robot/compare/v2.0.0-ALPHA.1..v2.0.0-ALPHA.2)

- [`530bac111`](https://github.com/simple-robot/simpler-robot/commit/530bac111): readme
- [`6ed3bd24c`](https://github.com/simple-robot/simpler-robot/commit/6ed3bd24c): mirai-starter
- [`77dc6c828`](https://github.com/simple-robot/simpler-robot/commit/77dc6c828): spring metadata
- [`4ecbce643`](https://github.com/simple-robot/simpler-robot/commit/4ecbce643): update springboot-starter; 追加springboot部分配置文件提示。
- [`f0f7837be`](https://github.com/simple-robot/simpler-robot/commit/f0f7837be): 任务执行器; test
- [`fbe5dfeb4`](https://github.com/simple-robot/simpler-robot/commit/fbe5dfeb4): 任务执行器
- [`d1b121b29..81759ca0a`](https://github.com/simple-robot/simpler-robot/compare/d1b121b29..fbe5dfeb4): readme
- [`2a3e943a9`](https://github.com/simple-robot/simpler-robot/commit/2a3e943a9): logger;
- [`7c31644b2`](https://github.com/simple-robot/simpler-robot/commit/7c31644b2): logger; clean import; listener registers;
- [`9021aecb6`](https://github.com/simple-robot/simpler-robot/commit/9021aecb6): Core springboot starter module
- [`2e7e88682`](https://github.com/simple-robot/simpler-robot/commit/2e7e88682): Springboot starter module
- [`496ce2297`](https://github.com/simple-robot/simpler-robot/commit/496ce2297): api-Task runner
- [`4bb9a2eb1`](https://github.com/simple-robot/simpler-robot/commit/4bb9a2eb1): json-fastjson
- [`ec0ec94ca`](https://github.com/simple-robot/simpler-robot/commit/ec0ec94ca): http template.
- [`d1353003c`](https://github.com/simple-robot/simpler-robot/commit/d1353003c): remove some comment
- [`b7412161d..8b226e862`](https://github.com/simple-robot/simpler-robot/compare/b7412161d..d1353003c): http template.
- [`1245d35d4`](https://github.com/simple-robot/simpler-robot/commit/1245d35d4): http-template-ktor
- [`95901e6cc`](https://github.com/simple-robot/simpler-robot/commit/95901e6cc): http-template-core
- [`3a414fc1e`](https://github.com/simple-robot/simpler-robot/commit/3a414fc1e): json-moshi
- [`4c0d22b3f`](https://github.com/simple-robot/simpler-robot/commit/4c0d22b3f): json-core
- [`37c7ca80c`](https://github.com/simple-robot/simpler-robot/commit/37c7ca80c): json-moshi
- [`29ad48f9a`](https://github.com/simple-robot/simpler-robot/commit/29ad48f9a): http client; json core;
- [`5e6e9443f`](https://github.com/simple-robot/simpler-robot/commit/5e6e9443f): http client;
- [`a641d884e..f6512edc4`](https://github.com/simple-robot/simpler-robot/compare/a641d884e..5e6e9443f): ktor.
- [`54d43c079`](https://github.com/simple-robot/simpler-robot/commit/54d43c079): http template core
- [`6b5690c21`](https://github.com/simple-robot/simpler-robot/commit/6b5690c21): http template
- [`1480b6aa1`](https://github.com/simple-robot/simpler-robot/commit/1480b6aa1): 修改部分顺序
- [`39f1ed203`](https://github.com/simple-robot/simpler-robot/commit/39f1ed203): update logger.
- [`83fc0e30c..962fee2f0`](https://github.com/simple-robot/simpler-robot/compare/83fc0e30c..39f1ed203): tips
- [`160afa875`](https://github.com/simple-robot/simpler-robot/commit/160afa875): MessageEventGet更名为MessageGet
- [`034e32129..91a34056e`](https://github.com/simple-robot/simpler-robot/compare/034e32129..160afa875): tips.
- [`2fb0f7c9e`](https://github.com/simple-robot/simpler-robot/commit/2fb0f7c9e): remove test main.
- [`09acaf45b`](https://github.com/simple-robot/simpler-robot/commit/09acaf45b): 夹点儿私货
- [`7b8d549b4`](https://github.com/simple-robot/simpler-robot/commit/7b8d549b4): remove test main.
- [`a9278518f`](https://github.com/simple-robot/simpler-robot/commit/a9278518f): 夹点儿私货
- [`141562a5b`](https://github.com/simple-robot/simpler-robot/commit/141562a5b): readme.
- [`13940ea37`](https://github.com/simple-robot/simpler-robot/commit/13940ea37): version. sb-maven plugin.

