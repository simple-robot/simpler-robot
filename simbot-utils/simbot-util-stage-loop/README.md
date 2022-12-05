# Simbot Util Stage Loop

提供一个简单的“可挂起状态循环器”的简易工具模块。

部分组件中可能存在一些需要建立长连接、检查连接状态、处理心跳、状态回退等过程的处理，
此模块提供的 `StageLoop` 可以一定程度上的简化开发并统一代码风格。