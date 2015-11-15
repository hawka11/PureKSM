package pure.ksm.endtoend

import org.junit.Test
import pure.ksm.core.*
import pure.ksm.repository.inmemory.InMemoryTransitionRepository
import kotlin.test.assertTrue

class SimpleTest {

    @Test
    public fun test() {
        val repo = InMemoryTransitionRepository()
        val stateMachine = TestStateMachine({ r -> })

        val id = repo.create(TelcoState.InitialState, Context(listOf()))

        val next = WithinLock.update(id, repo, { last -> stateMachine.handle(last, TelcoEvent.Recharge) })

        assertTrue { next!!.state.javaClass == TelcoState.RechargeRequested.javaClass }
    }
}