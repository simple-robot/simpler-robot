---
authors: forliy
title: 有关BOOT模块的思考
tags: [杂谈]
---

实际上，有关于 `BOOT` 模块（**不是**指的 `Spring Boot Starter` 哦 ），我们也有一些思考。。。

<!--truncate-->

就如同上面那一句开篇语中所体现的情况一样，我们甚至需要在提到 `BOOT` 模块的时候为其紧随一个"免责声明"，
来避免 `BOOT` 与 `Spring Boot Starter` 被搞混。
换个角度想想，`BOOT` 模块真的有必要存在吗？

目前来讲，`BOOT` 模块最主要的作用就是为 `Spring Boot Starter` 提供一部分功能支持、注解定义，
以及为一些不想要使用Spring的Kotlin开发者提供 _一定程度_ 上的便利。
但是这些情况都应该有更优的解决方案。
对于Java用户，为其提供一个功能完善并且更具有 "`Spring Boot`风格" 的实现模块，
而为Kotlin用于提供一个更精简的 `autoconfigure` 模块，或许是个更不错的方式。

实际上 `BOOT` 模块之于 simbot3，就类似于 `SENDER` 等三大送信器之于 simbot2。
它们本应是没有必要继续"传承"的内容，却也不知为何，从上一个版本保留了下来。
后续，也许在simbot4、或者更大胆一些的未来，`BOOT` 模块将不再存在。


