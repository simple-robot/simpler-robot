# simpler-robot

[![](https://img.shields.io/maven-central/v/love.forte.simple-robot/parent)](https://repo1.maven.org/maven2/love/forte/simple-robot/parent/)

## 简介

这是simple-robot的2.x版本。

虽然说是2.x版本，但是内容大幅更改，例如包路径、类定义、api以及模块依赖结构，可以直接当成一个新框架用了。
但是整体性的使用思路与理念不会变。


simpler-robot是一个JVM平台的通用机器人开发框架，基于核心API开发不同平台的机器人应用。

## 支持平台

目前支持的机器人平台：
- QQ
    - mirai
- wechat
    - 可爱猫


计划中准备支持的平台：
- QQ
    - onebot(QQ机器人通用协议)
- TG
- discord    

## 文档

simpler-robot文档：https://www.yuque.com/simpler-robot/simpler-robot-doc

## 其他模块

simpler-robot所独立的公共模块项目：
- github: https://github.com/ForteScarlet/forte-common


simpler-robot所使用的特殊码CatCode：
- github: https://github.com/ForteScarlet/CatCode
- gitee : https://gitee.com/ForteScarlet/CatCode


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



