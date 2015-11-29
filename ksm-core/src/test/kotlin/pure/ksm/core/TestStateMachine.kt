package pure.ksm.core

import pure.ksm.core.TelcoEvent.*
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
                is RechargeConfirmed -> go(RechargeCompleteFinal, event, context.append(TestData("recharge confirmed")))
                is TimeoutTick -> checkTimeout(context)
                else -> error(event, context)
            }
        }

        onTransition(InitialState, RechargeRequested) {
            t("initial->rechargeRequested")
        }

        onTransition(RechargeRequested::class, RechargeCompleteFinal::class) {
            t("rechargeRequested->rechargeComplete")
        }

        onTransition(RechargeRequested::class, FinalState::class) {
            t("rechargeRequested->finalState")
        }

    }

    private fun checkTimeout(context: Context): Transition {
        //return if (context.) go(TelcoState.TimedoutFinal, TimeoutTick, context) else stay(context.state, TimeoutTick, context)
        return go(TelcoState.TimeoutFinal, TimeoutTick, context)
    }
}