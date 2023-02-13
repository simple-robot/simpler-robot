---
authors: [forliy, forte]
title: v3.0.0-beta.M3包路径变更公告
tags: [公告]
---

在 [**`v3.0.0-beta.M3`**](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0-beta-M3) 中，我们将做最后一个大面积的不兼容变更，并不再继续追加新特性或不兼容变更。

<!--truncate-->

我们将会在 [**`v3.0.0-beta.M3`**](https://github.com/simple-robot/simpler-robot/releases/tag/v3.0.0-beta-M3) 版本对部分内容的包路径做出调整，具体涉及类型如下：

import YES from '@site/src/components/YES';


| 原路径                                                          | 调整后                                                                                                |
|--------------------------------------------------------------|----------------------------------------------------------------------------------------------------|
| `love.forte.simbot.BotInfo`                                  | <code>love.forte.simbot.<b><YES value='bot' /></b>.BotInfo</code>                                  |
| `love.forte.simbot.Bot`                                      | <code>love.forte.simbot.<b><YES value='bot' /></b>.Bot</code>                                      |
| `love.forte.simbot.SocialRelationsContainer`                 | <code>love.forte.simbot.<b><YES value='definition' /></b>.SocialRelationsContainer</code>          |
| `love.forte.simbot.FriendsContainer`                         | <code>love.forte.simbot.<b><YES value='definition' /></b>.FriendsContainer</code>                  |
| `love.forte.simbot.ContactsContainer`                        | <code>love.forte.simbot.<b><YES value='definition' /></b>.ContactsContainer</code>                 |
| `love.forte.simbot.GroupsContainer`                          | <code>love.forte.simbot.<b><YES value='definition' /></b>.GroupsContainer</code>                   |
| `love.forte.simbot.GuildsContainer`                          | <code>love.forte.simbot.<b><YES value='definition' /></b>.GuildsContainer</code>                   |
| `love.forte.simbot.BotRegistrar`                             | <code>love.forte.simbot.<b><YES value='bot' /></b>.BotRegistrar</code>                             |
| `love.forte.simbot.ComponentMismatchException`               | <code>love.forte.simbot.<b><YES value='bot' /></b>.ComponentMismatchException</code>               |
| `love.forte.simbot.VerifyFailureException`                   | <code>love.forte.simbot.<b><YES value='bot' /></b>.VerifyFailureException</code>                   |
| `love.forte.simbot.BotManager`                               | <code>love.forte.simbot.<b><YES value='bot' /></b>.BotManager</code>                               |
| `love.forte.simbot.BotAlreadyRegisteredException`            | <code>love.forte.simbot.<b><YES value='bot' /></b>.BotAlreadyRegisteredException</code>            |
| `love.forte.simbot.ComponentModel`                           | <code>love.forte.simbot.<b><YES value='bot' /></b>.ComponentModel</code>                           |
| `love.forte.simbot.BotVerifyInfo`                            | <code>love.forte.simbot.<b><YES value='bot' /></b>.BotVerifyInfo</code>                            |
| `love.forte.simbot.BotVerifyInfoDecoderFactory`              | <code>love.forte.simbot.<b><YES value='bot' /></b>.BotVerifyInfoDecoderFactory</code>              |
| `love.forte.simbot.BotVerifyInfoDecoder`                     | <code>love.forte.simbot.<b><YES value='bot' /></b>.BotVerifyInfoDecoder</code>                     |
| `love.forte.simbot.StandardBotVerifyInfoDecoderFactory`      | <code>love.forte.simbot.<b><YES value='bot' /></b>.StandardBotVerifyInfoDecoderFactory</code>      |
| `love.forte.simbot.StandardSerialFormatBotVerifyInfoDecoder` | <code>love.forte.simbot.<b><YES value='bot' /></b>.StandardSerialFormatBotVerifyInfoDecoder</code> |
| `love.forte.simbot.StandardStringFormatBotVerifyInfoDecoder` | <code>love.forte.simbot.<b><YES value='bot' /></b>.StandardStringFormatBotVerifyInfoDecoder</code> |
| `love.forte.simbot.StandardBinaryFormatBotVerifyInfoDecoder` | <code>love.forte.simbot.<b><YES value='bot' /></b>.StandardBinaryFormatBotVerifyInfoDecoder</code> |
| `love.forte.simbot.JsonBotVerifyInfoDecoder`                 | <code>love.forte.simbot.<b><YES value='bot' /></b>.JsonBotVerifyInfoDecoder</code>                 |
| `love.forte.simbot.YamlBotVerifyInfoDecoder`                 | <code>love.forte.simbot.<b><YES value='bot' /></b>.YamlBotVerifyInfoDecoder</code>                 |
| `love.forte.simbot.PropertiesBotVerifyInfoDecoder`           | <code>love.forte.simbot.<b><YES value='bot' /></b>.PropertiesBotVerifyInfoDecoder</code>           |
| `love.forte.simbot.DecoderBotVerifyInfo`                     | <code>love.forte.simbot.<b><YES value='bot' /></b>.DecoderBotVerifyInfo</code>                     |
| `love.forte.simbot.ByteArrayBotVerifyInfo`                   | <code>love.forte.simbot.<b><YES value='bot' /></b>.ByteArrayBotVerifyInfo</code>                   |
| `love.forte.simbot.OriginBotManager`                         | <code>love.forte.simbot.<b><YES value='bot' /></b>.OriginBotManager</code>                         |

> 简单来说，即将与 **`Bot`** 有关的内容从原本的 `love.forte.simbot.*` 调整到了 `love.forte.simbot.bot.*` 中。


