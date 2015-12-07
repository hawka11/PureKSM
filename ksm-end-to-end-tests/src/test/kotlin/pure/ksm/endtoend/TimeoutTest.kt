package pure.ksm.endtoend

import org.junit.Test
import pure.ksm.core.*
import pure.ksm.core.ticker.EventTicker
import pure.ksm.repository.inmemory.InMemoryTransitionRepository
import java.util.concurrent.TimeUnit.*
import kotlin.test.assertTrue

class TimeoutTest {

    private val TIMEOUT_MILLIS = SECONDS.toMillis(TIMEOUT_SECS)
    private val TICK_AFTER_TIMEOUT = TIMEOUT_MILLIS + 200
    private val SLEEP_LONGER_TIMEOUT = TIMEOUT_MILLIS + 400

    @Test
    public fun test() {
        val repo = InMemoryTransitionRepository()
        val stateMachine = TestStateMachine({ r -> })

        val id = repo.create(TelcoState.InitialState, Context(listOf()))

        val next = WithinLock.update(id, repo, { last -> stateMachine.handle(last, TelcoEvent.Recharge) })
        assertTrue { next!!.state.javaClass == TelcoState.RechargeRequested.javaClass }

        EventTicker.timeout(repo).schedule(TICK_AFTER_TIMEOUT, { last -> stateMachine.handle(last, TelcoEvent.TimeoutTick) })

        Thread.sleep(SLEEP_LONGER_TIMEOUT)

        val timedout = repo.get(id)
        assertTrue { timedout!!.state.javaClass == TelcoState.TimeoutFinal.javaClass }
    }
}