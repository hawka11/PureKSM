package pure.ksm.core

interface FinalState

class ErrorFinalState(val e: Exception) : FinalState

object ErrorEvent