package pure.ksm.core

import pure.ksm.core.state.FinalState
import pure.ksm.core.state.State

sealed class TelcoState : State {
    object InitialState : TelcoState()

    object RechargeRequested : TelcoState()

    object RechargeComplete : TelcoState(), FinalState
}

sealed class TelcoEvent : Event {
    object Recharge : TelcoEvent()

    object RechargeConfirmed : TelcoEvent()
}

data class TestData(val data: String)
