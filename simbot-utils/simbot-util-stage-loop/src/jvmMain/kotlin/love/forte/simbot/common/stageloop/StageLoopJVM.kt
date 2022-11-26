package love.forte.simbot.common.stageloop

import java.util.concurrent.ConcurrentLinkedDeque

public actual open class StageLoop<S : Stage<S>> actual constructor() {
    
    private val deque = ConcurrentLinkedDeque<S>()
    
    @Volatile
    private var _currentStage: S? = null
    
    /**
     * 在 [run] 中**正在执行**的状态实例。
     * 如果为null则代表没有循环在进行。
     */
    public actual open val currentStage: S? get() = _currentStage
    
    /**
     * 向事件队列尾部追加事件。
     */
    public actual open fun appendStage(stage: S) {
        deque.add(stage)
    }
    
    /**
     * 根据提供的 [stage] 变更 [currentStage] 的状态并执行它（如果不为null的话）
     *
     */
    public actual open suspend operator fun invoke(stage: S?) {
        _currentStage = stage
        stage?.invoke(this)
    }
    
    /**
     * 取出下一个需要执行的状态。
     */
    public actual open fun poll(): S? {
        return deque.poll()
    }
    
    
}