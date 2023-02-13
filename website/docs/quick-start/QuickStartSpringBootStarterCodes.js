import React from 'react';
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';

function mavenCode(version) {
    return `<!-- Spring Boot 相关。此处选择使用parent对Spring进行版本控制 -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <!-- Spring Boot 版本 -->
    <version>3.0.0</version>
    <relativePath/> <!-- lookup parent from repository -->
  </parent>

<dependencies>
    <!-- 引入你所需要的Spring Boot依赖。这里假设你需要使用 web 相关功能 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- simbot Spring Boot Starter -->
    <dependency>
        <groupId>love.forte.simbot.boot</groupId>
        <artifactId>simboot-core-spring-boot-starter</artifactId>
        <version>${version.simbot.version}</version>
    </dependency>
    <!-- 给其他组件留个板凳... -->
    <!-- ... -->
    
</dependencies>
  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>
  
`.trim()}

function gradleKts(version) {
    return `plugins {
  id("org.springframework.boot") version "3.0.0" // Spring Boot
  id("io.spring.dependency-management") version "1.0.11.RELEASE"
  \`java\`
}

group = "..."
version = "..."

repositories {
  mavenCentral()
}

// 使用你所需要的Spring Boot依赖。这里假设你需要使用web相关内容。
implementation("org.springframework.boot:spring-boot-starter-web")

// simbot Spring Boot Starter
implementation("love.forte.simbot.boot:simboot-core-spring-boot-starter:${version.simbot.version}")

// 给其他组件留个板凳...
// ...
`.trim()}

function gradleGroovy(version) {
    return `plugins {
  id 'org.springframework.boot' version '3.0.0' // Spring Boot
  id 'io.spring.dependency-management' version '1.0.11.RELEASE'
  id 'java'
}

group = '...'
version = '...'

repositories {
  mavenCentral()
}

// 使用你所需要的Spring Boot依赖。这里假设你需要使用web相关内容。
implementation 'org.springframework.boot:spring-boot-starter-web'

// simbot Spring Boot Starter
implementation 'love.forte.simbot.boot:simboot-core-spring-boot-starter:${version.simbot.version}'

// 给其他组件留个板凳...
// ...
`.trim()}

export default function QuickStartSpringBootStarterCodes({version}) {
    return <Tabs groupId="use-dependency">
        <TabItem value="Gradle Kotlin DSL" label="Gradle Kotlin DSL" default>
            <CodeBlock language="kotlin">
                { gradleKts(version) }
            </CodeBlock>
        </TabItem>

        <TabItem value="Gradle Groovy" label="Gradle Groovy">
            <CodeBlock language="groovy">
                { gradleGroovy(version) }
            </CodeBlock>
        </TabItem>
        <TabItem value="Maven" label="Maven">
            <CodeBlock language="xml">
                { mavenCode(version) }
            </CodeBlock>
        </TabItem>
    </Tabs>

}
