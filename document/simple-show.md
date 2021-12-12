<!--suppress HtmlDeprecatedAttribute -->
<div align="center">
    <img src="../.github/logo/logo.png" alt="logo" style="width:230px; height:230px; border-radius:50%; "/>
    <h3>
        - simpler-robot | 极简示例 -
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
<a href="https://repo1.maven.org/maven2/love/forte/simple-robot/parent/" target="_blank">
  <img alt="release" src="https://img.shields.io/nexus/r/love.forte.simple-robot/parent?label=simbot-releases&server=https%3A%2F%2Foss.sonatype.org" /></a>
<a href="https://oss.sonatype.org/content/repositories/snapshots/love/forte/simple-robot/parent/" target="_blank">
  <img alt="snapshot" src="https://img.shields.io/nexus/s/love.forte.simple-robot/parent?label=simbot-snapshot&server=https%3A%2F%2Foss.sonatype.org" /></a>
<a href="https://www.yuque.com/simpler-robot/simpler-robot-doc" target="_blank">
  <img alt="doc" src="https://img.shields.io/badge/doc-yuque-brightgreen" /></a>
   <hr>
   <img alt="stars" src="https://img.shields.io/github/stars/ForteScarlet/simpler-robot" />
   <img alt="forks" src="https://img.shields.io/github/forks/ForteScarlet/simpler-robot" />
   <img alt="watchers" src="https://img.shields.io/github/watchers/ForteScarlet/simpler-robot" />
   <img alt="repo size" src="https://img.shields.io/github/repo-size/ForteScarlet/simpler-robot" />
   <img alt="lines" src="https://img.shields.io/tokei/lines/github/ForteScarlet/simpler-robot" />
   <a href="https://github.com/ForteScarlet/simpler-robot/releases/latest"><img alt="release" src="https://img.shields.io/github/v/release/ForteScarlet/simpler-robot" /></a>
   <img alt="issues" src="https://img.shields.io/github/issues-closed/ForteScarlet/simpler-robot?color=green" />
   <img alt="last commit" src="https://img.shields.io/github/last-commit/ForteScarlet/simpler-robot" />
   <a href="../LICENSE"><img alt="license" src="https://img.shields.io/github/license/ForteScarlet/simpler-robot" /></a>
    </div>




### 监听消息

```java
@Beans
public class TestListener {
  /** 发送一句“我收到了”，并再复读收到的所有消息 */
  @OnPrivate
  public void listen(PrivateMsg msg, Sender sender) {
    sender.sendPrivateMsg(msg, "我收到了");
    sender.sendPrivateMsg(msg, msg.getMsgContent());
  }
}
```

### 监听并筛选消息

```java
@Beans
public class TestListener {
  /** 
   * 监听群里的 'hi! simbot' 消息并作出回应 
   * 这里使用 MsgSender 来获取一个送信器。
   * MsgSender中包含 SENDER、SETTER、GETTER三个送信器。
   * */
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
  public void listenGroup(GroupMsg msg, Sender sender){
    // 获取发消息的人的账号
    String accountCode = msg.getAccountInfo().getAccountCode();
    // 获取消息构建器
    MessageContentBuilder builder = builderFactory.getMessageContentBuilder();
    // 构建消息实例
    MessageContent msgContent = builder.at(accountCode).text(" 我在哦").build();
    // 发送消息
    sender.sendGroupMsg(msg, msgContent);
  }
}
```
