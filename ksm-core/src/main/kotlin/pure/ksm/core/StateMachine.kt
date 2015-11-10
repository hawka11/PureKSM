package pure.ksm.core

import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

abstract class StateMachine {

    private val LOG = LoggerFactory.getLogger(StateMachine::class.java)

    private val defByState: MutableMap<Any, (context: Context, event: Any) -> Transition> = hashMapOf()
    private val defByTransition: MutableMap<Class<out Any>, MutableMap<Class<out Any>, () -> Unit>> = hashMapOf()

    public fun handle(last: Transition, event: Any): Transition {

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
            if (it.key.isAssignableFrom(last.state.javaClass)) {
                it.value.entries.forEach { ij ->
                    if (ij.key.isAssignableFrom(next.state.javaClass)) {
                        ij.value()
                    }
                }
            }
        }

        return next
    }

    protected fun onReceive(state: Any, def: (context: Context, event: Any) -> Transition) = defByState.put(state, def)

    protected fun onTransition(state: Any, next: Any, f: () -> Unit) = onTransition(state.javaClass, next.javaClass, f)

    protected fun onTransition(state: KClass<out Any>, next: KClass<out Any>, f: () -> Unit) = onTransition(state.java, next.java, f)

    private fun onTransition(state: Class<out Any>, next: Class<out Any>, f: () -> Unit) {
        if (!defByTransition.containsKey(state)) defByTransition[state] = hashMapOf()
        defByTransition.getRaw(state)!!.put(next, f)
    }

    protected fun error(event: Any, context: Context) = Transition.To(ErrorFinalState(RuntimeException()), event, context)

    protected fun stay(state: Any, event: Any, context: Context) = go(state, event, context)

    protected fun go(state: Any, event: Any, context: Context) = Transition.To(state, event, context)
}