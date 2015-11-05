package pure.ksm.core


data class Transition(
        val state: Any,
        val event: Any,
        val context: Context) {

    companion object {
        fun Initial(state: Any, context: Context): Transition {
            return Transition(state, InitialEvent(), context)
        }

        fun To(state: Any, event: Any, context: Context): Transition {
            return Transition(state, event, context)
        }
    }
}

class InitialEvent

