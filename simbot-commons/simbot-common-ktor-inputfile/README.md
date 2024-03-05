# simbot-common-ktor-inputfile

一个适用于 Ktor Client，可更简单的将一个本地文件作为 `Ktor` 的 `MultiPartFormDataContent` 的一部分。
提供一个类型 `InputFile`，以及相关的扩展方法、工厂方法，
比如在 JVM 下可以通过 `File` 和 `Path` 构建它。

在 JVM 中对 `ktor-client-core` 的依赖是**仅编译**的。
