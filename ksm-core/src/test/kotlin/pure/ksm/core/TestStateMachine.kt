package pure.ksm.core

import pure.ksm.core.TelcoEvent.Recharge
import pure.ksm.core.TelcoEvent.RechargeConfirmed
import pure.ksm.core.TelcoState.*

class TestStateMachine(val t: Function1<String, Any>) : StateMachine() {

    init {

        withState(InitialState) { context, event ->
            when (event) {
                is Recharge -> handleRecharge(context.append(TestData("recharge accepted")))
                else -> unhandled(event, context)
            }
        }

        withState(RechargeRequested) { context, event ->
            when (event) {
                is RechargeConfirmed -> handleRechargeConfirmed(context.append(TestData("recharge confirmed")))
                else -> unhandled(event, context)
            }
        }

        onTransition(InitialState, RechargeRequested) {
            t("initial->rechargeRequested")
        }

        onTransition(RechargeRequested, RechargeComplete) {
            t("rechargeRequested->rechargeComplete")
        }

    }

    private fun handleRechargeConfirmed(context: Context) = go(RechargeComplete, RechargeConfirmed, context)

    private fun handleRecharge(context: Context) = go(RechargeRequested, Recharge, context)
}