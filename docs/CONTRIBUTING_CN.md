_中文_ | [English](CONTRIBUTING.md)

# 如何参与贡献

**♥** 首先，非常感谢您愿意花时间阅读本指南、并了解如何为 Simple Robot 做出贡献！ **♥**

本指南会主要介绍如何向核心库
[Simple Robot](https://github.com/simple-robot/simpler-robot) (下文简称 _simbot核心库_ )
以及组织 [Simple Robot 组织库][组织首页]、[Simple Robot 图书馆](https://github.com/simple-robot-library)
中的大部分库贡献您的一份力量！

## 变得友好

不论是在提交反馈、提交贡献还是社区讨论，我们都应做到友好地对待一切。
不使用过激言语，不对他人进行人身攻击或言语骚扰，不歧视任何地区/国家/种族/性别，不违反公序良俗与法律道德。

## 我发现了 Bug
### Issues

如果您在simbot核心库或官方维护的组件库中发现了一个 bug，那么首先您可以前往
[核心库 Issues](https://github.com/simple-robot/simpler-robot/issues/new/choose)
中选择合适的分类 (例如 '问题反馈') 并提交与此 bug 相关的详细信息 (包括使用的库版本、错误日志等);

在反馈时，我们建议您使用 Markdown 语法[^about write] 来提高反馈内容的可读性与观感，这也可以帮助参与者快速阅读与定位问题所在。

[^about write]: [关于在 GitHub 上编写和设置格式](https://docs.github.com/zh/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/about-writing-and-formatting-on-github)

### Pull Request

如果您有兴趣与能力为我们贡献此 bug 的修复，
那么欢迎您通过 [Pull Request](https://github.com/simple-robot/simpler-robot/pulls) 提交您的变更。
当您提交代码变更时，需要了解如下几点：

**代码贡献**

您可以在 [代码贡献](#代码贡献) 小节阅读到更多有关代码贡献的细节。

**参与者**

包括核心库与官方组件库在内，它们都会在项目中存在配置参与者的地方。
本指南以simbot核心库为例，在 `buildSrc/src/main/kotlin/P.kt` 中的如下代码内添加有关您的信息:

```Kotlin
override val developers: List<Developer> = developers {
    developer {
        ...
    }
    ...

    此处添加您的信息
}
```

## 讨论区贡献

在 [讨论区][讨论区] 留下你的疑问或回答，
为其他来者留下一盏指路的明灯~

## 代码贡献
### 注释风格

simbot核心库对源代码的注释有着一些约定。

* 要对所有访问级别为 `public`、`protected` 的内容 (包括类/接口、函数、属性等)
  使用**中文**或**易懂的英文**编写较为细致的文档注释[^KDoc]。
  适当地添加文档 tag (例如 `@param`、`@throws`)，不过如果在文档正文中已经提及那么也可省略。

* 内部访问级别 (例如 `private`、`internal`) 的内容，如果十分简单也可以不用编写完全细致的**文档注释**，
  但应适当添加一些有助于其他开发者阅读与理解的解释性注释。

* 被重写的抽象函数，如果其含义没有变化，那么可以省略文档注释。

* 对于类和接口类型，如果是新增的内容，尽可能添加您的作者信息 (`@author`) 和版本信息 (`@since`)。
  不要添加非标准的 tag (比如 `@Date`、`@HelloWorld`)。

[^KDoc]: [Document Kotlin code: KDoc](https://kotlinlang.org/docs/kotlin-doc.html)

### 代码风格

simbot核心库对整体的代码风格有着一些约定。

这些代码风格的大部分规则被作为配置文件保存在了 `.editorconfig` 中，部分IDE (例如 IDEA) 应当可以自动识别加载、或手动加载它。
这些风格与
[Kotlin Coding conventions](https://kotlinlang.org/docs/coding-conventions.html)
中的大部分描述很类似，你也可以以它为参考。

下面会再列举一些常见的风格约定。

* 中缀表达式 (例如 `infix` 或数字运算符 `+` 、`-` 等) 前后应当包含一个空格。例如应该是 `a += b` 而不是 `a+=b`。

* 变量的名称与类型之间，`:` 之后应该有一个空格。例如应该是 `a: Int` 而不是 `a:Int`。

* 函数的参数 (包括函数的定义与调用时的入参) 在同一行时，分隔的 `,` 后应当有一个空格。
  例如应该是 `foo(1, 2)` 而不是 `foo(1,2)`; 应该是 `function name(a: Int, b: Int)` 而不是 `function name(a: Int,b: Int)`

* 定义的函数参数较多、需要换行时，各参数应各自占一行且头部对齐，且不与 `(` 和 `)` 在同一行；如果参数需要添加注解，每个注解应该各自占一行，例如：
    ```Kotlin
    function name(
        p1: Int,
        @Anno2
        @Anno1
        p2: String
    )
    ```

### KMP

simbot核心库绝大多数模块都是支持 [KMP](https://kotlinlang.org/docs/multiplatform.html) 的。

### 二进制兼容

在版本迭代中应提供更好的兼容性保证。因此当一个 API 被废弃或需要被修改时，应保证修改后的内容对旧版本的二进制兼容，
并添加适当的废弃说明与警告。

新增的实验性内容也应当标记合适的 Opt 注解与适当的提示来向开发者发出警告。

### 友好 API

simbot核心库对外提供的公开API中，都应该是 Java 友好、或有配套的 Java 友好 API，尤其指可挂起函数 `suspend fun`。
大部分挂起函数的 Java 友好 API 通过编译器插件
[Kotlin Suspend Transform compiler plugin](https://github.com/ForteScarlet/kotlin-suspend-transform-compiler-plugin) 实现，
可参阅手册中的
[Java友好](https://simbot.forte.love/java-friendly.html) 章节
和
[组件开发 - 编译器插件](https://simbot.forte.love/component-dev-compiler-plugin.html)
章节。

## 文档贡献
### API文档

API文档是使用 [Dokka](https://github.com/Kotlin/dokka) 根据源码的文档注释生成的。你可以在小节 [注释风格](#注释风格)
中了解更多。

### 手册贡献

simbot4
核心库的手册在一个独立的库中: [simple-robot-library/simbot4-website](https://github.com/simple-robot-library/simbot4-website/)。
前往此仓库了解并参与贡献吧~!

## 社区贡献
### 应用作品

基于 simbot 开发的应用项目，并为项目标记 `simbot` tag，为社区生态添注活力！

### 组件库/插件库/其他

基于 simbot 开发组件库/插件库，并为项目标记 `simbot` tag，为社区生态添注活力！
你可以：
- 前往手册的 [组件库开发](https://simbot.forte.love/component-dev.html) 了解更多开发组件库的细节！
- 前往 [讨论区][讨论区] 向其他人或开发团队寻求协助！
- 前往 [组织首页][组织首页] 添加其中的 **社群**，并寻求协助！

[组织首页]: https://github.com/simple-robot
[讨论区]: https://github.com/orgs/simple-robot/discussions
