import React from 'react';
import Tabs from '@theme/Tabs';
import TabItem from '@theme/TabItem';
import CodeBlock from '@theme/CodeBlock';
import Admonition from '@theme/Admonition';

//region Simple示例
//region 最简示例
function simpleCodeValueLauncher1() {
    return `suspend fun main() {
    val launcher: ApplicationLauncher<SimpleApplication> = simbotApplication(Simple)
    val app: SimpleApplication = launcher.launch() 
    app.join()
}`
}

function simpleCodeValueLauncher2() {
    return `suspend fun main() {
    simbotApplication(Simple).launch().join()
}`
}

function simpleCodeValueCreate1() {
    return `suspend fun main() {
    val app: SimpleApplication = createSimbotApplication(Simple)
    app.join()
}`
}

function simpleCodeValueCreate2() {
    return `suspend fun main() {
    createSimbotApplication(Simple).join()
}`
}

function SimpleSimpleCode() {
    return <>
        <Tabs groupId="w19code">
            <TabItem value="launcher">
                <CodeBlock language="kotlin" title="App.kt">
                    {simpleCodeValueLauncher1()}
                </CodeBlock>
                <CodeBlock language="kotlin" title="App.kt">
                    {simpleCodeValueLauncher2()}
                </CodeBlock>
            </TabItem>
            <TabItem value="create">
                <CodeBlock language="kotlin" title="App.kt">
                    {simpleCodeValueCreate1()}
                </CodeBlock>

                <CodeBlock language="kotlin" title="App.kt">
                    {simpleCodeValueCreate2()}
                </CodeBlock>
            </TabItem>
        </Tabs>
    </>
}

//endregion

//region 完整示例
function fullCodeValueLauncher() {
    return `suspend fun main() {
    val launcher: ApplicationLauncher<SimpleApplication> = simbotApplication(
        factory = Simple,
        configurator = { // this: SimpleApplicationConfiguration
            logger = ...
            coroutineContext = ...
        }
    ) { // this: SimpleApplicationBuilder, it: SimpleApplicationConfiguration
        
        // 组件注册
        installAllComponents()
        // 事件提供者注册
        // BotManager也属于EventProvider
        installAllEventProviders()
        // 带配置的注册
        install(FooComponent) {
            // config for it
        }
        
        install(FooBotManager) // or ignore config
        
        // 全局通用的bot注册相关
        bots {
            val bot = register(...)
            register(...)?.start()
        }
        
        // 内置事件处理器配置
        eventProcessor {
            // 配置监听函数
            listeners {
                listener(myListener)
                listener(MessageEvent) {
                    isAsync = true | false
                    blockNext = true | false
                    logger = ...
                    handle { context, event ->
                        // ...
                    }
                }
                val myListener: EventListener = ...
                // plus listener
                +myListener
            }
            
            // 配置拦截器
            interceptors {
                // 监听函数拦截器
                listenerIntercept {
                    // ...
                }
                // 事件流程拦截器
                processingIntercept {
                    // ...
                }
            }
        }
        
        // 额外的扩展允许直接在此处定义监听函数
        listeners {
            listener(myListener)
            listener(MessageEvent) {
                handle { context, messageEvent ->
                    // ...
                }
            }
        }
        
        // 完成时的回调函数
        onCompletion {
            // ...
        }
    }
    
    val app: SimpleApplication = launcher.launch()
    app.join()
}`
}

function fullCodeValueCreate() {
    return `suspend fun main() {
    val app: SimpleApplication = createSimbotApplication(
        factory = Simple,
        configurator = { // this: SimpleApplicationConfiguration
        
            // config block
            
        }
    ) { // this: SimpleApplicationBuilder, it: SimpleApplicationConfiguration
    
        // build block
        
    }
    
    app.join()
}`
}

function fullCodeValueBuilder() {
    return `suspend fun main() {
    val app: SimpleApplication = buildSimbotApplication(Simple) { 
            config { // this: SimpleApplicationConfiguration
            
                // config block
                
            }
            
            build { // this: SimpleApplicationBuilder, it: SimpleApplicationConfiguration
            
                // build block
                
            }
            
        }
    
    app.join()
}`
}

function SimpleFullCode() {
    return <>
        <Tabs groupId="w19code">
            <TabItem value="launcher">
                <CodeBlock language="kotlin" title="App.kt">
                    {fullCodeValueLauncher()}
                </CodeBlock>
            </TabItem>
            <TabItem value="create">
                <CodeBlock language="kotlin" title="App.kt">
                    {fullCodeValueCreate()}
                </CodeBlock>
            </TabItem>
            <TabItem value="builder">
                <CodeBlock language="kotlin" title="App.kt">
                    {fullCodeValueBuilder()}
                </CodeBlock>
            </TabItem>
        </Tabs>
    </>
}

//endregion

//region 扩展示例


function extraCodeLauncherValue1() {
    return `suspend fun main() {
    val launcher: ApplicationLauncher<SimpleApplication> = simpleApplication({
        // config block
    }) {
        // build block
    }
    
    val app: SimpleApplication = launcher.launch()
    app.join()
}`
}

function extraCodeLauncherValue2() {
    return `suspend fun main() {
    val launcher = simpleApplication()
    val app = launcher.launch()
    app.join()
}`
}

function extraCodeLauncherValue3() {
    return `suspend fun main() {
    simpleApplication().launch().join()
}`
}

function extraCodeCreateValue1() {
    return `suspend fun main() {
    val app: SimpleApplication = createSimpleApplication({
        // config block
    }) {
        // build block
    }
    
    app.join()
}`
}

function extraCodeCreateValue2() {
    return `suspend fun main() {
    val app = createSimpleApplication()
    app.join()
}`
}

function extraCodeCreateValue3() {
    return `suspend fun main() {
    createSimpleApplication().join()
}`
}

function SimpleExtraCode() {
    return <Tabs groupId="w19code">
        <TabItem value="launcher">
            <CodeBlock language="kotlin" title="App.kt">{extraCodeLauncherValue1()}</CodeBlock>
            <CodeBlock language="kotlin" title="App.kt">{extraCodeLauncherValue2()}</CodeBlock>
            <CodeBlock language="kotlin" title="App.kt">{extraCodeLauncherValue3()}</CodeBlock>
        </TabItem>
        <TabItem value="create">
            <CodeBlock language="kotlin" title="App.kt">{extraCodeCreateValue1()}</CodeBlock>
            <CodeBlock language="kotlin" title="App.kt">{extraCodeCreateValue2()}</CodeBlock>
            <CodeBlock language="kotlin" title="App.kt">{extraCodeCreateValue3()}</CodeBlock>
        </TabItem>
    </Tabs>
}

//endregion

function SimpleCode() {
    return <Tabs groupId='w19code-show'>
        <TabItem value='最简示例'>
            <SimpleSimpleCode/>
        </TabItem>
        <TabItem value='完整示例'>
            <SimpleFullCode/>
        </TabItem>
        <TabItem value='扩展示例'>
            <SimpleExtraCode/>
        </TabItem>
    </Tabs>
}

//endregion


//region Boot示例

//region 最简示例
function bootSimpleCodeValueLauncher1() {
    return `suspend fun main() {
    val launcher: ApplicationLauncher<BootApplication> = simbotApplication(Boot)
    val launch: BootApplication = launcher.launch()
    launch.join()
}`
}

function bootSimpleCodeValueLauncher2() {
    return `suspend fun main() {
    simbotApplication(Boot).launch().join()
}`
}

function bootSimpleCodeValueCreate1() {
    return `suspend fun main() {
    val app: BootApplication = createSimbotApplication(Boot)
    app.join()
}`
}

function bootSimpleCodeValueCreate2() {
    return `suspend fun main() {
    createSimbotApplication(Boot).join()
}`
}

function BootSimpleCode() {
    return <>
        <Tabs groupId="w19code">
            <TabItem value="launcher">
                <CodeBlock language="kotlin" title="com/example/App.kt">
                    {bootSimpleCodeValueLauncher1()}
                </CodeBlock>
                <CodeBlock language="kotlin" title="com/example/App.kt">
                    {bootSimpleCodeValueLauncher2()}
                </CodeBlock>
            </TabItem>
            <TabItem value="create">
                <CodeBlock language="kotlin" title="com/example/App.kt">
                    {bootSimpleCodeValueCreate1()}
                </CodeBlock>

                <CodeBlock language="kotlin" title="com/example/App.kt">
                    {bootSimpleCodeValueCreate2()}
                </CodeBlock>
            </TabItem>
        </Tabs>
    </>
}

//endregion

//region 完整示例
function bootFullCodeValueLauncher() {
    return `suspend fun main() {
    suspend fun main() {
    val launcher: ApplicationLauncher<BootApplication> = simbotApplication(
        factory = Boot,
        configurator = { // this: BootApplicationConfiguration
            logger = ...
            coroutineContext = ...
            
            // 用于各种资源扫描的类加载器
            classLoader = ...
            // 依赖类扫描包路径
            classesScanPackage = listOf("com.example")
            // 顶层函数形式的监听函数扫描
            topLevelListenerScanPackage = listOf("com.example")
            // 顶层函数的binder函数扫描
            topLevelBinderScanPackage = listOf("com.example")
            args = listOf("-foo", "-bar")
            // *.bot 资源文件扫描路径
            botConfigurationResources = listOf("simbot-bots/*.bot*")
            // 额外配置的*.bot 资源
            botConfigurations = listOf(File("xxx/fooBot.bot").toResource())
            // 是否自动启动扫描注册的bot
            isAutoStartBots = true
        }
    ) { // this: BootApplicationBuilder, it: BootApplicationConfiguration
        
        // 组件注册
        installAllComponents()
        
        // 事件提供者注册
        // BotManager也属于EventProvider
        installAllEventProviders()
        
        // 带配置的注册
        install(FooComponent) {
            // config for it
        }
        
        install(FooBotManager) // or ignore config
        
        // 对依赖注入内容的**额外**配置
        beans {
            bean("foo", Foo())
            bean("bar", Bar::class) { Bar() }
            scan("foo.example", "bar.example")
        }
        
        // 配置额外的binders
        binders {
            binder("foo-binder", FooBinderFactory)
            binder(BarBinderFactory)
        }
        
        
        // 全局通用的bot注册，与 Simple 几乎一致。只不过此处为配置文件扫描以外的内容。
        bots {
            // ...
        }
        
        // 事件处理器与 Simple 几乎一致。只不过此处配置的为bean扫描以外的内容。
        // 依赖管理功能会尝试自动扫描并注册相关的监听函数。
        eventProcessor {
            // ...
        }
        
        
        // 完成时的回调函数
        onCompletion {
            // ...
        }
    }
    
    val app: BootApplication = launcher.launch()
    app.join()
}`
}

function bootFullCodeValueCreate() {
    return `suspend fun main() {
    val app: BootApplication = createSimbotApplication(
        factory = Boot,
        configurator = { // this: BootApplicationConfiguration
            // config block
        }
    ) { // this: BootApplicationBuilder, it: BootApplicationConfiguration
        // build block
    }
    
    app.join()
}`
}

function bootFullCodeValueBuilder() {
    return `suspend fun main() {
    val app: BootApplication = buildSimbotApplication(Boot) {
        config { // this: BootApplicationConfiguration
            // config block
        }
        build { // this: BootApplicationBuilder, it: BootApplicationConfiguration
            // build block
        }
    }
    
    app.join()
}`
}

function BootFullCode() {
    return <>
        <Tabs groupId="w19code">
            <TabItem value="launcher">
                <CodeBlock language="kotlin" title="com/example/App.kt">
                    {bootFullCodeValueLauncher()}
                </CodeBlock>
            </TabItem>
            <TabItem value="create">
                <CodeBlock language="kotlin" title="com/example/App.kt">
                    {bootFullCodeValueCreate()}
                </CodeBlock>
            </TabItem>
            <TabItem value="builder">
                <CodeBlock language="kotlin" title="com/example/App.kt">
                    {bootFullCodeValueBuilder()}
                </CodeBlock>
            </TabItem>
        </Tabs>
    </>
}

//endregion


//region 扩展示例
function bootExtraCodeLauncherValue1() {
    return `suspend fun main() {
    val launcher: ApplicationLauncher<BootApplication> = bootApplication({
        // config block
    }) {
        // build block
    }
    
    val launch: BootApplication = launcher.launch()
    launch.join()
}`
}

function bootExtraCodeLauncherValue2() {
    return `suspend fun main() {
    val launcher = bootApplication()
    val launch = launcher.launch()
    launch.join()
}`
}

function bootExtraCodeLauncherValue3() {
    return `suspend fun main() {
    bootApplication().launch().join()
}`
}

function bootExtraCodeCreateValue1() {
    return `suspend fun main() {
    val app: BootApplication = createBootApplication({
        // config block
    }) {
        // build block
    }
    
    app.join()
}`
}

function bootExtraCodeCreateValue2() {
    return `suspend fun main() {
    val app = createBootApplication()
    app.join()
}`
}

function bootExtraCodeCreateValue3() {
    return `suspend fun main() {
    createBootApplication().join()
}`
}

function BootExtraCode() {
    return <Tabs groupId="w19code">
        <TabItem value="launcher">
            <CodeBlock language="kotlin" title="com/example/App.kt">{bootExtraCodeLauncherValue1()}</CodeBlock>
            <CodeBlock language="kotlin" title="com/example/App.kt">{bootExtraCodeLauncherValue2()}</CodeBlock>
            <CodeBlock language="kotlin" title="com/example/App.kt">{bootExtraCodeLauncherValue3()}</CodeBlock>
        </TabItem>
        <TabItem value="create">
            <CodeBlock language="kotlin" title="com/example/App.kt">{bootExtraCodeCreateValue1()}</CodeBlock>
            <CodeBlock language="kotlin" title="com/example/App.kt">{bootExtraCodeCreateValue2()}</CodeBlock>
            <CodeBlock language="kotlin" title="com/example/App.kt">{bootExtraCodeCreateValue3()}</CodeBlock>
        </TabItem>
    </Tabs>
}

//endregion

//region 快速开始示例
function bootQsKtCodeValue1() {
    return `import love.forte.simboot.core.SimbootApp
import love.forte.simboot.core.SimbootApplication

@SimbootApplication
class App

suspend fun main(vararg args: String) {
    val app = SimbootApp.run(App::class, args = args)
    app.join()
}`
}

function bootQsKtCodeValue2() {
    return `import love.forte.simboot.core.SimbootApp
import love.forte.simboot.core.SimbootApplication
import love.forte.simboot.core.invoke

@SimbootApplication
class App

suspend fun main(vararg args: String) {
    val app = SimbootApp<App>(args = args)
    app.join()
}`
}

function bootQsKtCodeValue3() {
    return `import love.forte.simboot.core.SimbootApp
import love.forte.simboot.core.SimbootApplication
import love.forte.simboot.core.invoke

@SimbootApplication
class App

suspend fun main(vararg args: String) {
    SimbootApp<App>(args = args).join()
}`
}

function bootQsKtCodeValue4() {
    return `package com.example

import love.forte.simboot.core.SimbootApp


suspend fun main(vararg args: String) {
    SimbootApp.run(args = args).join()
}`
}

function BootQsKtCode() {
    return <>
        <CodeBlock language='kotlin' title='com/example/App.kt'>{bootQsKtCodeValue1()}</CodeBlock>
        <CodeBlock language='kotlin' title='com/example/App.kt'>{bootQsKtCodeValue2()}</CodeBlock>
        <CodeBlock language='kotlin' title='com/example/App.kt'>{bootQsKtCodeValue3()}</CodeBlock>
        <CodeBlock language='kotlin' title='com/example/App.kt'>{bootQsKtCodeValue4()}</CodeBlock>
    </>
}

function bootQsJavaCodeValue1() {
    return `@SimbootApplication
public class App {
    public static void main(String[] args) {
        final ApplicationLauncher<BootApplication> launcher = SimbootApp.run(App.class, args);
        final BootApplication application = launcher.launchBlocking();
        application.joinBlocking();
    }
}`
}

function bootQsJavaCodeValue2() {
    return `public class App {
    public static void main(String[] args) {
        final ApplicationLauncher<BootApplication> launcher = SimbootApp.run(args);
        final BootApplication application = launcher.launchBlocking();
        application.joinBlocking();
    }
}`
}

function bootQsJavaCodeValue3() {
    return `@SimbootApplication
public class App {
    public static void main(String[] args) {
        SimbootApp.run(App.class, args)
                .launchBlocking()
                .joinBlocking();
    }
}`
}

function bootQsJavaCodeValue4() {
    return `public class App {
    public static void main(String[] args) {
        SimbootApp.run(args)
                .launchBlocking()
                .joinBlocking();
    }
}`
}

function BootQsJavaCode() {
    return <>
        <CodeBlock language="java" title="com/example/App.java">{bootQsJavaCodeValue1()}</CodeBlock>
        <CodeBlock language="java" title="com/example/App.java">{bootQsJavaCodeValue2()}</CodeBlock>
        <CodeBlock language="java" title="com/example/App.java">{bootQsJavaCodeValue3()}</CodeBlock>
        <CodeBlock language="java" title="com/example/App.java">{bootQsJavaCodeValue4()}</CodeBlock>
    </>
}


function BootQsCode() {
    return <Tabs groupId='code'>
        <TabItem value="Kotlin">
            <BootQsKtCode/>
        </TabItem>
        <TabItem value="Java">
            <BootQsJavaCode/>
        </TabItem>
    </Tabs>
}

//endregion


function BootCode() {
    return <Tabs groupId='w19code-show'>
        <TabItem value="最简示例">
            <BootSimpleCode/>
        </TabItem>
        <TabItem value="完整示例">
            <BootFullCode/>
        </TabItem>
        <TabItem value="扩展示例">
            <BootExtraCode/>
        </TabItem>
        <TabItem value="快速启动示例">
            <BootQsCode/>
        </TabItem>
    </Tabs>
}

//endregion

//region SpringBoot示例
//region 快速开始示例
function springBootQsKtCodeValue1() {
    return `@EnableSimbot
@SpringBootApplication
class SpringBootApp


fun main(vararg args: String) {
    runApplication<SpringBootApp>(*args)
}`
}

function springBootQsJavaCodeValue1() {
    return `@EnableSimbot
@SpringBootApplication
public class SpringBootApp {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootApp.class, args);
    }
}`
}

function SpringBootQsCode() {
    return <Tabs groupId='code'>
        <TabItem value="Kotlin">
            <CodeBlock language="kotlin" title='com/example/SpringBootApp.kt'>{springBootQsKtCodeValue1()}</CodeBlock>
        </TabItem>
        <TabItem value="Java">
            <CodeBlock language="java" title='com/example/SpringBootApp.java'>{springBootQsJavaCodeValue1()}</CodeBlock>
        </TabItem>
    </Tabs>
}
//endregion

function SpringBootCode() {
    return <>
        <Admonition type='note'>
            <p>
                Spring Boot模块下不太建议通过前述的 <code>{ ' simbotApplication { ... } ' }</code> 的方式来使用 ————
                这些行为将会由内部的Spring Boot配置来完成。
            </p>
            <p>
                因此此处只提供快速开始示例。
            </p>
        </Admonition>

        <Tabs groupId="w19code-show">
            <TabItem value="快速开始示例">
                <SpringBootQsCode />
            </TabItem>
        </Tabs></>
}
//endregion

export default function Codes4w19({}) {
    return <Tabs>
        <TabItem default value="Simple">
            <SimpleCode />
        </TabItem>

        <TabItem value="Boot">
            <BootCode />
        </TabItem>

        <TabItem value="SpringBoot">
            <SpringBootCode />
        </TabItem>

    </Tabs>
}
