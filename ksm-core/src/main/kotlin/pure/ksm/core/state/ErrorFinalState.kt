package pure.ksm.core.state

import pure.ksm.core.Event

class ErrorFinalState(val e: Exception) : FinalState

object ErrorEvent: Event