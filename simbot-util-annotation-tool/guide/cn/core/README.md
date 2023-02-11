# core

核心模块，此模块下提供本库最基础的核心功能, 其中包括：

- [**AnnotationTool**](AnnotationTool)
- [**AnnotationMetadata**](AnnotationMetadata)

## 概述

在此处，将会对一些整体性的内容进行说明。

### 不可变返回值

对于整个核心库来说，所有返回值是`? extends Collection<...>`或者`? extends Map<...>` 类型的，即集合相关的类型， 如果没有特殊说明，均为 **不可变**
的。（我也标注了对应的注解`@Unmodifiable`来帮助IDE进行检测）。

这意味着，你不能直接操作你所得到的集合类型返回值，否则将可能会出现异常。

