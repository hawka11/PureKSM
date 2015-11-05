package pure.ksm.core

import pure.ksm.core.state.State
import java.util.concurrent.TimeUnit

interface TransitionRepository {

    fun get(stateMachineId: String): Transition?

    fun getAllIds(): Set<String>

    fun getInProgressIds(): Set<String>

    fun create(
            initialState: State,
            initialContextData: List<Any>): String

    fun tryLock(stateMachineId: String, timeout: Long, timeUnit: TimeUnit): Lock?

    interface Lock {

        fun getLatestTransition(): Transition

        fun update(transition: Transition)

        /**
         * Must be idempotent
         */
        fun unlock(): Boolean

        /**
         * Must be idempotent
         */
        fun unlockAndRemove(): Boolean
    }
}