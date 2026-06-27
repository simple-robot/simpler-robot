[中文](CONTRIBUTING_CN.md) | _English_

# How to Contribute

**?** First of all, thank you very much for taking the time to read this guide and learn how you can contribute to Simple Robot! **?**

This guide mainly introduces how you can contribute to the core library
[Simple Robot](https://github.com/simple-robot/simpler-robot) (referred to below as the _simbot core library_),
as well as most repositories in the [Simple Robot Organization][Organization Home] and the
[Simple Robot Library](https://github.com/simple-robot-library).

## Be Friendly

Whether you are filing feedback, submitting contributions, or joining community discussions, please always stay friendly.
Avoid aggressive language; do not engage in personal attacks or verbal harassment; do not discriminate against any
region/country/race/gender; and do not violate public order, good morals, or laws and regulations.

## I Found a Bug

### Issues

If you find a bug in the simbot core library or any officially maintained component repository, please first go to
[Core Library Issues](https://github.com/simple-robot/simpler-robot/issues/new/choose),
choose an appropriate category (for example, "Issue Report"), and submit detailed information about the bug
(including the library version you are using, error logs, etc.).

When reporting issues, we recommend using Markdown syntax[^about write] to improve readability and presentation,
which also helps contributors quickly scan and locate the problem.

[^about write]: [About writing and formatting on GitHub](https://docs.github.com/en/get-started/writing-on-github/getting-started-with-writing-and-formatting-on-github/about-writing-and-formatting-on-github)

### Pull Request

If you are interested and able to help fix the bug, you are welcome to submit your changes via a
[Pull Request](https://github.com/simple-robot/simpler-robot/pulls).
When you submit code changes, please note the following:

**Code Contribution**

You can read more details in the [Code Contributions](#code-contributions) section.

**Contributors**

Both the core library and official component repositories have places in the project to configure contributors.
Taking the simbot core library as an example, add your information in the following code in `build-logic/shared/src/main/kotlin/P.kt`:

```Kotlin
override val developers: List<Developer> = developers {
    developer {
        ...
    }
    ...

    Add your info here
}
```

## Contributing in Discussions

Leave your questions or answers in [Discussions][Discussions],
and help others find their way~

## Code Contributions

> [!info]
> If you want to contribute code, please submit your PR to the `v4-dev` development branch.

### Documentation Style

The simbot core library has some conventions for source-code documentation.

* For all `public` and `protected` declarations (including classes/interfaces, functions, properties, etc.),
  write fairly detailed KDoc[^KDoc] in **Chinese** or in **clear, easy-to-understand English**.
  Add documentation tags as appropriate (for example, `@param`, `@throws`), but you may omit them if they are
  already covered in the main text.

* For non-public declarations (for example, `private`, `internal`), if the code is very simple you don't have to write
  fully detailed **documentation comments**, but you should add explanatory comments as needed to help other developers
  read and understand the code.

* For overridden abstract functions whose meaning has not changed, you may omit documentation comments.

* For new classes and interfaces, add author information (`@author`) and version information (`@since`) whenever possible.
  Do not add non-standard tags (for example, `@Date`, `@HelloWorld`).

[^KDoc]: [Document Kotlin code: KDoc](https://kotlinlang.org/docs/kotlin-doc.html)

### Code Style

The simbot core library has a few conventions for overall code style.

Most rules are stored as configuration in `.editorconfig`. Some IDEs (for example, IntelliJ IDEA) should load it
automatically, or you can load it manually.
These conventions are similar to the descriptions in
[Kotlin Coding conventions](https://kotlinlang.org/docs/coding-conventions.html),
which you can also use as a reference.

Below are some common style conventions:

* Put a space on both sides of infix expressions (such as `infix` functions or numeric operators like `+`, `-`, etc.).
  For example, use `a += b` instead of `a+=b`.

* Between a variable name and its type, there should be a space after `:`.
  For example, use `a: Int` instead of `a:Int`.

* When function parameters (both in definitions and in call arguments) are on the same line, put a space after the
  separating comma `,`.
  For example, use `foo(1, 2)` instead of `foo(1,2)`; use `function name(a: Int, b: Int)` instead of
  `function name(a: Int,b: Int)`.

* When a function has many parameters and needs to wrap across multiple lines, place each parameter on its own line and
  align them, and do not put `(` and `)` on the same line.
  If parameters need annotations, put each annotation on its own line. For example:
    ```Kotlin
    function name(
        p1: Int,
        @Anno2
        @Anno1
        p2: String
    )
    ```

### KMP

Most modules in the simbot core library support [KMP](https://kotlinlang.org/docs/multiplatform.html).

### Binary Compatibility

We aim to provide better compatibility guarantees across versions. Therefore, when an API is deprecated or needs to be
changed, please ensure the new version remains binary-compatible with older versions, and add appropriate deprecation
notes and warnings.

New experimental APIs should also be marked with appropriate opt-in annotations and warnings to inform developers.

### Java-Friendly APIs

Public APIs exposed by the simbot core library should be Java-friendly, or have corresponding Java-friendly APIs—especially
for suspending functions (`suspend fun`).
Most Java-friendly APIs for suspending functions are implemented via the compiler plugin
[Kotlin Suspend Transform compiler plugin](https://github.com/ForteScarlet/kotlin-suspend-transform-compiler-plugin).
See the documentation site chapters
[Java Friendly](https://simbot.forte.love/java-friendly.html)
and
[Component Development - Compiler Plugin](https://simbot.forte.love/component-dev-compiler-plugin.html).

## Documentation Contributions

### API Documentation

API documentation is generated with [Dokka](https://github.com/Kotlin/dokka) from the documentation comments in the
source code. See [Documentation Style](#documentation-style) for more.

### Documentation Site

The documentation site for the simbot4 core library lives in a separate repository:
[simple-robot-library/simbot4-website](https://github.com/simple-robot-library/simbot4-website/).
Head over there to learn more and contribute!

## Community Contributions

### Applications

Build applications based on simbot and tag your project with `simbot` to energize the community ecosystem!

### Component Libraries / Plugin Libraries / Others

Build component libraries / plugin libraries based on simbot and tag your project with `simbot` to energize the
community ecosystem!
You can:

- Read the docs chapter [Component Library Development](https://simbot.forte.love/component-dev.html) to learn more details.
- Ask for help in [Discussions][Discussions] from others or the development team.
- Go to the [Organization Home][Organization Home], add the **Community** entry, and seek help there.

[Organization Home]: https://github.com/simple-robot
[Discussions]: https://github.com/orgs/simple-robot/discussions
