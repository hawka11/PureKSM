package pure.ksm.core

import pure.ksm.core.state.State

data class Transition(
        val state: State,
        val event: Event,
        val context: Context) {

    companion object {
        fun Initial(state: State, context: Context): Transition {
            return Transition(state, InitialEvent(), context)
        }

        fun To(state: State, event: Event, context: Context): Transition {
            return Transition(state, event, context)
        }
    }
}

class InitialEvent : Event

