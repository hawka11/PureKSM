package pure.ksm.core

import org.junit.Test
import pure.ksm.core.TelcoEvent.Recharge
import pure.ksm.core.TelcoEvent.RechargeConfirmed
import pure.ksm.core.TelcoState.*
import kotlin.test.assertTrue

class StateMachineTest {

    @Test
    public fun test() {

        var onTransitionResult = ""

        val sm = TestStateMachine({ r ->
            onTransitionResult += "$r:"
            Any() // return
        })

        val initial = Transition.Initial(InitialState, Context(listOf()))

        val rechargeTransition = sm.handle(initial, Recharge);
        assertSuccessRechargeTransition(rechargeTransition, onTransitionResult)

        val confirmedTransition = sm.handle(rechargeTransition, RechargeConfirmed);
        assertConfirmedTransition(confirmedTransition, onTransitionResult)
    }

    private fun assertSuccessRechargeTransition(rechargeTransition: Transition, onTransitionResult: String) {
        assertTrue { rechargeTransition.state.javaClass == RechargeRequested.javaClass }

        val data = rechargeTransition.context.mostRecent(TestData::class.java)
        assertTrue { data != null }
        assertTrue { data?.data.equals("recharge accepted") }

        assertTrue { onTransitionResult.equals("initial->rechargeRequested:") }
    }

    private fun assertConfirmedTransition(confirmedTransition: Transition, onTransitionResult: String) {
        assertTrue { confirmedTransition.state.javaClass == RechargeComplete.javaClass }

        val data = confirmedTransition.context.mostRecent(TestData::class.java)
        assertTrue { data != null }
        assertTrue { data?.data.equals("recharge confirmed") }

        assertTrue { onTransitionResult.equals("initial->rechargeRequested:rechargeRequested->rechargeComplete:rechargeRequested->finalState:") }
    }
}
