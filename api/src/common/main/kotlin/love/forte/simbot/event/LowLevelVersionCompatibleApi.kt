package love.forte.simbot.event

@Target(
    AnnotationTarget.ANNOTATION_CLASS,
    AnnotationTarget.CLASS,
)
@RequiresOptIn("为了兼容旧版本而存在的内容，你应该逐渐替换他们。")
public annotation class LowLevelVersionCompatibleApi
