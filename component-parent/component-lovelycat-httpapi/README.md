# lovely cat httpapi 组件



## 配置
```yaml

```

## 注意事项
- 可爱猫官方论坛：http://www.keaimao.com.cn , 
可爱猫相关资源文件贴：http://www.keaimao.com.cn/forum.php?mod=viewthread&tid=127&extra=page%3D1

- 可爱猫组件所测试并对接的为`httpAPI 2.0(低配版)`插件

- 可爱猫组件中绝大部分 `xxxInfo`中都只能获取到code和nickname，而无法获取到备注与头像信息。尤其为头像信息，基本上没有。

- 可爱猫账号ID与群ID均为字符串，不可转化为数字。

### CatCode
- 通过MessageBuilder的图片流或字节数组构建的时候，会将图片转化为base64数据，效率较低。
- 可以发送文件、视频，但是只能使用**可爱猫**所在机器的文件**绝对路径**发送。

- 发送视频使用 `video` 类型的cat码，参数为`file`, 指向可爱猫应用的本地文件路径。（未测试）
- 发送文件使用 `file` 类型的cat码，参数为`file`, 指向可爱猫应用的本地文件路径。（未测试）



### Sender
- 发送消息不支持撤回。
- 不支持群签到
- 发送存在**at全体**的消息时等效于**修改群公告**
- 通过Builder构建消息的时候，如果存在多个消息类型，消息类型发送的顺序为先发图片后发文本。


### Getter
尚未实现。



### Setter
尚未实现。


