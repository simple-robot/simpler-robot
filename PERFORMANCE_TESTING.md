# 性能测试报告
此处会针对一部分需要可能需要关注、可能需要进行对比的相关api进行性能测试。
如果你对某些内容的性能对比感兴趣、或者对某些内容有好的优化策略/方案，欢迎通过 
[issues](https://github.com/ForteScarlet/simpler-robot/issues/new/choose) 分享你的想法
或 [pr](https://github.com/ForteScarlet/simpler-robot/pulls) 贡献你的力量，十分感谢。

这些测试内容都应可以从相关的测试模块中找到。

## randomID(Random)
> [IDTest.kt](simbot-apis/simbot-api/src/test/kotlin/jmh/IdTest.kt)

针对内容：
- `randomID()` 对比 `UUID.randomUUID`
- `randomID()` 使用 `kotlin.Random.Default` 对比 `ThreadLocalRandom`

```
# JMH version: 1.33
# VM version: JDK 1.8.0_332, OpenJDK 64-Bit Server VM, 25.332-b09
# Threads: 8 threads, will synchronize iterations

Benchmark                                    Mode  Cnt         Score         Error  Units
IdTest.randomIdByDefaultGenerate            thrpt   25  70801412.214 ± 1498689.005  ops/s
IdTest.randomIdByThreadLocalRandomGenerate  thrpt   25  66505118.175 ±  730902.232  ops/s
IdTest.randomUUIDGenerate                   thrpt   25   1435474.691 ±   39339.236  ops/s
```

## TimeDuration
> [DurationTest](simbot-project-tests/simbot-project-test-jmh-duration/src/main/kotlin/love/forte/simbotest/duration/DurationTest.kt)

针对内容：

对几个可以代表"Duration"的类型的单位转化效率的测试。
包括：
- `kotlin.time.Duration`
- `java.time.Duration`
- `java.util.concurrent.TimeUnit`

> 讲道理，`kotiln.time.Duration` 的表现结果我是没想到会是这样。唔。。也许是我的使用方式有误？

```
# JMH version: 1.35
# VM version: JDK 17.0.3, OpenJDK 64-Bit Server VM, 17.0.3+7-LTS
# VM invoker: /Users/forte/Library/Java/JavaVirtualMachines/azul-17.0.3/Contents/Home/bin/java
# VM options: -Dvisualvm.id=6442126014700 -javaagent:JetBrains/Toolbox/apps/IDEA-U/ch-0/221.5921.22/IntelliJ IDEA 2022.1 EAP.app/Contents/lib/idea_rt.jar=51013:JetBrains/Toolbox/apps/IDEA-U/ch-0/221.5921.22/IntelliJ IDEA 2022.1 EAP.app/Contents/bin -Dfile.encoding=UTF-8
# Blackhole mode: compiler (auto-detected, use -Djmh.blackhole.autoDetect=false to disable)
# Warmup: 2 iterations, 2 min each
# Measurement: 2 iterations, 2 min each
# Timeout: 10 min per iteration
# Threads: 3 threads (1 group; 1x "javaDurationMilliToSecond", 1x "ktDurationMilliToSecond", 1x "timeUnitMilliToSecond" in each group), will synchronize iterations
# Benchmark mode: Throughput, ops/time


Benchmark                                              Mode  Cnt       Score   Error   Units
DurationTest.MilliToSecond                            thrpt    2  355646.441          ops/ms
DurationTest.MilliToSecond:javaDurationMilliToSecond  thrpt    2  211252.355          ops/ms
DurationTest.MilliToSecond:ktDurationMilliToSecond    thrpt    2   42129.351          ops/ms
DurationTest.MilliToSecond:timeUnitMilliToSecond      thrpt    2  102264.734          ops/ms

DurationTest.SecondToMilli                            thrpt    2  554247.894          ops/ms
DurationTest.SecondToMilli:javaDurationSecondToMilli  thrpt    2  223422.624          ops/ms
DurationTest.SecondToMilli:ktDurationSecondToMilli    thrpt    2   42339.539          ops/ms
DurationTest.SecondToMilli:timeUnitSecondToMilli      thrpt    2  288485.731          ops/ms
```



```
# JMH version: 1.35
# VM version: JDK 1.8.0_332, OpenJDK 64-Bit Server VM, 25.332-b09
# VM invoker: /Users/forte/Library/Java/JavaVirtualMachines/azul-1.8.0_332/Contents/Home/jre/bin/java
# VM options: -Dvisualvm.id=9852521014808 -javaagent:JetBrains/Toolbox/apps/IDEA-U/ch-0/221.5921.22/IntelliJ IDEA 2022.1 EAP.app/Contents/lib/idea_rt.jar=52750:JetBrains/Toolbox/apps/IDEA-U/ch-0/221.5921.22/IntelliJ IDEA 2022.1 EAP.app/Contents/bin -Dfile.encoding=UTF-8
# Blackhole mode: full + dont-inline hint (auto-detected, use -Djmh.blackhole.autoDetect=false to disable)
# Warmup: 2 iterations, 2 min each
# Measurement: 2 iterations, 2 min each
# Timeout: 10 min per iteration
# Threads: 3 threads (1 group; 1x "javaDurationMilliToSecond", 1x "ktDurationMilliToSecond", 1x "timeUnitMilliToSecond" in each group), will synchronize iterations
# Benchmark mode: Throughput, ops/time

Benchmark                                              Mode  Cnt       Score   Error   Units
DurationTest.MilliToSecond                            thrpt    2  501943.887          ops/ms
DurationTest.MilliToSecond:javaDurationMilliToSecond  thrpt    2  173481.768          ops/ms
DurationTest.MilliToSecond:ktDurationMilliToSecond    thrpt    2  107571.847          ops/ms
DurationTest.MilliToSecond:timeUnitMilliToSecond      thrpt    2  220890.272          ops/ms

DurationTest.SecondToMilli                            thrpt    2  536347.374          ops/ms
DurationTest.SecondToMilli:javaDurationSecondToMilli  thrpt    2  212098.647          ops/ms
DurationTest.SecondToMilli:ktDurationSecondToMilli    thrpt    2  104243.386          ops/ms
DurationTest.SecondToMilli:timeUnitSecondToMilli      thrpt    2  220005.341          ops/ms
```


## DurationConvert
> [DurationConvertTest](simbot-apis/simbot-api/src/test/kotlin/jmh/DurationConvertTest.kt)

针对内容：

`java.time.Duration` 和 `TimeUnit` 向 `kotlin.time.Duration` 转化时的性能。

```
# JMH version: 1.35
# VM version: JDK 1.8.0_332, OpenJDK 64-Bit Server VM, 25.332-b09
# VM invoker: C:\Users\Administrator\.jdks\azul-1.8.0_332\jre\bin\java.exe
# VM options: -Dvisualvm.id=91729287991200 -javaagent:jetbrains\apps\IDEA-U\ch-0\221.5921.22\lib\idea_rt.jar=11947:jetbrains\apps\IDEA-U\ch-0\221.5921.22\bin -Dfile.encoding=UTF-8
# Blackhole mode: full + dont-inline hint (auto-detected, use -Djmh.blackhole.autoDetect=false to disable)
# Warmup: 1 iterations, 1 min each
# Measurement: 1 iterations, 30 s each
# Timeout: 10 min per iteration
# Threads: 2 threads, will synchronize iterations

Benchmark                                                 Mode  Cnt       Score   Error   Units
DurationConvertTest.kotlinMinutesDurationToJavaDuration  thrpt       195519.827          ops/ms
DurationConvertTest.kotlinSecondsDurationToJavaDuration  thrpt       260752.775          ops/ms

// TODO?

```
