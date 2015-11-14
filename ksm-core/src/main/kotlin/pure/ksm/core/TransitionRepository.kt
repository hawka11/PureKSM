package pure.ksm.core

import java.util.concurrent.TimeUnit

interface TransitionRepository {

    fun get(id: String): Transition?

    fun ids(): Set<String>

    fun create(state: Any, context: Context): String

    fun tryLock(id: String, timeout: Long, timeUnit: TimeUnit): Lock?

    interface Lock {

        fun latest(): Transition

        fun update(transition: Transition)

        //idempotent
        fun unlock(): Boolean

        //idempotent
        fun remove(): Boolean
    }
}