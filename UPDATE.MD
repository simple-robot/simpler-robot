# now

## 核心
- 为 `MsgInterceptChainFactory` 和 `ListenerInterceptChainFactory` 增加一个 `isEmpty` 用于判断当前是否存在相关拦截，
以解决在用户未使用任何拦截器的情况下依旧创建了大量拦截器所需类的问题，以节约资源使用。(#90)

- 尝试修复一段时间运行后卡死(无法接收处理消息)的问题。 (#92)

- 尝试优化 `AtDetection` 相关的效率。

## mirai组件
- 简单调整 mirai组件内部的 `BotAliveThread` 终止方式。
- 为mirai组件中的 `MiraiMessageContentBuilder` 提供更多支持原生mirai消息构建的方法。  
    
  简单示例：
  ```java
        
      /** 注入得到mirai组件环境下的 MiraiMessageContentBuilderFactory 工厂。 */  
      @Depend
      private MiraiMessageContentBuilderFactory builderFactory;
      
      @Listen(GroupMsg.class)
      public void listen(Sender sender) {
          MiraiMessageContentBuilder builder = builderFactory.getMessageContentBuilder();
  
          // 以下这些是Mirai的原生消息信息
          At at1 = new At(123);
          AtAll atAll = AtAll.INSTANCE;
          PlainText text = new PlainText("这是一段文字");
  
          // 构建一个原生的Mirai消息链
          MessageChainBuilder nativeMsgBuilder = new MessageChainBuilder();
          MessageChain chain = nativeMsgBuilder
                  .append(at1)
                  .append(atAll)
                  .append(text)
                  .build();
  
  
          builder = builder
                  // 这是simbot所提供的构建方法
                  .at("123123")
                  // 将这个消息链通过 builder.message 植入当前的simbot消息构建器
                  .message(chain);
  
          /*
              mirai组件下提供了以下方法来支持原生mirai消息拼接：
              - message(Message)                    // Message是原生mirai的类型
              - singleMessage(SingleMessage)        // SingleMessage是原生mirai的类型
              - messageContent(MiraiMessageContent) // MiraiMessageContent是simbot-mirai组件提供的包装类型
  
              // 这是为kotlin使用者调用的方法，提供一个Neko(可省略),
              // 和一个 suspend函数，此函数接收一个 Content 参数(一般为发送目标的对象，例如一个群或者一个好友), 并通过这个目标得到一个 SingleMessage示例。
              - message(Neko?, suspend (Contact) -> SingleMessage)
  
              // 这是为Java使用者调用的方法，含义与上面这个一样，只不过函数不是suspend类型。
              - messageLazy(Neko?, (Contact) -> SingleMessage)
  
              // 对于上述这两个有函数的方法，如果你想发送的消息无法返回一个 SingleMessage, 而是直接调用方法发送（例如mirai发送头像戳一戳nudge），
              // 那么你可以在发送完成后返回一个:
              // Java: EmptySingleMessage.INSTANCE
              // Kotlin: EmptySingleMessage
           */
  
          // 构建为消息正文，并发送
          MiraiMessageContent build = builder.build();
          sender.sendGroupMsg(123456, build);
      }
  ```

- 消息撤回支持解析普通的flagContent.id。（即支持消息标识序列化）
  
  需要注意的是，这次支持并不会兼容之前版本的flagContent.id，只有2.0.6及之后版本的id允许反序列化。（因为变更了id的规则和内容）
  
  简单示例：
  ```java
       @Listen(GroupMsg.class)
       public void listen(GroupMsg msg, Setter setter) {
           // 这是正常得到的flag。
           GroupMsg.MessageFlag flag = msg.getFlag();
           // 得到这个flag的id。
           String id = flag.getFlag().getId();
           System.out.println("id = " + id);
   
           // 假设你中途序列化了，并只保存了ID值。
           // 你可以通过 Flags.getFlag 或者 Flags.getFlagById 进行反序列化。
           // 方法1：Flags.getFlag
           // getFlag需要一个FlagContent参数，
           // 对于群消息，你可以使用 GroupMsg.FlagContent.byId(id)
           // 对于私聊消息，你可以使用 PrivateMsg.FlagContent.byId(id)
   
           Flag<GroupMsg.FlagContent> flagById1 = Flags.getFlag(GroupMsg.FlagContent.byId(id));
   
           // 方法2：Flags.getFlagById
           // getFlagById需要一个ID参数，以及一个 “通过ID获取Content” 的函数。
           // 关于第二个函数参数，
           // 对于群消息，你可以使用 Flags.getFlagById(id, GroupMsg.FlagContent::byId);
           // 这等同于 Flags.getFlagById(id, id -> GroupMsg.FlagContent.byId(id));
           // 对于私聊消息，你可以使用 Flags.getFlagById(id, PrivateMsg.FlagContent::byId);
           // 这等同于 Flags.getFlagById(id, id -> PrivateMsg.FlagContent.byId(id));
   
           Flag<GroupMsg.FlagContent> flagById2 = Flags.getFlagById(id, GroupMsg.FlagContent::byId);
   
   
           // 反序列化后进行撤回
           setter.setMsgRecall(flagById1);
       }
   ```


