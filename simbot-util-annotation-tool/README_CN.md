# Annotation-Tool
<a href="https://repo1.maven.org/maven2/love/forte/annotation-tool/core" target="_blank">
      <img alt="release" src="https://img.shields.io/nexus/r/love.forte.annotation-tool/core?label=repo1-lastVersion&server=https%3A%2F%2Foss.sonatype.org" />
    </a>

中文 | [English](README.md)

这是一个服务于注解的工具库，一个小巧可爱的库。

## 使用

- **核心模块：** [核心](core)
- **核心模块(for kotlin)：** [kt核心](kcore)

## 手册
[手册](guide/cn) | [~~Guide~~](guide/en)

## 速览

首先，你需要得到一个 `AnnotationTool` 实例：

```java
AnnotationTool tool=AnnotationTools.getAnnotationTool();
```

### 创建注解实例

```java
    public void test1()throws ReflectiveOperationException {
        Map<String, Object> params=new HashMap<>();
        params.put("value","Hello World");
        params.put("size",15);
        params.put("name","ForteScarlet");
        // throws ReflectiveOperationException
        final Element annotationInstance1=tool.createAnnotationInstance(Element.class,params);
        assert annotationInstance1.name().equals("ForteScarlet");
        assert annotationInstance1.size()==15;
        assert annotationInstance1.value().equals("Hello World");

        params.remove("name");
        final Element annotationInstance2=tool.createAnnotationInstance(Element.class,params);
        // default value support.
        assert annotationInstance2.name().equals("forte");
        assert annotationInstance2.size()==15;
        assert annotationInstance2.value().equals("Hello World");

        final Element annotationInstance3=tool.createAnnotationInstance(Element.class,params);
        assert annotationInstance2.equals(annotationInstance3);

        final Element nativeElement=ExampleMain.class.getAnnotation(Element.class);
        assert nativeElement.equals(annotationInstance3);
}
```

### 注解映射

```java
// Account.java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Account {
    String value();
}

// User.java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@AnnotationMapper(Account.class)
public @interface User {
    @AnnotationMapper.Property(value = "value", target = Account.class)
    String name();
}

// Bot.java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@AnnotationMapper(User.class)
public @interface Bot {
    @AnnotationMapper.Property(value = "name", target = User.class)
    String nickname();
}

// Demo.java
public class Demo {

    @Bot(nickname = "ForteScarlet")
    public static void main(String[] args) throws ReflectiveOperationException {
        final Method main = Demo.class.getMethod("main", String[].class);

        final AnnotationTool tool = AnnotationTools.getAnnotationTool();
        final Account account = tool.getAnnotation(main, Account.class);
        assert account != null;

        System.out.println(account); // @Account(value="ForteScarlet")
        System.out.println(account.value()); // ForteScarlet
    }
}
```

## [LICENSE](LICENSE)
