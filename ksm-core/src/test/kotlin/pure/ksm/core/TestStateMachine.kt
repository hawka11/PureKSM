package pure.ksm.core

import pure.ksm.core.TelcoEvent.Recharge
import pure.ksm.core.TelcoEvent.RechargeConfirmed
import pure.ksm.core.TelcoState.*

class TestStateMachine(val t: Function1<String, Any>) : StateMachine() {

    init {

        onReceive(InitialState) { context, event ->
            when (event) {
                is Recharge -> go(RechargeRequested, event, context.append(TestData("recharge accepted")))
                else -> error(event, context)
            }
        }

        onReceive(RechargeRequested) { context, event ->
            when (event) {
                is RechargeConfirmed -> go(RechargeComplete, event, context.append(TestData("recharge confirmed")))
                else -> error(event, context)
            }
        }

        onTransition(InitialState, RechargeRequested) {
            t("initial->rechargeRequested")
        }

        onTransition(RechargeRequested::class, RechargeComplete::class) {
            t("rechargeRequested->rechargeComplete")
        }

        onTransition(RechargeRequested::class, FinalState::class) {
            t("rechargeRequested->finalState")
        }

    }
}