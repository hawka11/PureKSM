package pure.ksm.core

class StateMachine {

    public fun handle(context: Context, event: Event): Transition {

        return context.getState().let { it.handle(context, event) }

    }
}