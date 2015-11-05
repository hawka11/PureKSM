package pure.ksm.core

sealed class TelcoState {
    object InitialState : TelcoState()

    object RechargeRequested : TelcoState()

    object RechargeComplete : TelcoState(), FinalState
}

sealed class TelcoEvent {
    object Recharge : TelcoEvent()

    object RechargeConfirmed : TelcoEvent()
}

data class TestData(val data: String)
