<!--suppress HtmlDeprecatedAttribute -->

<div align="center">
    <img src="../../.github/logo/logo.png" alt="logo" style="width:230px; height:230px; border-radius:100%; " />
    <h3>
        - simpler-robot -
    </h3>
    <span>
        <a href="https://github.com/ForteScarlet/simpler-robot" target="_blank">github</a>
    </span> 
    &nbsp;&nbsp; | &nbsp;&nbsp;
    <span>
        <a href="https://gitee.com/ForteScarlet/simpler-robot" target="_blank">gitee</a>
    </span> <br />
    <small> &gt; 感谢 <a href="https://github.com/ForteScarlet/CatCode" target="_blank">CatCode</a> 开发团队成员制作的simbot logo &lt; </small>
    <br>
    <small> &gt; 走过路过，不要忘记点亮一颗⭐喔~ &lt; </small> 
    <br>
   <a href="https://github.com/ForteScarlet/simpler-robot/releases/latest"><img alt="release" src="https://img.shields.io/github/v/release/ForteScarlet/simpler-robot" /></a>
<a href="https://repo1.maven.org/maven2/love/forte/simple-robot/component-kaiheila-parent/" target="_blank">
  <img alt="release" src="https://img.shields.io/nexus/r/love.forte.simple-robot/component-kaiheila-parent?label=simbot-lastVersion&server=https%3A%2F%2Foss.sonatype.org" /></a>
<a href="https://oss.sonatype.org/content/repositories/snapshots/love/forte/simple-robot/component-kaiheila-parent/" target="_blank">
  <img alt="snapshot" src="https://img.shields.io/nexus/s/love.forte.simple-robot/component-kaiheila-parent?label=simbot-snapshot&server=https%3A%2F%2Foss.sonatype.org" /></a>
<a href="https://www.yuque.com/simpler-robot/simpler-robot-doc" target="_blank">
  <img alt="doc" src="https://img.shields.io/badge/doc-yuque-brightgreen" /></a>
   <hr>
   <img alt="stars" src="https://img.shields.io/github/stars/ForteScarlet/simpler-robot" />
   <img alt="forks" src="https://img.shields.io/github/forks/ForteScarlet/simpler-robot" />
   <img alt="watchers" src="https://img.shields.io/github/watchers/ForteScarlet/simpler-robot" />
   <img alt="repo size" src="https://img.shields.io/github/repo-size/ForteScarlet/simpler-robot" />
   <img alt="lines" src="https://img.shields.io/tokei/lines/github/ForteScarlet/simpler-robot" />
   <img alt="issues" src="https://img.shields.io/github/issues-closed/ForteScarlet/simpler-robot?color=green" />
   <img alt="last commit" src="https://img.shields.io/github/last-commit/ForteScarlet/simpler-robot" />
   <a href="../../LICENSE"><img alt="license" src="https://img.shields.io/github/license/ForteScarlet/simpler-robot" /></a>
    </div>










## 简介

### simple-robot简介

☞ [simple-robot](../../README.md)

### 开黑啦组件简介

这是一个对接 [开黑啦平台](https://www.kaiheila.cn/) 的机器人开发框架，是实现了simple-robot(下文简称`simbot`)标准API的通用开发框架。

开黑啦组件提供多个模块来支持开发者开发一个开黑啦bot应用或第三方框架。



## 模块简介

- [核心模块 kaiheila-core](kaiheila-core)

  核心模块提供针对于 [开黑啦开发者平台](https://developer.kaiheila.cn/doc) 以及 [simbot-标准API模块](../../core-api/api) 进行封装，提供一套实现了simbot-api的标准开黑啦标准库。
  其中包括对于[事件](https://developer.kaiheila.cn/doc/event)、[KMarkdown](https://developer.kaiheila.cn/doc/kmarkdown)、通用api标准等基础信息的封装。


- `api模块 component-kaiheila-api-v$version`

  api模块提供针对于 [开黑啦http接口api](https://developer.kaiheila.cn/doc/reference) 的对应版本的封装。

上述其他模块以针对开黑啦为主，面向开黑啦框架、第三方组件等开发者。


- `标准模块 component-kaiheila`

  标准的开黑啦-simbot组件模块，基于上述其他模块以外，整合 [simbot-core](../../UPDATE.MD) 并实现完整的simbot功能，面向bot应用开发者。


<br>

## 使用
```xml
<!-- 开黑啦 V3 API组件 -->
<dependency>
    <groupId>love.forte.simple-robot</groupId>
    <artifactId>component-kaiheila-v3</artifactId>
    <version>0.0.1-PREVIEW</version>
</dependency>
```



## 文档

TODO

## Demo

TODO




## 极简示例

文档-快速开始：https://www.yuque.com/simpler-robot/simpler-robot-doc/qeyorq


### 监听消息

```java
@Beans
public class TestListener {
  /** 发送一句“我收到了”，并再复读收到的所有消息 */
  @OnPrivate
  public void listen(PrivateMsg msg, MsgSender sender) {
    sender.SENDER.sendPrivateMsg(msg, "我收到了");
    sender.SENDER.sendPrivateMsg(msg, msg.getMsgContent());
  }
}
```

### 监听并筛选消息

```java
@Beans
public class TestListener {
  /** 监听群里的 'hi! simbot' 消息并作出回应 */
  @OnGroup
  @Filter("hi! simbot")
  public void listenGroup(GroupMsg msg, MsgSender sender) {
    // 获取发消息的人的账号
    String accountCode = m.getAccountInfo().getAccountCode();
    // 准备at这个人的CatCode
    String at = CatCodeUtil.INSTANCE.getStringTemplate().at(accountCode);
    // 发送消息
    sender.SENDER.sendGroupMsg(m, at + " 我在哦");
  }
}
```

或

```java
@Beans
public class TestListener {
  /** 通过依赖注入得到消息构建器工厂。 */
  @Depend
  private MessageContentBuilderFactory builderFactory;

  /** 监听群里的 'hi! simbot' 消息并作出回应 */
  @OnGroup
  @Filter("hi! simbot")
  public void listenGroup(GroupMsg msg, MsgSender sender){
    // 获取发消息的人的账号
    String accountCode = msg.getAccountInfo().getAccountCode();
    // 获取消息构建器
    MessageContentBuilder builder = builderFactory.getMessageContentBuilder();
    // 构建消息实例
    MessageContent msgContent = builder.at(accountCode).text(" 我在哦").build();
    // 发送消息
    sender.SENDER.sendGroupMsg(msg, msgContent);
  }
}
```


## 协助我
- 你可以通过 [pr](https://github.com/ForteScarlet/simpler-robot/pulls "pull request") 为项目代码作出贡献。
- 你可以通过 [issue](https://github.com/ForteScarlet/simpler-robot/issues "issues") 提出一个建议或者反馈一个问题。
- 你可以通过 [讨论区](https://github.com/ForteScarlet/simpler-robot/discussions "discussions") 与其他人或者simbot开发团队相互友好交流。
- 如果你通过此项目创建了一个很酷的项目，欢迎通过 [issue](https://github.com/ForteScarlet/simpler-robot/issues) 、[讨论区](https://github.com/ForteScarlet/simpler-robot/discussions) 、[QQ群寻找群主](https://jq.qq.com/?_wv=1027&k=1Lopqryf)
  等方式联系团队开发人员，并将你酷酷的项目展示在作品展示区。


## 捐助我
如果你喜欢这个项目，不妨试着 [捐助](https://www.yuque.com/docs/share/43264d27-99a7-4287-97c0-b387f5b0947e) 一下我们，十分感谢。


## 特别鸣谢

[![](https://logonoid.com/images/thumbs/intellij-idea-logo.png "IntelliJ IDEA")](https://www.jetbrains.com/idea/)

感谢 [jetbrains](https://www.jetbrains.com/ "jetbrains") 为团队提供的免费 [IntelliJ IDEA](https://www.jetbrains.com/idea/ "IntelliJ IDEA") 授权，也希望大家能够支持IDEA，支持正版。

*****

[![](../../logo/CatCodeLogo@0,1x.png "CatCode")](https://github.com/ForteScarlet/CatCode)

感谢 [猫猫码](https://github.com/ForteScarlet/CatCode "CatCode") 为本项目提供支持并绘制项目LOGO。



