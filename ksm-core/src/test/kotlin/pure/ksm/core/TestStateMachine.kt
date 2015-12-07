package pure.ksm.core

import pure.ksm.core.TelcoEvent.*
import pure.ksm.core.TelcoState.*
import java.time.OffsetDateTime

public val TIMEOUT_SECS = 2L

class TestStateMachine(val t: Function1<String, Any>) : StateMachine() {

    init {

        onReceive(InitialState) { last, event ->
            when (event) {
                is Recharge -> go(RechargeRequested, event, last.context.append(TestData("recharge accepted")))
                else -> error(event, last.context)
            }
        }

        onReceive(RechargeRequested) { last, event ->
            when (event) {
                is RechargeConfirmed -> go(RechargeCompleteFinal, event, last.context.append(TestData("recharge confirmed")))
                is TimeoutTick -> checkTimeout(last)
                else -> error(event, last.context)
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

    private fun checkTimeout(last: Transition) = if (isTimeout(last))
        go(TelcoState.TimeoutFinal, TimeoutTick, last.context) else
        stay(last.state, TimeoutTick, last.context)

    private fun isTimeout(last: Transition) = OffsetDateTime.now().isAfter(last.transitioned.plusSeconds(TIMEOUT_SECS))
}