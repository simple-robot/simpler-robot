# 性能测试报告

此处会针对一部分需要可能需要关注、可能需要进行对比的相关api进行性能测试。
如果你对某些内容的性能对比感兴趣、或者对某些内容有好的方案或建议，欢迎通过
[issues](https://github.com/ForteScarlet/simpler-robot/issues/new/choose) 分享你的想法
或 [pr](https://github.com/ForteScarlet/simpler-robot/pulls) 贡献你的力量，十分感谢。

这些测试内容都应可以从相关的测试模块中找到。

## randomID(Random)

(v3)

> [IDTest.kt](simbot-apis/simbot-api/src/test/kotlin/jmh/IdTest.kt)

针对内容：

- `randomID()` 对比 `UUID.randomUUID`
- `randomID()` 使用 `kotlin.Random.Default` 对比 `ThreadLocalRandom`

> JMH version: 1.33
> VM version: JDK 1.8.0_332, OpenJDK 64-Bit Server VM, 25.332-b09

```

Benchmark                                    Mode  Cnt         Score         Error  Units
IdTest.randomIdByDefaultGenerate            thrpt   25  70801412.214 ± 1498689.005  ops/s
IdTest.randomIdByThreadLocalRandomGenerate  thrpt   25  66505118.175 ±  730902.232  ops/s
IdTest.randomUUIDGenerate                   thrpt   25   1435474.691 ±   39339.236  ops/s
```

## TimeDuration

(v3)

> [DurationTest](simbot-project-tests/simbot-project-test-jmh-duration/src/main/kotlin/love/forte/simbotest/duration/DurationTest.kt)

针对内容：

对几个可以代表"Duration"的类型的单位转化效率的测试。
包括：

- `kotlin.time.Duration`
- `java.time.Duration`
- `java.util.concurrent.TimeUnit`

> 讲道理，`kotiln.time.Duration` 的表现结果我是没想到会是这样。唔。。也许是我的使用方式有误？

**第一轮**

> JMH version: 1.35
> VM version: JDK 17.0.3, OpenJDK 64-Bit Server VM, 17.0.3+7-LTS

```
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

**第二轮**

> JMH version: 1.35
> VM version: JDK 1.8.0_332, OpenJDK 64-Bit Server VM, 25.332-b09

```
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

(v3)

> [DurationConvertTest](simbot-apis/simbot-api/src/test/kotlin/jmh/DurationConvertTest.kt)

针对内容：

`java.time.Duration` 和 `TimeUnit` 向 `kotlin.time.Duration` 转化时的性能。

**第一轮**

> JMH version: 1.35
> VM version: JDK 17.0.2, OpenJDK 64-Bit Server VM, 17.0.2+8-86

```
Benchmark                                                       Mode  Cnt       Score   Error   Units
DurationConvertTest.MinutesDuration                            thrpt       447133.994          ops/ms
DurationConvertTest.MinutesDuration:javaMinutesDuration        thrpt        76914.765          ops/ms
DurationConvertTest.MinutesDuration:javaMinutesDurationSimbot  thrpt       103878.816          ops/ms
DurationConvertTest.MinutesDuration:minutesTimeUnit            thrpt       266340.413          ops/ms

DurationConvertTest.SecondsDuration                            thrpt       533031.432          ops/ms
DurationConvertTest.SecondsDuration:javaSecondsDuration        thrpt        77124.192          ops/ms
DurationConvertTest.SecondsDuration:javaSecondsDurationSimbot  thrpt       106764.854          ops/ms
DurationConvertTest.SecondsDuration:secondsTimeUnit            thrpt       349142.386          ops/ms
```


## K2

随手记录

```
enable:  1m 3s
disable: 1m 57s
```
