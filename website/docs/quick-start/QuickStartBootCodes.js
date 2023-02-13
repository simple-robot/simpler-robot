import React from 'react';
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';

function mavenCode(version) {
    return `
<dependencies>
    <!-- simbot Boot -->
    <dependency>
        <groupId>love.forte.simbot.boot</groupId>
        <artifactId>simboot-core</artifactId>
        <version>${version.simbot.version}</version>
    </dependency>

    <!-- 给其他组件留个盘子 -->
    <!-- ... -->

</dependencies>

`.trim()
}

function gradleKts(version) {
    return `plugins {
  \`java\`  // or kotlin
}

group = "..."
version = "..."

repositories {
  mavenCentral()
}

// simbot Boot
implementation("love.forte.simbot.boot:simboot-core:${version.simbot.version}on")

// 给其他组件留个盘子
// ...
`.trim()
}

function gradleGroovy(version) {
    return `plugins {
  id 'java' // or kotlin
}

group = '...'
version = '...'

repositories {
  mavenCentral()
}

// simbot Boot
implementation 'love.forte.simbot.boot:simboot-core:${version.simbot.version}'

// 给其他组件留个盘子
// ...
`.trim()
}

export default function QuickStartBootCodes({version}) {
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
