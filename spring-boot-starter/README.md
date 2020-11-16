# spring-boot-starter 模块

为部分模块/组件 提供Springboot-starter支持。

springboot-starter模块下的内容理应会生成spring相关的metadata以实现配置文件的快速提示。

如果你在`IDEA`中依然无法看到相关的配置文件内容提示，可以尝试安装插件`spring assistant`。



## 安装
你可以选择使用`core-starter(核心starter)`并手动整合其他组件，或者直接选择一个已有的组件starter。

首先使用parent进行版本管理, 由于考虑到spring项目大多数会以`spring-boot-starter-parent`作为父标签，
因此此处使用 `dependencyManagement` 进行版本控制：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>love.forte.simple-robot</groupId>
            <artifactId>parent</artifactId>
            <version>${simbot.version}</version>
            <scope>import</scope>
            <type>pom</type>
        </dependency>
    </dependencies>
</dependencyManagement>
```


如果选择核心starter，导入以下依赖（此处版本使用parent进行版本管理。）：
```xml
<dependency>
    <groupId>love.forte.simple-robot</groupId>
    <artifactId>core-spring-boot-starter</artifactId>
</dependency>
<!-- 以及其他相关依赖... -->
```

如果选择任意一个组件starter，以mirai组件为例：
```xml
<dependency>
    <groupId>love.forte.simple-robot</groupId>
    <artifactId>component-mirai-spring-boot-starter</artifactId>
</dependency>
```



## 使用
在你的启动类或者其他任意相关配置类上标注 `@EnableSimbot`
```java
@EnableSimbot
@SpringBootApplication
public class TestApp {
    public static void main(String[] args) {
        SpringApplication.run(TestApp.class, args);
    }
}
``` 



## 配置
核心或组件的配置与正常使用一致，且simbot-springboot-starter启动器均整合Spring的配置信息，因此可以直接使用Spring的配置文件进行配置。

`core-starter` 提供了一个特殊的专属配置：
```
simbot.core.appClass=xxx.xxx.XxxClass
```
此项配置可选，内容为一个类的全限定路径，*上述的`xxx.xxx.XxxClass`仅为示例*。

可以选择一个标注了`@SimbotApplication`的指定simbot启动类。如果不指定则会使用内置的默认启动器。


## 注意事项

### log

springboot-starter中理应排除了所有的`forte-common.log(日志)`模块，而使用的是springboot的默认日志实现。

如果在使用的时候出现了日志冲突，你可以选择排除掉依赖中的`forte-common.log`日志模块：
```xml
<dependency>
    <groupId>love.forte.simple-robot</groupId>
    <artifactId>core-spring-boot-starter</artifactId>
    <exclusions>
        <exclusion>
            <groupId>love.forte.common</groupId>
            <artifactId>log</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

也可以选择排除掉spring的日志模块而导入simbot中的日志模块：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
    <exclusions>
        <exclusion>
            <artifactId>spring-boot-starter-logging</artifactId>
            <groupId>org.springframework.boot</groupId>
        </exclusion>
    </exclusions>
</dependency>
<!-- 导入forte-common日志模块 -->
<dependency>
    <groupId>love.forte.common</groupId>
    <artifactId>log</artifactId>
</dependency>
```

### 依赖

虽然`simbot`可以通过 `@Beans` 注入Springboot中的内容，但是Spring却无法通过 `@Component` 注入 `simbot` 中的部分内容。
在starter中，我整合了部分可能会用到的依赖并注入到了Spring中，但是难免会有疏漏。
你可以通过构建一个自定义的Configuration，并或者借助 `DependCenter` 来手动获取一些 `simbot` 中才会存在的依赖。







