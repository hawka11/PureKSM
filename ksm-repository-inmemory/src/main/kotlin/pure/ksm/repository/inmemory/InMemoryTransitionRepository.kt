package pure.ksm.repository.inmemory;

import org.slf4j.LoggerFactory
import pure.ksm.core.Context
import pure.ksm.core.Transition
import pure.ksm.core.TransitionRepository
import pure.ksm.core.TransitionRepository.Lock
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.ReentrantLock

public class InMemoryTransitionRepository : TransitionRepository {

    private val LOG = LoggerFactory.getLogger(InMemoryTransitionRepository::class.java)

    private val idGenerator = AtomicLong(1000);
    private val transitionById = ConcurrentHashMap<String, Transition>();
    private val lockById = ConcurrentHashMap<String, ReentrantLock>();

    override public fun create(state: Any, context: Context): String {
        val id = idGenerator.andIncrement.toString()

        val transition = Transition.Initial(state, context)

        transitionById.put(id, transition)
        lockById.put(id, ReentrantLock())

        return id
    }

    override public fun get(id: String) = transitionById[id]

    override fun ids() = lockById.entries.map { it.key }.toSet()

    override fun tryLock(id: String, timeout: Long, timeUnit: TimeUnit): Lock? {
        try {
            val reentrantLock = lockById[id]

            if (reentrantLock != null && reentrantLock.tryLock(timeout, timeUnit)) {
                return object : Lock {
                    override fun latest() = transitionById[id]!!

                    override fun update(transition: Transition) {
                        transitionById.put(id, transition)
                    }

                    override fun unlock(): Boolean {
                        val lock = lockById[id]

                        return if (lock != null) {
                            try {
                                lock.unlock()
                                true
                            } catch (e: IllegalMonitorStateException) {
                                false
                            }
                        } else false
                    }

                    override fun remove(): Boolean {
                        val unlocked = unlock();
                        transitionById.remove(id);
                        lockById.remove(id);
                        return unlocked;
                    }
                }
            }
        } catch (e: Exception) {
            LOG.info("Could not get lock for [{}]", id);
        }

        return null
    }
}
