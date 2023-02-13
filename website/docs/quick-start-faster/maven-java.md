---
title: Maven & Java
sidebar_position: 1
---

本章节介绍通过 [simple-robot/simbot-archetypes](https://github.com/simple-robot/simbot-archetypes) 快速搭建基于Maven的Java样板项目。

:::caution 注意

文档内容很可能远不及仓库内容更新及时。最好的选择，前往[**原仓库**](https://github.com/simple-robot/simbot-archetypes)阅读其说明并使用，
本章节仅作抛砖引玉之意。

:::

## 环境

确保当前系统环境中存在 [Maven](https://maven.apache.org/) 与Java。

## 使用
### 创建项目

选择一个你想要创建项目的目录，并在当前目录下执行下述命令：

```shell
mvn archetype:generate \
  -DarchetypeGroupId="love.forte.simbot.archetypes" \
  -DarchetypeArtifactId="simbot-maven-java-archetype" \
  -DarchetypeVersion="<version>" \
  -DgroupId=<my.groupid> \
  -DartifactId=<my-artifactId>
```

替换上述属性 `<version>`、`<my.groupId>`、`<my-artifactId>` 属性为你新建项目的所需属性。

:::tip 版本?

`version` 信息可前往[**原仓库**](https://github.com/simple-robot/simbot-archetypes)查看。

:::

### 补充信息

maven可能会在后续继续询问、补充部分内容，你可以选择一路 <kbd>enter</kbd> 过去，或者稍微填写一下。

### 创建完成

等待maven初始化项目完成，然后即可将此项目导入到IDE中，根据示例代码中的说明开始上手。
