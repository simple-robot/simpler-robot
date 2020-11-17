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
