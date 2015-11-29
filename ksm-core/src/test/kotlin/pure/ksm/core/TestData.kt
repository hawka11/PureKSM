package pure.ksm.core

sealed class TelcoState {
    object InitialState : TelcoState()

    object RechargeRequested : TelcoState()

    object RechargeCompleteFinal : TelcoState(), FinalState

    object TimeoutFinal : TelcoState(), FinalState

}

sealed class TelcoEvent {
    object Recharge : TelcoEvent()

    object RechargeConfirmed : TelcoEvent()

    object TimeoutTick : TelcoEvent()
}

data class TestData(val data: String)
