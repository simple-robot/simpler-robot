---
title: QQ频道组件
---

针对于[QQ频道机器人](https://bot.q.qq.com/wiki/)所实现的组件，对接[官方API](https://bot.q.qq.com/wiki/develop/api/)，无额外的第三方API依赖。

import Label from '@site/src/components/Label'

> **组件ID**：<Label>simbot.tencentguild</Label>
>
> **版本状态**：<Label title='无法保证可用性、未来可能会频发发生破坏性改动'>alpha</Label>

:::info 链接引导

组件仓库地址：<a href='https://github.com/simple-robot/simbot-component-tencent-guild'><b><span class='bi-github'></span> simple-robot/simbot-component-tencent-guild</b></a>

在线API Doc：**<https://docs.simbot.forte.love/components/tencent-guild>**

:::

:::tip 😊

### 组件优势

- 对接**官方API**，更可靠、更稳定。
- 无额外的 _第三方API_ 依赖，更新节奏更好把控，内容可控性强。


:::

:::danger 😟

### 组件劣势

- 此组件早在QQ频道机器人内测阶段便开始参与，但是由于当时时间较早，且官方放开内容过少且改动频繁，因此遗留问题很多且消磨了团队的大量更新意愿。
- 官方API目前仍有大量限制（包括但不限于**公域**下机器人只能回复不能主动推送（或有严格限制）、很多东西需要审核/报备、频道权限配置等），导致组件实现起来困难较多。

:::
