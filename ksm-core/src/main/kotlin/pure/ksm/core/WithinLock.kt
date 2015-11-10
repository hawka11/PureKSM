package pure.ksm.core

import org.slf4j.LoggerFactory
import java.util.concurrent.TimeUnit

public object WithinLock {

    private val LOG = LoggerFactory.getLogger(WithinLock::class.java)

    public fun invokeAndUpdate(
            id: String,
            repository: TransitionRepository,
            f: (context: Context) -> Transition): Transition? {

        var result: Transition? = null

        val lock = repository.tryLock(id, 5, TimeUnit.SECONDS)

        if (lock != null) {
            try {
                result = f(lock.getLatestTransition().context)
                lock.update(result)
            } catch(e: Exception) {
                result = Transition.To(ErrorFinalState(e), ErrorEvent, lock.getLatestTransition().context)
                lock.update(result)
            } finally {
                lock.unlock()
            }
        } else {
            LOG.warn("")
        }

        return result
    }
}