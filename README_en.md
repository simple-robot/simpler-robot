<!--suppress HtmlDeprecatedAttribute -->
<div align="center">
<a href="https://simbot.forte.love/">
<picture>
  <source media="(prefers-color-scheme: dark)" srcset=".simbot/logo-dark.svg">
  <source media="(prefers-color-scheme: light)" srcset=".simbot/logo.svg">
  <img alt="simbot logo" src=".simbot/logo.svg" width="260" />
</picture>
</a>
<h2>
    - Simple Robot -
</h2>
<small>
        ~ simbot v4 ~      
</small>
<br>
    <span>
        <a href="https://github.com/simple-robot/simpler-robot" target="_blank">GitHub</a>
    </span> 
    &nbsp;&nbsp; | &nbsp;&nbsp;
    <span>
        <a href="https://gitee.com/simple-robot/simpler-robot" target="_blank">Gitee</a>
    </span> <br />
    <small> &gt; Thanks to the members of the <a href="https://github.com/ForteScarlet/CatCode" target="_blank">CatCode</a> development team for creating the simbot logo &lt; </small>
    <br>
    <small> &gt; Passing by? Don't forget to light up a ⭐~ &lt; </small> 
    <br>
   <a href="https://github.com/simple-robot/simpler-robot/releases/latest"><img alt="release" src="https://img.shields.io/github/v/release/simple-robot/simpler-robot" /></a>
<a href="https://repo1.maven.org/maven2/love/forte/simbot/simbot-api/" target="_blank">
  <img alt="release" src="https://img.shields.io/maven-central/v/love.forte.simbot/simbot-api" /></a>
<a href="https://simbot.forte.love" target="_blank">
  <img alt="doc" src="https://img.shields.io/badge/doc-simbot-brightgreen" /></a>
<a href="https://qodana.cloud/projects/p9mmM/reports/79Xen" target="_blank">
  <img alt="Qodana" src="https://github.com/simple-robot/simpler-robot/actions/workflows/qodana_code_quality.yml/badge.svg" /></a>
  <a href="https://deepwiki.com/simple-robot/simpler-robot"><img src="https://deepwiki.com/badge.svg" alt="Ask DeepWiki"></a>
   <hr>
   <img alt="stars" src="https://img.shields.io/github/stars/simple-robot/simpler-robot" />
   <img alt="forks" src="https://img.shields.io/github/forks/simple-robot/simpler-robot" />
   <img alt="watchers" src="https://img.shields.io/github/watchers/simple-robot/simpler-robot" />
   <img alt="repo-size" src="https://img.shields.io/github/repo-size/simple-robot/simpler-robot" />
   <img alt="code-size" src="https://img.shields.io/github/languages/code-size/simple-robot/simpler-robot" />

   <img alt="issues" src="https://img.shields.io/github/issues-closed/simple-robot/simpler-robot?color=green" />
   <img alt="top-language" src="https://img.shields.io/github/languages/top/simple-robot/simpler-robot" />
<a href="./COPYING"><img alt="copying" src="https://img.shields.io/github/license/simple-robot/simpler-robot" /></a>

<br>

</div>

[中文](README.md)

## Introduction

**`Simple Robot`** is a high-performance asynchronous event scheduling framework in the **Bot style** that is built
on [Kotlin coroutines](https://github.com/Kotlin/kotlinx.coroutines)
and [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) (hereinafter referred to as simbot),
delivering efficient asynchrony and Java-friendly APIs.

simbot provides a unified asynchronous API and an intuitive design style to help you create **Bot-style** event-driven
applications quickly and efficiently. It is mainly used to integrate with a wide variety of bot application
platforms/frameworks, and it already offers several component library implementations.

simbot's **platform capabilities** are component-driven. Install different component libraries to gain support for
different features.

For example, using KOOK and QQ Guild in simbot:

```Kotlin
suspend fun main() {
    launchSimpleApplication { config() }
        .joinWith { module() }
}

fun ApplicationFactoryConfigurer<*, *, *>.config() {
    // Install the KOOK and QQ Guild component libraries
    useKook()
    useQQGuild()
}

/**
 * Configure and apply the built `Application`
 */
suspend fun Application.module() {
    registerBots()
    registerListeners()
}

/**
 * Register the required bots
 */
suspend fun Application.registerBots() {
    // Register a KOOK bot so KOOK-related events can be handled afterwards
    kookBots {
        register(...) { ... }.start()
    }

    // Register a QQ Guild bot so QQ Guild-related events can be handled afterwards
    qqGuildBots {
        register(...) { ... }.start()
    }
}

fun Application.registerListeners() {
    listeners {
        // Register an event handler
        // ChatChannelMessageEvent is a generic type defined by the simbot API representing all sub-channel message events
        // This includes QQ Guild public channel message events and KOOK channel message events
        listen<ChatChannelMessageEvent> {
            println("context: $this")
            println("context.event: $event")

            // Return the event handling result
            EventResult.empty()
        }

        // Register another event handler
        // Explicitly listen for QQ Guild public channel message events
        // Using process eliminates the need to return a value
        process<QGAtMessageCreateEvent> {
            println("context: $this")
            println("context.event: $event")
        }

        // Register one more event handler
        // Explicitly listen for KOOK channel message events
        // Using process eliminates the need to return a value
        process<KookChannelMessageEvent> {
            println("context: $this")
            println("context.event: $event")
        }
    }
}
```

## Documentation & Guides

- [Organization Homepage](https://github.com/simple-robot/) Learn more about components, documentation, community, and
  more!
- [Communities](https://simbot.forte.love/communities.html) Community information is also provided in the documentation.
- [Application Manual][doc-homepage]
- [Documentation Portal & API reference](https://docs.simbot.forte.love)

## Support Us

Lighting up a **✨star🌟** for us is the greatest motivation and support for keeping the project going!

- Read the [**Contribution Guide**](docs/CONTRIBUTING_CN.md) to learn how you can contribute!
- Join the conversation with others or the simbot development team via the [**Discussions**][discussions].
- If you have created an awesome open-source project based on simbot, feel free to share it via [ISSUES][issues]
  or [Discussions][discussions] so we can showcase your cool project in the gallery.

## Contact Us

- To report issues, make suggestions, or ask questions, please use [**ISSUES**][issues].
- To communicate with the development team or other developers, head to [**Discussions**][discussions].
- Visit the [GitHub Organization Homepage](https://github.com/simple-robot/) for more **community information**.

[pr]: https://github.com/simple-robot/simpler-robot/pulls

[issues]: https://github.com/simple-robot/simpler-robot/issues

[discussions]: https://github.com/orgs/simple-robot/discussions

## Special Thanks

<a href="https://www.jetbrains.com/?from=simpler-robot">
<img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jetbrains.png" width="200" alt="jetbrains" />
</a>

Thanks to [JetBrains][jetbrains] for providing the team with free licenses. We also encourage everyone to
support [JetBrains][jetbrains], its products, and genuine software.

[jetbrains]: https://www.jetbrains.com/?from=simpler-robot

## Stars!

[![Star History Chart](https://api.star-history.com/svg?repos=simple-robot/simpler-robot&type=Date)](https://star-history.com/#simple-robot/simpler-robot&Date)

> powered by [Star History](https://star-history.com)

## License

Simple Robot is open-sourced under the [LGPLv3](https://www.gnu.org/licenses/#LGPL) license.

```
This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by 
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
Lesser GNU General Public License for more details.

You should have received a copy of the Lesser GNU General Public License 
along with this program.  If not, see <https://www.gnu.org/licenses/>.
```

[doc-homepage]: https://simbot.forte.love/
