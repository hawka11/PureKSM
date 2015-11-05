package pure.ksm.core

import org.slf4j.LoggerFactory
import pure.ksm.core.state.ErrorEvent
import pure.ksm.core.state.ErrorFinalState
import java.util.concurrent.TimeUnit

public class WithinLock(val repository: TransitionRepository, val stateMachine: StateMachine) {

    private val LOG = LoggerFactory.getLogger(WithinLock::class.java)

    public fun invoke(id: String, f: (context: Context, stateMachine: StateMachine) -> Transition): Transition? {

        var result: Transition? = null

        val lock = repository.tryLock(id, 5, TimeUnit.SECONDS)

        if (lock != null) {
            try {
                result = f(lock.getLatestTransition().context, stateMachine)
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