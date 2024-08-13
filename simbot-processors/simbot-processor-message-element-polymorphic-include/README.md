# Message element polymorphic include

为所有 `Message.Element` (或某个指定子类) 生成一个注册多态序列化信息的聚合函数的 KSP 处理器，
常用于组件开发等场景。

```kotlin
// 生成类似于如下的API
internal fun PolymorphicModuleBuilder<Message.Element>.includeElements() {
    subclass(...)
    subclass(...)
    subclass(...)
}

```

会寻找指定的类型的所有**非抽象**、**非接口**、且标记了 `kotlinx.serialization.Serializable` 注解的子类。

可选配置项：

```kotlin
// 选项参数以 `simbot.processor.message-element-polymorphic-include` 开头
ksp {
    // 是否启用
    arg("simbot.processor.message-element-polymorphic-include.enable", "true")
    arg("simbot.processor.message-element-polymorphic-include.localOnly", "false")
    arg("simbot.processor.message-element-polymorphic-include.baseClass", "love.forte.simbot.message.Message.Element")
    arg("simbot.processor.message-element-polymorphic-include.visibility", "internal") // 默认 internal 可选: public, internal
    arg("simbot.processor.message-element-polymorphic-include.generateFunName", "includeMessageElementPolymorphic")
    arg("simbot.processor.message-element-polymorphic-include.outputPackage", "love.forte.simbot.message") // 默认为 null，如果不提供（不是空字符串哦）则与 baseClass 同包
    arg("simbot.processor.message-element-polymorphic-include.outputFileName", "MessageElementPolymorphicInclude.generated")
    arg("simbot.processor.message-element-polymorphic-include.outputFileJvmName", "") // 默认 null
    arg("simbot.processor.message-element-polymorphic-include.outputFileJvmMultifile", "false") // 默认 false
}
```
