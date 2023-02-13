import React from 'react';
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';

function mavenCode(version) {
    return `
<!-- 配置快照仓库 -->
<repositories>
    <repository>
        <id>sonatype-snapshot</id>
        <name>Sonatype Snapshots Repository</name>
        <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
        <snapshots>
            <enabled>true</enabled>
        </snapshots>
    </repository>
</repositories>
    
<dependencies>
    <!-- simbot Core -->
    <dependency>
        <groupId>love.forte.simbot</groupId>
        <artifactId>simbot-core</artifactId>
        <version>${version.simbot.version}-SNAPSHOT</version>
    </dependency>
    <!-- 给其他组件预留一个座位... -->
    <!-- ... -->

</dependencies>

`.trim()
}

function gradleKts(version) {
    return `plugins {
  \`kotlin\`
}

group = "..."
version = "..."

repositories {
    mavenCentral()
    // 快照仓库
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        mavenContent {
            snapshotsOnly()
        }
    }
}

// simbot核心标准库
implementation("love.forte.simbot:simbot-core:${version.simbot.snapshotVersion}")

// 给其他组件预留一个座位...
// ...
`.trim()
}

function gradleGroovy(version) {
    return `plugins {
  id 'kotlin'
}

group = '...'
version = '...'

repositories {
    mavenCentral()
    // 快照仓库
    maven { 
        url 'https://oss.sonatype.org/content/repositories/snapshots/'
        mavenContent {
            snapshotsOnly()
        }
     }
}

// simbot核心标准库
implementation 'love.forte.simbot:simbot-core:${version.simbot.snapshotVersion}'

// 给其他组件预留一个座位...
// ...
`.trim()
}

export default function QuickStartCoreSnapshotCodes({version}) {
    return <Tabs groupId="use-dependency">
        <TabItem value="Gradle Kotlin DSL" label="Gradle Kotlin DSL" default>
            <CodeBlock language="kotlin">
                {gradleKts(version)}
            </CodeBlock>
        </TabItem>

        <TabItem value="Gradle Groovy" label="Gradle Groovy">
            <CodeBlock language="groovy">
                {gradleGroovy(version)}
            </CodeBlock>
        </TabItem>

        <TabItem value="Maven" label="Maven" >
            <CodeBlock language="xml">
                {mavenCode(version)}
            </CodeBlock>
        </TabItem>
    </Tabs>

}
