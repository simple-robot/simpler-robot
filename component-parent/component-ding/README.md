# 钉钉群机器人组件

提供了对钉钉机器人的送信整合。

## 说明

你可以在任何组件中使用钉钉机器人模组，并使其与您的机器人应用相结合。

钉钉机器人模组中所使用的交互方式来源于钉钉机器人[官方文档](https://ding-doc.dingtalk.com/doc#/serverapi3/iydd5h)。


## 配置文件：
```properties
# suppress inspection "UnusedProperty" for whole file
# 此处可支持多个钉钉bot，格式与simbot.core.bots格式类型
# 关于secret和access_token, 分比对应code和path
# access_token就是注册了bot之后，给你的webhook地址后的那个access_token参数。此参数是必须存在的
# secret是钉钉机器人三种安全策略的第二种，可以生成，也可以不存在。
# 具体请查看钉钉机器人官方文档：https://ding-doc.dingtalk.com/doc#/serverapi3/iydd5h/404d04c3
# 例如：
# simbot.ding.bots=secret:access_token
# 其中，secret可省略，则为：
# simbot.ding.bots=:access_token
simbot.component.ding.bots=${secret}:${access_token}
# 钉钉bot的webhook地址，不要携带任何参数, 如果不填默认值为 https://oapi.dingtalk.com/robot/send
simbot.component.ding.webhook=https://oapi.dingtalk.com/robot/send
```



## 使用

### 导入依赖：

[![img](https://img.shields.io/maven-central/v/love.forte.simple-robot/component-ding)](https://repo1.maven.org/maven2/love/forte/simple-robot/component-ding/)

```xml
<dependency>
    <groupId>love.forte.simple-robot</groupId>
    <artifactId>component-ding</artifactId>
    <version>${version}</version>
</dependency>
```

### 注入
注入`DingSenderManager`类来使用你的钉钉机器人。获取机器人的key为`access_token`


关于`DingSenderManager`发送消息的类型，我提供了三种方式：
- 使用字符串
- 使用`DingSpecialMessageChain`
- 使用`DingSpecialMessage`

其中，`DingSpecialMessageChain`代表多个`DingSpecialMessage`的集合体，一般用于将普通消息与at类型消息合并在一起的时候使用。

关于字符串，它会将非Cat码部分解析为Text类型的消息，而Cat码部分会解析成对应的类型。（关于可解析的Cat码，下面的代码中的注释里会有简单介绍。）

而关于`DingSpecialMessage`，他是所有的钉钉机器人可发送的消息类型的接口，我提供了如下的实现类：

```java

        // 钉钉 text类型的消息
        // [Cat:at,qq=123,all,456,789]
        // 或者
        // [Cat:at,code=123,all,456,789]
        DingText dingText = new DingText("123");

        // 钉钉 at类型的消息
        // 参数1 at的人
        // 参数2 是否at全体, 默认为false
        DingAt dingAt = new DingAt(new String[]{"1111", "2222", "3333"});
        // 只at全体的ding at
        DingAt atAll = DingAt.getAtAll();
        // 不at任何人的空at
        DingAt emptyAt = DingAt.getEmpty();


        // 钉钉 markdown类型的消息
        // [Cat:ding-markdown,title=标题,text=正文]
        DingMarkdown markdown = new DingMarkdown("标题", "正文");

        // 可以通过DingMarkDownBuilder构造markdown
        DingMarkdownBuilder builder = new DingMarkdownBuilder("标题");
        DingMarkdown markdown2 = builder
                .h1("标题1")
                .h2("标题2")
                .h3("标题3")
                .h4("标题4")
                .orderedList("有序1", "有序2", "有序3")
                .build();

        System.out.println(markdown2.getText());


        // 钉钉 link类型
        // 参数：
        /*
        title
        text
        messageUrl
        picUrl 默认为null
         */
        // [Cat:ding-link,title=标题,text=正文,messageUrl=链接]
        DingLink link = new DingLink("标题", "正文", "文本链接");

        // 钉钉 独立跳转链接
        // [Cat:ding-actionCard,title=标题.text=正文,btnOrientation=0,btnTitle=t1&#44;t2,btnUrl=url1&#44;url2]
        // 其中，&#44;是逗号的转移。一般不用管，当成btnTitle=1,2就行
        DingAutonomyActionCard dingAutonomyActionCard = new DingAutonomyActionCard("title", "text", "1", new DingAutonomyActionCardButtons[]{new DingAutonomyActionCardButtons("标题", "链接")});
        // 钉钉 整体跳转链接
        // [Cat:ding-actionCard,title=标题.text=正文,btnOrientation=0,btnTitle=t1,btnUrl=url1]
        DingWholeActionCard dingWholeActionCard = new DingWholeActionCard("title", "text", "1", "button title", "button url");


        // 钉钉 FeedCard 暂时不支持CQ码解析
        DingFeedCard feedCard = new DingFeedCard(new DingFeedCardLink[]{new DingFeedCardLink("标题", "图片链接", "跳转链接")});

```


## 自定义

钉钉机器人模组内置的一些东西可以通过自定义来实现定制化。

注①：以下提及的接口在注入依赖的时候需要使其的优先级(`@Beans`的`priority`参数)大于`PriorityConstant.COMPONENT_TENTH`

注②：以下提及的可定制化接口的具体说明请参考源代码的注释，此处不会过于详细的介绍。

### DingSecretCalculator
> love.forte.simbot.component.ding.sceret.DingSecretCalculator

签名计算器，用于计算钉钉机器人交互时候的密钥签名


### DingSenderBuilder
> love.forte.simbot.component.ding.sender.DingSenderBuilder

接收参数并构建`DingSender`实例的构建器。


### DingSenderManager
> love.forte.simbot.component.ding.sender.DingSenderManager

最终得到的`DingSenderManager`实例。不过一般通过定义`DingSenderBuilder`即可实现大部分的需求。




