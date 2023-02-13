---
title: 打包
---

import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';

## Application

在 `Gradle` 中，你可以使用 `application` 插件进行打包（参考：[gradle-plugin: application](https://docs.gradle.org/current/userguide/application_plugin.html#header)）

在Maven或者IDEA自带打包工具中，应该也有类似的功能，但是我实际没上手过所以这部分各位就先自行探索罢。

## Fat Jar
在 `Maven` 或 `Gradle` 下，你可以通过 [Spring Boot 构建应用(打包)](https://spring.io/guides/gs/spring-boot/) 的方式将你的程序打包为一个 `Fat Jar` 并部署到你希望的地方。

<Tabs groupId="build-tool">
<TabItem value="Maven" default>

```xml title=pom.xml
<build>
  <plugins>
  	<plugin>
    	<groupId>org.springframework.boot</groupId>
    	<artifactId>spring-boot-maven-plugin</artifactId>
    	<!-- 也许需要指定版本号  -->
    	<configuration>
            <addResources>true</addResources>
    	</configuration>
    	<executions>
            <execution>
                <goals>
                    <goal>repackage</goal>
                </goals>
            </execution>
        </executions>
    </plugin>
  </plugins>
</build>
```

然后执行 `mvn package` 。

</TabItem>
<TabItem value="Gradle Kotlin DSL">

```kotlin title=gradle.build.kts
plugins {
    // ...
    id("org.springframework.boot")
}
```

然后执行 Task: `bootJar`。

</TabItem>
<TabItem value="Gradle Groovy">

```groovy title=gradle.build
plugins {
    // ...
    id 'org.springframework.boot'
}
```

然后执行 Task: `bootJar`。

</TabItem>
</Tabs>
