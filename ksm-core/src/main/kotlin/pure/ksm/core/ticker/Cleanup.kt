package pure.ksm.core.ticker

import org.slf4j.LoggerFactory
import pure.ksm.core.FinalState
import pure.ksm.core.TransitionRepository
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule

class Cleanup(val timer: Timer, val repository: TransitionRepository) {

    private val LOG = LoggerFactory.getLogger(Cleanup::class.java)

    public fun schedule(delay: Long) {
        timer.schedule(delay, {
            repository.ids().forEach {
                removeIfRequired(it, repository)
            }
        })
    }

    companion object {
        fun default(repository: TransitionRepository) = EventTicker(Timer("pure-cleanup-ticker"), repository)
    }

    private fun removeIfRequired(id: String, repository: TransitionRepository) {
        val lock = repository.tryLock(id, 5, TimeUnit.SECONDS)

        if (lock != null) {
            try {
                if (shouldCleanup(lock)) {
                    LOG.warn("Should cleanup, removing [$id]")
                    lock.remove()
                }
            } catch(e: Exception) {
                LOG.warn("Something went wrong, removing [$id]")
                lock.remove();
            }
        } else {
            LOG.warn("Could not get lock for id $id")
        }
    }

    private fun shouldCleanup(lock: TransitionRepository.Lock): Boolean {
        return lock.latest().state is FinalState
    }
}