package pure.ksm.core

import org.slf4j.LoggerFactory
import pure.ksm.core.state.ErrorFinalState
import pure.ksm.core.state.State

abstract class StateMachine {

    private val LOG = LoggerFactory.getLogger(StateMachine::class.java)

    private val defByState: MutableMap<State, (context: Context, event: Event) -> Transition> = hashMapOf()
    private val defByTransition: MutableMap<State, MutableMap<State, () -> Unit>> = hashMapOf()

    public fun handle(last: Transition, event: Event): Transition {

        val next = try {

            val state = last.state

            val def = defByState[state]

            if (def != null)
                def.invoke(last.context, event)
            else
                throw IllegalStateException("No Configuration for $state")

        } catch (e: Exception) {

            LOG.error("")

            Transition.To(ErrorFinalState(e), event, last.context)
        }

        defByTransition.entries.forEach { it ->
            if (it.key.javaClass.isAssignableFrom(last.state.javaClass)) {
                it.value.entries.forEach { ij ->
                    if (ij.key.javaClass.isAssignableFrom(next.state.javaClass)) {
                        ij.value()
                    }
                }
            }
        }

        return next
    }

    protected fun withState(state: State, def: (context: Context, event: Event) -> Transition) {
        defByState.put(state, def);
    }

    protected fun onTransition(state: State, next: State, f: () -> Unit) {

        if (!defByTransition.containsKey(state)) defByTransition[state] = hashMapOf()

        defByTransition[state]!!.put(next, f)
    }

    protected fun unhandled(event: Event, context: Context): Transition {
        return Transition.To(ErrorFinalState(RuntimeException()), event, context)
    }

    protected fun stay(state: State, event: Event, context: Context) = go(state, event, context)

    protected fun go(state: State, event: Event, context: Context) = Transition.To(state, event, context)
}