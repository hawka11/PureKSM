package pure.ksm.core

import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

public object WithinLock {

    private val LOG = LoggerFactory.getLogger(WithinLock::class.java)

    public fun update(
            id: String,
            repository: TransitionRepository,
            f: (transition: Transition) -> Transition): Transition? {

        var next: Transition? = null

        val lock = repository.tryLock(id, 5, TimeUnit.SECONDS)

        if (lock != null) {
            try {
                next = f(lock.latest())
                lock.update(next)
            } catch(e: Exception) {
                next = Transition.To(ErrorFinalState(e), ErrorEvent, lock.latest().context)
                lock.update(next)
            } finally {
                lock.unlock()
            }
        } else {
            LOG.warn("Could not get lock for id $id")
        }

        return next
    }

}