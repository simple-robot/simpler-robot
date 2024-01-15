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

