package pure.ksm.core

import java.time.OffsetDateTime

data class Transition(
        val state: Any,
        val event: Any,
        val transitioned: OffsetDateTime,
        val context: Context) {

    companion object {
        fun Initial(state: Any, context: Context): Transition {
            return Transition(state, InitialEvent(), OffsetDateTime.now(), context)
        }

        fun To(state: Any, event: Any, context: Context): Transition {
            return Transition(state, event, OffsetDateTime.now(), context)
        }
    }
}

class InitialEvent

