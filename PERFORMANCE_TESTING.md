# 性能测试报告
此处会针对一部分需要可能需要关注、可能需要进行对比的相关api进行性能测试公示。
如果你对某些内容的性能对比感兴趣、或者对某些内容有好的优化策略/方案，欢迎通过 
[issues](https://github.com/ForteScarlet/simpler-robot/issues/new/choose) 分享你的想法
或 [pr](https://github.com/ForteScarlet/simpler-robot/pulls) 贡献你的力量，十分感谢。

这些测试内容都应可以从相关模块中的测试模块中找到。

## randomID(Random)
> [IDTest.kt](apis/simbot-api/src/test/kotlin/jmh/IdTest.kt)

针对内容：
- `randomID()` 对比 `UUID.randomUUID`
- `randomID()` 使用 `kotlin.Random.Default` 对比 `ThreadLocalRandom`

```
# JMH version: 1.33
# VM version: JDK 1.8.0_332, OpenJDK 64-Bit Server VM, 25.332-b09
# Threads: 8 threads, will synchronize iterations

Benchmark                                    Mode  Cnt         Score         Error  Units
IdTest.randomIdByDefaultGenerate            thrpt    5  21491308.108 ± 2497554.041  ops/s
IdTest.randomIdByThreadLocalRandomGenerate  thrpt    5  15683417.337 ±  211902.295  ops/s
IdTest.randomUUIDGenerate                   thrpt    5   1706539.656 ±  237828.282  ops/s
```

## Timestamp.now()
> [TimestampCreateTest.kt](apis/simbot-api/src/test/kotlin/jmh/TimestampCreateTest.kt)

针对 `Timestamp` 对当前时间的实例获取。

```
# JMH version: 1.33
# VM version: JDK 1.8.0_332, OpenJDK 64-Bit Server VM, 25.332-b09
# Threads: 8 threads, will synchronize iterations

Benchmark                                    Mode  Cnt         Score         Error  Units
IdTest.randomIdByDefaultGenerate            thrpt   25  70801412.214 ± 1498689.005  ops/s
IdTest.randomIdByThreadLocalRandomGenerate  thrpt   25  66505118.175 ±  730902.232  ops/s
IdTest.randomUUIDGenerate                   thrpt   25   1435474.691 ±   39339.236  ops/s
```