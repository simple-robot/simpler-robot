# AnnotationTool

`AnnotationTool` 是针对于注解工具的接口约束，也是核心模块中最主要的内容之一。

## 概述

#### 反射异常
由于对注解的各种操作很有可能涉及到反射，因此此接口中大部分方法都需要捕获 `ReflectiveOperationException` 异常。

