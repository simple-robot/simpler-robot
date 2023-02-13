---
authors: forliy
title: 2022年第12周周报
tags: [2022周报,周报]
---

2022年第12周周报喵。

<!--truncate-->

## 📖 文档持续更新
文档从 [语雀](https://www.yuque.com/simpler-robot/simpler-robot-doc/mudleb) 向此处迁移的任务仍在持续进行。本周有如下的相关内容被迁移或者编写：
- **基础内容** 及相关章节。
- 调整文档结构，部分与"概述"相关内容整合到一个 **概述** 目录中。
- **走马观花** 及相关章节。
- **概述/依赖注入** 及相关章节。
- **组件** 及相关章节。
- **标准定义/预期能力** 及相关章节。
- 其他一些暂时想不起来的内容。

<hr />

## 🛖 优化仓库引导
虽然暂时还没有明确的说明，但是实际上目前与 simbot 相关的仓库/组织有三个主要场所：

### 主仓库
首先是 `Simple Robot` 的主代码仓库 [ForteScarlet/simpler-robot](https://github.com/simple-robot/simpler-robot) , 
这里是 simbot 基础库（api模块、核心模块、boot模块、spring boot starter模块等等）内容的所属仓库，也是主要仓库。

### Simple Robot 附属库
其次，是存放所有与 `Simple Robot` 相关，但是与基础内容无关的组织库 [simple-robot](https://github.com/simple-robot) 。
此组织下所有的仓库基本上都属于 `Simple Robot` 的相关附属产物，例如各种组件库、工具、示例项目等。

其实我在考虑是否要把 [simpler-robot](https://github.com/simple-robot/simpler-robot) 也迁移到这里，因为这样可以使例如 issue 等内容能够
在组织内仓库间相互转移、能够统一同步到 Gitee 等。但是这样做的其他影响我还不确定，毕竟对于 GitHub 我并不是十分熟悉。这件事总而言之先放放吧。

### Simple Robot 图书馆
这是与 `Simple Robot` 所相关的另外一个组织库 [simple-robot-library](https://github.com/simple-robot-library) , 
此组织库主要存放各种文档、网站相关的库，绝大部分都是自动构建发布的。比如当前文档网站是通过 [simbot-robot-library/simbot3-website](https://github.com/simple-robot-library/simbot3-website)
仓库进行构建的。

这里实际上没有什么实质上能够看的"代码"，他们的主要作用是用来构建文档、网站的。

### Gitee镜像
除了上述的几个 仓库/组织库 以外，gitee中，有与 [**GitHub** ForteScarlet/simpler-robot](https://github.com/simple-robot/simpler-robot)
和 [**GitHub** simple-robot](https://github.com/simple-robot) 相对应的镜像库, 它们分别为 
[**Gitee** ForteScarlet/simpler-robot](https://gitee.com/ForteScarlet/simpler-robot) 和 [**Gitee** simple-robot-project](https://gitee.com/simple-robot-project) 。

对应关系如此图：

| GitHub                                          | Gitee                                          |
|-------------------------------------------------|------------------------------------------------|
| <https://github.com/simple-robot/simpler-robot> | <https://gitee.com/ForteScarlet/simpler-robot> |
| <https://github.com/simple-robot>               | <https://gitee.com/simple-robot-project>       |



其中，[**Gitee** simple-robot-project](https://gitee.com/simple-robot-project) 中的部分主要仓库会定期自动更新（比如部分组件仓库），
但是 [**Gitee** ForteScarlet/simpler-robot](https://gitee.com/ForteScarlet/simpler-robot) 目前暂时不会，是由我什么时候想起来了就去手动点一下同步的方式进行的。😋


### 引导优化
回到主题，我们优化了组织库 [simple-robot (附属库) ](https://github.com/simple-robot) 
和组织库 [simple-robot-library (图书馆) ](https://github.com/simple-robot-library)
的首页引导，它不再如以前那般空旷了。也许引导内容仍旧比较简陋，但是这是个不错的开始，不是吗？👀

<hr />

## 🔧 Mirai辅助工具
mirai的简易辅助工具 [simbot-mirai-login-helper](https://github.com/simple-robot/simbot-mirai-login-helper) 
更新了小版本： [v3.0.10](https://github.com/simple-robot/simbot-mirai-login-helper/releases/tag/v3.0.10) ，优化补充了一些小内容。

<hr />


## 🚫 群规细则
我在本站增加了一个 [群规细则](/group-rule) 页面，用来记录在群内的所有违规行为及其处罚措施。各位要以和谐共处为目的友善交往喔～

目前，[群规细则](/group-rule) 没有显式入口，你可以通过修改路径为 `/group-rule` 进行跳转。未来也许会考虑在相关的适当位置追加入口。

<hr />


## 📚 复习
继续十分痛苦但是充满偷懒的复习4月份要考试的东西。😢  

*<small>讲道理，这时候恐怕已经来不大及辽</small>*



