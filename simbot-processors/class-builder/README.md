# simbot-processor-class-builder

为 Kotlin 中的某个 `class` 生成一个 Builder。

例如：

```kotlin
class Person(
    val name: String,
    val age: Int
) {
    var size: Int? = null
}
```

生成：

```Kotlin
@BuilderFor(Person::class)
class PersonBuilder {

    public lateinit var name: String
    public var age: Int by kotlin.properties.Delegates.notNull()
    public var size: Int? = null

    // functions...

    public fun build(): Person {
        return Person(
            name = name,
            age = age,
        ).also {
            it.size = size
        }
    }
}
```

特性:

- 非 `null` 属性通过 `lateinit var` 或 `Delegates.notNull()` 进行处理。
- List、Set、Map 等集合属性会生成 `add`、`addAll`、`clear` 等方法。
- 如果属性也可能存在一个 `Builder`（找到了带有 `BuilderFor` 的 Builder），生成 `inline` 的 `block: XxxBuilder.() -> Unit`
  DSL 扩展API。
- List、Set、Map (value) 的元素如果也可能存在一个 `Builder`，生成 `inline` 的 `block: XxxBuilder.() -> Unit` DSL 扩展API。
