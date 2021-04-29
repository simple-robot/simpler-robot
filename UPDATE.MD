# now

## 核心
- 为 `MsgInterceptChainFactory` 和 `ListenerInterceptChainFactory` 增加一个 `isEmpty` 用于判断当前是否存在相关拦截，
以解决在用户未使用任何拦截器的情况下依旧创建了大量拦截器所需类的问题，以节约资源使用。(#90)

- 尝试修复一段时间运行后卡死(无法接收处理消息)的问题。 (#92)

## mirai组件
- 简单调整 mirai组件内部的 `BotAliveThread` 终止方式。
- 消息撤回支持解析普通的flagContent.id。（即支持消息标识序列化）
你可以：
```java
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


